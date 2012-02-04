# Quarzite, a thin Clojure layer on top the Quartz Scheduler

Quarzite is a thin idiomatic Clojure layer on top the [Quartz Scheduler](http://quartz-scheduler.org/).


## Usage

Quartzite is a young project and until 1.0 is released and documentation guides are written,
it may be challenging to use for anyone except the author. For code examples, see our test
suite.

Once the library matures, we will update this document.


## Project goals

 * Support all commonly used Quartz features but follow the 80/20 rule
 * Be (reasonably) idiomatic but easy to understand for people familiar with Quartz
 * Integrate with libraries like JodaTime where appropriate, like [Monger, a modern Clojure MongoDB client](https://github.com/michaelklishin/monger) does
 * Not a half-assed effort: libraries should be well maintained and test-driven or not be open sourced in the first place


## Supported Features

 * Job builder DSL: complete
 * Trigger builder DSL: complete
 * Simple (periodic) scheduling: complete
 * Cron scheduling: complete
 * Daily time interval scheduling: complete
 * Calendar interval scheduling: complete
 * Persistent and/or custom Job Store support: no additions necessary thanks to Quartz
 * Listeners: not yet
 * Custom configuration: no additions necessary thanks to Quartz



## Maven Artifacts

With Leiningen:

    [clojurewerkz/quartzite "1.0.0-SNAPSHOT"]

New snapshots are released to [clojars.org](https://clojars.org/clojurewerkz/quartzite) every few days.



## Continuous Integration

[![Continuous Integration status](https://secure.travis-ci.org/michaelklishin/quartzite.png)](http://travis-ci.org/michaelklishin/quartzite)


CI is hosted by [travis-ci.org](http://travis-ci.org)


## Supported Clojure versions

Quartzite is built from the ground up for Clojure 1.3 and up.


## Development

Install [lein-multi](https://github.com/maravillas/lein-multi) with

    lein plugin install lein-multi 1.1.0

then run tests against Clojure 1.3.0 and 1.4.0[-beta1] using

    lein multi test

Then create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit
a pull request on Github.


## License

Copyright (C) 2011 Michael S. Klishin

Distributed under the Eclipse Public License, the same as Clojure.
