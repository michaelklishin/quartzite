(ns clojurewerkz.quartzite.schedule.simple
  (:import [org.quartz SimpleScheduleBuilder DateBuilder]))

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

(defn with-interval-in-days
  [^SimpleScheduleBuilder ssb ^long days]
  (.withIntervalInHours ssb (* 24 days)))


(defn with-repeat-count
  [^SimpleScheduleBuilder ssb ^long l]
  (.withRepeatCount ssb l))

(defn repeat-forever
  [^SimpleScheduleBuilder ssb]
  (.repeatForever ssb))



(defn with-misfire-handling-instruction-ignore-misfires
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionIgnoreMisfires ssb))

(defn ignore-misfires
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionIgnoreMisfires ssb))

(defn with-misfire-handling-instruction-fire-now
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionFireNow ssb))


(defn with-misfire-handling-instruction-next-with-existing-count
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionNextWithExistingCount ssb))

(defn next-with-existing-count
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionNextWithExistingCount ssb))


(defn with-misfire-handling-instruction-next-with-remaining-count
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionNextWithRemainingCount ssb))

(defn next-with-remaining-count
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionNextWithRemainingCount ssb))


(defn with-misfire-handling-instruction-now-with-existing-count
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionNowWithExistingCount ssb))

(defn now-with-existing-count
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionNowWithExistingCount ssb))


(defn with-misfire-handling-instruction-now-with-remaining-count
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionNowWithRemainingCount ssb))

(defn now-with-remaining-count
  [^SimpleScheduleBuilder ssb]
  (.withMisfireHandlingInstructionNowWithRemainingCount ssb))



(defn finalize
  [^SimpleScheduleBuilder ssb]
  (.build ssb))

(defmacro schedule
  [& body]
  `(let [ssb# (SimpleScheduleBuilder/simpleSchedule)]
     ;; unlike job and trigger builder DSLs, we do not finalize
     ;; result here because this is how Quartz works. MK.
     (-> ssb# ~@body)))
