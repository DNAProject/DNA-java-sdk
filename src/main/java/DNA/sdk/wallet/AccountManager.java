package DNA.sdk.wallet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import DNA.Fixed8;
import DNA.Helper;
import DNA.UInt160;
import DNA.UInt256;
import DNA.Core.AssetType;
import DNA.Core.Block;
import DNA.Core.Blockchain;
import DNA.Core.IssueTransaction;
import DNA.Core.RecordTransaction;
import DNA.Core.RecordType;
import DNA.Core.RegisterTransaction;
import DNA.Core.SignatureContext;
import DNA.Core.StateUpdateTransaction;
import DNA.Core.Transaction;
import DNA.Core.TransactionAttribute;
import DNA.Core.TransactionAttributeUsage;
import DNA.Core.TransactionInput;
import DNA.Core.TransactionOutput;
import DNA.Core.TransferTransaction;
import DNA.Core.Scripts.Program;
import DNA.Implementations.Blockchains.Rest.RestBlockchain;
import DNA.Implementations.Wallets.IUserManager;
import DNA.Implementations.Wallets.UserManagerFactory;
import DNA.Implementations.Wallets.SQLite.UserWallet;
import DNA.Network.Rest.RestException;
import DNA.Network.Rest.RestNode;
import DNA.Wallets.Account;
import DNA.Wallets.Contract;
import DNA.Wallets.Wallet;
import DNA.Websocket.Utils.GetBlockTransactionUtils;
import DNA.sdk.dbpool.DBParamInitailer;
import DNA.sdk.info.account.AccountAsset;
import DNA.sdk.info.account.AccountInfo;
import DNA.sdk.info.account.Asset;
import DNA.sdk.info.asset.AssetInfo;
import DNA.sdk.info.mutil.TxJoiner;
import DNA.sdk.info.transaction.TransactionInfo;
import DNA.sdk.info.transaction.TxInputInfo;
import DNA.sdk.info.transaction.TxOutputInfo;

import com.alibaba.fastjson.JSON;

public class AccountManager {
	private String action = "sendrawtransaction",version = "v001",type = "t001";
	private IUserManager uw;
	private RestNode restNode;
	private boolean isWaitSync = true;
	
	public void setWaitSync(boolean isWaitSync) {
		this.isWaitSync = isWaitSync;
	}
	
	{
		/**
		 * AccountManager.setPKGenerateAlgorithm("ECC"); ECC/SM2/default(ECC)
		 * AccountManager.setDNAConnectWay("Rest");	Rest/Rpc/default(Rest)
		 * AccountManager wm = AccountManager.getWallet(path, url, token);
		 * 
		 * AccountManager.setPKGenerateAlgorithm("ECC"); ECC/SM2/default(ECC)
		 * AccountManager.setDNAConnectWay("Rest");	Rest/Rpc/default(Rest)
		 * AccountManager.setDBConnectInfo(dbUrl, dbDriver, dbUsername, dbPassword);
		 * AccountManager wm = AccountManager.getWallet(username, password, url, token);
		 * 
		 * 
		 * 启动同步
		 * wm.startSyncBlock();
		 * 
		 * 创建账户
		 * wm.createAccount();
		 * 
		 * 注册资产
		 * wm.reg/iss/trf/...
		 * 
		 * 查询操作
		 * wm.query...
		 * 
		 */
	}
	
	public static void setPKGenerateAlgorithm(String algorithm) {
		// ECC or SM2
	}
	
	public static void setDBConnectInfo(String dbUrl, String dbDriver, String dbUsername, String dbPassword) {
		DBParamInitailer.init(dbUrl, dbDriver, dbUsername, dbPassword, 10);
	}
	
	public static AccountManager getWallet(String adminName, String adminPswd, String url, String accessToken) {
		AccountManager wm = new AccountManager();
		wm.initBlockRestNode(url, accessToken);
		wm.initRestNode(url, accessToken);
		wm.initUserManager(adminName, adminPswd);
		return wm;
	}
	
	
	public static AccountManager getWallet(String path, String url, String accessToken) {
		AccountManager wm = new AccountManager();
		wm.initBlockRestNode(url, accessToken);
		wm.initRestNode(url, accessToken);
		wm.initWallet(path);
		return wm;
	}
	
	public static AccountManager getWallet(String url, String accessToken) {
		AccountManager wm = new AccountManager();
		wm.initRestNode(url, accessToken);
		return wm;
	}
	
	public static AccountManager getWallet(String path) {
		AccountManager wm = new AccountManager();
		wm.initWallet(path);
		return wm;
	}
	
	private AccountManager() {
	}
	
	private void initBlockRestNode(String url, String token) {
		Blockchain.register(new RestBlockchain(new RestNode(url, token)));
	}
	
	private void initRestNode(String url, String token) {
		this.restNode = new RestNode(url, token);
	}
	
	private void initWallet(String path) {
		if(new File(path).exists() && new File(path).isFile()) {
			uw = UserWallet.open(path, "0x123456");
		} else {
			uw = UserWallet.create(path, "0x123456");
		}
	}
	private void initUserManager(String user, String pswd) {
		uw = UserManagerFactory.newOrclUserManager(user, pswd);
	}
	
	/**
	 * 启动同步线程
	 */
	public void startSyncBlock() {
		uw.start();
	}
	
	public void stopSyncBlock() {
		uw.close();
	}
	
	public boolean hasFinishedSyncBlock() {
		try {
			return uw.hasFinishedSyncBlock();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 更新访问令牌
	 * 
	 * @param accessToken
	 */
	public void updateToken(String accessToken) {
		restNode.setAccessToken(accessToken);
	}
	
	
	/**
	 * 创建单个账户
	 */
	public String createAccount() {
		return createAddress();
	}
	
	/**
	 * 创建多个账户
	 */
	public List<String> createAccount(int n) {
		return Stream.generate(() -> createAddress()).limit(n).collect(Collectors.toList());
	}
	// 
	private String createAddress() {
		return uw.getContract(Contract.createSignatureContract(uw.createAccount().publicKey).address()).address();
	}
	
	/**
	 * 根据私钥创建账户
	 * 
	 * @param prikey
	 * @return
	 */
	public String loadPrivateKey(String prikey) {
		Account acc = uw.createAccount(Helper.hexToBytes(prikey));
		return uw.getContract(Contract.createSignatureContract(acc.publicKey).address()).address();
	}
	
	/**
	 * 导出所有账户地址
	 * 
	 * @return
	 */
	public List<String> listAccount() {
		return Arrays.stream(uw.getContracts()).map(p -> p.address()).collect(Collectors.toList());
	}
	
	public String address2UInt160(String address) {
		return Wallet.toScriptHash(address).toString();
	}
	
	public String uint1602Address(String uint160) {
		return Wallet.toAddress(UInt160.parse(uint160));
	}
	
	public int blockHeight() {
		try {
			return restNode.getBlockHeight();
		} catch (RestException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setAuthType(String authType) {
		this.restNode.setAuthType(authType);
	}
	
	public void setAccessToken(String accessToken) {
		this.restNode.setAccessToken(accessToken);
	}
	
	public void setVersion(String version, String type) {
		this.version = version;
		this.type = type;
	}
	
	/**
	 * 注册资产
	 * @throws Exception 
	 */
	public String reg() throws Exception {// in-null,out-null
		throw new UnsupportedOperationException();
	}
	/**
	 * 注册资产
	 * 
	 * @param issuer	资产发行者地址
	 * @param name		资产名称
	 * @param amount	资产数量
	 * @param desc		描述
	 * @param controller 资产控制者地址
	 * @param precision	 精度
	 * @return	交易编号
	 * @throws Exception
	 */
	public String reg(String issuer, String name, long amount, String desc, String controller, int precision) throws Exception {
		return regToken(issuer, name, amount, desc, controller, precision);
	}
	/**
	 * 注册Token资产
	 * 
	 * @param issuer	资产发行者地址
	 * @param name		资产名称
	 * @param amount	资产数量
	 * @param desc		描述
	 * @param controller 资产控制者地址
	 * @param precision	 精度
	 * @return	交易编号
	 * @throws Exception
	 */
	public String regToken(String issuer, String name, long amount, String desc, String controller, int precision) throws Exception {
		return regToken(getAccount(issuer), name, amount, desc, controller, precision);
	}
	public String regToken(Account acc, String assetName, long assetAmount, String txDesc, String controller, int precision) throws Exception {
		return reg(getRegTx(acc, assetName, assetAmount, txDesc, AssetType.Token, controller, precision));
	}
	/**
	 * 注册Share资产
	 * 
	 * @param issuer	资产发行者地址
	 * @param name		资产名称
	 * @param amount	资产数量
	 * @param desc		描述
	 * @param controller 资产控制者地址
	 * @param precision	 精度
	 * @return	交易编号
	 * @throws Exception
	 */
	public String regShare(String issuer, String name, long amount, String desc, String controller, int precision) throws Exception {
		return regShare(getAccount(issuer), name, amount, desc, controller, precision);
	}
	public String regShare(Account acc, String assetName, long assetAmount, String txDesc, String controller, int precision) throws Exception {
		return reg(getRegTx(acc, assetName, assetAmount, txDesc, AssetType.Share, controller, precision));
	}
	private String reg(RegisterTransaction regTx) throws Exception {
		RegisterTransaction signedTx4Reg = uw.makeTransaction(regTx, Fixed8.ZERO);
		SignatureContext context = new SignatureContext(signedTx4Reg);
		boolean f1 = uw.sign(context);
		if(context.isCompleted()){
			signedTx4Reg.scripts = context.getScripts();
		}
		String txHex = Helper.toHexString(signedTx4Reg.toArray());
		boolean f2 = restNode.sendRawTransaction(action, version, type, txHex);
		String txid = signedTx4Reg.hash().toString();
		System.out.println("reg.sign:"+f1+",rst:"+f2+",txid:"+ txid);
		if(f2 && isWaitSync) {
			uw.saveTransaction(signedTx4Reg);
			wait(uw,txid); // 等待生效
		}
		return txid;
	}
	
	/**
	 * 分发资产
	 * @throws Exception 
	 */
	public String iss() {//in-null
		throw new UnsupportedOperationException();
	}
	/**
	 * 分发资产
	 * 
	 * @param sendAddr	资产控制者地址
	 * @param assetid	资产编号
	 * @param amount	资产数量
	 * @param recvAddr	接收者地址
	 * @param desc		描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public String iss(String sendAddr, String assetid, long amount, String recvAddr, String desc) throws Exception {
		return iss(getIssTx(assetid, amount, recvAddr, desc), getAddress(sendAddr));
	}
	private String iss(IssueTransaction issueTx, UInt160 from) throws Exception {
		IssueTransaction signedTx4Iss = uw.makeTransaction(issueTx, Fixed8.ZERO, from);
		SignatureContext context4Iss = new SignatureContext(signedTx4Iss);
		boolean f3 = uw.sign(context4Iss);
		if(context4Iss.isCompleted()){
			signedTx4Iss.scripts = context4Iss.getScripts();
		}
		uw.saveTransaction(signedTx4Iss);
		String txHex = Helper.toHexString(signedTx4Iss.toArray());;
		boolean f4 = restNode.sendRawTransaction(action, version, type, txHex);
		
		String txid4Iss = signedTx4Iss.hash().toString();
		System.out.println("iss.sign:"+f3+",rst:"+f4+",txid:"+ txid4Iss);
		if(f4 && isWaitSync) {
			wait(uw,txid4Iss); // 等待生效
		}
		return txid4Iss;
	}
	/**
	 * 转账
	 */
	public String trf() {
		throw new UnsupportedOperationException();
	}
	/**
	 * 转移资产
	 * 
	 * @param sendAddr	资产控制者地址
	 * @param assetid	资产编号
	 * @param amount	资产数量
	 * @param recvAddr	接收者地址
	 * @param desc		描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public String trf(String sendAddr, String assetid, long amount, String recvAddr, String desc) throws Exception {
		return trf(getTrfTx(assetid, amount, recvAddr, desc), getAddress(sendAddr));
	}
	private String trf(TransferTransaction trfTx, UInt160 from) throws Exception {
		TransferTransaction signedTx4Trf = uw.makeTransaction(trfTx, Fixed8.ZERO, from);
		SignatureContext context4Trf = new SignatureContext(signedTx4Trf);
		boolean f5 = uw.sign(context4Trf);
		if(context4Trf.isCompleted()){
			signedTx4Trf.scripts = context4Trf.getScripts();
		}
		uw.saveTransaction(signedTx4Trf);
		String txHex = Helper.toHexString(signedTx4Trf.toArray());;
		boolean f6 = restNode.sendRawTransaction(action, version, type, txHex);
		
		String txid4Trf = signedTx4Trf.hash().toString();
		System.out.println("trf.sign:"+f5+",rst:"+f6+",txid:"+ txid4Trf);
		if(f6 && isWaitSync) {
			wait(uw,txid4Trf); // 等待生效
		}
		return txid4Trf;
	}
	
	
	
	// 1. 构造交易
	/**
	 * 构造注册资产交易
	 * 
	 * @param issuer	资产控制者
	 * @param name		资产名称
	 * @param amount	资产数量
	 * @param desc		描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public RegisterTransaction makeRegisterTransaction(String issuer, String name, long amount, String desc, String controller, int precision) {
		return uw.makeTransaction(getRegTx(getAccount(issuer), name, amount, desc, AssetType.Token, controller, precision), Fixed8.ZERO);
	}
	/**
	 * 构造分发资产交易
	 * 
	 * @param sendAddr	资产控制者地址
	 * @param assetid	资产编号
	 * @param amount	资产数量
	 * @param recvAddr	接收者地址
	 * @param desc		描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public IssueTransaction makeIssueTransaction(String sendAddr, String assetid, long amount, String recvAddr, String desc) {
		return uw.makeTransaction(getIssTx(assetid, amount, recvAddr, desc), Fixed8.ZERO);
	}
	/**
	 * 构造分发资产交易(多个接收者)
	 * 
	 * @param sendAddr	资产控制者地址
	 * @param list		接收者信息列表，包括接收者地址、接收资产编号、接收数量
	 * @param desc		描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public IssueTransaction makeIssueTransaction(String sendAddr, List<TxJoiner>list, String desc) {
		return uw.makeTransaction(getIssTx(list, desc), Fixed8.ZERO);
	}
	/**
	 * 构造转移资产交易
	 * 
	 * @param sendAddr	资产控制者地址
	 * @param assetid	资产编号
	 * @param amount	资产数量
	 * @param recvAddr	接收者地址
	 * @param desc		描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public TransferTransaction makeTransferTransaction(String sendAddr, String assetid, long amount, String recvAddr, String desc) {
		return uw.makeTransaction(getTrfTx(assetid, amount, recvAddr, desc), Fixed8.ZERO, getAddress(sendAddr));
	}
	/**
	 * 构造转移资产交易(多个接收者)
	 * 
	 * @param sendAddr	资产控制者地址
	 * @param list		接收者信息列表，包括接收者地址、接收资产编号、接收数量
	 * @param desc		描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public TransferTransaction makeTransferTransaction(String sendAddr, List<TxJoiner> list, String desc) {
		return uw.makeTransaction(getTrfTx(list, desc), Fixed8.ZERO, getAddress(sendAddr));
	}
	
	/**
	 * 构造状态更新交易
	 * 
	 * @param namespace
	 * @param key
	 * @param value
	 * @param controller
	 * @return
	 */
	public StateUpdateTransaction makeStateUpdateTransaction(String namespace, String key, String value, String controller) {
		StateUpdateTransaction tx = new StateUpdateTransaction();
		tx.attributes = new TransactionAttribute[0];
		tx.inputs = new TransactionInput[0];
		tx.outputs = new TransactionOutput[0];
		tx.namespace = namespace.getBytes();
		tx.key = key.getBytes();
		tx.value = value.getBytes();
		tx.updater = getAccount(controller).publicKey;
		return tx;
	}
	// 2. 交易签名
	/**
	 * 交易签名
	 * 
	 * @param tx	待签名的交易
	 * @return	签名完成且序列化后的交易
	 */
	public String signatureData(DNA.Core.Transaction tx) {
		SignatureContext context = new SignatureContext(tx);
		boolean f5 = uw.sign(context);
		if(f5 && context.isCompleted()){
			tx.scripts = context.getScripts();
		} else {
			throw new RuntimeException("Signature incompleted");
		}
		uw.saveTransaction(tx);
		return Helper.toHexString(tx.toArray());
	}
	// 3. 发送交易
	/**
	 * 发送交易
	 * 
	 * @param tx	待发送的交易
	 * @return		发送成功与否
	 * @throws RestException
	 */
	public boolean sendTransaction(DNA.Core.Transaction tx) throws RestException {
		if(sendData(Helper.toHexString(tx.toArray()))) {
			uw.saveTransaction(tx);
			return true;
		}
		return false;
	}
	/**
	 * 发送交易
	 * 
	 * @param txHex	签名完成的交易
	 * @return		发送成功与否
	 * @throws RestException
	 */
	public boolean sendData(String txHex) throws RestException {
		return restNode.sendRawTransaction(action, version, type, txHex);
	}
	
	
	/**
	 * 存证
	 * 
	 * @param data	存证内容
	 * @param desc	描述
	 * @return	交易编号
	 * @throws Exception
	 */
	public String storeCert(String data, String desc) throws Exception {
		RecordTransaction tx = getRcdTx(data, desc);
		String txHex = Helper.toHexString(tx.toArray());;
		boolean f = restNode.sendRawTransaction(action, version, type, txHex);
		
		String txid = tx.hash().toString();
		System.out.println("rcd.sign:null, rst:"+f+",txid:"+txid);
		return txid;
	}
	/**
	 * 取证
	 * 
	 * @param txid	交易编号
	 * @return	存证内容
	 * @throws Exception
	 */
	public String queryCert(String txid) throws Exception {
		Transaction tx = restNode.getRawTransaction(txid);
		if(tx instanceof RecordTransaction) {
			RecordTransaction rr = (RecordTransaction) tx;
			return new String(rr.recordData);
		}
		return null;
	}
	
	
	// 获取账户
	public Account getAccount(String address) {
		return uw.getAccount(uw.getContract(address).publicKeyHash);
	}
	// 获取地址
	private UInt160 getAddress(String address) {
		return Wallet.toScriptHash(address);
	}
	
	/**
	 * 等待该笔交易同步至账户管理器中
	 * 
	 * @param txid
	 */
	public void wait(String txid) {
		wait(uw, txid);
	}
	// 等待Tx生效
	private void wait(IUserManager uw, String txid) {
		int count = 3;	// 最长等待1分钟
		while(--count > 0) {
			Map<Transaction, Integer> txs = uw.LoadTransactions();
			if(txs != null && txs.keySet().stream().filter(p -> txs.get(p).intValue() > 1).filter(p -> p.hash().toString().equals(txid)).count() == 1) {
				System.out.println("sync finish, txid:"+txid);
				return;
			}
			try {
				Thread.sleep(1000*5);
			} catch (InterruptedException e) {
			}
			System.out.println("sleep.....5s");
		}
		System.out.println("sync timeout,txid:"+txid);
	}
	
	private RegisterTransaction getRegTx(Account acc, String assetName, long assetAmount, String txDesc, AssetType assetType, String controller, int precision) {
		RegisterTransaction tx = new RegisterTransaction();
		
		tx.precision = 8;//(byte) precision;						// 精度
		tx.assetType = AssetType.Token;			// 资产类型
		tx.recordType = RecordType.UTXO;			// 记账模式
		tx.nonce = (int)Math.random()*10;		// 随机数
		
		tx.assetType = assetType ;	
		tx.name = assetName;	
		tx.amount = Fixed8.parse(String.valueOf(assetAmount));	
		tx.issuer = acc.publicKey;	
		tx.admin = Wallet.toScriptHash(controller); 
//		tx.admin = Wallet.toScriptHash(Contract.createSignatureContract(acc.publicKey).address()); 
		tx.outputs = new TransactionOutput[0];
		if(txDesc != null && txDesc.length() > 0) {
			tx.attributes = new TransactionAttribute[1];
			tx.attributes[0] = new TransactionAttribute();
			tx.attributes[0].usage = TransactionAttributeUsage.Description;
			tx.attributes[0].data = (txDesc+new Date().toString()).getBytes();
		}
		return tx;
	}
	private IssueTransaction getIssTx(String assetId, long assetAmount, String recvAddr, String txDesc) {
		IssueTransaction tx = new IssueTransaction();
		tx.outputs = new TransactionOutput[1];
		tx.outputs[0] = new TransactionOutput();
		tx.outputs[0].assetId = UInt256.parse(assetId);
		tx.outputs[0].value = Fixed8.parse(String.valueOf(assetAmount));
		tx.outputs[0].scriptHash = Wallet.toScriptHash(recvAddr);
		if(txDesc != null && txDesc.length() > 0) {
			tx.attributes = new TransactionAttribute[1];
			tx.attributes[0] = new TransactionAttribute();
			tx.attributes[0].usage = TransactionAttributeUsage.Description;
			tx.attributes[0].data = (txDesc+new Date().toString()).getBytes();
		}
		return tx;
	}
	private IssueTransaction getIssTx(List<TxJoiner> recvlist, String txDesc) {
		int size = recvlist.size();
		IssueTransaction tx = new IssueTransaction();
		tx.outputs = new TransactionOutput[1];
		tx.outputs = new TransactionOutput[size];
		for(int i=0; i<size; ++i) {
			TxJoiner recv = recvlist.get(i);
			tx.outputs[i] = new TransactionOutput();
			tx.outputs[i].assetId = UInt256.parse(recv.assetid);
			tx.outputs[i].value = Fixed8.parse(String.valueOf(recv.value));
			tx.outputs[i].scriptHash = Wallet.toScriptHash(recv.address);
		}
		if(txDesc != null && txDesc.length() > 0) {
			tx.attributes = new TransactionAttribute[1];
			tx.attributes[0] = new TransactionAttribute();
			tx.attributes[0].usage = TransactionAttributeUsage.Description;
			tx.attributes[0].data = (txDesc+new Date().toString()).getBytes();
		}
		return tx;
	}
	
	private TransferTransaction getTrfTx(String assetId, long assetAmount, String recvAddr, String txDesc) {
		TransferTransaction tx = new TransferTransaction();
		tx.outputs = new TransactionOutput[1];
		tx.outputs[0] = new TransactionOutput();
		tx.outputs[0].assetId = UInt256.parse(assetId);
		tx.outputs[0].value = Fixed8.parse(String.valueOf(assetAmount));
		tx.outputs[0].scriptHash = Wallet.toScriptHash(recvAddr);
		if(txDesc != null && txDesc.length() > 0) {
			tx.attributes = new TransactionAttribute[1];
			tx.attributes[0] = new TransactionAttribute();
			tx.attributes[0].usage = TransactionAttributeUsage.Description;
			tx.attributes[0].data = (txDesc+new Date().toString()).getBytes();
		}
		return tx;
	}
	
	private TransferTransaction getTrfTx(List<TxJoiner> recvList, String txDesc) {
		int size = recvList.size();
		TransferTransaction tx = new TransferTransaction();
		tx.outputs = new TransactionOutput[size];
		for(int i=0; i<size; ++i) {
			TxJoiner recv = recvList.get(i);
			tx.outputs[i] = new TransactionOutput();
			tx.outputs[i].assetId = UInt256.parse(recv.assetid);
			tx.outputs[i].value = Fixed8.parse(String.valueOf(recv.value));
			tx.outputs[i].scriptHash = Wallet.toScriptHash(recv.address);
		}
		if(txDesc != null && txDesc.length() > 0) {
			tx.attributes = new TransactionAttribute[1];
			tx.attributes[0] = new TransactionAttribute();
			tx.attributes[0].usage = TransactionAttributeUsage.Description;
			tx.attributes[0].data = (txDesc+new Date().toString()).getBytes();
		}
		return tx;
	}
	
	private RecordTransaction getRcdTx(String data, String txDesc) {
		RecordTransaction rcdTx = new RecordTransaction();
		rcdTx.recordType = "";
		rcdTx.recordData = data.getBytes();
		rcdTx.outputs = new TransactionOutput[0];
		rcdTx.inputs = new TransactionInput[0];
		rcdTx.attributes = new TransactionAttribute[0];
		if(txDesc != null && txDesc.length() > 0) {
			rcdTx.attributes = new TransactionAttribute[1];
			rcdTx.attributes[0] = new TransactionAttribute();
			rcdTx.attributes[0].usage = TransactionAttributeUsage.Description;
			rcdTx.attributes[0].data = (txDesc+new Date().toString()).getBytes();
		}  else {
			rcdTx.attributes = new TransactionAttribute[0];
		}
		rcdTx.scripts = new Program[0];
		System.out.println("txid.len="+rcdTx.hash().toString().length());
		return rcdTx;
	}
	
	/**
	 * 获取账户信息
	 * 
	 * @param address
	 * @return
	 */
	public AccountInfo getAccountInfo(String address) {
		AccountInfo info = new AccountInfo();
		Contract con = uw.getContract(address);
		Account acc = uw.getAccountByScriptHash(Wallet.toScriptHash(address));
		info.address = con.address();
		info.pubkey = Helper.toHexString(acc.publicKey.getEncoded(true));
		info.prikey = Helper.toHexString(acc.privateKey);
		info.priwif = acc.export();
		info.pkhash = acc.publicKeyHash.toString();
		return info;
	}

	/**
	 * 获取账户资产
	 * 
	 * @param address
	 * @return
	 */
	public AccountAsset getAccountAsset(String address) {
		AccountAsset asset = new AccountAsset();
		Contract con = uw.getContract(address);
		asset.address = con.address();
		asset.canUseAssets = new ArrayList<Asset>();
		asset.freezeAssets = new ArrayList<Asset>();
		Arrays.stream(uw.findUnspentCoins()).filter(p -> address.equals(Wallet.toAddress(p.scriptHash))).forEach(p -> {
			Asset as = new Asset();
			as.assetid = p.assetId.toString();
			as.amount = p.value.toLong();
			asset.canUseAssets.add(as);
		});
		Arrays.stream(uw.findUnconfirmedCoins()).filter(p -> address.equals(Wallet.toAddress(p.scriptHash))).forEach(p -> {
			Asset as = new Asset();
			as.assetid = p.assetId.toString();
			as.amount = p.value.toLong();
			asset.freezeAssets.add(as);
		});
		return asset;
	}
	
	/**
	 * 获取资产信息
	 * 
	 * @param assetid
	 * @return
	 * @throws RestException
	 */
	public AssetInfo getAssetInfo(String assetid) throws RestException {
		String ss = restNode.getAsset(assetid);
		return JSON.parseObject(ss, AssetInfo.class);
	}
	
	/**
	 * 获取交易信息
	 * 
	 * @param txid
	 * @return
	 * @throws RestException
	 */
	public TransactionInfo getTransactionInfo(String txid) throws RestException {
		TransactionInfo info = new TransactionInfo();
		info.txid = txid;
		Transaction tx = restNode.getRawTransaction(txid);
		if(tx instanceof RegisterTransaction) {
			info.type = RegisterTransaction.class.getSimpleName();
		} else if(tx instanceof IssueTransaction) {
			info.type = IssueTransaction.class.getSimpleName();
		} else if(tx instanceof TransferTransaction) {
			info.type = TransferTransaction.class.getSimpleName();
		} else if(tx instanceof RecordTransaction) {
			info.type = RecordTransaction.class.getSimpleName();
		}
		
		info.inputs = new ArrayList<TxInputInfo>();
		Arrays.stream(tx.inputs).map(p -> getTxByNextTxInput(p)).forEach(p -> {
			TxInputInfo in = new TxInputInfo();
			in.address = Wallet.toAddress(p.scriptHash);
			in.assetid = p.assetId.toString();
			in.amount = p.value.toLong();
			info.inputs.add(in);
		});
		info.outputs = new ArrayList<TxOutputInfo>();
		Arrays.stream(tx.outputs).forEach(p -> {
			TxOutputInfo out = new TxOutputInfo();
			out.address = Wallet.toAddress(p.scriptHash);
			out.assetid = p.assetId.toString();
			out.amount = p.value.toLong();
			info.outputs.add(out);
		});
		StringBuilder sb = new StringBuilder();
		for(TransactionAttribute attr: tx.attributes) {
			sb.append(Helper.toHexString(attr.data));
		}
		if(sb.toString().length() > 0) {
			info.attrs = new String(Helper.hexToBytes(sb.toString()));
		}
		return info;
	}
	private TransactionOutput getTxByNextTxInput(TransactionInput input){
		Transaction tx;
		try {
			tx = restNode.getRawTransaction(input.prevHash.toString());
		} catch (RestException e) {
			throw new RuntimeException("Not find tx by next txInput");
		}
		return tx.outputs[input.prevIndex];
	}
	
	public static Block fromWebSocketData(String ss) {
		return GetBlockTransactionUtils.from(ss);
	}
}
