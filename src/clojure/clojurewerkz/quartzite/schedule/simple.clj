(ns clojurewerkz.quartzite.schedule.simple
  (:import [org.quartz SimpleScheduleBuilder]))

(defn with-interval-in-milliseconds
  [^SimpleScheduleBuilder ssb ^long milliseconds]
  (.withIntervalInMilliseconds ssb milliseconds))

(defn with-interval-in-seconds
  [^SimpleScheduleBuilder ssb ^long seconds]
  (.withIntervalInSeconds ssb seconds))

(defn with-interval-in-minutes
  [^SimpleScheduleBuilder ssb ^long minutes]
  (.withIntervalInMinutes ssb minutes))

(defn with-interval-in-hours
  [^SimpleScheduleBuilder ssb ^long hours]
  (.withIntervalInHours ssb hours))


(defn with-repeat-count
  [^SimpleScheduleBuilder ssb ^long l]
  (.withRepeatCount ssb l))

(defn repeat-forever
  [^SimpleScheduleBuilder ssb]
  (.repeatForever ssb))





(defn finalize
  [^SimpleScheduleBuilder ssb]
  (.build ssb))

(defmacro schedule
  [& body]
  `(let [ssb# (SimpleScheduleBuilder/simpleSchedule)]
     ;; unlike job and trigger builder DSLs, we do not finalize
     ;; result here because this is how Quartz works. MK.
     (-> ssb# ~@body)))
