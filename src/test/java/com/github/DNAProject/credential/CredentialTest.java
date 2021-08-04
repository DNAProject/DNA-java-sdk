package com.github.DNAProject.credential;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.account.Account;
import com.github.DNAProject.common.*;
import com.github.DNAProject.core.payload.DeployCode;
import com.github.DNAProject.core.transaction.Transaction;
import com.github.DNAProject.crypto.Curve;
import com.github.DNAProject.crypto.ECC;
import com.github.DNAProject.dnaid.CredentialStatusType;
import com.github.DNAProject.dnaid.DnaId2;
import com.github.DNAProject.dnaid.ProofPurpose;
import com.github.DNAProject.sdk.exception.SDKException;
import com.github.DNAProject.sdk.wallet.Control;
import com.github.DNAProject.sdk.wallet.Identity;
import com.github.DNAProject.smartcontract.Vm;
import com.github.DNAProject.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.DNAProject.smartcontract.nativevm.abi.Struct;

import org.junit.Before;
import org.junit.Test;

import java.util.*;


public class CredentialTest {
    DnaSdk dnaSdk;
    public static final String DNAID_CONTRACT_ADDRESS = "0000000000000000000000000000000000000003";
    public static final String PAYER_ADDRESS = "AQFUKqrFdgR9A56ryyqDTXGLkdRFh7mbXY";
    public static final String PAYER_WIF = "L3exzrGEmqm1zYdkziXfNWebmXnBK4eu7yY2hUXH6kx6W6KMheMo";

    @Before
    public void setUp() throws SDKException {
        dnaSdk = DnaSdk.getInstance();
        dnaSdk.setRestful("http://1.116.79.177:20334");
        dnaSdk.setDefaultConnect(dnaSdk.getRestful());
        dnaSdk.openWalletFile("src\\test\\java\\com\\github\\DNAProject\\credential\\credentialTest.json");
        Vm.NATIVE_INVOKE_NAME = "DNA.Native.Invoke";
        dnaSdk.neovm().credentialRecord().setContractAddress("45867428484ccdfc1d1ad205d725ab7158f5b67b");
    }

    @Test
    public void deployContract() throws Exception {
        String code = "58c56b05322e302e306a00527ac42241476d7269314b786847736b6d6a733236366f4a347066626b79336e784e63516136681b444e412e52756e74696d652e426173653538546f416464726573736a51527ac41400000000000000000000000000000000000000036a52527ac4681953797374656d2e53746f726167652e476574436f6e746578746a53527ac46c0122c56b6a00527ac46a51527ac46a52527ac46a51c306436f6d6d69747d9c7c75645c006a52c3c0547d9e7c75640a00006c75666203006a52c300c36a53527ac46a52c351c36a54527ac46a52c352c36a55527ac46a52c353c36a56527ac46a56c36a55c36a54c36a53c3546a00c306a002000000006e6c75666203006a51c3065265766f6b657d9c7c75644f006a52c3c0537d9e7c75640a00006c75666203006a52c300c36a53527ac46a52c351c36a58527ac46a52c352c36a55527ac46a55c36a58c36a53c3536a00c3064b04000000006e6c75666203006a51c30652656d6f76657d9c7c75644f006a52c3c0537d9e7c75640a00006c75666203006a52c300c36a53527ac46a52c351c36a56527ac46a52c352c36a55527ac46a55c36a56c36a53c3536a00c3064406000000006e6c75666203006a51c3094765745374617475737d9c7c756435006a52c3c0517d9e7c75640a00006c75666203006a52c300c36a53527ac46a53c3516a00c306f307000000006e6c75666203006a51c307557067726164657d9c7c756483006a52c3c0577d9e7c75640a00006c75666203006a52c300c36a59527ac46a52c351c36a5a527ac46a52c352c36a5b527ac46a52c353c36a5c527ac46a52c354c36a5d527ac46a52c355c36a5e527ac46a52c356c36a5f527ac46a5fc36a5ec36a5dc36a5cc36a5bc36a5ac36a59c3576a00c3069b08000000006e6c75666203006c75665ec56b6a00527ac46a51527ac46a52527ac46a53527ac46a54527ac46a55527ac46203006a54c36a53c352c66b6a00527ac46a51527ac46c6a56527ac46a56c30f7665726966795369676e61747572656a00c352c3006811444e412e4e61746976652e496e766f6b656a57527ac46a57c301017d9e7c7564290021636f6d6d697465724964207665726966795369676e6174757265206572726f722ef06203006a55c358007b6b766b946c6c52727f086469643a646e613a7d9e7c75641f0017696c6c6567616c206f776e6572496420666f726d61742ef06203006a52c36a00c353c3681253797374656d2e53746f726167652e4765746a58527ac46a58c3c0007d9e7c756425001d436f6d6d69742c20636c61696d496420616c7265616479206578697374f06203006a52c3516a53c36a55c354c176c96a59527ac46a59c3681853797374656d2e52756e74696d652e53657269616c697a656a58527ac46a58c36a52c36a00c353c3681253797374656d2e53746f726167652e5075746a55c36a53c36a52c306436f6d6d697454c1681553797374656d2e52756e74696d652e4e6f74696679516c75665ec56b6a00527ac46a51527ac46a52527ac46a53527ac46a54527ac46203006a54c36a53c352c66b6a00527ac46a51527ac46c6a55527ac46a55c30f7665726966795369676e61747572656a00c352c3006811444e412e4e61746976652e496e766f6b656a56527ac46a56c301017d9e7c756424001c6f6e744964207665726966795369676e6174757265206572726f722ef06203006a52c36a00c353c3681253797374656d2e53746f726167652e4765746a57527ac46a57c3c0007d9c7c756424001c5265766f6b652c20636c61696d496420646f206e6f74206578697374f06203006a57c3681a53797374656d2e52756e74696d652e446573657269616c697a656a58527ac46a58c3c0547d9f7c756427001f5265766f6b652c20696c6c6567616c20666f726d6174206f6620636c61696df06203006a58c352c36a53c37d9e7c7576641000756a58c353c36a53c37d9e7c756432002a5265766f6b652c206f6e74496420646f206e6f7420686176652061636365737320746f207265766f6b65f0620300006a58c3517bc46a58c3681853797374656d2e52756e74696d652e53657269616c697a656a59527ac46a59c36a52c36a00c353c3681253797374656d2e53746f726167652e5075746a53c36a52c3065265766f6b6553c1681553797374656d2e52756e74696d652e4e6f74696679516c75665cc56b6a00527ac46a51527ac46a52527ac46a53527ac46a54527ac46203006a54c36a53c352c66b6a00527ac46a51527ac46c6a55527ac46a55c30f7665726966795369676e61747572656a00c352c3006811444e412e4e61746976652e496e766f6b656a56527ac46a56c301017d9e7c756426001e6f776e65724964207665726966795369676e6174757265206572726f722ef06203006a52c36a00c353c3681253797374656d2e53746f726167652e4765746a57527ac46a57c3c0007d9c7c756424001c52656d6f76652c20636c61696d496420646f206e6f74206578697374f06203006a57c3681a53797374656d2e52756e74696d652e446573657269616c697a656a58527ac46a58c3c0547d9f7c756427001f52656d6f76652c20696c6c6567616c20666f726d6174206f6620636c61696df06203006a58c353c36a53c37d9e7c756420001852656d6f76652c206f776e657249642069732077726f6e67f06203006a52c36a00c353c3681553797374656d2e53746f726167652e44656c6574656a53c36a52c30652656d6f766553c1681553797374656d2e52756e74696d652e4e6f74696679516c756659c56b6a00527ac46a51527ac46a52527ac46203006a52c36a00c353c3681253797374656d2e53746f726167652e4765746a53527ac46a53c3c0007d9c7c75640a00526c75666203006a53c3681a53797374656d2e52756e74696d652e446573657269616c697a656a54527ac46a54c3c0547d9f7c75642a00224765745374617475732c20696c6c6567616c20666f726d6174206f6620636c61696df06203006a54c351c36c75665dc56b6a00527ac46a51527ac46a52527ac46a53527ac46a54527ac46a55527ac46a56527ac46a57527ac46a58527ac46203006a00c351c3681b53797374656d2e52756e74696d652e436865636b5769746e65737391641b0013436865636b5769746e657373206661696c6564f06203006a58c36a57c36a56c36a55c36a54c36a53c36a52c36814444e412e436f6e74726163742e4d6967726174656a59527ac46a59c3916416000e4d696772617465206661696c6564f0620300516c7566";
        String name = "credential";
        String version = "1.0";
        String author = "author";
        String email = "email";
        String desc = "desc";
        String contractAddress = makeDeployCodeTransaction(code, name, version, author, email, desc);
        System.out.println("contractAddress:" + contractAddress);
    }

    @Test
    public void createDnaId() throws Exception {
        Map<String, String> dnaId = createDnaId("123456");
        System.out.println("dnaId:" + dnaId);
    }

    @Test
    public void createCred() {
        String issuerDnaId = "did:dna:AQFUKqrFdgR9A56ryyqDTXGLkdRFh7mbXY";
        String dnaIdPassword = "123456";
        String salt = "oxsJG8zko2L8DqeLIFD27A==";
        String context = "credential:demo";
        String subjectDnaId = "did:dna:AND16ZNbMDe2W5z1X4gcZfrHs9G29G11w2";
        Map<String, Object> credentialInfo = new HashMap<>();
        credentialInfo.put("Name", "ABC");
        credentialInfo.put("Gender", "male");
        credentialInfo.put("SubjectDnaId", subjectDnaId);
        long expires = 31536000000L;
        String credential = createCred(issuerDnaId, dnaIdPassword, salt, context, credentialInfo, expires);
        System.out.println("credential:" + credential);
    }

    @Test
    public void sendCredentialRecordTx() {
        String issuerDnaId = "did:dna:AQFUKqrFdgR9A56ryyqDTXGLkdRFh7mbXY";
        String dnaIdPassword = "123456";
        String salt = "oxsJG8zko2L8DqeLIFD27A==";
        String subjectDnaId = "did:dna:AND16ZNbMDe2W5z1X4gcZfrHs9G29G11w2";
        String credential = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpkbmE6QVFGVUtxckZkZ1I5QTU2cnl5cURUWEdMa2RSRmg3bWJYWSNrZXlzLTEiLCJ0eXAiOiJKV1QifQ==.eyJpc3MiOiJkaWQ6ZG5hOkFRRlVLcXJGZGdSOUE1NnJ5eXFEVFhHTGtkUkZoN21iWFkiLCJleHAiOjE2NTk2MDE2MTIsIm5iZiI6MTYyODA2NTYxMiwiaWF0IjoxNjI4MDY1NjEyLCJqdGkiOiJ1cm46dXVpZDpmZjM1ZDZjZC0wNTQ1LTQyZTEtOGQ1Zi05YzAwMzY3OWQzYmUiLCJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSIsImh0dHBzOi8vZG5haWQub250LmlvL2NyZWRlbnRpYWxzL3YxIiwiY3JlZGVudGlhbDpkZW1vIl0sInR5cGUiOlsiVmVyaWZpYWJsZUNyZWRlbnRpYWwiXSwiY3JlZGVudGlhbFN1YmplY3QiOnsiR2VuZGVyIjoibWFsZSIsIk5hbWUiOiJBQkMiLCJTdWJqZWN0RG5hSWQiOiJkaWQ6ZG5hOkFORDE2Wk5iTURlMlc1ejFYNGdjWmZySHM5RzI5RzExdzIifSwiY3JlZGVudGlhbFN0YXR1cyI6eyJpZCI6IjQ1ODY3NDI4NDg0Y2NkZmMxZDFhZDIwNWQ3MjVhYjcxNThmNWI2N2IiLCJ0eXBlIjoiQXR0ZXN0Q29udHJhY3QifSwicHJvb2YiOnsiY3JlYXRlZCI6IjIwMjEtMDgtMDRUMDg6MjY6NTJaIiwicHJvb2ZQdXJwb3NlIjoiYXNzZXJ0aW9uTWV0aG9kIn19fQ==.AbSjGYtWbj5EJV5Mpf+wzPTE8CTur6ACy6S54fl0x/ayRw0ndFAtST3duXmo0ZbdMKcXetBQeV5Fx2PrCTaltQM=";
        String txHash = sendCredentialRecordTx(issuerDnaId, dnaIdPassword, salt, subjectDnaId, credential);
        System.out.println("txHash:" + txHash);
    }

    @Test
    public void verifyCred() throws Exception {
        String issuerDnaId = "did:dna:AQFUKqrFdgR9A56ryyqDTXGLkdRFh7mbXY";
        String dnaIdPassword = "123456";
        String salt = "oxsJG8zko2L8DqeLIFD27A==";
        String credential = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpkbmE6QVFGVUtxckZkZ1I5QTU2cnl5cURUWEdMa2RSRmg3bWJYWSNrZXlzLTEiLCJ0eXAiOiJKV1QifQ==.eyJpc3MiOiJkaWQ6ZG5hOkFRRlVLcXJGZGdSOUE1NnJ5eXFEVFhHTGtkUkZoN21iWFkiLCJleHAiOjE2NTk2MDE2MTIsIm5iZiI6MTYyODA2NTYxMiwiaWF0IjoxNjI4MDY1NjEyLCJqdGkiOiJ1cm46dXVpZDpmZjM1ZDZjZC0wNTQ1LTQyZTEtOGQ1Zi05YzAwMzY3OWQzYmUiLCJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSIsImh0dHBzOi8vZG5haWQub250LmlvL2NyZWRlbnRpYWxzL3YxIiwiY3JlZGVudGlhbDpkZW1vIl0sInR5cGUiOlsiVmVyaWZpYWJsZUNyZWRlbnRpYWwiXSwiY3JlZGVudGlhbFN1YmplY3QiOnsiR2VuZGVyIjoibWFsZSIsIk5hbWUiOiJBQkMiLCJTdWJqZWN0RG5hSWQiOiJkaWQ6ZG5hOkFORDE2Wk5iTURlMlc1ejFYNGdjWmZySHM5RzI5RzExdzIifSwiY3JlZGVudGlhbFN0YXR1cyI6eyJpZCI6IjQ1ODY3NDI4NDg0Y2NkZmMxZDFhZDIwNWQ3MjVhYjcxNThmNWI2N2IiLCJ0eXBlIjoiQXR0ZXN0Q29udHJhY3QifSwicHJvb2YiOnsiY3JlYXRlZCI6IjIwMjEtMDgtMDRUMDg6MjY6NTJaIiwicHJvb2ZQdXJwb3NlIjoiYXNzZXJ0aW9uTWV0aG9kIn19fQ==.AbSjGYtWbj5EJV5Mpf+wzPTE8CTur6ACy6S54fl0x/ayRw0ndFAtST3duXmo0ZbdMKcXetBQeV5Fx2PrCTaltQM=";
        boolean verify = verifyCred(issuerDnaId, dnaIdPassword, salt, credential);
        System.out.println("verify:" + verify);
    }

    @Test
    public void checkEvent() throws Exception {
        String txHash = "206b58fb8ed882e9d468f95a5d892f3c1d02ac2e1dfe33f21cd413b230e6add0";
        Object event = checkEvent(txHash);
        System.out.println("event:" + event);
    }


    private String makeDeployCodeTransaction(String code, String name, String version, String author, String email, String desc) throws Exception {
        DeployCode tx = dnaSdk.vm().makeDeployCodeTransaction(code, true, name, version, author, email, desc, PAYER_ADDRESS, 30000000, 0);
        Account payer = new Account(Account.getPrivateKeyFromWIF(PAYER_WIF), dnaSdk.getWalletMgr().getSignatureScheme());
        dnaSdk.addSign(tx, payer);
        dnaSdk.getConnect().sendRawTransaction(tx);
        System.out.println("txHash:" + tx.hash().toString());
        String contractHash = Address.AddressFromVmCode(code).toHexString();
        return contractHash;
    }

    private Map<String, String> createDnaId(String pwd) throws Exception {
        Account payerAcct = new Account(Account.getPrivateKeyFromWIF(PAYER_WIF), dnaSdk.getWalletMgr().getSignatureScheme());

        Identity identity = createIdentity(dnaSdk, pwd);
        String txhash = sendRegister(dnaSdk, identity, pwd, payerAcct, 25000, 0);

        Map keystore = WalletQR.exportIdentityQRCode(dnaSdk.getWalletMgr().getWallet(), identity);
        keystore.put("publicKey", identity.controls.get(0).publicKey);
        String keystoreStr = JSON.toJSONString(keystore);
        String wif = exportWif(keystoreStr, pwd);

        HashMap<String, String> res = new HashMap<>();
        res.put("dnaId", identity.dnaid);
        res.put("keystore", keystoreStr);
        res.put("tx", txhash);
        res.put("wif", wif);

        return res;
    }

    private String sendRegister(DnaSdk dnaSdk, Identity ident, String password, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ident == null || password == null || password.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }

        String addr = ident.dnaid.replace(Common.diddna, "");
        String prikey = com.github.DNAProject.account.Account.getGcmDecodedPrivateKey(ident.controls.get(0).key, password,
                addr, ident.controls.get(0).getSalt(), dnaSdk.getWalletMgr().getWalletFile().getScrypt().getN(), dnaSdk.getWalletMgr().getSignatureScheme());
        Account account = new Account(Helper.hexToBytes(prikey), dnaSdk.getWalletMgr().getSignatureScheme());

        Transaction tx = makeRegister(dnaSdk, ident.dnaid, ident.controls.get(0).publicKey, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        dnaSdk.addSign(tx, account);
        dnaSdk.addSign(tx, payerAcct);

        boolean b = dnaSdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    private Transaction makeRegister(DnaSdk dnaSdk, String dnaId, String publickey, String payer, long gaslimit, long gasprice) throws Exception {
        if (payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }

        byte[] pk = Helper.hexToBytes(publickey);

        List list = new ArrayList();
        list.add(new Struct().add(dnaId, pk));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = dnaSdk.vm().buildNativeParams(new Address(Helper.hexToBytes(DNAID_CONTRACT_ADDRESS)), "regIDWithPublicKey", args, payer, gaslimit, gasprice);
        return tx;
    }


    private Identity createIdentity(DnaSdk dnaSdk, String password) throws Exception {
        String label = "";
        byte[] prikey = ECC.generateKey();
        byte[] salt = ECC.generateKey(16);
        Account account = new Account(prikey, dnaSdk.getWalletMgr().getSignatureScheme());
        com.github.DNAProject.sdk.wallet.Account acct;
        switch (dnaSdk.getWalletMgr().getSignatureScheme()) {
            case SHA256WITHECDSA:
                acct = new com.github.DNAProject.sdk.wallet.Account("ECDSA", new Object[]{Curve.P256.toString()}, "aes-256-gcm", "SHA256withECDSA", "sha256");
                break;
            case SM3WITHSM2:
                acct = new com.github.DNAProject.sdk.wallet.Account("SM2", new Object[]{Curve.SM2P256V1.toString()}, "aes-256-gcm", "SM3withSM2", "sha256");
                break;
            default:
                throw new SDKException(ErrorCode.OtherError("scheme type error"));
        }
        if (password != null) {
            acct.key = account.exportGcmEncryptedPrikey(password, salt, dnaSdk.getWalletMgr().getWalletFile().getScrypt().getN());
        } else {
            acct.key = Helper.toHexString(account.serializePrivateKey());
        }
        acct.address = Address.addressFromPubKey(account.serializePublicKey()).toBase58();
        if (label == null || label.equals("")) {
            String uuidStr = UUID.randomUUID().toString();
            label = uuidStr.substring(0, 8);
        }

        Identity idt = new Identity();
        idt.dnaid = Common.diddna + acct.address;
        idt.label = label;

        idt.controls = new ArrayList<Control>();
        Control ctl = new Control(acct.key, "keys-1", Helper.toHexString(account.serializePublicKey()));
        ctl.setSalt(salt);
        ctl.setAddress(acct.address);
        idt.controls.add(ctl);
        return idt;
    }

    private String exportWif(String keystore, String pwd) throws Exception {
        Account account = exportAccount(keystore, pwd);
        return account.exportWif();
    }

    private Account exportAccount(String keystoreBefore, String pwd) throws Exception {
        String keystore = keystoreBefore.replace("\\", "");
        JSONObject jsonObject = JSON.parseObject(keystore);
        String key = jsonObject.getString("key");
        String address = jsonObject.getString("address");
        String saltStr = jsonObject.getString("salt");

        int scrypt = jsonObject.getJSONObject("scrypt").getIntValue("n");
        String privateKey = Account.getGcmDecodedPrivateKey(key, pwd, address, com.alibaba.fastjson.util.Base64.decodeFast(saltStr), scrypt, dnaSdk.getWalletMgr().getSignatureScheme());
        return new Account(Helper.hexToBytes(privateKey), dnaSdk.getWalletMgr().getSignatureScheme());
    }

    private Map<String, String> createDnaId2(String pwd) throws Exception {
        Account payerAcct = new Account(Account.getPrivateKeyFromWIF(PAYER_WIF), dnaSdk.getWalletMgr().getSignatureScheme());
        HashMap<String, String> res = new HashMap<>();
        Identity identity = dnaSdk.getWalletMgr().createIdentity(pwd);
        String txhash = dnaSdk.nativevm().dnaId().sendRegister(identity, pwd, payerAcct, 20000, 0);
        dnaSdk.getWalletMgr().getWallet().clearIdentity();
        dnaSdk.getWalletMgr().writeWallet();
        Map keystore = WalletQR.exportIdentityQRCode(dnaSdk.getWalletMgr().getWallet(), identity);
        keystore.put("publicKey", identity.controls.get(0).publicKey);
        res.put("dnaId", identity.dnaid);
        res.put("keystore", JSON.toJSONString(keystore));

        res.put("tx", txhash);
        dnaSdk.getWalletMgr().getWallet().clearIdentity();
        return res;
    }

    private String createCred(String issuerDnaId, String dnaIdPassword, String salt, String context, Map credentialInfo, long expires) {
        try {
            Account issuerAcc = dnaSdk.getWalletMgr().getAccount(issuerDnaId, dnaIdPassword, java.util.Base64.getDecoder().decode(salt));
            DnaId2 issuer = new DnaId2(issuerDnaId, issuerAcc, dnaSdk.neovm().credentialRecord(), dnaSdk.nativevm().dnaId());
            Date expiration = new Date(System.currentTimeMillis() + expires);
            String cred = issuer.createJWTCred(new String[]{context}, new String[]{}, issuerDnaId, credentialInfo, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            return cred;
        } catch (Exception e) {
            System.out.println("createCred error...");
            e.printStackTrace();
        }
        return "";
    }

    private String sendCredentialRecordTx(String issuerDnaId, String dnaIdPassword, String salt, String subjectDnaId, String credential) {
        try {
            Account issuerAcc = dnaSdk.getWalletMgr().getAccount(issuerDnaId, dnaIdPassword, java.util.Base64.getDecoder().decode(salt));
            DnaId2 issuer = new DnaId2(issuerDnaId, issuerAcc, dnaSdk.neovm().credentialRecord(), dnaSdk.nativevm().dnaId());

            String txHash = issuer.commitCred(credential, subjectDnaId, issuerAcc, 20000, 0, dnaSdk);
            return txHash;
        } catch (Exception e) {
            System.out.println("sendCredentialRecordTx error...");
            e.printStackTrace();
        }
        return "";
    }

    private boolean verifyCred(String issuerDnaId, String dnaIdPassword, String salt, String credential) {
        try {
            Account issuerAcc = dnaSdk.getWalletMgr().getAccount(issuerDnaId, dnaIdPassword, java.util.Base64.getDecoder().decode(salt));
            DnaId2 issuer = new DnaId2(issuerDnaId, issuerAcc, dnaSdk.neovm().credentialRecord(), dnaSdk.nativevm().dnaId());
            String[] credibleDnaIds = new String[]{Common.diddna + issuerAcc.getAddressU160().toBase58()};
            boolean verify = issuer.verifyJWTCred(credibleDnaIds, credential);
            return verify;
        } catch (Exception e) {
            System.out.println("createCred error...");
            e.printStackTrace();
        }
        return false;
    }

    private Object checkEvent(String txHash) throws Exception {
        Object event = dnaSdk.getConnect().getSmartCodeEvent(txHash);
        return event;
    }
}


