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


import java.util.List;
import com.kingsrook.qbits.standardprocesstrace.BaseTest;
import com.kingsrook.qbits.standardprocesstrace.StandardProcessTraceTestApplication;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTrace;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceBackendActivityStats;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceSummaryLine;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceSummaryLineRecordInt;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceSummaryStatusEnum;
import com.kingsrook.qqq.backend.core.actions.processes.QProcessCallbackFactory;
import com.kingsrook.qqq.backend.core.actions.processes.RunProcessAction;
import com.kingsrook.qqq.backend.core.actions.tables.InsertAction;
import com.kingsrook.qqq.backend.core.actions.tables.QueryAction;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunProcessInput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunProcessOutput;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QCriteriaOperator;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QFilterCriteria;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.tables.QQQTableTableManager;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/*******************************************************************************
 ** Unit test for StandardProcessTracer 
 *******************************************************************************/
class StandardProcessTracerTest extends BaseTest
{

   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void test() throws QException
   {
      insertHomerMargeAndMrBurns();

      ////////////////////////////////////////
      // run process skipping backend steps //
      ////////////////////////////////////////
      RunProcessInput input = new RunProcessInput();
      input.setProcessName(StandardProcessTraceTestApplication.PROCESS_NAME_PLACE_ORDERS);
      input.setCallback(QProcessCallbackFactory.forFilter(new QQueryFilter()));
      input.setFrontendStepBehavior(RunProcessInput.FrontendStepBehavior.SKIP);
      new RunProcessAction().execute(input);

      baseAssertionsAfterProcess();

      Integer personTableId = QQQTableTableManager.getQQQTableId(QContext.getQInstance(), StandardProcessTraceTestApplication.TABLE_NAME_PERSON);
      Integer orderTableId  = QQQTableTableManager.getQQQTableId(QContext.getQInstance(), StandardProcessTraceTestApplication.TABLE_NAME_ORDER);

      List<QRecord> processTraceBackendActivityStatRecords = QueryAction.execute(ProcessTraceBackendActivityStats.TABLE_NAME, new QQueryFilter());
      assertThat(processTraceBackendActivityStatRecords)
         .hasSizeGreaterThanOrEqualTo(2)
         .anyMatch(r -> r.getValue("qqqTableId").equals(personTableId) && r.getValue("actionName").equals(QueryAction.class.getSimpleName()) && r.getValueInteger("callCount").equals(1) && r.getValueInteger("recordCount").equals(3))
         .anyMatch(r -> r.getValue("qqqTableId").equals(orderTableId) && r.getValue("actionName").equals(InsertAction.class.getSimpleName()) && r.getValueInteger("callCount").equals(1) && r.getValueInteger("recordCount").equals(2));
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testResumingFrontendProcess() throws QException
   {
      insertHomerMargeAndMrBurns();

      //////////////////////////////////////
      // start process - break at preview //
      //////////////////////////////////////
      RunProcessInput input = new RunProcessInput();
      input.setProcessName(StandardProcessTraceTestApplication.PROCESS_NAME_PLACE_ORDERS);
      input.setCallback(QProcessCallbackFactory.forFilter(new QQueryFilter()));
      input.setFrontendStepBehavior(RunProcessInput.FrontendStepBehavior.BREAK);
      RunProcessOutput output = new RunProcessAction().execute(input);

      /////////////////////////////////////
      // continue to execute the process //
      /////////////////////////////////////
      input.setProcessUUID(output.getProcessUUID());
      input.setStartAfterStep(output.getProcessState().getNextStepName().get());
      new RunProcessAction().execute(input);

      baseAssertionsAfterProcess();

      Integer personTableId = QQQTableTableManager.getQQQTableId(QContext.getQInstance(), StandardProcessTraceTestApplication.TABLE_NAME_PERSON);
      Integer orderTableId  = QQQTableTableManager.getQQQTableId(QContext.getQInstance(), StandardProcessTraceTestApplication.TABLE_NAME_ORDER);

      List<QRecord> processTraceBackendActivityStatRecords = QueryAction.execute(ProcessTraceBackendActivityStats.TABLE_NAME, new QQueryFilter());
      assertThat(processTraceBackendActivityStatRecords)
         .hasSizeGreaterThanOrEqualTo(2)
         ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         // this time, there'll be 2 calls to the query (for preview, and execute steps), for twice as many records total //
         ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         .anyMatch(r -> r.getValue("qqqTableId").equals(personTableId) && r.getValue("actionName").equals(QueryAction.class.getSimpleName()) && r.getValueInteger("callCount").equals(2) && r.getValueInteger("recordCount").equals(6))
         .anyMatch(r -> r.getValue("qqqTableId").equals(orderTableId) && r.getValue("actionName").equals(InsertAction.class.getSimpleName()) && r.getValueInteger("callCount").equals(1) && r.getValueInteger("recordCount").equals(2));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private static void insertHomerMargeAndMrBurns() throws QException
   {
      ///////////////////////////////////////////////////////////////////////////////////////////////////////////
      // insert 3 people to run through the process.                                                           //
      // the process will insert an order for Homer & Marge, but is hard-coded to make an error for Mr. Burns. //
      ///////////////////////////////////////////////////////////////////////////////////////////////////////////
      new InsertAction().execute(new InsertInput(StandardProcessTraceTestApplication.TABLE_NAME_PERSON).withRecords(List.of(
         new QRecord().withValue("id", 100).withValue("firstName", "Homer").withValue("lastName", "Simpson"),
         new QRecord().withValue("id", 101).withValue("firstName", "Marge").withValue("lastName", "Simpson"),
         new QRecord().withValue("id", 102).withValue("firstName", "Mr.").withValue("lastName", "Burns")
      )));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private static void baseAssertionsAfterProcess() throws QException
   {
      List<QRecord> processTraceRecords = QueryAction.execute(ProcessTrace.TABLE_NAME, new QQueryFilter());
      assertEquals(1, processTraceRecords.size());

      List<QRecord> processTraceSummaryLineRecords = QueryAction.execute(ProcessTraceSummaryLine.TABLE_NAME, new QQueryFilter());
      assertEquals(2, processTraceSummaryLineRecords.size());

      QRecord okSummaryLineRecord = processTraceSummaryLineRecords.stream().filter(r -> r.getValueString("status").equals(ProcessTraceSummaryStatusEnum.OK.getPossibleValueId())).findFirst().get();
      assertNotNull(okSummaryLineRecord);
      assertEquals(2, okSummaryLineRecord.getValueInteger("recordCount"));

      List<QRecord> okRecordInts = QueryAction.execute(ProcessTraceSummaryLineRecordInt.TABLE_NAME, new QQueryFilter(new QFilterCriteria("processTraceSummaryLineId", QCriteriaOperator.EQUALS, okSummaryLineRecord.getValueLong("id"))));
      assertEquals(2, okRecordInts.size());
      assertThat(okRecordInts).allMatch(r -> r.getValueInteger("recordId").equals(100) || r.getValueInteger("recordId").equals(101));

      QRecord errorSummaryLineRecord = processTraceSummaryLineRecords.stream().filter(r -> r.getValueString("status").equals(ProcessTraceSummaryStatusEnum.ERROR.getPossibleValueId())).findFirst().get();
      assertNotNull(errorSummaryLineRecord);
      assertEquals(1, errorSummaryLineRecord.getValueInteger("recordCount"));

      List<QRecord> errorRecordInts = QueryAction.execute(ProcessTraceSummaryLineRecordInt.TABLE_NAME, new QQueryFilter(new QFilterCriteria("processTraceSummaryLineId", QCriteriaOperator.EQUALS, errorSummaryLineRecord.getValueLong("id"))));
      assertEquals(1, errorRecordInts.size());
      assertEquals(102, errorRecordInts.get(0).getValueInteger("recordId"));
   }

}