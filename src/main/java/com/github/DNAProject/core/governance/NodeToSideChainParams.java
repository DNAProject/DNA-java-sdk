package com.github.DNAProject.core.governance;

import com.github.DNAProject.common.Address;
import com.github.DNAProject.io.BinaryReader;
import com.github.DNAProject.io.BinaryWriter;
import com.github.DNAProject.io.Serializable;
import com.github.DNAProject.io.utils;

import java.io.IOException;

public class NodeToSideChainParams implements Serializable {
    public String peerPubkey;
    public Address address;
    public String sideChainId;
    public NodeToSideChainParams(){}

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.peerPubkey = reader.readVarString();
        this.address = utils.readAddress(reader);
        this.sideChainId = reader.readVarString();
    }

    @Override
    public void serialize(BinaryWriter binaryWriter) throws IOException {

    }
}
