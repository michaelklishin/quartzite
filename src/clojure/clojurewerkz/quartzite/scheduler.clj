(ns clojurewerkz.quartzite.scheduler
  (:import [org.quartz Scheduler JobDetail JobKey Trigger TriggerKey SchedulerListener ListenerManager]
           [java.util List]))

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

(defn standby
  []
  (.standby ^Scheduler @*scheduler*))

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

(defn unschedule-job
  [^TriggerKey key]
  (.unscheduleJob ^Scheduler @*scheduler* key))

(defn delete-job
  [^JobKey key]
  (.deleteJob ^Scheduler @*scheduler* key))

(defn unschedule-jobs
  [^List keys]
  (.unscheduleJobs ^Scheduler @*scheduler* keys))

(defn delete-jobs
  [^List keys]
  (.deleteJobs ^Scheduler @*scheduler* keys))



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


(defn clear!
  []
  (.clear ^Scheduler @*scheduler*))


(defn add-scheduler-listener
  [^SchedulerListener listener]
  (.addSchedulerListener ^ListenerManager (.getListenerManager ^Scheduler @*scheduler*) listener))
