(ns clojurewerkz.quartzite.test.triggers
  (:refer-clojure :exclude [key])
  (:use [clojure.test]
        [clojurewerkz.quartzite.triggers]))


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
  (let [trigger (build (with-identity "basic.trigger2")
                       (modified-by-calendar "my.holidays.calendar"))]
    (is (= "my.holidays.calendar" (.getCalendarName trigger)))))
