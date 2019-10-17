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

   ​	eureka是Netflix开发的服务发现组件。eureka是纯正的servlet应用，使用了Jersey框架实现自身的Restful http接口。peer之间的同步与服务注册均通过http协议实现。定时任务（发送心跳、定时清理过期服务、节点同步等）通过jdk Timer实现，内存缓存使用google guava实现。

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

   - 启动后向**register**注册自身服务（instanceinfo）

   - 每隔eureka.instance.lease-renewal-interval-in-seconds时间后向**register**发送心跳（renew）

   - 每隔eureka.client.instance-info-replication-interval-seconds（一般不配置，因为实例信息基本不会更新）检查本地实例信息是否过期，如果过期通过register()接口向**register**更新InstanceInfo

   **register：** Eureka注册中心有一个Map来保存所有的服务及映射的机器信息

   ```java
   private final ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>> registry
               = new ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>>();
   ```

   - 服务注册时，会把服务的信息写到这个registry中
   - 服务从治理中心拉取服务列表信息时，不会从这个registry中拉取，而是从一个ResponseCache中拉取，这样读写分离的原因应该是为了支持高并发。

   而ResponseCache又分为了两个部分，一个是ReadWriteMap，一个是ReadOnlyMap。

   - ReadWriteMap的数据是从registry中来的，可以认为是registry的缓存，当服务注册时，除了把信息写到registry中外，还会让ReadWriteMap主动过期，使得会去从registry重新拉取数据。
   - ReadOnlyMap的数据是从ReadWriteMap来的，可以认为是ReadWriteMap的缓存（所以它是registry缓存的缓存，双层缓存了），当服务需要获取服务列表是，会直接取这个ReadOnlyMap的数据，当这个数据不存在时，才会从ReadWriteMap中更新。
   - ReadWriteMap与registry的数据是实时一致的（因为有注册后让ReadWriteMap失效的机制），但是ReadWriteMap与ReadOnlyMap不是实时一致的。
   - 有定时任务会定时从ReadWriteMap同步到ReadOnlyMap，这个时间配置是：eureka.server.responseCacheUpdateInvervalMs
   - EurekaServer内部有定时任务，每隔检查过期实例时间，扫描Registry里面过期的实例并删除，并且使对应的ReadWriteMap缓存失效，这个时间是eureka.server.eviction-interval-timer-in-ms

   **consumer:** 

   - Service Consumer在启动时会从**register**获取所有服务列表，并在本地缓存。需要注意的是，需要确保配置eureka.client.shouldFetchRegistry=true
   - 由于在本地有一份缓存，所以需要定期更新，定期更新频率可以通过eureka.client.registryFetchIntervalSeconds配置

5. eureka 自我保护机制