package DNA.Core;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import DNA.*;
import DNA.IO.*;
import DNA.IO.Json.*;

public class ClaimTransaction extends Transaction {
	public TransactionInput[] claims;
	
	public ClaimTransaction() {
		super(TransactionType.ClaimTransaction);
	}
	
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        try {
			claims = reader.readSerializableArray(TransactionInput.class);
		} catch (InstantiationException | IllegalAccessException ex) {
        	throw new IOException(ex);
		}
        if (claims.length == 0) {
        	throw new IOException();
        }
        if (Arrays.stream(claims).distinct().count() != claims.length) {
        	throw new IOException();
        }
	}
	
	@Override
	public UInt160[] getScriptHashesForVerifying() {
        HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
        for (Entry<UInt256, List<TransactionInput>> group : Arrays.stream(claims).collect(Collectors.groupingBy(p -> p.prevHash)).entrySet()) {
            Transaction tx;
			try {
				tx = Blockchain.current().getTransaction(group.getKey());
			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
            if (tx == null) throw new IllegalStateException();
            for (TransactionInput claim : group.getValue()) {
                if (tx.outputs.length <= claim.prevIndex) {
                	throw new IllegalStateException();
                }
                hashes.add(tx.outputs[claim.prevIndex].scriptHash);
            }
        }
        return hashes.stream().sorted().toArray(UInt160[]::new);
	}
	
	@Override
	public JObject json() {
        JObject json = super.json();
        json.set("claims", new JArray(Arrays.stream(claims).map(p -> p.json()).toArray(JObject[]::new)));
        return json;
	}
	
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeSerializableArray(claims);
	}
	
	@Override
	public boolean verify() {
		return super.verify();
	}
}
