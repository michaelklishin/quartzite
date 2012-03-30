(defproject clojurewerkz/quartzite "1.0.0-beta3"
  :description "Quarzite is a thin Clojure layer on top the Quartz Scheduler"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.quartz-scheduler/quartz "2.1.3"]
                 [clj-time "0.3.7"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :profiles {:1.4 {:dependencies [[org.clojure/clojure "1.4.0-beta4"]]},
             :dev {:resource-paths ["test/resources"],
                   :dependencies [[org.clojure/tools.logging "0.2.3" :exclusions [org.clojure/clojure]]
                                  [org.slf4j/slf4j-simple "1.6.2"]
                                  [org.slf4j/slf4j-api "1.6.2"]
                                  [log4j "1.2.16" :exclusions [javax.mail/mail
                                                               javax.jms/jms
                                                               com.sun.jdmk/jmxtools
                                                               com.sun.jmx/jmxri]]]}}
  :aliases { "all" ["with-profile" "dev:dev,1.4"] }
  :repositories {"clojure-releases" "http://build.clojure.org/releases",
                 "sonatype" {:url "http://oss.sonatype.org/content/repositories/releases",
                             :snapshots false,
                             :releases {:checksum :fail, :update :always}}}
  :warn-on-reflection true)