# Getting Started with Quartzite

This guide combines an overview of Quartzite with a quick tutorial that helps you to get started with it.
It should take about 10 minutes to read and study the provided code examples. This guide covers:

 * Features of Quartzite
 * Clojure version requirement
 * How to add Quartzite dependency to your project
 * Basic operations (initializing the scheduler, scheduling and unscheduling jobs)
 * Overview of how persistent schedule stores are used with Quartzite

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/quartzite.docs).


## What version of Quartzite does this guide cover?

This guide covers Quartzite `2.0.x` (including beta releases).


## Quartzite Overview

Quartzite is an expressive scheduling Clojure library built on top of the [Quartz Scheduler](http://quartz-scheduler.org). It supports all Quartz 2.1.x features,
provides a clean Clojure DSL and is well maintained.


### What Quartzite is not

Quartzite is not a replacement for Quartz, instead, Quartzite builds on top of it. Quartzite is not a standalone tool like Cron, it is a library that
applications use to programmatically schedule operations, unschedule them, calculate schedules using data from various data sources. Quartzite benefits
from the extensibility of Quartz, for example, pluggable durable schedule stores.

### Project Maturity

Quartzite is a fairly mature project. It is past the 1.0 release, about 1 year old with active use from day one. The API is locked down and new features
are introduced only if they are really necessary.


## Supported Clojure versions

Quartzite requires Clojure 1.7.


## Adding Quartzite Dependency To Your Project

### With Leiningen

    [clojurewerkz/quartzite "2.1.0"]

### With Maven

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>quartzite</artifactId>
      <version>2.1.0</version>
    </dependency>

It is recommended to stay up-to-date with new versions. New releases and important changes are announced [@ClojureWerkz](http://twitter.com/ClojureWerkz).


## Overview

Quartzite is a scheduling library. It lets you execute operations
("jobs") on specific schedules and with desired error handling. Unlike
many other similar systems (cron being one of the oldest and probably
the most widely known), Quartzite is not a standalone tool. Instead,
it is an API that applications use to integrate scheduling
functionality.


## Initializing the scheduler

Quartzite is built on top Quartz. In Quartz, a scheduler is a
temporary database of jobs, schedules and other related
information. Before jobs can be submitted for execution, the scheduler
has to be initialized with the
`clojurewerkz.quartzite.scheduler/initialize` function:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]))

(defn -main
  [& m]
  (let [s (qs/initialize)]
    ))
```

This function starts a an instance of Quartz scheduler that maintains
a thread pool of non-daemon threads. Using this function in the
top-level namespace code is a **bad idea**: it will cause Leiningen
tasks like `lein jar` to hang forever because Quartz threads will
prevent JVM from exiting.

When Quartzite scheduler is initialized, it does not trigger jobs
until it is started using `clojurewerkz.quartzite.scheduler/start`.
Very often this will happen on application start up:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]))

(defn -main
  [& m]
  (let [s (-> (qs/initialize) qs/start)]
    ))
```

As the example above demonstrates, `clojurewerkz.quartzite.scheduler/start` returns
its scheduler argument after starting it.



## Jobs and triggers.

Quartz separates operations that are performed (called jobs) from
rules according to which they are performed (triggers). Triggers
combine execution schedule (for example, "every 4 hours" or "at noon
every Friday"), date/time when execution starts and ends, operation
priority and a few other things.

A job may have multiple triggers associated with them (although it is
common to have just one). Both jobs and triggers have identifiers
(called "keys") that you use to manage them, for example, unschedule,
pause or resume.


## Defining jobs.

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

These examples demonstrate defining the "executable part" of a job. As we've mentioned before, to make it possible to manage jobs and triggers,
Quartz requires them to have identities. To define a complete job that can be submitted for scheduling, you use a DSL in the `clojurewerkz.quartzite.jobs`
namespace:

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
              (j/with-identity (j/key "jobs.noop.1")))]
    ))
```

`clojurewerkz.quartzite.jobs/key` function can be used with any other function that accepts job keys.

Now that we know how jobs are defined, lets move on to triggers.


## Using simple periodic schedules

Triggers are defined using another DSL, this time from the
`clojurewerkz.quartzite.triggers` namespace. Lets define a trigger
that fires (executes its associated job) 10 times every 200 ms,
starting immediately:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]))

(defn -main
  [& m]
  (let [s       (-> (qs/initialize) qs/start)
        trigger (t/build
                  (t/with-identity (t/key "triggers.1"))
                  (t/start-now)
                  (t/with-schedule (schedule
                                     (with-repeat-count 10)
                                     (with-interval-in-milliseconds 200))))]))
```

So how triggers are defined is quite similar to how jobs are
defined. Quartz provides several types of execution schedules and
Quartzite supports all of them in the DSL. A couple more schedule
types will be demonstrated later in this guide.

Simple periodic schedule demonstrated here is used when you need to
perform a task N times with a fixed time interval.

Now that we know how to define jobs and triggers, lets schedule them
for execution.


## Scheduling jobs for execution

`clojurewerkz.quartzite.scheduler/schedule` submits a job and a trigger associated with it for execution:

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
them) using `clojurewerkz.quartzite.scheduler/delete-trigger` and
`clojurewerkz.quartzite.scheduler/delete-triggers` functions:

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
       job  (j/build
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
  (qs/delete-trigger s tk)))
```

Please note that `delete-trigger` takes a *trigger*
key. `clojurewerkz.quartzite.scheduler/delete-triggers` works the same
way but takes a collection of keys.

There are other functions that delete jbos and all their triggers,
pause execution of triggers and so on. They are covered in the
[Scheduling, unscheduling and pausing jobs](/articles/unscheduling_and_pausing.html) guide.


## Using Cron expression schedules

One of the schedule types that Quartz supports is the Cron expression
schedule. It lets you define the schedule as a single expression
similar to those used with
cron(8)(http://linux.die.net/man/8/cron). This form is concise but may
also seem cryptic. As such, Cron schedules are most commonly used when
migrating legacy applications or by developers who are deeply familiar
with Cron.

To define a trigger that will use a Cron expression schedule, you
combine DSLs from `clojurewerkz.quartzite.triggers` and
`clojurewerkz.quartzite.schedule.cron` namespaces:

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
        job (j/build
              (j/of-type NoOpJob)
              (j/with-identity (j/key "jobs.noop.1")))
        trigger (t/build
                  (t/with-identity (t/key "triggers.1"))
                  (t/start-now)
                  (t/with-schedule (schedule
                                     (cron-schedule "0 0 15 ? * 5"))))]
  (qs/schedule s job trigger)))
```

The Quartz cron expressions are more powerful than the traditional
[crontab(5)](http://linux.die.net/man/5/crontab) spec, providing an
additional field for seconds, a special character for "increments" and
more. See the
[CronExpression](http://www.quartz-scheduler.org/api/2.1.5/org/quartz/CronExpression.html)
javadoc for more details.


## Using calendar interval schedules

Third type of trigger schdule is the calendar interval schedule. It
fires at fixed intervals: minutes, hours, days, weeks, months or
years.  In this example, we use intervals of 1 day:

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
  (let [s   (-> (qs/initialize) qs/start)
       job (j/build
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
            [clojurewerkz.quartzite.schedule.daily-interval :refer [schedule monday-through-friday
                                                                    starting-daily-at time-of-day ending-daily-at
                                                                    with-interval-in-minutes]]))

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
                                     (with-interval-in-minutes 2)
                                     (monday-through-friday)
                                     (starting-daily-at (time-of-day 9 00 00))
                                     (ending-daily-at (time-of-day 17 00 00)))))]
  (qs/schedule s job trigger)))
```



## Wrapping up

We have walked through basic features of Quartzite (and Quartz, the
library it is built on). Quartzite makes it easier to work with
executing operations on a schedule:

 * Separates scheduling and execution rules from jobs
 * Provides multiple types of schedules
 * Provides means to identify jobs and triggers, schedule, unschedule, pause and resume them

Granted, this flexibility comes at some additional boilerplate but
Quartzite avoids most of it with a number of DSLs that Clojure is so
good at.

Quartz has many more small features this guide does not cover:

 * Passing context map to jobs
 * It is possible to use persistent stores (there are JDBC databases stores, [MongoDB Quartz store](https://github.com/michaelklishin/quartz-mongodb) and so on) for scheduling data
 * Event listeners
 * Querying scheduler state (like getting a list of all paused or misfired triggers)
 * Misfires and error handling scenarios
 * Clustering
 * 3rd party extensions

Most of these features are covered in the rest of the guides.

While Quartzite focuses on providing convenient expressive DSLs for
scheduling, it benefits directly from all those features. To integrate
many of them, all you have to do is to add a dependency to your
project (or have a JAR on the classpath any other way) and specify a
line or few in the Quartz configuration file, `quartz.properties`,
that must also be on the classpath.


## What to read next

The documentation is organized as a number of guides, covering all
kinds of topics.

We recommend that you read the following guides first, if possible, in
this order:

 * [Scheduler initialization](/articles/scheduler.html)
 * [Defining jobs](/articles/jobs.html)
 * [Defining triggers and schedules](/articles/triggers.html)
 * [Using durable stores for scheduler state](/articles/durable_quartz_stores.html)
 * [Using Quartz plugins](/articles/quartz_plugins.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on
Twitter or the [Quartzite mailing list](https://groups.google.com/forum/#!forum/clojure-quartz).

Let us know what was unclear or what has not been covered. Maybe you
do not like the guide style or grammar or discover spelling
mistakes. Reader feedback is key to making the documentation better.
