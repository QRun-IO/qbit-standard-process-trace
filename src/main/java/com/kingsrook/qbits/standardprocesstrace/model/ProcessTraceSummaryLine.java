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

package com.kingsrook.qbits.standardprocesstrace.model;


import java.util.List;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.processes.Status;
import com.kingsrook.qqq.backend.core.model.data.QAssociation;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.fields.AdornmentType;
import com.kingsrook.qqq.backend.core.model.metadata.fields.FieldAdornment;
import com.kingsrook.qqq.backend.core.model.metadata.fields.ValueTooLongBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.joins.QJoinMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildJoin;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildRecordListWidget;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildTable;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;
import com.kingsrook.qqq.backend.core.model.metadata.tables.Association;
import com.kingsrook.qqq.backend.core.model.metadata.tables.ExposedJoin;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QFieldSection;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.Tier;
import static com.kingsrook.qqq.backend.core.model.metadata.fields.AdornmentType.ChipValues.iconAndColorValues;


/*******************************************************************************
 ** QRecord Entity for com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceSummaryLine table
 *******************************************************************************/
@QMetaDataProducingEntity(
   producePossibleValueSource = true,
   produceTableMetaData = true,
   tableMetaDataCustomizer = ProcessTraceSummaryLine.TableMetaDataCustomizer.class,
   childTables = {
      @ChildTable(
         childTableEntityClass = ProcessTraceSummaryLineRecordInt.class,
         joinFieldName = "processTraceSummaryLineId",
         childJoin = @ChildJoin(enabled = true),
         childRecordListWidget = @ChildRecordListWidget(label = "Records", enabled = true, maxRows = 250))
   }
)
public class ProcessTraceSummaryLine extends QRecordEntity
{
   public static final String TABLE_NAME                  = "processTraceSummaryLine";
   public static final String RECORD_INT_ASSOCIATION_NAME = "processTraceSummaryLineRecordInt";



   /***************************************************************************
    **
    ***************************************************************************/
   public static class TableMetaDataCustomizer implements MetaDataCustomizerInterface<QTableMetaData>
   {

      /***************************************************************************
       **
       ***************************************************************************/
      @Override
      public QTableMetaData customizeMetaData(QInstance qInstance, QTableMetaData table) throws QException
      {
         String parentJoinName = QJoinMetaData.makeInferredJoinName(ProcessTrace.TABLE_NAME, ProcessTraceSummaryLine.TABLE_NAME);
         String childJoinName  = QJoinMetaData.makeInferredJoinName(ProcessTraceSummaryLine.TABLE_NAME, ProcessTraceSummaryLineRecordInt.TABLE_NAME);

         table
            .withIcon(new QIcon().withName("edit_note"))
            .withRecordLabelFormat("%s - %s")
            .withRecordLabelFields("processTraceId", "status")
            .withSection(new QFieldSection("identity", new QIcon().withName("badge"), Tier.T1, List.of("id", "processTraceId")))
            .withSection(new QFieldSection("data", new QIcon().withName("text_snippet"), Tier.T2, List.of("recordCount", "status", "message")))
            .withSection(new QFieldSection("records", new QIcon().withName("view_compact"), Tier.T2).withWidgetName(childJoinName))
            .withExposedJoin(new ExposedJoin().withLabel("Process Trace").withJoinPath(List.of(parentJoinName)).withJoinTable(ProcessTrace.TABLE_NAME))
            .withAssociation(new Association().withName(RECORD_INT_ASSOCIATION_NAME).withAssociatedTableName(ProcessTraceSummaryLineRecordInt.TABLE_NAME).withJoinName(childJoinName));

         table.getField("status").withFieldAdornment(new FieldAdornment(AdornmentType.CHIP)
            .withValues(iconAndColorValues(Status.INFO.name(), "info", AdornmentType.ChipValues.COLOR_INFO))
            .withValues(iconAndColorValues(Status.OK.name(), "check", AdornmentType.ChipValues.COLOR_SUCCESS))
            .withValues(iconAndColorValues(Status.WARNING.name(), "warning_amber", AdornmentType.ChipValues.COLOR_WARNING))
            .withValues(iconAndColorValues(Status.ERROR.name(), "report", AdornmentType.ChipValues.COLOR_ERROR)));

         return (table);
      }
   }



   @QField(isEditable = false, isPrimaryKey = true)
   private Long id;

   @QField(possibleValueSourceName = ProcessTrace.TABLE_NAME)
   private Long processTraceId;

   @QField()
   private Integer recordCount;

   @QField(possibleValueSourceName = ProcessTraceSummaryStatusEnum.NAME)
   private String status;

   @QField(maxLength = 250, valueTooLongBehavior = ValueTooLongBehavior.TRUNCATE_ELLIPSIS)
   private String message;

   @QAssociation(name = RECORD_INT_ASSOCIATION_NAME)
   private List<ProcessTraceSummaryLineRecordInt> processTraceSummaryLineRecordIntList;



   /*******************************************************************************
    ** Default constructor
    *******************************************************************************/
   public ProcessTraceSummaryLine()
   {
   }



   /*******************************************************************************
    ** Constructor that takes a QRecord
    *******************************************************************************/
   public ProcessTraceSummaryLine(QRecord record)
   {
      populateFromQRecord(record);
   }



   /*******************************************************************************
    ** Getter for id
    *******************************************************************************/
   public Long getId()
   {
      return (this.id);
   }



   /*******************************************************************************
    ** Setter for id
    *******************************************************************************/
   public void setId(Long id)
   {
      this.id = id;
   }



   /*******************************************************************************
    ** Fluent setter for id
    *******************************************************************************/
   public ProcessTraceSummaryLine withId(Long id)
   {
      this.id = id;
      return (this);
   }



   /*******************************************************************************
    ** Getter for processTraceId
    *******************************************************************************/
   public Long getProcessTraceId()
   {
      return (this.processTraceId);
   }



   /*******************************************************************************
    ** Setter for processTraceId
    *******************************************************************************/
   public void setProcessTraceId(Long processTraceId)
   {
      this.processTraceId = processTraceId;
   }



   /*******************************************************************************
    ** Fluent setter for processTraceId
    *******************************************************************************/
   public ProcessTraceSummaryLine withProcessTraceId(Long processTraceId)
   {
      this.processTraceId = processTraceId;
      return (this);
   }



   /*******************************************************************************
    ** Getter for recordCount
    *******************************************************************************/
   public Integer getRecordCount()
   {
      return (this.recordCount);
   }



   /*******************************************************************************
    ** Setter for recordCount
    *******************************************************************************/
   public void setRecordCount(Integer recordCount)
   {
      this.recordCount = recordCount;
   }



   /*******************************************************************************
    ** Fluent setter for recordCount
    *******************************************************************************/
   public ProcessTraceSummaryLine withRecordCount(Integer recordCount)
   {
      this.recordCount = recordCount;
      return (this);
   }



   /*******************************************************************************
    ** Getter for status
    *******************************************************************************/
   public String getStatus()
   {
      return (this.status);
   }



   /*******************************************************************************
    ** Setter for status
    *******************************************************************************/
   public void setStatus(String status)
   {
      this.status = status;
   }



   /*******************************************************************************
    ** Fluent setter for status
    *******************************************************************************/
   public ProcessTraceSummaryLine withStatus(String status)
   {
      this.status = status;
      return (this);
   }



   /*******************************************************************************
    ** Getter for message
    *******************************************************************************/
   public String getMessage()
   {
      return (this.message);
   }



   /*******************************************************************************
    ** Setter for message
    *******************************************************************************/
   public void setMessage(String message)
   {
      this.message = message;
   }



   /*******************************************************************************
    ** Fluent setter for message
    *******************************************************************************/
   public ProcessTraceSummaryLine withMessage(String message)
   {
      this.message = message;
      return (this);
   }



   /*******************************************************************************
    ** Getter for processTraceSummaryLineRecordIntList
    *******************************************************************************/
   public List<ProcessTraceSummaryLineRecordInt> getProcessTraceSummaryLineRecordIntList()
   {
      return (this.processTraceSummaryLineRecordIntList);
   }



   /*******************************************************************************
    ** Setter for processTraceSummaryLineRecordIntList
    *******************************************************************************/
   public void setProcessTraceSummaryLineRecordIntList(List<ProcessTraceSummaryLineRecordInt> processTraceSummaryLineRecordIntList)
   {
      this.processTraceSummaryLineRecordIntList = processTraceSummaryLineRecordIntList;
   }



   /*******************************************************************************
    ** Fluent setter for processTraceSummaryLineRecordIntList
    *******************************************************************************/
   public ProcessTraceSummaryLine withProcessTraceSummaryLineRecordIntList(List<ProcessTraceSummaryLineRecordInt> processTraceSummaryLineRecordIntList)
   {
      this.processTraceSummaryLineRecordIntList = processTraceSummaryLineRecordIntList;
      return (this);
   }

}
