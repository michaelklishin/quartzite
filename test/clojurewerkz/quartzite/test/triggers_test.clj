(ns clojurewerkz.quartzite.test.triggers-test
  (:refer-clojure :exclude [key])
  (:require [clojurewerkz.quartzite.jobs :as jobs]
            [clojure.test :refer :all]
            [clojurewerkz.quartzite.conversion :refer :all]
            [clojurewerkz.quartzite.triggers :refer :all]
            [clj-time.core :refer [now minus plus days hours minutes]])
  (:import [java.util Date]
           [org.joda.time DateTime]))


(deftest test-instantiation-of-keys
  (is (not (= (key) (key))))
  (is (not (= (key "key1") (key))))
  (is (not (= (key "key1") (key "key2"))))
  (is (not (= (key "key1" "group1") (key "key1" "group2"))))
  (is (= (key "key1" "group1") (key "key1" "group1")))
  (is (= (key "key1") (key "key1"))))


(deftest test-trigger-builder-dsl-example1
  (let [trigger (build (with-identity    "basic.trigger1" "basic.group1")
                       (with-description "A description"))]
    (is (= (key "basic.trigger1" "basic.group1") (.getKey trigger)))
    (is (= "A description" (.getDescription trigger)))))

(deftest test-trigger-builder-dsl-example2
  (let [trigger (build (with-identity    "basic.trigger2")
                       (with-description "A description")
                       (with-priority    3))]
    (is (= 3 (.getPriority trigger)))))


(deftest test-trigger-builder-dsl-example3
  (let [trigger (build (with-identity "basic.trigger3")
                       (modified-by-calendar "my.holidays.calendar"))]
    (is (= "my.holidays.calendar" (.getCalendarName trigger)))))


(deftest test-trigger-builder-dsl-example4
  (let [d       (Date.)
        trigger (build (with-identity "basic.trigger4")
                       (start-now))
        st      (.getStartTime trigger)]
    (is (= (.getYear d)    (.getYear st)))
    (is (= (.getMonth d)   (.getMonth st)))
    (is (= (.getDay d)     (.getDay st)))
    (is (= (.getHours d)   (.getHours st)))
    (is (= (.getMinutes d) (.getMinutes st)))))


(deftest test-trigger-builder-dsl-example5
  (let [start   (.toDate ^DateTime (now))
        end     (.toDate ^DateTime (plus (now) (hours 3)))
        trigger (build (with-identity "basic.trigger5")
                       (start-at start)
                       (end-at   end))]
    (is (= start (.getStartTime trigger)))
    (is (= end   (.getEndTime trigger)))))


(deftest test-trigger-builder-dsl-example6
  (let [trigger (build (with-identity "basic.trigger6")
                       (start-now)
                       (for-job "some.job"))]
    (is (= (jobs/key "some.job") (.getJobKey trigger)))))


(deftest test-trigger-builder-dsl-example7
  (let [trigger (build (with-identity "basic.trigger7")
                       (start-now)
                       (for-job (jobs/key "some.job")))]
    (is (= (jobs/key "some.job") (.getJobKey trigger)))))


(deftest test-trigger-builder-dsl-example8
  (let [trigger (build (with-identity "basic.trigger8")
                       (start-now)
                       (for-job "collect.underpants" "business"))]
    (is (= (jobs/key "collect.underpants" "business") (.getJobKey trigger)))))

(deftest test-trigger-builder-dsl-example9
  (let [trigger (build (with-identity "basic.trigger8")
                       (start-now)
                       (for-job "collect.underpants" "business")
                       (using-job-data { :who "Gnomes" :what "Know about business" }))]
    (is (= (to-job-data { "who" "Gnomes" "what" "Know about business" }) (.getJobDataMap trigger)))
    (is (= { "who" "Gnomes" "what" "Know about business" } (from-job-data (.getJobDataMap trigger))))))

(deftest test-job-builder-dsl-example10
  (let [tk  (key "basic.trigger10" "basic.group10")
        trigger (build (with-identity tk))]
    (is (= tk (.getKey trigger)))))

(deftest test-job-builder-dsl-example11
  (let [tk  (key "basic.trigger11")
        trigger (build (with-identity tk))]
    (is (= tk (.getKey trigger)))))