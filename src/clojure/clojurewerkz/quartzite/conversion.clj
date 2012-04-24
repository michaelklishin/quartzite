(ns clojurewerkz.quartzite.conversion
  (:refer-clojure :exclude [key])
  (:import [org.quartz JobDataMap JobExecutionContext]
           [org.quartz.utils Key]
           [clojure.lang IPersistentMap]
           [org.quartz.impl JobDetailImpl]))


;;
;; API
;;

;; we may go back to regular functions eventually, we will see
;; if JodaTime or Monger integration may need/benefit from extension points here. MK.
(defprotocol JobDataMapConversion
  (to-job-data   [input] "Converts Clojure data to JobDataMap that Quartz uses")
  (from-job-data [input] "Converts JobDataMap that Quartz uses to Clojure data structures"))

(extend-protocol JobDataMapConversion
  IPersistentMap
  (to-job-data [^java.lang.IPersistentMap input]
    (JobDataMap. ^Map input))


  JobDataMap
  (from-job-data [^JobDataMap input]
    (into {} input))

  JobExecutionContext
  (from-job-data [^JobExecutionContext input]
    (from-job-data (.getMergedJobDataMap input))))


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


