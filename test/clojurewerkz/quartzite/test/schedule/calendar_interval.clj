(ns clojurewerkz.quartzite.test.schedule.calendar-interval
  (:refer-clojure :exclude [key])
  (:use [clojure.test]
        [clojurewerkz.quartzite.schedule calendar-interval])
  (:import [org.quartz DateBuilder DateBuilder$IntervalUnit]
           [org.quartz.impl.triggers CalendarIntervalTriggerImpl]))


(deftest test-calendar-interval-schedule-dsl-example1
  (let [i     2
        n     10
        ^CalendarIntervalTriggerImpl sched (schedule
                                              (with-interval-in-seconds i)
                                              (with-misfire-handling-instruction-ignore-misfires)
                                              (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/SECOND (.getRepeatIntervalUnit sched)))))


(deftest test-calendar-interval-schedule-dsl-example2
  (let [i     5
        n     10
        ^CalendarIntervalTriggerImpl sched (schedule
                                              (with-interval-in-seconds i)
                                              (ignore-misfires)
                                              (finalize))]
    (is (= i (.getRepeatInterval sched)))))


(deftest test-calendar-interval-schedule-dsl-example3
  (let [i     3
        n     10
        ^CalendarIntervalTriggerImpl sched (schedule
                                              (with-interval-in-minutes i)
                                              (with-misfire-handling-instruction-fire-and-proceed)
                                              (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/MINUTE (.getRepeatIntervalUnit sched)))))


(deftest test-calendar-interval-schedule-dsl-example4
  (let [i     333
        n     10
        ^CalendarIntervalTriggerImpl sched (schedule
                                              (with-interval-in-hours i)
                                              (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/HOUR (.getRepeatIntervalUnit sched)))))


(deftest test-calendar-interval-schedule-dsl-example5
  (let [i       4
        ^CalendarIntervalTriggerImpl sched (schedule
                                              (with-interval-in-days i)
                                              (with-misfire-handling-instruction-do-nothing)
                                              (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/DAY (.getRepeatIntervalUnit sched)))))


(deftest test-calendar-interval-schedule-dsl-example6
  (let [i       3
        ^CalendarIntervalTriggerImpl sched (schedule
                                              (with-interval-in-weeks i)
                                              (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/WEEK (.getRepeatIntervalUnit sched)))))


(deftest test-calendar-interval-schedule-dsl-example7
  (let [i       3
        ^CalendarIntervalTriggerImpl sched (schedule
                                              (with-interval-in-months i)
                                              (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/MONTH (.getRepeatIntervalUnit sched)))))

(deftest test-calendar-interval-schedule-dsl-example8
  (let [i       3
        ^CalendarIntervalTriggerImpl sched (schedule
                                              (with-interval-in-years i)
                                              (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/YEAR (.getRepeatIntervalUnit sched)))))
