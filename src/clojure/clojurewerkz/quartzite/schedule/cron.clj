(ns clojurewerkz.quartzite.schedule.cron
  (:import [org.quartz CronScheduleBuilder]
           [org.quartz.spi MutableTrigger]))


(defn cron-schedule
  [^String expression]
  (CronScheduleBuilder/cronSchedule expression))

(defn daily-at-hour-and-minute
  [^long hour ^long minute]
  (CronScheduleBuilder/dailyAtHourAndMinute hour minute))


(defn finalize
  [^CronScheduleBuilder ssb]
  (.build ssb))

(defmacro schedule
  [& body]
  `(let [s# ~(first body)]
     (-> s# ~@(rest body))))
