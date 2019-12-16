package com.github.DNAProject.io;

import com.github.DNAProject.common.Address;
import com.github.DNAProject.common.Helper;

import java.io.IOException;
import java.math.BigInteger;

public class utils {

    public static long readVarInt(BinaryReader reader) throws IOException {
        byte[] r = reader.readVarBytes();
        BigInteger b = Helper.BigIntFromNeoBytes(r);
        return b.longValue();
    }
    public static Address readAddress(BinaryReader reader) throws IOException {
        byte[] r = reader.readVarBytes();
        return new Address(r);
    }
}
