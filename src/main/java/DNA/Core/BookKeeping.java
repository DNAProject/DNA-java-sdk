package DNA.Core;

import java.io.IOException;

import DNA.IO.BinaryReader;
import DNA.IO.BinaryWriter;

public class BookKeeping extends Transaction {

	public BookKeeping() {
		super(TransactionType.BookKeeping);
	}

	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
	}
	
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
	}
}
