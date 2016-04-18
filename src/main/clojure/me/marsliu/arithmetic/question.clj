(ns me.marsliu.arithmetic.question
  (:require [clojure.java.io :as io]
            [clj-time.core :as ctc]
            [clj-time.format :as ctf]
            [clj-time.local :as ctl]
            [net.cgrand.enlive-html :as h]
            [ring.util.response :as response]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc]
            [java-jdbc.sql :as sql]
            [me.marsliu.arithmetic.equation :as eqt]
            [me.marsliu.arithmetic.env :as env]))

;; 定义每页显示的题目数目。
(def page-size 100)

;; 定义与sqlite匹配的日期字符串格式
(def dt-formatter (ctf/formatter-local "yyyy-MM-dd HH:mm::ss"))

(defn new-equation
  "根据环境设置构造一个算式，将其保存在数据库中，返回它的记录id，算式、文本和结果。"
  []
  (let [[e h] eqt/ploy-length-range
        l (eqt/rand-in-range e h)
        result (rand-int eqt/max-value)
        equation (eqt/solve-ploy result l)
        description (eqt/to-string equation)]
    ;; 开发阶段的测试逻辑，如果这里推导出来的算式，eval结果与result不同，说明推导过程有错误，记录一次 error。
    (let [t (eval equation)]
      (if (not= t result)
        (log/errorf "ploy %s create by %s but its result is %s" equation result t)))
    (let [qid (-> (jdbc/insert! env/db
                                :exam {:equation (eqt/to-code equation)
                                       :description description
                                       :result result})
                  first
                  (get (keyword "last_insert_rowid()")))]
      {:id qid :description description :result result})))

(defn give-me-one
  "如果 exam 中有没做完的习题，从中取一个返回，否则构造一个新习题。"
  []
  (let [{c :c} (first (jdbc/query env/db ["select count(*) as c from exam"]))]
    (if (= 0 c)
      (new-equation)
      (first (jdbc/query env/db ["select id, description, result from exam limit 1"])))))

(defn answer
  "发送问题答案到answer接口，返回判断。"
  [id result]
  (let [{r :result} (first (jdbc/query env/db ["select result from exam where id=?" id]))
        a (Integer. result)]
    (if (= r a)
      (let [row (first
                 (jdbc/query
                  env/db
                  ["select id, equation, description, result, created from exam where id=?" id]))]
        (jdbc/insert! env/db
                      :history
                      (assoc row :finished (ctf/unparse dt-formatter (ctl/local-now))))
        (jdbc/delete! env/db :exam ["id=?" id])
        (json/write-str {:result :ok}))
      (json/write-str {:result :error}))))

(defn questions
  "返回答卷历史的 JSON 形式。默认前一百条。"
  [page]
  (let [page-number (Integer/parseInt page)
        offset (* page-number page-size)
        limit page-size
        data (doall
              (jdbc/query
               env/db
               ["select id, description, finished from history order by id limit ? offset ?"
                limit offset]))]
    (json/write-str data)))

(defn count
  "返回日志计数，供翻页使用。"
  [request]
  (let [rowset (doall
                (jdbc/query
                 env/db ["select count(*) from history where finished is not null"]))]
    (json/write-str {:status :ok :result (first (first rowset))})))

(defn ask
  "返回一个练习题，如果指定了 id ，在 exam 中寻找既有的习题。如果没有指定 id，优先查找 exam 中
  既有的习题返回，如果exam中没有习题，构造一个新的。
  FIXME:没有找到对应习题的时候，要么返回404，要么应该构造一个新的习题。"
  [id]
  (if (empty? id)
    (json/write-str (give-me-one))
    (let [data (first
                (jdbc/query
                 env/db
                 ["select id, decription, result from exam where id=?" id]))]
      (json/write-str data))))
  
