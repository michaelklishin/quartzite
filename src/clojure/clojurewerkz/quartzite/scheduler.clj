(ns clojurewerkz.quartzite.scheduler
  (:import [org.quartz.Scheduler]))

;;
;; Implementation
;;

(def ^:dynamic *scheduler* (atom (org.quartz.impl.StdSchedulerFactory/getDefaultScheduler)))



;;
;; API
;;


(defmacro with-scheduler
  [sched & body]
  `(binding [*scheduler* ~sched]
     (do ~@body)))


(defn start
  []
  (.start @*scheduler*))

(defn start-delayed
  [^long seconds]
  (.startDelayed @*scheduler* seconds))

(defn shutdown
  ([]
     (.shutdown @*scheduler*))
  ([^Boolean wait-for-jobs-to-complete]
     (.shutdown @*scheduler* wait-for-jobs-to-complete)))


(defn started?
  []
  (.isStarted @*scheduler*))

(defn standby?
  []
  (.isInStandbyMode @*scheduler*))

(defn shutdown?
  []
  (.isShutdown @*scheduler*))
