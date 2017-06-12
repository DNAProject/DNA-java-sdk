package DNA.Core;

import java.io.IOException;

import DNA.Helper;
import DNA.IO.BinaryReader;
import DNA.IO.BinaryWriter;
import DNA.IO.JsonReader;
import DNA.IO.Json.JObject;

/**
 * 存证交易
 * 
 */
public class RecordTransaction extends Transaction {
	public String recordType;
	public byte[] recordData;
	
	public RecordTransaction() {
		super(TransactionType.RecordTransaction);
	}
	
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		recordType = reader.readVarString();
		recordData = reader.readVarBytes();
	}
	
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeVarString(recordType);
		writer.writeVarBytes(recordData);
	}
	
	@Override
	protected void fromJsonExclusiveData(JsonReader reader) {
		JObject json = reader.json();
		try {
			recordType = json.get("RecordType").asString();
			recordData = Helper.hexToBytes(json.get("RecordData").asString());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
