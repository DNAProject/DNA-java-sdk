package example.dnaid;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.common.Address;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.core.dnaid.Attribute;
import com.github.DNAProject.core.transaction.Transaction;
import com.github.DNAProject.crypto.SignatureScheme;
import com.github.DNAProject.sdk.info.IdentityInfo;
import com.github.DNAProject.sdk.wallet.Account;
import com.github.DNAProject.sdk.wallet.Identity;

import java.util.List;

public class DnaIdDemo {

    public static void main(String[] args) {

        String password = "111111";

        try {
            DnaSdk dnaSdk = getDnaSdk();

            Account payer = dnaSdk.getWalletMgr().createAccount(password);

            com.github.DNAProject.account.Account payerAcct = dnaSdk.getWalletMgr().getAccount(payer.address,password,dnaSdk.getWalletMgr().getWallet().getAccount(payer.address).getSalt());
            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            String privatekey1 = "2ab720ff80fcdd31a769925476c26120a879e235182594fbb57b67c0743558d7";
            com.github.DNAProject.account.Account account1 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);

            if(false){
                Account account = dnaSdk.getWalletMgr().createAccount(password);
                com.github.DNAProject.account.Account account2 = dnaSdk.getWalletMgr().getAccount(account.address,password,account.getSalt());
                Identity identity = dnaSdk.getWalletMgr().createIdentityFromPriKey(password, Helper.toHexString(account2.serializePrivateKey()));
                dnaSdk.nativevm().dnaId().sendRegister(identity,password,account2,20000,0);
                Thread.sleep(6000);
                System.out.println(dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid));
                return;
            }


            if(false){
                Identity identity3 = dnaSdk.getWalletMgr().createIdentity(password);
                Attribute[] attributes = new Attribute[1];
                attributes[0] = new Attribute("key1".getBytes(),"String".getBytes(),"value1".getBytes());
                dnaSdk.nativevm().dnaId().sendRegisterWithAttrs(identity3,password,attributes,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                dnaSdk.getWalletMgr().writeWallet();
                Thread.sleep(6000);
                String ddo = dnaSdk.nativevm().dnaId().sendGetDDO(identity3.dnaid);
                System.out.println(ddo);
                System.exit(0);
            }
            if(true){
                if(dnaSdk.getWalletMgr().getWallet().getIdentities().size() < 1){
//                    Identity identity = dnaSdk.getWalletMgr().createIdentity(password);
                    Identity identity = dnaSdk.getWalletMgr().createIdentityFromPriKey(password,privatekey0);
                    dnaSdk.nativevm().dnaId().sendRegister(identity,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                    dnaSdk.getWalletMgr().writeWallet();
                    Thread.sleep(6000);
                    return;
                }
                Identity identity = dnaSdk.getWalletMgr().getWallet().getIdentities().get(0);
//                String ddo = dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid);
//                System.out.println(ddo);

                Attribute[] attributes = new Attribute[1];
                attributes[0] = new Attribute("key1".getBytes(),"String".getBytes(),"value1".getBytes());
                byte[] salt = identity.controls.get(0).getSalt();
//                dnaSdk.nativevm().dnaId().sendAddAttributes(identity.dnaid,password,identity.controls.get(0).getSalt(),attributes,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
//                dnaSdk.nativevm().dnaId().sendRemoveAttribute(identity.dnaid,password,identity.controls.get(0).getSalt(),"key1",payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                dnaSdk.nativevm().dnaId().sendAddRecovery(identity.dnaid,password,salt,account1.getAddressU160().toBase58(),payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
//                dnaSdk.nativevm().dnaId().sendAddPubKey(identity.dnaid,password,salt,Helper.toHexString(account1.serializePublicKey()),payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
//                dnaSdk.nativevm().dnaId().sendRemovePubKey(identity.dnaid,password,salt,Helper.toHexString(account1.serializePublicKey()),payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                String ddo2 = dnaSdk.nativevm().dnaId().sendGetDDO(identity.dnaid);
                System.out.println(ddo2);
                System.out.println(account1.getAddressU160().toBase58());
                System.exit(0);
            }
            Account account = dnaSdk.getWalletMgr().createAccountFromPriKey(password,privatekey0);
            if(dnaSdk.getWalletMgr().getWallet().getIdentities().size() < 3){
                Identity identity = dnaSdk.getWalletMgr().createIdentity(password);
                Transaction tx = dnaSdk.nativevm().dnaId().makeRegister(identity.dnaid,identity.controls.get(0).publicKey,payer.address,dnaSdk.DEFAULT_GAS_LIMIT,0);
                dnaSdk.signTx(tx,identity.dnaid,password,new byte[]{});
                dnaSdk.addSign(tx,payerAcct);
                dnaSdk.getConnect().sendRawTransaction(tx);

                Identity identity2 = dnaSdk.getWalletMgr().createIdentity(password);
                dnaSdk.nativevm().dnaId().sendRegister(identity2,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);

                Identity identity3 = dnaSdk.getWalletMgr().createIdentity(password);
                Attribute[] attributes = new Attribute[1];
                attributes[0] = new Attribute("key1".getBytes(),"String".getBytes(),"value1".getBytes());
                dnaSdk.nativevm().dnaId().sendRegisterWithAttrs(identity3,password,attributes,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                dnaSdk.getWalletMgr().writeWallet();
                Thread.sleep(6000);

            }
            List<Identity> dids = dnaSdk.getWalletMgr().getWallet().getIdentities();
            System.out.println("dids.get(0).dnaid:" + dids.get(0).dnaid);
//            System.out.println("dids.get(1).dnaid:" + dids.get(1).dnaid);
//            System.out.println("dids.get(2).dnaid:" + dids.get(2).dnaid);
            String ddo1 = dnaSdk.nativevm().dnaId().sendGetDDO(dids.get(0).dnaid);
//            String publicKeys = dnaSdk.nativevm().dnaId().sendGetPublicKeys(dids.get(0).dnaid);
//            String ddo2 = dnaSdk.nativevm().dnaId().sendGetDDO(dids.get(1).dnaid);
//            String ddo3 = dnaSdk.nativevm().dnaId().sendGetDDO(dids.get(2).dnaid);

            System.out.println("ddo1:" + ddo1);
//            System.out.println("ddo2:" + ddo2);
//            System.out.println("ddo3:" + ddo3);

            IdentityInfo info2 = dnaSdk.getWalletMgr().getIdentityInfo(dids.get(1).dnaid,password,new byte[]{});
            IdentityInfo info3 = dnaSdk.getWalletMgr().getIdentityInfo(dids.get(2).dnaid,password,new byte[]{});

            com.github.DNAProject.account.Account acct = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey0),SignatureScheme.SHA256WITHECDSA);
            com.github.DNAProject.account.Account acct2 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);
            Address multiAddr = Address.addressFromMultiPubKeys(2,acct.serializePublicKey(),acct2.serializePublicKey());

            if(false){
                Account account2 = dnaSdk.getWalletMgr().createAccountFromPriKey(password, privatekey1);
//                dnaSdk.nativevm().dnaId().sendChangeRecovery(dids.get(0).dnaid,account2.address,account.address,password,dnaSdk.DEFAULT_GAS_LIMIT,0);
                String txhash2 = dnaSdk.nativevm().dnaId().sendAddRecovery(dids.get(0).dnaid,password,new byte[]{},multiAddr.toBase58(),payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = dnaSdk.getConnect().getSmartCodeEvent(txhash2);
                System.out.println(obj);
                System.out.println(dnaSdk.nativevm().dnaId().sendGetDDO(dids.get(0).dnaid));
            }

            if(false){
                dnaSdk.nativevm().dnaId().sendAddPubKey(dids.get(0).dnaid,password,new byte[]{},info3.pubkey,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                dnaSdk.nativevm().dnaId().sendRemovePubKey(dids.get(0).dnaid,account.address,password,new byte[]{},info2.pubkey,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                dnaSdk.nativevm().dnaId().sendAddPubKey(dids.get(0).dnaid,account.address,password,new byte[]{},info2.pubkey,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                Transaction tx = dnaSdk.nativevm().dnaId().makeAddPubKey(dids.get(0).dnaid,multiAddr.toBase58(),null,info2.pubkey,payer.address,dnaSdk.DEFAULT_GAS_LIMIT,0);
    //          dnaSdk.signTx(tx,new com.github.DNAProject.account.Account[][]{{acct,acct2}});
    //          dnaSdk.addSign(tx,payerAcc.address,password);
    //          dnaSdk.getConnect().sendRawTransaction(tx.toHexString());
            }


            if(false){
                String ddo4 = dnaSdk.nativevm().dnaId().sendGetDDO(dids.get(0).dnaid);
                System.out.println("ddo4:" + ddo4);
                System.exit(0);
                System.out.println("ddo1:" + ddo1);
                System.out.println("publicKeysState:" + dnaSdk.nativevm().dnaId().sendGetKeyState(dids.get(0).dnaid,1));
                System.out.println("attributes:" + dnaSdk.nativevm().dnaId().sendGetAttributes(dids.get(0).dnaid));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static DnaSdk getDnaSdk() throws Exception {
        String ip = "http://127.0.0.1";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        DnaSdk wm = DnaSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("DnaIdDemo.json");
        return wm;
    }
}
