(ns me.marsliu.arithmetic.app
  (:require [me.marsliu.arithmetic.index :as index]
            [me.marsliu.arithmetic.question :as question])
  (:use [ring.middleware.reload :refer [wrap-reload]]
        [compojure.core :only (GET POST defroutes)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :as response]))

(defmacro grace-app
  "如果环境中设置了 DEVELOP 变量，启用 wrap-reload"
  [& forms]
  (if (System/getenv "DEVELOP")
    `(-> app* wrap-reload compojure.handler/api)
    `(-> app* compojure.handler/site)))

(defroutes app*
  (route/resources "/")
  (GET "/" req (index/index req))
  (GET "/api/questions/ask/:id" [id] (question/ask id))
  (GET "/api/questions/ask" [] (question/ask []))
  (GET "/api/questions/count" request (question/count request))
  (GET "/api/questions/list" [page] (if (empty? page)
                                      (question/questions "0")
                                      (question/questions page)))
  (POST "/api/question/:qid/answer" [qid result] (question/answer qid result)))

(def app (grace-app))                 

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
