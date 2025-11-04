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


import java.time.Instant;
import java.util.List;
import com.kingsrook.qbits.standardprocesstrace.metadata.ProcessTraceBackendActivityStatsMetaDataProducer;
import com.kingsrook.qbits.standardprocesstrace.metadata.ProcessTraceJoinBackendActivityStatsMetaDataProducer;
import com.kingsrook.qbits.standardprocesstrace.metadata.ProcessTraceJoinBackendActivityStatsWidgetMetaDataProducer;
import com.kingsrook.qbits.standardprocesstrace.metadata.ProcessTraceJoinKeyRecordQQQTableMetaDataProducer;
import com.kingsrook.qbits.standardprocesstrace.metadata.ProcessTraceJoinQQQProcessMetaDataProducer;
import com.kingsrook.qbits.standardprocesstrace.utils.ProcessTraceTableCustomizer;
import com.kingsrook.qqq.backend.core.actions.customizers.TableCustomizers;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.fields.AdornmentType;
import com.kingsrook.qqq.backend.core.model.metadata.fields.DisplayFormat;
import com.kingsrook.qqq.backend.core.model.metadata.fields.FieldAdornment;
import com.kingsrook.qqq.backend.core.model.metadata.fields.ValueTooLongBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.joins.QJoinMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildJoin;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildRecordListWidget;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildTable;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;
import com.kingsrook.qqq.backend.core.model.metadata.tables.ExposedJoin;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QFieldSection;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.Tier;
import com.kingsrook.qqq.backend.core.model.metadata.tables.UniqueKey;
import com.kingsrook.qqq.backend.core.model.processes.QQQProcess;
import com.kingsrook.qqq.backend.core.model.tables.QQQTable;


/*******************************************************************************
 ** QRecord Entity for com.kingsrook.qbits.standardprocesstrace.model.ProcessTrace table
 *******************************************************************************/
@QMetaDataProducingEntity(
   producePossibleValueSource = true,
   produceTableMetaData = true,
   tableMetaDataCustomizer = ProcessTrace.TableMetaDataCustomizer.class,
   childTables = {
      @ChildTable(
         childTableEntityClass = ProcessTraceSummaryLine.class,
         joinFieldName = "processTraceSummaryLineId",
         childJoin = @ChildJoin(enabled = true),
         childRecordListWidget = @ChildRecordListWidget(label = "Summary Lines", enabled = true, maxRows = 250)),
   }
)
public class ProcessTrace extends QRecordEntity
{
   public static final String TABLE_NAME = "processTrace";



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
         String summaryLinesChildJoinName = QJoinMetaData.makeInferredJoinName(TABLE_NAME, ProcessTraceSummaryLine.TABLE_NAME);

         table
            .withUniqueKey(new UniqueKey("processUUID"))
            .withIcon(new QIcon().withName("border_color"))
            .withRecordLabelFormat("%s - %s")
            .withRecordLabelFields("qqqProcessId", "processUUID")
            .withSection(new QFieldSection("identity", new QIcon().withName("badge"), Tier.T1, List.of("id", "qqqProcessId", "processUUID", "userId")))
            .withSection(new QFieldSection("data", new QIcon().withName("text_snippet"), Tier.T2, List.of("startTimestamp", "endTimestamp", "runtimeMillis", "keyRecordQqqTableId", "keyRecordId", "recordCount", "exceptionMessage")))
            .withSection(new QFieldSection("summaryLines", new QIcon().withName("horizontal_rule"), Tier.T2).withWidgetName(summaryLinesChildJoinName))
            .withExposedJoin(new ExposedJoin().withLabel("Summary Lines").withJoinPath(List.of(summaryLinesChildJoinName)).withJoinTable(ProcessTraceSummaryLine.TABLE_NAME))
            .withExposedJoin(new ExposedJoin().withLabel("Key Record Table").withJoinPath(List.of(ProcessTraceJoinKeyRecordQQQTableMetaDataProducer.NAME)).withJoinTable(QQQTable.TABLE_NAME))
            .withExposedJoin(new ExposedJoin().withLabel("Process").withJoinPath(List.of(ProcessTraceJoinQQQProcessMetaDataProducer.NAME)).withJoinTable(QQQProcess.TABLE_NAME));

         table.withCustomizer(TableCustomizers.POST_QUERY_RECORD, new QCodeReference(ProcessTraceTableCustomizer.class));
         table.getField("keyRecordId").withFieldAdornment(new FieldAdornment(AdornmentType.LINK).withValue(AdornmentType.LinkValues.TO_RECORD_FROM_TABLE_DYNAMIC, true));

         if(new ProcessTraceBackendActivityStatsMetaDataProducer().isEnabled())
         {
            table.getSections().add(new QFieldSection("backendActivityStats", new QIcon().withName("query_stats"), Tier.T2)
               .withWidgetName(ProcessTraceJoinBackendActivityStatsWidgetMetaDataProducer.NAME));

            table.withExposedJoin(new ExposedJoin()
               .withLabel("Backend Activity Stats")
               .withJoinPath(List.of(ProcessTraceJoinBackendActivityStatsMetaDataProducer.NAME))
               .withJoinTable(ProcessTraceBackendActivityStats.TABLE_NAME));
         }

         return (table);
      }
   }



   @QField(isEditable = false, isPrimaryKey = true)
   private Long id;

   @QField(isEditable = false)
   private Instant startTimestamp;

   @QField(isEditable = false)
   private Instant endTimestamp;

   @QField(displayFormat = DisplayFormat.COMMAS)
   private Integer runtimeMillis;

   @QField(label = "Process", possibleValueSourceName = QQQProcess.TABLE_NAME)
   private Integer qqqProcessId;

   @QField()
   private String processUUID;

   @QField()
   private Integer userId;

   @QField(label = "Key Record Table", possibleValueSourceName = QQQTable.TABLE_NAME)
   private Integer keyRecordQqqTableId;

   @QField()
   private Integer keyRecordId;

   @QField(displayFormat = DisplayFormat.COMMAS)
   private Integer recordCount;

   @QField(maxLength = 250, valueTooLongBehavior = ValueTooLongBehavior.TRUNCATE_ELLIPSIS)
   private String exceptionMessage;



   /*******************************************************************************
    ** Default constructor
    *******************************************************************************/
   public ProcessTrace()
   {
   }



   /*******************************************************************************
    ** Constructor that takes a QRecord
    *******************************************************************************/
   public ProcessTrace(QRecord record)
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
   public ProcessTrace withId(Long id)
   {
      this.id = id;
      return (this);
   }



   /*******************************************************************************
    ** Getter for startTimestamp
    *******************************************************************************/
   public Instant getStartTimestamp()
   {
      return (this.startTimestamp);
   }



   /*******************************************************************************
    ** Setter for startTimestamp
    *******************************************************************************/
   public void setStartTimestamp(Instant startTimestamp)
   {
      this.startTimestamp = startTimestamp;
   }



   /*******************************************************************************
    ** Fluent setter for startTimestamp
    *******************************************************************************/
   public ProcessTrace withStartTimestamp(Instant startTimestamp)
   {
      this.startTimestamp = startTimestamp;
      return (this);
   }



   /*******************************************************************************
    ** Getter for endTimestamp
    *******************************************************************************/
   public Instant getEndTimestamp()
   {
      return (this.endTimestamp);
   }



   /*******************************************************************************
    ** Setter for endTimestamp
    *******************************************************************************/
   public void setEndTimestamp(Instant endTimestamp)
   {
      this.endTimestamp = endTimestamp;
   }



   /*******************************************************************************
    ** Fluent setter for endTimestamp
    *******************************************************************************/
   public ProcessTrace withEndTimestamp(Instant endTimestamp)
   {
      this.endTimestamp = endTimestamp;
      return (this);
   }



   /*******************************************************************************
    ** Getter for runtimeMillis
    *******************************************************************************/
   public Integer getRuntimeMillis()
   {
      return (this.runtimeMillis);
   }



   /*******************************************************************************
    ** Setter for runtimeMillis
    *******************************************************************************/
   public void setRuntimeMillis(Integer runtimeMillis)
   {
      this.runtimeMillis = runtimeMillis;
   }



   /*******************************************************************************
    ** Fluent setter for runtimeMillis
    *******************************************************************************/
   public ProcessTrace withRuntimeMillis(Integer runtimeMillis)
   {
      this.runtimeMillis = runtimeMillis;
      return (this);
   }



   /*******************************************************************************
    ** Getter for qqqProcessId
    *******************************************************************************/
   public Integer getQqqProcessId()
   {
      return (this.qqqProcessId);
   }



   /*******************************************************************************
    ** Setter for qqqProcessId
    *******************************************************************************/
   public void setQqqProcessId(Integer qqqProcessId)
   {
      this.qqqProcessId = qqqProcessId;
   }



   /*******************************************************************************
    ** Fluent setter for qqqProcessId
    *******************************************************************************/
   public ProcessTrace withQqqProcessId(Integer qqqProcessId)
   {
      this.qqqProcessId = qqqProcessId;
      return (this);
   }



   /*******************************************************************************
    ** Getter for processUUID
    *******************************************************************************/
   public String getProcessUUID()
   {
      return (this.processUUID);
   }



   /*******************************************************************************
    ** Setter for processUUID
    *******************************************************************************/
   public void setProcessUUID(String processUUID)
   {
      this.processUUID = processUUID;
   }



   /*******************************************************************************
    ** Fluent setter for processUUID
    *******************************************************************************/
   public ProcessTrace withProcessUUID(String processUUID)
   {
      this.processUUID = processUUID;
      return (this);
   }



   /*******************************************************************************
    ** Getter for userId
    *******************************************************************************/
   public Integer getUserId()
   {
      return (this.userId);
   }



   /*******************************************************************************
    ** Setter for userId
    *******************************************************************************/
   public void setUserId(Integer userId)
   {
      this.userId = userId;
   }



   /*******************************************************************************
    ** Fluent setter for userId
    *******************************************************************************/
   public ProcessTrace withUserId(Integer userId)
   {
      this.userId = userId;
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
   public ProcessTrace withRecordCount(Integer recordCount)
   {
      this.recordCount = recordCount;
      return (this);
   }



   /*******************************************************************************
    ** Getter for exceptionMessage
    *******************************************************************************/
   public String getExceptionMessage()
   {
      return (this.exceptionMessage);
   }



   /*******************************************************************************
    ** Setter for exceptionMessage
    *******************************************************************************/
   public void setExceptionMessage(String exceptionMessage)
   {
      this.exceptionMessage = exceptionMessage;
   }



   /*******************************************************************************
    ** Fluent setter for exceptionMessage
    *******************************************************************************/
   public ProcessTrace withExceptionMessage(String exceptionMessage)
   {
      this.exceptionMessage = exceptionMessage;
      return (this);
   }



   /*******************************************************************************
    ** Getter for keyRecordId
    *******************************************************************************/
   public Integer getKeyRecordId()
   {
      return (this.keyRecordId);
   }



   /*******************************************************************************
    ** Setter for keyRecordId
    *******************************************************************************/
   public void setKeyRecordId(Integer keyRecordId)
   {
      this.keyRecordId = keyRecordId;
   }



   /*******************************************************************************
    ** Fluent setter for keyRecordId
    *******************************************************************************/
   public ProcessTrace withKeyRecordId(Integer keyRecordId)
   {
      this.keyRecordId = keyRecordId;
      return (this);
   }



   /*******************************************************************************
    ** Getter for keyRecordQqqTableId
    *******************************************************************************/
   public Integer getKeyRecordQqqTableId()
   {
      return (this.keyRecordQqqTableId);
   }



   /*******************************************************************************
    ** Setter for keyRecordQqqTableId
    *******************************************************************************/
   public void setKeyRecordQqqTableId(Integer keyRecordQqqTableId)
   {
      this.keyRecordQqqTableId = keyRecordQqqTableId;
   }



   /*******************************************************************************
    ** Fluent setter for keyRecordQqqTableId
    *******************************************************************************/
   public ProcessTrace withKeyRecordQqqTableId(Integer keyRecordQqqTableId)
   {
      this.keyRecordQqqTableId = keyRecordQqqTableId;
      return (this);
   }

}
