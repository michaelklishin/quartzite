## Changes between Quartzite 1.0.0-rc1 and 1.0.0-rc2

### Better clj-time and Joda Time integration

`clojurewerkz.quartzite.conversions/to-date` was introduced to convert various inputs (for example,
variosu Joda Time date/time objects) to `java.util.Date`. This makes it possible to use Joda Time objects
in the trigger builder DSL:

``` clojure
(t/build
  ;; uses an org.joda.time.DateTime instance
  (t/start-at (-> 2 secs from-now))
  (t/with-schedule (calin/schedule
                     (calin/with-interval-in-seconds 2))))
```



## Changes between Quartzite 1.0.0-beta5 and 1.0.0-rc1

### quartzite.scheduler/maybe-schedule

`clojurewerkz.quartzite.scheduler/maybe-schedule` is a variation of `clojurewerkz.quartzite.scheduler/schedule`
that won't schedule (trigger, job) pair more than once. It thus avoids `org.quartz.ObjectAlreadyExistsException`
exceptions that Quartz will raise when one attempts to schedule the same job on the same trigger/schedule more
than once.




## Changes between Quartzite 1.0.0-beta4 and 1.0.0-beta5

### clj-time Upgraded to 0.4.1

[clj-time](https://github.com/seancorfield/clj-time) dependency has been upgraded to version 0.4.1.



## Changes between Quartzite 1.0.0-beta3 and 1.0.0-beta4

### quartzite.scheduler/all-scheduled?

`clojurewerkz.quartzite.scheduler/all-scheduled?` is a convenient way to check if several keys
(trigger or job) are scheduled.



## Changes between Quartzite 1.0.0-beta2 and 1.0.0-beta3

### JobDataMapConversion protocol now supports JobExecutionContext

`clojurewerkz.quartzite.conversion/from-job-data` now can work with JobExecutionContext instances.



## Changes between Quartzite 1.0.0-beta1 and 1.0.0-beta2

### Utility date/time functions, clj-time dependency

Several date/time functions extracted from various apps that use Quartzite will be
incubating in the `clojurewerkz.quartzite.date-time` namespace for possible inclusion
into clj-time. date/time functions are very relevant to Quartzite and thus depending
on clj-time and providing additional functions makes sense.

### Leiningen 2

Quartzite now uses [Leiningen 2](https://github.com/technomancy/leiningen/wiki/Upgrading).
