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


import java.util.Objects;
import com.kingsrook.qqq.backend.core.actions.tables.CountAction;
import com.kingsrook.qqq.backend.core.actions.tables.DeleteAction;
import com.kingsrook.qqq.backend.core.actions.tables.GetAction;
import com.kingsrook.qqq.backend.core.actions.tables.InsertAction;
import com.kingsrook.qqq.backend.core.actions.tables.QueryAction;
import com.kingsrook.qqq.backend.core.actions.tables.UpdateAction;
import com.kingsrook.qqq.backend.core.model.actions.audits.AuditDetailAccumulator;
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.PossibleValueEnum;


/*******************************************************************************
 ** ProcessTraceBackendActivityStatsAction - possible value enum
 *******************************************************************************/
public enum ProcessTraceBackendActivityStatsAction implements PossibleValueEnum<String>
{
   INSERT(InsertAction.class.getSimpleName(), "Insert"),
   UPDATE(UpdateAction.class.getSimpleName(), "Update"),
   DELETE(DeleteAction.class.getSimpleName(), "Delete"),
   QUERY(QueryAction.class.getSimpleName(), "Query"),
   GET(GetAction.class.getSimpleName(), "Get"),
   COUNT(CountAction.class.getSimpleName(), "Count"),
   AGGREGATE(AuditDetailAccumulator.class.getSimpleName(), "Aggregate");

   private final String id;
   private final String label;

   public static final String NAME = "ProcessTraceBackendActivityStatsAction";



   /*******************************************************************************
    **
    *******************************************************************************/
   ProcessTraceBackendActivityStatsAction(String id, String label)
   {
      this.id = id;
      this.label = label;
   }



   /*******************************************************************************
    ** Get instance by id
    **
    *******************************************************************************/
   public static ProcessTraceBackendActivityStatsAction getById(String id)
   {
      if(id == null)
      {
         return (null);
      }

      for(ProcessTraceBackendActivityStatsAction value : ProcessTraceBackendActivityStatsAction.values())
      {
         if(Objects.equals(value.id, id))
         {
            return (value);
         }
      }

      return (null);
   }



   /*******************************************************************************
    ** Getter for id
    **
    *******************************************************************************/
   public String getId()
   {
      return id;
   }



   /*******************************************************************************
    ** Getter for label
    **
    *******************************************************************************/
   public String getLabel()
   {
      return label;
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public String getPossibleValueId()
   {
      return (getId());
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public String getPossibleValueLabel()
   {
      return (getLabel());
   }
}
