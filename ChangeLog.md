## Changes between Quartzite 1.0.0-beta1 and 1.0.0-beta2

### JobDataMapConversion protocol now supports JobExecutionContext

`clojurewerkz.quartzite.conversion.from-job-data` now can work with JobExecutionContext instances.


### Utility date/time functions, clj-time dependency

Several date/time functions extracted from various apps that use Quartzite will be
incubating in the `clojurewerkz.quartzite.date-time` namespace for possible inclusion
into clj-time. date/time functions are very relevant to Quartzite and thus depending
on clj-time and providing additional functions makes sense.

### Leiningen 2

Quartzite now uses [Leiningen 2](https://github.com/technomancy/leiningen/wiki/Upgrading).
