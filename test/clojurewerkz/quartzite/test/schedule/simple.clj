(ns clojurewerkz.quartzite.test.schedule.simple
  (:refer-clojure :exclude [key])
  (:use [clojure.test]
        [clojurewerkz.quartzite.schedule simple])
  (:import [org.quartz DateBuilder SimpleTrigger Trigger]))


(deftest test-simple-schedule-dsl-example1
  (let [i     2
        n     10
        sched (schedule
                (with-interval-in-seconds i)
                (with-repeat-count        n)
                (finalize))]
    (is (= (* 1000 i) (.getRepeatInterval sched)))
    (is (= n          (.getRepeatCount    sched)))))


(deftest test-simple-schedule-dsl-example2
  (let [i     5
        n     10
        sched (schedule
                (with-interval-in-milliseconds i)
                (with-repeat-count        n)
                (finalize))]
    (is (= i (.getRepeatInterval sched)))
    (is (= n (.getRepeatCount    sched)))))


(deftest test-simple-schedule-dsl-example3
  (let [i     3
        n     10
        sched (schedule
                (with-interval-in-minutes i)
                (with-repeat-count        n)
                (finalize))]
    (is (= (* i (DateBuilder/MILLISECONDS_IN_MINUTE)) (.getRepeatInterval sched)))
    (is (= n (.getRepeatCount    sched)))))


(deftest test-simple-schedule-dsl-example4
  (let [i     333
        n     10
        sched (schedule
                (with-interval-in-hours i)
                (with-repeat-count      n)
                (finalize))]
    (is (= (* i (DateBuilder/MILLISECONDS_IN_HOUR)) (.getRepeatInterval sched)))
    (is (= n (.getRepeatCount    sched)))))


(deftest test-simple-schedule-dsl-example5
  (let [i       4
        sched (schedule
                (with-interval-in-hours i)
                (repeat-forever)
                (finalize))]
    (is (= (* i (DateBuilder/MILLISECONDS_IN_HOUR)) (.getRepeatInterval sched)))
    (is (= (SimpleTrigger/REPEAT_INDEFINITELY) (.getRepeatCount sched)))))


(deftest test-simple-schedule-dsl-example6
  (let [i       3
        sched (schedule
                (with-interval-in-days i)
                (repeat-forever)
                (finalize))]
    (is (= (* 24 i (DateBuilder/MILLISECONDS_IN_HOUR)) (.getRepeatInterval sched)))))
