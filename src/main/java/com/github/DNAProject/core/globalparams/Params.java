package com.github.DNAProject.core.globalparams;

import com.github.DNAProject.common.Helper;
import com.github.DNAProject.io.BinaryReader;
import com.github.DNAProject.io.BinaryWriter;
import com.github.DNAProject.io.Serializable;

import java.io.IOException;
import java.math.BigInteger;

public class Params implements Serializable {
    public Param[] params;
    public Params(Param[] params) {
        this.params = params;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        long l = params.length;
        byte[] aa = Helper.BigIntToNeoBytes(BigInteger.valueOf(l));
        String bb = Helper.toHexString(aa);
        writer.writeVarBytes(aa);
        for(int i=0;i< params.length;i++) {
            writer.writeVarString(params[i].key);
            writer.writeVarString(params[i].value);
        }
    }
}
