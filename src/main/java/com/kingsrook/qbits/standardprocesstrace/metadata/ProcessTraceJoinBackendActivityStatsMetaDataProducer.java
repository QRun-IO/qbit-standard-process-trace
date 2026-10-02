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

package com.kingsrook.qbits.standardprocesstrace.metadata;


import com.kingsrook.qbits.standardprocesstrace.model.ProcessTrace;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceBackendActivityStats;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducer;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.joins.JoinOn;
import com.kingsrook.qqq.backend.core.model.metadata.joins.JoinType;
import com.kingsrook.qqq.backend.core.model.metadata.joins.QJoinMetaData;


/*******************************************************************************
 * Meta Data Producer for ProcessTraceJoinBackendActivityStats
 * Note: not done via annotations, so it can be disabled via config
 *******************************************************************************/
public class ProcessTraceJoinBackendActivityStatsMetaDataProducer extends MetaDataProducer<QJoinMetaData>
{
   public static final String NAME = "ProcessTraceJoinBackendActivityStats";



   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public boolean isEnabled()
   {
      return (new ProcessTraceBackendActivityStatsMetaDataProducer().isEnabled());
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public QJoinMetaData produce(QInstance qInstance) throws QException
   {
      return (new QJoinMetaData()
         .withName(NAME)
         .withLeftTable(ProcessTrace.TABLE_NAME)
         .withRightTable(ProcessTraceBackendActivityStats.TABLE_NAME)
         .withType(JoinType.ONE_TO_MANY)
         .withJoinOn(new JoinOn("id", "processTraceId"))
      );
   }

}
