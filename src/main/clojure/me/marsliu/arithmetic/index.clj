(ns me.marsliu.arithmetic.index
  (:use [compojure.core :only (GET defroutes)])
  (:require (compojure handler route))
  (:require [clojure.java.io :as io]
            [net.cgrand.enlive-html :as h]
            [ring.util.response :as response]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [me.marsliu.arithmetic.equation :as eqt]
            [me.marsliu.arithmetic.env :as env]
            [me.marsliu.arithmetic.question :as q]))

(h/deftemplate index-template "templates/index.html" [])

(defn index
  "显示主界面"
  [req]
  (if (not (.exists (io/file "arithmetic.db")))
    (env/setup-db))
    (index-template))
