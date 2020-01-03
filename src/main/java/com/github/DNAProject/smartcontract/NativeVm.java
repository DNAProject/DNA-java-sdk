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

package com.github.DNAProject.smartcontract;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.smartcontract.nativevm.*;

public class NativeVm {
    private Gas gas = null;
    private DnaId dnaId = null;
    private GlobalParams globalParams = null;
    private Auth auth = null;
    private Governance governance = null;
    private DnaSdk sdk;
    public NativeVm(DnaSdk sdk){
        this.sdk = sdk;
    }
    /**
     *  get Asset Tx
     * @return instance
     */

    public Gas gas() {
        if(gas == null){
            gas = new Gas(sdk);
        }
        return gas;
    }
    public DnaId dnaId(){
        if (dnaId == null){
            dnaId = new DnaId(sdk);
        }
        return dnaId;
    }
    public GlobalParams gParams(){
        if (globalParams == null){
            globalParams = new GlobalParams(sdk);
        }
        return globalParams;
    }
    public Auth auth(){
        if (auth == null){
            auth = new Auth(sdk);
        }
        return auth;
    }
    public Governance governance(){
        if (governance == null){
            governance = new Governance(sdk);
        }
        return governance;
    }
}
