(defproject clojurewerkz/quartzite "1.0.0-SNAPSHOT"
  :description "Quarzite is a thin Clojure layer on top the Quartz Scheduler"
  :license { :name "Eclipse Public License" }
  :repositories {
                 "clojure-releases" "http://build.clojure.org/releases"
                 "sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}
                             }
                 }
  :dependencies [[org.clojure/clojure         "1.3.0"]
                 [org.quartz-scheduler/quartz "2.1.3"]]
  :multi-deps {
               "1.4" [[org.clojure/clojure "1.4.0-beta1"]]
               :all [[org.quartz-scheduler/quartz "2.1.3"]]
               }
  :dev-dependencies [[clj-time                  "0.3.5" :exclusions [org.clojure/clojure]]
                     [org.clojure/tools.logging "0.2.3" :exclusions [org.clojure/clojure]]
                     [org.slf4j/slf4j-simple    "1.6.2"]
                     [org.slf4j/slf4j-api       "1.6.2"]
                     [log4j                     "1.2.16" :exclusions [javax.mail/mail
                                                                      javax.jms/jms
                                                                      com.sun.jdmk/jmxtools
                                                                      com.sun.jmx/jmxri]]]
  :source-path        "src/clojure"
  :java-source-path   "src/java"
  :dev-resources-path "test/resources"
  :warn-on-reflection true)
