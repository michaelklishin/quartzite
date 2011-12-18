(ns clojurewerkz.quartzite.triggers
  (:refer-clojure :exclude [key])
  (:import [org.quartz Trigger TriggerBuilder TriggerKey]
           [org.quartz.utils Key]))


;;
;; Implementation
;;

;; ...



;;
;; API
;;

(defn ^TriggerKey key
  ([]
     (TriggerKey. (Key/createUniqueName nil)))
  ([named]
     (TriggerKey. (name named)))
  ([named, group]
     (TriggerKey. (name named) (name group))))



(defn ^TriggerBuilder with-identity
  ([^TriggerBuilder tb s]
     (.withIdentity tb (key s))
     tb)
  ([^TriggerBuilder tb s group]
     (.withIdentity tb (key s group))
     tb))

(defn ^TriggerBuilder with-description
  [^TriggerBuilder tb ^String s]
  (.withDescription tb s)
  tb)


(defn ^TriggerBuilder with-priority
  [^TriggerBuilder tb ^long l]
  (.withPriority tb l)
  tb)

(defn ^TriggerBuilder modified-by-calendar
  [^TriggerBuilder tb ^String s]
  (.modifiedByCalendar tb s)
  tb)


(defn ^Trigger finalize
  [^TriggerBuilder tb]
  (.build tb))


(defmacro ^Trigger build
  [& body]
  `(let [tb# (TriggerBuilder/newTrigger)]
     (finalize (-> tb# ~@body))))
