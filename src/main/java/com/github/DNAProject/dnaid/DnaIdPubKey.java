package com.github.DNAProject.dnaid;

public class DnaIdPubKey {
    public String id; // pubkey URI
    public PubKeyType type; // pubkey type, for example: EcdsaSecp256r1VerificationKey2019
    public String controller;
    public String publicKeyHex;
}
