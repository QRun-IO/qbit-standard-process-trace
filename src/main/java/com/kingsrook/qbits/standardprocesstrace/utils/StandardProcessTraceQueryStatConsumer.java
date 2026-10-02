/*
 * QQQ - Low-code Application Framework for Engineers.
 * Copyright (C) 2021-2025.  Kingsrook, LLC
 * 651 N Broad St Ste 205 # 6917 | Middletown DE 19709 | United States
 * contact@kingsrook.com
 * https://github.com/Kingsrook/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kingsrook.qbits.standardprocesstrace.utils;


import com.kingsrook.qqq.backend.core.actions.tables.helpers.QueryStatConsumerInterface;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.querystats.QueryStat;


/*******************************************************************************
 * Standard Process Trace's implementation of a QueryStat consumer.
 *
 * <p>That is, to accumulate them in the {@link ProcessTraceBackendActivityStatsManager},
 * associated with the processTraceId in the user's session, so they can be stored
 * when the process trace is stored.</p>
 *******************************************************************************/
public class StandardProcessTraceQueryStatConsumer implements QueryStatConsumerInterface
{
   private static final QLogger LOG = QLogger.getLogger(StandardProcessTraceQueryStatConsumer.class);

   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public void accept(QueryStat queryStat)
   {
      try
      {
         String processTraceId = QContext.getQSession().getValue(StandardProcessTracer.PROCESS_TRACE_ID_SESSION_KEY);
         if(processTraceId == null)
         {
            return;
         }

         String backendName = QContext.getQInstance().getBackendForTable(queryStat.getTableName()).getName();
         String backendAction = queryStat.getBackendAction();

         ProcessTraceBackendActivityStatsManager.Key key = new ProcessTraceBackendActivityStatsManager.Key(backendName, queryStat.getTableName(), backendAction);
         ProcessTraceBackendActivityStatsManager.getInstance().add(Long.valueOf(processTraceId), key, 1, queryStat.getRecordCount(), queryStat.getFirstResultMillis());
      }
      catch(Exception e)
      {
         LOG.warn("Error accepting query stats for process trace", e);
      }
   }
}
