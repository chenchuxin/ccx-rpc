# ccx-rpc

#### 介绍
这是一个基于 Netty + Zookeeper + Protostuff 的简易 RPC 框架。
造轮子主要是为了学习，因为我觉得"会用"、"会读源码"、"会写出来"是完全不一样的水平。

github: https://github.com/chenchuxin/ccx-rpc

gitee: https://gitee.com/imccx/ccx-rpc

#### 目录
以下是重要的包的简介：
```
|- ccx-rpc-common：基础的代码
  |- extendsion：扩展，主要实现了一套自己的 SPI，参考 dubbo，做了简化
  |- url: 同样也是参考 dubbo 的 URL，一般是构建参数用的
|- ccx-rpc-core: rpc 核心逻辑
  |- annotation：里面包含了一些自定义的注解，例如 @RpcService(服务提供)、@RpcReference(服务引用)
  |- compress: 压缩，网络传输需要压缩数据
  |- config: 定义了一套配置的接口，例如配置服务绑定的端口，zk 的地址等
  |- proxy: 代理，用于客户端代理，客户端调用服务接口，实际上是一个网络请求的过程
  |- registry: 注册中心，例如 zk 注册中心
  |- remoting: 网络相关的东西，例如自定义协议、Netty 收发请求等
  |- serialize: 序列化，网络传输，序列化是必不可少了
  |- spring: 一些 spring 相关的东西，例如扫描器、bean 的处理
|- ccx-rpc-demo: 框架的使用例子
  |- ccx-rpc-demo-client: 客户端，服务引用方
  |- ccx-rpc-demo-service: 服务提供方
```

#### 功能列表
- [x] 自定义 SPI 扩展
- [x] 注册中心
    - [x] Zookeeper
    - [ ] Eureka
    - [ ] Nacos
    - [ ] Consul
    - [ ] ...
- [x] 序列化
    - [x] Protostuff
    - [ ] Kryo
    - [ ] ...
- [x] 压缩
    - [x] gzip
    - [ ] ...
- [x] 远程通信
    - [x] Netty
    - [ ] Mina
    - [ ] ...
- [x] 配置
    - [x] JVM 参数配置
    - [ ] Spring 配置文件配置
    - [ ] Apollo 动态配置
- [x] 负载均衡
    - [x] 随机策略
    - [x] 轮询策略
    - [ ] 一致性哈希
- [ ] 集群容错
- [ ] 优雅停机
- [ ] 监控后台
- [ ] 线程模型
- [ ] 多版本
- [ ] 服务分组
- [ ] 过滤器

#### 运行
1. 环境要求：JDK8 以上
2. 需要安装 zookeeper 并运行
3. 启动服务 `com.ccx.rpc.demo.service.ServiceBootstrap`，main 方法中修改 zk 地址，当然也可以去掉 `System.setProperty`，在运行参数中指定
4. 启动客户端 `com.ccx.rpc.demo.client.ClientBootstrap`，修改配置的方法跟服务提供方一样
5. 访问客户端地址 `http://localhost:8864/user/1` 就可以啦

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
