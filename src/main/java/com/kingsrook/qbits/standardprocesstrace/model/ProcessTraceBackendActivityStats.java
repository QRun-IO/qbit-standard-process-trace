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

package com.kingsrook.qbits.standardprocesstrace.model;


import com.kingsrook.qqq.backend.core.model.backends.QQQBackend;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.fields.DisplayFormat;
import com.kingsrook.qqq.backend.core.model.tables.QQQTable;


/*******************************************************************************
 * QRecord Entity for ProcessTraceBackendActivityStats table
 * Note: not using meta-data producing annotations, so it can be disabled via config
 *******************************************************************************/
public class ProcessTraceBackendActivityStats extends QRecordEntity
{
   public static final String TABLE_NAME = "processTraceBackendActivityStats";

   @QField(isEditable = false, isPrimaryKey = true)
   private Long id;

   @QField(possibleValueSourceName = ProcessTrace.TABLE_NAME)
   private Long processTraceId;

   @QField(label = "Backend", possibleValueSourceName = QQQBackend.TABLE_NAME)
   private Integer qqqBackendId;

   @QField(label = "Table", possibleValueSourceName = QQQTable.TABLE_NAME)
   private Integer qqqTableId;

   @QField(label = "Action", possibleValueSourceName = ProcessTraceBackendActivityStatsAction.NAME)
   private String actionName;

   @QField(displayFormat = DisplayFormat.COMMAS)
   private Integer callCount;

   @QField(displayFormat = DisplayFormat.COMMAS)
   private Integer recordCount;

   @QField(displayFormat = DisplayFormat.COMMAS)
   private Integer runtimeMillis;



   /*******************************************************************************
    ** Default constructor
    *******************************************************************************/
   public ProcessTraceBackendActivityStats()
   {
   }



   /*******************************************************************************
    ** Constructor that takes a QRecord
    *******************************************************************************/
   public ProcessTraceBackendActivityStats(QRecord record)
   {
      populateFromQRecord(record);
   }



   /*******************************************************************************
    * Getter for id
    * @see #withId(Long)
    *******************************************************************************/
   public Long getId()
   {
      return (this.id);
   }



   /*******************************************************************************
    * Setter for id
    * @see #withId(Long)
    *******************************************************************************/
   public void setId(Long id)
   {
      this.id = id;
   }



   /*******************************************************************************
    * Fluent setter for id
    *******************************************************************************/
   public ProcessTraceBackendActivityStats withId(Long id)
   {
      this.id = id;
      return (this);
   }



   /*******************************************************************************
    * Getter for processTraceId
    * @see #withProcessTraceId(Long)
    *******************************************************************************/
   public Long getProcessTraceId()
   {
      return (this.processTraceId);
   }



   /*******************************************************************************
    * Setter for processTraceId
    * @see #withProcessTraceId(Long)
    *******************************************************************************/
   public void setProcessTraceId(Long processTraceId)
   {
      this.processTraceId = processTraceId;
   }



   /*******************************************************************************
    * Fluent setter for processTraceId
    *******************************************************************************/
   public ProcessTraceBackendActivityStats withProcessTraceId(Long processTraceId)
   {
      this.processTraceId = processTraceId;
      return (this);
   }




   /*******************************************************************************
    * Getter for actionName
    * @see #withActionName(String)
    *******************************************************************************/
   public String getActionName()
   {
      return (this.actionName);
   }



   /*******************************************************************************
    * Setter for actionName
    * @see #withActionName(String)
    *******************************************************************************/
   public void setActionName(String actionName)
   {
      this.actionName = actionName;
   }



   /*******************************************************************************
    * Fluent setter for actionName
    *******************************************************************************/
   public ProcessTraceBackendActivityStats withActionName(String actionName)
   {
      this.actionName = actionName;
      return (this);
   }



   /*******************************************************************************
    * Getter for callCount
    * @see #withCallCount(Integer)
    *******************************************************************************/
   public Integer getCallCount()
   {
      return (this.callCount);
   }



   /*******************************************************************************
    * Setter for callCount
    * @see #withCallCount(Integer)
    *******************************************************************************/
   public void setCallCount(Integer callCount)
   {
      this.callCount = callCount;
   }



   /*******************************************************************************
    * Fluent setter for callCount
    *******************************************************************************/
   public ProcessTraceBackendActivityStats withCallCount(Integer callCount)
   {
      this.callCount = callCount;
      return (this);
   }



   /*******************************************************************************
    * Getter for recordCount
    * @see #withRecordCount(Integer)
    *******************************************************************************/
   public Integer getRecordCount()
   {
      return (this.recordCount);
   }



   /*******************************************************************************
    * Setter for recordCount
    * @see #withRecordCount(Integer)
    *******************************************************************************/
   public void setRecordCount(Integer recordCount)
   {
      this.recordCount = recordCount;
   }



   /*******************************************************************************
    * Fluent setter for recordCount
    *******************************************************************************/
   public ProcessTraceBackendActivityStats withRecordCount(Integer recordCount)
   {
      this.recordCount = recordCount;
      return (this);
   }



   /*******************************************************************************
    * Getter for runtimeMillis
    * @see #withRuntimeMillis(Integer)
    *******************************************************************************/
   public Integer getRuntimeMillis()
   {
      return (this.runtimeMillis);
   }



   /*******************************************************************************
    * Setter for runtimeMillis
    * @see #withRuntimeMillis(Integer)
    *******************************************************************************/
   public void setRuntimeMillis(Integer runtimeMillis)
   {
      this.runtimeMillis = runtimeMillis;
   }



   /*******************************************************************************
    * Fluent setter for runtimeMillis
    *******************************************************************************/
   public ProcessTraceBackendActivityStats withRuntimeMillis(Integer runtimeMillis)
   {
      this.runtimeMillis = runtimeMillis;
      return (this);
   }


   /*******************************************************************************
    * Getter for qqqBackendId
    * @see #withQqqBackendId(Integer)
    *******************************************************************************/
   public Integer getQqqBackendId()
   {
      return (this.qqqBackendId);
   }



   /*******************************************************************************
    * Setter for qqqBackendId
    * @see #withQqqBackendId(Integer)
    *******************************************************************************/
   public void setQqqBackendId(Integer qqqBackendId)
   {
      this.qqqBackendId = qqqBackendId;
   }



   /*******************************************************************************
    * Fluent setter for qqqBackendId
    *******************************************************************************/
   public ProcessTraceBackendActivityStats withQqqBackendId(Integer qqqBackendId)
   {
      this.qqqBackendId = qqqBackendId;
      return (this);
   }



   /*******************************************************************************
    * Getter for qqqTableId
    * @see #withQqqTableId(Integer)
    *******************************************************************************/
   public Integer getQqqTableId()
   {
      return (this.qqqTableId);
   }



   /*******************************************************************************
    * Setter for qqqTableId
    * @see #withQqqTableId(Integer)
    *******************************************************************************/
   public void setQqqTableId(Integer qqqTableId)
   {
      this.qqqTableId = qqqTableId;
   }



   /*******************************************************************************
    * Fluent setter for qqqTableId
    *******************************************************************************/
   public ProcessTraceBackendActivityStats withQqqTableId(Integer qqqTableId)
   {
      this.qqqTableId = qqqTableId;
      return (this);
   }


}
