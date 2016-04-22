(ns me.marsliu.arithmetic.app
  (:require [me.marsliu.arithmetic.index :as index]
            [me.marsliu.arithmetic.question :as question])
  (:use [ring.middleware.reload :refer [wrap-reload]]
        [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
        [compojure.core :only [GET POST defroutes routes wrap-routes]])
  (:require [compojure.route :as route]
            [ring.util.response :as response]))

(defmacro grace-site
  "如果环境中设置了 DEVELOP 变量，启用 wrap-reload"
  [handler]
  (if (System/getenv "DEVELOP")
    `(wrap-routes (wrap-reload ~handler) #(wrap-defaults % site-defaults))
    `(wrap-routes ~handler #(wrap-defaults % site-defaults))))

(defmacro grace-api
  "如果环境中设置了 DEVELOP 变量，启用 wrap-reload"
  [handler]
  (if (System/getenv "DEVELOP")
    `(wrap-routes (wrap-reload ~handler) #(wrap-defaults % api-defaults))
    `(wrap-routes ~handler #(wrap-defaults % api-defaults))))

(defroutes site-routes
  (route/resources "/")
  (GET "/" req (index/index req)))

(defroutes api-routes
  (GET "/api/questions/ask/:id" [id] (question/ask id))
  (GET "/api/questions/ask" [] (question/ask []))
  (GET "/api/questions/count" request (question/log-count request))
  (GET "/api/questions/list" [page] (if (empty? page)
                                      (question/questions "0")
                                      (question/questions page)))
  (POST "/api/question/:qid/answer" [qid result] (question/answer qid result)))

(def app
  (routes
   (grace-site site-routes)
   (grace-api api-routes)))

;; ring/compojure site arch sample from
;; http://stackoverflow.com/questions/28348974/clojure-coding-with-emacs-and-cider
;; (ns my-project.handler
;;   (:require [compojure.core :refer :all]
;;             [compojure.route :as route]
;;             [ring.middleware.reload :refer [wrap-reload]]
;;             [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

;; (defroutes app-routes
;;   (GET "/" [] "Hello World")
;;   (route/not-found "Not Found"))

;; (def app
;;   (-> app-routes
;;       wrap-reload
;;       (wrap-defaults site-defaults)))
