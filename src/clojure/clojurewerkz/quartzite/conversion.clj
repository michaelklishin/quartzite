(ns clojurewerkz.quartzite.conversion
  (:refer-clojure :exclude [key])
  (:require [clojure.walk :as wlk])
  (:import [org.quartz JobDataMap JobExecutionContext]
           org.quartz.utils.Key
           [org.quartz TriggerKey JobKey]
           clojure.lang.IPersistentMap
           [org.quartz JobDetail Trigger]))


;;
;; API
;;

;; Monger and other ClojureWerkz project integration extension point. MK.
(defprotocol JobDataMapConversion
  (^org.quartz.JobDataMap
    to-job-data   [input] "Instantiates a JobDataMap instance from a Clojure map")
  (from-job-data [input] "Converts a JobDataMap to a Clojure map"))

(extend-protocol JobDataMapConversion
  IPersistentMap
  (to-job-data [^clojure.lang.IPersistentMap input]
    (JobDataMap. (wlk/stringify-keys input)))


  JobDataMap
  (from-job-data [^JobDataMap input]
    (wlk/stringify-keys (into {} input)))

  JobExecutionContext
  (from-job-data [^JobExecutionContext input]
    (from-job-data (.getMergedJobDataMap input))))


(defn from-key
  "Converts a Key instance (TriggerKey, JobKey) to a Clojure map"
  [^Key key]
  {:name (.getName key)
   :group (.getGroup key)})

(defn from-job-detail
  [^JobDetail jd]
  {:key (from-key (.getKey jd))
   :description (.getDescription jd)
   :job-data (from-job-data (.getJobDataMap jd))})

(defn from-trigger
  [^Trigger t]
  {:key (from-key (.getKey t))
   :description (.getDescription t)
   :calendar-name (.getCalendarName t)
   :start-time (.getStartTime t)
   :end-time (.getEndTime t)
   :next-fire-time (.getNextFireTime t)
   :previous-fire-time (.getPreviousFireTime t)})



(defprotocol DateConversion
  (to-date [input] "Converts given input to java.util.Date"))

(extend-protocol DateConversion
  java.util.Date
  (to-date [input]
    input)

  ;; common cases
  org.joda.time.DateTime
  (to-date [input]
    (.toDate input))
  org.joda.time.MutableDateTime
  (to-date [input]
    (.toDate input))

  ;; catch-all for Joda Date types convertable to java.util.Date
  org.joda.time.base.BaseDateTime
  (to-date [input]
    (.toDate input)))


(defprotocol KeyCoercion
  (^org.quartz.TriggerKey
    to-trigger-key [input] "Converts a key to a TriggerKey instance")
  (^org.quartz.JobKey
    to-job-key [input] "Converts a key to a JobKey instance"))

(extend-protocol KeyCoercion
  org.quartz.TriggerKey
  (to-trigger-key [input]
    input)

  org.quartz.JobKey
  (to-job-key [input]
    input)

  String
  (to-trigger-key [input]
    (TriggerKey. input))
  (to-job-key [input]
    (JobKey. input)))
