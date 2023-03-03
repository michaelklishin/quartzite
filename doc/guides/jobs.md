# Quartzite, a powerful Clojure scheduling library: defining jobs

This guide covers:

 * How to define periodically executed jobs
 * Using job keys to identify jobs
 * How to submit jobs for execution
 * How to pause and resume jobs
 * How to remove jobs from the scheduler
 * Using job contexts and data maps

Although many examples won't make much sense without demonstrating the
use of *triggers*, this guide will focus more on jobs and related
scheduler operations, while the dedicated [guide on triggers](/articles/triggers.html) will focus more on using various
kinds of schedules.

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on Github](https://github.com/clojurewerkz/quartzite.docs).



## What version of Quartzite does this guide cover?

This guide covers Quartzite `2.0.x` (including beta releases).


## Overview

Quartz separates operations that are performed (called jobs) from
rules according to which they are performed (triggers). Triggers
combine execution schedule (for example, "every 4 hours" or "at noon
every Friday"), date/time when execution starts and ends, operation
priority and a few other things.

A job may have multiple triggers associated with them (although it is
common to have just one). Both jobs and triggers have identifiers
(called "keys") that you use to manage them, for example, unschedule,
pause or resume.


## Defining Quartz jobs with a Clojure DSL

Jobs in Quartz are objects that implement `org.quartz.Job`, a single
function interface. One way to define a job is to define a record that
implements that interface:

``` clojure
(defrecord NoOpJob []
  org.quartz.Job
  (execute [this ctx]
    ;; intentional no-op
    ))
```

This does not look very Clojuric, does it. Because jobs are single
method interfaces, it makes perfect sense to use Clojure functions as
jobs.  Unfortunately, due to certain Quartz implementation details and
the way Clojure loads generated classes, many approaches to using
functions do not work.

Quartzite provides a macro that makes defining jobs more concise but
avoids limitations of using proxies and reification. The macro is
`clojurewerkz.quartzite.jobs/defjob`:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :refer [defjob]]))

(defn -main
  [& m]
  (let [s (-> (qs/initialize) qs/start)]
    ))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))
```

These examples demonstrate defining the "executable part" of a job. As
we've mentioned before, to make it possible to manage jobs and
triggers, Quartz requires them to have identities. To define a
complete job that can be submitted for scheduling, you use a DSL in
the `clojurewerkz.quartzite.jobs` namespace:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j]))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (let [s   (-> (qs/initialize) qs/start)
        job (j/build
              (j/of-type NoOpJob)
              (j/with-identity (j/key "jobs.noop.1")))]))
```

`clojurewerkz.quartzite.jobs/key` function can be used with any other function that accepts job keys.


## Using keys to identify jobs

In order to pause or completely remove a job from the scheduler, there
needs to be a way to identify it. Job identifiers are called "keys". A
key consists of a string identifier and an (optional) group. To
instantiate keys, use `clojurewerkz.quartzite.jobs/key` function:

``` clojure
(ns quartzite.docs.examples
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j])
  (:import java.util.UUID))

;; this key uses the default group
;; and features account id in the identifier (to guarantee uniqueness)
(j/key "invoices.102")

;; this key uses a custom group
;; and uses a random UUID as the identifier
(j/key (str (UUID/randomUUID)) "aggregators")
```

When group is not specified, the default group is used. It is common
to use groups to "namespace" executed jobs, for example, to separate
operations that perform periodic data aggregation from those that
generate invoices.



## Jobs contexts and job data maps

Many jobs will need some kind of context to carry out their
duties. For example, an aggregation job associated with a particular
account will need that account's id in order to load it from a data
store. A job that involves retrieving Web pages may need the URL to
use and so on.

When Quartz executes a job, it will pass it a **job context** object
that among other things, includes arbitrary data that the job needs.

Lets take a look at a simplest Quartzite job possible:

``` clojure
(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))
```

It takes the aforementioned **job context** which is an instance of
[JobExecutionContext](http://quartz-scheduler.org/api/2.1.5/org/quartz/JobExecutionContext.html).
The job execution context you can retrieve **job data map**. Quartzite
offers a function that returns **job data map** as an immutable
Clojure map:

``` clojure
(require '[clojurewerkz.quartzite.conversion :as qc])

(defjob NoOpJob
  [ctx]
  (let [m (qc/from-job-data ctx)]
    (comment "Do something with the job data map")))
```

Note that **Quartzite will always stringify keys** when converting
Clojure maps to the internal job context representation. This is
because Quartz and some of its extensions assume that keys are
strings.

Job data is optional and can be added via the job definition DSL:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j]))

(defjob BillingJob
  [ctx]
  (comment "Implement me"))

(defn -main
  [& m]
  (let [s   (-> (qs/initialize) qs/start)
        job (j/build
             (j/of-type BillingJob)
             (j/using-job-data {"account-id" "356dbd7b08bbd5c449505e5378538b5d06e68eb1" "rollover?" true})
             (j/with-identity (j/key "jobs.billing.356dbd7b08bbd5c449505e5378538b5d06e68eb1")))]
    ))
```


## Scheduling jobs for execution

`clojurewerkz.quartzite.scheduler/schedule` submits a job and a
trigger associated with it for execution:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.simple :refer [schedule with-repeat-count with-interval-in-milliseconds]]))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (let [s   (-> (qs/initialize) qs/start)
        job (j/build
              (j/of-type NoOpJob)
              (j/with-identity (j/key "jobs.noop.1")))
        trigger (t/build
                  (t/with-identity (t/key "triggers.1"))
                  (t/start-now)
                  (t/with-schedule (schedule
                                     (with-repeat-count 10)
                                     (with-interval-in-milliseconds 200))))]
  (qs/schedule s job trigger)))
```

If the scheduler is started, execution begins according to the start
moment of the submitted trigger. In the example above, the trigger
will fire 10 times every 200 ms and expire after that. Expired
triggers do not execute associated jobs.


## Unscheduling jobs

To unschedule an operation, you unschedule its trigger (or several of
them) using `clojurewerkz.quartzite.scheduler/unschedule-job` and
`clojurewerkz.quartzite.scheduler/unschedule-jobs` functions:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.simple :refer [schedule with-repeat-count with-interval-in-milliseconds]]))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (let [s   (-> (qs/initialize) qs/start)
        job (j/build
              (j/of-type NoOpJob)
              (j/with-identity (j/key "jobs.noop.1")))
        tk      (t/key "triggers.1")
        trigger (t/build
                  (t/with-identity tk)
                  (t/start-now)
                  (t/with-schedule (schedule
                                     (with-repeat-count 10)
                                     (with-interval-in-milliseconds 200))))]
  ;; submit for execution
  (qs/schedule s job trigger)
  ;; and immediately unschedule the trigger
  (qs/unschedule-job s tk)))
```

Please note that `unschedule-job` takes a *trigger*
key. `clojurewerkz.quartzite.scheduler/unschedule-jobs` works the same
way but takes a collection of keys.

There are other functions that delete jbos and all their triggers,
pause execution of triggers and so on. They are covered in the
[Scheduling, unscheduling and pausing jobs](/articles/unscheduling_and_pausing.html) guide.


## Pausing and resuming jobs

Jobs can be paused and resumed. Pausing a job pauses all of its
triggers so the job won't be executed but is not removed from the
scheduler. To pause a single job, use
`clojurewerkz.quartzite.scheduler/pause-job` and pass it the job's
key:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (let [s   (-> (qs/initialize) qs/start)
        jk  (j/key "jobs.noop.1")]
    ;; pause a single job
    (qs/pause-job s jk)))
```

`clojurewerkz.quartzite.scheduler/pause-jobs` will pause one or
several groups of jobs by pausing their triggers. What groups are
paused is determined by the *group matcher*, instantiated via Java
interop:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]])
  (:import org.quartz.impl.matchers.GroupMatcher))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (let [s       (-> (qs/initialize) qs/start)
        matcher (GroupMatcher/groupEquals "billing")]
    ;; pause a group of jobs
    (qs/pause-jobs s matcher)))
```

In addition to the exact matcher, there are several other matchers available:

``` clojure
(import org.quartz.impl.matchers.GroupMatcher)

(GroupMatcher/groupStartsWith "billing")
(GroupMatcher/groupEndsWith "delayed")
(GroupMatcher/groupContains "organizations")
```

Resuming a job makes all its triggers fire
again. `clojurewerkz.quartzite.scheduler/resume-job` is the function
that does that for a single job:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (let [s   (-> (qs/initialize) qs/start)
        jk  (j/key "jobs.noop.1")]
    ;; resumes a single job
    (qs/resume-job s jk)))
```

`clojurewerkz.quartzite.scheduler/resume-jobs` resumes one or more job
groups using the already covered group matchers:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j])
  (:use [clojurewerkz.quartzite.jobs :only [defjob]])
  (:import org.quartz.impl.matchers.GroupMatcher))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (let [s (-> (qs/initialize) qs/start)]
    ;; resumes a group of jobs
    (qs/resume-jobs s (GroupMatcher/groupEquals "billing"))))
```

Finally, `clojurewerkz.quartzite.scheduler/pause-all!` and
`clojurewerkz.quartzite.scheduler/resume-all!` are functions that
pause and resume *the entire scheduler*. Use them carefully. Both take
no arguments.

### Jobs and Trigger Misfires

A misfire occurs if a persistent trigger "misses" its firing time
because of being paused, the scheduler being shutdown, or because
there are no available threads in Quartz's thread pool for executing
the job. When a job is resumed, if any of its triggers have missed one
or more fire-times, the trigger's misfire instruction will apply.

Misfires are covered in the [triggers guide](/articles/triggers.html).


## Completely removing jobs from the scheduler

It is possible to completely remove a job from the scheduler. Doing so
will also remove *all the associated triggers*. The job will never be
executed again (unless it is
re-scheduled). `clojurewerkz.quartzite.scheduler/delete-job` deletes a
single job by job key:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (let [s  (-> (qs/initialize) qs/start)
        jk (j/key "jobs.noop.1")]
    ;; deletes a single job
    (qs/delete-job s jk)))
```

while `clojurewerkz.quartzite.scheduler/delete-jobs` removes multiple jobs and takes a collection of keys:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (let [s    (-> (qs/initialize) qs/start)
        jk1  (j/key "jobs.noop.1")
        jk2  (j/key "jobs.noop.2")]
    ;; deletes several jobs
    (qs/delete-jobs s [jk1 jk2])))
```



## What to read next

The documentation is organized as a number of guides, covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Defining triggers and schedules](/articles/triggers.html)
 * [Using durable stores for scheduler state](/articles/durable_quartz_stores.html)
 * [Using Quartz plugins](/articles/quartz_plugins.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on
Twitter or the [Quartzite mailing list](https://groups.google.com/forum/#!forum/clojure-quartz).

Let us know what was unclear or what has not been covered. Maybe you
do not like the guide style or grammar or discover spelling
mistakes. Reader feedback is key to making the documentation better.
