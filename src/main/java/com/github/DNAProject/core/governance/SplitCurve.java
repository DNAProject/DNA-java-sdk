package com.github.DNAProject.core.governance;

import com.github.DNAProject.common.Helper;
import com.github.DNAProject.io.BinaryReader;
import com.github.DNAProject.io.BinaryWriter;
import com.github.DNAProject.io.Serializable;

import java.io.IOException;
import java.math.BigInteger;

public class SplitCurve implements Serializable {
    public int[] Yi;
    public SplitCurve(){}
    public SplitCurve(int[] Yi){
        this.Yi = Yi;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        byte[] nBytes = reader.readVarBytes();
        BigInteger b = Helper.BigIntFromNeoBytes(nBytes);
        int n = b.intValue();
        this.Yi = new int[n];
        for(int i=0; i<n; i++){
            byte[] vBytes = reader.readVarBytes();
            b = Helper.BigIntFromNeoBytes(vBytes);
            this.Yi[i] = b.intValue();
        }
    }

    @Override
    public void serialize(BinaryWriter binaryWriter) throws IOException {

    }
}
