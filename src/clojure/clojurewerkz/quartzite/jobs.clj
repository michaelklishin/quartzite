(ns clojurewerkz.quartzite.jobs
  (:refer-clojure :exclude [key])
  (:import [org.quartz Job JobDetail JobBuilder JobKey JobExecutionContext JobDataMap]
           [org.quartz.utils Key]
           [clojure.lang IPersistentMap])
  (:use    [clojurewerkz.quartzite.conversion :only [to-job-data]]))


;;
;; Implementation
;;

;; ...



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



(defn ^JobBuilder with-identity
  ([^JobBuilder jb s]
     (if (instance? JobKey s)
       (.withIdentity jb ^JobKey s)
       (.withIdentity jb (key s))))
  ([^JobBuilder jb s group]
     (.withIdentity jb (key s group))))

(defn ^JobBuilder with-description
  [^JobBuilder jb ^String s]
  (.withDescription jb s))

(defn ^JobBuilder store-durably
  [^JobBuilder jb]
  (.storeDurably jb))

(defn ^JobBuilder request-recovery
  [^JobBuilder jb]
  (.requestRecovery jb))

(defn ^JobBuilder of-type
  [^JobBuilder jb clazz]
  (.ofType jb clazz))

(defn ^JobBuilder using-job-data
  [^JobBuilder tb m]
  (.usingJobData tb (to-job-data m)))

(defn ^JobDetail finalize
  [^JobBuilder jb]
  (.build jb))


(defmacro ^JobDetail build
  [& body]
  `(let [jb# (JobBuilder/newJob)]
     (finalize (-> jb# ~@body))))

;; This macro is necessary because clojure.core/proxy and clojure.core/reify
;; do not work for this specific use case with Quartz. See https://groups.google.com/forum/#!topic/clojure/WIIcvsYLzh0
;; for the discussion. MK.
(defmacro defjob
  [jtype args & body]
  `(defrecord ~jtype []
       org.quartz.Job
     (execute [this ~@args]
       ~@body)))