(ns me.marsliu.arithmetic.env
  (:require [clojure.tools.logging :as log]
            [jdbc.core :as jdbc]
            [sqlingvo.core :as sql]
            [sqlingvo.db :as db]))

(def db {:subname "arithmetic.db"
         :classname   "org.sqlite.JDBC"
         :subprotocol "sqlite"})

(defn setup-db
  "初始化数据库"
  []
  (with-open [conn (jdbc/connection db
                                    )]
    (jdbc/execute conn
                  "create table exam(
id integer primary key AUTOINCREMENT, 
equation text, 
description text, 
result integer, 
created text default CURRENT_TIMESTAMP)")
    (jdbc/execute conn
                  "create table history(
id integer primary key, 
equation text, 
description text, 
result integer, 
created text,
finished text default CURRENT_TIMESTAMP)")
    (log/info "数据库初始化完毕")))
 
