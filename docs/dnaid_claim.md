<h1 align="center"> 可信声明及存证 </h1>



##  1. 可信声明

可验证声明用来证明实体的某些属性。

可信声明存证合约提供了可信声明的存证服务。即存证可信声明Id，签发者ONT身份，属主ONT身份等信息，以及记录可用性信息即是否被吊销等信息。

可信声明规范说明文档请参考：[可信声明规范](https://github.com/ontio/ontology-DID/blob/master/docs/cn/claim_spec_cn.md)


### 1.1 数据结构和规范



java-sdk采用JSON Web Token的格式表示claim以便于在声明发行者和申请者之间进行传递，jwt格式包含三部分header,payload,signature.


### 1.2 可信声明格式

我们使用JSON Web Token的扩展格式来表示一个可信声明，它可以在发行者和接收者之间传输。

可信声明基本结构由三部分组成：Header，Payload，Signature。我们应该尽可能的多重用JWT标准属性，在某些特殊情况下可以使用定制属性。

我们通过在最后附加区块链证明来扩展JWT格式，一个典型的可信声明被组织为**header.payload.signature.blockchain_proof**

	注意：blockchain_proof是可选的。某些情况下，可信声明可以不包含blockchain_proof

#### Header
Header部分定义了该可信声明的格式类型，使用的签名方案以及用于验证签名的公钥id
```
{
    "alg": "ES256",
    "typ": "JWT-X",
    "kid": "did:ont:TRAtosUZHNSiLhzBdHacyxMX4Bg3cjWy3r#keys-1"
}
```

- **alg** 指明使用的签名方案。

-  **typ** 格式类型，可以是以下两种值
	- JWT:不包含区块链证明的可信声明
	- JWT-X:包含区块链证明的可信声明
	
	
- **kid** 指明用于签名验证的公钥。格式和DNA-ID 规范定义的一样：```<DNAID>#keys-<number>```

#### Payload
在Payload部分，可信声明id，content，metadata被编码为JSON对象。使用JWT规范中指定的一些名称，即 `jti`，`iss`，`sub`，`iat`，`exp`
```
{
    "ver": "0.7.0",
    "iss": "did:ont:TRAtosUZHNSiLhzBdHacyxMX4Bg3cjWy3r",
    "sub": "did:ont:SI59Js0zpNSiPOzBdB5cyxu80BO3cjGT70",
    "iat": 1525465044,
    "exp": 1530735444,
    "jti":"4d9546fdf2eb94a364208fa65a9996b03ba0ca4ab2f56d106dac92e891b6f7fc",
    "@context":"https://example.com/template/v1",
    "clm":{
        "Name": "Bob Dylan",
        "Age": "22"
    },
    "clm-rev":{ 
        "typ": "AttestContract",
        "addr": "8055b362904715fd84536e754868f4c8d27ca3f6"
    }
}
```

- **ver** 指明可信声明版本
-  **iss** 可信声明签发者的ONT ID
-  **sub** 可信声明接收者的ONT ID
-  **iat** unix时间戳格式的创建时间
-  **exp** unix时间戳格式的过期时间
-  **jti** 可信声明的唯一标识符
-  **@context** 可信声明内容定义文档的uri，包含每个字段的含义和值的类型的定义
-  **clm** 指明了可信声明内容
-  **clm-rev** 指明了可信声明吊销方式。支持的方式在[附录C](claim_spec.md#C-Revocation).


要签发一个可信声明，首先需要构造可信声明id、content和metadata的JSON对象，然后使用标准的JSON序列化方法对其进行序列化。最后，使用签发者的某个私钥对header和payload的二进制数据进行签名。例如上面的payload在序列化后看起来像这样：
```
	{"ver":"0.7.0","iss":"did:ont:TRAtosUZHNSiLhzBdHacyxMX4Bg3cjWy3r","sub":"did:ont:SI59Js0zpNSiPOzBdB5cyxu80BO3cjGT70","iat":1525465044,"exp":1530735444,"jti":"4d9546fdf2eb94a364208fa65a9996b03ba0ca4ab2f56d106dac92e891b6f7fc","@context":"https://example.com/template/v1","clm":{"Name":"Bob Dylan","Age":"22"},"clm-rev":{"Type":"Contract","Addr":"8055b362904715fd84536e754868f4c8d27ca3f6"}} 

```


#### Signature

在构造完Header和Payload部分后，根据JWS标准计算签名。详细的描述在 [RFC 7515 Section 5.1](https://tools.ietf.org/html/rfc7515#section-5.1)。简化的版本如下：

- 根据JWS规范对Header和Payload部分进行序列化，作为签名的输入
	
	sig := sign(Base64URL(header) || . || Base64URL(payload))


- 根据Header部分指定的特定签名方案来计算签名。

- 对签名进行编码
	
	signature := Base64URL(sig).


#### Blockchain proof
```
{
    "Type":"MerkleProof",
    "TxnHash":"c89e76ee58ae6ad99cfab829d3bf5bd7e5b9af3e5b38713c9d76ef2dcba2c8e0",
    "ContractAddr": "8055b362904715fd84536e754868f4c8d27ca3f6",
    "BlockHeight":10,
    "MerkleRoot":"bfc2ac895685fbb01e22c61462f15f2a6e3544835731a43ae0cba82255a9f904",
    "Nodes":[{
    	"Direction":"Right",
        "TargetHash":"2fa49b6440104c2de900699d31506845d244cc0c8c36a2fffb019ee7c0c6e2f6"
    }, {
        "Direction":"Left",
        "TargetHash":"fc4990f9758a310e054d166da842dab1ecd15ad9f8f0122ec71946f20ae964a4"
    }]
}
```

- **Type** 固定值"MerkleProof"
- **TxnHash** 将可信声明id存证在合约里的交易hash值
- **ContractAddr** 存证合约的地址
- **BlockHeight** 存证交易对应的区块高度
- **MerkleRoot** 该区块高度的区块对应的Merkle树根
- **Nodes** Merkle树证明的证明路径

MerkleProof按照以下格式编码
	
	BASE64URL(MerkleProof)

现在一个完整的可信声明就被创建好了
	
	BASE64URL(Header) || '.' || BASE64URL(Payload) || '.' || BASE64URL(Signature)  '.' || BASE64URL(MerkleProof) 


#### 数据结构

* Claim 具有以下数据结构

```java
class Claim{
  header : Header
  payload : Payload
  signature : byte[]
}
```


```java
class Header {
    public String Alg = "DNA-ES256";
    public String Typ = "JWT-X";
    public String Kid;
    }
```

字段说明
`alg` 使用的签名框架
`typ` 可以是下面两个值中的一个
     JWT: 表示区块链证明不包含在claim中
     JWT-X: 表示区块链证明是claim中的一部分
`kid` 用于签名的公钥

```java
class Payload {
    public String Ver;
    public String Iss;
    public String Sub;
    public long Iat;
    public long Exp;
    public String Jti;
    @JSONField(name = "@context")
    public String Context;
    public Map<String, Object> ClmMap = new HashMap<String, Object>();
    public Map<String, Object> ClmRevMap = new HashMap<String, Object>();
    }
```

`ver` Claim版本号
`iss` 发行方的dnaid
`sub` 申请方的dnaid
`iat` 创建时间
`exp` 超期时间
`jti` claim的唯一标志
`@context` 指定声明内容定义文档URI，其定义了每个字段的含义和值得类型
`clm` 包含claim内容的对象
`clm-rev` 定义个claim 的撤销机制，

### 1.3 创建可信声明

根据用户输入内容构造声明对象，该声明对象里包含了签名后的数据。
创建claim：
* 1.查询链上是否存在Issuer的DDO
* 2.签名者的公钥必须在DDO的Owners中存在
* 3.claimId 是对claim中删除Signature、Id、Proof的数据转byte数组，做一次sha256，再转hexstring
* 4.对要签名的json数据转成Map对key做排序。
* 5.Signature中Value值：claim 删除Signature、Proof后转byte数组, 做两次sha256得到的byte数组。

```java
Map<String, Object> map = new HashMap<String, Object>();
map.put("Issuer", dids.get(0).dnaid);
map.put("Subject", dids.get(1).dnaid);
Map clmRevMap = new HashMap();
clmRevMap.put("typ","AttestContract");
clmRevMap.put("addr",dids.get(1).dnaid.replace(Common.diddna,""));
String claim = dnaSdk.nativevm().dnaId().createDnaIdClaim(dids.get(0).dnaid,password,salt, "claim:context", map, map,clmRevMap,System.currentTimeMillis()/1000 +100000);
```

**createDnaIdClaim**

```java
String createDnaIdClaim(String signerDnaid, String password,byte[] salt, String context, Map<String, Object> claimMap, Map metaData,Map clmRevMap,long expire)
```


 功能说明： 创建可信声明

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | signerDnaid| String | 签名者dnaid | 必选 |
|        | password    | String | 签名者密码   | 必选 |
|        | salt        | byte[] | 解密需要的参数|必选|
|        | context| String  |指定声明内容定义文档URI，其定义了每个字段的含义和值得类型 | 必选|
|        | claimMap| Map  |声明的内容 | 必选|
|        | metaData   | Map | 声明发行者和申请者dnaid | 必选 |
|        | clmRevMap   | Map | claim的撤回机制 | 必选 |
|        | expire   | long | 声明过期时间     | 必选 |
| 输出参数 | claim   | String  | 可信声明  |  |

   

### 1.4 验证可信声明

验证claim步骤：
* 1.查询链上是否存在Metadata中Issuer的DDO
* 2.Owner是否存在Sgnature中的PublicKeyId
* 3.对要验签的json数据转成Map对key做排序。
* 4.删除Signature做验签（根据PublicKeyId的id值查找到公钥,签名是Signature中Value做base64解码）
```java
boolean b = dnaSdk.nativevm().dnaId().verifyDnaIdClaim(claim);
```

**verifyDnaIdClaim**

```java
boolean verifyDnaIdClaim(String claim)
```

功能说明： 验证可信声明

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | claim| String | 可信声明 | 必选 |
| 输出参数 | true或false   | boolean  |   |  |




## 2. 可信声明存证合约使用步骤

该合约提供存储、吊销、查询状态等功能。

### 2.1. 初始化SDK

使用存证合约之前先初始化，并设置合约地址。

```java
String ip = "http://127.0.0.1";
String restUrl = ip + ":" + "20334";
String rpcUrl = ip + ":" + "20336";
String wsUrl = ip + ":" + "20335";
DnaSdk wm = DnaSdk.getInstance();
wm.setRpc(rpcUrl);
wm.setRestful(restUrl);
wm.setDefaultConnect(wm.getRestful());
wm.openWalletFile("RecordTxDemo.json");
wm.setCodeAddress("803ca638069742da4b6871fe3d7f78718eeee78a");
```

> Note: codeAddress是存证合约地址。

    
    
###  2.2 将可信申明保存到链上 

**sendCommit**

```java
String sendCommit(String issuerDnaid, String password,byte[] salt, String subjectDnaid, String claimId, Account payerAcct, long gaslimit, long gasprice)
```

功能说明： 将数据保存到链上，声明存证，当且仅当该声明没有被存证过，且Commit函数是由committer调用，才能存证成功；否则，存证失败。存证成功后，该声明的状态就是已存证（committed）。

参数说明：

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | issuerDnaid| String | 可信申明签发者数字身份dnaid | 必选 |
| 输入参数 | password| String | 数字身份密码 | 必选 |
| 输入参数 | subjectDnaid| String | 可信申明申请者dnaid | 必选 |
| 输入参数 | claimId| String | 可信申明claim唯一性标志，即Claim里面的Jti字段 | 必选 |
| 输入参数 | payerAcct| String | 交易费用支付者账号 | 必选 |
| 输入参数 | gaslimit| String | gaslimit | 必选 |
| 输入参数 | gasprice| String | gasprice | 必选 |
| 输出参数 | 交易hash   | boolean  |   |  |



示例代码

```java
String[] claims = claim.split("\\.");
JSONObject payload = JSONObject.parseObject(new String(Base64.getDecoder().decode(claims[1].getBytes())));
String commitHash = dnaSdk.neovm().claimRecord().sendCommit(dids.get(0).dnaid,password,dids.get(1).dnaid,payload.getString("jti"),account1,dnaSdk.DEFAULT_GAS_LIMIT,0)
```

###  2.3. 查询可信申明的状态

**sendGetStatus**

```
String sendGetStatus(String claimId)
```

功能说明：查询可信申明的状态


参数说明：

```claimId```： 可信申明claim唯一性标志，即Claim里面的Jti字段

返回值：有两部分: 第一部分，claim的状态："Not attested", "Attested", "Attest has been revoked";第二部分是存证者的dnaid


示例代码

```java
String getstatusRes2 = dnaSdk.neovm().claimRecord().sendGetStatus(payload.getString("jti"));
```


###  2.4. 撤销可信申明


**sendRevoke**

```java
String sendRevoke(String issuerDnaid,String password,byte[] salt,String claimId,Account payerAcct,long gaslimit,long gas)
```

功能说明：撤销可信申明


| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | issuerDnaid| String | 可信申明签发者数字身份dnaid | 必选 |
| 输入参数 | password| String | 数字身份密码 | 必选 |
| 输入参数 | salt| String | issuer的salt | 必选 |
| 输入参数 | claimId| String | 可信申明claim唯一性标志，即Claim里面的Jti字段 | 必选 |
| 输入参数 | payerAcct| String | 交易费用支付者账号 | 必选 |
| 输入参数 | gaslimit| String | gaslimit | 必选 |
| 输入参数 | gasprice| String | gasprice | 必选 |
| 输出参数 | 交易hash   | boolean  |   |  |


示例代码

```java
String revokeHash = dnaSdk.neovm().claimRecord().sendRevoke(dids.get(0).dnaid,password,salt,payload.getString("jti"),account1,dnaSdk.DEFAULT_GAS_LIMIT,0);
```
