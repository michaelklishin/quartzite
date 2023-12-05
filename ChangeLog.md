## Changes Between Quartzite 2.1.0 and 2.2.0

### Upgrades Quartz to 2.3.2

To address a number of CVEs.

GitHub issue: [#45](https://github.com/michaelklishin/quartzite/pull/45)

Contributed by @kevin-ewing.


## Changes Between Quartzite 2.0.0 and 2.1.0

### `get-job` and `get-trigger` Fixes

`clojurewerkz.quartzite.scheduler/get-job` and `/get-trigger` now use correct
order of arguments for key group and key.

Contributed by songsd and Ryan Fowler.

### `get-matching-jobs` Fixes

`clojurewerkz.quartzite.scheduler/get-matching-jobs` now returns jobs (as advertised).

Contributed by Przemysław Wojnowski.

### Stringified Job Keys

When job maps are converted to `JobDataDetails` used by Quartz, all keys are
now stringified and nested maps are ignored.

Contributed by Nikita Beloglazov.

### clj-time Upgrade

The project now depends on clj-time `0.14.0`.

### Test Suite Improvements

Contributed by Nikita Beloglazov, Przemysław Wojnowski, Ryan Fowler.


## Changes Between Quartzite 1.3.0 and 2.0.0

### Scheduler as Explicit Arguments

Following our [commitment to make our projects rely less on dynamic vars](http://blog.clojurewerkz.org/blog/2014/04/26/major-breaking-public-api-changes-coming-in-our-projects/) we
have changed `clojurewerkz.quartzite.scheduler` function to take an explicit
scheduler argument.

So, in 1.3 releases, you would do:

``` clojure
(require '[clojurewerkz.quartzite.scheduler :as qs])

(qs/initialize)
(qs/start)
(qs/schedule job-detail trigger)
(qs/shutdown)
```

and in 2.0 release, the same code would look like so:

``` clojure
(require '[clojurewerkz.quartzite.scheduler :as qs])

(let [s (-> (qs/initialize) qs/start)]
  (qs/schedule s job-detail trigger)
  (qs/shutdown s))
```

### quartzite.date-time is Gone

`clojurewerkz.quartzite.date-time` was removed as all of the functions in that
namespace have been contributed to `clj-time` a while ago, and some provide more
correct or extensible functionality.

Simply use `clj-time` counterparts when upgrading.

### Clojure 1.4 and 1.5 are No Longer Supported

The library no longer supports Clojure 1.4 and 1.5 as of this version.



## Changes Between Quartzite 1.2.0 and 1.3.0

### Clojure 1.6 by Default

The library now depends on Clojure 1.6.

### clj-time upgraded to 0.8.0

[clj-time](https://github.com/clj-time/clj-time) dependency has been upgraded to version 0.8.0.


## Changes between Quartzite 1.1.0 and 1.2.0

### Clojure 1.3 is No Longer Supported

Quartzite requires Clojure 1.4+ as of this version.


### clj-time upgraded to 0.6.0

[clj-time](https://github.com/clj-time/clj-time) dependency has been upgraded to version 0.6.0.

### New Functions

* `clojurewerkz.quartzite.scheduler/get-currently-executing-jobs` returns a set of currently executing jobs.
* `clojurewerkz.quartzite.scheduler/currently-executing-job?` returns true there is a running job for a given key.


## Changes between Quartzite 1.0.0 and 1.1.0

`1.1.x` has one **breaking API change** in how it converts job context
maps to `JobDetailContext` in Quartz.

### clj-time upgraded to 0.5.0

[clj-time](https://github.com/seancorfield/clj-time) dependency has been upgraded to version 0.5.0, uses
[Joda Time 2.2](https://github.com/JodaOrg/joda-time/blob/master/RELEASE-NOTES.txt).

`clojurewerkz.quartzite.date-time` functions were pushed upstream to `clj-time`, find them in
`clj-time.core` and `clj-time.periodic`.

`clojurewerkz.quartzite.date-time` will be **removed** from Quartzite in the release following
`1.1.0`.

### Stringified Keys in JobDetailContext

Quartzite will now stringify all keys in Clojure maps converted to job detail context
instances. This is due to the fact that some Quartz internals implicitly assume JobDetailContext
keys are always strings. 


### Clojure 1.5 By Default

Quartzite now depends on `org.clojure/clojure` version `1.5.0`. It is
still compatible with Clojure 1.3+ and if your `project.clj` depends on
a different version, it will be used, but 1.5 is the default now.

We encourage all users to upgrade to 1.5, it is a drop-in replacement
for the majority of projects out there.


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
