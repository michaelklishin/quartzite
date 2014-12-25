;; Copyright (c) 2011-2014 Michael S. Klishin, Alex Petrov, and the ClojureWerkz Team
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns clojurewerkz.quartzite.scheduler
  (:import [org.quartz Scheduler JobDetail JobKey Trigger TriggerKey SchedulerListener ListenerManager JobExecutionContext]
           org.quartz.impl.matchers.GroupMatcher
           java.util.List)
  (:require [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.conversion :refer :all]))

;;
;; API
;;

(defn initialize
  "Initializes a scheduler."
  []
  (org.quartz.impl.StdSchedulerFactory/getDefaultScheduler))

(defn start
  "Starts Quartzite's scheduler. Newly initialized scheduler is not active (in standby mode),
   this function starts it"
  [^Scheduler scheduler]
  (doto scheduler
    .start))

(defn start-delayed
  "Starts Quartzite's scheduler after a delay in seconds"
  [^Scheduler scheduler ^long seconds]
  (.startDelayed ^Scheduler scheduler seconds))

(defn standby
  "Puts scheduler in standby mode. When in scheduler is standby mode, no triggers will fire"
  [^Scheduler scheduler]
  (.standby scheduler))

(defn shutdown
  "Shuts scheduler down, releasing all the resources associated with it. When passed true causes scheduler
   to wait for running jobs to complete before shutting down"
  ([^Scheduler scheduler]
     (.shutdown scheduler))
  ([^Scheduler scheduler ^Boolean wait-for-jobs-to-complete]
     (.shutdown scheduler wait-for-jobs-to-complete)))

(defn started?
  "Returns true if the scheduler has been ever started, false otherwise"
  [^Scheduler scheduler]
  (.isStarted ^Scheduler scheduler))

(defn standby?
  "Returns true if the scheduler is in standby mode, false otherwise"
  [^Scheduler scheduler]
  (.isInStandbyMode ^Scheduler scheduler))

(defn shutdown?
  "Returns true if the scheduler has been shut down, false otherwise"
  [^Scheduler scheduler]
  (.isShutdown ^Scheduler scheduler))

(defn schedule
  "Adds given job to the scheduler and associates it with given trigger.
   Trigger controls job execution schedule, initial execution time and other characteristics"
  [^Scheduler scheduler ^JobDetail job-detail ^Trigger trigger]
  (.scheduleJob ^Scheduler scheduler job-detail trigger))

(defn add-job
  "Adds given job to the scheduler with no associated trigger"
  ([^Scheduler scheduler ^JobDetail job-detail ^Boolean replace]
     (.addJob ^Scheduler scheduler job-detail replace))
  
  ([^Scheduler scheduler ^JobDetail job-detail]
     (add-job scheduler job-detail false)))

(defn add-trigger
  "Adds given trigger to the scheduled job with which the trigger has been associated"
  [^Scheduler scheduler ^Trigger trigger]
  (.scheduleJob ^Scheduler scheduler trigger))


(defn delete-trigger
  "Removes the indicated trigger from the scheduler. If the related job does not have any other triggers,
   and the job is not durable, then the job will also be deleted"
  [^Scheduler scheduler ^TriggerKey key]
  (.unscheduleJob ^Scheduler scheduler key))

(defn delete-job
  "Deletes the identified job and all triggers associated with it from the scheduler"
  [^Scheduler scheduler ^JobKey key]
  (.deleteJob ^Scheduler scheduler key))

(defn delete-triggers
  "Remove all of the indicated triggers from the scheduler"
  [^Scheduler scheduler ^List keys]
  (.unscheduleJobs ^Scheduler scheduler keys))


(defn delete-jobs
  "Remove all of the indicated jobs (and associated triggers) from the scheduler. Bulk equivalent of delete-job"
  [^Scheduler scheduler ^List keys]
  (.deleteJobs ^Scheduler scheduler keys))



(defn pause-job
  "Pauses a job with the given key"
  [^Scheduler scheduler ^JobKey key]
  (.pauseJob ^Scheduler scheduler key))

(defn resume-job
  "Resumes a job with the given key"
  [^Scheduler scheduler ^JobKey key]
  (.resumeJob ^Scheduler scheduler key))

(defn pause-jobs
  "Pauses a group of jobs"
  [^Scheduler scheduler ^GroupMatcher matcher]
  (.pauseJobs ^Scheduler scheduler matcher))

(defn resume-jobs
  "Resumes a group of jobs"
  [^Scheduler scheduler ^GroupMatcher matcher]
  (.resumeJobs ^Scheduler scheduler matcher))

(defn pause-trigger
  "Pauses a trigger with the given key"
  [^Scheduler scheduler ^TriggerKey key]
  (.pauseTrigger ^Scheduler scheduler key))

(defn resume-trigger
  "Resumes a trigger with the given key"
  [^Scheduler scheduler ^TriggerKey key]
  (.resumeTrigger ^Scheduler scheduler key))

(defn pause-triggers
  "Pauses a group of triggers"
  [^Scheduler scheduler ^GroupMatcher matcher]
  (.pauseTriggers ^Scheduler scheduler matcher))

(defn resume-triggers
  "Resumes a group of triggers"
  [^Scheduler scheduler ^GroupMatcher matcher]
  (.resumeTriggers ^Scheduler scheduler matcher))

(defn pause-all!
  "Pauses all triggers and jobs"
  [^Scheduler scheduler]
  (.pauseAll ^Scheduler scheduler))

(defn resume-all!
  "Resumes all paused triggers and jobs"
  [^Scheduler scheduler]
  (.resumeAll ^Scheduler scheduler))


;;
;; Querying
;;


(defn get-job-group-names
  "Get the names of all known JobDetail groups."
  [^Scheduler scheduler]
  (.getJobGroupNames ^Scheduler scheduler))

(defn get-trigger-group-names
  "Get the names of all known Trigger  groups."
  [^Scheduler scheduler]
  (.getTriggerGroupNames ^Scheduler scheduler))

(defn get-trigger
  "Returns a Trigger instance for the given key."
  ([^Scheduler scheduler key]
     (.getTrigger ^Scheduler scheduler key))
  ([^Scheduler scheduler ^String group ^String key]
     (.getTrigger ^Scheduler scheduler (t/key group key))))

(defn get-job
  "Returns a JobDetail instance for the given key."
  ([^Scheduler scheduler key]
     (.getJobDetail ^Scheduler scheduler (to-job-key key)))
  ([^Scheduler scheduler ^String group ^String key]
     (.getJobDetail ^Scheduler scheduler (j/key group key))))

(defn get-triggers-of-job
  "Returns a set of Trigger instances for the given collection of keys."
  [^Scheduler scheduler key]
  (.getTriggersOfJob ^Scheduler scheduler (to-job-key key)))

(defn get-triggers
  "Returns a set of Trigger instances for the given collection of keys."
  [^Scheduler scheduler keys]
  (let [xs (map (fn [k] (if (instance? JobKey k)
                          (get-triggers-of-job scheduler k)
                          (get-trigger scheduler k))) keys)]
    (flatten xs)))

(defn get-jobs
  "Returns a set of JobDetail instances for the given collection of keys."
  [^Scheduler scheduler keys]
  (map (fn [k] (get-job scheduler k)) keys))

(defn get-currently-executing-jobs
  "Returns a set of JobExecutionContext that represent the currently executing jobs for a given key"
  [^Scheduler scheduler key]
  (filter #(= (.. ^JobExecutionContext % getJobDetail getKey) (j/key key))
          (.getCurrentlyExecutingJobs ^Scheduler scheduler)))

(defn currently-executing-job?
  "Returns true if there is currently executing job for the given key"
  [^Scheduler scheduler key]
  (boolean (seq (get-currently-executing-jobs scheduler key))))

(defn get-trigger-keys
  "Returns a set of keys that match the given group matcher. Commonly used with the functions in the clojurewerkz.quartzite.matchers.*
   namespace."
  [^Scheduler scheduler ^GroupMatcher gm]
  (.getTriggerKeys ^Scheduler scheduler gm))

(defn get-job-keys
  "Returns a set of keys that match the given group matcher. Commonly used with the functions in the clojurewerkz.quartzite.matchers.*
   namespace."
  [^Scheduler scheduler ^GroupMatcher gm]
  (.getJobKeys ^Scheduler scheduler gm))

(defn get-matching-triggers
  "Returns a set of Trigger instances with keys that match the given group matcher.
   Commonly used with the functions in the clojurewerkz.quartzite.matchers.* namespace."
  [^Scheduler scheduler ^GroupMatcher gm]
  (->> (get-trigger-keys scheduler gm)
       (get-triggers scheduler)))

(defn get-matching-jobs
  "Returns a set of JobDetail instances with keys that match the given group matcher.
   Commonly used with the functions in the clojurewerkz.quartzite.matchers.* namespace."
  [^Scheduler scheduler ^GroupMatcher gm]
  (->> (get-job-keys scheduler gm)
       (get-triggers scheduler)))



(defn scheduled?
  "Checks if entity with given key already exists within the scheduler"
  [^Scheduler scheduler key]
  (.checkExists ^Scheduler scheduler key))

(defn all-scheduled?
  "Returns true if all provided keys (trigger or job) are scheduled"
  [^Scheduler scheduler & keys]
  (every? (fn [k] (scheduled? scheduler k)) keys))

(defn maybe-schedule
  "Adds given job to the scheduler and associates it with given trigger but only if they
   are not already known to the scheduler.

   Like schedule but avoids org.quartz.ObjectAlreadyExistsException by checking if keys of
   provided job and trigger are already scheduled and do not need to be scheduled again"
  [^Scheduler scheduler ^JobDetail job-detail ^Trigger trigger]
  (when-not (all-scheduled? ^Scheduler scheduler
                            (.getKey job-detail)
                            (.getKey trigger))
    (.scheduleJob ^Scheduler scheduler job-detail trigger)))

(defn trigger
  "Returns trigger for given key"
  [^Scheduler scheduler ^JobKey jk]
  (.triggerJob ^Scheduler scheduler jk))

(defn clear!
  "Resets the scheduler by clearing all triggers and jobs from it"
  [^Scheduler scheduler]
  (.clear ^Scheduler scheduler))

(defn add-scheduler-listener
  "Registers a schedule listener. Use it to hook into Quartz scheduler events"
  [^Scheduler scheduler ^SchedulerListener listener]
  (.addSchedulerListener ^ListenerManager (.getListenerManager ^Scheduler scheduler) listener))
