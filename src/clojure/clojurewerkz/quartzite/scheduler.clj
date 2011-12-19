(ns clojurewerkz.quartzite.scheduler
  (:import [org.quartz Scheduler JobDetail Trigger]))

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
  (.start ^Scheduler @*scheduler*))

(defn start-delayed
  [^long seconds]
  (.startDelayed ^Scheduler @*scheduler* seconds))

(defn shutdown
  ([]
     (.shutdown ^Scheduler @*scheduler*))
  ([^Boolean wait-for-jobs-to-complete]
     (.shutdown ^Scheduler @*scheduler* wait-for-jobs-to-complete)))

(defn recreate
  []
  (swap! *scheduler* (fn [_] (org.quartz.impl.StdSchedulerFactory/getDefaultScheduler))))


(defn started?
  []
  (.isStarted ^Scheduler @*scheduler*))

(defn standby?
  []
  (.isInStandbyMode ^Scheduler @*scheduler*))

(defn shutdown?
  []
  (.isShutdown ^Scheduler @*scheduler*))


(defn schedule
  [^JobDetail job-detail ^Trigger trigger]
  (.scheduleJob ^Scheduler @*scheduler* job-detail trigger))
