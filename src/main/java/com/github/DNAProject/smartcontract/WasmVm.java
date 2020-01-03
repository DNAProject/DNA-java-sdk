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
import com.github.DNAProject.common.Address;
import com.github.DNAProject.common.ErrorCode;
import com.github.DNAProject.core.scripts.WasmScriptBuilder;
import com.github.DNAProject.core.transaction.Attribute;
import com.github.DNAProject.sdk.exception.SDKException;
import com.github.DNAProject.core.payload.DeployWasmCode;
import com.github.DNAProject.core.payload.InvokeWasmCode;

import java.util.List;
import java.util.Random;

public class WasmVm {

    private DnaSdk sdk;

    public WasmVm(DnaSdk sdk) {
        this.sdk = sdk;
    }

    public DeployWasmCode makeDeployCodeTransaction(String codeStr, String name, String codeVersion, String author,
                                                    String email, String description, Address payer, long gasLimit,
                                                    long gasPrice) throws SDKException {
        if (name == null || name.equals("") || codeVersion == null || codeVersion.equals("") || author == null || author.equals("") || email == null || email.equals("") || description == null || description.equals("")) {
            throw new SDKException(ErrorCode.InvalidInterfaceParam);
        }
        return new DeployWasmCode(codeStr, name, codeVersion, author, email, description, payer, gasLimit, gasPrice);
    }

    public InvokeWasmCode makeInvokeCodeTransaction(String contractHash, String method, List<Object> params, Address payer,
                                                    long gasLimit, long gasPrice) {
        byte[] invokeCode = WasmScriptBuilder.createWasmInvokeCode(contractHash, method, params);
        InvokeWasmCode tx = new InvokeWasmCode(invokeCode);
        tx.payer = payer;
        tx.gasLimit = gasLimit;
        tx.gasPrice = gasPrice;
        tx.attributes = new Attribute[0];
        tx.nonce = new Random().nextInt();
        return tx;
    }

}
