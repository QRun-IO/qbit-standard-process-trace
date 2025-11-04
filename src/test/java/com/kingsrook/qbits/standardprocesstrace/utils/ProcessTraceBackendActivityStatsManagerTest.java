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
import java.util.concurrent.TimeUnit;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceBackendActivityStats;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.logging.QCollectingLogger;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.utils.SleepUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


/*******************************************************************************
 ** Unit test for ProcessTraceBackendActivityStatsManager 
 *******************************************************************************/
class ProcessTraceBackendActivityStatsManagerTest
{
   ProcessTraceBackendActivityStatsManager.Key key = new ProcessTraceBackendActivityStatsManager.Key("b", "t", "a");



   /*******************************************************************************
    **
    *******************************************************************************/
   @AfterEach
   void afterEach()
   {
      ProcessTraceBackendActivityStatsManager manager = ProcessTraceBackendActivityStatsManager.getInstance();
      manager.shutdown();
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testMaxAgeLarger() throws QException
   {
      long              processTraceId   = 1L;
      QCollectingLogger collectingLogger = QLogger.activateCollectingLoggerForClass(ProcessTraceBackendActivityStatsManager.class);

      ProcessTraceBackendActivityStatsManager manager = ProcessTraceBackendActivityStatsManager.getInstance();
      manager.initProcess(processTraceId);
      manager.add(processTraceId, key, 1, 2, 3);
      manager.setCleanupIntervalMillis(10);
      manager.setCleanupMaxAge(100);
      SleepUtils.sleep(50, TimeUnit.MILLISECONDS);

      QLogger.deactivateCollectingLoggerForClass(ProcessTraceBackendActivityStatsManager.class);
      assertThat(collectingLogger.getCollectedMessages()).allMatch(clm -> clm.getMessage().contains("\"size\":1"));

      List<ProcessTraceBackendActivityStats> stats = manager.getAndRemoveStats(processTraceId);
      assertEquals(1, stats.size());
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Test
   void testMaxAgeSmaller() throws QException
   {
      long              processTraceId   = 2L;
      QCollectingLogger collectingLogger = QLogger.activateCollectingLoggerForClass(ProcessTraceBackendActivityStatsManager.class);

      ProcessTraceBackendActivityStatsManager manager = ProcessTraceBackendActivityStatsManager.getInstance();
      manager.initProcess(processTraceId);
      manager.add(processTraceId, key, 1, 2, 3);
      manager.setCleanupIntervalMillis(5);
      manager.setCleanupMaxAge(20);
      SleepUtils.sleep(50, TimeUnit.MILLISECONDS);

      QLogger.deactivateCollectingLoggerForClass(ProcessTraceBackendActivityStatsManager.class);
      assertThat(collectingLogger.getCollectedMessages()).anyMatch(clm -> clm.getMessage().contains("\"size\":1"));
      assertThat(collectingLogger.getCollectedMessages()).anyMatch(clm -> clm.getMessage().contains("\"size\":0"));

      List<ProcessTraceBackendActivityStats> stats = manager.getAndRemoveStats(processTraceId);
      assertEquals(0, stats.size());

      ////////////////////////////////////////////////////////////////////////////////////////////
      // make sure even after a cleanup, we don't fail if we do try to add values for a process //
      ////////////////////////////////////////////////////////////////////////////////////////////
      manager.add(processTraceId, key, 1, 2, 3);
      stats = manager.getAndRemoveStats(processTraceId);
      assertEquals(1, stats.size());
   }

}