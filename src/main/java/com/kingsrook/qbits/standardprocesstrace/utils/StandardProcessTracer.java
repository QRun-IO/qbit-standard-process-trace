/*
 * QQQ - Low-code Application Framework for Engineers.
 * Copyright (C) 2021-2025.  Kingsrook, LLC
 * 651 N Broad St Ste 205 # 6917 | Middletown DE 19709 | United States
 * contact@kingsrook.com
 * https://github.com/Kingsrook/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package com.kingsrook.qbits.standardprocesstrace.utils;


import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.kingsrook.qbits.standardprocesstrace.StandardProcessTraceQBitConfig;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTrace;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceBackendActivityStats;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceSummaryLine;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceSummaryLineRecordInt;
import com.kingsrook.qqq.backend.core.actions.tables.GetAction;
import com.kingsrook.qqq.backend.core.actions.tables.InsertAction;
import com.kingsrook.qqq.backend.core.actions.tables.UpdateAction;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.actions.processes.ProcessSummaryLine;
import com.kingsrook.qqq.backend.core.model.actions.processes.ProcessSummaryLineInterface;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepInput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepOutput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunProcessInput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunProcessOutput;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertOutput;
import com.kingsrook.qqq.backend.core.model.actions.tables.update.UpdateInput;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.processes.QQQProcessTableManager;
import com.kingsrook.qqq.backend.core.model.session.QSession;
import com.kingsrook.qqq.backend.core.model.session.QUser;
import com.kingsrook.qqq.backend.core.model.tables.QQQTableTableManager;
import com.kingsrook.qqq.backend.core.processes.implementations.etl.streamedwithfrontend.StreamedETLWithFrontendProcess;
import com.kingsrook.qqq.backend.core.processes.tracing.ProcessTracerInterface;
import com.kingsrook.qqq.backend.core.processes.tracing.ProcessTracerKeyRecordMessage;
import com.kingsrook.qqq.backend.core.processes.tracing.ProcessTracerMessage;
import com.kingsrook.qqq.backend.core.utils.CollectionUtils;
import com.kingsrook.qqq.backend.core.utils.StringUtils;
import com.kingsrook.qqq.backend.core.utils.ValueUtils;
import com.kingsrook.qqq.backend.core.utils.memoization.Memoization;
import static com.kingsrook.qqq.backend.core.logging.LogUtils.logPair;


/*******************************************************************************
 ** Implementation of ProcessTracerInterface that inserts records into the
 ** standard ProcessTrace tables defined in this QBit.
 *******************************************************************************/
public class StandardProcessTracer implements ProcessTracerInterface
{
   private static final QLogger LOG = QLogger.getLogger(StandardProcessTracer.class);

   private static Memoization<String, QRecord> userRecordMemoization = new Memoization<>();

   private static StandardProcessTraceQBitConfig standardProcessTraceQBitConfig;

   public static final String PROCESS_TRACE_ID_SESSION_KEY = StandardProcessTracer.class.getName() + ".processTraceId";

   private Instant startTime;
   private Long    processTraceId;



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void handleProcessStart(RunProcessInput runProcessInput)
   {
      try
      {
         startTime = Instant.now();
         QRecord userRecord = getUser();

         InsertOutput insertOutput = new InsertAction().execute(new InsertInput(ProcessTrace.TABLE_NAME).withRecordEntity(new ProcessTrace()
            .withQqqProcessId(QQQProcessTableManager.getQQQProcessId(QContext.getQInstance(), runProcessInput.getProcessName()))
            .withStartTimestamp(startTime)
            .withUserId(userRecord == null ? null : userRecord.getValueInteger("id"))
            .withProcessUUID(runProcessInput.getProcessUUID())));

         processTraceId = insertOutput.getRecords().get(0).getValueLong("id");

         ////////////////////////////////////////////////////////////////////////////////////
         // store the processTraceId in the session.  this will be used by the             //
         // ProcessTraceQueryStatConsumer to associated QueryStats with this process trace //
         ////////////////////////////////////////////////////////////////////////////////////
         if(QContext.getQSession() != null)
         {
            QContext.getQSession().setValue(PROCESS_TRACE_ID_SESSION_KEY, String.valueOf(processTraceId));
         }

         ProcessTraceBackendActivityStatsManager.getInstance().initProcess(processTraceId);
      }
      catch(QException e)
      {
         LOG.warn("Error inserting processTrace header record.", e);
      }
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void handleProcessResume(RunProcessInput runProcessInput)
   {
      try
      {
         //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         // if a user is resuming a process, e.g., from a frontend, then we want to put that processTraceId in their session //
         //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         if(processTraceId == null)
         {
            QRecord processTrace = GetAction.execute(ProcessTrace.TABLE_NAME, Map.of("processUUID", runProcessInput.getProcessUUID()));
            if(processTrace != null)
            {
               processTraceId = processTrace.getValueLong("id");
            }
         }

         if(processTraceId != null && QContext.getQSession() != null)
         {
            QContext.getQSession().setValue(PROCESS_TRACE_ID_SESSION_KEY, String.valueOf(processTraceId));
         }
      }
      catch(Exception e)
      {
         LOG.warn("Error resuming process trace", e, logPair("processUUID", () -> runProcessInput.getProcessUUID()));
      }
   }



   /***************************************************************************
    **
    ***************************************************************************/
   private QRecord getUser() throws QException
   {
      if(standardProcessTraceQBitConfig == null || !StringUtils.hasContent(standardProcessTraceQBitConfig.getUserTableName()) || !StringUtils.hasContent(standardProcessTraceQBitConfig.getUserIdReferenceFieldName()))
      {
         return (null);
      }

      QSession session = QContext.getQSession();
      if(session == null)
      {
         return (null);
      }

      QUser user = session.getUser();
      if(user == null)
      {
         return (null);
      }

      return (userRecordMemoization.getResult(user.getIdReference(), (idReference) ->
         GetAction.execute(standardProcessTraceQBitConfig.getUserTableName(), Map.of(standardProcessTraceQBitConfig.getUserIdReferenceFieldName(), idReference)))
      ).orElse(null);
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void handleStepStart(RunBackendStepInput runBackendStepInput)
   {
      /////////////////////////////////
      // noop in this implementation //
      /////////////////////////////////
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void handleMessage(RunBackendStepInput runBackendStepInput, ProcessTracerMessage message)
   {
      try
      {
         if(message instanceof ProcessTracerKeyRecordMessage keyRecordMessage)
         {
            Integer keyTableId = QQQTableTableManager.getQQQTableId(QContext.getQInstance(), keyRecordMessage.getTableName());

            new UpdateAction().execute(new UpdateInput(ProcessTrace.TABLE_NAME).withRecord(new ProcessTrace()
               .withId(processTraceId)
               .withKeyRecordQqqTableId(keyTableId)
               .withKeyRecordId(keyRecordMessage.getRecordId())
               .toQRecordOnlyChangedFields(true)));
         }
      }
      catch(Exception e)
      {
         LOG.warn("Error handling processTrace message.", e, logPair("message", message));
      }
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void handleStepFinish(RunBackendStepInput runBackendStepInput, RunBackendStepOutput runBackendStepOutput)
   {
      /////////////////////////////////
      // noop in this implementation //
      /////////////////////////////////
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void handleProcessBreak(RunProcessInput runProcessInput, RunProcessOutput runProcessOutput, Exception processException)
   {
      /////////////////////////////////
      // noop in this implementation //
      /////////////////////////////////
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void handleProcessFinish(RunProcessInput runProcessInput, RunProcessOutput runProcessOutput, Exception processException)
   {
      try
      {
         if(processTraceId == null)
         {
            QRecord processTrace = GetAction.execute(ProcessTrace.TABLE_NAME, Map.of("processUUID", runProcessInput.getProcessUUID()));
            if(processTrace != null)
            {
               processTraceId = processTrace.getValueLong("id");

               if(startTime == null)
               {
                  startTime = processTrace.getValueInstant("startTimestamp");
               }
            }
         }

         if(processTraceId != null)
         {
            //////////////////////////////////////////////
            // update trace header with end time values //
            //////////////////////////////////////////////
            Instant endTimestamp = Instant.now();
            Long    millis       = startTime == null ? null : endTimestamp.toEpochMilli() - startTime.toEpochMilli();

            ///////////////////////////////////
            // include record count if known //
            ///////////////////////////////////
            Integer recordCount = null;
            if(runProcessOutput.getValue("recordCount") != null)
            {
               recordCount = runProcessOutput.getValueInteger("recordCount");
            }

            new UpdateAction().execute(new UpdateInput(ProcessTrace.TABLE_NAME).withRecord(new ProcessTrace()
               .withId(processTraceId)
               .withEndTimestamp(endTimestamp)
               .withRuntimeMillis(millis == null ? null : millis.intValue())
               .withRecordCount(recordCount)
               .withExceptionMessage(processException == null ? null : processException.getMessage())
               .toQRecordOnlyChangedFields(true)
            ));

            /////////////////////////
            // build summary lines //
            /////////////////////////
            Serializable processSummary = runProcessOutput.getValue(StreamedETLWithFrontendProcess.FIELD_PROCESS_SUMMARY);
            if(processSummary instanceof List)
            {
               List<ProcessTraceSummaryLine>               summaryLines        = new ArrayList<>();
               List<? extends ProcessSummaryLineInterface> processSummaryLines = (List<? extends ProcessSummaryLineInterface>) processSummary;
               for(ProcessSummaryLineInterface processSummaryLineInterface : processSummaryLines)
               {
                  if(processSummaryLineInterface instanceof ProcessSummaryLine processSummaryLine)
                  {
                     Integer count = processSummaryLine.getCount();
                     ProcessTraceSummaryLine traceSummaryLine = new ProcessTraceSummaryLine()
                        .withProcessTraceId(processTraceId)
                        .withStatus(processSummaryLine.getStatus() == null ? null : processSummaryLine.getStatus().name())
                        .withRecordCount(count)
                        .withMessage((count == null ? "" : (count + " ")) + processSummaryLine.getMessage());
                     summaryLines.add(traceSummaryLine);

                     if(CollectionUtils.nullSafeHasContents(processSummaryLine.getPrimaryKeys()))
                     {
                        String tableName = runProcessOutput.getValueString(StreamedETLWithFrontendProcess.FIELD_DESTINATION_TABLE);
                        if(!StringUtils.hasContent(tableName))
                        {
                           tableName = runProcessOutput.getValueString("tableName");
                        }
                        Integer qqqTableId = QQQTableTableManager.getQQQTableId(QContext.getQInstance(), tableName);

                        List<ProcessTraceSummaryLineRecordInt> recordIntList = new ArrayList<>();
                        traceSummaryLine.setProcessTraceSummaryLineRecordIntList(recordIntList);

                        for(Serializable primaryKey : processSummaryLine.getPrimaryKeys())
                        {
                           try
                           {
                              Integer recordId = ValueUtils.getValueAsInteger(primaryKey);
                              if(recordId != null)
                              {
                                 recordIntList.add(new ProcessTraceSummaryLineRecordInt()
                                    .withQqqTableId(qqqTableId)
                                    .withRecordId(recordId));
                              }
                           }
                           catch(Exception e)
                           {
                              /////////////////////////////////////////////////////////////////////
                              // todo log (would be loud)?  or, let not be integer maybe better? //
                              /////////////////////////////////////////////////////////////////////
                           }
                        }
                     }
                  }
                  else
                  {
                     ProcessTraceSummaryLine traceSummaryLine = new ProcessTraceSummaryLine()
                        .withProcessTraceId(processTraceId)
                        .withStatus(processSummaryLineInterface.getStatus() == null ? null : processSummaryLineInterface.getStatus().name())
                        .withMessage(processSummaryLineInterface.getMessage());
                     summaryLines.add(traceSummaryLine);
                  }
               }

               new InsertAction().execute(new InsertInput(ProcessTraceSummaryLine.TABLE_NAME).withRecordEntities(summaryLines));
            }

            //////////////////////////////////////////
            // build and store backend stat records //
            //////////////////////////////////////////
            if(QContext.getQInstance().getTable(ProcessTraceBackendActivityStats.TABLE_NAME) != null)
            {
               List<ProcessTraceBackendActivityStats> stats = ProcessTraceBackendActivityStatsManager.getInstance().getAndRemoveStats(processTraceId);
               new InsertAction().execute(new InsertInput(ProcessTraceBackendActivityStats.TABLE_NAME).withRecordEntities(stats));
            }
         }
      }
      catch(Exception e)
      {
         LOG.warn("Error completing store of processTrace records.", e);
      }
      finally
      {
         if(QContext.getQSession() != null)
         {
            QContext.getQSession().removeValue(PROCESS_TRACE_ID_SESSION_KEY);
         }
      }
   }



   /*******************************************************************************
    ** Setter for standardProcessTraceQBitConfig
    **
    *******************************************************************************/
   public static void setStandardProcessTraceQBitConfig(StandardProcessTraceQBitConfig standardProcessTraceQBitConfig)
   {
      StandardProcessTracer.standardProcessTraceQBitConfig = standardProcessTraceQBitConfig;
   }
}
