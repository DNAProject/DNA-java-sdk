package DNA.Core;

import java.io.IOException;

import DNA.IO.BinaryReader;
import DNA.IO.BinaryWriter;

/**
 *  记账节点
 *  
 */
public class MinerTransaction extends Transaction {
	
	public MinerTransaction() {
		super(TransactionType.MinerTransaction);
	}
	
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
	}
	
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
	}
}
