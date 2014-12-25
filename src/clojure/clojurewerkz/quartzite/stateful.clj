;; Copyright (c) 2011-2014 Michael S. Klishin, Alex Petrov, and the ClojureWerkz Team
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

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
  (.. ctx getJobDetail getJobDataMap clear)
  (.. ctx getJobDetail getJobDataMap (putAll m))
  m)

(defn get-job-detail-data
  "Returns the stateful job data which has been and will be persisted."
  [^JobExecutionContext jec]
  (:job-data (conv/from-job-detail (.getJobDetail jec))))

