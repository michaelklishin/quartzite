(ns ^{:doc "Contains factory functions that produce group matchers. Group matchers are used to retrieve
            triggers and jobs from the scheduler en masse."
      :author "Michael S. Klishin"} clojurewerkz.quartzite.matchers
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
