;; Copyright (c) 2011-2014 Michael S. Klishin, Alex Petrov, and the ClojureWerkz Team
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns clojurewerkz.quartzite.matchers
  "Contains factory functions that produce group matchers.
   Group matchers are used to retrieve triggers and jobs from the scheduler en masse."
  (:import org.quartz.impl.matchers.GroupMatcher
           org.quartz.utils.Key))


;;
;; API
;;

(defn match?
  "Returns true if given group matcher matches the given key"
  [^GroupMatcher matcher ^Key key]
  (.isMatch matcher key))

(defn ^org.quartz.impl.matchers.GroupMatcher
  group-equals
  "Returns a group matcher that matches keys in the given group"
  [^String s]
  (GroupMatcher/groupEquals s))

(defn ^org.quartz.impl.matchers.GroupMatcher
  group-starts-with
  "Returns a group matcher that matches keys in all groups that start with the given prefix"
  [^String s]
  (GroupMatcher/groupStartsWith s))

(defn ^org.quartz.impl.matchers.GroupMatcher
  group-ends-with
  "Returns a group matcher that matches keys in all groups that end with the given suffix"
  [^String s]
  (GroupMatcher/groupEndsWith s))

(defn ^org.quartz.impl.matchers.GroupMatcher
  group-contains
  "Returns a group matcher that matches keys in all groups that contain the given substring"
  [^String s]
  (GroupMatcher/groupContains s))
