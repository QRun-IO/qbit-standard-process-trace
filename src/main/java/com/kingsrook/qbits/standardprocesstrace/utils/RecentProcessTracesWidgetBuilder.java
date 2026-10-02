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


import com.kingsrook.qbits.standardprocesstrace.model.ProcessTrace;
import com.kingsrook.qqq.backend.core.actions.dashboard.widgets.RecordListWidgetRenderer;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QCriteriaOperator;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QFilterOrderBy;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaData;
import com.kingsrook.qqq.backend.core.utils.StringUtils;


/*******************************************************************************
 ** factory/builder class, to create instances of RecordListWidgetRenderer for
 ** specific tables and/or processes within a table.
 *******************************************************************************/
public class RecentProcessTracesWidgetBuilder
{

   /***************************************************************************
    **
    ***************************************************************************/
   public static QWidgetMetaData buildWidget(String tableName)
   {
      QWidgetMetaData widgetMetaData = RecordListWidgetRenderer.widgetMetaDataBuilder(tableName + "RecentProcessTraces")
         .withTableName(ProcessTrace.TABLE_NAME)
         .withMaxRows(20)
         .withLabel("Recent Process Traces")
         .withFilter(new QQueryFilter()
            .withCriteria("qqqTable.name", QCriteriaOperator.EQUALS, tableName)
            .withCriteria("keyRecordId", QCriteriaOperator.EQUALS, "${input.id}")
            .withOrderBy(new QFilterOrderBy("id", false))
         ).getWidgetMetaData();

      return (widgetMetaData);
   }



   /***************************************************************************
    **
    ***************************************************************************/
   public static QWidgetMetaData buildWidget(String tableName, String processName)
   {
      QWidgetMetaData widgetMetaData = RecordListWidgetRenderer.widgetMetaDataBuilder(tableName + StringUtils.ucFirst(processName) + "RecentProcessTraces")
         .withTableName(ProcessTrace.TABLE_NAME)
         .withMaxRows(20)
         .withLabel("Recent Process Traces")
         .withFilter(new QQueryFilter()
            .withCriteria("qqqProcess.name", QCriteriaOperator.EQUALS, processName)
            .withCriteria("qqqTable.name", QCriteriaOperator.EQUALS, tableName)
            .withCriteria("keyRecordId", QCriteriaOperator.EQUALS, "${input.id}")
            .withOrderBy(new QFilterOrderBy("id", false))
         ).getWidgetMetaData();

      return (widgetMetaData);
   }

}
