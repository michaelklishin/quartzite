(ns clojurewerkz.quartzite.test.schedule.cron
  (:refer-clojure :exclude [key])
  (:use [clojure.test]
        [clojurewerkz.quartzite.schedule cron]
        [clj-time.core :only [date-time]])
  (:import [org.quartz CronScheduleBuilder DateBuilder]
           [org.quartz.impl.triggers CronTriggerImpl]
           [org.joda.time DateTime]
           [java.util TimeZone]))


(deftest test-cron-schedule-dsl-example1
  (let [s     "0 0 3 15 * ?"
        ^DateTime d1     (date-time 2012 2 15 3)
        ^DateTime d2     (date-time 2012 2 16 3)
        ^TimeZone tz     (TimeZone/getTimeZone "Europe/Moscow")
        ^CronTriggerImpl sched  (schedule
                                  (cron-schedule s)
                                  (in-time-zone tz)
                                  (with-misfire-handling-instruction-ignore-misfires)
                                  (finalize))]
    (is (= s (.getCronExpression sched)))
    (is (.willFireOn sched (.toCalendar d1 nil) true))
    (is (not (.willFireOn sched (.toCalendar d2 nil) true)))))


(deftest test-cron-schedule-dsl-example2
  (let [^DateTime d1     (date-time 2012 2 15 15)
        ^DateTime d2     (date-time 2012 2 16 15)
        ^CronTriggerImpl sched (schedule
                                 (daily-at-hour-and-minute 15 0)
                                 (ignore-misfires)
                                 (finalize))]
    (is (= "0 0 15 ? * *" (.getCronExpression sched)))
    (is (.willFireOn sched (.toCalendar d1 nil) true))
    (is (.willFireOn sched (.toCalendar d2 nil) true))))


(deftest test-cron-schedule-dsl-example3
  (let [^DateTime d1     (date-time 2012 1  5  15)
        ^DateTime d2     (date-time 2012 1  6  15)
        ^DateTime d3     (date-time 2012 1  12  15)
        ^CronTriggerImpl sched (schedule
                                 (weekly-on-day-and-hour-and-minute DateBuilder/THURSDAY 15 0)
                                 (with-misfire-handling-instruction-do-nothing)
                                 (finalize))]
    (is (= "0 0 15 ? * 5" (.getCronExpression sched)))
    (is (.willFireOn sched (.toCalendar d1 nil) true))
    (is (not (.willFireOn sched (.toCalendar d2 nil) true)))
    (is (.willFireOn sched (.toCalendar d3 nil) true))))


(deftest test-cron-schedule-dsl-example4
  (let [^DateTime d1     (date-time 2012 1  7  15)
        ^DateTime d2     (date-time 2012 1  3  15)
        ^CronTriggerImpl sched (schedule
                                 (monthly-on-day-and-hour-and-minute 7 15 0)
                                 (with-misfire-handling-instruction-fire-and-proceed)
                                 (finalize))]
    (is (= "0 0 15 7 * ?" (.getCronExpression sched)))
    (is (.willFireOn sched (.toCalendar d1 nil) true))
    (is (not (.willFireOn sched (.toCalendar d2 nil) true)))))
