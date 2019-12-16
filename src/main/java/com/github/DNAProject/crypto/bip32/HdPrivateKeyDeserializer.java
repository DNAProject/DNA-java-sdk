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

package com.github.DNAProject.crypto.bip32;

import com.github.DNAProject.common.ErrorCode;
import com.github.DNAProject.sdk.exception.SDKException;

import java.util.Arrays;

import static com.github.DNAProject.crypto.bip32.HdKey.confirmHdKeyChecksum;
import static io.github.novacrypto.base58.Base58.base58Decode;

final class HdPrivateKeyDeserializer implements Deserializer<HdPrivateKey> {

    static final HdPrivateKeyDeserializer DEFAULT = new HdPrivateKeyDeserializer(Bitcoin.MAIN_NET);

    private final Network network;

    HdPrivateKeyDeserializer(final Network network) {
        this.network = network;
    }

    @Override
    public HdPrivateKey deserialize(final CharSequence extendedBase58Key) throws SDKException {
        final byte[] extendedKeyData = base58Decode(extendedBase58Key);
        try {
            return deserialize(extendedKeyData);
        } finally {
            Arrays.fill(extendedKeyData, (byte) 0);
        }
    }

    @Override
    public HdPrivateKey deserialize(final byte[] extendedKeyData) throws SDKException {
        confirmHdKeyChecksum(extendedKeyData);
        final ByteArrayReader reader = new ByteArrayReader(extendedKeyData);
        final int version = reader.readSer32();
        if (version != Bitcoin.MAIN_NET.getPrivateVersion()) {
            throw new SDKException(ErrorCode.OtherError(String.format("Can't find network that matches private version 0x%x", version)));
        }
        return new HdPrivateKey(new HdKey
                .Builder()
                .network(Bitcoin.MAIN_NET)
                .depth(reader.read())
                .parentFingerprint(reader.readSer32())
                .childNumber(reader.readSer32())
                .chainCode(reader.readRange(32))
                .key(getKey(reader))
                .neutered(false)
                .build()
        );
    }

    private byte[] getKey(final ByteArrayReader reader) throws SDKException {
        if (reader.read() != 0) {
            throw new SDKException(ErrorCode.OtherError("Expected 0 padding at position 45"));
        }
        return reader.readRange(32);
    }
}