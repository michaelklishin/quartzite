(ns clojurewerkz.quartzite.scheduler
  (:import [org.quartz Scheduler JobDetail JobKey Trigger TriggerKey]))

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

(defn unschedule
  [^TriggerKey tk]
  (.unscheduleJob ^Scheduler @*scheduler* tk))

(defprotocol KeyBasedSchedulingPredicates
  (^Boolean scheduled? [key] "Checks if entity with given key already exists within the scheduler"))

(extend-protocol KeyBasedSchedulingPredicates
  JobKey
  (scheduled? [^JobKey key]
    (.checkExists ^Scheduler @*scheduler* key))

  TriggerKey
  (scheduled? [^TriggerKey key]
    (.checkExists ^Scheduler @*scheduler* key)))




(defn trigger
  [^JobKey jk]
  (.triggerJob ^Scheduler @*scheduler* jk))
