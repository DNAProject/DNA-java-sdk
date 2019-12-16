package com.github.DNAProject.smartcontract.nativevm;
import com.github.DNAProject.DnaSdkTest;
import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.DnaSdkTest;
import com.github.DNAProject.account.Account;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.crypto.SignatureScheme;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GasTest {

    public String password = "111111";
    DnaSdk ontSdk;
    Account account;
    Account receiveAcc;

    @Before
    public void setUp() throws Exception {
        ontSdk=DnaSdk.getInstance();
        ontSdk.setRestful(DnaSdkTest.URL);
        account = new Account(Helper.hexToBytes(DnaSdkTest.PRIVATEKEY),SignatureScheme.SHA256WITHECDSA);
        receiveAcc = new Account(SignatureScheme.SHA256WITHECDSA);
        ontSdk.nativevm().gas().sendTransfer(account,receiveAcc.getAddressU160().toBase58(),10L,account,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);

        String accountOng = ontSdk.nativevm().gas().unboundGas(account.getAddressU160().toBase58());
        ontSdk.nativevm().gas().withdrawGas(account,account.getAddressU160().toBase58(),1000,account,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        Object obj = ontSdk.getConnect().getBalance(account.getAddressU160().toBase58());
        System.out.println(obj);
    }
    @Test
    public void sendTransfer() throws Exception {
        long accountOng = ontSdk.nativevm().gas().queryBalanceOf(account.getAddressU160().toBase58());
        long receiveAccOng = ontSdk.nativevm().gas().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().gas().sendTransfer(account,receiveAcc.getAddressU160().toBase58(),10L,account,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        long accountOng2 = ontSdk.nativevm().gas().queryBalanceOf(account.getAddressU160().toBase58());
        long receiveAccOng2 = ontSdk.nativevm().gas().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(accountOng-accountOng2 == 10);
        Assert.assertTrue(receiveAccOng2-receiveAccOng == 10);
    }


    @Test
    public void sendApprove() throws Exception {
        long allowance = ontSdk.nativevm().gas().queryAllowance(account.getAddressU160().toBase58(),receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().gas().sendApprove(account,receiveAcc.getAddressU160().toBase58(),10,account,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        long allowance2 = ontSdk.nativevm().gas().queryAllowance(account.getAddressU160().toBase58(),receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(allowance2-allowance == 10);

        long acctbalance = ontSdk.nativevm().gas().queryBalanceOf(account.getAddressU160().toBase58());
        long reciebalance = ontSdk.nativevm().gas().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().gas().sendTransferFrom(receiveAcc,account.getAddressU160().toBase58(),receiveAcc.getAddressU160().toBase58(),10,receiveAcc,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        long acctbalance2 = ontSdk.nativevm().gas().queryBalanceOf(account.getAddressU160().toBase58());
        long reciebalance2 = ontSdk.nativevm().gas().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(acctbalance-acctbalance2 == 10);
        Assert.assertTrue(reciebalance2 - reciebalance == 10);
        long allowance3 = ontSdk.nativevm().gas().queryAllowance(account.getAddressU160().toBase58(),receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(allowance3 == allowance);
    }

    @Test
    public void queryName() throws Exception {
        String name = ontSdk.nativevm().gas().queryName();
        String symbol = ontSdk.nativevm().gas().querySymbol();
        long decimals = ontSdk.nativevm().gas().queryDecimals();
        Assert.assertTrue(decimals == 9);
        long total = ontSdk.nativevm().gas().queryTotalSupply();
        Assert.assertFalse(total < 0);
    }
}