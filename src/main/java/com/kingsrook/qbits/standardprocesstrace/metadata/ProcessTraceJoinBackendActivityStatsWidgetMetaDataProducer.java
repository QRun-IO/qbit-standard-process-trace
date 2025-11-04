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


import com.kingsrook.qqq.backend.core.actions.dashboard.widgets.ChildRecordListRenderer;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducer;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerMultiOutput;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.joins.QJoinMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitProductionContext;


/*******************************************************************************
 * Meta Data Producer for ProcessTraceJoinBackendActivityStats
 * Note: not done via annotations, so it can be disabled via config
 *******************************************************************************/
public class ProcessTraceJoinBackendActivityStatsWidgetMetaDataProducer extends MetaDataProducer<QWidgetMetaData>
{
   public static final String NAME = "ProcessTraceJoinBackendActivityStatsWidget";



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
   public QWidgetMetaData produce(QInstance qInstance) throws QException
   {
      MetaDataProducerMultiOutput metaDataProducerMultiOutput = QBitProductionContext.peekMetaDataProducerMultiOutput();
      QJoinMetaData               joinMetaData                = metaDataProducerMultiOutput.get(QJoinMetaData.class, ProcessTraceJoinBackendActivityStatsMetaDataProducer.NAME);

      return ChildRecordListRenderer.widgetMetaDataBuilder(joinMetaData)
         .withName(NAME)
         .withLabel("Backend Activity Stats")
         .withMaxRows(250)
         .getWidgetMetaData();
   }

}
