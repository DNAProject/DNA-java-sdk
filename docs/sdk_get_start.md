<h1 align="center"> Java sdk 使用说明 </h1>


本文大纲如下：
* [快速开始](#快速开始)
	* [1. 公私钥和地址](#1-公私钥和地址)
		* [1.1 公私钥存储](#11-公私钥存储)
			* [1.1.1 自己存储：](#111-自己存储)
				* [随机创建账号：](#随机创建账号)
				* [根据私钥创建账号](#根据私钥创建账号)
			* [1.1.2 按钱包规范存储：](#112-按钱包规范存储)
		* [1.2 地址](#12-地址)
	* [2. 原生资产转账](#2-原生资产转账)
		* [2.1 初始化](#21-初始化)
		* [2.2 查询](#22-查询)
			* [ 查询ont，ong余额](#查询ontong余额)
			* [ 查询交易是否在交易池中](#查询交易是否在交易池中)
			* [ 查询交易是否调用成功](#查询交易是否调用成功)
			* [ 同步查询智能合约event](#同步查询智能合约event)
			* [其他与链交互接口列表：](#其他与链交互接口列表)
		* [2.3 交易反序](#23-交易反序)	
		* [2.4 GAS转账](#24-GAS转账)
			* [ 构造转账交易并发送](#构造转账交易并发送)
			* [ 多次签名](#多次签名)
			* [ 一转多或多转多](#一转多或多转多)
			* [使用签名机签名](#使用签名机签名)
		* [2.5 GAS转账](#25-GAS转账)
			* [ GAS转账](#GAS转账)
			* [ 提取GAS](#提取GAS)
	* [3. OEP4转账](#3-OEP4转账)
		* [3.1 查询](#31-查询)
		* [3.2 转账](#32-转账)
	* [4. 批量交易](#4-批量交易)
		* [4.1 批量构造交易](#41-批量构造交易)
		* [4.2 批量发送交易](#42-批量发送交易)
		* [4.3 在钱包中创建Dnaid](#43-在钱包中创建Dnaid)
		
# 快速开始

在公私钥对创建前，请先熟悉区块链接口，如果对数字身份和智能合约感兴趣，请参考对应说明。
 
区块链接口请参考：
    - [区块链节点接口](basic.md)。	
数字身份请参考：
    - [数字身份](dnaid.md)
    - [可信声明](dnaid_claim.md)
智能合约请参考：
    - [智能合约部署和调用](smartcontract.md)
    
## 1. 公私钥和地址

账户是基于公私钥创建的，地址是公钥转换而来。

###  1.1 **公私钥存储**

公私钥存储可以根据情况存储在自己数据库中，也可以根据钱包规范存储在文件中。

#### 1.1.1 自己存储：

自己存储，是指账户信息保存在用户数据库或其他地方，而不存储在遵循钱包规范的文件中。
#####  随机创建账号：
```java
com.github.DNAProject.account.Account acct = new com.github.DNAProject.account.Account(dnaSdk.defaultSignScheme);
acct.serializePrivateKey();//私钥
acct.serializePublicKey();//公钥
acct.getAddressU160().toBase58();//base58地址
```            
##### 根据私钥创建账号            
```java     
com.github.DNAProject.account.Account acct0 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey0), dnaSdk.defaultSignScheme);
com.github.DNAProject.account.Account acct1 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey1), dnaSdk.defaultSignScheme);
com.github.DNAProject.account.Account acct2 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey2), dnaSdk.defaultSignScheme);

```

#### 1.1.2 按钱包规范存储：

账户和身份信息保持在遵循钱包规范的文件中。[例子](https://github.com/DNAProject/DNA-java-sdk/blob/master/src/main/java/example/wallet/WalletDemo.java) 




#### 在钱包中批量创建账号:

一个创建多个账号的方法。
```java
dnaSdk.getWalletMgr().createAccounts(10, "passwordtest");
dnaSdk.getWalletMgr().writeWallet();

随机创建:
AccountInfo info0 = dnaSdk.getWalletMgr().createAccountInfo("passwordtest");

通过私钥创建:
AccountInfo info = dnaSdk.getWalletMgr().createAccountInfoFromPriKey("passwordtest","e467a2a9c9f56b012c71cf2270df42843a9d7ff181934068b4a62bcdd570e8be");

获取账号
com.github.DNAProject.account.Account acct0 = dnaSdk.getWalletMgr().getAccount(info.addressBase58,"passwordtest",salt);

```




###  1.2 **地址**


包括单签地址和多签地址,生成方式与NEO地址相同。

签地址是由一个公钥转换而来，多签地址是由多个公钥转换而来。

```java
单签地址生成：
String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";

//生成账号，获取地址
com.github.DNAProject.account.Account acct0 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey0), dnaSdk.defaultSignScheme);
Address sender = acct0.getAddressU160();

//base58地址解码
sender = Address.decodeBase58("AVcv8YBABi9m6vH7faq3t8jWNamDXYytU2")；

多签地址生成：
Address recvAddr = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());


```


| 方法名 | 参数 | 参数描述 |
| :--- | :--- | :--- |
| addressFromMultiPubkeys | int m,byte[]... pubkeys | 最小验签个数(<=公钥个数)，公钥 |

## 2. 原生资产转账

参考例子：[例子](https://github.com/DNAProject/DNA-java-sdk/blob/master/src/main/java/example/transaction/MakeTxWithoutWalletDemo.java)


### 2.1 初始化

转账之前需要创建SDK实例，配置节点IP。

```java

String ip = "http://127.0.0.1";
String rpcUrl = ip + ":" + "20336";
DnaSdk dnaSdk = DnaSdk.getInstance();
dnaSdk.setRpc(rpcUrl);
dnaSdk.setDefaultConnect(wm.getRpc());

或使用restful：
String restUrl = ip + ":" + "20334";
dnaSdk.setRestful(restUrl);
dnaSdk.setDefaultConnect(wm.getRestful());

也可以选择websocket：
String wsUrl = ip + ":" + "20335";
dnaSdk.setWesocket(wsUrl, lock);
dnaSdk.setDefaultConnect(wm.getWebSocket());

```


### 2.2 查询

当发完交易之后可能需要查询交易是否已经落账，还可能需要查询账户余额。

####  **查询gas余额**

```java
dnaSdk.getConnect().getBalance("AVcv8YBABi9m6vH7faq3t8jWNamDXYytU2");
dnaSdk.nativevm().gas().queryBalanceOf("AVcv8YBABi9m6vH7faq3t8jWNamDXYytU2")

查gas信息：
System.out.println(dnaSdk.nativevm().gas().queryName());
System.out.println(dnaSdk.nativevm().gas().querySymbol());
System.out.println(dnaSdk.nativevm().gas().queryDecimals());
System.out.println(dnaSdk.nativevm().gas().queryTotalSupply());



```

#### **查询交易是否在交易池中**

通过接口查询交易是否在交易池中
```json

dnaSdk.getConnect().getMemPoolTxState("d441a967315989116bf0afad498e4016f542c1e7f8605da943f07633996c24cc")


response 交易池存在此交易:

{
    "Action": "getmempooltxstate",
    "Desc": "SUCCESS",
    "Error": 0,
    "Result": {
        "State":[
            {
              "Type":1,
              "Height":744,
              "ErrCode":0
            },
            {
              "Type":0,
              "Height":0,
              "ErrCode":0
            }
       ]
    },
    "Version": "1.0.0"
}

或 交易池不存在此交易

{
    "Action": "getmempooltxstate",
    "Desc": "UNKNOWN TRANSACTION",
    "Error": 44001,
    "Result": "",
    "Version": "1.0.0"
}

```


#### **查询交易是否调用成功**

查询智能合约推送内容，代表交易执行成功，如果没有成功```States```中不会有```transfer```的事件。

```json
dnaSdk.getConnect().getSmartCodeEvent("d441a967315989116bf0afad498e4016f542c1e7f8605da943f07633996c24cc")


response:
{
    "Action": "getsmartcodeeventbyhash",
    "Desc": "SUCCESS",
    "Error": 0,
    "Result": {
        "TxHash": "20046da68ef6a91f6959caa798a5ac7660cc80cf4098921bc63604d93208a8ac",
        "State": 1,
        "GasConsumed": 0,
        "Notify": [
            {
                "ContractAddress": "ff00000000000000000000000000000000000001",
                "States": [
                    "transfer",
                    "T9yD14Nj9j7xAB4dbGeiX9h8unkKHxuWwb",
                    "TA4WVfUB1ipHL8s3PRSYgeV1HhAU3KcKTq",
                    1000000000
                ]
            }
        ]
    },
    "Version": "1.0.0"
}

```

根据块高查询智能合约事件，返回有事件的交易hash


```json

dnaSdk.getConnect().getSmartCodeEvent(10)

response:
{
    "Action": "getsmartcodeeventbyheight",
    "Desc": "SUCCESS",
    "Error": 0,
    "Result": [{
	"GasConsumed": 0,
	"Notify": [{
		"States": ["transfer", "AFmseVrdL9f9oyCzZefL9tG6UbvhPbdYzM", "APrfMuKrAQB5sSb5GF8tx96ickZQJjCvwG", 1000000000],
		"ContractAddress": "0100000000000000000000000000000000000000"
	}],
	"TxHash": "b8a4f77e19fcae04faa576fbc71fa5a9775166d4485ce13f1ba5ff30ce264c52",
	"State": 1
     }, {
	"GasConsumed": 0,
	"Notify": [{
		"States": ["transfer", "AFmseVrdL9f9oyCzZefL9tG6UbvhPbdYzM", "AFmseVrdL9f9oyCzZefL9tG6UbvhUMqNMV", 1000000000000000000],
		"ContractAddress": "0200000000000000000000000000000000000000"
	}],
	"TxHash": "7e8c19fdd4f9ba67f95659833e336eac37116f74ea8bf7be4541ada05b13503e",
	"State": 1
     }, {
	"GasConsumed": 0,
	"Notify": [],
	"TxHash": "80617b4a97eb4266e5e38886f234f324d57587362b5039a01c45cf413461f53b",
	"State": 1
     }, {
	"GasConsumed": 0,
	"Notify": [],
	"TxHash": "ede7ecc6e4e7e699b8ba1f07f2e5f8af3b65e70f126d82f7765d20a506080d2d",
	"State": 0
}],
    "Version": "1.0.0"
}

```

#### **同步查询智能合约event**

发完交易后，直到查到交易才返回。

```json
//发完交易每隔3秒请求一次，最长等待60秒

Object object = dnaSdk.getConnect().waitResult(tx.hash().toString());
System.out.println(object);

response success:
{
	"GasConsumed": 0,
	"Notify": [],
	"TxHash": "cb9e0d4a7a4aea0518bb39409613b8ef76798df3962feb8f8040e05329674890",
	"State": 1
}

response fail,reject by txpool:
com.github.DNAProject.sdk.exception.SDKException: {"Action":"getmempooltxstate","Desc":"UNKNOWN TRANSACTION","Error":44001,"Result":"","Version":"1.0.0"}

```

#### 其他与链交互接口列表：

与链交互还有如下接口。
```

      |                     Main   Function                      |           Description            
 -----|----------------------------------------------------------|---------------------------------------------
    1 | dnaSdk.getConnect().getNodeCount()                       |  查询节点数量
    2 | dnaSdk.getConnect().getBlock(15)                         |  查询块
    3 | dnaSdk.getConnect().getBlockJson(15)                     |  查询块    
    4 | dnaSdk.getConnect().getBlockJson("txhash")               |  查询块    
    5 | dnaSdk.getConnect().getBlock("txhash")                   |  查询块     
    6 | dnaSdk.getConnect().getBlockHeight()                     |  查询当前块高
    7 | dnaSdk.getConnect().getTransaction("txhash")             |  查询交易                                     
    8 | dnaSdk.getConnect().getStorage("contractaddress", key)   |  查询智能合约存储
    9 | dnaSdk.getConnect().getBalance("address")                |  查询余额
   10 | dnaSdk.getConnect().getContractJson("contractaddress")   |  查询智能合约          
   11 | dnaSdk.getConnect().getSmartCodeEvent(59)                |  查询智能合约事件
   12 | dnaSdk.getConnect().getSmartCodeEvent("txhash")          |  查询智能合约事件
   13 | dnaSdk.getConnect().getBlockHeightByTxHash("txhash")     |  查询交易所在高度
   14 | dnaSdk.getConnect().getMerkleProof("txhash")             |  获取merkle证明
   15 | dnaSdk.getConnect().sendRawTransaction("txhexString")    |  发送交易
   16 | dnaSdk.getConnect().sendRawTransaction(Transaction)      |  发送交易
   17 | dnaSdk.getConnect().sendRawTransactionPreExec()          |  发送预执行交易
   18 | dnaSdk.getConnect().getAllowance("gas","from","to")      |  查询允许使用值
   19 | dnaSdk.getConnect().getMemPoolTxCount()                  |  查询交易池中交易总量
   20 | dnaSdk.getConnect().getMemPoolTxState()                  |  查询交易池中交易状态
```  

### 2.3 交易反序

获取json格式的交易数据

```json  
http://127.0.0.1:20334/api/v1/transaction/8f4ab5db768e41e56643eee10ad9749be0afa54a891bcd8e5c45543a8dd0cf7d?raw=0

{
    "Action": "gettransaction",
    "Desc": "SUCCESS",
    "Error": 0,
    "Result": {
        "Version": 0,
        "Nonce": 391455426,
        "GasPrice": 500,
        "GasLimit": 20000,
        "Payer": "ASyx6be9APCR6BzcM81615FgBU26gqr1JL",
        "TxType": 209,
        "Payload": {
            "Code": "00c66b147af216ff3da82b999b26f5efe165de5f944ac5496a7cc814d2c124dd088190f709b684e0bc676d70c41b37766a7cc80800ca9a3b000000006a7cc86c51c1087472616e736665721400000000000000000000000000000000000000010068164f6e746f6c6f67792e4e61746976652e496e766f6b65"
        },
        "Attributes": [],
        "Sigs": [
            {
                "PubKeys": [
                    "0369d1e9a5a1d83fa1798bbd162e8d8d8ef8e4e1a0e03aa2753b472943e235e219"
                ],
                "M": 1,
                "SigData": [
                    "017b80d5f0826b52b2037ee564be55f0ada1d0cb714a80967deb2d04b49a59f6c4358c57d06ee8f7666aec3fc570c5251c30be1cd134acb791775de9e11cacd22c"
                ]
            }
        ],
        "Hash": "8f4ab5db768e41e56643eee10ad9749be0afa54a891bcd8e5c45543a8dd0cf7d",
        "Height": 95796
    },
    "Version": "1.0.0"
}

```  

获取hex格式的交易数据
```json  
http://127.0.0.1:20334/api/v1/transaction/8f4ab5db768e41e56643eee10ad9749be0afa54a891bcd8e5c45543a8dd0cf7d?raw=1


{
    "Action": "gettransaction",
    "Desc": "SUCCESS",
    "Error": 0,
    "Result": "00d1c2225517f401000000000000204e0000000000007af216ff3da82b999b26f5efe165de5f944ac5497900c66b147af216ff3da82b999b26f5efe165de5f944ac5496a7cc814d2c124dd088190f709b684e0bc676d70c41b37766a7cc80800ca9a3b000000006a7cc86c51c1087472616e736665721400000000000000000000000000000000000000010068164f6e746f6c6f67792e4e61746976652e496e766f6b6500014241017b80d5f0826b52b2037ee564be55f0ada1d0cb714a80967deb2d04b49a59f6c4358c57d06ee8f7666aec3fc570c5251c30be1cd134acb791775de9e11cacd22c23210369d1e9a5a1d83fa1798bbd162e8d8d8ef8e4e1a0e03aa2753b472943e235e219ac",
    "Version": "1.0.0"
}

``` 

要理解交易中的数据内容，需要反序列化交易数据，才能推断出是什么类型的交易和具体信息。
``` 
//版本号    交易类型  随机数   gasprice    gaslimit              网络费付款人       交易数据 
(version(1) type(1) nonce(4) gasprice(8) gaslimit(8))22 bytes + (payer)21 bytes + payload code bytes( any bytes)

claim gas 
//             claim address                                                 contract address                         to   address                                 amount                       "transferFrom"                           gas                     SYSCALL         "*.Native.Invoke"
//00 c66b 14bb2d5b718efeac060ac825338ca440216da4d8dc 6a7cc8 140000000000000000000000000000000000000001 6a7cc8 14bb2d5b718efeac060ac825338ca440216da4d8dc 6a7cc8 08 806a735501000000 6a7cc8 6c 0c7472616e7366657246726f6d 140000000000000000000000000000000000000002 0068 164f6e746f6c6f67792e4e61746976652e496e766f6b65
gas transfer
//                     from                                           to                                        amount                                 "transfer"                                                                       gas                     SYSCALL           "*.Native.Invoke"
//00 c66b 147af216ff3da82b999b26f5efe165de5f944ac549 6a7cc8 14d2c124dd088190f709b684e0bc676d70c41b3776 6a7cc8 08 00ca9a3b00000000 6a7cc8 6c 51c1 087472616e73666572                                                      140000000000000000000000000000000000000001 0068 164f6e746f6c6f67792e4e61746976652e496e766f6b65

For amount ：   1-16  is  0x51-0x60  .     >=16 is  long,  08 is the total amount bytes .  
Example: 1000 is  0xe803000000000000 -> 0x00000000000003e8   change from little endian to big endian if print.

```  

### 2.4 GAS转账

GAS转账可以一对一，也可以一对多，多对多，多对一。

#### **构造转账交易并发送**

```java
转出方与收款方地址：
Address sender = acct0.getAddressU160();
Address recvAddr = acct1;
//多签地址生成
//Address recvAddr = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());

构造转账交易：
long amount = 1000;
Transaction tx = dnaSdk.nativevm().gas().makeTransfer(sender.toBase58(),recvAddr.toBase58(), amount,sender.toBase58(),30000,0);
String hash = tx.hash().toString()

对交易做签名：
dnaSdk.signTx(tx, new com.github.DNAProject.account.Account[][]{{acct0}});
//多签地址的签名方法：
dnaSdk.signTx(tx, new com.github.DNAProject.account.Account[][]{{acct1, acct2}});
//如果转出方与网络费付款人不是同一个地址，需要添加网络费付款人的签名


发送预执行（可选）：
Object obj = dnaSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
System.out.println(obj);
成功返回：
{"State":1,"Gas":30000,"Result":"01"}
余额不足返回异常：
com.github.DNAProject.network.exception.RestfulException: {"Action":"sendrawtransaction","Desc":"SMARTCODE EXEC ERROR","Error":47001,"Result":"","Version":"1.0.0"}


发送交易：
dnaSdk.getConnect().sendRawTransaction(tx.toHexString());


同步发送交易：
Object obj = dnaSdk.getConnect().syncSendRawTransaction(tx.toHexString());

response success:
{
	"GasConsumed": 0,
	"Notify": [],
	"TxHash": "cb9e0d4a7a4aea0518bb39409613b8ef76798df3962feb8f8040e05329674890",
	"State": 1
}

response fail,reject by txpool:
com.github.DNAProject.sdk.exception.SDKException: {"Action":"getmempooltxstate","Desc":"UNKNOWN TRANSACTION","Error":44001,"Result":"","Version":"1.0.0"}

```



| 方法名 | 参数 | 参数描述 |
| :--- | :--- | :--- |
| makeTransfer | String sender，String recvAddr,long amount,String payer,long gaslimit,long gasprice | 发送方地址，接收方地址，金额，网络费付款人地址，gaslimit，gasprice |
| makeTransfer | State\[\] states,String payer,long gaslimit,long gasprice | 一笔交易包含多个转账。 |


#### **多次签名**

如果转出方与网络费付款人不是同一个地址，需要添加网络费付款人的签名

```java

1.添加单签签名
dnaSdk.addSign(tx,acct0);

2.添加多签签名
dnaSdk.addMultiSign(tx,2,new byte[][]{acct.serializePublicKey(),acct2.serializePublicKey()},acct);
dnaSdk.addMultiSign(tx,2,new byte[][]{acct.serializePublicKey(),acct2.serializePublicKey()},acct2);

3.多签签名分多次签
acct0签名：
dnaSdk.addMultiSign(tx,2,new byte[][]{acct.serializePublicKey(),acct2.serializePublicKey()},acct);

acct1签名：
dnaSdk.addMultiSign(tx,2,new byte[][]{acct.serializePublicKey(),acct2.serializePublicKey()},acct2);

```


 
#### **一转多或多转多**

1. 构造多个state的交易
2. 签名
3. 一笔交易上限为1024笔转账


```java

Address sender1 = acct0.getAddressU160();
Address sender2 = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());
int amount = 10;
int amount2 = 20;

State state = new State(sender1, recvAddr, amount);
State state2 = new State(sender2, recvAddr, amount2);
Transaction tx = dnaSdk.nativevm().ont().makeTransfer(new State[]{state1,state2},sender1.toBase58(),30000,0);

//第一个转出方是单签地址，第二个转出方是多签地址：
dnaSdk.signTx(tx, new com.github.DNAProject.account.Account[][]{{acct0}});
dnaSdk.addMultiSign(tx,2,new byte[][]{acct1.serializePublicKey(),acct2.serializePublicKey()},acct1);
dnaSdk.addMultiSign(tx,2,new byte[][]{acct1.serializePublicKey(),acct2.serializePublicKey()},acct2);
```

#### 使用签名机签名

 **构造交易并签名**

1. 构造交易，序列化交易，发送交易给签名机
2. 签名机接收到交易，反序列化，检查交易，添加签名
3. 发送交易

```java

序列化交易发送给签名机：
Transaction tx = dnaSdk.nativevm().gas().makeTransfer(sender.toBase58(),recvAddr.toBase58(), amount,sender.toBase58(),30000,0);
String txHex = tx.toHexString();

接收方反序列化交易并签名：

Transaction txRx = Transaction.deserializeFrom(Helper.hexToBytes(txHex));


签名：
dnaSdk.addSign(txRx,acct0);
```

**SDK与签名机交互**：

[例子](https://github.com/DNAProject/DNA-java-sdk/blob/master/src/main/java/example/signature/SignServerDemo.java)

```java
节点启动时打开签名机服务：
go run SigSvr.go


设置签名机URL：
String url = ip + ":" + "20000/cli";
DnaSdk dnaSdk = DnaSdk.getInstance();
dnaSdk.setSignServer(url);
        

String txHex = tx.toHexString();

请求单签交易：
dnaSdk.getSignServer().sendSigRawTx(txHex);
 
请求多签交易： 
String[] signs = new String[]{"02039b196d5ed74a4d771ade78752734957346597b31384c3047c1946ce96211c2a7",
                    "0203428daa06375b8dd40a5fc249f1d8032e578b5ebb5c62368fc6c5206d8798a966"};
dnaSdk.getSignServer().sendMultiSigRawTx(txHex,2,signs);

请求构造转账交易并签名：
dnaSdk.getSignServer().sendSigTransferTx("ont","TU5exRFVqjRi5wnMVzNoWKBq9WFncLXEjK","TA5SgQXTeKWyN4GNfWGoXqioEQ4eCDFMqE",10,30000,0);
            

```

 **对数据做签名**

SDK提供直接对数据做签名的接口。[例子](https://github.com/DNAProject/DNA-java-sdk/blob/master/src/main/java/example/signature/SignatureDemo.java) 


```java
com.github.DNAProject.account.Account acct = new com.github.DNAProject.account.Account(dnaSdk.defaultSignScheme);

byte[] data = "12345".getBytes();
byte[] signature = dnaSdk.signatureData(acct, data);

System.out.println(dnaSdk.verifySignature(acct.serializePublicKey(), data, signature));

```



### 2.5 GAS转账

GAS的精度是9。
####  **GAS转账**


```json
dnaSdk.nativevm().gas().makeTransfer(sender.toBase58(),recvAddr.toBase58(), amount,sender.toBase58(),30000,0);
```

####  **提取gas**

1. 查询是否有gas可以提取
2. 发送提取gas交易

```json
查询未提取gas:
String addr = acct0.getAddressU160().toBase58();
String gas = sdk.nativevm().gas().unboundGas(addr);

//提取gas
com.github.DNAProject.account.Account account = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey0), dnaSdk.signatureScheme);
String hash = dnaSdk.nativevm().gas().withdrawGas(account,toAddr,64000L,payerAcct,30000,500);

```



## 3. OEP4转账


### 3.1 查询

```
dnaSdk.neovm().oep4().setContractAddress("dff96f4a83b9cb0de3d5e05ca3dff7af841dc0f3");
System.out.println(dnaSdk.neovm().oep4().queryBalanceOf(account.getAddressU160().toBase58()));  

```
### 3.2 转账

```
dnaSdk.neovm().oep4().setContractAddress("dff96f4a83b9cb0de3d5e05ca3dff7af841dc0f3");
String txhash = dnaSdk.neovm().oep4().sendTransfer(acct1, acct2.getAddressU160().toBase58(), 1000, acct1, 20000, 500);
  
```

### 3.3 并发

实现步骤如 [例子](https://github.com/DNAProject/DNA-java-sdk/tree/master/src/main/java/example/oep/Oep4MultiTheadDemo.java)



## 4. 批量交易

SDK发送注册Dnaid和转账等交易时，根据钱包中账户和身份信息解密出私钥再做签名，这个过程大概需要1-2秒时间。为了节省发交易时间，可以多线程或多机器事先创建交易，再批量发送。

实现步骤如下，[例子](https://github.com/DNAProject/DNA-java-sdk/tree/master/src/main/java/example/transaction/CreateManyTx.java)

### 4.1 批量构造交易

1. 打开文件
2. 构造交易，下面以构造注册Dnaid交易为例。
3. 写入交易

> 构造交易时，如果新创建账户，需要用户自己保存账户私钥。

```
//open file, make registry dnaid transaction, save tx to file.
File file = new File(filePath);
if (!file.exists()) {
    file.createNewFile();
}
com.github.DNAProject.account.Account payerAcct = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey1), SignatureScheme.SHA256WITHECDSA);
FileOutputStream fos = new FileOutputStream(file);
for (int i = 0; i < 3; i++) {
    com.github.DNAProject.account.Account account = new com.github.DNAProject.account.Account(SignatureScheme.SHA256WITHECDSA);
    String dnaid = Common.diddna + account.getAddressU160().toBase58();
    Transaction tx = dnaSdk.nativevm().dnaId().makeRegister(dnaid, Helper.toHexString(account.serializePublicKey()), payerAcct.getAddressU160().toBase58(), 20000, 500);
    dnaSdk.addSign(tx, account);
    dnaSdk.addSign(tx, payerAcct);
    System.out.println("PrivateKey:"+Helper.toHexString(account.serializePrivateKey())+",txhash:"+tx.hash().toString());

    fos.write(tx.toHexString().getBytes());
    fos.write(",".getBytes());
    fos.write(tx.hash().toString().getBytes());
    fos.write("\n".getBytes());
}


```
文件中数据格式：

```
交易，交易hash
交易，交易hash

00d1df24a313f401000000000000204e000000000000aa6e06c79f864152ab7f3139074aad822ffea8559800c66b2a6469643a6f6e743a414774577a7933693148453654516e31633265636b78527841516d524662467333686a7cc821036391476eed630fc1cffb1317545d9390f22d68cdc7092095dc1b78e4baeef27c6a7cc86c127265674944576974685075626c69634b65791400000000000000000000000000000000000000030068164f6e746f6c6f67792e4e61746976652e496e766f6b6500024241011928500aa1ac40d908e92c9db7c16be4063dda2cdabe9908206747c6303635578fde0be66032f586596e91c80f490a085e612be28b95da0edb319cb60f774e472321036391476eed630fc1cffb1317545d9390f22d68cdc7092095dc1b78e4baeef27cac424101bb1df17b91cd709ce38b4ec40db10c2dfd5e9ca7219dd5ca1c6200eaf60d8ccf1be9b85b9b22398204c6366ac20e8bb7797f21ebc17e7db540627b99d5a8bb41232102df6f28e327352a44720f2b384e55034c1a7f54ba31785aa3a338f613a5b7cc26ac,b7e2c99f449cb3403619bc5c5887c52f44993180c61c9fae85d5e772ce3d7fda
00d1039e89c7f401000000000000204e000000000000aa6e06c79f864152ab7f3139074aad822ffea8559800c66b2a6469643a6f6e743a416446335547344867515864515a5577714a53756536744a6f534e564237644d32596a7cc82102a1f44af3d81c6cb0aaa5e1d597b891c436b0c724ae446b5d9cb9039e09b9938c6a7cc86c127265674944576974685075626c69634b65791400000000000000000000000000000000000000030068164f6e746f6c6f67792e4e61746976652e496e766f6b65000242410168436b2f5da6db0b2b4260b587ba7bce3dba1597a400dec52d0f00a4aa77f37c9979d10c31a80888edd6a41da4c89596033ab9ee634886f26850b32e83681f4e232102a1f44af3d81c6cb0aaa5e1d597b891c436b0c724ae446b5d9cb9039e09b9938cac4241019ba8633b42d3427ab202cafcb1c50a65de30c65120a96d958f985aff7205fcc6230e64842ea5cdf4e6b2d0b8bef1b19c795bf392a0c1cc8a93bc9ec9bebc9a35232102df6f28e327352a44720f2b384e55034c1a7f54ba31785aa3a338f613a5b7cc26ac,a8bd8258c8819f0ecf0652bb1d8c08860a5b75d16e194f3d6cd395f5c2a9f7fe
00d1554170d9f401000000000000204e000000000000aa6e06c79f864152ab7f3139074aad822ffea8559800c66b2a6469643a6f6e743a41547a4d6e79556d42614369686355556872786472566d437173336d73426351685a6a7cc8210274af46f5f038d040e5096f4653df4e7ea5598567effe4542ad6f19cda6ea05846a7cc86c127265674944576974685075626c69634b65791400000000000000000000000000000000000000030068164f6e746f6c6f67792e4e61746976652e496e766f6b650002424101ea9d7dedf17ecfef8d1d03f4d00b819c16fab8275b5d77d811a1db8140d25722fc2200a60ea76e5a449cce4e127ceec73377cce1b227c624b8df9f95be55bf4123210274af46f5f038d040e5096f4653df4e7ea5598567effe4542ad6f19cda6ea0584ac424101e401877fc4892212a309b7de30d5d9dd675a7171f89d945fe44c3b9eba46df2c2966f54fefdf5eabfa651acf63332cf9475868aa3055531a97a1fffbd43b09b8232102df6f28e327352a44720f2b384e55034c1a7f54ba31785aa3a338f613a5b7cc26ac,bd888014a82cec39f72400e4b8c43e46702bd96a16172b144361cb7b3bc024f4

```

### 4.2 批量发送交易

1. 打开文件
2. 读取一行数据
3. 提取交易数据，发送交易数据

```
//read transaction from file, send transaction to node
FileReader fr = new FileReader(filePath);
BufferedReader bf = new BufferedReader(fr);
String txHex = null;
while ((txHex=bf.readLine())!=null){
    txHex = txHex.split(",")[0];
    Object obj = dnaSdk.getConnect().sendRawTransactionPreExec(txHex);//change to sendRawTransaction
    System.out.println(obj);
}


```


### 4.3 在钱包中创建Dnaid

如果需要把Dnaid保存到钱包，根据4.1中保存的私钥，在钱包中创建Dnaid即可。

```
Identity identity = dnaSdk.getWalletMgr().createIdentityFromPriKey(password,privatekey0);
dnaSdk.getWalletMgr().writeWallet();
```