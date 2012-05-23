(ns clojurewerkz.quartzite.test.matchers-test
  (:use clojure.test
        clojurewerkz.quartzite.matchers)
  (:require [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.triggers :as t]))


(deftest test-group-matcher-factory-functions
  (testing "group-equals"
    (let [g (group-equals "abc")]
      (is (.isMatch g (j/key "job1" "abc")))
      (is (not (.isMatch g (j/key "job1" "group2"))))
      (is (not (.isMatch g (j/key "job1"))))
      (is (.isMatch g (j/key "job99" "abc")))))
  (testing "group-starts-with"
    (let [g (group-starts-with "abc")]
      (is (.isMatch g (j/key "job1" "abcdef")))
      (is (.isMatch g (j/key "job99" "abcdef")))
      (is (not (.isMatch g (j/key "job1" "group2"))))
      (is (not (.isMatch g (j/key "job1"))))))
  (testing "group-ends-with"
    (let [g (group-ends-with "def")]
      (is (.isMatch g (j/key "job1" "abcdef")))
      (is (.isMatch g (j/key "job99" "abcdef")))
      (is (not (.isMatch g (j/key "job1" "group2"))))
      (is (not (.isMatch g (j/key "job1"))))))
  (testing "group-contains"
    (let [g (group-contains "bc")]
      (are [key] (is (match? g key))
        (j/key "job1" "abcdef")
        (j/key "job1" "bcolumbia")
        (j/key "job1" "abc"))
      (are [key] (is (not (match? g key)))
        (j/key "job1" "def")
        (j/key "job888")
        (j/key "job1" "generation.invoices")))))
