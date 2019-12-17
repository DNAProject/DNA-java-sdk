<h1 align="center"> 数字资产 </h1>


## 钱包文件及规范

钱包文件是一个Json格式的数据存储文件，可同时存储多个数字身份和多个数字资产账户。具体参考[钱包文件规范](../docs/Wallet_File_Specification.md)。

为了管理数字资产，您首先需要创建/打开一个钱包文件。

```java
//如果不存在钱包文件，会自动创建钱包文件。
DnaSdk dnaSdk = DnaSdk.getInstance();
dnaSdk.openWalletFile("Demo.json");
```
> 注：目前仅支持文件形式钱包文件，也可以扩展支持数据库或其他存储方式。

## 资产账户数据结构说明
`address` 是base58编码的账户地址。
`label` 是账户的名称。
`isDefault`表明账户是否是默认的账户。默认值为false。
`lock` 表明账户是否是被用户锁住的。客户端不能消费掉被锁的账户中的资金。
`algorithm` 是秘钥算法名称。
`parameters` 是加密算法所需参数。
`curve` 是椭圆曲线的名称。
`key` 是NEP-2格式的私钥。该字段可以为null（对于只读地址或非标准地址）。
`encAlg` 私钥加密的算法名称，固定为aes-256-ctr。
`salt` 私钥解密参数。
`extra` 是客户端存储额外信息的字段。该字段可以为null。
`signatureScheme` 是签名方案，用于交易签名。
`hash` hash算法，用于派生秘钥。


```java
public class Account {
    public String label = "";
    public String address = "";
    public boolean isDefault = false;
    public boolean lock = false;
    public String algorithm = "";
    public Map parameters = new HashMap() ;
    public String key = "";
    @JSONField(name = "enc-alg")
    public String encAlg = "aes-256-gcm";
    public String salt = "";
    public String hash = "sha256";
    public String signatureScheme = "SHA256withECDSA";
    public Object extra = null;
}
```

## 数字资产账户管理

以下举例说明如何管理钱包中的资产账户。

* 创建数字资产账号

```java
DnaSdk dnaSdk = DnaSdk.getInstance();
Account acct = dnaSdk.getWalletMgr().createAccount("password");
//创建的账号或身份只在内存中，如果要写入钱包文件，需调用写入接口
dnaSdk.getWalletMgr().writeWallet();
```


* 移除数字资产账号

```java
dnaSdk.getWalletMgr().getWallet().removeAccount(address);
//写入钱包
dnaSdk.getWalletMgr().writeWallet();
```

* 设置默认数字资产账号

```java
dnaSdk.getWalletMgr().getWallet().setDefaultAccount(index);
dnaSdk.getWalletMgr().getWallet().setDefaultAccount("address");
```
> Note: index表示设置第index个account为默认账户，address表示设置该address对应的account为默认账户

## 原生数字资产接口


原生数字资产是Gas。封装了构造交易、交易签名、发送交易。

#### 1. 转账
```java
String sendTransfer(Account sendAcct, String recvAddr, long amount, Account payerAcct, long gaslimit, long gasprice)
```
功能说明： 从发送方转移一定数量的资产到接收方账户

参数说明：

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | sendAcct| Account | 发送方账户 | 必选 |
|        | recvAddr    | Account | 接收方地址   | 必选 |
|        | amount        | long | 转移的资产数量|必选|
|        | payerAcct| Account  |支付交易费用的账户 | 必选|
|        | gaslimit   | long | 声明发行者和申请者dnaid | 必选 |
|        | gasprice   | long | gas价格 | 必选 |
| 输出参数 | 交易hash   | String  | 交易hash  |  |

#### 2. 授权转移资产
```java
String sendApprove(Account sendAcct, String recvAddr, long amount, Account payerAcct, long gaslimit, long gasprice)
```
功能说明： sendAddr账户允许recvAddr转移amount数量的资产

参数说明：
       
| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | sendAcct| Account | 发送方账户 | 必选 |
|        | recvAddr    | Account | 接收方地址   | 必选 |
|        | amount        | long | 授权的资产数量|必选|
|        | payerAcct| Account  |支付交易费用的账户 | 必选|
|        | gaslimit   | long | 声明发行者和申请者dnaid | 必选 |
|        | gasprice   | long | gas价格 | 必选 |
| 输出参数 | 交易hash   | String  | 交易hash  |  |

#### 3. TransferFrom

```java
String sendTransferFrom(Account sendAcct, String fromAddr, String toAddr, long amount, Account payerAcct, long gaslimit, long gasprice)
```
功能说明： sendAcct账户从fromAddr账户转移amount数量的资产到toAddr账户

参数说明：     
        
| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | sendAcct| Account | 发送方账户 | 必选 |
|        | fromAddr    | Account | 资产转出方地址   | 必选 |
|        | toAddr    | Account | 资产转入方地址   | 必选 |
|        | amount        | long | 转移的资产数量|必选|
|        | payerAcct| Account  |支付交易费用的账户 | 必选|
|        | gaslimit   | long | 声明发行者和申请者dnaid | 必选 |
|        | gasprice   | long | gas价格 | 必选 |
| 输出参数 | 交易hash   | String  | 交易hash  |  |

#### 4. 查询余额

```java
long queryBalanceOf(String address)
```
功能说明： 查询账户address资产余额

参数说明：

```address```： 账户地址

返回值：账户余额

5. 查询Allowance
```java
long queryAllowance(String fromAddr,String toAddr)
```
功能说明： 查询fromAddr授权toAddr转移的数量

参数说明：

```fromAddr```: 授权转出方的账户地址

```toAddr```: 允许转入方的账户地址

返回值：授权转移的数量

#### 6. 查询资产名

```java
String queryName()
```
功能说明： 查询资产名信息

参数说明：

返回值：资产名称

#### 7. 查询资产Symbol

```java
String querySymbol()
```
功能说明： 查询资产Symbol信息

参数说明：

返回值：Symbol信息

#### 8. 查询资产的精确度

```java
long queryDecimals()
```
功能说明： 查询资产的精确度

参数说明：

返回值：精确度
            
#### 9. 查询资产的总供应量
```java
long queryTotalSupply()
```
功能说明： 查询资产的总供应量

参数说明：

返回值：总供应量


资产转移示例代码：

```java
//step1:获得dnaSdk实例
DnaSdk sdk = DnaSdk.getInstance();
sdk.setRpc(url);
sdk.openWalletFile("OntAssetDemo.json");
//step2:获得gas实例
gas = sdk.nativevm().gas()
//step3:调用转账方法
com.github.DNAProject.account.Account account1 = new com.github.DNAProject.account.Account(privateKey,SignatureScheme.SHA256WITHECDSA);
dnaSdk.nativevm().gas().sendTransfer(account1,"TA4pCAb4zUifHyxSx32dZRjTrnXtxEWKZr",10000,account1,dnaSdk.DEFAULT_GAS_LIMIT,0);
```




查看如何部署和调用智能合约，详见[smartcontract](smartcontract.md)。
