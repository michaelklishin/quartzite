(ns clojurewerkz.quartzite.test.querying-test
  (:use clojure.test
        clojurewerkz.quartzite.test.helper)
  (:require [clojurewerkz.quartzite.scheduler :as sched]
            [clojurewerkz.quartzite.jobs      :as j]
            [clojurewerkz.quartzite.triggers  :as t]
            [clojurewerkz.quartzite.schedule.calendar-interval :as calin]))

(wrap-fixtures)

;;
;; Group Names operations
;;

(j/defjob NoOpJob
  [ctx])

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
   (t/with-description "descroption")
   (t/for-job job)
   (t/with-schedule (calin/schedule
                     (calin/with-interval-in-hours 4)))))

(deftest test-job-and-trigger-group-names
  (let [job1 (make-no-op-job "job-in-test-trigger-group-names" "test-job1")
        tk1  (make-no-op-trigger job1 "trigger-1-in-test-trigger-group-names" "test-trigger-1")
        job2 (make-no-op-job "job-in-test-trigger-group-names" "test-job2")
        tk2  (make-no-op-trigger job2 "trigger-2-in-test-trigger-group-names" "test-trigger-2")]

    (sched/schedule job1 tk1)
    (sched/schedule job2 tk2)

    (let [job-group-names (sched/get-job-group-names)]
      (is (= 2 (count job-group-names)))
      (is (= "test-job1" (first job-group-names)))
      (is (= "test-job2" (second job-group-names))))

    (let [trigger-group-names (sched/get-trigger-group-names)]
      (is (= 2 (count trigger-group-names)))
      (is (= "test-trigger-1" (first trigger-group-names)))
      (is (= "test-trigger-2" (second trigger-group-names))))))