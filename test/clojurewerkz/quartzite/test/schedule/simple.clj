(ns clojurewerkz.quartzite.test.schedule.simple
  (:refer-clojure :exclude [key])
  (:use [clojure.test]
        [clojurewerkz.quartzite.schedule simple])
  (:import [org.quartz DateBuilder SimpleTrigger Trigger]))


(deftest test-simple-schedule-dsl-example1
  (let [i     2
        n     10
        trigger (schedule
                 (with-interval-in-seconds i)
                 (with-repeat-count        n)
                 (finalize))]
    (is (= (* 1000 i) (.getRepeatInterval trigger)))
    (is (= n          (.getRepeatCount    trigger)))))


(deftest test-simple-schedule-dsl-example2
  (let [i     5
        n     10
        trigger (schedule
                 (with-interval-in-milliseconds i)
                 (with-repeat-count        n)
                 (finalize))]
    (is (= i (.getRepeatInterval trigger)))
    (is (= n (.getRepeatCount    trigger)))))


(deftest test-simple-schedule-dsl-example3
  (let [i     3
        n     10
        trigger (schedule
                 (with-interval-in-minutes i)
                 (with-repeat-count        n)
                 (finalize))]
    (is (= (* i (DateBuilder/MILLISECONDS_IN_MINUTE)) (.getRepeatInterval trigger)))
    (is (= n (.getRepeatCount    trigger)))))


(deftest test-simple-schedule-dsl-example4
  (let [i     333
        n     10
        trigger (schedule
                 (with-interval-in-hours i)
                 (with-repeat-count      n)
                 (finalize))]
    (is (= (* i (DateBuilder/MILLISECONDS_IN_HOUR)) (.getRepeatInterval trigger)))
    (is (= n (.getRepeatCount    trigger)))))


(deftest test-simple-schedule-dsl-example5
  (let [i       4
        trigger (schedule
                 (with-interval-in-hours i)
                 (repeat-forever)
                 (finalize))]
    (is (= (* i (DateBuilder/MILLISECONDS_IN_HOUR)) (.getRepeatInterval trigger)))
    (is (= (SimpleTrigger/REPEAT_INDEFINITELY) (.getRepeatCount trigger)))))
