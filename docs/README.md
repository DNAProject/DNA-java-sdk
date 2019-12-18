<h1 align="center"> DNA Java SDK 介绍 </h1>

<p align="center" class="version">Version 2.0.0 </p>


# 总体介绍

该项目是DNA官方Java SDK，它是一个综合性SDK，目前支持：本地钱包管理、数字身份管理、数字资产管理、智能合约部署和调用、与节点通信等。未来还将支持更丰富的功能和应用。

## 主要内容

- [快速开始](sdk_get_start.md)
- [区块链节点接口](basic.md)
- [数字资产](asset.md)
- [数字身份](dnaid.md)
- [可信声明](dnaid_claim.md)
- [智能合约部署和调用](smartcontract.md)
- [SDK接口描述](interface.md)
- [钱包文件及规范](Wallet_File_Specification_cn.md)
- [权限管理](auth.md)
- [治理合约](governance.md)
- [节点API文档](https://github.com/DNAProject/DNA/tree/master/docs/specifications)
- [错误码](errorcode.md)

## 代码结构说明：

* acount：账号相关操作，如生成公私钥
* common：通用基础接口
* core：核心层，包括合约、交易、签名等
* crypto：加密相关，如ECC/SM
* io：io操作
* network：restful\rpc\websocket与链通信接口
* sdk：对SDK底层做封装、Info信息、通信管理、Claim管理、钱包管理、异常类。
* dnaSdk类：提供管理器和交易实例，管理器包括：walletMgr、connManager。
    * walletMgr钱包管理器主要管理数字身份及数字资产账户，用户向链上发送交易需要私钥做签名。 
    * connManager与链上通信管理。任何发送交易和查询都需要通过连接管理器。

## 安装说明

### 请配置JDK 8的开发环境

> **注意:**  SDK用的key的长度超过128位，由于java的安全策略文件对key的长度的限制，需要下载local_policy.jar和US_export_policy.jar这两个jar包，替换JRE库${java_home}/jre/lib/security目录下对应的jar包。

jar包下载地址：

>http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html


### Build

```
$ mvn clean install
```

### 引入依赖


```
<!-- https://mvnrepository.com/artifact/com.github.DNAProject/DNASDKJava -->
<dependency>
    <groupId>com.github.DNAProject</groupId>
    <artifactId>DNASDKJava/artifactId>
    <version>2.0.0</version>
</dependency>
```

### 预准备

* 启动[DNA节点](https://github.com/DNAProject/DNA)，无论是主网、测试网、私网都可以。确保rpc端口可以访问，并且确保SDK可以连接RPC服务器。
