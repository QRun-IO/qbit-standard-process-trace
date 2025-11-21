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

package com.kingsrook.qbits.standardprocesstrace.metadata;


import java.util.List;
import com.kingsrook.qbits.standardprocesstrace.StandardProcessTraceQBitConfig;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTrace;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceBackendActivityStats;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.instances.QInstanceEnricher;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducer;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitConfig;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitProductionContext;
import com.kingsrook.qqq.backend.core.model.metadata.tables.Capability;
import com.kingsrook.qqq.backend.core.model.metadata.tables.ExposedJoin;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.SectionFactory;


/*******************************************************************************
 * Meta Data Producer for ProcessTraceBackendActivityStats
 * Note: not done via annotations, so it can be disabled via config
 *******************************************************************************/
public class ProcessTraceBackendActivityStatsMetaDataProducer extends MetaDataProducer<QTableMetaData>
{

   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public boolean isEnabled()
   {
      QBitConfig qBitConfig = QBitProductionContext.peekQBitConfig();
      if(qBitConfig instanceof StandardProcessTraceQBitConfig config)
      {
         if(!config.getIncludeBackendActivityStats())
         {
            return (false);
         }
      }

      return (true);
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public QTableMetaData produce(QInstance qInstance) throws QException
   {
      QTableMetaData table = new QTableMetaData()
         .withName(ProcessTraceBackendActivityStats.TABLE_NAME)
         .withIcon(new QIcon().withName("query_stats"))
         .withRecordLabelFormat("%s %s %s %s")
         .withRecordLabelFields("processTraceId", "qqqBackendId", "qqqTableId", "actionName")
         .withPrimaryKeyField("id")
         .withFieldsFromEntity(ProcessTraceBackendActivityStats.class)
         .withSection(SectionFactory.defaultT1("id", "processTraceId", "qqqBackendId", "qqqTableId", "actionName"))
         .withSection(SectionFactory.defaultT2("callCount", "recordCount", "runtimeMillis"))
         .withExposedJoin(new ExposedJoin().withLabel("Process Trace").withJoinPath(List.of(ProcessTraceJoinBackendActivityStatsMetaDataProducer.NAME)).withJoinTable(ProcessTrace.TABLE_NAME));

      table.withoutCapabilities(Capability.allWriteCapabilities());

      QInstanceEnricher.setInferredFieldBackendNames(table);

      return (table);
   }

}
