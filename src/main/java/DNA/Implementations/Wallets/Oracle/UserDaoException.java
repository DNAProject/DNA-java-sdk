package DNA.Implementations.Wallets.Oracle;

public class UserDaoException  extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UserDaoException(String msg) {
		super(msg);
	}

	public UserDaoException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public UserDaoException(Throwable t) {
		super(t);
	}
}
