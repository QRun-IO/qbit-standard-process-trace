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


import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitConfig;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;


/*******************************************************************************
 * Configuration data for this qbit.
 *
 *******************************************************************************/
public class StandardProcessTraceQBitConfig implements QBitConfig
{
   private MetaDataCustomizerInterface<QTableMetaData> tableMetaDataCustomizer;

   private String userIdPossibleValueSourceName;
   private String userTableName;
   private String userIdReferenceFieldName;

   private boolean includeBackendActivityStats = false;



   /*******************************************************************************
    ** Getter for userTableName
    *******************************************************************************/
   public String getUserTableName()
   {
      return (this.userTableName);
   }



   /*******************************************************************************
    ** Setter for userTableName
    *******************************************************************************/
   public void setUserTableName(String userTableName)
   {
      this.userTableName = userTableName;
   }



   /*******************************************************************************
    ** Fluent setter for userTableName
    *******************************************************************************/
   public StandardProcessTraceQBitConfig withUserTableName(String userTableName)
   {
      this.userTableName = userTableName;
      return (this);
   }


   /*******************************************************************************
    ** Getter for userIdReferenceFieldName
    *******************************************************************************/
   public String getUserIdReferenceFieldName()
   {
      return (this.userIdReferenceFieldName);
   }



   /*******************************************************************************
    ** Setter for userIdReferenceFieldName
    *******************************************************************************/
   public void setUserIdReferenceFieldName(String userIdReferenceFieldName)
   {
      this.userIdReferenceFieldName = userIdReferenceFieldName;
   }



   /*******************************************************************************
    ** Fluent setter for userIdReferenceFieldName
    *******************************************************************************/
   public StandardProcessTraceQBitConfig withUserIdReferenceFieldName(String userIdReferenceFieldName)
   {
      this.userIdReferenceFieldName = userIdReferenceFieldName;
      return (this);
   }



   /*******************************************************************************
    ** Getter for userIdPossibleValueSourceName
    *******************************************************************************/
   public String getUserIdPossibleValueSourceName()
   {
      return (this.userIdPossibleValueSourceName);
   }



   /*******************************************************************************
    ** Setter for userIdPossibleValueSourceName
    *******************************************************************************/
   public void setUserIdPossibleValueSourceName(String userIdPossibleValueSourceName)
   {
      this.userIdPossibleValueSourceName = userIdPossibleValueSourceName;
   }



   /*******************************************************************************
    ** Fluent setter for userIdPossibleValueSourceName
    *******************************************************************************/
   public StandardProcessTraceQBitConfig withUserIdPossibleValueSourceName(String userIdPossibleValueSourceName)
   {
      this.userIdPossibleValueSourceName = userIdPossibleValueSourceName;
      return (this);
   }



   /*******************************************************************************
    * Getter for includeBackendActivityStats
    * @see #withIncludeBackendActivityStats(boolean)
    *******************************************************************************/
   public boolean getIncludeBackendActivityStats()
   {
      return (this.includeBackendActivityStats);
   }



   /*******************************************************************************
    * Setter for includeBackendActivityStats
    * @see #withIncludeBackendActivityStats(boolean)
    *******************************************************************************/
   public void setIncludeBackendActivityStats(boolean includeBackendActivityStats)
   {
      this.includeBackendActivityStats = includeBackendActivityStats;
   }



   /*******************************************************************************
    * Fluent setter for includeBackendActivityStats
    *
    * @param includeBackendActivityStats
    * indicate whether or not the ProcessTraceBackendActivityStats table should be
    * included in the QInstance (and records should be built in that table under
    * processTrace records).
    * <p>Note that using this feature requires the QQQ QueryStatManager to be active
    * in the application server.</p>
    * @return this
    *******************************************************************************/
   public StandardProcessTraceQBitConfig withIncludeBackendActivityStats(boolean includeBackendActivityStats)
   {
      this.includeBackendActivityStats = includeBackendActivityStats;
      return (this);
   }



   /*******************************************************************************
    * Getter for tableMetaDataCustomizer
    * @see #withTableMetaDataCustomizer(MetaDataCustomizerInterface)
    *******************************************************************************/
   public MetaDataCustomizerInterface<QTableMetaData> getTableMetaDataCustomizer()
   {
      return (this.tableMetaDataCustomizer);
   }



   /*******************************************************************************
    * Setter for tableMetaDataCustomizer
    * @see #withTableMetaDataCustomizer(MetaDataCustomizerInterface)
    *******************************************************************************/
   public void setTableMetaDataCustomizer(MetaDataCustomizerInterface<QTableMetaData> tableMetaDataCustomizer)
   {
      this.tableMetaDataCustomizer = tableMetaDataCustomizer;
   }



   /*******************************************************************************
    * Fluent setter for tableMetaDataCustomizer
    *
    * @param tableMetaDataCustomizer
    * table meta data customizer to be applied to all tables produced by this qbit.
    * @return this
    *******************************************************************************/
   public StandardProcessTraceQBitConfig withTableMetaDataCustomizer(MetaDataCustomizerInterface<QTableMetaData> tableMetaDataCustomizer)
   {
      this.tableMetaDataCustomizer = tableMetaDataCustomizer;
      return (this);
   }


}
