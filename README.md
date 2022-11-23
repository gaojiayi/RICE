# RICE
### Distributed Cluster Scheduler
[![Build Status](https://travis-ci.org/apache/incubator-dubbo.svg?branch=master)](https://travis-ci.org/apache/incubator-dubbo)
[![codecov](https://codecov.io/gh/apache/incubator-dubbo/branch/master/graph/badge.svg)](https://codecov.io/gh/apache/incubator-dubbo)
![maven](https://img.shields.io/maven-central/v/org.apache.dubbo/dubbo.svg)
![license](https://img.shields.io/github/license/alibaba/dubbo.svg)
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/apache/incubator-dubbo.svg)](http://isitmaintained.com/project/apache/incubator-dubbo "Average time to resolve an issue")
[![Percentage of issues still open](http://isitmaintained.com/badge/open/apache/incubator-dubbo.svg)](http://isitmaintained.com/project/apache/incubator-dubbo "Percentage of issues still open")
[![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=Apache%20Dubbo%20(incubating)%20is%20a%20high-performance%2C%20java%20based%2C%20open%20source%20RPC%20framework.&url=http://dubbo.incubator.apache.org/&via=ApacheDubbo&hashtags=rpc,java,dubbo,micro-service)
[![](https://img.shields.io/twitter/follow/ApacheDubbo.svg?label=Follow&style=social&logoWidth=0)](https://twitter.com/intent/follow?screen_name=ApacheDubbo)
[![Gitter](https://badges.gitter.im/alibaba/dubbo.svg)](https://gitter.im/alibaba/dubbo?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

![rice_logo](doc/assets/rice_logo.jpg)

### 介绍
RICE 是一个分布式集群调度中间件，目前面向业务而不是数据的分布式调度框架有阿里云的SchedulerX2.0，但是并不是开源的。也有社区大牛基于SchedulerX2.0研发了PowerJob，但是power-job作者在集群容错方面并不是做的很友好，在多个调度server下，没有办法做到server均衡负载任务，也就是说任务在多个server上均衡分布，导致某一个调度服务器负载很高。

RICE可以实现动态任务分配，当一个server调度器挂机之后，原本在宕机服务的任务能够在集群中重新分配，由其他server调度。另外像power-job或者SchedulerX2.0引入和Spring和Akka等第三方jar。RICE做到简而美，对架构做了重新设计，尽可能的减少对第三方库的依赖。自研注册中心，日志，网络通讯模块，序列化，网络协议等。

另外作者把这个项目叫RICE，也是因为当时有这个想法的时候，正好是袁隆平爷爷的变故，RICE的中文名是米饭，水稻的意思，也是为了纪念这位共和国伟人。


****

[RICE官网](http://www.ricescheduler.com)

****

[RICE在线demo](http://demo.ricescheduler.com)

****

[RICE开发文档](http://doc.ricescheduler.com)

****


### 开发进度
#### 1.0.0版本计划
| **处理器**  | **控制器**  | **调度器**  | **控制台**  |**其他**   |
| :------------ | :------------ | :------------ | :------------ | :------------ |
| ✔任务发布 </br>✔ 任务上线通知 </br>┅ 与spring的集成(类似dubbo)|✔ Jraft选举 </br> ✔ 任务发现通知 </br> ✔  调度器状态变更通知 </br> ✔  任务重分配通知</br>✔处理器状态维护</br>  | ✔单机任务</br>✔广播任务</br>✔Map任务</br>✔MapReduce任务</br>✔工作流任务</br>| ┅UI</br>     |  ✔ 归档 </br>  ✔ 编译打包</br>  ┅ 执行shell脚本   |      |
















