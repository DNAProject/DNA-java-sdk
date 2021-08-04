package com.github.DNAProject.dnaid;

import com.github.DNAProject.account.Account;

public class DnaIdSigner {
    String dnaId;
    DnaIdPubKey pubKey;
    Account signer;

    public DnaIdSigner(String dnaId, DnaIdPubKey pubKey, Account signer) {
        this.dnaId = dnaId;
        this.pubKey = pubKey;
        this.signer = signer;
    }

//    public byte[] hash(byte[] msg) throws Exception {
//        return pubKey.type.getAlg().hash(msg);
//    }
}
