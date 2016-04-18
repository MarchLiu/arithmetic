(defproject me.marsliu/arithmetic "0.1.0"
  :description "给小朋友写的口算练习"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-time "0.8.0"]
;;                 [org.clojars.marsliu/clj-parsec "0.1.0"]
                 [ring "1.4.0"]
                 [org.clojure/java.jdbc "0.6.0-alpha1"]
                 [java-jdbc/dsl "0.1.3"]
                 [javax.servlet/servlet-api "2.5"]
                 [compojure "1.5.0"]
                 [enlive "1.1.6"]
                 [korma "0.4.0"]
                 [org.xerial/sqlite-jdbc "3.7.15-M1"]]
  :plugins [[lein-ring "0.9.7"]]
  :profile {:dev {:plugins [[cider/cider-nrepl "0.11.0"]]}}
  :source-paths ["src/main/clojure"]
  :resource-paths ["resources"]
  :ring {:handler me.marsliu.arithmetic.app/app})
