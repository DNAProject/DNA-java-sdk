package DNA.Implementations.Wallets.Mysql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import DNA.Core.Block;
import DNA.Core.Transaction;
import DNA.IO.Serializable;
import DNA.Wallets.Coin;
import DNA.Wallets.Wallet;

public class WebWallet extends Wallet {
	private String policy;	// 标识一组账户使用的保护策略
	private WebWalletDb db;
	
	public static WebWallet create(String cakey, String password) {
		WebWallet ww = null;
		try {
			ww = new WebWallet(cakey, password, true);
			ww.createAccount();
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			throw new RuntimeException("Failed to createWallet", e);
		}
		return ww;
	}
	
	public static WebWallet open(String cakey, String password) {
		WebWallet ww = null;
		try {
			ww = new WebWallet(cakey, password, false);
//			ww.createAccount();
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			throw new RuntimeException("Failed to openWallet", e);
		}
		return ww;
	}
	
	protected WebWallet(String policy, String password, boolean create)
			throws BadPaddingException, IllegalBlockSizeException {
		super(policy, password, create);
		this.policy = policy;
		if(create) {
			db.storeKey(toKey(), policy);
		} else {
			
		}
	}
	
	public void buildDatabase() {
		db = new WebWalletDb();
    }

	 @Override
    public DNA.Wallets.Account createAccount(byte[] privateKey) {
    	// account
    	DNA.Wallets.Account account = super.createAccount(privateKey);
        storeAccount(account);
        // contract
        DNA.Wallets.Contract contract = DNA.Wallets.Contract.createSignatureContract(account.publicKey);
        addContract(contract);
        storeContract(contract);
        return account;
    }
	 
	@Override
	protected DNA.Wallets.Account[] loadAccounts() {
		try {
			Account[] entities = db.loadAccount(policy);
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
	protected Coin[] loadCoins() {
		return new Coin[0];
	}

	@Override
	protected DNA.Wallets.Contract[] loadContracts() {
		try {
			Contract[] entities = db.loadContract(policy);
        	DNA.Wallets.Contract[] contracts = new DNA.Wallets.Contract[entities.length];
        	for (int i = 0; i < contracts.length; i++) {
        		contracts[i] = Serializable.from(entities[i].rawData, DNA.Wallets.Contract.class);
        	}
        	return contracts;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Key2 cacheKey;
	private Key2 loadKey(){ 
		Key2 keyV =  db.loadKey(policy)[0];
		return keyV;
	}
	private byte[] toValue(String name) {
		if("PasswordHash".equals(name)) {
			return cacheKey.PasswordHash;
		} else if("IV".equals(name)) {
			return cacheKey.IV;
		} else if("MasterKey".equals(name)) {
			return cacheKey.MasterKey;
		} else if("Version".equals(name)) {
			return cacheKey.Version;
		} else if("Height".equals(name)) {
			return cacheKey.Height;
		}
		return null;
	}
	@Override
	protected byte[] loadStoredData(String name) {
		if(cacheKey == null) {
			cacheKey = loadKey();
		}
		return toValue(name);
	}

	@Override
	protected void onProcessNewBlock(Block block, Coin[] added, Coin[] changed,
			Coin[] deleted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSaveTransaction(Transaction tx, Coin[] added,
			Coin[] changed) {
		// TODO Auto-generated method stub
		
	}

	Key2 toKey() {
		Key2 key2 = new Key2();
		key2.PasswordHash = list.stream().filter(p -> p.name.equals("PasswordHash")).findAny().get().value;
		key2.IV = list.stream().filter(p -> p.name.equals("IV")).findAny().get().value;
		key2.MasterKey = list.stream().filter(p -> p.name.equals("MasterKey")).findAny().get().value;
		key2.Version = list.stream().filter(p -> p.name.equals("Version")).findAny().get().value;
		key2.Height = list.stream().filter(p -> p.name.equals("Height")).findAny().get().value;
		return key2;
	}
	class Data {
		public String name;
		public byte[] value;
		public Data(String name, byte[] value) {
			this.name = name;
			this.value = value;
		}
	}
	List<Data> list;
	@Override
	protected void saveStoredData(String name, byte[] value) {
		if(list == null) {
			list = new ArrayList<Data>();
		}
		list.add(new Data(name, value));
	}
	
	
	public void storeAccount(DNA.Wallets.Account account) {
		byte[] decryptedPrivateKey = new byte[96];
        System.arraycopy(account.publicKey.getEncoded(false), 1, decryptedPrivateKey, 0, 64);
        System.arraycopy(account.privateKey, 0, decryptedPrivateKey, 64, 32);
    	Account entity = new Account();
    	entity.privateKeyEncrypted = encryptPrivateKey(decryptedPrivateKey);
    	entity.publicKeyHash = account.publicKeyHash.toArray();
        Arrays.fill(decryptedPrivateKey, (byte)0);
        db.storeAccount(entity, policy);
	}
	public void storeContract(DNA.Wallets.Contract contract) {
		super.addContract(contract);
    	Contract entity = new Contract();
    	entity.scriptHash = contract.scriptHash().toArray();
    	entity.publicKeyHash = contract.publicKeyHash.toArray();
    	entity.rawData = contract.toArray();
    	db.storeContract(entity, policy);
	}
}
