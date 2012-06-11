(defproject clojurewerkz/quartzite "1.0.0-SNAPSHOT"
  :description "Quarzite is a thin Clojure layer on top the Quartz Scheduler"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.quartz-scheduler/quartz "2.1.5"]
                 [clj-time "0.4.2"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :test-selectors {:all     (constantly true)
                   :focus   :focus
                   :default (constantly true)}
  :profiles {:1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.0-master-SNAPSHOT"]]}
             :dev {:resource-paths ["test/resources"]
                   :dependencies [[org.clojure/tools.logging "0.2.3" :exclusions [org.clojure/clojure]]
                                  [org.slf4j/slf4j-log4j12   "1.6.4"]]}}
  :aliases {"all" ["with-profile" "dev:dev,1.4:dev,1.5"]
            "ci"  ["with-profile" "dev:dev,1.4"]}
  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}
  :warn-on-reflection true)