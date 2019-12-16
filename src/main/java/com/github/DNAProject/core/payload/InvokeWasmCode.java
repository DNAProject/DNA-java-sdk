package com.github.DNAProject.core.payload;

import com.github.DNAProject.core.transaction.Transaction;
import com.github.DNAProject.core.transaction.TransactionType;
import com.github.DNAProject.io.BinaryReader;
import com.github.DNAProject.io.BinaryWriter;

import java.io.IOException;

public class InvokeWasmCode extends Transaction {

    public byte[] invokeCode;

    public InvokeWasmCode(byte[] invokeCode) {
        super(TransactionType.InvokeWasm);
        this.invokeCode = invokeCode;
    }

    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        try {
            invokeCode = reader.readVarBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(invokeCode);
    }
}
