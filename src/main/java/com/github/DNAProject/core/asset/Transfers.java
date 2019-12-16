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

package com.github.DNAProject.core.asset;

import com.github.DNAProject.io.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Transfers implements Serializable {
    public State[] states;
    public Transfers(){

    }
    public Transfers(State[] states){
        this.states = states;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        int len = (int)reader.readVarInt();
        states = new State[len];
        for(int i = 0;i <len;i++){
            try {
                states[i] = reader.readSerializable(State.class);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeSerializableArray(states);
    }

    public static Transfers deserializeFrom(byte[] value) throws IOException {
        try {
            int offset = 0;
            ByteArrayInputStream ms = new ByteArrayInputStream(value, offset, value.length - offset);
            BinaryReader reader = new BinaryReader(ms);
            Transfers transfers = new Transfers();
            transfers.deserialize(reader);
            return transfers;
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }
    public Object json() {
        Map json = new HashMap<>();
        List list = new ArrayList<>();
        for(int i=0;i<states.length;i++){
            list.add(states[i].json());
        }
        json.put("States",list);
        return json;
    }

}
