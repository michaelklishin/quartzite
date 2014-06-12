(ns clojurewerkz.quartzite.test.stateful-test
  (:require [clojurewerkz.quartzite.scheduler :as sched]
            [clojurewerkz.quartzite.jobs      :as j]
            [clojurewerkz.quartzite.triggers  :as t]
            [clojurewerkz.quartzite.matchers  :as m]
            [clojurewerkz.quartzite.stateful  :as stateful]
            [clojurewerkz.quartzite.schedule.simple :as s]
            [clojurewerkz.quartzite.schedule.calendar-interval :as calin]
            [clojure.test :refer :all]
            [clojurewerkz.quartzite.conversion :refer :all]
            [clojurewerkz.quartzite.test.helper :refer :all]
            [clj-time.core :refer [now from-now]])
  (:import java.util.concurrent.CountDownLatch
           org.quartz.impl.matchers.GroupMatcher))

(println (str "Using Clojure version " *clojure-version*))

(wrap-fixtures)

;;
;; Case 1. Stateful jobs are not executed concurrently
;;

(def latch1 (CountDownLatch. 2))

; job takes longer than interval
(stateful/def-stateful-job JobA
  [ctx]
  (Thread/sleep 1000)
  (.countDown ^CountDownLatch latch1))

(deftest test-stateful-job
  (is (sched/started?))
  (let [jk      (j/key "clojurewerkz.quartzite.test.execution.job1"     "tests")
        tk      (t/key "clojurewerkz.quartzite.test.execution.trigger1" "tests")
        job     (j/build
                 (j/of-type JobA)
                 (j/with-identity "clojurewerkz.quartzite.test.execution.job1" "tests"))
        trigger  (t/build
                  (t/start-now)
                  (t/with-identity "clojurewerkz.quartzite.test.execution.trigger1" "tests")
                  (t/with-description "just a trigger")
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 2)
                                    (s/with-interval-in-milliseconds 200))))
        start (System/currentTimeMillis)]
    (sched/schedule job trigger)
    (is (sched/all-scheduled? jk tk))
    (is (not (empty? (sched/get-triggers [tk]))))
    (is (not (empty? (sched/get-jobs [jk]))))
    (let [t (sched/get-trigger tk)
          m (from-trigger t)]
      (is t)
      (is (:key m))
      (is (:description m))
      (is (:start-time m))
      (is (:next-fire-time m)))
    (is (sched/get-job jk))
    (is (not (empty? (sched/get-job-keys (m/group-equals "tests")))))
    (.await ^CountDownLatch latch1)
    (let [time-to-run (- (System/currentTimeMillis) start)]
      (is (>= time-to-run 2000)))))

;;
;; Case 2. State is actually stored.
;;

(def latch2 (CountDownLatch. 10))
(def atom1 (atom 0))

(stateful/def-stateful-job JobB
  [ctx]
  (let [current (stateful/get-job-detail-data ctx)
        state (get current "the-state" 0)
        new-state (inc state)]
    (stateful/replace! ctx (assoc current "the-state" new-state))
    (reset! atom1 new-state)
    (.countDown ^CountDownLatch latch2)))

(deftest test-stateful-job-state
  (is (sched/started?))
  (let [jk      (j/key "clojurewerkz.quartzite.test.execution.job2"     "tests")
        tk      (t/key "clojurewerkz.quartzite.test.execution.trigger2" "tests")
        job     (j/build
                 (j/of-type JobB)
                 (j/with-identity "clojurewerkz.quartzite.test.execution.job2" "tests"))
        trigger  (t/build
                  (t/start-now)
                  (t/with-identity "clojurewerkz.quartzite.test.execution.trigger2" "tests")
                  (t/with-description "just a trigger")
                  (t/with-schedule (s/schedule
                                    (s/with-repeat-count 10)
                                    (s/with-interval-in-milliseconds 10))))
        start (System/currentTimeMillis)]
    (sched/schedule job trigger)
    (is (not (empty? (sched/get-job-keys (m/group-equals "tests")))))
    (.await ^CountDownLatch latch2)
    (is (= @atom1 10))))
