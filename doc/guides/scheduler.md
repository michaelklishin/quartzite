# Quartzite, a powerful Clojure scheduling library: schedulers

This guide covers:

 * Quartz schedulers
 * Scheduler lifecycle
 * Scheduler configuration
 * Using multiple schedulers with Quartzite

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/quartzite.docs).



## What version of Quartzite does this guide cover?

This guide covers Quartzite `2.0.x` (including beta releases).


## Overview

Quartz scheduler is a specialized database that stores jobs and
triggers and executes the jobs according to trigger schedules. It may
store its state in a durable data store (like relational databases
accessible via JDBC or
[MongoDB](https://github.com/michaelklishin/quartz-mongodb)).

Quartz schedulers are also *event emitters*. It is possible to
register listeners for various events (such as scheduler being paused
or a trigger firing).  Schedulers can be extended in using 3rd party
plugins.

Quartzite assumes that most application only ever use a single
scheduler.


## Quartz scheduler lifecycle

Scheduler may be in one of the following states:

 * Not started
 * Started
 * In the standby mode (paused)
 * Shut down

These is not technically an exhaustive list but it gives you a good idea.

Before jobs can be submitted for execution, the scheduler has to be
initialized with the `clojurewerkz.quartzite.scheduler/initialize`
function:

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
until it is started using
`clojurewerkz.quartzite.scheduler/start`. Very often this will happen
on application start up:

``` clojure
(ns my.service
  (:require [clojurewerkz.quartzite.scheduler :as qs]))

(defn -main
  [& m]
  (let [s (-> (qs/initialize) qs/start)]
    ))
```

To put scheduler into standby mode, use `clojurewerkz.quartzite.scheduler/standby` function:

``` clojure
(ns quartzite.docs.examples
  (:require [clojurewerkz.quartzite.scheduler :as qs]))

(defn -main
  [& args]
  (let [s (-> (qs/initialize) qs/start)]
    (println "Putting the scheduler in the standby mode...")
    (qs/standby s)))
```

When in the standby mode, scheduler is not active: triggers do not fire and as a consequence, no jobs are executed.

To shut down the scheduler, use `clojurewerkz.quartzite.scheduler/shutdown`:

``` clojure
(ns quartzite.docs.examples
  (:require [clojurewerkz.quartzite.scheduler :as qs]))

(defn -main
  [& args]
  (let [s (-> (qs/initialize) qs/start)]
    (println "Shutting the scheduler down...")
    (qs/shutdown s)))
```


Quartzite provides several predicate functions that let you determine current state of the scheduler:

``` clojure
(ns quartzite.docs.examples
  (:require [clojurewerkz.quartzite.scheduler :as qs]))

(defn -main
  [& args]
  (let [s (-> (qs/initialize) qs/start)]
    (when (qs/started? s)
      (println "The scheduler is running."))
    (qs/standby s)
    (when (qs/standby?s )
      (println "The scheduler is no longer running."))
    (qs/shutdown s)
    (when (qs/shutdown? s)
      (println "The scheduler is shut down."))))
```


## Quartz scheduler configuration

Quartzite relies on Quartz to handle configuration. Quartz uses a property file, `quartz.properties`, that needs to be on the classpath.
With [Leiningen](http://leiningen.org), you specify resource (non-code) file location using the `:resource-paths` key in `project.clj`:

``` clojure
:resource-paths     ["src/resources"]

;; alternative location for dev and test environments:
:profiles     {:dev {:resource-paths ["test/resources"]}}
```

Here is a minimalistic example of `quartz.properties`:

```
org.quartz.scheduler.instanceName = MyappScheduler
org.quartz.threadPool.threadCount = 4
```

A more involved example may use a custom durable store and one or more plugins:

``` clojure
org.quartz.scheduler.instanceName = MyappScheduler
org.quartz.threadPool.threadCount = 4

## Use a custom persistent scheduler store
#
org.quartz.jobStore.class=mycompany.myservice.quartz.JobStore
org.quartz.jobStore.addresses=10.2.21.102
org.quartz.jobStore.dbName=myservice_production
org.quartz.jobStore.collectionPrefix=quartz

## Quartz plugins
#
org.quartz.plugin.triggHistory.class = org.quartz.plugins.history.LoggingTriggerHistoryPlugin
org.quartz.plugin.jobHistory.class = org.quartz.plugins.history.LoggingJobHistoryPlugin
```

Please refer to the [Quartz Scheduler configuration reference](http://quartz-scheduler.org/documentation/quartz-2.x/configuration/) to learn more.


## What to read next

The documentation is organized as a number of guides, covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Defining jobs](/articles/jobs.html)
 * [Defining triggers and schedules](/articles/triggers.html)
 * [Using durable stores for scheduler state](/articles/durable_quartz_stores.html)
 * [Using Quartz plugins](/articles/quartz_plugins.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Quartzite mailing list](https://groups.google.com/forum/#!forum/clojure-quartz)

Let us know what was unclear or what has not been covered. Maybe you
do not like the guide style or grammar or discover spelling
mistakes. Reader feedback is key to making the documentation better.
