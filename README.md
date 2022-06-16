<p align="center">
	<a target="_blank" href="https://github.com/gngpp/mayi/blob/main/LICENSE">
		<img src="https://img.shields.io/badge/license-MIT-blue.svg" ></img>
	</a>
	<a target="_blank" href="https://github.com/1976/mayi">
		<img src="https://img.shields.io/badge/version-2.6.1-brightgreen.svg" ></img>
	</a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img src="https://img.shields.io/badge/JDK-17+-green.svg" ></img>
	</a>
</p>


### 项目描述
> 基于**RBAC模型**的系统核心设计，**OAuth2**认证的基础上拓展打造分布式微服务开发脚手架，实现主要功能：RBAC权限管理、**OAuth2.1**认证管理、安全管理、服务监控等。

### 主要技术栈
- 开发框架：Spring Boot 2.6.1
- 微服务框架：Spring Cloud 2021.0.0
- 认证服务器：Authorization Server 0.2.1
- 安全框架：Spring Security 5.5.0
- 持久层框架：MyBatis-Plus 3.3.1
- 数据库连接池：Druid、Hikari
- 服务注册与发现: Nacos
- 客户端负载均衡：Ribbon
- 服务监控：Spring Boot Actuator、Spring boot Admin
- 网关组件：Spring Gateway
- 运行容器：Undertow

#### 运行/Run
```shell
$ git clone https://github.com/gngpp/mayi.git && cd mayi
```

- 本地运行
> Run前提需要安装中间件：`MySQL`、`Redis`、`Nacos`, 运行顺序 Redis\MySQL\Nacos。根目录`bootstrap.yml`需要指定`dev`环境，之后再启动系统每个服务。
```shell
$ ./gradlew task mayi-gateway:bootRun  --args="--spring.config.additional-location=$(pwd)/bootstrap.yml"   
$ ./gradlew task mayi-auth:bootRun  --args="--spring.config.additional-location=$(pwd)/bootstrap.yml"        
$ ./gradlew task mayi-upms:mayi-upms-biz:bootRun  --args="--spring.config.additional-location=$(pwd)/bootstrap.yml"       
$ ./gradlew task mayi-visual:mayi-visual-admin:bootRun   --args="--spring.config.additional-location=$(pwd)/bootstrap.yml"    
```

- Docker运行
> 本地环境测试，只运行`MySQL`、`Redis`、`Nacos`，根目录`bootstrap.yml`需要指定`dev`环境
```shell
$ docker-compose -f docker-compose-test.yml up -d
$ ./gradlew task mayi-gateway:bootRun  --args="--spring.config.additional-location=$(pwd)/bootstrap.yml"   
$ ./gradlew task mayi-auth:bootRun  --args="--spring.config.additional-location=$(pwd)/bootstrap.yml"        
$ ./gradlew task mayi-upms:mayi-upms-biz:bootRun  --args="--spring.config.additional-location=$(pwd)/bootstrap.yml"       
$ ./gradlew task mayi-visual:mayi-visual-admin:bootRun   --args="--spring.config.additional-location=$(pwd)/bootstrap.yml"    
```

> 本地环境开发（包含所有服务），需要先本地构建项目所有服务jar包，根目录`bootstrap.yml`需要指定`prod`环境
```shell
$ ./gradlew task clean
$ ./gradlew task bootJar 
$ docker-compose -f docker-compose-dev.yml up -d
```

> 正式环境发布（包含所有服务），过程：源码-成品-运行，根目录`bootstrap.yml`需要指定`prod`环境
```shell
$ docker-compose -f docker-compose-prod.yml up -d
```

### 配套前端Nodejs服务
[mayi-web](https://github.com/gngpp/mayi-web/tree/dev)
## 贡献

感谢[Jetbrains](https://www.jetbrains.com/?from=mayi)制作的IDE，以及免费的开源许可证。

![](https://raw.githubusercontent.com/wkgcass/vproxy/master/doc/jetbrains.png)
