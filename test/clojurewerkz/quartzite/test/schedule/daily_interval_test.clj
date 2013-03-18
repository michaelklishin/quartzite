(ns clojurewerkz.quartzite.test.schedule.daily-interval-test
  (:refer-clojure :exclude [key])
  (:use [clojure.test]
        [clojurewerkz.quartzite.schedule daily-interval])
  (:import [org.quartz DateBuilder DateBuilder$IntervalUnit SimpleTrigger]
           [org.quartz.impl.triggers DailyTimeIntervalTriggerImpl]
           [java.util TreeSet]))


(deftest test-daily-interval-schedule-dsl-example1
  (let [i     2
        n     10
        ^DailyTimeIntervalTriggerImpl sched (schedule
                (with-interval-in-seconds i)
                (with-repeat-count        n)
                (on-days-of-the-week (TreeSet. [(Integer/valueOf 1) (Integer/valueOf 2) (Integer/valueOf 3) (Integer/valueOf 4)]))
                (with-misfire-handling-instruction-ignore-misfires)
                (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/SECOND (.getRepeatIntervalUnit sched)))
    (is (= n          (.getRepeatCount    sched)))))


(deftest test-daily-interval-schedule-dsl-example2
  (let [i     5
        n     10
        ^DailyTimeIntervalTriggerImpl sched (schedule
                (with-interval-in-seconds i)
                (with-repeat-count        n)
                (monday-through-friday)
                (starting-daily-at (time-of-day 15 00 00))
                (ending-daily-at (time-of-day 15 00 00))
                (ignore-misfires)
                (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/SECOND (.getRepeatIntervalUnit sched)))
    (is (= n (.getRepeatCount    sched)))))


(deftest test-daily-interval-schedule-dsl-example3
  (let [i     3
        n     10
        ^DailyTimeIntervalTriggerImpl sched (schedule
                (with-interval-in-minutes i)
                (with-repeat-count        n)
                (saturday-and-sunday)
                (with-misfire-handling-instruction-fire-and-proceed)
                (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/MINUTE (.getRepeatIntervalUnit sched)))
    (is (= n (.getRepeatCount    sched)))))


(deftest test-daily-interval-schedule-dsl-example4
  (let [i     333
        n     10
        ^DailyTimeIntervalTriggerImpl sched (schedule
                (with-interval-in-hours i)
                (with-repeat-count      n)
                (every-day)
                (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= DateBuilder$IntervalUnit/HOUR (.getRepeatIntervalUnit sched)))
    (is (= n (.getRepeatCount    sched)))))


(deftest test-daily-interval-schedule-dsl-example5
  (let [i       4
        ^DailyTimeIntervalTriggerImpl sched (schedule
                (with-interval-in-hours i)
                (on-saturday-and-sunday)
                (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= (SimpleTrigger/REPEAT_INDEFINITELY) (.getRepeatCount sched)))))


(deftest test-daily-interval-schedule-dsl-example6
  (let [i       3
        ^DailyTimeIntervalTriggerImpl sched (schedule
                (with-interval-in-days i)
                (on-monday-through-friday)
                (finalize))]
    (is (= (* 24 i) (.getRepeatInterval sched)))))
