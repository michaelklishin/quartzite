(ns clojurewerkz.quartzite.test.execution
  (:use [clojure.test]
        [clojurewerkz.quartzite.conversion])
  (:require [clojurewerkz.quartzite.scheduler :as sched]
            [clojurewerkz.quartzite.jobs      :as j]
            [clojurewerkz.quartzite.triggers  :as t]
            [clojurewerkz.quartzite.schedule.simple :as s])
  (:import [java.util.concurrent CountDownLatch]))


(sched/start)


;;
;; Case 1
;;

(def latch1 (CountDownLatch. 10))

(defrecord JobA []
  org.quartz.Job
  (execute [this ctx]
    (.countDown ^CountDownLatch latch1)))



(deftest test-basic-periodic-execution-with-a-job-defined-using-defrecord
  (is (sched/started?))
  (let [job     (j/build
                 (j/of-type clojurewerkz.quartzite.test.execution.JobA)
                 (j/with-identity "clojurewerkz.quartzite.test.execution.job1" "tests"))
        trigger  (t/build
                  (t/start-now)
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-milliseconds 200))))]
    (sched/schedule job trigger)
    (.await ^CountDownLatch latch1)))



;;
;; Case 2
;;

(def counter2 (atom 0))

(defrecord JobB []
  org.quartz.Job
  (execute [this ctx]
    (swap! counter2 inc)))

(deftest test-unscheduling-of-a-job-defined-using-defrecord
  (is (sched/started?))
  (let [jk      (j/key "clojurewerkz.quartzite.test.execution.job2"     "tests")
        tk      (t/key "clojurewerkz.quartzite.test.execution.trigger2" "tests")
        job     (j/build
                 (j/of-type clojurewerkz.quartzite.test.execution.JobB)
                 (j/with-identity "clojurewerkz.quartzite.test.execution.job2" "tests"))
        trigger  (t/build
                  (t/start-now)
                  (t/with-identity "clojurewerkz.quartzite.test.execution.trigger2" "tests")
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-milliseconds 400))))]
    (sched/schedule job trigger)
    (is (sched/scheduled? jk))
    (is (sched/scheduled? tk))
    (Thread/sleep 2000)
    (sched/unschedule tk)
    (is (not (sched/scheduled? jk)))
    (is (not (sched/scheduled? tk)))
    (Thread/sleep 2000)
    (is (< @counter2 7))))



;;
;; Case 3
;;

(def counter3 (atom 0))

(defrecord JobC []
  org.quartz.Job
  (execute [this ctx]
    (swap! counter3 inc)))

(deftest test-manual-triggering-of-a-job-defined-using-defrecord
  (is (sched/started?))
  (let [jk      (j/key "clojurewerkz.quartzite.test.execution.job3" "tests")
        tk      (t/key "clojurewerkz.quartzite.test.execution.trigger3" "tests")
        job     (j/build
                 (j/of-type clojurewerkz.quartzite.test.execution.JobC)
                 (j/with-identity "clojurewerkz.quartzite.test.execution.job3" "tests"))
        trigger  (t/build
                  (t/start-now)
                  (t/with-identity "clojurewerkz.quartzite.test.execution.trigger3" "tests")
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-seconds 2))))]
    (sched/schedule job trigger)
    (sched/trigger jk)
    (Thread/sleep 500)
    (is (= 2 @counter3))))


;;
;; Case 4
;;

(def value4 (atom nil))

(defrecord JobD []
  org.quartz.Job
  (execute [this ctx]
    (swap! value4 (fn [_]
                    (from-job-data (.getMergedJobDataMap ctx))))))

(deftest test-job-data-access
  (is (sched/started?))
  (let [jk      (j/key "clojurewerkz.quartzite.test.execution.job4" "tests")
        tk      (t/key "clojurewerkz.quartzite.test.execution.trigger4" "tests")
        job     (j/build
                 (j/of-type clojurewerkz.quartzite.test.execution.JobD)
                 (j/with-identity "clojurewerkz.quartzite.test.execution.job4" "tests")
                 (j/using-job-data { "job-key" "job-value" }))
        trigger  (t/build
                  (t/start-now)
                  (t/with-identity "clojurewerkz.quartzite.test.execution.trigger4" "tests")
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-seconds 2))))]
    (sched/schedule job trigger)
    (sched/trigger jk)
    (Thread/sleep 1000)
    (is (= "job-value" (get @value4 "job-key")))))


;;
;; Case 5
;;

(def counter5 (atom 0))

(defrecord JobE []
  org.quartz.Job
  (execute [this ctx]
    (swap! counter5 inc)))

(deftest test-job-data-access
  (is (sched/started?))
  (let [jk      (j/key "clojurewerkz.quartzite.test.execution.job4" "tests")
        tk      (t/key "clojurewerkz.quartzite.test.execution.trigger4" "tests")
        job     (j/build
                 (j/of-type clojurewerkz.quartzite.test.execution.JobD)
                 (j/with-identity "clojurewerkz.quartzite.test.execution.job4" "tests")
                 (j/using-job-data { "job-key" "job-value" }))
        trigger  (t/build
                  (t/start-now)
                  (t/with-identity "clojurewerkz.quartzite.test.execution.trigger4" "tests")
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-seconds 2))))]
    (sched/schedule job trigger)
    (sched/unschedule tk)
    (sched/schedule job trigger)
    (sched/unschedule [tk])
    (Thread/sleep 3000)
    (is (= 0 @counter5))))
