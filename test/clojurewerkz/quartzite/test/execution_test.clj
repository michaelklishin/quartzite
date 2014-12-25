(ns clojurewerkz.quartzite.test.execution-test
  (:require [clojurewerkz.quartzite.scheduler :as sched]
            [clojurewerkz.quartzite.jobs      :as j]
            [clojurewerkz.quartzite.triggers  :as t]
            [clojurewerkz.quartzite.matchers  :as m]
            [clojurewerkz.quartzite.schedule.simple :as s]
            [clojurewerkz.quartzite.schedule.calendar-interval :as calin]
            [clojure.test :refer :all]
            [clojurewerkz.quartzite.conversion :refer :all]
            [clj-time.core :refer [now seconds from-now]])
  (:import java.util.concurrent.CountDownLatch
           org.quartz.impl.matchers.GroupMatcher))

;;
;; Case 1
;;

(def latch1 (CountDownLatch. 10))

(defrecord JobA []
  org.quartz.Job
  (execute [this ctx]
    (.countDown ^CountDownLatch latch1)))

(deftest ^:focus test-basic-periodic-execution-with-a-job-defined-using-defrecord
  (let [s       (-> (sched/initialize) sched/start)
        job     (j/build
                 (j/of-type JobA)
                 (j/with-identity "clojurewerkz.quartzite.test.execution-test.job1" "tests"))
        trigger  (t/build
                  (t/start-now)
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-milliseconds 200))))]
    (is (sched/started? s))
    (sched/schedule s job trigger)
    (let [j (sched/get-job s (j/key "clojurewerkz.quartzite.test.execution-test.job1" "tests"))
          m (from-job-detail j)]
      (is j)
      (is (:key m))
      (is (nil? (:description m)))
      (is (:job-data m)))
    (try
      (.await ^CountDownLatch latch1)
      (finally
        (sched/shutdown s)))))



;;
;; Case 2
;;

(def counter2 (atom 0))

(j/defjob JobB
  [ctx]
  (swap! counter2 inc))

(deftest test-unscheduling-of-a-job-defined-using-defjob
  (let [s       (-> (sched/initialize) sched/start)
        jk      (j/key "clojurewerkz.quartzite.test.execution-test.job2"     "tests")
        tk      (t/key "clojurewerkz.quartzite.test.execution-test.trigger2" "tests")
        job     (j/build
                 (j/of-type JobB)
                 (j/with-identity "clojurewerkz.quartzite.test.execution-test.job2" "tests"))
        trigger  (t/build
                  (t/start-now)
                  (t/with-identity "clojurewerkz.quartzite.test.execution-test.trigger2" "tests")
                  (t/with-description "just a trigger")
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-milliseconds 400))))]
    (sched/schedule s job trigger)
    (is (sched/all-scheduled? s jk tk))
    (is (not (empty? (sched/get-triggers s [tk]))))
    (is (not (empty? (sched/get-jobs s [jk]))))
    (let [t (sched/get-trigger s tk)
          m (from-trigger t)]
      (is t)
      (is (:key m))
      (is (:description m))
      (is (:start-time m))
      (is (:next-fire-time m)))
    (is (sched/get-job s jk))
    (is (nil? (sched/get-job s (j/key "ab88fsyd7f" "k28s8d77s"))))
    (is (nil? (sched/get-trigger s (t/key "ab88fsyd7f"))))
    (is (not (empty? (sched/get-trigger-keys s (m/group-equals "tests")))))
    (is (not (empty? (sched/get-matching-triggers s (m/group-equals "tests")))))
    (is (not (empty? (sched/get-job-keys s (m/group-equals "tests")))))
    (is (not (empty? (sched/get-matching-jobs s (m/group-equals "tests")))))
    (Thread/sleep 2000)
    (sched/delete-trigger s tk)
    (is (not (sched/all-scheduled? s tk jk)))
    (Thread/sleep 2000)
    (is (< @counter2 7))
    (sched/shutdown s)))



;;
;; Case 3
;;

(def counter3 (atom 0))

(j/defjob JobC
  [ctx]
  (swap! counter3 inc))

(deftest test-manual-triggering-of-a-job-defined-using-defjob
  (let [s       (-> (sched/initialize) sched/start)
        jk      (j/key "clojurewerkz.quartzite.test.execution-test.job3" "tests")
        tk      (t/key "clojurewerkz.quartzite.test.execution-test.trigger3" "tests")
        job     (j/build
                 (j/of-type JobC)
                 (j/with-identity "clojurewerkz.quartzite.test.execution-test.job3" "tests"))
        trigger  (t/build
                  (t/start-now)
                  (t/with-identity "clojurewerkz.quartzite.test.execution-test.trigger3" "tests")
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-seconds 2))))]
    (sched/schedule s job trigger)
    (sched/trigger s jk)
    (Thread/sleep 500)
    (is (= 2 @counter3))
    (sched/shutdown s)))


;;
;; Case 4
;;

(def value4 (atom nil))

(j/defjob JobD
  [ctx]
  (swap! value4 (fn [_]
                  (from-job-data (.getMergedJobDataMap ctx)))))

(deftest test-job-data-access
  (let [s       (-> (sched/initialize) sched/start)
        jk      (j/key "clojurewerkz.quartzite.test.execution-test.job4" "tests")
        tk      (t/key "clojurewerkz.quartzite.test.execution-test.trigger4" "tests")
        job     (j/build
                 (j/of-type JobD)
                 (j/with-identity "clojurewerkz.quartzite.test.execution-test.job4" "tests")
                 (j/using-job-data { "job-key" "job-value" }))
        trigger  (t/build
                  (t/start-now)
                  (t/with-identity "clojurewerkz.quartzite.test.execution-test.trigger4" "tests")
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-seconds 2))))]
    (sched/schedule s job trigger)
    (sched/trigger s jk)
    (Thread/sleep 1000)
    (is (= "job-value" (get @value4 "job-key")))
    (sched/shutdown s)))


;;
;; Case 5
;;

(def counter5 (atom 0))

(j/defjob JobE
  [ctx]
  (let [i (get (from-job-data ctx) "job-key")]
    (swap! counter5 + i)))

(deftest test-job-pausing-resuming-and-unscheduling
  (let [s       (-> (sched/initialize) sched/start)
        jk      (j/key "clojurewerkz.quartzite.test.execution-test.job5" "tests.jobs.unscheduling")
        tk      (t/key "clojurewerkz.quartzite.test.execution-test.trigger5" "tests.jobs.unscheduling")
        job     (j/build
                 (j/of-type JobE)
                 (j/with-identity "clojurewerkz.quartzite.test.execution-test.job5" "tests.triggers.unscheduling")
                 (j/using-job-data { "job-key" 2 }))
        trigger  (t/build
                  (t/start-now)
                  (t/with-identity "clojurewerkz.quartzite.test.execution-test.trigger5" "tests.triggers.unscheduling")
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-seconds 1))))]
    (sched/schedule s job trigger)
    (sched/pause-job s jk)
    (sched/resume-job s jk)
    (sched/pause-jobs s (GroupMatcher/groupEquals "tests.jobs.unscheduling"))
    (sched/resume-jobs s (GroupMatcher/groupEquals "tests.jobs.unscheduling"))
    (sched/pause-trigger s tk)
    (sched/resume-trigger s tk)
    (sched/pause-triggers s (GroupMatcher/groupEquals "tests.triggers.unscheduling"))
    (sched/resume-triggers s (GroupMatcher/groupEquals "tests.triggers.unscheduling"))
    (sched/pause-all! s)
    (sched/resume-all! s)
    (sched/delete-trigger s tk)
    (Thread/sleep 300)
    (sched/delete-triggers s [tk])
    (sched/delete-job s jk)
    (sched/delete-jobs s [jk])
    (Thread/sleep 3000)
    ;; with start-now policty some executions
    ;; manages to get through. In part this test is supposed
    ;; to demonstrate it as much as test unscheduling/pausing functions. MK.
    (is (< @counter5 15))
    (sched/shutdown s)))


;;
;; Case 6
;;

(def latch6 (CountDownLatch. 3))

(j/defjob JobF
  [ctx]
  (.countDown ^CountDownLatch latch6))

(deftest test-basic-periodic-execution-with-calendar-interval-schedule
  (let [s       (-> (sched/initialize) sched/start)
        job     (j/build
                 (j/of-type JobF)
                 (j/with-identity "clojurewerkz.quartzite.test.execution-test.job6" "tests"))
        trigger  (t/build
                  (t/start-now)
                  (t/with-schedule (calin/schedule
                                    (calin/with-interval-in-seconds 2))))]
    (sched/schedule s job trigger)
    (try
      (.await ^CountDownLatch latch6)
      (finally
        (sched/shutdown s)))))



;;
;; Case 7
;;

(def counter7 (atom 0))

(j/defjob JobG
  [ctx]
  (swap! counter7 inc))

(deftest test-double-scheduling
  (let [s       (-> (sched/initialize) sched/start)
        job     (j/build
                 (j/of-type JobG)
                 (j/with-identity "clojurewerkz.quartzite.test.execution-test.job7" "tests"))
        trigger  (t/build
                  (t/start-at (-> 2 seconds from-now))
                  (t/with-schedule (calin/schedule
                                    (calin/with-interval-in-seconds 2))))]
    (is (sched/schedule s job trigger))
    ;; schedule will raise an exception
    (is (thrown?
         org.quartz.ObjectAlreadyExistsException
         (sched/schedule s job trigger)))
    ;; but maybe-schedule will not
    (is (not (sched/maybe-schedule s job trigger)))
    (Thread/sleep 7000)
    (is (= 3 @counter7))
    (sched/shutdown s)))
