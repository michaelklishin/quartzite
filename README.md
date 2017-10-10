# Quartzite, a thin Clojure layer on top the Quartz Scheduler

Quartzite is a powerful Clojure scheduling library built on top the [Quartz Scheduler](http://quartz-scheduler.org/).


## Project goals

 * Support all commonly used Quartz features but follow the 80/20 rule
 * Be (reasonably) idiomatic but easy to understand for people familiar with Quartz
 * Be [well documented](http://clojurequartz.info/)
 * Be [well tested](https://github.com/michaelklishin/quartzite/tree/master/test/clojurewerkz/quartzite/test)
 * Integrate with libraries like JodaTime where appropriate, like [Monger, a modern Clojure MongoDB client](https://github.com/michaelklishin/monger) does
 * Not a half-assed effort: libraries should be well maintained and test-driven or not be open sourced in the first place


## Project Maturity

Quartzite is past `2.0`. We consider it to be stable
and reasonably mature. Quartz Scheduler is a very mature project.

API changes generally follow semantic versioning and are driven by the user
feedback.


## Supported Clojure Versions

Quartzite requires Clojure 1.6 or later. The most recent release is always
recommended.


## Maven Artifacts

### The Most Recent Release

Quartzite artifacts are [released to Clojars](https://clojars.org/clojurewerkz/quartzite). If you are using Maven, add the following repository
definition to your `pom.xml`:

```xml
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
```

### The Most Recent Version

With Leiningen:

[![Clojars Project](http://clojars.org/clojurewerkz/quartzite/latest-version.svg)](http://clojars.org/clojurewerkz/quartzite)

With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>quartzite</artifactId>
      <version>2.1.0</version>
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

    lein all test

Then create a branch and make your changes on it. Once you are done with your changes and all
tests pass, submit a pull request on Github.


## License

Copyright (C) 2011-2016 Michael S. Klishin

Distributed under the Eclipse Public License, the same as Clojure.


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/michaelklishin/quartzite/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

