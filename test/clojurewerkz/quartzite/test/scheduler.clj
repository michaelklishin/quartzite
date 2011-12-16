(ns clojurewerkz.quartzite.test.scheduler
  (:require [clojurewerkz.quartzite.scheduler :as qs])
  (:use [clojure.test]))

(deftest test-starting-reactor
  (is (not (qs/started?)))
  (is (qs/standby?))
  (qs/start)
  (is (qs/started?))
  (is (not (qs/standby?)))
  (qs/start) ;; no exceptions
  (is (not (qs/shutdown?)))
  (qs/shutdown)
  (is (qs/shutdown?)))


