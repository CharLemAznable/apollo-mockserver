### apollo-mockserver

[![Build](https://github.com/CharLemAznable/apollo-mockserver/actions/workflows/build.yml/badge.svg)](https://github.com/CharLemAznable/apollo-mockserver/actions/workflows/build.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.charlemaznable/apollo-mockserver/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.charlemaznable/apollo-mockserver/)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)
![GitHub code size](https://img.shields.io/github/languages/code-size/CharLemAznable/apollo-mockserver)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=alert_status)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)

[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=bugs)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=security_rating)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=sqale_index)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=code_smells)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)

[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=ncloc)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=coverage)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_apollo-mockserver&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=CharLemAznable_apollo-mockserver)

Apollo配置Mock服务 JUnit5扩展.

##### Maven Dependency

```xml
<dependency>
  <groupId>com.github.charlemaznable</groupId>
  <artifactId>apollo-mockserver</artifactId>
  <version>2022.0.7</version>
</dependency>
```

##### Maven Dependency SNAPSHOT

```xml
<dependency>
  <groupId>com.github.charlemaznable</groupId>
  <artifactId>apollo-mockserver</artifactId>
  <version>2022.0.8-SNAPSHOT</version>
</dependency>
```

#### MockApolloServer启动/停止

```java
MockApolloServer.setUpMockServer();

// ...

MockApolloServer.tearDownMockServer();
```

MockApolloServer将读取类路径下```mockdata-{XXX}.properties```文件作为配置内容.

properties文件名中的```XXX```对应apollo配置的```namespace```.

properties文件中的```key```对应apollo配置的```property```.

#### 配置修改/删除/重置

```java
MockApolloServer.addOrModifyProperty("namespace", "property", "VALUE");

MockApolloServer.deleteProperty("namespace", "property");

MockApolloServer.resetOverriddenProperties();
```

注: 重置配置仅清除手动修改/删除的配置, 还原配置内容为初始化时读取的类路径下的配置内容.

#### 使用JUnit5扩展

```java
@ExtendWith(MockApolloServerForAll.class)

@ExtendWith(MockApolloServerForEach.class)
```

```MockApolloServerForAll```扩展将在```BeforeAllCallback```启动MockApolloServer, 在```AfterAllCallback```停止MockApolloServer.

```MockApolloServerForEach```扩展将在```BeforeEachCallback```启动MockApolloServer, 在```AfterEachCallback```停止MockApolloServer.
