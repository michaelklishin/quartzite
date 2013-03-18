# Quarzite, a thin Clojure layer on top the Quartz Scheduler

Quarzite is a powerful Clojure scheduling library built on top the [Quartz Scheduler](http://quartz-scheduler.org/).


## Usage

Quartzite is a young project and until 1.0 is released and documentation guides are written,
it may be challenging to use. For code examples, see our test suite.

Once documentation guides are written, we will update this document.


## Project goals

 * Support all commonly used Quartz features but follow the 80/20 rule
 * Be (reasonably) idiomatic but easy to understand for people familiar with Quartz
 * Be [well documented](http://clojurequartz.info/)
 * Be [well tested](https://github.com/michaelklishin/quartzite/tree/master/test/clojurewerkz/quartzite/test)
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
 * Listeners: complete
 * Custom configuration: no additions necessary thanks to Quartz


## Supported Clojure versions

Quartzite is built from the ground up for Clojure 1.3 and up. The most recent release is always
recommended.


## Maven Artifacts

### The Most Recent Release

With Leiningen:

    [clojurewerkz/quartzite "1.1.0-beta1"]

With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>quartzite</artifactId>
      <version>1.1.0-beta1</version>
    </dependency>



## Getting Started, Documentation

Please refer to the [Getting Started with Clojure and Quartz](http://clojurequartz.info/articles/getting_started.html).
[Quartzite documentation guides](http://clojurequartz.info/) are not fully complete but cover most of the functionality.

Quality [Clojure documentation](http://clojure-doc.org) is available elsewhere.


## Community

[Quartzite has a mailing list](https://groups.google.com/group/clojure-quartz). Feel free to join it and ask any questions you may have.

To subscribe for announcements of releases, important changes and so on, please follow [@ClojureWerkz](https://twitter.com/#!/clojurewerkz) on Twitter.




## Quartzite Is a ClojureWerkz Project

Quartzite is part of the [group of Clojure libraries known as ClojureWerkz](http://clojurewerkz.org), together with
[Monger](http://clojuremongodb.info), [Welle](http://clojureriak.info), [Neocons](https://github.com/michaelklishin/neocons), [Langohr](https://github.com/michaelklishin/langohr), [Elastisch](https://github.com/clojurewerkz/elastisch) and several others.



## Continuous Integration

[![Continuous Integration status](https://secure.travis-ci.org/michaelklishin/quartzite.png)](http://travis-ci.org/michaelklishin/quartzite)


CI is hosted by [travis-ci.org](http://travis-ci.org)



## Development

Quartzite uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make
sure you have it installed and then run tests against all supported Clojure versions using

    lein2 all test

Then create a branch and make your changes on it. Once you are done with your changes and all
tests pass, submit a pull request on Github.


## License

Copyright (C) 2011-2012 Michael S. Klishin

Distributed under the Eclipse Public License, the same as Clojure.
