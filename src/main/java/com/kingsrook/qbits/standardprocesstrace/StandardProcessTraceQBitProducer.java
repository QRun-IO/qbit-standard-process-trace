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

package com.kingsrook.qbits.standardprocesstrace;


import com.kingsrook.qbits.standardprocesstrace.model.ProcessTrace;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceBackendActivityStats;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceSummaryLine;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceSummaryLineRecordInt;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerMultiOutput;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QAppSection;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaDataProducer;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.utils.StringUtils;


/*******************************************************************************
 **
 *******************************************************************************/
public class StandardProcessTraceQBitProducer implements QBitMetaDataProducer<StandardProcessTraceQBitConfig>
{
   private StandardProcessTraceQBitConfig standardProcessTraceQBitConfig;



   /***************************************************************************
    *
    ***************************************************************************/
   public static QAppSection getAppSection(QInstance qInstance)
   {
      QBitMetaData qBitMetaData = qInstance.getQBits().get(new StandardProcessTraceQBitProducer().getQBitMetaData().getName());

      QAppSection section = new QAppSection().withName("processTrace")
         .withTable(ProcessTrace.TABLE_NAME)
         .withTable(ProcessTraceSummaryLine.TABLE_NAME)
         .withTable(ProcessTraceSummaryLineRecordInt.TABLE_NAME);

      if(qBitMetaData != null && qBitMetaData.getConfig() instanceof StandardProcessTraceQBitConfig config)
      {
         if(config.getIncludeBackendActivityStats())
         {
            section.withTable(ProcessTraceBackendActivityStats.TABLE_NAME);
         }
      }

      return (section);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public StandardProcessTraceQBitConfig getQBitConfig()
   {
      return (standardProcessTraceQBitConfig);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public QBitMetaData getQBitMetaData()
   {
      QBitMetaData qBitMetaData = new QBitMetaData()
         .withGroupId("com.kingsrook.qbits")
         .withArtifactId("standard-process-trace")
         .withVersion("0.1.3")
         .withNamespace(getNamespace())
         .withConfig(getQBitConfig());

      return qBitMetaData;
   }



   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public void postProduceActions(MetaDataProducerMultiOutput metaDataProducerMultiOutput, QInstance qInstance) throws QException
   {
      if(standardProcessTraceQBitConfig != null)
      {
         //////////////////////////////////////////////////////////////////////////////////////
         // set the possible value source on the processTrace.userId field, if so configured //
         //////////////////////////////////////////////////////////////////////////////////////
         if(StringUtils.hasContent(standardProcessTraceQBitConfig.getUserIdPossibleValueSourceName()))
         {
            metaDataProducerMultiOutput.get(QTableMetaData.class, ProcessTrace.TABLE_NAME).getField("userId").setPossibleValueSourceName(standardProcessTraceQBitConfig.getUserIdPossibleValueSourceName());
         }
      }
   }



   /*******************************************************************************
    ** Getter for standardProcessTraceQBitConfig
    *******************************************************************************/
   public StandardProcessTraceQBitConfig getStandardProcessTraceQBitConfig()
   {
      return (this.standardProcessTraceQBitConfig);
   }



   /*******************************************************************************
    ** Setter for standardProcessTraceQBitConfig
    *******************************************************************************/
   public void setStandardProcessTraceQBitConfig(StandardProcessTraceQBitConfig standardProcessTraceQBitConfig)
   {
      this.standardProcessTraceQBitConfig = standardProcessTraceQBitConfig;
   }



   /*******************************************************************************
    ** Fluent setter for standardProcessTraceQBitConfig
    *******************************************************************************/
   public StandardProcessTraceQBitProducer withStandardProcessTraceQBitConfig(StandardProcessTraceQBitConfig standardProcessTraceQBitConfig)
   {
      this.standardProcessTraceQBitConfig = standardProcessTraceQBitConfig;
      return (this);
   }

}
