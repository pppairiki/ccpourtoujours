# Spring Cloud

## 服务治理（服务注册与发现）

### 角色

1. 服务提供者（provider）
2. 服务消费者（consumer）
3. 注册中心（服务代理register）

### 要解决的问题

1. 服务注册
2. 服务发现
3. 服务代理信息共享
4. 健康监控

### eureka

1. 简介

   ​	eureka事Netflix开发的服务发现组件。eureka是纯正的servlet应用，使用了Jersey框架实现自身的Restful http接口。peer之间的同步与服务注册均通过http协议实现。定时任务（发送心跳、定时清理过期服务、节点同步等）通过jdk Timer实现，内存缓存使用google guava实现。