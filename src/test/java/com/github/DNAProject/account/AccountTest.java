package com.github.DNAProject.account;

import com.github.DNAProject.common.Helper;
import com.github.DNAProject.crypto.SignatureScheme;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountTest {


    @Test
    public void generateSignature() throws Exception {
        Account account = new Account(SignatureScheme.SHA256WITHECDSA);
        byte[]  signature = account.generateSignature("hello".getBytes(),SignatureScheme.SHA256WITHECDSA,null);
        boolean b = account.verifySignature("hello".getBytes(),signature);
        assertTrue(b);
    }

    @Test
    public void serializePublicKey() throws Exception {
        Account account = new Account(SignatureScheme.SHA256WITHECDSA);
        byte[] publickey = account.serializePublicKey();
        assertNotNull(publickey);
    }

    @Test
    public void serializePrivateKey() throws Exception {
        Account account = new Account(SignatureScheme.SHA256WITHECDSA);
        byte[] privateKey = account.serializePrivateKey();
        assertNotNull(privateKey);
    }

    @Test
    public void compareTo() throws Exception {
        Account account1 = new Account(SignatureScheme.SHA256WITHECDSA);
        Account account2 = new Account(SignatureScheme.SHA256WITHECDSA);
        int res = account1.compareTo(account2);
        assertNotNull(res);
    }


}