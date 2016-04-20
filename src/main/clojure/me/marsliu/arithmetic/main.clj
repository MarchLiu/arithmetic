;; 供 nrepl 加载的开发用入口文件
(ns me.marsliu.arithmetic.main
    (:use [ring.adapter.jetty :only (run-jetty)])
    (:use [me.marsliu.arithmetic.app :only (app)])
    (:require net.cgrand.reload))

;; Evlive 自动重加载设置
(doseq [n (->> (loaded-libs)
               (filter #(.startsWith (name %) "me.marsliu.arithmetic"))
               (filter #(not= % "me.marsliu.arithmetic.main"))
               (map find-ns)
               (filter #(:net.cgrand.reload/deps (meta %))))]
  (net.cgrand.reload/auto-reload n))

;; use 该文件时server会启动，可以在 repl 中用 (.start server)和
;; (.stop server) 手动控制。
(def server (run-jetty #'app {:port 9080
                              :join? false
                              :auto-reload true
                              :auto-fresh true}))

(defn restart-server []
    (.stop server)
    (.start server))
