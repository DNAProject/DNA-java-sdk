package example.dnaid;
import com.alibaba.fastjson.JSON;
import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.sdk.wallet.Account;
import com.github.DNAProject.sdk.wallet.Identity;
import com.github.DNAProject.smartcontract.neovm.abi.AbiFunction;
import com.github.DNAProject.smartcontract.neovm.abi.AbiInfo;

import java.util.List;

public class AuthDemo {

    public static void main(String[] args){
        DnaSdk dnaSdk;
        String password = "111111";
        String abi = "{\"hash\":\"0x4d0d780599010f943c37c795a22f6161d49436cf\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"token\",\"type\":\"Array\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"foo\",\"parameters\":[{\"name\":\"operation\",\"type\":\"ByteArray\"},{\"name\":\"token\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"init\",\"parameters\":[],\"returntype\":\"Boolean\"}],\"events\":[]}";
        Account payer;
        try {
            dnaSdk = getDnaSdk();
//            System.out.println(Helper.toHexString("initContractAdmin".getBytes()));
//            System.exit(0);
            // 8007c33f29a892e3a36e2cfec657eff1d7431e8f
            String privatekey0 = "523c5fcf74823831756f0bcb3634234f10b3beb1c05595058534577752ad2d9f";
            String privatekey1 ="83614c773f668a531132e765b5862215741c9148e7b2f9d386b667e4fbd93e39";
            com.github.DNAProject.account.Account acct0 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey0), dnaSdk.defaultSignScheme);

            com.github.DNAProject.account.Account account = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey1), dnaSdk.defaultSignScheme);

            payer = dnaSdk.getWalletMgr().createAccount(password);
            com.github.DNAProject.account.Account payerAcct = dnaSdk.getWalletMgr().getAccount(payer.address,password,payer.getSalt());
            Identity identity = null;
            Identity identity2 = null;
            Identity identity3 = null;
            List<Identity> dids = dnaSdk.getWalletMgr().getWallet().getIdentities();
            if(dnaSdk.getWalletMgr().getWallet().getIdentities().size() < 3){
               // Identity identity1 = dnaSdk.getWalletMgr().importIdentity("",password,"".getBytes(),acct0.getAddressU160().toBase58());
                identity = dnaSdk.getWalletMgr().createIdentityFromPriKey(password,privatekey0);

                dnaSdk.nativevm().dnaId().sendRegister(identity,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
              //  dnaSdk.nativevm().dnaId().sendRegister(identity1,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                identity2 = dnaSdk.getWalletMgr().createIdentity(password);
                dnaSdk.nativevm().dnaId().sendRegister(identity2,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);

                identity3 = dnaSdk.getWalletMgr().createIdentity(password);
                dnaSdk.nativevm().dnaId().sendRegister(identity3,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);

                dnaSdk.getWalletMgr().writeWallet();

                Thread.sleep(6000);
            }else {
                identity = dnaSdk.getWalletMgr().getWallet().getIdentity(dids.get(0).dnaid);
                identity2 = dnaSdk.getWalletMgr().getWallet().getIdentity(dids.get(1).dnaid);
                identity3 = dnaSdk.getWalletMgr().getWallet().getIdentity(dids.get(2).dnaid);
            }



            System.out.println("dnaid1:" +dids.get(0).dnaid+" "+Helper.toHexString(dids.get(0).dnaid.getBytes()));
            System.out.println("dnaid2:" +dids.get(1).dnaid);
            System.out.println("dnaid3:" +dids.get(2).dnaid);
            Account account1 = dnaSdk.getWalletMgr().createAccount(password);
            System.out.println("####" + account1.address);

//            System.out.println("ddo1:" + dnaSdk.nativevm().dnaId().sendGetDDO(dids.get(0).dnaid));
//            System.out.println("ddo2:" + dnaSdk.nativevm().dnaId().sendGetDDO(dids.get(1).dnaid));
//            System.out.println("ddo3:" + dnaSdk.nativevm().dnaId().sendGetDDO(dids.get(2).dnaid));

            String contractAddr = "b93f1d81a00f95d09228f1f8934a71dd0e89999f";

            if(false){
                 identity = dnaSdk.getWalletMgr().createIdentityFromPriKey(password,privatekey1);
                dnaSdk.nativevm().dnaId().sendRegister(identity,password,payerAcct,dnaSdk.DEFAULT_GAS_LIMIT,0);
                System.out.println(Helper.toHexString(identity.dnaid.getBytes()));

            }
            //Identity identity = dnaSdk.getWalletMgr().createIdentityFromPriKey(password,privatekey1);
            System.out.println(account.getAddressU160().toBase58());
            System.out.println(identity.dnaid);
            System.out.println(Helper.toHexString(account.getAddressU160().toArray()));
            System.out.println(Helper.toHexString(identity.dnaid.getBytes()));

            if(false){
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "init";
                AbiFunction func = abiinfo.getFunction(name);
                func.name = name;
                System.out.println(func);
                func.setParamsValue();
                //Object obj =  dnaSdk.neovm().sendTransaction(Helper.reverse(contractAddr),null,null,0,0,func, true);
                Object obj =  dnaSdk.neovm().sendTransaction(Helper.reverse(contractAddr),acct0,acct0,30000,0,func, false);
                System.out.println(obj);
            }

            if(false){

//                String txhash = dnaSdk.nativevm().auth().sendInit(dids.get(0).dnaid,password,codeaddress,account,dnaSdk.DEFAULT_GAS_LIMIT,0);

                //String txhash = dnaSdk.nativevm().auth().sendTransfer(identity.dnaid,password,identity.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),identity2.dnaid,account,dnaSdk.DEFAULT_GAS_LIMIT,0);

              //  String txhash = dnaSdk.nativevm().auth().assignFuncsToRole(identity2.dnaid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),"role",new String[]{"foo"},account,dnaSdk.DEFAULT_GAS_LIMIT,0);
                String txhash = dnaSdk.nativevm().auth().assignDnaIdsToRole(identity2.dnaid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),"role",new String[]{identity2.dnaid},account,dnaSdk.DEFAULT_GAS_LIMIT,0);
                //String txhash = dnaSdk.nativevm().auth().delegate(identity2.dnaid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),identity3.dnaid,"role",6000,1,account,dnaSdk.DEFAULT_GAS_LIMIT,0);
               //String txhash = dnaSdk.nativevm().auth().withdraw(identity2.dnaid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),identity3.dnaid,"role",account,dnaSdk.DEFAULT_GAS_LIMIT,0);
              //  String txhash = dnaSdk.nativevm().auth().verifyToken(identity2.dnaid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(contractAddr),"foo");
//                Thread.sleep(6000);
//                Object object = dnaSdk.getConnect().getSmartCodeEvent(txhash);
//                System.out.println(object);


//     String txhash2 = dnaSdk.nativevm().auth().withdraw(dids.get(0).dnaid,password,contractAddr,dids.get(1).dnaid,"role",1,payer.address,password,dnaSdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object object2 = dnaSdk.getConnect().getSmartCodeEvent(txhash);
                System.out.println(object2);
            }
            if(true){
                dnaSdk.nativevm().auth().queryAuth(contractAddr,"role",identity2.dnaid);
            }
            if(false){
                AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                String name = "foo";
                AbiFunction func = abiinfo.getFunction(name);
                func.name = name;
                System.out.println(func);
                func.setParamsValue(identity2.dnaid.getBytes(),Long.valueOf(1));

                acct0 = dnaSdk.getWalletMgr().getAccount(identity2.dnaid,password,identity2.controls.get(0).getSalt());
                System.out.println("pk:"+Helper.toHexString(acct0.serializePublicKey()));
                Object obj =  dnaSdk.neovm().sendTransaction(Helper.reverse(contractAddr),acct0,account,30000,0,func, true);
                System.out.println(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static DnaSdk getDnaSdk() throws Exception {

//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String ip = "http://127.0.0.1";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        DnaSdk wm = DnaSdk.getInstance();
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.openWalletFile("AuthDemo.json");
        return wm;
    }
}
