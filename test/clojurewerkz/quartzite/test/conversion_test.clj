(ns clojurewerkz.quartzite.test.stateful-test
  (:require [clojure.test :refer :all]
            [clojurewerkz.quartzite.conversion :refer :all]))

(defrecord Abc [a b c])

(deftest test-job-data-conversion
  (are [input expected] (= (from-job-data (to-job-data input))
                           (or expected input))

       ; Simple case..
       {"string" "hello"
        "number" 123
        "boolean" true
        "character" \newline
        "keyword" :keyword
        "symbol" 'symbol}
       nil ; nil means that expected is same as input

        ; Check that first-level keys are stringified
       {123 123
        true true
        \newline \newline
        :keyword :keyword
        'symbol 'symbol}
       {"123" 123
        "true" true
        "\n" \newline
        "keyword" :keyword
        "symbol" 'symbol}

        ; Check that keys of nested maps not stringified.
        {"nested" {:a :a}}
        nil

        ; Check that nested record not converted to map.
        {"record" (->Abc :a :b :c)}
        nil))

