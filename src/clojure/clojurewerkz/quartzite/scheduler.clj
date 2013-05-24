(ns clojurewerkz.quartzite.scheduler
  (:import [org.quartz Scheduler JobDetail JobKey Trigger TriggerKey SchedulerListener ListenerManager]
           org.quartz.impl.matchers.GroupMatcher
           java.util.List)
  (:require [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.triggers :as t])
  (:use clojurewerkz.quartzite.conversion))

;;
;; Implementation
;;

(def ^:dynamic *scheduler* (atom nil))



;;
;; API
;;


(defmacro with-scheduler
  [sched & body]
  `(binding [*scheduler* ~sched]
     (do ~@body)))


(defn initialize
  "Initializers default scheduler. Use this function before starting Quartzite's scheduler"
  ([]
     (initialize (org.quartz.impl.StdSchedulerFactory/getDefaultScheduler)))
  ([scheduler]
     (reset! *scheduler* scheduler)))

(defn start
  "Starts Quartzite's scheduler. Newly initialized scheduler is not active (in standby mode),
   this function starts it"
  []
  (.start ^Scheduler @*scheduler*))

(defn start-delayed
  "Starts Quartzite's scheduler after a delay in seconds"
  [^long seconds]
  (.startDelayed ^Scheduler @*scheduler* seconds))

(defn standby
  "Puts scheduler in standby mode. When in scheduler is standby mode, no triggers will fire"
  []
  (.standby ^Scheduler @*scheduler*))

(defn shutdown
  "Shuts scheduler down, releasing all the resources associated with it. When passed true causes scheduler
   to wait for running jobs to complete before shutting down"
  ([]
     (.shutdown ^Scheduler @*scheduler*))
  ([^Boolean wait-for-jobs-to-complete]
     (.shutdown ^Scheduler @*scheduler* wait-for-jobs-to-complete)))

(defn recreate
  "Recreates (reinitializes) Quartzite's scheduler instance"
  []
  (swap! *scheduler* (fn [_] (org.quartz.impl.StdSchedulerFactory/getDefaultScheduler))))


(defn started?
  "Returns true if the scheduler has been ever started, false otherwise"
  []
  (.isStarted ^Scheduler @*scheduler*))

(defn standby?
  "Returns true if the scheduler is in standby mode, false otherwise"
  []
  (.isInStandbyMode ^Scheduler @*scheduler*))

(defn shutdown?
  "Returns true if the scheduler has been shut down, false otherwise"
  []
  (.isShutdown ^Scheduler @*scheduler*))

(defn schedule
  "Adds given job to the scheduler and associates it with given trigger.
   Trigger controls job execution schedule, initial execution time and other characteristics"
  [^JobDetail job-detail ^Trigger trigger]
  (.scheduleJob ^Scheduler @*scheduler* job-detail trigger))

(defn add-job
  "Adds given job to the scheduler with no associated trigger"
  ([^JobDetail job-detail ^Boolean replace]
     (.addJob ^Scheduler @*scheduler* job-detail replace))
  
  ([^JobDetail job-detail]
     (add-job job-detail true)))

(defn add-trigger
  "Adds given trigger to the scheduled job with which the trigger has been associated"
  [^Trigger trigger]
  (.scheduleJob ^Scheduler @*scheduler* trigger))


(defn delete-trigger
  "Removes the indicated trigger from the scheduler. If the related job does not have any other triggers,
   and the job is not durable, then the job will also be deleted"
  [^TriggerKey key]
  (.unscheduleJob ^Scheduler @*scheduler* key))

(defn ^{:deprecated true} unschedule-job
  "Removes the indicated trigger from the scheduler. If the related job does not have any other triggers,
   and the job is not durable, then the job will also be deleted"
  [^TriggerKey key]
  (delete-trigger key))

(defn delete-job
  "Deletes the identified job and all triggers associated with it from the scheduler"
  [^JobKey key]
  (.deleteJob ^Scheduler @*scheduler* key))

(defn delete-triggers
  "Remove all of the indicated triggers from the scheduler"
  [^List keys]
  (.unscheduleJobs ^Scheduler @*scheduler* keys))

(defn ^{:deprecated true} unschedule-jobs
  "Remove all of the indicated triggers from the scheduler"
  [^List keys]
  (delete-triggers keys))


(defn delete-jobs
  "Remove all of the indicated jobs (and associated triggers) from the scheduler. Bulk equivalent of delete-job"
  [^List keys]
  (.deleteJobs ^Scheduler @*scheduler* keys))



(defn pause-job
  "Pauses a job with the given key"
  [^JobKey key]
  (.pauseJob ^Scheduler @*scheduler* key))

(defn resume-job
  "Resumes a job with the given key"
  [^JobKey key]
  (.resumeJob ^Scheduler @*scheduler* key))

(defn pause-jobs
  "Pauses a group of jobs"
  [^GroupMatcher matcher]
  (.pauseJobs ^Scheduler @*scheduler* matcher))

(defn resume-jobs
  "Resumes a group of jobs"
  [^GroupMatcher matcher]
  (.resumeJobs ^Scheduler @*scheduler* matcher))

(defn pause-trigger
  "Pauses a trigger with the given key"
  [^TriggerKey key]
  (.pauseTrigger ^Scheduler @*scheduler* key))

(defn resume-trigger
  "Resumes a trigger with the given key"
  [^TriggerKey key]
  (.resumeTrigger ^Scheduler @*scheduler* key))

(defn pause-triggers
  "Pauses a group of triggers"
  [^GroupMatcher matcher]
  (.pauseTriggers ^Scheduler @*scheduler* matcher))

(defn resume-triggers
  "Resumes a group of triggers"
  [^GroupMatcher matcher]
  (.resumeTriggers ^Scheduler @*scheduler* matcher))

(defn pause-all!
  "Pauses all triggers and jobs"
  []
  (.pauseAll ^Scheduler @*scheduler*))

(defn resume-all!
  "Resumes all paused triggers and jobs"
  []
  (.resumeAll ^Scheduler @*scheduler*))


;;
;; Querying
;;


(defn get-job-group-names
  "Get the names of all known JobDetail groups."
  []
  (.getJobGroupNames ^Scheduler @*scheduler*))

(defn get-trigger-group-names
  "Get the names of all known Trigger  groups."
  []
  (.getTriggerGroupNames ^Scheduler @*scheduler*))

(defn get-trigger
  "Returns a Trigger instance for the given key."
  ([key]
     (.getTrigger ^Scheduler @*scheduler* (to-trigger-key key)))
  ([^String group ^String key]
     (.getTrigger ^Scheduler @*scheduler* (t/key group key))))

(defn get-job
  "Returns a JobDetail instance for the given key."
  ([key]
     (.getJobDetail ^Scheduler @*scheduler* (to-job-key key)))
  ([^String group ^String key]
     (.getJobDetail ^Scheduler @*scheduler* (j/key group key))))

(defn get-triggers-of-job
  "Returns a set of Trigger instances for the given collection of keys."
  [key]
  (.getTriggersOfJob ^Scheduler @*scheduler* (to-job-key key)))

(defn get-triggers
  "Returns a set of Trigger instances for the given collection of keys."
  [keys]
  (map get-trigger keys))

(defn get-jobs
  "Returns a set of JobDetail instances for the given collection of keys."
  [keys]
  (map get-job keys))

(defn get-trigger-keys
  "Returns a set of keys that match the given group matcher. Commonly used with the functions in the clojurewerkz.quartzite.matchers.*
   namespace."
  [^GroupMatcher gm]
  (.getTriggerKeys ^Scheduler @*scheduler* gm))

(defn get-job-keys
  "Returns a set of keys that match the given group matcher. Commonly used with the functions in the clojurewerkz.quartzite.matchers.*
   namespace."
  [^GroupMatcher gm]
  (.getJobKeys ^Scheduler @*scheduler* gm))

(def ^{:doc "Returns a set of Trigger instances with keys that match the given group matcher.
             Commonly used with the functions in the clojurewerkz.quartzite.matchers.* namespace."}
  get-matching-triggers
  (comp get-triggers get-trigger-keys))

(def ^{:doc "Returns a set of JobDetail instances with keys that match the given group matcher.
             Commonly used with the functions in the clojurewerkz.quartzite.matchers.* namespace."}
  get-matching-jobs
  (comp get-jobs get-job-keys))



(defprotocol KeyPredicates
  (^Boolean scheduled? [key] "Checks if entity with given key already exists within the scheduler"))

(extend-protocol KeyPredicates
  JobKey
  (scheduled? [^JobKey key]
    (.checkExists ^Scheduler @*scheduler* key))

  TriggerKey
  (scheduled? [^TriggerKey key]
    (.checkExists ^Scheduler @*scheduler* key)))

(defn all-scheduled?
  "Returns true if all provided keys (trigger or job) are scheduled"
  [& keys]
  (every? scheduled? keys))

(defn maybe-schedule
  "Adds given job to the scheduler and associates it with given trigger but only if they
   are not already known to the scheduler.

   Like schedule but avoids org.quartz.ObjectAlreadyExistsException by checking if keys of
   provided job and trigger are already scheduled and do not need to be scheduled again"
  [^JobDetail job-detail ^Trigger trigger]
  (when-not (all-scheduled? (.getKey job-detail)
                            (.getKey trigger))
    (.scheduleJob ^Scheduler @*scheduler* job-detail trigger)))


(defn trigger
  "Returns trigger for given key"
  [^JobKey jk]
  (.triggerJob ^Scheduler @*scheduler* jk))


(defn clear!
  "Resets the scheduler by clearing all triggers and jobs from it"
  []
  (.clear ^Scheduler @*scheduler*))


(defn add-scheduler-listener
  "Registers a schedule listener. Use it to hook into Quartz scheduler events"
  [^SchedulerListener listener]
  (.addSchedulerListener ^ListenerManager (.getListenerManager ^Scheduler @*scheduler*) listener))
