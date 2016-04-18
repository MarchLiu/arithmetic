#!/usr/bin/env python3

import random


class Expr(object):
    def __init__(self, equation):
        self.equation = equation
        try:
            self.result = int(eval(self.equation))
        except:
            print('can\'t eval "{0}"'.format(self.equation))
    def quoted(self):
        return "({0})".format(self.equation)
    def full(self):
        return "{0}={1}".format(self.equation, self.result)

class Binary(Expr):
    def __init__(self, x, y, opt):
        self.x = x
        self.y = y
        self.opt = opt
        self.equation = "{0}{1}{2}".format(self.x.equation, self.opt, self.y.equation)
        if opt == "/":
            if (type(self.y) is Binary):
                self.equation = "{0}{1}{2}".format(self.x.equation, self.opt, self.y.quoted())
        if opt in ["*", "/", "-"]:
            if (type(self.y) is Binary) and self.y.opt in ["+", "-"]:
                self.equation = "{0}{1}{2}".format(self.x.equation, self.opt, self.y.quoted())
        Expr.__init__(self, self.equation)
    def pair(self):
        return self.x, self.y

class Atom(Expr):
    def __init__(self, x):
        self.x = x
        self.equation = "{0}".format(self.x)
        Expr.__init__(self, self.equation)

multi_chart = {}
div_valid = {}

for i in range(1, 10):
    for j in range(1, 10):
        expr = Binary(Atom(i), Atom(j), "*")
        multi_chart[(i, j)] = expr
        div_valid[expr.result] = expr

opts = ["+", "-", "*", "/"]

def create_binary():
    opt = random.choice(opts)
    if opt == "+":
        x = random.randint(0, 100)
        left = Atom(x)
        right = Atom(random.randint(0, 100-x))
        return Binary(left, right, opt)
    elif opt == "-":
        x = random.randint(0, 100)
        left = Atom(x)
        right = Atom(random.randint(0, x))
        return Binary(left, right, opt)
    elif opt == "*":
        return random.choice(list(multi_chart.values()))
    elif opt == "/":
        return random.choice(list(div_valid.values()))

def eval_binary(result, local_opts = None):
    if local_opts == None:
        local_opts = opts
    opt = random.choice(local_opts)
    if opt == "+":
        x = random.randint(0, result)
        left = Atom(x)
        right = Atom(result-x)
        return Binary(left, right, opt)
    elif opt == "-":
        x = random.randint(result, 100)
        left = Atom(x)
        right = Atom(x-result)
        return Binary(left, right, opt)
    elif opt == "*":
        if result in div_valid:
            return div_valid[result]
        else:
            next_opts = [opt for opt in local_opts if opt != "*"]
            return eval_binary(result, next_opts)
    elif opt == "/":
        if result in [1, 2, 3, 4, 5, 6, 7, 8, 9]:
            x = random.randint(1, 9)
            right = Atom(x)
            left = Atom(x*result)
            return Binary(left, right, opt)
        else:
            next_opts = [opt for opt in local_opts if opt != "/"]
            return eval_binary(result, next_opts)

def create_expr(times):
    if times < 1:
        raise "等式的运算次数太少，必须大于一"
    elif times == 1:
        return create_binary()

    opt = random.choice(opts)
    if opt == "+":
        left = Atom(random.randint(0, 100))
        while True:
            right = create_expr(times-1)
            expr = Binary(left, right, opt)
            if expr.result <= 100:
                return expr
    elif opt == "-":
        left = Atom(random.randint(0, 100))
        while True:
            right = create_expr(times-1)
            expr = Binary(left, right, opt)
            if expr.result >= 0:
                return expr
    elif opt == "*":
        left = Atom(random.randint(0, 9))
        while True:
            right = create_expr(times-1)
            if 0 <= right.result <= 9:
                return Binary(left, right, opt)
    elif opt == "/":
        expr = random.choice(list(div_valid.values()))
        while True:
            if times-1 == 1:
                y = eval_binary(expr.y.x)
                return Binary(Atom(expr.result), y, opt)
            print("get {0} and find the div equation {1}".format(expr.full(), expr.y.equation))
            y = create_expr(times-1)
            print("found {0}".format(y.full()))
            if y.result == expr.y:
                return Binary(expr.result, y, opt)
    else:
        raise "不支持运算符（{0}）".format(opt)

def eval_expr(result, times, local_opts=None):
    if local_opts == None:
        local_opts = opts
    if times < 1:
        raise "等式的运算次数太少，必须大于一"
    elif times == 1:
        return eval_binary(result, local_opts)

    opt = random.choice(local_opts)
    if opt == "+":
        x = random.randint(0, result)
        left = Atom(x)
        right = eval_expr(result-x, times-1, local_opts)
        return Binary(left, right, opt)
    elif opt == "-":
        x = random.randint(result, 100)
        left = Atom(x)
        right = eval_expr(x-result, times-1, local_opts)
        return Binary(left, right, opt)
    elif opt == "*":
        if result in div_valid:
            left = div_valid[result].x
            y = left.x
            right = eval_expr(y, times-1, local_opts)
            return Binary(left, right, opt)
        else:
            next_opts = [opt for opt in local_opts if opt != "*"]
            return eval_expr(result, times-1, next_opts)
    elif opt == "/":
        if result in [1, 2, 3, 4, 5, 6, 7, 8, 9]:
            x = random.randint(1, 9)
            right = Atom(x)
            left = eval_expr(x*result, times-1, local_opts)
            return Binary(left, right, opt)
        else:
            next_opts = [opt for opt in local_opts if opt != "/"]
            return eval_expr(result, times-1, next_opts)
    else:
        raise "不支持运算符（{0}）".format(opt)

# while True:
#     expr = create_expr(2)
#     print("{0}=?".format(expr.equation))
#     while True:
#         result = input()
#         if str(expr.result) == result:
#             print("答对啦！{0}".format(expr.full()))
#             break
#         else:
#             print("不对，{0} 不等于 {1}，再想想？".format(expr.equation, result))

while True:
    result = random.randint(1, 100)
    expr = eval_expr(result, 2)
    print("{0}=?".format(expr.equation))
    while True:
        result = input()
        if str(expr.result) == result:
            print("答对啦！{0}".format(expr.full()))
            break
        else:
            print("不对，{0} 不等于 {1}，再想想？".format(expr.equation, result))
