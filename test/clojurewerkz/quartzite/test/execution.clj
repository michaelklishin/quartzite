(ns clojurewerkz.quartzite.test.execution
  (:use [clojure.test])
  (:require [clojurewerkz.quartzite.jobs     :as qj]
            [clojurewerkz.quartzite.triggers :as qt]))


(deftest test-basic-periodic-execution
  (let [latch   (java.util.concurrent.CountDownLatch. 10)
        job     (qj/build (qj/with-identity    "basic.job1")
                          (qj/with-group       "basic.group1")
                          (qj/with-description "A description"))]
    (is false)))
