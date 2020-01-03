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

package com.github.DNAProject.smartcontract.neovm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.account.Account;
import com.github.DNAProject.common.Common;
import com.github.DNAProject.common.ErrorCode;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.core.transaction.Transaction;
import com.github.DNAProject.io.BinaryReader;
import com.github.DNAProject.io.BinaryWriter;
import com.github.DNAProject.io.Serializable;
import com.github.DNAProject.sdk.exception.SDKException;
import com.github.DNAProject.smartcontract.neovm.abi.AbiFunction;
import com.github.DNAProject.smartcontract.neovm.abi.AbiInfo;
import com.github.DNAProject.smartcontract.neovm.abi.BuildParams;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ClaimRecord {
    private DnaSdk sdk;
    private String contractAddress = "36bb5c053b6b839c8f6b923fe852f91239b9fccc";

    private String abi = "{\"hash\":\"0x36bb5c053b6b839c8f6b923fe852f91239b9fccc\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"Commit\",\"parameters\":[{\"name\":\"claimId\",\"type\":\"ByteArray\"},{\"name\":\"commiterId\",\"type\":\"ByteArray\"},{\"name\":\"ownerId\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"},{\"name\":\"Revoke\",\"parameters\":[{\"name\":\"claimId\",\"type\":\"ByteArray\"},{\"name\":\"dnaId\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"},{\"name\":\"GetStatus\",\"parameters\":[{\"name\":\"claimId\",\"type\":\"ByteArray\"}],\"returntype\":\"ByteArray\"}],\"events\":[{\"name\":\"ErrorMsg\",\"parameters\":[{\"name\":\"id\",\"type\":\"ByteArray\"},{\"name\":\"error\",\"type\":\"String\"}],\"returntype\":\"Void\"},{\"name\":\"Push\",\"parameters\":[{\"name\":\"id\",\"type\":\"ByteArray\"},{\"name\":\"msg\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"ByteArray\"}],\"returntype\":\"Void\"}]}";

    public ClaimRecord(DnaSdk sdk) {
        this.sdk = sdk;
    }

    public void setContractAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getContractAddress() {
        return contractAddress;
    }

    /**
     *
     * @param issuerDnaid
     * @param password
     * @param subjectDnaid
     * @param claimId
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendCommit(String issuerDnaid, String password, byte[] salt, String subjectDnaid, String claimId, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if(issuerDnaid==null||issuerDnaid.equals("")||password==null||password.equals("")||subjectDnaid==null||subjectDnaid.equals("")
                || claimId==null||claimId.equals("")||payerAcct == null){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeCommit(issuerDnaid,subjectDnaid,claimId,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx, issuerDnaid, password,salt);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param issuerDnaid
     * @param subjectDnaid
     * @param claimId
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeCommit(String issuerDnaid, String subjectDnaid, String claimId,String payer, long gaslimit, long gasprice) throws Exception {
        if(issuerDnaid==null||issuerDnaid.equals("")||subjectDnaid==null||subjectDnaid.equals("")||payer==null||payer.equals("")
                || claimId==null||claimId.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }

        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
        String name = "Commit";
        AbiFunction func = abiinfo.getFunction(name);
        func.name = name;
        func.setParamsValue(claimId.getBytes(),issuerDnaid.getBytes(),subjectDnaid.getBytes());
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payer,gaslimit, gasprice);
        return tx;
    }

    /**
     *
     * @param issuerDnaid
     * @param password
     * @param claimId
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendRevoke(String issuerDnaid,String password,byte[] salt, String claimId,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(issuerDnaid==null||issuerDnaid.equals("")||password==null||password.equals("")
                || claimId==null||claimId.equals("")||payerAcct == null){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = issuerDnaid.replace(Common.diddna,"");
        Transaction tx = makeRevoke(issuerDnaid,claimId,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx, addr, password,salt);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public Transaction makeRevoke(String issuerDnaid,String claimId,String payer,long gaslimit,long gasprice) throws Exception {
        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
        String name = "Revoke";
        AbiFunction func = abiinfo.getFunction(name);
        func.name = name;
        func.setParamsValue(claimId.getBytes(),issuerDnaid.getBytes());
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payer,gaslimit, gasprice);
        return tx;
    }
    public String sendGetStatus(String claimId) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (claimId == null || claimId == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
        String name = "GetStatus";
        AbiFunction func = abiinfo.getFunction(name);
        func.name = name;
        func.setParamsValue(claimId.getBytes());
        Object obj =  sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        String res = ((JSONObject)obj).getString("Result");
        if(res.equals("")){
            return "";
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(res));
        BinaryReader br = new BinaryReader(bais);
        ClaimTx claimTx = new ClaimTx();
        claimTx.deserialize(br);
        if(claimTx.status.length == 0){
            return new String(claimTx.claimId)+"."+"00"+"."+new String(claimTx.issuerDnaId)+"."+new String(claimTx.subjectDnaId);
        }
        return new String(claimTx.claimId)+"."+Helper.toHexString(claimTx.status)+"."+new String(claimTx.issuerDnaId)+"."+new String(claimTx.subjectDnaId);
    }
}

class ClaimTx implements Serializable {
    public byte[] claimId;
    public byte[] issuerDnaId;
    public byte[] subjectDnaId;
    public byte[] status;
    ClaimTx(){}
    ClaimTx(byte[] claimId,byte[] issuerDnaId,byte[] subjectDnaId,byte[] status){
        this.claimId = claimId;
        this.issuerDnaId = issuerDnaId;
        this.subjectDnaId = subjectDnaId;
        this.status = status;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        byte dataType = reader.readByte();
        long length = reader.readVarInt();
        byte dataType2 = reader.readByte();
        this.claimId = reader.readVarBytes();
        byte dataType3 = reader.readByte();
        this.issuerDnaId = reader.readVarBytes();
        byte dataType4 = reader.readByte();
        this.subjectDnaId = reader.readVarBytes();
        byte dataType5 = reader.readByte();
        this.status = reader.readVarBytes();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {

    }
}
