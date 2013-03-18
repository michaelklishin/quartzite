(ns clojurewerkz.quartzite.test.schedule.cron-test
  (:refer-clojure :exclude [key])
  (:use [clojure.test]
        [clojurewerkz.quartzite.schedule cron]
        [clj-time.core :only [date-time]])
  (:import [org.quartz CronScheduleBuilder DateBuilder]
           [org.quartz.impl.triggers CronTriggerImpl]
           [org.joda.time DateTime]
           [java.util TimeZone]))


(deftest test-cron-schedule-dsl-example1
  (let [s     "0 0 12 15 * ?"
        ^DateTime d1     (date-time 2015 2 15 12)
        ^DateTime d2     (date-time 2015 2 16 12)
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
  (let [^DateTime d1     (date-time 2015 2 15 15)
        ^DateTime d2     (date-time 2015 2 16 15)
        ^CronTriggerImpl sched (schedule
                                 (daily-at-hour-and-minute 15 0)
                                 (ignore-misfires)
                                 (finalize))]
    (is (= "0 0 15 ? * *" (.getCronExpression sched)))
    (is (.willFireOn sched (.toCalendar d1 nil) true))
    (is (.willFireOn sched (.toCalendar d2 nil) true))))


(deftest test-cron-schedule-dsl-example3
  (let [^DateTime d1     (date-time 2015 1  1  15)
        ^DateTime d2     (date-time 2015 1  2  15)
        ^DateTime d3     (date-time 2015 1  8  15)
        ^CronTriggerImpl sched (schedule
                                 (weekly-on-day-and-hour-and-minute DateBuilder/THURSDAY 15 0)
                                 (with-misfire-handling-instruction-do-nothing)
                                 (finalize))]
    (is (= "0 0 15 ? * 5" (.getCronExpression sched)))
    (is (.willFireOn sched (.toCalendar d1 nil) true))
    (is (not (.willFireOn sched (.toCalendar d2 nil) true)))
    (is (.willFireOn sched (.toCalendar d3 nil) true))))


(deftest test-cron-schedule-dsl-example4
  (let [^DateTime d1     (date-time 2015 1  7  15)
        ^DateTime d2     (date-time 2015 1  3  15)
        ^CronTriggerImpl sched (schedule
                                 (monthly-on-day-and-hour-and-minute 7 15 0)
                                 (with-misfire-handling-instruction-fire-and-proceed)
                                 (finalize))]
    (is (= "0 0 15 7 * ?" (.getCronExpression sched)))
    (is (.willFireOn sched (.toCalendar d1 nil) true))
    (is (not (.willFireOn sched (.toCalendar d2 nil) true)))))

(deftest test-cron-schedule-last-day-of-the-month
  (let [^DateTime d1     (date-time 2015 1  7  15)
        ^DateTime d2     (date-time 2015 1  31  15)
        ^DateTime d3     (date-time 2015 2  28  15)
        ^DateTime d4     (date-time 2015 3  31  15)
        ^DateTime d5     (date-time 2015 4  30  15)
        ^DateTime d6     (date-time 2015 4  28  15)
        ^DateTime d7     (date-time 2016 2  29  15)
        ^DateTime d8     (date-time 2017 2  28  15)
        ^CronTriggerImpl sched (schedule
                                (cron-schedule "0 0 15 L * ?")
                                 (finalize))]
    (is (not (.willFireOn sched (.toCalendar d1 nil) true)))
    (is (.willFireOn sched (.toCalendar d2 nil) true))
    (is (.willFireOn sched (.toCalendar d3 nil) true))
    (is (.willFireOn sched (.toCalendar d4 nil) true))
    (is (.willFireOn sched (.toCalendar d5 nil) true))
    (is (not (.willFireOn sched (.toCalendar d6 nil) true)))
    (is (.willFireOn sched (.toCalendar d7 nil) true))
    (is (.willFireOn sched (.toCalendar d8 nil) true))))

(deftest test-cron-schedule-next-to-last-day-of-the-month
  (let [^DateTime d1     (date-time 2015 1  7  15)
        ^DateTime d2     (date-time 2015 1  30  15)
        ^DateTime d3     (date-time 2015 2  27  15)
        ^DateTime d4     (date-time 2015 3  30  15)
        ^DateTime d5     (date-time 2015 4  29  15)
        ^DateTime d6     (date-time 2015 5  31  15)
        ^CronTriggerImpl sched (schedule
                                (cron-schedule "0 0 15 L-1 * ?")
                                 (finalize))]
    (is (not (.willFireOn sched (.toCalendar d1 nil) true)))
    (is (.willFireOn sched (.toCalendar d2 nil) true))
    (is (.willFireOn sched (.toCalendar d3 nil) true))
    (is (.willFireOn sched (.toCalendar d4 nil) true))
    (is (.willFireOn sched (.toCalendar d5 nil) true))
    (is (not (.willFireOn sched (.toCalendar d6 nil) true)))))
