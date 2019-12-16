package com.github.DNAProject.sdk.manager;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.DnaSdkTest;
import com.github.DNAProject.sdk.wallet.Account;
import com.github.DNAProject.sdk.wallet.Identity;
import com.github.DNAProject.sdk.wallet.Wallet;
import com.github.DNAProject.smartcontract.nativevm.DnaId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class WalletMgrTest {
    private DnaSdk dnaSdk;
    private WalletMgr walletMgr;
    private Wallet wallet;
    private DnaId dnaIdTx;

    String password = "111111";
    byte[] salt = new byte[]{};
    Account payer;

    String walletFile = "wallet.json";

    @Before
    public void setUp() throws Exception {
        dnaSdk = DnaSdk.getInstance();
        dnaSdk.setRestful(DnaSdkTest.URL);
        dnaSdk.openWalletFile(walletFile);
        walletMgr = dnaSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        dnaIdTx = dnaSdk.nativevm().dnaId();
        payer = dnaSdk.getWalletMgr().createAccount(password);

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
    public void openWallet() {
        dnaSdk.openWalletFile("wallet.json");
        walletMgr = dnaSdk.getWalletMgr();
        assertNotNull(walletMgr);
    }

    @Test
    public void getWallet() {
    }

    @Test
    public void writeWallet() throws Exception {
        walletMgr.writeWallet();
        File f = new File("wallet.json");
        boolean isExist = f.exists() && !f.isDirectory();
        assertTrue(isExist);
    }

    @Test
    public void createIdentity() throws Exception {
         Identity identity = walletMgr.createIdentity(password);
         com.github.DNAProject.account.Account account = walletMgr.getAccount(identity.dnaid,password,identity.controls.get(0).getSalt());
         assertNotNull(account);
         assertNotNull(identity);
         assertNotNull(identity.dnaid);
         assertNotEquals(identity.dnaid,"");
    }

    @Test
    public void importIdentity() throws Exception {
        List<Identity> identities = wallet.getIdentities();
        identities.clear();
        walletMgr.writeWallet();
        assertEquals(identities.size(), 0);

        Identity identity = walletMgr.createIdentity(password);
        com.github.DNAProject.account.Account account = walletMgr.getAccount(identity.dnaid,password,identity.controls.get(0).getSalt());
        String prikeyStr = account.exportGcmEncryptedPrikey(password,identity.controls.get(0).getSalt(),16384);
        assertTrue(identities.size() == 1);
        identities.clear();
        walletMgr.writeWallet();
        assertTrue(identities.size() == 0);

        String addr = identity.dnaid.substring(8);
        walletMgr.importIdentity(prikeyStr,password,identity.controls.get(0).getSalt(),addr);
        assertTrue(identities.size() == 1);
        Identity identity1 = identities.get(0);
        assertEquals(identity.dnaid,identity1.dnaid);
    }

    @Test
    public void importAccount() throws Exception {
        List<Account> accounts = walletMgr.getWallet().getAccounts();
        accounts.clear();
        assertEquals(accounts.size(), 0);
        walletMgr.writeWallet();
        Account account = walletMgr.createAccount(password);
        com.github.DNAProject.account.Account accountDiff = walletMgr.getAccount(account.address,password,account.getSalt());
        String prikeyStr = accountDiff.exportGcmEncryptedPrikey(password,account.getSalt(),16384);
       assertTrue(accounts.size() == 1);
       accounts.clear();
       assertTrue(accounts.size() == 0);
       walletMgr.writeWallet();

       Account account1 = walletMgr.importAccount(prikeyStr,password,account.address,account.getSalt());
       assertTrue(accounts.size() == 1);
       assertEquals(account.address, account1.address);

    }
}