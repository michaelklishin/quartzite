(defproject clojurewerkz/quartzite "1.0.0-SNAPSHOT"
  :description "Quarzite is a thin Clojure library on top the Quartz Scheduler"
  :license { :name "Eclipse Public License" }
  :repositories {
                 "clojure-releases" "http://build.clojure.org/releases"
                 "sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}
                             }
                 }  
  :dependencies [[org.clojure/clojure         "1.3.0"]
                 [org.quartz-scheduler/quartz "2.1.1"]]
  :dev-dependencies [[clj-time                "0.3.3" :exclusions [org.clojure/clojure]]]
  :source-path        "src/clojure"
  :java-source-path   "src/java"
  :dev-resources-path "test/resources"
  :warn-on-reflection true)
