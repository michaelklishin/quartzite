(ns clojurewerkz.quartzite.jobs
  (:refer-clojure :exclude [key])
  (:import [org.quartz Job JobDetail JobBuilder JobKey JobExecutionContext]
           [org.quartz.utils Key]))


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
     (.withIdentity jb (key s)))
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


(defn job-for
  [f]
  (proxy [org.quartz.Job]
      []
    (execute [ctx]
      (f ctx))))

(defn ^JobBuilder execute
  [^JobBuilder jb f]
  (let [prx (job-for f)]
    (.ofType jb (class prx))))

(defn ^JobDetail finalize
  [^JobBuilder jb]
  (.build jb))


(defmacro ^JobDetail build
  [& body]
  `(let [jb# (JobBuilder/newJob)]
     (finalize (-> jb# ~@body))))
