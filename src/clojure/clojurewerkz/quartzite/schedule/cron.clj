(ns clojurewerkz.quartzite.schedule.cron
  (:import [org.quartz CronScheduleBuilder]
           [org.quartz.spi MutableTrigger]
           [java.util TimeZone]))


(defn cron-schedule
  [^String expression]
  (CronScheduleBuilder/cronSchedule expression))

(defn daily-at-hour-and-minute
  [^long hour ^long minute]
  (CronScheduleBuilder/dailyAtHourAndMinute hour minute))

(defn weekly-on-day-and-hour-and-minute
  [^long day-of-week ^long hour ^long minute]
  (CronScheduleBuilder/weeklyOnDayAndHourAndMinute day-of-week hour minute))

(defn monthly-on-day-and-hour-and-minute
  [^long day-of-month ^long hour ^long minute]
  (CronScheduleBuilder/monthlyOnDayAndHourAndMinute day-of-month hour minute))


(defn in-time-zone
  [^CronScheduleBuilder ssb ^TimeZone tz]
  (.inTimeZone ssb tz))


(defn with-misfire-handling-instruction-ignore-misfires
  [^CronScheduleBuilder ssb]
  (.withMisfireHandlingInstructionIgnoreMisfires ssb))

(defn ignore-misfires
  [^CronScheduleBuilder ssb]
  (.withMisfireHandlingInstructionIgnoreMisfires ssb))

(defn with-misfire-handling-instruction-do-nothing
  [^CronScheduleBuilder ssb]
  (.withMisfireHandlingInstructionDoNothing ssb))

(defn with-misfire-handling-instruction-fire-and-proceed
  [^CronScheduleBuilder ssb]
  (.withMisfireHandlingInstructionFireAndProceed ssb))

(defn fire-and-proceed
  [^CronScheduleBuilder ssb]
  (.withMisfireHandlingInstructionFireAndProceed ssb))


(defn finalize
  [^CronScheduleBuilder ssb]
  (.build ssb))

(defmacro schedule
  [& body]
  `(let [s# ~(first body)]
     (-> s# ~@(rest body))))
