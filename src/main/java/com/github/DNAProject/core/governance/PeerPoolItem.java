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

package com.github.DNAProject.core.governance;

import com.github.DNAProject.common.Address;
import com.github.DNAProject.io.BinaryReader;
import com.github.DNAProject.io.BinaryWriter;
import com.github.DNAProject.io.Serializable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PeerPoolItem implements Serializable {
    public int index;
    public String peerPubkey;
    public Address address;
    public int status;
    public long initPos;
    public long totalPos;
    public PeerPoolItem(){}
    public PeerPoolItem(int index,String peerPubkey,Address address,int status,long initPos,long totalPos){
        this.index = index;
        this.peerPubkey = peerPubkey;
        this.address = address;
        this.status = status;
        this.initPos = initPos;
        this.totalPos = totalPos;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.index = reader.readInt();
        this.peerPubkey = reader.readVarString();
        try {
            this.address = reader.readSerializable(Address.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.status = reader.readByte();
        this.initPos = reader.readLong();
        this.totalPos = reader.readLong();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeInt(index);
        writer.writeVarString(peerPubkey);
        writer.writeSerializable(address);
        writer.writeByte((byte)status);
        writer.writeLong(initPos);
        writer.writeLong(totalPos);
    }
    public Object Json(){
        Map map = new HashMap();
        map.put("index",index);
        map.put("peerPubkey",peerPubkey);
        map.put("address",address.toBase58());
        map.put("status",status);
        map.put("initPos",initPos);
        map.put("totalPos",totalPos);
        return map;
    }
}
