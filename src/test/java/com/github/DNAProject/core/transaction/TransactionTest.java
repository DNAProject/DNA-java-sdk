package com.github.DNAProject.core.transaction;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.account.Account;
import com.github.DNAProject.common.Address;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.crypto.SignatureScheme;
import com.github.DNAProject.smartcontract.Vm;
import org.junit.Before;
import org.junit.Test;

public class TransactionTest {

    DnaSdk dnaSdk;
    Vm vm;
    String gasContract = "0000000000000000000000000000000000000002";

    @Before
    public void setUp(){
        dnaSdk = DnaSdk.getInstance();
        vm = new Vm(dnaSdk);
    }

    @Test
    public void serialize() throws Exception {
        Transaction tx = vm.buildNativeParams(Address.parse(gasContract),"init","1".getBytes(),null,0,0);
        Account account = new Account(Helper.hexToBytes("0bc8c1f75a028672cd42c221bf81709dfc7abbbaf0d87cb6fdeaf9a20492c194"),SignatureScheme.SHA256WITHECDSA);
        dnaSdk.signTx(tx,new Account[][]{{account}});

        String t = tx.toHexString();
        System.out.println(t);

        Transaction tx2 = Transaction.deserializeFrom(Helper.hexToBytes(t));
        System.out.println(tx2.json());


    }
}