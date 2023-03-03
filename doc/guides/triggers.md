# Quartzite, a powerful Clojure scheduling library: defining triggers and schedules

This guide covers:

 * How to define triggers
 * Using trigger keys to identify triggers
 * How to use various types of schedules
 * How to pass context information to executed jobs

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on Github](https://github.com/clojurewerkz/quartzite.docs).



## What version of Quartzite does this guide cover?

This guide covers Quartzite `2.0.x` (including beta releases).


## What Quartz triggers

Quartz separates operations that are performed (called jobs) from
rules according to which they are performed (triggers). Triggers
combine execution schedule (for example, "every 4 hours" or "at noon
every Friday"), date/time when execution starts and ends, operation
priority and a few other things.

A job may have multiple triggers associated with them (although it is
common to have just one). Both jobs and triggers have identifiers
(called "keys") that you use to manage them, for example, unschedule,
pause or resume.


## Defining Quartz triggers with a Clojure DSL

Triggers are defined using a DSL from the
`clojurewerkz.quartzite.triggers` namespace. Lets define a trigger
that fires (executes its associated job) 10 times every 200 ms,
starting immediately:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]))

(defn -main
  [& m]
  (let [s      (-> (qs/initialize) qs/start)
       trigger (t/build
                  (t/with-identity (t/key "triggers.1"))
                  (t/start-now)
                  (t/with-schedule (schedule
                                     (with-repeat-count 10)
                                     (with-interval-in-milliseconds 200))))]))
```

So how triggers are defined is quite similar to how jobs are defined. Quartz provides several types of execution schedules
and Quartzite supports all of them in the DSL. A couple more schedule types will be demonstrated later in this guide.

Simple periodic schedule demonstrated here is used when you need to perform a task N times with a fixed time interval.


## Using keys to identify triggers

In order to pause or completely remove a job from the scheduler, there needs to be a way to identify it. Job identifiers are called "keys". A key consists
of a string identifier and an (optional) group. To instantiate keys, use `clojurewerkz.quartzite.triggers/key` function:

``` clojure
(ns quartzite.docs.examples
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t])
  (:import java.util.UUID))

;; this key uses the default group
;; and features account id in the identifier (to guarantee uniqueness)
(t/key "invoices.102")

;; this key uses a custom group
;; and uses a random UUID as the identifier
(t/key (str (UUID/randomUUID)) "aggregators")
```

When group is not specified, the default group is used. It is common to use groups to "namespace" triggers, for example, to separate triggers
that schdule periodic data aggregation from those that generate invoices.


## Using Cron expression schedules

One of the schedule types that Quartz supports is the Cron expression schedule. It lets you define the schedule as a single expression used
by [cron(8)](http://linux.die.net/man/8/cron). This form is concise but may also seem cryptic. As such, Cron schedules
are most commonly used when migrating legacy applications or by developers who are deeply familiar with Cron.

To define a trigger that will use a Cron expression schedule, you combine DSLs from `clojurewerkz.quartzite.triggers` and `clojurewerkz.quartzite.schedule.cron` namespaces:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
	    [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.cron :refer [schedule cron-schedule]]))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (let [s   (-> (qs/initialize) qs/start)
       job  (j/build
              (j/of-type NoOpJob)
              (j/with-identity (j/key "jobs.noop.1")))
        trigger (t/build
                  (t/with-identity (t/key "triggers.1"))
                  (t/start-now)
                  (t/with-schedule (schedule
                                     (cron-schedule "0 0 15 ? * 5"))))]
  (qs/schedule s job trigger)))
```

To learn more about Cron expressions, consult [crontab(5)](http://linux.die.net/man/5/crontab).


## Using calendar interval schedules

Third type of trigger schdule is the calendar interval schedule. It fires at fixed intervals: minutes, hours, days, weeks, months or years.
In this example, we use intervals of 1 day:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
	    [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.calendar-interval :refer [schedule with-interval-in-days]]))

(defjob NoOpJob
  [ctx]
  (comment "Does nothing"))

(defn -main
  [& m]
  (qs/initialize)
  (qs/start)
  (let [job (j/build
              (j/of-type NoOpJob)
              (j/with-identity (j/key "jobs.noop.1")))
        trigger (t/build
                  (t/with-identity (t/key "triggers.1"))
                  (t/start-now)
                  (t/with-schedule (schedule
                                     (with-interval-in-days 1))))]
  (qs/schedule s job trigger)))
```


## Using daily interval schedules

Daily interval schedules make it easy to define schedules like

 * "Monday through Friday from 9 to 17"
 * "Every weekend at 3 in the morning"
 * "Every Friday at noon"
 * "Every day at 13:45"
 * "Every hour on Thursdays but not later than 15:00, up to 400 times total"

without having to deal with Cron expressions:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
	    [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.daily-interval :refer [schedule monday-through-friday starting-daily-at time-of-day ending-daily-at with-interval-in-minutes]]))

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
                                     (with-interval-in-minutes 2)
                                     (monday-through-friday)
                                     (starting-daily-at (time-of-day 9 00 00))
                                     (ending-daily-at (time-of-day 17 00 00)))))]
  (qs/schedule s job trigger)))
```




## Pausing and resuming triggers

Triggers can be paused and resumed. Paused triggers will not fire and associated jobs will not be executed.
To pause a trigger, use `clojurewerkz.quartzite.scheduler/pause-trigger` and pass it the trigger's key:

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
    (qs/schedule s job trigger)
    (qs/pause-trigger s tk)))
```

`clojurewerkz.quartzite.scheduler/pause-triggers` will pause one or several groups of triggers. What groups
are paused is determined by the *group matcher*, instantiated via Java interop:

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
        tk  (t/key "jobs.noop.1")]
    ;; pauses a single trigger
    (qs/pause-trigger s tk)))
```

In addition to the exact matcher, there are several other matchers available:

``` clojure
(import org.quartz.impl.matchers.GroupMatcher)

(GroupMatcher/groupStartsWith "billing")
(GroupMatcher/groupEndsWith "delayed")
(GroupMatcher/groupContains "organizations")
```

Resuming a trigger makes it fire
again. `clojurewerkz.quartzite.scheduler/resume-trigger` is the
function that does that for a single trigger:

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
        tk  (t/key "jobs.noop.1")]
    ;; resumes a single trigger
    (qs/resume-trigger s tk)))
```

`clojurewerkz.quartzite.scheduler/resume-triggers` resumes one or more
trigger groups using the already covered group matchers:

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
  (qs/initialize)
  (qs/start)
  (let [s (-> (qs/initialize) qs/start)]
    ;; resumes a group of triggers
    (qs/resume-triggers s (GroupMatcher/groupEquals "billing"))))
```

Finally, `clojurewerkz.quartzite.scheduler/pause-all!` and
`clojurewerkz.quartzite.scheduler/resume-all!` are functions that
pause and resume *the entire scheduler*. Use them carefully. Both take
no arguments.


## Completely removing trigger from the scheduler

It is possible to completely remove a trigger from the
scheduler. Doing so will also remove *all the associated
triggers*. The trigger will never be executed again (unless it is
re-scheduled). `clojurewerkz.quartzite.scheduler/delete-trigger`
deletes a single trigger by trigger key:

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
        tk (t/key "jobs.noop.1")]
    ;; deletes a single trigger
    (qs/delete-trigger s tk)))
```

while `clojurewerkz.quartzite.scheduler/delete-triggers` removes
multiple triggers and takes a collection of keys:

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
  ;; deletes a group of triggers
  (let [s   (-> (qs/initialize) qs/start)
        tk1 (t/key "jobs.noop.1")
        tk2 (t/key "jobs.noop.2")]
    (qs/delete-triggers s [tk1 tk2])))
```



## Misfires

A misfire occurs if a persistent trigger "misses" its firing time
because of being paused, the scheduler being shutdown, or because
there are no available threads in Quartz's thread pool for executing
the job.

The different trigger types have different misfire instructions
available to them. By default they use a 'smart policy' instruction
which has dynamic behavior based on trigger type and
configuration. When the scheduler starts, it searches for any
persistent triggers that have misfired, and it then updates each of
them based on their individually configured misfire instructions.

Not all misfire instructions make sense for all triggers. Different
triggers use different schedules and thus misfire instructions
available to them depend on the schedule.

### Misfire Instructions Available to All Triggers

#### The "Ignore Misfires" Instruction

The "ignore misfires" instruction instructs the scheduler should
simply fire the trigger as soon as it can, as many times as necessary
to catch back up with the schedule. This means the trigger may fire
multiple times in rapid succession.

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.simple :as s])

(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/ignore-misfires)
                   (s/with-repeat-count 10)
                   (s/with-interval-in-seconds 2))))
```



### Misfire Instructions Available to Triggers That Use the Simple Schedule

#### The "Fire Now" Instruction

Instructs the scheduler to fire the trigger immediately upon
misfire. Should only be used for one-shot (non-repeating) timers.

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.simple :as s])

(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-next-with-existing-count)
                   (s/with-repeat-count 10)
                   (s/with-interval-in-seconds 2))))
```

#### The "Next With Existing Repeat Count" Instruction

Instructs the scheduler to re-schedule the trigger to the next
scheduled time after "now" with the repeat counter unchanged. If the
end time of the trigger has arrived, the trigger will be marked as
completed (will not fire again).

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.simple :as s])

(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-next-with-existing-count)
                   (s/with-repeat-count 10)
                   (s/with-interval-in-seconds 2))))
```

#### The "Next With Remaining Repeat Count" Instruction

Instructs the scheduler to re-schedule the trigger to the next
scheduled time after "now" and changes the repeat counter to what it
would be, if it had not missed any fires.

If the end time of the trigger has arrived, the trigger will be marked
as completed (will not fire again).

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.simple :as s])

(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-next-with-remaining-count)
                   (s/with-repeat-count 10)
                   (s/with-interval-in-seconds 2))))
```

#### The "Now With Existing Repeat Count" Instruction

Instructs the scheduler to fire the trigger "now" and not change the
repeat count. If "now" is after the trigger's end time, the trigger
will not fire.

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.simple :as s])

(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-now-with-existing-count)
                   (s/with-repeat-count 10)
                   (s/with-interval-in-seconds 2))))
```

#### The "Now With Remaining Repeat Count" Instruction

Instructs the scheduler to fire the trigger "now" and changes the
repeat counter to what it would be, if it had not missed any fires. If
"now" is after the trigger's end time, the trigger will not fire.

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.simple :as s])

(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-now-with-remaining-count)
                   (s/with-repeat-count 10)
                   (s/with-interval-in-seconds 2))))
```



### Misfire Instructions Available to Triggers That Use the Daily Time Interval Schedule

#### The "Fire Once Now" Instruction

Instructs the scheduler to fire the trigger immediately upon misfire.

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.daily-interval :as s])
 
(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-fire-and-proceed)
                   (s/on-monday-through-friday)
                   (s/with-interval-in-minutes 10))))
```

#### The "Do Nothing" Instruction

Instructs the scheduler to do nothing.

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.daily-interval :as s])
 
(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-do-nothing)
                   (s/on-monday-through-friday)
                   (s/with-interval-in-minutes 10))))
```



### Misfire Instructions Available to Triggers That Use the Calendar Interval Schedule

#### The "Fire Once Now" Instruction

Instructs the scheduler to fire the trigger immediately upon misfire.

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.calendar-interval :as s])

(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-fire-and-proceed)
                   (s/with-interval-in-hours 12))))
```

#### The "Do Nothing" Instruction

Instructs the scheduler to do nothing.

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.calendar-interval :as s])

(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-do-nothing)
                   (s/with-interval-in-hours 12))))
```



### Misfire Instructions Available to Triggers That Use the Cron Schedule

#### The "Fire Once Now" Instruction

Instructs the scheduler to fire the trigger immediately upon misfire.

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.cron :as s])

(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-fire-and-proceed)
                   (s/in-time-zone (java.util.TimeZone/getTimeZone "Europe/Moscow"))
                   (s/cron-schedule "0 0 12 15 * ?"))))
```

#### The "Do Nothing" Instruction

Instructs the scheduler to do nothing.

A code example that uses this instruction:

``` clojure
(require '[clojurewerkz.quartzite.triggers :as t])
(require '[clojurewerkz.quartzite.schedule.cron :as s])

(t/build
 (t/start-now)
 (t/with-identity "id1" "group1")
 (t/with-schedule (s/schedule
                   (s/with-misfire-handling-instruction-do-nothing)
                   (s/in-time-zone (java.util.TimeZone/getTimeZone "Europe/Moscow"))
                   (s/cron-schedule "0 0 12 15 * ?"))))
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
