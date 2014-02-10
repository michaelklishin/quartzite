;; Copyright (c) 2011-2014 Michael S. Klishin, Alex Petrov, and the ClojureWerkz Team
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

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
