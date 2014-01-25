(ns clojurewerkz.quartzite.test.jobs-test
  (:refer-clojure :exclude [key])
  (:require [clojure.test :refer :all]
            [clojurewerkz.quartzite.jobs :refer :all]
            [clojurewerkz.quartzite.conversion :refer :all])
  (:import [org.quartz JobDataMap]))


;;
;; Keys
;;

(deftest test-instantiation-of-keys
  (is (not (= (key) (key))))
  (is (not (= (key "key1") (key))))
  (is (not (= (key "key1") (key "key2"))))
  (is (not (= (key "key1" "group1") (key "key1" "group2"))))
  (is (= (key "key1" "group1") (key "key1" "group1")))
  (is (= (key "key1") (key "key1"))))



;;
;; Builder DSL
;;

(defrecord AJob []
  org.quartz.Job
  (execute [this ctx]
    ;; intentional no-op
    ))

(deftest test-job-builder-dsl-example1
  (let [job (build (with-identity    "basic.job1" "basic.group1")
                   (with-description "A description"))]
    (is (= (key "basic.job1" "basic.group1") (.getKey job)))))


(deftest test-job-builder-dsl-example2
  (let [job (build (with-identity    "basic.job2" "basic.group2")
                   (with-description "A description"))]
    (is (= "A description" (.getDescription job)))))


(deftest test-job-builder-dsl-example3
  (let [job (build (with-identity    "basic.job3" "basic.group3")
                   (with-description "A description")
                   (of-type AJob))]
   (.getJobClass job)))

(deftest test-job-builder-dsl-example4
  (let [job (build (with-identity    "basic.job4" "basic.group4")
                   (store-durably)
                   (request-recovery))]
    (is (.requestsRecovery job))
    (is (.isDurable job))))

(deftest test-job-builder-dsl-example5
  (let [jk  (key "basic.job5" "basic.group5")
        job (build (with-identity jk)
                   (store-durably))]
    (is (= jk (.getKey job)))))

(deftest test-job-builder-dsl-example6
  (let [jk  (key "basic.job6")
        job (build (with-identity jk)
                   (store-durably))]
    (is (= jk (.getKey job)))))


;;
;; Clojure <=> JobDataMap conversion
;;

(deftest test-conversion-of-clojure-maps-to-job-data-maps
  (let [input  {:long 100 "string" "Hello, Quartz" :keyword :clojure}
        output (to-job-data input)]
    (is (instance? JobDataMap output))
    (is (= :clojure        (.get output "keyword")))
    (is (= "Hello, Quartz" (.get output "string")))
    (is (= 100             (.get output "long")))))

(deftest test-conversion-of-job-data-maps-to-clojure-maps
  (let [input  (JobDataMap. {:keyword :clojure "string" "Hello, Quartz" :long 100})
        output (from-job-data input)]
    (is (map? output))
    (is (= :clojure        (get output "keyword")))
    (is (= "Hello, Quartz" (get output "string")))
    (is (= 100             (get output "long")))))
