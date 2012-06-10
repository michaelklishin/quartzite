(ns clojurewerkz.quartzite.test.helper
  (:use clojure.test)
  (:require [clojurewerkz.quartzite.scheduler :as sched]))

(defn wrap-fixtures
  []
  (use-fixtures :each
              (fn [f]
                (sched/initialize)
                (sched/start)
                (f)
                (sched/shutdown))
              (fn [f]
                (sched/clear!)
                (f)
                (sched/clear!))))