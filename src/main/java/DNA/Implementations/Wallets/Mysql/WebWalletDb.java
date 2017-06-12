package DNA.Implementations.Wallets.Mysql;

/**
 * 数据库账户信息数据操作类
 * 
 * @author 12146
 *
 */
public class WebWalletDb {
	private WebWalletDao dao = new WebWalletDao();
	
	public WebWalletDb () {
	}
	
	public void storeAccount(Account account, String policy) {
		dao.insertAccount(account, policy);;
	}
	public void storeContract(Contract contract, String policy) {
		dao.insertContract(contract, policy);
	}
	public void storeKey(Key2 key, String policy) {
		dao.insertKey(key, policy);
	}
	public Account[] loadAccount(String address) {
		return dao.selectAccount(address);
	}
	public Contract[] loadContract(String address) {
		return dao.selectContract(address);
	}
	public Key2[] loadKey(String address) {
		return dao.selectKey(address);
	}
}
