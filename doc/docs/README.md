![rice_logo](../assets/rice_logo.jpg)

RICE 是一个基于MIT开源协议非云原生的分布式集群调度中间件，目前面向业务而不是数据的分布式调度框架有阿里云的SchedulerX2.0，但是并不是开源的。也有社区大牛基于SchedulerX2.0研发了PowerJob，但是power-job作者在集群容错方面并不是做的很友好，在多个调度server下，没有办法做到server均衡负载任务，也就是说任务在多个server上均衡分布，导致某一个调度服务器负载很高。 (具体细节查看我给powerJob的[issue](https://github.com/PowerJob/PowerJob/issues/360))

RICE可以实现动态任务分配，当一个server调度器挂机之后，原本在宕机服务的任务能够在集群中重新分配，由其他server调度。另外像power-job或者SchedulerX2.0引入和Spring和Akka等第三方jar。RICE做到简而美，对架构做了重新设计，尽可能的减少对第三方库的依赖。自研注册中心，在线日志，网络通讯模块，序列化，网络协议等。

另外作者把这个项目叫RICE，也是因为当时有这个想法的时候，正好是袁隆平爷爷的变故，RICE的中文名是米饭，水稻的意思，也是为了纪念这位共和国伟人。
