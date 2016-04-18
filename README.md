# ARITHMETIC

给小朋友练口算的小程序，顺便熟悉一下 clojure 技术链。

## 安装与运行

其实，并没有什么需要配置的。很常规的基于 lein 的 clojure 的体系。为了 emacs cider 支持加了
点东西。如果下载了源码，可以在项目目录中运行 `lein ring server` ，或者在 `lein repl` 中调用
`(use 'me.marsliu.arithmetic)` 加载调试用的进程来运行。

不用在意那个域名，我并不拥有它……

项目简单的用了一个 sqlite3 ，如果本地没找到数据库文件，会自动建立一个。

项目需要 jdk，理论上说 1.6+  即可，我个人用的是 Java 8 。

github 现在不提供 release 下载服务了？如果要自己打包的话，可以执行 `lein ring uberjar` ，切记
不要用 lein 自己的 ubuerjar 命令。

如果使用 jar ，可以运行 `java -jar arithmetic-1.0.0-standalone.jar` 启动，它会监听本机所有 ip，
默认端口是 3001 ，所以给小朋友一个平板或者手机，我就可以开着电脑做我的事情去了。

如果设置了环境变量 PORT ，程序会监听 PORT 指定的端口。

## 部署问题

这个程序只是给自己家小孩子弄个玩具。所以并没有按照一个真正的互联网项目去开发，很多地方都不完备。
测试也写的很不完整，完全不建议放到外网上。如果有哪位朋友需要放在外网用，请自取代码后添加登录、
用户管理和参数配置。

数据中其实记录了题目创建的时间和完成的时间，可以通过这些信息统计小朋友的学习情况。但是目前没有
界面去查看这些东西。数据库设计的也很潦草。其实如果不是为了熟悉一下相关的数据库工具，写两个个日志
文件足够了。

## 配置

因为是给自己家孩子写的，所以定位就在二年级小朋友的程度，两到四个自然数的四则运算，不涉及小数，
不涉及九九乘法表之外的乘除法，最大的数字不超过两百。这些可以在代码里调整，相关参数在
`me.marsliu.arithmetic.equation` 文件中。

`me.marsliu.arithmetic.question` 文件中有答题历史的分页参数，因为是个写着玩的玩具，其实界面上也
还没有分页。

## 原理和相关知识

程序只是在预设的数值和长度范围内取随机数，然后按照这两个随机数逆推出算式，并不复杂。原以为会用到
core.logic 和 clj-parsec ，结果并没有。显示时是否加括号的判断也是手工写出来的。稍微复杂一点的 CS
本科一年级课程就超过这个项目的复杂度了，所以在算法方面么，恐怕参考价值确实不高。

项目中用到了 ring/compojure/enlive/jquery/bootstrap ，为了无刷新， enlive 的逻辑最后都移到了前端
代码中。并不复杂。当然，交互设计也很不完整。如果说有什么参考价值，大概就是初步了解一下基于 clojure 
的 web 开发技术链吧。
