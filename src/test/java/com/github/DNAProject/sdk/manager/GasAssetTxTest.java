package com.github.DNAProject.sdk.manager;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.DnaSdkTest;
import com.github.DNAProject.core.transaction.Transaction;
import com.github.DNAProject.sdk.wallet.Account;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class GasAssetTxTest {

    DnaSdk ontSdk;
    Account info1 = null;
    Account info2 = null;
    Account info3 = null;
    String password = "111111";
    String wallet = "OntAssetTxTest.json";

    Account payer;
    @Before
    public void setUp() throws Exception {
        ontSdk = DnaSdk.getInstance();
        String restUrl = DnaSdkTest.URL;
        ontSdk.setRestful(restUrl);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile(wallet);
        info1 = ontSdk.getWalletMgr().createAccountFromPriKey(DnaSdkTest.PASSWORD, DnaSdkTest.PRIVATEKEY);
        info2 = ontSdk.getWalletMgr().createAccount(password);
        info3 = ontSdk.getWalletMgr().createAccount(password);

        payer = ontSdk.getWalletMgr().createAccount(password);
    }
    @After
    public void removeWallet(){
        File file = new File(wallet);
        if(file.exists()){
            if(file.delete()){
                System.out.println("delete wallet file success");
            }
        }
    }
    @Test
    public void sendTransfer() throws Exception {
        com.github.DNAProject.account.Account sendAcct = ontSdk.getWalletMgr().getAccount(info1.address,password,info1.getSalt());
        com.github.DNAProject.account.Account payerAcct = ontSdk.getWalletMgr().getAccount(payer.address,password,payer.getSalt());
        String res= ontSdk.nativevm().gas().sendTransfer(sendAcct,info2.address,100L,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);


        Assert.assertNotNull(res);
    }

    @Test
    public void makeTransfer() throws Exception {

        Transaction tx = ontSdk.nativevm().gas().makeTransfer(info1.address,info2.address,100L,payer.address,ontSdk.DEFAULT_GAS_LIMIT,0);
        Assert.assertNotNull(tx);
    }

    @Test
    public void sendApprove() throws Exception {
        com.github.DNAProject.account.Account sendAcct1 = ontSdk.getWalletMgr().getAccount(info1.address,password,info1.getSalt());
        com.github.DNAProject.account.Account sendAcct2 = ontSdk.getWalletMgr().getAccount(info2.address,password,info2.getSalt());
        com.github.DNAProject.account.Account payerAcct = ontSdk.getWalletMgr().getAccount(payer.address,password,payer.getSalt());
        ontSdk.nativevm().gas().sendApprove(sendAcct1,sendAcct2.getAddressU160().toBase58(),10L,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
        long info1balance = ontSdk.nativevm().gas().queryBalanceOf(sendAcct1.getAddressU160().toBase58());
        long info2balance = ontSdk.nativevm().gas().queryBalanceOf(sendAcct2.getAddressU160().toBase58());
        Thread.sleep(6000);

        long allo = ontSdk.nativevm().gas().queryAllowance(sendAcct1.getAddressU160().toBase58(),sendAcct2.getAddressU160().toBase58());
        Assert.assertTrue(allo == 10);
        ontSdk.nativevm().gas().sendTransferFrom(sendAcct2,info1.address,sendAcct2.getAddressU160().toBase58(),10L,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        long info1balance2 = ontSdk.nativevm().gas().queryBalanceOf(info1.address);
        long info2balance2 = ontSdk.nativevm().gas().queryBalanceOf(info2.address);

        Assert.assertTrue((info1balance - info1balance2) == 10);
        Assert.assertTrue((info2balance2 - info2balance) == 10);


    }

    @Test
    public void sendOngTransferFrom() throws Exception {
        String unboundOngStr = ontSdk.nativevm().gas().unboundGas(info1.address);
        long unboundOng = Long.parseLong(unboundOngStr);
        String res = ontSdk.nativevm().gas().withdrawGas(ontSdk.getWalletMgr().getAccount(info1.address,password,info1.getSalt()),info2.address,unboundOng/100,ontSdk.getWalletMgr().getAccount(payer.address,password,payer.getSalt()),ontSdk.DEFAULT_GAS_LIMIT,0);
        Assert.assertNotNull(res);
    }

    @Test
    public void queryTest() throws Exception {

        long decimal = ontSdk.nativevm().gas().queryDecimals();
        long decimal2 = ontSdk.nativevm().gas().queryDecimals();
        Assert.assertNotNull(decimal);
        Assert.assertNotNull(decimal2);

        String name = ontSdk.nativevm().gas().queryName();

        String gassym = ontSdk.nativevm().gas().querySymbol();

        long total = ontSdk.nativevm().gas().queryTotalSupply();

    }
}