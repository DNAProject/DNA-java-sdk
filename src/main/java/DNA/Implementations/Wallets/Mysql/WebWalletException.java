package DNA.Implementations.Wallets.Mysql;

public class WebWalletException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public WebWalletException(String msg) {
		super(msg);
	}

	public WebWalletException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public WebWalletException(Throwable t) {
		super(t);
	}
}
