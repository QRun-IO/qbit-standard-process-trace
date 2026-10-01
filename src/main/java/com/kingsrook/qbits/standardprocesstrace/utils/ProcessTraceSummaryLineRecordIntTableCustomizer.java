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


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.kingsrook.qqq.backend.core.actions.customizers.TableCustomizerInterface;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.tables.QueryOrGetInputInterface;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QCriteriaOperator;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QFilterCriteria;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.metadata.fields.AdornmentType;
import com.kingsrook.qqq.backend.core.model.tables.QQQTable;
import com.kingsrook.qqq.backend.core.processes.utils.GeneralProcessUtils;


/*******************************************************************************
 **
 *******************************************************************************/
public class ProcessTraceSummaryLineRecordIntTableCustomizer implements TableCustomizerInterface
{
   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public List<QRecord> postQuery(QueryOrGetInputInterface queryInput, List<QRecord> records) throws QException
   {
      if(queryInput.getShouldGenerateDisplayValues())
      {
         ///////////////////////////////////////////////////////////////////////////////////////
         // for records with a qqqTableId - look up that table name, then set a display-value //
         // for the Link type adornment, to the recordId record within that table.            //
         ///////////////////////////////////////////////////////////////////////////////////////
         Set<Serializable> tableIds = records.stream().map(r -> r.getValue("qqqTableId")).filter(Objects::nonNull).collect(Collectors.toSet());
         if(!tableIds.isEmpty())
         {
            Map<Serializable, QRecord> tableMap = GeneralProcessUtils.loadTableToMap(QQQTable.TABLE_NAME, "id", new QQueryFilter(new QFilterCriteria("id", QCriteriaOperator.IN, tableIds)));

            for(QRecord record : records)
            {
               QRecord qqqTableRecord = tableMap.get(record.getValue("qqqTableId"));
               if(qqqTableRecord != null && record.getValue("recordId") != null)
               {
                  record.setDisplayValue("recordId:" + AdornmentType.LinkValues.TO_RECORD_FROM_TABLE_DYNAMIC, qqqTableRecord.getValueString("name"));
               }
            }
         }
      }

      return (records);
   }
}
