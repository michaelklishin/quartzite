(ns clojurewerkz.quartzite.stateful
  (:import [org.quartz JobExecutionContext])
  (:require [clojurewerkz.quartzite.conversion :as conv]))

(defmacro def-stateful-job
  "Just like clojurewerkz.quartzite.jobs/defjob but defines a stateful job"
  [jtype args & body]
  `(defrecord ~jtype []
     org.quartz.StatefulJob
     (execute [this ~@args]
       ~@body)))

(defn replace!
  "Replaces the job data of the current context execution for the map m, so it will be persisted. Returns m."
  [^JobExecutionContext ctx m]
  (.. ctx (getJobDetail) (getJobDataMap) (clear))
  (.. ctx (getJobDetail) (getJobDataMap) (putAll m))
  m)

(defn get-job-detail-data
  "Returns the stateful job data which has been and will be persisted."
  [^JobExecutionContext jec]
  (:job-data (conv/from-job-detail (.getJobDetail jec))))

