package com.github.DNAProject.sdk.wallet;

import com.github.DNAProject.DnaSdk;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class WalletTest {

    DnaSdk dnaSdk;
    Identity id1;
    Identity id2;
    Account acct1;
    Account acct2;

    String walletFile = "WalletTest.json";

    @Before
    public void setUp() throws Exception {
        dnaSdk = DnaSdk.getInstance();
        dnaSdk.openWalletFile(walletFile);


        id1 = dnaSdk.getWalletMgr().createIdentity("passwordtest");
        id2 = dnaSdk.getWalletMgr().createIdentity("passwordtest");

        acct1 = dnaSdk.getWalletMgr().createAccount("passwordtest");
        acct2 = dnaSdk.getWalletMgr().createAccount("passwordtest");
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
    public void getAccount() throws Exception {
        Account acct = dnaSdk.getWalletMgr().getWallet().getAccount(acct1.address);
        Assert.assertNotNull(acct);

        dnaSdk.getWalletMgr().getWallet().setDefaultIdentity(id1.dnaid);
        dnaSdk.getWalletMgr().getWallet().setDefaultIdentity(1);
        dnaSdk.getWalletMgr().getWallet().setDefaultAccount(acct1.address);
        dnaSdk.getWalletMgr().getWallet().setDefaultAccount(1);
        Identity did = dnaSdk.getWalletMgr().getWallet().getIdentity(id1.dnaid);
        Assert.assertNotNull(did);
        boolean b = dnaSdk.getWalletMgr().getWallet().removeIdentity(id1.dnaid);
        Assert.assertTrue(b);

        boolean b2 = dnaSdk.getWalletMgr().getWallet().removeAccount(acct1.address);
        Assert.assertTrue(b2);


    }


}