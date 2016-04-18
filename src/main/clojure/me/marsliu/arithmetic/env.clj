(ns me.marsliu.arithmetic.env
  (:require [clojure.tools.logging :as log]
            [clojure.java.jdbc :as jdbc]
            [java-jdbc.ddl :as ddl]))

(def db {:subname "arithmetic.db"
         :classname   "org.sqlite.JDBC"
         :subprotocol "sqlite"})

(defn setup-db
  "初始化数据库"
  []
  (jdbc/with-db-connection [db db]
    (jdbc/execute! db (ddl/create-table :history
                                        [:id "integer primary key"]
                                        [:equation "text"]
                                        [:description "text"]
                                        [:result "integer"]
                                        [:created "text"]
                                        [:finished "text default CURRENT_TIMESTAMP"]))
    (jdbc/execute! db (ddl/create-table :exam
                                        [:id "integer primary key autoincrement"]
                                        [:equation "text"]
                                        [:description "text"]
                                        [:result "integer"]
                                        [:created "text default CURRENT_TIMESTAMP"])))
  (log/info "数据库初始化完毕"))
  
