# Using durable Quartz stores with Quartzite

This guide covers:

 * Overview of durable and transients job stores
 * Available durable job stores
 * Quartz and Clojure class loaders
 * How to use durable job stores with Quartzite

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/quartzite.docs).


## What version of Quartzite does this guide cover?

This guide covers Quartzite `2.0.x` (including beta releases).


## Overview

During Quartz operation, it has to do some housekeeping: track trigger execution history,
trigger and job state, misfires and so on. By default Quartz keeps this information
in memory. In some cases it is all you need but has obvious drawbacks:

 * If JVM is stopped, killed or crashes, all state is lost
 * Really large number of jobs and triggers may consume a lot of RAM

To address this issue, Quartz supports *durable job stores*. They
store all the scheduler state, not just information about jobs.


## Available Durable Quartz Job Stores

Quartz comes with a JDBC-backed job data store out of the box. There is also a [MongoDB-backed one](https://github.com/michaelklishin/quartz-mongodb).

For more information, see Quartz documentation on [JDBC-backed durable Quartz stores](http://quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigJobStoreTX).


## Quartz and Clojure Class Loaders

Clojure is a compiled language: code is compiled when it is loaded (usually via `clojure.core/require`). To dynamically
load classes Clojure code compiles to, Clojure uses a special class loader. Since Quartz also uses its own class loader,
due to the JVM security model it cannot see job classes Clojure compiler generates.

A solution to this issue is described in the following section.


## How to use durable job stores with Quartzite

### With a SQL Database

Quartz provides several classes for JDBC-backed job stores. This solution was
tested using the JobStoreTX class which should be used in applications that
are not running inside an application server like Immutant or JBoss AS.

To allow Quartz to see classes the Clojure compiler generates, we must provide a
custom implementation of the ClassLoadHelper class which Quartz uses for
discovering classes. A working implementation is below:

``` java
// Example package
package oceania.myservice.quartz;

import clojure.lang.DynamicClassLoader;
import org.quartz.spi.ClassLoadHelper;

public class DynamicClassLoadHelper extends DynamicClassLoader implements ClassLoadHelper {
    public void initialize() {}

    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> loadClass(String name, Class<T> clazz)
        throws ClassNotFoundException {
        return (Class<? extends T>) loadClass(name);
    }

    public ClassLoader getClassLoader() {
        return this;
    }
}
```


To configure Quartz to use this class loader, modify your `quartz.properties` file
to include the following lines:

```
org.quartz.scheduler.classLoadHelper.class=oceania.myservice.quartz.DynamicClassLoadHelper

## To use the JobStoreTX with Postgresql include the following:
## (Documentation for other database backends is below)
org.quartz.dataSource.db.driver=org.postgresql.Driver
org.quartz.dataSource.db.URL=<jdbc url string here>
org.quartz.dataSource.db.user=<db user here>
org.quartz.dataSource.db.password=<db password here>

org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.PostgreSQLDelegate
org.quartz.jobStore.dataSource=db
```

For more configuration properties for the dataSource see [Quartz Data Source Config](http://quartz-scheduler.org/documentation/quartz-2.2.x/configuration/ConfigDataSources)

For jobStore see [Quartz Job Store Config](http://quartz-scheduler.org/documentation/quartz-2.2.x/configuration/ConfigJobStoreTX)

Checkout the jobStore configuration options if you want to utilize a database other than PostgreSQL.


Finally, download the Quartz release from [Quartz](http://quartz-scheduler.org/downloads) and navigate to the "docs/dbTables" subdirectory to find the SQL table-creation scripts. Run the script appropriate for your SQL backend and you should be all set!


### With MongoDB

It is possible to use [MongoDB as a durable job store for Quartz](https://github.com/michaelklishin/quartz-mongodb/).

A solution to the different class loaders issue is to subclass the job store class and (if it allows this) make it use
Clojure's `clojure.lang.DynamicClassLoader`:

``` java
package megacorp.myservice.quartz;

import clojure.lang.DynamicClassLoader;
import com.novemberain.quartz.mongodb.MongoDBJobStore;

public class JobStore extends MongoDBJobStore implements org.quartz.spi.JobStore {
  @Override
  protected ClassLoader getJobClassLoader() {
    // makes it possible for Quartz to load and instantiate jobs that are defined
    // using defrecord without AOT compilation.
    return new DynamicClassLoader();
  }
}
```

and then configure Quartz to use it via the `quartz.properties` file:

```
org.quartz.scheduler.instanceName = MyServiceScheduler
org.quartz.threadPool.threadCount = 4

## Use MongoDB-backed store to persistently store information about
# scheduled jobs, triggers and their state.
#
org.quartz.jobStore.class=megacorp.myservice.quartz.JobStore
org.quartz.jobStore.addresses=127.0.0.1
org.quartz.jobStore.dbName=myservice_production
org.quartz.jobStore.collectionPrefix=quartz

## Quartz plugins
#
org.quartz.plugin.triggHistory.class = org.quartz.plugins.history.LoggingTriggerHistoryPlugin
org.quartz.plugin.jobHistory.class = org.quartz.plugins.history.LoggingJobHistoryPlugin
```

For more information about mixed Clojure/Java projects, see [Clojure/Java projects with Leiningen 2+](https://github.com/technomancy/leiningen/blob/master/doc/MIXED_PROJECTS.md).

Future versions of Quartzite may ship with custom job classes like this out of the box.


## Wrapping Up

Quartz provides support for durable stores for its state. Using a durable store
is a good idea for availability reasons. Due to specifics of how Clojure compiler
and Quartz scheduler work, using durable stores with Quartzite requires a little
bit of glue code. Quartzite authors continue looking for a good generic solution.
