(ns clojurewerkz.quartzite.jobs
  (:refer-clojure :exclude [key])
  (:import [org.quartz Job JobDetail JobBuilder JobKey]
           [org.quartz.utils Key]))


;;
;; Implementation
;;

(def ^{ :dynamic true } *job*)



;;
;; API
;;

(defn ^JobKey key
  ([]
     (JobKey. (Key/createUniqueName nil)))
  ([named]
     (JobKey. (name named)))
  ([named, group]
     (JobKey. (name named) (name group))))



(defn with-identity
  ([^JobBuilder jb s]
     (.withIdentity jb (key s))
     jb)
  ([^JobBuilder jb s group]
     (.withIdentity jb (key s group))
     jb))

(defn with-description
  [^JobBuilder jb ^String s]
  (.withDescription jb s)
  jb)


(defn ^JobDetail finalize
  [^JobBuilder jb]
  (.build jb))


(defmacro ^JobDetail build
  [& body]
  `(let [jb# (JobBuilder/newJob)]
     (finalize (-> jb# ~@body))))
