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
