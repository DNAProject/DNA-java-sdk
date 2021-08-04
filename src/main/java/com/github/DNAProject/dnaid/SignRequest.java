package com.github.DNAProject.dnaid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;

@JSONType(orders = {"credentialSubject", "dnaId", "proof"})
public class SignRequest {
    Object credentialSubject;
    String dnaId;
    Proof proof;

    public SignRequest(Object credentialSubject, String dnaId, Proof proof) {
        this.credentialSubject = credentialSubject;
        this.dnaId = dnaId;
        this.proof = proof;
    }

    public byte[] genNeedSignData() {
        Proof proof = this.proof;
        this.proof = this.proof.genNeedSignProof();
        String jsonStr = JSON.toJSONString(this, SerializerFeature.MapSortField);
        this.proof = proof;
        return jsonStr.getBytes();
    }
}
