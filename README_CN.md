# DNA SDK 手册



## 1 准备工作

*** 注册成为DNA 接入区块链平台用户

*** 下载DNA SKD(java版本)， 配置JAVA8运行环境

> > 注意： 配置java运行环境后运行程序时如出现如下错误：
> >
> > java.security.InvalidKeyException: Illegal key size
> >
> > 则这是秘钥长度大于128，安全策略文件受限的原因。可以去官网下载local_policy.jar和US_export_policy.jar，替换jre目录中${java_home}/jre/lib/security原有的与安全策略这两个jar即可。下载地址：
> >
> > [http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)



## 2 接入步骤

***使用注册账户中提供的AppId和AppKey，从OAuthServer申请访问令牌，可参考Oauth手册。

***调用sdk时，将该访问令牌作为其中一个初始化参数传递进来，实现数字资产的注册、分发、转移、存证、取证等操作。

`注意事项一`：

​	用户注册仅针对在联盟链或私有链有认证需求的场景下，公有链环境或者社区用户自行部署DNA的情况下无需参考Oauth手册，调用sdk时使用任意访问令牌token均可。

`注意事项二`：

​	DNA默认关闭认证选项，在有认证需求的场景下，需要将DNA认证选项打开，DNA的默认认证方式是OAuth。



## 3 参数信息说明##

调用每一个接口方法之前必须实例化账户管理器，后续的接口都是基于账户管理来调用的。实例化账户管理器所需参数包括：连接地址url，账户存储位置路径path，访问令牌accessToken。

### 3.1 创建账户###

通过传递一个int类型的数值，可以创建多个账户合约，以合约地址列表形式返回。

| 参数   | 字段       | 类型           | 说明       |
| ---- | -------- | ------------ | -------- |
| 输入参数 | numCount | int          | 单次创建账户个数 |
| 输出参数 | list     | List<String> | 账户合约地址列表 |

eg: 

List<String>  list = wm.createAccount(numCount);



### 3.2 注册资产###

通过传递资产的基本信息来产生一笔区块链上合法的资产，返回资产编号。后续的资产类操作就可以使用该资产编号。

| 参数   | 字段         | 类型     | 说明            |
| ---- | ---------- | ------ | ------------- |
| 输入参数 | issuer     | String | 发行者地址         |
|      | name       | String | 资产名称          |
|      | amount     | long   | 资产数量          |
|      | desc       | String | 描述            |
|      | controller | String | 控制者地址         |
|      | precision  | int    | 精度            |
| 输出参数 | txid       | String | 交易编号，这里代表资产编号 |

eg：

String assetid = wm.reg(issuer, name, amount , desc, controller, precision);



### 3.3 分发资产###

通过传递分发资产的基本信息来完成一笔资产的分发操作，返回交易编号。

| 参数   | 字段         | 类型     | 说明      |
| ---- | ---------- | ------ | ------- |
| 输入参数 | controller | String | 资产控制者地址 |
|      | assetid    | String | 资产编号    |
|      | amount     | long   | 分发数量    |
|      | recver     | String | 接收者地址   |
|      | desc       | String | 描述      |
| 输出参数 | txid       | String | 交易编号    |

eg:

String txid = wm.iss(controller, assetid, amount , recver , desc );



### 3.4 转移资产###

通过传递分发资产的基本信息来完成一笔资产的分发操作，返回交易编号。

| 参数   | 字段         | 类型     | 说明      |
| ---- | ---------- | ------ | ------- |
| 输入参数 | controller | String | 资产控制者地址 |
|      | assetid    | String | 资产编号    |
|      | amount     | long   | 转移数量    |
|      | recver     | String | 接收者地址   |
|      | desc       | String | 描述      |
| 输出参数 | txid       | String | 交易编号    |

eg:

String txid = wm.trf(controller, assetid, amount , recver , desc );



### 3.5 存证###

通过传递存证交易的基本信息来完成一笔资产的分发操作，返回交易编号。

| 参数   | 字段      | 类型     | 说明   |
| ---- | ------- | ------ | ---- |
| 输入参数 | content | String | 存证信息 |
|      | desc    | String | 描述   |
| 输出参数 | txid    | String | 交易编号 |

eg:

String txid = wm.storeCert(content, desc);



### 3.6 取证###

查询类操作，传递存证时的交易编号，输出具体存证内容

| 参数   | 字段     | 类型     | 说明   |
| ---- | ------ | ------ | ---- |
| 输入参数 | txid   | String | 交易编号 |
| 输出参数 | cotent | String | 存证内容 |

eg：

String content= wm.queryCert(txid);



### 3.7 账户信息###

查询类操作，传递账户地址，输出账户具体信息

| 参数   | 字段      | 类型          | 说明                             |
| ---- | ------- | ----------- | ------------------------------ |
| 输入参数 | address | String      | 合约地址                           |
| 输出参数 | info    | AccountInfo | 账户信息，包括合约地址、公钥、私钥、公钥hash、私钥wif |

eg:

AccountInfo info = wm.getAccountInfo(address);



### 3.8 账户资产###

查询类操作。传递账户地址，输出账户资产详情

| 参数   | 字段      | 类型           | 说明                                 |
| ---- | ------- | ------------ | ---------------------------------- |
| 输入参数 | address | String       | 合约地址                               |
| 输出参数 | info    | AccountAsset | 账户资产信息，包括合约地址、可用资产/冻结资产(资产编号、资产数量) |

eg:

AccountAsset info = wm.getAccountAsset(userAddr);



### 3.9 资产信息###

查询类操作。传递资产编号，输出资产详情。

| 参数   | 字段      | 类型        | 说明                          |
| ---- | ------- | --------- | --------------------------- |
| 输入参数 | assetid | String    | 资产编号                        |
| 输出参数 | info    | AssetInfo | 资产信息，包括资产编号、名称、注册数量、注册人、控制人 |

eg:

AssetInfo info = wm.getAssetInfo(assetid);



### 3.10 交易信息###

查询类操作。传递交易编号，返回交易具体信息。

| 参数   | 字段   | 类型              | 说明   |
| ---- | ---- | --------------- | ---- |
| 输入参数 | txid | String          | 交易编号 |
| 输出参数 | info | TransactionInfo | 交易信息 |

eg：

TransactionInfo info = wm.getTransactionInfo(txid);



## 4 调用示例

### 4.1 创建账户

```
// 打开账户管理器
String path = "./dat/tsGo_01.db3";
String url = "http://localhost:20334";
String accessToken = "";				// 从认证服务器获取该访问令牌
UserWalletManager wm = UserWalletManager.getWallet(path, url, accessToken);
// 创建账户
String user01 = wm.createAccount();			// 创建单个账户
List<String> list = wm.createAccount(10); 	// 批量创建10个账户
System.out.println("user:"+user01);
System.out.println("list:"+list);
```

### 4.2 注册资产

```
// 打开账户管理器
String path = "./dat/tsGo_01.db3";
String url = "http://localhost:20334";
String accessToken = "";				// 从认证服务器获取该访问令牌
UserWalletManager wm = UserWalletManager.getWallet(path, url, accessToken);
// 注册资产
String issuer= "";		// 资产发行者地址
String name = "";			// 资产名称
long amount = 10000;		// 资产数量
String desc = "";			// 描述
String controller = "";		// 资产控制者地址
int precision = 0;			// 精度
String assetid = wm.reg(issuer, name, amount , desc, controller, precision);
System.out.println("rs:"+assetid);
```

### 4.3 发行资产

```
// 打开账户管理器
String path = "./dat/tsGo_01.db3";
String url = "http://localhost:20334";
String accessToken = "";				// 从认证服务器获取该访问令牌
UserWalletManager wm = UserWalletManager.getWallet(path, url, accessToken);
// 分发资产
String controller= "";		// 资产控制者地址
String assetid = "";		// 资产编号(由注册资产产生)
long amount = 100;			// 分发数量
String recver = "";			// 分发资产接收者地址
String desc = "";			// 描述
String txid = wm.iss(controller, assetid, amount , recver , desc );
System.out.println("rs:"+txid);
```

### 4.4 转移资产

```
// 打开账户管理器
String path = "./dat/tsGo_01.db3";
String url = "http://localhost:20334";
String accessToken = "";				// 从认证服务器获取该访问令牌
UserWalletManager wm = UserWalletManager.getWallet(path, url, accessToken);
// 转移资产
String controller= "";		// 资产控制者地址
String assetid = "";		// 资产编号(由注册资产产生)
long amount = 100;		// 转移数量
String recver = "";		// 转移资产接收者地址
String desc = "";		// 描述
String txid = wm.trf(controller, assetid, amount , recver , desc );
System.out.println("rs:"+txid);
```

### 4.5 存证

```
// 打开账户管理器
String url = "http://localhost:20334";
String accessToken = "";				// 从认证服务器获取该访问令牌
UserWalletManager wm = UserWalletManager.getWallet(url, accessToken);
// 存证
String content = "";		// 待存储的信息
String desc = "";			// 描述
String txid = wm.storeCert(content, desc);
System.out.println("rs:"+txid);
```

### 4.6 取证

```
// 打开账户管理器
String url = "http://localhost:20334";
String accessToken = "";				// 从认证服务器获取该访问令牌
UserWalletManager wm = UserWalletManager.getWallet(url, accessToken);
// 取证
String txid = "";		// 存证编号
String content= wm.queryCert(txid);
System.out.println("rs:"+content);
```

### 4.7 账户信息

```
// 打开账户管理器
String path = "./dat/tsGo_01.db3";
UserWalletManager wm = UserWalletManager.getWallet(path);
// 查询账户信息
String userAddr = "";		// 账户地址
AccountInfo info = wm.getAccountInfo(userAddr);
```

### 4.8 账户资产

```
// 打开账户管理器
String path = "./dat/tsGo_01.db3";
UserWalletManager wm = UserWalletManager.getWallet(path);
// 查询账户资产
String userAddr = "";		// 账户地址
AccountAsset info = wm.getAccountAsset(userAddr);
```

### 4.9 资产信息

```
// 打开账户管理器
String url = "http://localhost:20334";
String accessToken = "";				// 从认证服务器获取该访问令牌
UserWalletManager wm = UserWalletManager.getWallet(url, accessToken);
// 查询账户资产
String assetid = "";
AssetInfo info = wm.getAssetInfo(assetid);
```

### 4.10 交易信息

```
// 打开账户管理器
String url = "http://localhost:20334";
String accessToken = "";				// 从认证服务器获取该访问令牌
UserWalletManager wm = UserWalletManager.getWallet(url, accessToken);
// 查询账户资产
String txid = "";
TransactionInfo info = wm.getTransactionInfo(txid);
```



## 4 开发说明

​	该SDK供客户端使用，其中含有账户信息的管理，比如合约地址、公钥、私钥，这些信息保存至客户端数据库中。具体的数据库可根据需求自由选择，目前实现的数据库有sqlite、mysql，sqlite是一个文件数据库，初始化时需要传递路径，上面的示例是根据该sqlite保存账户信息的数据库实现给出的，mysql使用时还需要创建对应的表，以及实现具体的数据库连接。通过SDK接入DNA区块链时，可以直接使用当前的Sqlite数据库保存账户的公私钥信息的UserWalletManager，也可自己实现一个账户管理器来管理账户私密信息。自己实现账户管理器可参考DNA.Implementations.Wallets.SQLite.UserWallet类和DNA.Implementations.Wallets.Mysql.WebWallet类。



## 5 错误代码

| 返回代码  | 描述信息                | 说明                |
| :---- | ------------------- | ----------------- |
| 0     | SUCCESS             | 成功                |
| 41001 | SESSION_EXPIRED     | 会话无效或已过期（ 需要重新登录） |
| 41002 | SERVICE_CEILING     | 达到服务上限            |
| 41003 | ILLEGAL_DATAFORMAT  | 不合法数据格式           |
| 42001 | INVALID_METHOD      | 无效的方法             |
| 42002 | INVALID_PARAMS      | 无效的参数             |
| 42003 | INVALID_TOKEN       | 无效的令牌             |
| 43001 | INVALID_TRANSACTION | 无效的交易             |
| 43002 | INVALID_ASSET       | 无效的资产             |
| 43003 | INVALID_BLOCK       | 无效的块              |
| 44001 | UNKNOWN_TRANSACTION | 找不到交易             |
| 44002 | UNKNOWN_ASSET       | 找不到资产             |
| 44003 | UNKNOWN_BLOCK       | 找不到块              |
| 45001 | INVALID_VERSION     | 协议版本错误            |
| 45002 | INTERNAL_ERROR      | 内部错误              |

