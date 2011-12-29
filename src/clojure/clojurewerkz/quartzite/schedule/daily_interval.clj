(ns clojurewerkz.quartzite.schedule.daily-interval
  (:import [org.quartz DailyTimeIntervalScheduleBuilder DateBuilder TimeOfDay]
           [java.util Set]))

(defn with-interval-in-seconds
  [^DailyTimeIntervalScheduleBuilder dtisb ^long seconds]
  (.withIntervalInSeconds dtisb seconds))

(defn with-interval-in-minutes
  [^DailyTimeIntervalScheduleBuilder dtisb ^long minutes]
  (.withIntervalInMinutes dtisb minutes))

(defn with-interval-in-hours
  [^DailyTimeIntervalScheduleBuilder dtisb ^long hours]
  (.withIntervalInHours dtisb hours))

(defn with-interval-in-days
  [^DailyTimeIntervalScheduleBuilder dtisb ^long days]
  (.withIntervalInHours dtisb (* 24 days)))


(defn with-repeat-count
  [^DailyTimeIntervalScheduleBuilder dtisb ^long l]
  (.withRepeatCount dtisb l))


(defn on-every-day
  [^DailyTimeIntervalScheduleBuilder dtisb]
  (.onEveryDay dtisb))

(defn every-day
  [^DailyTimeIntervalScheduleBuilder dtisb]
  (on-every-day dtisb))



(defn on-days-of-the-week
  [^DailyTimeIntervalScheduleBuilder dtisb ^Set days]
  (.onDaysOfTheWeek dtisb days))

(defn days-of-the-week
  [^DailyTimeIntervalScheduleBuilder dtisb ^Set days]
  (on-days-of-the-week dtisb days))



(defn on-monday-through-friday
  [^DailyTimeIntervalScheduleBuilder dtisb]
  (.onMondayThroughFriday dtisb))

(defn monday-through-friday
  [^DailyTimeIntervalScheduleBuilder dtisb]
  (on-monday-through-friday dtisb))


(defn on-saturday-and-sunday
  [^DailyTimeIntervalScheduleBuilder dtisb]
  (.onSaturdayAndSunday dtisb))

(defn saturday-and-sunday
  [^DailyTimeIntervalScheduleBuilder dtisb]
  (on-saturday-and-sunday dtisb))


(defn time-of-day
  [^long hours ^long minutes ^long seconds]
  (TimeOfDay. hours minutes seconds))

(defn starting-daily-at
  [^DailyTimeIntervalScheduleBuilder dtisb ^TimeOfDay at]
  (.startingDailyAt dtisb at))

(defn ending-daily-at
  [^DailyTimeIntervalScheduleBuilder dtisb ^TimeOfDay at]
  (.endingDailyAt dtisb at))



(defn with-misfire-handling-instruction-ignore-misfires
  [^DailyTimeIntervalScheduleBuilder dtisb]
  (.withMisfireHandlingInstructionIgnoreMisfires dtisb))

(defn ignore-misfires
  [^DailyTimeIntervalScheduleBuilder dtisb]
  (.withMisfireHandlingInstructionIgnoreMisfires dtisb))

(defn with-misfire-handling-instruction-fire-and-proceed
  [^DailyTimeIntervalScheduleBuilder dtisb]
  (.withMisfireHandlingInstructionFireAndProceed dtisb))




(defn finalize
  [^DailyTimeIntervalScheduleBuilder dtisb]
  (.build dtisb))

(defmacro schedule
  [& body]
  `(let [dtisb# (DailyTimeIntervalScheduleBuilder/dailyTimeIntervalSchedule)]
     (-> dtisb# ~@body)))
