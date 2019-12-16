/*
 * Copyright (C) 2018 The DNA Authors
 * This file is part of The DNA library.
 *
 *  The DNA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The DNA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The DNA.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.DNAProject.core.payload;

import java.io.IOException;
import java.math.BigInteger;

import com.github.DNAProject.common.Address;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.core.transaction.TransactionType;
import com.github.DNAProject.core.transaction.Transaction;
import org.bouncycastle.math.ec.ECPoint;

import com.github.DNAProject.crypto.ECC;
import com.github.DNAProject.io.BinaryReader;
import com.github.DNAProject.io.BinaryWriter;

/**
 *
 */
public class Bookkeeper extends Transaction {
    public ECPoint issuer;
    public BookkeeperAction action;
    public byte[] cert;

    public Bookkeeper() {
        super(TransactionType.Bookkeeper);
    }

    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        issuer = ECC.secp256r1.getCurve().createPoint(
                new BigInteger(1, reader.readVarBytes()), new BigInteger(1, reader.readVarBytes()));
        action = BookkeeperAction.valueOf(reader.readByte());
        cert = reader.readVarBytes();
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        return null;
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(Helper.removePrevZero(issuer.getXCoord().toBigInteger().toByteArray()));
        writer.writeVarBytes(Helper.removePrevZero(issuer.getYCoord().toBigInteger().toByteArray()));
        writer.writeByte(action.value());
        writer.writeVarBytes(cert);
    }
}
