(defproject clojurewerkz/quartzite "2.2.0"
  :description "Quarzite is a thin Clojure layer on top the Quartz Scheduler"
  :min-lein-version "2.5.1"
  :license {:name "Eclipse Public License"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.quartz-scheduler/quartz "2.3.2"]
                 [clj-time "0.14.2"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :test-selectors {:all     (constantly true)
                   :focus   :focus
                   :default (constantly true)}
  :profiles {:1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :master {:dependencies [[org.clojure/clojure "1.10.0-master-SNAPSHOT"]]}
             :dev {:resource-paths ["test/resources"]
                   :dependencies [[org.clojure/tools.logging "0.2.3" :exclusions [org.clojure/clojure]]
                                  [org.slf4j/slf4j-log4j12   "1.6.4"]]}}
  :aliases {"all" ["with-profile" "dev:dev,1.7:dev,master"]}
  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}
  :global-vars {*warn-on-reflection* true}
  :mailing-list {:name "clojure-quartz"
                 :archive "https://groups.google.com/group/clojure-quartz"
                 :post "clojure-quartz@googlegroups.com"}
  :plugins [[codox "0.8.10"]]
  :codox {:sources ["src/clojure"]
          :output-dir "doc/api"})
