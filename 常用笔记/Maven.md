# 1、为什么需要maven？

开发传统的java web项目

需要mysql jar包了怎么办？

只能去网上招，忍着一直在加载的网页，去mysql官网一顿摸索，重要找到了，发现版本下错了。。。。。或者在网上求助，哪位大佬有mysql jar包吗？运气好可能会等来一个百度云的链接，在百度云的龟速下，历经九九八十一难，终于得到了需要的jar包。

一个项目不可能只需要引入一个jar包，spring、mybatis、连接池。。。。。。等等。

而且不仅仅是数量上的问题，jar包之间也有可能相互依赖，例如

A.jar 依赖 B.jar， B.jar 依赖C.jar

为了使用A这一个包，我可能要去招很多很多个包。

总结一下有哪些需求

1、需要有一个jar包仓库，从这个仓库中我们可以得到任何想要的包。

2、当我们需要A的时候，可以自动下载A所依赖的包。

当然，这些需求对于maven来说都是小意思，maven不仅可以下载jar包，还可以测试代码的正确性、编译代码，直接生成我们所需要的jar包或者war包。

总的来收，我们只需要告诉他我们需要哪些jar包，然后专注于代码的实现，其余的一些列工作都可以交给maven。

