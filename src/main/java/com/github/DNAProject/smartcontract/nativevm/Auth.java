package com.github.DNAProject.smartcontract.nativevm;

import com.alibaba.fastjson.JSONObject;
import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.account.Account;
import com.github.DNAProject.common.Address;
import com.github.DNAProject.common.ErrorCode;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.core.transaction.Transaction;
import com.github.DNAProject.io.BinaryReader;
import com.github.DNAProject.io.BinaryWriter;
import com.github.DNAProject.io.Serializable;
import com.github.DNAProject.sdk.exception.SDKException;
import com.github.DNAProject.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.DNAProject.smartcontract.nativevm.abi.Struct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Auth {
    private DnaSdk sdk;
    private final String contractAddress = "0000000000000000000000000000000000000006";
    public Auth(DnaSdk sdk) {
        this.sdk = sdk;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String sendInit(String adminDnaId,String password,byte[] salt, String contractAddr,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(adminDnaId ==null || adminDnaId.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(bos);
        bw.writeVarBytes(adminDnaId.getBytes());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"initContractAdmin",null, payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,adminDnaId,password,salt);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }


    /**
     *
     * @param adminDnaId
     * @param password
     * @param contractAddr
     * @param newAdminDnaID
     * @param keyNo
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendTransfer(String adminDnaId, String password,byte[] salt,long keyNo,  String contractAddr, String newAdminDnaID, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if(adminDnaId ==null || adminDnaId.equals("") || contractAddr == null || contractAddr.equals("") || newAdminDnaID==null || newAdminDnaID.equals("")||payerAcct==null){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo <0 || gaslimit <0 || gasprice <0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gasprice should not be less than 0"));
        }
        Transaction tx = makeTransfer(adminDnaId,contractAddr,newAdminDnaID,keyNo,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,adminDnaId,password,salt);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    /**
     *
     * @param adminDnaID
     * @param contractAddr
     * @param newAdminDnaID
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeTransfer(String adminDnaID,String contractAddr, String newAdminDnaID,long keyNo,String payer,long gaslimit,long gasprice) throws SDKException {
        if(adminDnaID ==null || adminDnaID.equals("") || contractAddr == null || contractAddr.equals("") || newAdminDnaID==null || newAdminDnaID.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo <0 || gaslimit <0 || gasprice <0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gasprice should not be less than 0"));
        }

        List list = new ArrayList();
        list.add(new Struct().add(Helper.hexToBytes(contractAddr),newAdminDnaID.getBytes(),keyNo));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"transfer",arg,payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param dnaid
     * @param password
     * @param contractAddr
     * @param funcName
     * @param keyNo
     * @return
     * @throws Exception
     */
    public String verifyToken(String dnaid,String password,byte[] salt,long keyNo, String contractAddr,String funcName) throws Exception {
        if(dnaid ==null || dnaid.equals("") || password ==null || password.equals("")|| contractAddr == null || contractAddr.equals("") || funcName==null || funcName.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo < 0){
            throw new SDKException(ErrorCode.ParamErr("key or gaslimit or gas price should not be less than 0"));
        }
        Transaction tx = makeVerifyToken(dnaid,contractAddr,funcName,keyNo);
        sdk.signTx(tx,dnaid,password,salt);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null){
            throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error: "));
        }
        return ((JSONObject)obj).getString("Result");
    }

    /**
     *
     * @param dnaid
     * @param contractAddr
     * @param funcName
     * @param keyNo
     * @return
     * @throws SDKException
     */
    public Transaction makeVerifyToken(String dnaid,String contractAddr,String funcName,long keyNo) throws SDKException {
        if(dnaid ==null || dnaid.equals("")|| contractAddr == null || contractAddr.equals("") || funcName==null || funcName.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo < 0){
            throw new SDKException(ErrorCode.ParamErr("key or gaslimit or gas price should not be less than 0"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(Helper.hexToBytes(contractAddr),dnaid.getBytes(),funcName.getBytes(),keyNo));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"verifyToken",arg,null,0,0);
        return tx;
    }

    /**
     *
     * @param adminDnaID
     * @param password
     * @param contractAddr
     * @param role
     * @param funcName
     * @param keyNo
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String assignFuncsToRole(String adminDnaID,String password,byte[] salt, long keyNo,String contractAddr,String role,String[] funcName,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(adminDnaID ==null || adminDnaID.equals("") || contractAddr == null || contractAddr.equals("") || role==null || role.equals("") || funcName == null || funcName.length == 0||payerAcct==null){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo < 0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gas price should not be less than 0"));
        }
        Transaction tx = makeAssignFuncsToRole(adminDnaID,contractAddr,role,funcName,keyNo,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,adminDnaID,password,salt);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param adminDnaID
     * @param contractAddr
     * @param role
     * @param funcName
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeAssignFuncsToRole(String adminDnaID,String contractAddr,String role,String[] funcName,long keyNo,String payer,long gaslimit,long gasprice) throws SDKException {
        if(adminDnaID ==null || adminDnaID.equals("") || contractAddr == null || contractAddr.equals("") || role==null || role.equals("") || funcName == null || funcName.length == 0
                || payer==null || payer.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo < 0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gas price should not be less than 0"));
        }

        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(Helper.hexToBytes(contractAddr),adminDnaID.getBytes(),role.getBytes());
        struct.add(funcName.length);
        for (int i = 0; i < funcName.length; i++) {
            struct.add(funcName[i]);
        }
        struct.add(keyNo);
        list.add(struct);
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"assignFuncsToRole",arg,payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param adminDnaId
     * @param password
     * @param contractAddr
     * @param role
     * @param dnaIDs
     * @param keyNo
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String assignDnaIdsToRole(String adminDnaId, String password, byte[] salt, long keyNo, String contractAddr, String role, String[] dnaIDs, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if(adminDnaId == null || adminDnaId.equals("") || password==null || password.equals("") || contractAddr== null || contractAddr.equals("") ||
                role == null || role.equals("") || dnaIDs==null || dnaIDs.length == 0){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo<0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gasprice should not be less than 0"));
        }
        Transaction tx = makeAssignDnaIDsToRole(adminDnaId,contractAddr,role,dnaIDs,keyNo,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,adminDnaId,password,salt);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param adminDnaId
     * @param contractAddr
     * @param role
     * @param dnaIDs
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeAssignDnaIDsToRole(String adminDnaId, String contractAddr, String role, String[] dnaIDs, long keyNo, String payer, long gaslimit, long gasprice) throws SDKException {
        if(adminDnaId == null || adminDnaId.equals("") || contractAddr== null || contractAddr.equals("") ||
                role == null || role.equals("") || dnaIDs==null || dnaIDs.length == 0){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo <0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gasprice should not be less than 0"));
        }
        byte[][] dnaId = new byte[dnaIDs.length][];
        for(int i=0; i< dnaIDs.length ; i++){
            dnaId[i] = dnaIDs[i].getBytes();
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(Helper.hexToBytes(contractAddr),adminDnaId.getBytes(),role.getBytes());
        struct.add(dnaId.length);
        for(int i =0;i<dnaId.length;i++){
            struct.add(dnaId[i]);
        }
        struct.add(keyNo);
        list.add(struct);
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"assignOntIDsToRole",arg,payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param dnaid
     * @param password
     * @param contractAddr
     * @param toDnaId
     * @param role
     * @param period
     * @param level
     * @param keyNo
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String delegate(String dnaid,String password,byte[] salt, long keyNo,String contractAddr,String toDnaId,String role,long period,long level,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(dnaid == null || dnaid.equals("") ||password == null || password.equals("") || contractAddr == null || contractAddr.equals("") ||toDnaId==null || toDnaId.equals("")||
                role== null || role.equals("") ||payerAcct==null){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(period<0 || level <0 || keyNo <0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("period level key gaslimit or gasprice should not be less than 0"));
        }
        Transaction tx = makeDelegate(dnaid,contractAddr,toDnaId,role,period,level,keyNo,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,dnaid,password,salt);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param dnaid
     * @param contractAddr
     * @param toAddr
     * @param role
     * @param period
     * @param level
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeDelegate(String dnaid,String contractAddr,String toAddr,String role,long period,long level,long keyNo,String payer,long gaslimit,long gasprice) throws SDKException {
        if(dnaid == null || dnaid.equals("")|| contractAddr == null || contractAddr.equals("") ||toAddr==null || toAddr.equals("")||
                role== null || role.equals("") || payer ==null || payer.equals("")){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(period<0 || level <0 || keyNo <0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("period level keyNo gaslimit or gasprice should not be less than 0"));
        }

        List list = new ArrayList();
        list.add(new Struct().add(Helper.hexToBytes(contractAddr),dnaid.getBytes(),toAddr.getBytes(),role.getBytes(),period,level,keyNo));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"delegate",arg,payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param initiatorDnaid
     * @param password
     * @param contractAddr
     * @param delegate
     * @param role
     * @param keyNo
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String withdraw(String initiatorDnaid,String password,byte[] salt,long keyNo, String contractAddr,String delegate, String role,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(initiatorDnaid == null || initiatorDnaid.equals("")|| password ==null|| password.equals("") || contractAddr == null || contractAddr.equals("") ||
                role== null || role.equals("") || payerAcct==null){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo <0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gasprice should not be less than 0"));
        }
        Transaction tx = makeWithDraw(initiatorDnaid,contractAddr,delegate,role,keyNo,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,initiatorDnaid,password,salt);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param dnaid
     * @param contractAddr
     * @param delegate
     * @param role
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeWithDraw(String dnaid,String contractAddr,String delegate, String role,long keyNo,String payer,long gaslimit,long gasprice) throws SDKException {
        if(dnaid == null || dnaid.equals("")|| contractAddr == null || contractAddr.equals("") ||
                role== null || role.equals("") || payer ==null || payer.equals("")){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo <0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("key gaslimit or gasprice should not be less than 0"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(Helper.hexToBytes(contractAddr),dnaid.getBytes(),delegate.getBytes(),role.getBytes(),keyNo));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"withdraw",arg,payer,gaslimit,gasprice);
        return tx;
    }

    public Object queryAuth(String contractAddr, String role, String dnaid) throws Exception {
        Object obj = sdk.getConnect().getStorage(contractAddr,contractAddr+Helper.toHexString(role.getBytes())+Helper.toHexString(dnaid.getBytes()));
        return obj;
    }
}
class TransferParam implements Serializable {
    byte[] contractAddr;
    byte[] newAdminDnaID;
    long KeyNo;
    TransferParam(byte[] contractAddr,byte[] newAdminDnaID,long keyNo){
        this.contractAddr = contractAddr;
        this.newAdminDnaID = newAdminDnaID;
        KeyNo = keyNo;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.contractAddr = reader.readVarBytes();
        this.newAdminDnaID = reader.readVarBytes();
        KeyNo = reader.readVarInt();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.newAdminDnaID);
        writer.writeVarInt(KeyNo);
    }
}
class VerifyTokenParam implements Serializable{
    byte[] contractAddr;
    byte[] caller;
    byte[] fn;
    long keyNo;
    VerifyTokenParam(byte[] contractAddr,byte[] caller,byte[] fn,long keyNo){
        this.contractAddr = contractAddr;
        this.caller = caller;
        this.fn = fn;
        this.keyNo = keyNo;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.caller);
        writer.writeVarBytes(this.fn);
        writer.writeVarInt(keyNo);
    }
}

class FuncsToRoleParam implements Serializable{
    byte[] contractAddr;
    byte[] adminDnaID;
    byte[] role;
    String[] funcNames;
    long keyNo;

    FuncsToRoleParam(byte[] contractAddr,byte[] adminDnaID,byte[] role,String[] funcNames,long keyNo){
        this.contractAddr =contractAddr;
        this.adminDnaID = adminDnaID;
        this.role =role;
        this.funcNames = funcNames;
        this.keyNo = keyNo;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.contractAddr = reader.readVarBytes();
        this.adminDnaID = reader.readVarBytes();
        this.role = reader.readVarBytes();
        int length = (int)reader.readVarInt();
        this.funcNames = new String[length];
        for(int i = 0;i< length;i++){
            this.funcNames[i] = reader.readVarString();
        }
        this.keyNo = reader.readVarInt();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.adminDnaID);
        writer.writeVarBytes(this.role);
        writer.writeVarInt(this.funcNames.length);
        for(String name:this.funcNames){
            writer.writeVarString(name);
        }
        writer.writeVarInt(this.keyNo);
    }
}
class DnaIDsToRoleParam implements Serializable{
    byte[] contractAddr;
    byte[] adminDnaID;
    byte[] role;
    byte[][] persons;
    long keyNo;
    DnaIDsToRoleParam(byte[] contractAddr, byte[] adminDnaID, byte[] role, byte[][] persons, long keyNo){
        this.contractAddr = contractAddr;
        this.adminDnaID = adminDnaID;
        this.role = role;
        this.persons = persons;
        this.keyNo = keyNo;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.contractAddr = reader.readVarBytes();
        this.adminDnaID = reader.readVarBytes();
        this.role = reader.readVarBytes();
        int length = (int)reader.readVarInt();
        this.persons = new byte[length][];
        for(int i = 0; i< length;i++){
            this.persons[i] = reader.readVarBytes();
        }
        this.keyNo = reader.readVarInt();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.adminDnaID);
        writer.writeVarBytes(this.role);
        writer.writeVarInt(this.persons.length);
        for(byte[] p: this.persons){
            writer.writeVarBytes(p);
        }
        writer.writeVarInt(this.keyNo);
    }
}

class DelegateParam implements  Serializable{
    byte[] contractAddr;
    byte[] from;
    byte[] to;
    byte[] role;
    long period;
    long level;
    long keyNo;
    DelegateParam(byte[] contractAddr,byte[] from,byte[] to,byte[] role, long period, long level,long keyNo){
        this.contractAddr = contractAddr;
        this.from = from;
        this.to = to;
        this.role = role;
        this.period = period;
        this.level = level;
        this.keyNo = keyNo;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.from);
        writer.writeVarBytes(this.to);
        writer.writeVarBytes(this.role);
        writer.writeVarInt(this.period);
        writer.writeVarInt(this.level);
        writer.writeVarInt(this.keyNo);
    }
}

class AuthWithdrawParam implements Serializable{
    byte[] contractAddr;
    byte[] initiator;
    byte[] delegate;
    byte[] role;
    long keyNo;
    public AuthWithdrawParam(byte[] contractAddr,byte[] initiator, byte[] delegate,byte[] role,long keyNo){
        this.contractAddr = contractAddr;
        this.initiator = initiator;
        this.delegate = delegate;
        this.role = role;
        this.keyNo = keyNo;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.initiator);
        writer.writeVarBytes(this.delegate);
        writer.writeVarBytes(this.role);
        writer.writeVarInt(this.keyNo);
    }
}


