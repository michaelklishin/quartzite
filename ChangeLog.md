## Changes between Quartzite 1.0.0 and 1.0.1

No changes yet.


## Changes between Quartzite 1.0.0-rc6 and 1.0.0

### Renamed Functions

`clojurewerkz.quartzite.scheduler/unschedule-job` was renamed to `clojurewerkz.quartzite.scheduler/delete-trigger`,
`clojurewerkz.quartzite.scheduler/unschedule-jobs` was renamed to `clojurewerkz.quartzite.scheduler/delete-triggers`.

The old functions are deprecated but not removed.


### Clojure 1.4 By Default

Quartzite now depends on `org.clojure/clojure` version `1.4.0`. It is still compatible with Clojure 1.3 and if your `project.clj` depends
on 1.3, it will be used, but 1.4 is the default now.

We encourage all users to upgrade to 1.4, it is a drop-in replacement for the majority of projects out there.



### Stateful Jobs Support

`clojurewerkz.quartzite.stateful/def-stateful-job` is a new macro for defining stateful jobs. It has exactly
the same API signature as as `clojurewerkz.quartzite.jobs/defjob`.

Contributed by dlebrero.


## Changes between Quartzite 1.0.0-rc5 and 1.0.0-rc6

Added `clojurewerkz.quartzite.scheduler/get-job-group-names` and `clojurewerkz.quartzite.scheduler/get-trigger-group-names`, that
could be used to retrieve lists of jobs and triggers.



## Changes between Quartzite 1.0.0-rc4 and 1.0.0-rc5

### clojurewerkz.quartzite.scheduler/get-triggers, /get-jobs, /get-matching-triggers, /get-matching-jobs

`clojurewerkz.quartzite.scheduler/get-trigger` and `clojurewerkz.quartzite.scheduler/get-job` return trigger and job instances
for the supplied collections of keys.

`clojurewerkz.quartzite.scheduler/get-matching-triggers` function combines
`clojurewerkz.quartzite.scheduler/get-trigger-keys` with `clojurewerkz.quartzite.scheduler/get-triggers`.

`clojurewerkz.quartzite.scheduler/get-matching-jobs` works the same way for jobs.



## Changes between Quartzite 1.0.0-rc3 and 1.0.0-rc4

### clojurewerkz.quartzite.scheduler/get-trigger and /get-job

`clojurewerkz.quartzite.scheduler/get-trigger` and `clojurewerkz.quartzite.scheduler/get-job` return trigger and job instances
for the supplied key. `clojurewerkz.quartzite.conversion/from-trigger` and `clojurewerkz.quartzite.conversion/from-job-detail` can be
used to produce Clojure maps from those instances (for example, for easy serialization to JSON or any other format)



## Changes between Quartzite 1.0.0-rc2 and 1.0.0-rc3

### clojurewerkz.quartzite.matchers

`clojurewerkz.quartzite.matchers` namespaces provides factory functions that instatiate various group matchers.
Group matchers are used to retrieve trigger and job keys using functions in the `clojurewerkz.quartzite.scheduler`
namespace:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as s]
            [clojurewerkz.quartzite.matchers :as m]))

;; initialize the scheduler, add some triggers and jobs
;; ...

;; retrieve a set of trigger keys in the group "billing"
(s/get-trigger-keys (m/group-equals "billing"))

;; retrieve a set of job keys in all groups that start with "emails"
(s/get-job-keys (m/group-starts-with "emails"))
```


### Quartz updagraded to 2.1.5

Quartz Scheduler was upgraded to version 2.1.5.


### clj-time upgraded to 0.4.2

[clj-time](https://github.com/seancorfield/clj-time) dependency has been upgraded to version 0.4.2, uses
[Joda Time 2.1](https://github.com/JodaOrg/joda-time/blob/master/RELEASE-NOTES.txt).



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
