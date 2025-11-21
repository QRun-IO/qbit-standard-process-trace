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

package com.kingsrook.qbits.standardprocesstrace.utils;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.kingsrook.qbits.standardprocesstrace.model.ProcessTraceBackendActivityStats;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.backends.QQQBackendTableManager;
import com.kingsrook.qqq.backend.core.model.tables.QQQTableTableManager;
import com.kingsrook.qqq.backend.core.utils.CollectionUtils;
import com.kingsrook.qqq.backend.core.utils.PrefixedDefaultThreadFactory;
import com.kingsrook.qqq.backend.core.utils.StringUtils;
import static com.kingsrook.qqq.backend.core.logging.LogUtils.logPair;


/*******************************************************************************
 * Singleton that collects QueryStats, associated with a processTraceId.
 *
 * <p>Runs a scheduled executor thread to clean itself up - e.g., in case processes
 * leak (e.g., started in a frontend but exited before final step).  Configured by
 * {@link #setCleanupIntervalMillis(long)} and {@link #setCleanupMaxAge(long)}</p>
 *******************************************************************************/
public class ProcessTraceBackendActivityStatsManager
{
   private static final QLogger LOG = QLogger.getLogger(ProcessTraceBackendActivityStatsManager.class);

   private static ProcessTraceBackendActivityStatsManager processTraceBackendActivityStatsManager = null;

   private Map<Long, ProcessStats> stats = Collections.synchronizedMap(new HashMap<>());

   private long CLEANUP_INTERVAL_MILLIS = 60 * 60 * 1000;
   private long CLEANUP_MAX_AGE         = 12 * 60 * 60 * 1000;

   private ScheduledExecutorService scheduledExecutorService;



   /***************************************************************************
    * container for all data collected for a particular process trace.
    *
    * stores start milliseconds (for cleanup), plus map of keys to stats.
    ***************************************************************************/
   private record ProcessStats(long startMillis, Map<Key, Stats> stats)
   {
      /*******************************************************************************
       ** Constructor that uses default values
       **
       *******************************************************************************/
      public ProcessStats()
      {
         this(System.currentTimeMillis(), new LinkedHashMap<>());
      }
   }



   /***************************************************************************
    * what backend/table/action a set of stats are associated with.
    *
    * e.g., memoryBackend, personTable, insertAction
    ***************************************************************************/
   public record Key(String backendName, String tableName, String actionName) implements Serializable
   {
   }



   /***************************************************************************
    * the stats collected for individual keys.
    ***************************************************************************/
   private record Stats(Integer calls, Integer records, Integer millis)
   {
   }



   /*******************************************************************************
    ** Singleton constructor
    *******************************************************************************/
   private ProcessTraceBackendActivityStatsManager()
   {
      scheduleCleanupJob();
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private void scheduleCleanupJob()
   {
      scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new PrefixedDefaultThreadFactory("ProcessTraceBackendActivityStatsManager-cleanup"));
      scheduledExecutorService.scheduleAtFixedRate(this::cleanup, CLEANUP_INTERVAL_MILLIS, CLEANUP_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
   }



   /*******************************************************************************
    ** Singleton accessor
    *******************************************************************************/
   public static ProcessTraceBackendActivityStatsManager getInstance()
   {
      if(processTraceBackendActivityStatsManager == null)
      {
         processTraceBackendActivityStatsManager = new ProcessTraceBackendActivityStatsManager();
      }
      return (processTraceBackendActivityStatsManager);
   }



   /***************************************************************************
    * shutdown the scheduled job.  also clear stats.
    ***************************************************************************/
   public void shutdown()
   {
      scheduledExecutorService.shutdown();
      stats.clear();
   }



   /***************************************************************************
    * initialize storage for stats for a specific process trace id
    ***************************************************************************/
   public void initProcess(Long processTraceId)
   {
      stats.put(processTraceId, new ProcessStats());
   }



   /***************************************************************************
    * clean up stat entries for processes that were originally created before
    * CLEANUP_MAX_AGE (per {@link #setCleanupMaxAge(long)} milliseconds ago.
    ***************************************************************************/
   private void cleanup()
   {
      long limitStartMillis = System.currentTimeMillis() - CLEANUP_MAX_AGE;
      LOG.info("Starting cleanup of ProcessTraceBackendActivityStatsManager", logPair("size", stats.size()));
      stats.entrySet().removeIf(entry -> entry.getValue().startMillis < limitStartMillis);
      LOG.info("Finished cleanup of ProcessTraceBackendActivityStatsManager", logPair("size", stats.size()));
   }



   /***************************************************************************
    * when storing a processTrace (e.g., after the process has finished running),
    * convert the stats stored in this object to a list of {@link ProcessTraceBackendActivityStats}
    * entities.  Also remove the stats from this object's internal storage.
    ***************************************************************************/
   public List<ProcessTraceBackendActivityStats> getAndRemoveStats(Long processTraceId) throws QException
   {
      List<ProcessTraceBackendActivityStats> rs = new ArrayList<>();

      ProcessStats processStats = stats.remove(processTraceId);
      if(processStats != null)
      {
         for(Map.Entry<Key, Stats> entry : CollectionUtils.nonNullMap(processStats.stats()).entrySet())
         {
            rs.add(new ProcessTraceBackendActivityStats()
               .withProcessTraceId(processTraceId)
               .withQqqBackendId(getBackendId(entry.getKey().backendName()))
               .withQqqTableId(getTableId(entry.getKey().tableName()))
               .withActionName(entry.getKey().actionName())
               .withCallCount(entry.getValue().calls())
               .withRecordCount(entry.getValue().records())
               .withRuntimeMillis(entry.getValue().millis())
            );
         }
      }

      return (rs);
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private Integer getBackendId(String backendName)
   {
      if(!StringUtils.hasContent(backendName))
      {
         return (null);
      }

      try
      {
         return QQQBackendTableManager.getQQQBackendId(QContext.getQInstance(), backendName);
      }
      catch(Exception e)
      {
         return (null);
      }
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private Integer getTableId(String tableName)
   {
      if(!StringUtils.hasContent(tableName))
      {
         return (null);
      }

      try
      {
         return QQQTableTableManager.getQQQTableId(QContext.getQInstance(), tableName);
      }
      catch(Exception e)
      {
         return (null);
      }
   }



   /***************************************************************************
    * increment the stat values for a given process.
    *
    * @param key the backend, table, and action
    * @param addCalls the number of calls to add (typically 1 at a time?)
    * @param addRecords the number of records to add
    * @param addMillis the number of milliseconds of runtime to add
    ***************************************************************************/
   public void add(Long processTraceId, Key key, Integer addCalls, Integer addRecords, Integer addMillis)
   {
      ProcessStats    processStats     = stats.computeIfAbsent(processTraceId, k -> new ProcessStats());
      Map<Key, Stats> countsForProcess = processStats.stats();

      if(countsForProcess != null)
      {
         Stats statsForKey = countsForProcess.computeIfAbsent(key, (k) -> new Stats(0, 0, 0));
         countsForProcess.put(key, new Stats(statsForKey.calls + addCalls, statsForKey.records + addRecords, statsForKey.millis + addMillis));
      }
   }



   /***************************************************************************
    * sets the cleanup job's scheduled interval.  Also reschedules the job.
    *
    * @param cleanupIntervalMillis milliseconds between runs of the cleanup job
    ***************************************************************************/
   public void setCleanupIntervalMillis(long cleanupIntervalMillis)
   {
      CLEANUP_INTERVAL_MILLIS = cleanupIntervalMillis;
      scheduledExecutorService.shutdown();
      scheduleCleanupJob();
   }



   /***************************************************************************
    * @param cleanupMaxAge max age (in millis) that a process is allowed to be
    * before it gets cleaned up when a cleanup job runs.
    ***************************************************************************/
   public void setCleanupMaxAge(long cleanupMaxAge)
   {
      CLEANUP_MAX_AGE = cleanupMaxAge;
   }

}
