(ns clojurewerkz.quartzite.test.execution
  (:use [clojure.test])
  (:require [clojurewerkz.quartzite.scheduler :as sched]
            [clojurewerkz.quartzite.jobs      :as j]
            [clojurewerkz.quartzite.triggers  :as t]
            [clojurewerkz.quartzite.schedule.simple :as s])
  (:import [java.util.concurrent CountDownLatch]))

(def latch (CountDownLatch. 10))

(defrecord AJob []
  org.quartz.Job
  (execute [this ctx]
    (println "Executing AJob")
    (.countDown latch)))


(sched/start)


(deftest test-basic-periodic-execution
  (is (sched/started?))
  (let [job     (j/build
                 (j/of-type clojurewerkz.quartzite.test.execution.AJob)
                 (j/with-identity "clojurewerkz.quartzite.test.execution.job1" "tests"))
        trigger  (t/build
                  (t/start-now)
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-milliseconds 200))))]
    (sched/schedule job trigger)    
    (.await latch)))
