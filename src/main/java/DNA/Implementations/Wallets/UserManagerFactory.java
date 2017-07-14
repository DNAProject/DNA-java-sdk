package DNA.Implementations.Wallets;

import DNA.Implementations.Wallets.Oracle.UserManager;

public class UserManagerFactory {

	public static IUserManager newOrclUserManager(String username, String password) {
		return UserManager.getInstance(username, password);
	}
	
}
