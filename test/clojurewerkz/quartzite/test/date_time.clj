(ns clojurewerkz.quartzite.test.date-time
  (:use [clojurewerkz.quartzite.date-time]
        [clojure.test]
        [clj-time.core :only [now interval minus plus years months weeks days hours minutes from-now after? within? ago date-time]])
  (:import [java.util Date]
           [org.joda.time DateTime]))


(deftest test-last-day-of-the-month
  (let [d1 (date-time 2012 1 31)
        d2 (date-time 2012 2 29)
        d3 (date-time 2012 3 31)
        d4 (date-time 2012 4 30)
        d5 (date-time 2012 5 31)
        d6 (date-time 2012 6 30)
        d7 (date-time 2013 2 28)
        d8 (date-time 2016 2 29)]
    (is (= d1 (last-day-of-the-month 2012 1)))
    (is (= d1 (last-day-of-the-month (date-time 2012 1 13))))
    (is (= d2 (last-day-of-the-month 2012 2)))
    (is (= d2 (last-day-of-the-month (date-time 2012 2 8))))
    (is (= d3 (last-day-of-the-month 2012 3)))
    (is (= d4 (last-day-of-the-month 2012 4)))
    (is (= d5 (last-day-of-the-month 2012 5)))
    (is (= d6 (last-day-of-the-month 2012 6)))
    (is (= d7 (last-day-of-the-month 2013 2)))
    (is (= d8 (last-day-of-the-month 2016 2)))))

(deftest test-number-of-days-in-the-month
  (is (= 31 (number-of-days-in-the-month 2012 1)))
  (is (= 31 (number-of-days-in-the-month (date-time 2012 1 3))))
  (is (= 29 (number-of-days-in-the-month 2012 2)))
  (is (= 28 (number-of-days-in-the-month 2013 2)))
  (is (= 30 (number-of-days-in-the-month 2012 11)))
  (is (= 31 (number-of-days-in-the-month 2012 3)))
  (is (= 30 (number-of-days-in-the-month 2012 4)))
  (is (= 31 (number-of-days-in-the-month 2013 12)))
  (is (= 28 (number-of-days-in-the-month 2013 2)))
  (is (= 29 (number-of-days-in-the-month 2016 2))))


(deftest test-first-day-of-the-month
  (let [d1 (date-time 2012 1 1)
        d2 (date-time 2012 2 1)
        d3 (date-time 2012 3 1)
        d4 (date-time 2012 4 1)
        d5 (date-time 2012 5 1)
        d6 (date-time 2012 6 1)
        d7 (date-time 2013 2 1)
        d8 (date-time 2016 2 1)]
    (is (= d1 (first-day-of-the-month 2012 1)))
    (is (= d1 (first-day-of-the-month (date-time 2012 1 24))))
    (is (= d2 (first-day-of-the-month 2012 2)))
    (is (= d2 (first-day-of-the-month (date-time 2012 2 24))))
    (is (= d3 (first-day-of-the-month 2012 3)))
    (is (= d4 (first-day-of-the-month 2012 4)))
    (is (= d5 (first-day-of-the-month 2012 5)))
    (is (= d6 (first-day-of-the-month 2012 6)))
    (is (= d7 (first-day-of-the-month 2013 2)))
    (is (= d8 (first-day-of-the-month 2016 2)))))


(deftest test-today-at
  (let [^DateTime n  (now)
        y  (.getYear n)
        m  (.getMonthOfYear n)
        d  (.getDayOfMonth n)
        d1 (date-time y m d 13 0)]
    (is (= d1 (today-at 13 0)))
    (is (= d1 (today-at 13 0 0)))))


(deftest test-periodic-sequence
  (let [d0 (date-time 2012 3 3 20 0)
        d1 (date-time 2012 3 3 21 0)
        d2 (date-time 2012 3 3 22 0)
        d3 (date-time 2012 3 3 23 0)
        d4 (date-time 2012 3 4 0 0)
        d5 (date-time 2012 3 4 1 0)
        d6 (date-time 2012 3 4 2 0)
        uds (periodic-seq d0 (hours 1))]
    (are [a b] (= a b)
         d0 (first uds)
         d1 (second uds)
         d2 (nth uds 2)
         d3 (nth uds 3)
         d4 (nth uds 4)
         d5 (nth uds 5)
         d6 (nth uds 6))))
