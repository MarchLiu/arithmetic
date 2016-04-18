;; 算法引擎，通过随机选择数字然后求解多项算式生成问题，每一个步骤涉及的数字保持在
;; 0 到 max-value 之间，乘法只涉及九九乘法表。生成的算式长度最少两项，最多为
;; max-steps
(ns me.marsliu.arithmetic.equation
  (:require [clojure.string :as s]))

(defonce max-value 200)

;; 可用的因子，用于倒推除法算式，这里仅允许 [1, 9]
(defonce factors #{1 2 3 4 5 6 7 8 9})

;; 定义当前的算式最大最小长度，即组成算式的数字个数，最小值不能小于 2 。
(defonce ploy-length-range [2 5])

;; 给出乘法表，当前仅提供九九乘法口诀
(defonce multi-table {1 [[1 1]]
                  2 [[1 2] [2 1]]
                  3 [[1 3] [3 1]]
                  4 [[1 4] [2 2] [4 1]]
                  5 [[1 5] [5 1]]
                  6 [[1 6] [2 3] [3 2] [6 1]]
                  7 [[1 7] [7 1]]
                  8 [[1 8] [2 4] [4 2] [8 1]]
                  9 [[1 9] [3 3] [9 1]]
                  10 [[2 5] [5 2] ]
                  12 [[2 6] [3 4] [4 3] [6 2]]
                  14 [[2 7] [7 2]]
                  15 [[3 5] [5 3]]
                  16 [[2 8] [4 4] [8 2]]
                  18 [[2 9] [3 6] [6 3] [9 2]]
                  20 [[4 5] [5 4]]
                  21 [[3 7] [7 3]]
                  24 [[4 6] [6 4]]
                  25 [[5 5]]
                  27 [[3 9] [9 3]]
                  30 [[5 6] [6 5]]
                  32 [[4 8] [8 4]]
                  35 [[5 7] [7 5]]
                  36 [[4 9] [9 4]]
                  40 [[5 8] [8 5]]
                  42 [[6 7] [7 6]]
                  45 [[5 9] [9 5]]
                  48 [[6 8] [8 6]]
                  49 [[7 7]]
                  54 [[6 9] [9 6]]
                  56 [[7 8] [8 7]]
                  63 [[7 9] [9 7]]
                  64 [[8 8]]
                  72 [[8 9] [9 8]]
                  81 [[9 9]]})

;; 可用的二元运算符，这里仅允许 + - * /
(defonce operators #{\+ \- \* \/})

;; 运算符与文本字符的对应表
(defonce opt-table {+ \+ - \- * \× / \÷})

(defn rand-in-range [bottom top]
  (+ bottom (rand-int (- top bottom))))

(defn solve-binary-mul
  "slove-binary-multi 推导乘法算式。在乘法表中查找该数字对应的算式，随机选择一个返回，
  如果传入的参数不在乘法表内，返回 nil。正常逻辑不会出现这种情况，这是预防处理。"
  [^long n]
  (when-let [value (get multi-table n)]
    (let [[x y] (rand-nth value)]
      (list * x y))))

(defn solve-binary-div
  "slove-binary-div 推导除法算式，用 factors 中的的某个整数与 n 的乘积构造一个合法的除法算式。
如果传入的参数不在 factors 内，返回 nil。正常逻辑不会出现这种情况，这是预防处理。"
  [^long n]
  (when (contains? factors n)
    (let [other (rand-nth (vec factors))]
      (list / (* n other) other))))

(defn solve-binary-add
  "slove-binary-add 推导加法算式，随机给出结果为 n 的两个正整数的加法表达式。
如果 n 大于 max-value ，返回 nil。正常逻辑不会出现这种情况，这是预防处理。"
  [^long n]
  (when (< n max-value)
    (let [x (rand-int n) y (- n x)]
      (list + x y))))

(defn solve-binary-sub
  "slove-binary-sub 推导减法算式，随机给出结果为 n 的两个正整数的减法表达式。
如果 n 大于 max-value ，返回 nil。正常逻辑不会出现这种情况，这是预防处理。"
  [^long n]
  (when (< n max-value)
    (let [x (rand-in-range n max-value) y (- x n)]
      (list - x y))))

(defn solve-binary
  "给定一个数字，给出对应的二项算式。如果该数字不在九九乘法表范围内，排除乘法。如果不在因子表范围内，排除除法。"
  [^long n]
  (let [opts0 (if (contains? factors n) operators (disj operators \/))
        opts (if (contains? multi-table n) opts0 (disj opts0 \*))
        opt (rand-nth (vec opts))]
    (case opt
      \+ (solve-binary-add n)
      \- (solve-binary-sub n)
      \* (solve-binary-mul n)
      \/ (solve-binary-div n))))

(defn solve-ploy
  "给定一个数字，给出指定长度的随机算式，预期的算式长度不能小于2，即至少有两个数字参与运算。"
  [^long n ^long length]
  (assert (<= 2 length))
  (if (= length 2)
    (solve-binary n)
    (let [[opt left right] (solve-binary n)
          step (rand-in-range 1 length)
          residue (- length step)]
      (cond
        (= step 1) (list opt left (solve-ploy right residue))
        (= residue 1) (list opt (solve-ploy left step) right)
        :else (list opt (solve-ploy left step) (solve-ploy right residue))))))

(def operators-priority {+ 0 - 0 * 1 / 1})

(defn priority?
  "判断两个运算符的优先级关系，x 优先于 y 返回 true，否则返回 false"
  [x y]
  (> (- (get operators-priority x) (get operators-priority y)) 0))

(declare ets-left-helper)
(declare ets-right-helper)

(defn ets-left-helper
  "内部函数，根据上一级运算符和当前算式运算符的优先级比较决定是否加括号。这个函数用于左子式。"
  [up eqt]
  (if (number? eqt)
    (str eqt)
    (let [[opt left right] eqt
          opt-str (get opt-table opt)
          left-str (ets-left-helper opt left)
          right-str (ets-right-helper opt right)]
      ;; 如果上一级运算符比当前的高，当前算式的文本加括号
      (if (priority? up opt)
        (format "(%s%s%s)" left-str opt-str right-str)
        (format "%s%s%s" left-str opt-str right-str)))))

(defn ets-right-helper
  "内部函数，根据上一级运算符和当前算式运算符的优先级比较决定是否加括号。这个函数用于右子式。"
  [up eqt]
  (if (number? eqt)
    (str eqt)
    (let [[opt left right] eqt
          opt-str (get opt-table opt)
          left-str (ets-left-helper opt left)
          right-str (ets-right-helper opt right)]
      ;; 如果上一级运算符比当前的高，或者
      ;; 上一级符号是 - ，当前是 + 或 - ，或者
      ;; 上一级符号是 * ，当前是 * 或 /
      ;; ，加括号
      (if (or (priority? up opt)
              (and (= - up)
                   (or (= - opt)
                       (= + opt)))
              (and (= / up)
                   (or (= / opt)
                       (= * opt))))
        (format "(%s%s%s)" left-str opt-str right-str)
        (format "%s%s%s" left-str opt-str right-str)))))

(defn to-string
  "将算式代码展现为普通的中缀算式文本"
  [equation]
  (let [[opt left right] equation
        opt-str (get opt-table opt)
        left-str (ets-left-helper opt left)
        right-str (ets-right-helper opt right)]
    (format "%s%s%s" left-str opt-str right-str)))

(defn to-code
  "将算式代码展现为等价的代码文本"
  [equation]
  (cond
    (= + equation) "+"
    (= - equation) "-"
    (= * equation) "*"
    (= / equation) "/"
    (number? equation) (str equation)
    (list? equation) (format "(%s)" (s/join " " (map to-code equation)))
    :else (throw (IllegalArgumentException. (format "unknwon form %s" equation)))))
