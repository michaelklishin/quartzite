(ns clojurewerkz.quartzite.date-time
  (:use [clj-time.core :only [date-time now interval minus plus years months weeks days hours minutes from-now after? within? ago]])
  (:import [org.joda.time DateTime DateMidnight MutableDateTime]))


;;
;; API
;;

(defn ^DateTime last-day-of-the-month
  ([^DateTime dt]
     (last-day-of-the-month (.getYear dt) (.getMonthOfYear dt)))
  ([^long year ^long month]
     (-> ^DateTime (date-time year month) .dayOfMonth .withMaximumValue)))

(defn number-of-days-in-the-month
  (^long [^DateTime dt]
         (number-of-days-in-the-month (.getYear dt) (.getMonthOfYear dt)))
  (^long [^long year ^long month]
         (-> ^DateTime (last-day-of-the-month year month) .getDayOfMonth)))

(defn ^DateTime first-day-of-the-month
  ([^DateTime dt]
     (first-day-of-the-month (.getYear dt) (.getMonthOfYear dt)))
  ([^long year ^long month]
     (-> ^DateTime (date-time year month) .dayOfMonth .withMinimumValue)))


(defn ^DateMidnight date-midnight
  [^long year ^long month ^long day]
  (DateMidnight. year month day))

(defn ^DateTime today-at
  ([^long hours ^long minutes ^long seconds ^long millis]
     (let [^MutableDateTime mdt (-> ^DateTime (now) .toMutableDateTime)]
       (.toDateTime (doto mdt
                      (.setHourOfDay      hours)
                      (.setMinuteOfHour   minutes)
                      (.setSecondOfMinute seconds)
                      (.setMillisOfSecond millis)))))
  ([^long hours ^long minutes ^long seconds]
     (today-at hours minutes seconds 0))
  ([^long hours ^long minutes]
     (today-at hours minutes 0)))
