(ns clojurewerkz.quartzite.test.querying-test
  (:require [clojurewerkz.quartzite.scheduler :as sched]
            [clojurewerkz.quartzite.jobs      :as j]
            [clojurewerkz.quartzite.triggers  :as t]
            [clojurewerkz.quartzite.schedule.calendar-interval :as calin]
            [clojure.test :refer :all]))

;;
;; Group Names operations
;;

(j/defjob NoOpJob
  [ctx])

(j/defjob LongRunningJob
  [ctx]
  (Thread/sleep 5000))

(defn make-no-op-job
  [name job-group]
  (j/build
   (j/of-type NoOpJob)
   (j/with-identity name job-group)))

(defn make-no-op-trigger
  [job name job-group]
  (t/build
   (t/start-now)
   (t/with-identity name job-group)
   (t/with-description "description")
   (t/for-job job)
   (t/with-schedule (calin/schedule
                     (calin/with-interval-in-hours 4)))))

(defn make-long-running-job
  [id]
  (j/build
   (j/of-type LongRunningJob)
   (j/with-identity (j/key id))))

(defn make-long-running-job-trigger
  [job id]
  (t/build
   (t/start-now)
   (t/with-identity (t/key id))
   (t/with-description "description")
   (t/for-job job)))

(deftest test-job-and-trigger-group-names
  (let [s    (-> (sched/initialize) sched/start)
        job1 (make-no-op-job "job-in-test-trigger-group-names" "test-job1")
        tk1  (make-no-op-trigger job1 "trigger-1-in-test-trigger-group-names" "test-trigger-1")
        job2 (make-no-op-job "job-in-test-trigger-group-names" "test-job2")
        tk2  (make-no-op-trigger job2 "trigger-2-in-test-trigger-group-names" "test-trigger-2")]

    (sched/schedule s job1 tk1)
    (sched/schedule s job2 tk2)

    (let [job-group-names (sched/get-job-group-names s)]
      (is (= 2 (count job-group-names)))
      (is (= "test-job1" (first job-group-names)))
      (is (= "test-job2" (second job-group-names))))

    (let [trigger-group-names (sched/get-trigger-group-names s)]
      (is (= 2 (count trigger-group-names)))
      (is (= "test-trigger-1" (first trigger-group-names)))
      (is (= "test-trigger-2" (second trigger-group-names))))
    (sched/shutdown s)))


(deftest test-get-triggers-of-job
  (let [s    (-> (sched/initialize) sched/start)
        job-id "job-in-test-get-triggers-of-job"
        job-group "test-job1"
        job1 (make-no-op-job job-id job-group)
        trig-id "trigger-1-in-test-get-triggers-of-job"
        trig-group "test-trigger-1"
        tk1  (make-no-op-trigger job1 trig-id trig-group)]

    (sched/schedule s job1 tk1)

    (let [jk (j/key job-id job-group)
          triggers (sched/get-triggers-of-job s jk)]
      (is (= 1 (count triggers)))
      (is (.equals (first triggers) tk1)))))

(deftest test-get-executing-jobs
  (let [s    (-> (sched/initialize) sched/start)
        job-id "long-job-1"
        job1 (make-long-running-job job-id)
        trig-id "long-trigger-1"
        tk1 (make-long-running-job-trigger job1 trig-id)]

    (sched/schedule s job1 tk1)

    (Thread/sleep 1000) ; wait for the job to be scheduled and start running before querying

    (is (= 1 (count (sched/get-currently-executing-jobs s "long-job-1"))))
    (is (sched/currently-executing-job? s "long-job-1"))
    (sched/shutdown s)))
