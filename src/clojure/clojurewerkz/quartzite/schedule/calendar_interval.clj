;; Copyright (c) 2011-2014 Michael S. Klishin, Alex Petrov, and the ClojureWerkz Team
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

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
