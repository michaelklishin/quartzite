(ns clojurewerkz.quartzite.triggers
  (:refer-clojure :exclude [key])
  (:import [org.quartz Trigger TriggerBuilder TriggerKey]
           [org.quartz.utils Key]))
