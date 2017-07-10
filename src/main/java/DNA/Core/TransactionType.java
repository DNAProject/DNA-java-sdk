package DNA.Core;

/**
 * list transaction types supported by DNA 
 */
public enum TransactionType {
    /**
     *  used for accounting
     */
    BookKeeping(0x00),
    /**
     *  used for accounting
     */
    IssueTransaction(0x01),
    /**
     *  
     */
    BookKeeper(0x02),
    /**
     * 
     */
    DataFile(0x12),
    /**
     * 
     */
    DeployCode(0xd0),
    /**
     *  
     */
    PrivacyPayload(0x20),
    /**
     *  
     */
    RegisterTransaction(0x40),
    /**
     *  used for transfer Transaction, this is 
     */
    TransferTransaction(0x80), 
    /**
     * 存证交易
     */
    RecordTransaction(0x81),
    
    ;

    private byte value;
    TransactionType(int v) {
        value = (byte)v;
    }
    public byte value() {
        return value;
    }

    public static TransactionType valueOf(byte v) {
    	for (TransactionType e : TransactionType.values()) {
    		if (e.value == v) {
    			return e;
    		}
    	}
    	throw new IllegalArgumentException();
    }
}
