(ns me.marsliu.arithmetic.equation-test
  (:use [clojure.test])
  (:use [me.marsliu.arithmetic.equation]))

(deftest sbm-basic
  "二项乘法算式推导逻辑的基本测试"
  (loop [xs (keys multi-table)]
    (if xs
      (let [x (first xs)]
        (let [equation (solve-binary-mul x)
              result (eval equation)]
          (is (= result x) (format "Expect the equation %s is %s but %s" equation x result)))
        (recur (next xs))))))


(deftest sbd-basic
  "二项除法算式推导逻辑的基本测试"
  (loop [xs (range 1 10)]
    (if xs
      (let [x (first xs)]
        (let [equation (solve-binary-div x)
              result (eval equation)]
          (is (= result x) (format "Expect the equation %s is %s but %s" equation x result)))
        (recur (next xs))))))

(deftest sba-basic
  "二项加法算式推导逻辑的基本测试"
  (loop [xs (for [_ (range 30)] (rand-int max-value))]
    (if xs
      (let [x (first xs)]
        (let [equation (solve-binary-add x)
              result (eval equation)]
          (is (= result x) (format "Expect the equation %s is %s but %s" equation x result)))
        (recur (next xs))))))

(deftest sbs-basic
  "二项减法算式推导逻辑的基本测试"
  (loop [xs (for [_ (range 30)] (rand-int max-value))]
    (if xs
      (let [x (first xs)]
        (let [equation (solve-binary-sub x)
              result (eval equation)]
          (is (= result x) (format "Expect the equation %s is %s but %s" equation x result)))
        (recur (next xs))))))

(deftest slove-binary-basic
  "二项算式推导逻辑的基本测试，根据给定的参数随机构造对应的算式。"
  (loop [xs (for [_ (range 50)] (rand-int max-value))]
    (if xs
      (let [x (first xs)]
        (let [equation (solve-binary x)
              result (eval equation)]
          (is (= result x) (format "Expect the equation %s is %s but %s" equation x result)))
        (recur (next xs))))))
