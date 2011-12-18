(ns clojurewerkz.quartzite.test.jobs
  (:refer-clojure :exclude [key])
  (:use [clojure.test]
        [clojurewerkz.quartzite.jobs]))


(deftest test-instantiation-of-keys
  (is (not (= (key) (key))))
  (is (not (= (key "key1") (key))))
  (is (not (= (key "key1") (key "key2"))))
  (is (not (= (key "key1" "group1") (key "key1" "group2"))))
  (is (= (key "key1" "group1") (key "key1" "group1")))
  (is (= (key "key1") (key "key1"))))


(deftest test-job-builder-dsl-example1
  (let [job (build (with-identity    "basic.job1" "basic.group1")
                   (with-description "A description"))]
    (is (= (key "basic.job1" "basic.group1") (.getKey job)))))
