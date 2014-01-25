(ns clojurewerkz.quartzite.test.helper
  (:require [clojurewerkz.quartzite.scheduler :as sched]
            [clojure.test :refer :all]))

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