(ns clojurewerkz.quartzite.schedule.calendar-interval
  (:import [org.quartz CalendarIntervalScheduleBuilder DateBuilder]))


(defn with-interval-in-seconds
  [^CalendarIntervalScheduleBuilder cisb ^long seconds]
  (.withIntervalInSeconds cisb seconds))

(defn with-interval-in-minutes
  [^CalendarIntervalScheduleBuilder cisb ^long minutes]
  (.withIntervalInMinutes cisb minutes))

(defn with-interval-in-hours
  [^CalendarIntervalScheduleBuilder cisb ^long hours]
  (.withIntervalInHours cisb hours))

(defn with-interval-in-days
  [^CalendarIntervalScheduleBuilder cisb ^long days]
  (.withIntervalInDays cisb days))

(defn with-interval-in-weeks
  [^CalendarIntervalScheduleBuilder cisb ^long weeks]
  (.withIntervalInWeeks cisb weeks))

(defn with-interval-in-months
  [^CalendarIntervalScheduleBuilder cisb ^long months]
  (.withIntervalInMonths cisb months))

(defn with-interval-in-years
  [^CalendarIntervalScheduleBuilder cisb ^long years]
  (.withIntervalInYears cisb years))




(defn with-misfire-handling-instruction-ignore-misfires
  [^CalendarIntervalScheduleBuilder cisb]
  (.withMisfireHandlingInstructionIgnoreMisfires cisb))

(defn ignore-misfires
  [^CalendarIntervalScheduleBuilder cisb]
  (.withMisfireHandlingInstructionIgnoreMisfires cisb))

(defn with-misfire-handling-instruction-do-nothing
  [^CalendarIntervalScheduleBuilder cisb]
  (.withMisfireHandlingInstructionDoNothing cisb))


(defn with-misfire-handling-instruction-fire-and-proceed
  [^CalendarIntervalScheduleBuilder cisb]
  (.withMisfireHandlingInstructionFireAndProceed cisb))



(defn finalize
  [^CalendarIntervalScheduleBuilder cisb]
  (.build cisb))

(defmacro schedule
  [& body]
  `(let [cisb# (CalendarIntervalScheduleBuilder/calendarIntervalSchedule)]
     (-> cisb# ~@body)))
