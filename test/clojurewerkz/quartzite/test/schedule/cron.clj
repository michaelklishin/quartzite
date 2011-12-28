(ns clojurewerkz.quartzite.test.schedule.cron
  (:refer-clojure :exclude [key])
  (:use [clojure.test]
        [clojurewerkz.quartzite.schedule cron]
        [clj-time.core :only [date-time]])
  (:import [org.quartz CronScheduleBuilder]
           [org.quartz.impl.triggers CronTriggerImpl]
           [org.joda.time DateTime]))


(deftest test-cron-schedule-dsl-example1
  (let [s     "0 0 3 15 * ?"
        ^DateTime d1     (date-time 2012 2 15 3)
        ^DateTime d2     (date-time 2012 2 16 3)
        ^CronTriggerImpl sched  (schedule
                                  (cron-schedule s)
                                  (finalize))]
    (is (= s (.getCronExpression sched)))
    (is (.willFireOn sched (.toCalendar d1 nil) true))
    (is (not (.willFireOn sched (.toCalendar d2 nil) true)))))


(deftest test-cron-schedule-dsl-example2
  (let [^DateTime d1     (date-time 2012 2 15 15)
        ^DateTime d2     (date-time 2012 2 16 15)
        ^CronTriggerImpl sched (schedule
                                 (daily-at-hour-and-minute 15 0)
                                 (finalize))]
    (is (= "0 0 15 ? * *" (.getCronExpression sched)))
    (is (.willFireOn sched (.toCalendar d1 nil) true))
    (is (.willFireOn sched (.toCalendar d2 nil) true))))
