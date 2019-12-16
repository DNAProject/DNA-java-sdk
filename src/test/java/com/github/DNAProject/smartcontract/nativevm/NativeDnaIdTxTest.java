package com.github.DNAProject.smartcontract.nativevm;

import com.alibaba.fastjson.JSONObject;
import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.DnaSdkTest;
import com.github.DNAProject.common.Common;
import com.github.DNAProject.core.dnaid.Attribute;
import com.github.DNAProject.core.transaction.Transaction;
import com.github.DNAProject.sdk.info.AccountInfo;
import com.github.DNAProject.sdk.info.IdentityInfo;
import com.github.DNAProject.sdk.wallet.Account;
import com.github.DNAProject.sdk.wallet.Identity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NativeDnaIdTxTest {
    DnaSdk dnaSdk;
    String password = "111111";
    Account payer;
    com.github.DNAProject.account.Account payerAcct;
    Identity identity;
    String walletFile = "NativeDNATxTest.json";
    @Before
    public void setUp() throws Exception {
        dnaSdk = DnaSdk.getInstance();
        dnaSdk.setRestful(DnaSdkTest.URL);
        dnaSdk.setDefaultConnect(dnaSdk.getRestful());
        dnaSdk.openWalletFile(walletFile);
//        dnaSdk.setSignatureScheme(SignatureScheme.SHA256WITHECDSA);
        payer = dnaSdk.getWalletMgr().createAccount(password);
        payerAcct = dnaSdk.getWalletMgr().getAccount(payer.address,password,payer.getSalt());
        identity = dnaSdk.getWalletMgr().createIdentity(password);
        dnaSdk.nativevm().dnaId().sendRegister(identity,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);

    }

    @After
    public void removeWallet(){
        File file = new File(walletFile);
        if(file.exists()){
            if(file.delete()){
                System.out.println("delete wallet file success");
            }
        }
    }

    @Test
    public void sendRegister() throws Exception {
        String dnaid = dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid);
        if(!dnaid.isEmpty()) {
            return;
        }
        Transaction tx = dnaSdk.nativevm().dnaId().makeRegister(identity.dnaid,identity.controls.get(0).publicKey,payer.address,dnaSdk.DEFAULT_GAS_LIMIT,0);
        dnaSdk.signTx(tx, identity.dnaid,password,identity.controls.get(0).getSalt());
        dnaSdk.addSign(tx,payerAcct);
        dnaSdk.getConnect().sendRawTransaction(tx);

        Identity identity2 = dnaSdk.getWalletMgr().createIdentity(password);
        dnaSdk.nativevm().dnaId().sendRegister(identity2,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);

        Identity identity3 = dnaSdk.getWalletMgr().createIdentity(password);
        Attribute[] attributes = new Attribute[1];
        attributes[0] = new Attribute("key2".getBytes(),"value2".getBytes(),"type2".getBytes());
        dnaSdk.nativevm().dnaId().sendRegisterWithAttrs(identity3,password,attributes,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);

        Thread.sleep(6000);
        String ddo = dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid);
        Assert.assertTrue(ddo.contains(identity.dnaid));

        String dd02 = dnaSdk.nativevm().dnaId().sendGetDDO(identity3.dnaid);
        Assert.assertTrue(dd02.contains("key2"));

        String keystate = dnaSdk.nativevm().dnaId().sendGetKeyState(identity.dnaid,1);
        Assert.assertNotNull(keystate);

        //merkleproof
        Object merkleproof = dnaSdk.nativevm().dnaId().getMerkleProof(tx.hash().toHexString());
        boolean b = dnaSdk.nativevm().dnaId().verifyMerkleProof(JSONObject.toJSONString(merkleproof));
        Assert.assertTrue(b);

        //claim
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Issuer", identity.dnaid);
        map.put("Subject", identity2.dnaid);

        Map clmRevMap = new HashMap();
        clmRevMap.put("typ","AttestContract");
        clmRevMap.put("addr",identity.dnaid.replace(Common.diddna,""));

        String claim = dnaSdk.nativevm().dnaId().createDnaIdClaim(identity.dnaid,password,identity.controls.get(0).getSalt(), "claim:context", map, map,clmRevMap,System.currentTimeMillis()/1000 +100000);
        boolean b2 = dnaSdk.nativevm().dnaId().verifyDnaIdClaim(claim);
        Assert.assertTrue(b2);
    }
    @Test
    public void sendAddPubkey() throws Exception {
        IdentityInfo info = dnaSdk.getWalletMgr().createIdentityInfo(password);
        IdentityInfo info2 = dnaSdk.getWalletMgr().createIdentityInfo(password);
        Transaction tx = dnaSdk.nativevm().dnaId().makeAddPubKey(identity.dnaid,password,identity.controls.get(0).getSalt(),info.pubkey,payer.address,dnaSdk.DEFAULT_GAS_LIMIT,0);
        dnaSdk.signTx(tx, identity.dnaid,password,identity.controls.get(0).getSalt());
        dnaSdk.addSign(tx,payerAcct);
        dnaSdk.getConnect().sendRawTransaction(tx);

        dnaSdk.nativevm().dnaId().sendAddPubKey(identity.dnaid,password,identity.controls.get(0).getSalt(),info2.pubkey,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);

        Thread.sleep(6000);
        String ddo = dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid);
        Assert.assertTrue(ddo.contains(info.pubkey));
        Assert.assertTrue(ddo.contains(info2.pubkey));

        String publikeys = dnaSdk.nativevm().dnaId().sendGetPublicKeys(identity.dnaid);
        Assert.assertNotNull(publikeys);

        Transaction tx2 = dnaSdk.nativevm().dnaId().makeRemovePubKey(identity.dnaid,password,identity.controls.get(0).getSalt(),info.pubkey,payer.address,dnaSdk.DEFAULT_GAS_LIMIT,0);
        dnaSdk.signTx(tx2,identity.dnaid,password,identity.controls.get(0).getSalt());
        dnaSdk.addSign(tx2,payerAcct);
        dnaSdk.getConnect().sendRawTransaction(tx2);

        dnaSdk.nativevm().dnaId().sendRemovePubKey(identity.dnaid,password,identity.controls.get(0).getSalt(),info2.pubkey,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        String ddo3 = dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid);
        Assert.assertFalse(ddo3.contains(info.pubkey));
        Assert.assertFalse(ddo3.contains(info2.pubkey));
    }

    @Test
    public void sendAddAttributes() throws Exception {
        Attribute[] attributes = new Attribute[1];
        attributes[0] = new Attribute("key1".getBytes(),"value1".getBytes(),"String".getBytes());
        Transaction tx = dnaSdk.nativevm().dnaId().makeAddAttributes(identity.dnaid,password,identity.controls.get(0).getSalt(),attributes,payer.address,dnaSdk.DEFAULT_GAS_LIMIT,0);
        dnaSdk.signTx(tx, identity.dnaid,password,identity.controls.get(0).getSalt());
        dnaSdk.addSign(tx,payerAcct);
        dnaSdk.getConnect().sendRawTransaction(tx);

        Attribute[] attributes2 = new Attribute[1];
        attributes2[0] = new Attribute("key99".getBytes(),"value99".getBytes(),"String".getBytes());
        dnaSdk.nativevm().dnaId().sendAddAttributes(identity.dnaid,password,identity.controls.get(0).getSalt(),attributes2,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);

        Thread.sleep(6000);
        String ddo = dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid);
        Assert.assertTrue(ddo.contains("key1"));
        Assert.assertTrue(ddo.contains("key99"));

        String attribute = dnaSdk.nativevm().dnaId().sendGetAttributes(identity.dnaid);
        Assert.assertTrue(attribute.contains("key1"));

        Transaction tx2= dnaSdk.nativevm().dnaId().makeRemoveAttribute(identity.dnaid,password,identity.controls.get(0).getSalt(),"key1",payer.address,dnaSdk.DEFAULT_GAS_LIMIT,0);
        dnaSdk.signTx(tx2,identity.dnaid,password,identity.controls.get(0).getSalt());
        dnaSdk.addSign(tx2,payerAcct);
        dnaSdk.getConnect().sendRawTransaction(tx2);

        dnaSdk.nativevm().dnaId().sendRemoveAttribute(identity.dnaid,password,identity.controls.get(0).getSalt(),"key99",payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);

        String ddo2 = dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid);
        Assert.assertFalse(ddo2.contains("key1"));
        Assert.assertFalse(ddo2.contains("key99"));

    }

    @Test
    public void sendAddRecovery() throws Exception {
        Identity identity = dnaSdk.getWalletMgr().createIdentity(password);
        dnaSdk.nativevm().dnaId().sendRegister(identity,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);

        Identity identity2 = dnaSdk.getWalletMgr().createIdentity(password);
        dnaSdk.nativevm().dnaId().sendRegister(identity2,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);

        Thread.sleep(6000);

        Account account = dnaSdk.getWalletMgr().createAccount(password);

        Transaction tx = dnaSdk.nativevm().dnaId().makeAddRecovery(identity.dnaid,password,identity.controls.get(0).getSalt(),account.address,payer.address,dnaSdk.DEFAULT_GAS_LIMIT,0);
        dnaSdk.signTx(tx, identity.dnaid,password,identity.controls.get(0).getSalt());
        dnaSdk.addSign(tx,payerAcct);
        dnaSdk.getConnect().sendRawTransaction(tx);

        dnaSdk.nativevm().dnaId().sendAddRecovery(identity2.dnaid,password,identity2.controls.get(0).getSalt(),account.address,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);

        Thread.sleep(6000);
        String ddo = dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid);
        Assert.assertTrue(ddo.contains(account.address));
        String ddo2 = dnaSdk.nativevm().dnaId().sendGetDDO(identity2.dnaid);
        Assert.assertTrue(ddo2.contains(account.address));

        AccountInfo info2 = dnaSdk.getWalletMgr().createAccountInfo(password);

        Transaction tx2 = dnaSdk.nativevm().dnaId().makeChangeRecovery(identity.dnaid,info2.addressBase58,account.address,password,payerAcct.getAddressU160().toBase58(),dnaSdk.DEFAULT_GAS_LIMIT,0);
        dnaSdk.signTx(tx2,account.address,password,account.getSalt());

        dnaSdk.nativevm().dnaId().sendChangeRecovery(identity2.dnaid,info2.addressBase58,account.address,password,account.getSalt(),payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);

        String ddo3 = dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid);
        Assert.assertTrue(ddo3.contains(account.address));
        String ddo4 = dnaSdk.nativevm().dnaId().sendGetDDO(identity2.dnaid);
        Assert.assertTrue(ddo4.contains(info2.addressBase58));
    }
}