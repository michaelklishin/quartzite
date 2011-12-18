(ns clojurewerkz.quartzite.test.execution
  (:use [clojure.test])
  (:require [clojurewerkz.quartzite.jobs     :as qj]
            [clojurewerkz.quartzite.triggers :as qt]))


(deftest test-basic-periodic-execution
  (let [latch   (java.util.concurrent.CountDownLatch. 10)]
    ;; (is false "We haven't gotten to these tests yet")
    ))
