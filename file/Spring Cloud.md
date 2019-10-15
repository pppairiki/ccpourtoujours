# Spring Cloud

## 服务治理（服务注册与发现）

### 角色

1. 服务提供者（provider）
2. 服务消费者（consumer）
3. 注册中心（服务代理register）

### 解决的问题

1. 服务注册
2. 服务发现
3. 服务代理信息共享
4. 健康监控

### eureka

1. 简介

   ​	eureka事Netflix开发的服务发现组件。eureka是纯正的servlet应用，使用了Jersey框架实现自身的Restful http接口。peer之间的同步与服务注册均通过http协议实现。定时任务（发送心跳、定时清理过期服务、节点同步等）通过jdk Timer实现，内存缓存使用google guava实现。

2. eureka VS Zookeeper

   1. 从CAP定理来说，eureka数据ap型设计，zk是cp型设计，

      eureka强调高可用，zk追求强一致性。

   2. eureka不持久化只做缓存，zk做持久化。

   3. eureka通过增量更新注册信息，zk通过watch事件监控变化

   4. eureka提供客户端缓存，zk无客户端缓存。在网络隔离，注册中心访问不到的情况下，eureka可能返回几分钟之前可用的服务信息。

   综上，eureka适合作为服务注册发现中心，zk适合更广泛的分布式协调服务。

3. 架构

   ![1571111703952](C:\Users\riki\AppData\Roaming\Typora\typora-user-images\1571111703952.png)

   - 蓝色节点表示eureka注册中心
   - 黄色节点表示服务提供者
   - 淡黄色节点表示服务消费者

   从图中可以看出provider向register注册自身服务，consumer从register获取注册的服务信息进行远程服务调用，各register之间通过复制注册信息做到最终一致性。详细流程后文介绍

4. eureka 服务注册与发现流程

   ![1571111874066](C:\Users\riki\AppData\Roaming\Typora\typora-user-images\1571111874066.png)

   **provider：** eureka每个服务都会生成自己的InstanceInfo,包括appName,instanceId,port等等实例信息。

   1. 启动后向**register**注册自身服务（instanceinfo）
   2. 每隔eureka.instance.lease-renewal-interval-in-seconds时间后向**register**发送心跳（renew）
   3. 每隔eureka.client.instance-info-replication-interval-seconds（一般不配置，因为实例信息基本不会更新）检查本地实例信息是否过期，如果过期通过register()接口向**register**更新InstanceInfo

5. eureka 自我保护机制