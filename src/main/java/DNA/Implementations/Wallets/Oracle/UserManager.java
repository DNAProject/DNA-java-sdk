package DNA.Implementations.Wallets.Oracle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import DNA.Fixed8;
import DNA.Helper;
import DNA.UInt160;
import DNA.UInt256;
import DNA.Core.Block;
import DNA.Core.Blockchain;
import DNA.Core.TransactionInput;
import DNA.Cryptography.ECC;
import DNA.IO.Serializable;
import DNA.Implementations.Wallets.AbstractWallet;
import DNA.Implementations.Wallets.IUserManager;
import DNA.Wallets.CoinState;

public class UserManager extends AbstractWallet implements IUserManager {
	private static UserManager instance;

	public static UserManager getInstance(String username, String password) {
		if(instance == null) {
			init(username, password);
		}
		return instance;
	}
	public static synchronized void init(String username, String password) {
		if(instance == null) {
			instance = create(username, password);
		}
	}
	private static UserManager create(String username, String password) {
		userDao = new UserDao();
		// 查询账户表是否有账户，如无则创建，若有则打开
		boolean flag = false;
		policy = userDao.hasPolicy(username);
		if("".equals(policy)) {
			policy = Helper.toHexString(ECC.generateKey()).substring(1,8);
			Policy pp = new Policy();
			pp.username = username;
			pp.password = password.getBytes();
			pp.policy = policy;
			userDao.addPolicy(pp);
			flag = true;
		}
		try {
			return new UserManager(username, password, flag);
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			throw new RuntimeException("Init account mangager error,flag="+flag,e);
		}
	}
	
	private static String policy = "p001";
	private static UserDao userDao;
	
	protected UserManager(String path, String password, boolean create)
			throws BadPaddingException, IllegalBlockSizeException {
		super(path, password, create);
	}


	@Override
    public DNA.Wallets.Account createAccount(byte[] privateKey)  {
    	// account
    	DNA.Wallets.Account account = super.createAccount(privateKey);
        onCreateAccount(account);
        // contract
        addContract(DNA.Wallets.Contract.createSignatureContract(account.publicKey));
        return account;
    }
    
	private void onCreateAccount(DNA.Wallets.Account account) {
        byte[] decryptedPrivateKey = new byte[96];
        System.arraycopy(account.publicKey.getEncoded(false), 1, decryptedPrivateKey, 0, 64);
        System.arraycopy(account.privateKey, 0, decryptedPrivateKey, 64, 32);
    	Account entity = new Account();
    	entity.privateKeyEncrypted = encryptPrivateKey(decryptedPrivateKey);
    	entity.publicKeyHash = account.publicKeyHash.toArray();
    	userDao.insertAccount(entity, policy);
    	Arrays.fill(decryptedPrivateKey, (byte)0);
    }
    
    @Override
    public void addContract(DNA.Wallets.Contract contract) {
        super.addContract(contract);
    	Contract entity = new Contract();
    	entity.scriptHash = contract.scriptHash().toArray();
    	entity.publicKeyHash = contract.publicKeyHash.toArray();
    	entity.rawData = contract.toArray();
    	userDao.insertContract(entity, policy);
    }
	
	
	
	@Override
	protected DNA.Wallets.Coin[] loadCoins() {
		DNA.Implementations.Wallets.Oracle.Coin[] entities = userDao.selectCoin(policy);
		DNA.Wallets.Coin[] coins = new DNA.Wallets.Coin[entities.length];
    	for (int i = 0; i < coins.length; i++) {
    		coins[i] = new DNA.Wallets.Coin();
    		coins[i].input = new TransactionInput();
    		coins[i].input.prevHash = new UInt256(entities[i].txid);
    		coins[i].input.prevIndex = (short)entities[i].index;
    		coins[i].assetId = new UInt256(entities[i].assetId);
    		coins[i].value = new Fixed8(entities[i].value);
    		coins[i].scriptHash = new UInt160(entities[i].scriptHash);
    		coins[i].setState(CoinState.values()[entities[i].state]);
    	}
    	return coins;
	}

	@Override
	protected void onSaveTransaction(DNA.Core.Transaction tx, DNA.Wallets.Coin[] added,
			DNA.Wallets.Coin[] changed) {
		// update tx
		Transaction tx_changed = new Transaction();
		tx_changed.hash = tx.hash().toArray();
		tx_changed.type = tx.type.value();
		tx_changed.rawData = tx.toArray();
		tx_changed.height = -1;
		tx_changed.time = (int) (System.currentTimeMillis()/1000);
		userDao.insertTransaction(tx_changed, policy); // or update
		// update tx
		onCoinsChanged(added, changed, new DNA.Wallets.Coin[0]);
	}
	
	
	@Override
	protected void onProcessNewBlock(Block block, DNA.Wallets.Coin[] added, DNA.Wallets.Coin[] changed,
			DNA.Wallets.Coin[] deleted) {
		// save tx
		ArrayList<DNA.Core.Transaction> tx_changed = new ArrayList<DNA.Core.Transaction>();
		for (DNA.Core.Transaction tx : block.transactions) {
        	if (isWalletTransaction(tx)) {
        		tx_changed.add(tx);
        		Transaction entity = new Transaction();
        		entity.hash = tx.hash().toArray();
        		entity.type = tx.type.value();
        		entity.rawData = tx.toArray();
        		entity.height = block.height;
        		entity.time = block.timestamp;
        		userDao.insertTransaction(entity, policy);
        	}
        }
		
		// update coin
		onCoinsChanged(added, changed, deleted);
        // update height
        if (tx_changed.size() > 0 || added.length > 0 || changed.length > 0 || deleted.length > 0) {
        	saveStoredData("Height", ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(walletHeight()).array());
        }
	}
	
	private void onCoinsChanged(DNA.Wallets.Coin[] added, DNA.Wallets.Coin[] changed, DNA.Wallets.Coin[] deleted) {
    	userDao.insertCoin(Arrays.stream(added).map(p -> {
    		Coin entity = new Coin();
    		entity.txid = p.input.prevHash.toArray();
    		entity.index = Short.toUnsignedInt(p.input.prevIndex);
    		entity.assetId = p.assetId.toArray();
    		entity.value = p.value.getData();
    		entity.scriptHash = p.scriptHash.toArray();
    		entity.state = CoinState.Unspent.ordinal();
    		return entity;
    	}).toArray(Coin[]::new), policy);
    	userDao.updateCoin(Arrays.stream(changed).map(p -> {
    		Coin entity = new Coin();
    		entity.txid = p.input.prevHash.toArray();
    		entity.index = Short.toUnsignedInt(p.input.prevIndex);
    		entity.state = p.getState().ordinal();
    		return entity;
    	}).toArray(Coin[]::new), policy);
    	userDao.deleteCoin(Arrays.stream(deleted).map(p -> {
    		Coin entity = new Coin();
    		entity.txid = p.input.prevHash.toArray();
    		entity.index = Short.toUnsignedInt(p.input.prevIndex);
    		return entity;
    	}).toArray(Coin[]::new), policy);
    }

	public void storeAccount(DNA.Wallets.Account account) {
		byte[] decryptedPrivateKey = new byte[96];
        System.arraycopy(account.publicKey.getEncoded(false), 1, decryptedPrivateKey, 0, 64);
        System.arraycopy(account.privateKey, 0, decryptedPrivateKey, 64, 32);
    	DNA.Implementations.Wallets.Oracle.Account entity = new DNA.Implementations.Wallets.Oracle.Account();
    	entity.privateKeyEncrypted = encryptPrivateKey(decryptedPrivateKey);
    	entity.publicKeyHash = account.publicKeyHash.toArray();
        Arrays.fill(decryptedPrivateKey, (byte)0);
        userDao.insertAccount(entity, policy);
	}
	public void storeContract(DNA.Wallets.Contract contract) {
		super.addContract(contract);
		DNA.Implementations.Wallets.Oracle.Contract entity = new DNA.Implementations.Wallets.Oracle.Contract();
    	entity.scriptHash = contract.scriptHash().toArray();
    	entity.publicKeyHash = contract.publicKeyHash.toArray();
    	entity.rawData = contract.toArray();
    	userDao.insertContract(entity, policy);
	}
	
	@Override
	protected DNA.Wallets.Account[] loadAccounts() {
		try {
			Account[] entities = userDao.selectAccount(policy);
			DNA.Wallets.Account[] accounts = new DNA.Wallets.Account[entities.length];
			for (int i = 0; i < accounts.length; i++) {
	    		byte[] decryptedPrivateKey = decryptPrivateKey(entities[i].privateKeyEncrypted);
	    		accounts[i] = new DNA.Wallets.Account(decryptedPrivateKey);
	    		Arrays.fill(decryptedPrivateKey, (byte)0);
	    	}
	    	return accounts;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected DNA.Wallets.Contract[] loadContracts() {
		try {
			Contract[] entities = userDao.selectContract(policy);
			DNA.Wallets.Contract[] contracts = new DNA.Wallets.Contract[entities.length];
        	for (int i = 0; i < contracts.length; i++) {
        		contracts[i] = Serializable.from(entities[i].rawData, DNA.Wallets.Contract.class);
        	}
        	return contracts;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected byte[] loadStoredData(String name) {
		Key key = userDao.selectKey(name, policy);
		if(key != null) {
			return key.value;
		}
		throw new RuntimeException("Not find value by key:"+name);
	}

	@Override
	protected void saveStoredData(String name, byte[] value) {
		Key key = new Key();
		key.name = name;
		key.value = value;
		if(!userDao.hasKey(key, policy)) {
			userDao.insertKey(key, policy);
		} else {
			userDao.updateKey(key, policy);
		}
	}
	
	public boolean hasFinishedSyncBlock() throws Exception {
		return Blockchain.current().height() == walletHeight();
	}
}
