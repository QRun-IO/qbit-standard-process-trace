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

package com.kingsrook.qbits.standardprocesstrace;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.kingsrook.qbits.standardprocesstrace.utils.StandardProcessTracer;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.instances.AbstractQQQApplication;
import com.kingsrook.qqq.backend.core.model.actions.processes.ProcessSummaryLine;
import com.kingsrook.qqq.backend.core.model.actions.processes.ProcessSummaryLineInterface;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepInput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepOutput;
import com.kingsrook.qqq.backend.core.model.actions.processes.Status;
import com.kingsrook.qqq.backend.core.model.backends.QQQBackendsMetaDataProvider;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerMultiOutput;
import com.kingsrook.qqq.backend.core.model.metadata.QAuthenticationType;
import com.kingsrook.qqq.backend.core.model.metadata.QBackendMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.audits.AuditLevel;
import com.kingsrook.qqq.backend.core.model.metadata.audits.QAuditRules;
import com.kingsrook.qqq.backend.core.model.metadata.authentication.QAuthenticationMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldType;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QAppMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QAppSection;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.tables.Capability;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.processes.QQQProcessesMetaDataProvider;
import com.kingsrook.qqq.backend.core.model.tables.QQQTablesMetaDataProvider;
import com.kingsrook.qqq.backend.core.modules.backend.implementations.memory.MemoryBackendModule;
import com.kingsrook.qqq.backend.core.processes.implementations.etl.streamedwithfrontend.AbstractTransformStep;
import com.kingsrook.qqq.backend.core.processes.implementations.etl.streamedwithfrontend.ExtractViaQueryStep;
import com.kingsrook.qqq.backend.core.processes.implementations.etl.streamedwithfrontend.LoadViaInsertStep;
import com.kingsrook.qqq.backend.core.processes.implementations.etl.streamedwithfrontend.StreamedETLWithFrontendProcess;


/*******************************************************************************
 * test application (e.g., QInstance) for this QBit's tests.
 *******************************************************************************/
public class StandardProcessTraceTestApplication extends AbstractQQQApplication
{
   public static final String MEMORY_BACKEND_NAME = "memory";

   public static final String TABLE_NAME_PERSON = "person";
   public static final String TABLE_NAME_ORDER  = "order";

   public static final String PROCESS_NAME_PLACE_ORDERS = "placeOrders";



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public QInstance defineQInstance() throws QException
   {
      QInstance qInstance = new QInstance();

      qInstance.setAuthentication(new QAuthenticationMetaData().withType(QAuthenticationType.FULLY_ANONYMOUS));

      qInstance.addBackend(new QBackendMetaData()
         .withName(MEMORY_BACKEND_NAME)
         .withBackendType(MemoryBackendModule.class));

      //////////////////////////////
      // add tables table and PVS //
      //////////////////////////////
      new QQQTablesMetaDataProvider().defineAll(qInstance, MEMORY_BACKEND_NAME, MEMORY_BACKEND_NAME, null);
      new QQQProcessesMetaDataProvider().defineAll(qInstance, MEMORY_BACKEND_NAME, MEMORY_BACKEND_NAME, null);
      new QQQBackendsMetaDataProvider().defineAll(qInstance, MEMORY_BACKEND_NAME, MEMORY_BACKEND_NAME, null);

      //////////////////////
      // produce our qbit //
      //////////////////////
      StandardProcessTraceQBitProducer producer = new StandardProcessTraceQBitProducer()
         .withStandardProcessTraceQBitConfig(new StandardProcessTraceQBitConfig()
            .withTableMetaDataCustomizer((QInstance instance, QTableMetaData table) ->
            {
               table.setBackendName(MEMORY_BACKEND_NAME);
               return (table);
            })
            .withIncludeBackendActivityStats(true));

      MetaDataProducerMultiOutput allQBitMetaData = producer.produce(qInstance);
      allQBitMetaData.addSelfToInstance(qInstance);

      ////////////////////////////////////////
      // produce some test tables           //
      // note - nice to be a qbit this too? //
      ////////////////////////////////////////
      qInstance.addTable(new QTableMetaData()
         .withName(TABLE_NAME_PERSON)
         .withBackendName(MEMORY_BACKEND_NAME)
         .withPrimaryKeyField("id")
         .withField(new QFieldMetaData("id", QFieldType.INTEGER).withIsEditable(false))
         .withField(new QFieldMetaData("firstName", QFieldType.STRING).withIsEditable(true))
         .withField(new QFieldMetaData("lastName", QFieldType.STRING).withIsEditable(true))
         .withCapability(Capability.QUERY_STATS)
      );

      qInstance.addTable(new QTableMetaData()
         .withName(TABLE_NAME_ORDER)
         .withBackendName(MEMORY_BACKEND_NAME)
         .withPrimaryKeyField("id")
         .withField(new QFieldMetaData("id", QFieldType.INTEGER).withIsEditable(false))
         .withField(new QFieldMetaData("orderNo", QFieldType.STRING).withIsEditable(true))
         .withField(new QFieldMetaData("storeId", QFieldType.INTEGER).withIsEditable(true))
         .withField(new QFieldMetaData("shipTo", QFieldType.STRING).withIsEditable(true))
         .withCapability(Capability.QUERY_STATS)
      );

      qInstance.addProcess(StreamedETLWithFrontendProcess.processMetaDataBuilder()
         .withName(PROCESS_NAME_PLACE_ORDERS)
         .withTableName(TABLE_NAME_PERSON)
         .withSourceTable(TABLE_NAME_PERSON)
         .withDestinationTable(TABLE_NAME_ORDER)
         .withExtractStepClass(ExtractViaQueryStep.class)
         .withTransformStepClass(PlaceOrdersProcessTransformStep.class)
         .withLoadStepClass(LoadViaInsertStep.class)
         .getProcessMetaData()
         .withProcessTracerCodeReference(new QCodeReference(StandardProcessTracer.class))
      );

      ///////////////////////////////////////////
      // turn off audits (why on by default??) //
      ///////////////////////////////////////////
      qInstance.getTables().values().forEach(t -> t.setAuditRules(new QAuditRules().withAuditLevel(AuditLevel.NONE)));

      /////////////////
      // create apps //
      /////////////////
      qInstance.addApp(new QAppMetaData()
         .withName("logs")
         .withIcon(new QIcon("history_edu"))
         .withSection(StandardProcessTraceQBitProducer.getAppSection(qInstance)));

      qInstance.addApp(new QAppMetaData()
         .withName("testData")
         .withIcon(new QIcon("dataset"))
         .withSection(new QAppSection()
            .withName("people")
            .withTables(List.of(TABLE_NAME_PERSON))));

      return qInstance;
   }



   /***************************************************************************
    * test process transformer - for each input person, creates an output order (to insert)
    ***************************************************************************/
   public static class PlaceOrdersProcessTransformStep extends AbstractTransformStep
   {
      private ProcessSummaryLine okLine = new ProcessSummaryLine(Status.OK)
         .withMessageSuffix("")
         .withSingularFutureMessage("will get a new order")
         .withPluralFutureMessage("will get new orders")
         .withSingularPastMessage("got a new order")
         .withPluralPastMessage("got new orders");

      private ProcessSummaryLine errorLine = new ProcessSummaryLine(Status.ERROR)
         .withMessageSuffix("")
         .withSingularFutureMessage("will not get a new order")
         .withPluralFutureMessage("will not get new orders")
         .withSingularPastMessage("did not get a new order")
         .withPluralPastMessage("did not get new orders");



      /***************************************************************************
       *
       ***************************************************************************/
      @Override
      public ArrayList<ProcessSummaryLineInterface> getProcessSummary(RunBackendStepOutput runBackendStepOutput, boolean isForResultScreen)
      {
         ArrayList<ProcessSummaryLineInterface> rs = new ArrayList<>();
         okLine.addSelfToListIfAnyCount(rs);
         errorLine.addSelfToListIfAnyCount(rs);
         return rs;
      }



      /***************************************************************************
       *
       ***************************************************************************/
      @Override
      public void runOnePage(RunBackendStepInput runBackendStepInput, RunBackendStepOutput runBackendStepOutput) throws QException
      {
         for(QRecord personRecord : runBackendStepInput.getRecords())
         {
            ////////////////////////////////////////
            // don't let Mr. Burns order anything //
            ////////////////////////////////////////
            if("Burns".equals(personRecord.getValueString("lastName")))
            {
               errorLine.incrementCountAndAddPrimaryKey(personRecord.getValue("id"));
               continue;
            }

            QRecord orderRecord = new QRecord()
               .withValue("orderNo", UUID.randomUUID().toString())
               .withValue("shipTo", personRecord.getValueString("firstName") + " " + personRecord.getValueString("lastName"));
            runBackendStepOutput.addRecord(orderRecord);
            okLine.incrementCountAndAddPrimaryKey(personRecord.getValue("id"));
         }
      }
   }
}
