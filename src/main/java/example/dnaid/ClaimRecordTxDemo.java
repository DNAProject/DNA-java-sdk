package example.dnaid;


import com.alibaba.fastjson.JSONObject;
import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.common.Common;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.crypto.SignatureScheme;
import com.github.DNAProject.sdk.info.AccountInfo;
import com.github.DNAProject.sdk.wallet.Account;
import com.github.DNAProject.sdk.wallet.Identity;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimRecordTxDemo {
    public static void main(String[] args) {

        try {
            DnaSdk dnaSdk = getDnaSdk();




            String password = "111111";

            Account payerAccInfo = dnaSdk.getWalletMgr().createAccount(password);
            com.github.DNAProject.account.Account payerAcc = dnaSdk.getWalletMgr().getAccount(payerAccInfo.address,password,payerAccInfo.getSalt());


            if (dnaSdk.getWalletMgr().getWallet().getIdentities().size() < 2) {
                Identity identity = dnaSdk.getWalletMgr().createIdentity(password);

                dnaSdk.nativevm().dnaId().sendRegister(identity,password,payerAcc,dnaSdk.DEFAULT_GAS_LIMIT,0);

                Identity identity2 = dnaSdk.getWalletMgr().createIdentity(password);

                dnaSdk.nativevm().dnaId().sendRegister(identity2,password,payerAcc,dnaSdk.DEFAULT_GAS_LIMIT,0);

                dnaSdk.getWalletMgr().writeWallet();

                Thread.sleep(6000);
            }

            List<Identity> dids = dnaSdk.getWalletMgr().getWallet().getIdentities();


            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Issuer", dids.get(0).dnaid);
            map.put("Subject", dids.get(1).dnaid);

            Map clmRevMap = new HashMap();
            clmRevMap.put("typ","AttestContract");
            clmRevMap.put("addr",dids.get(1).dnaid.replace(Common.diddna,""));

            String claim = dnaSdk.nativevm().dnaId().createDnaIdClaim(dids.get(0).dnaid,password,dids.get(0).controls.get(0).getSalt(), "claim:context", map, map,clmRevMap,System.currentTimeMillis()/1000 +100000);
            System.out.println(claim);

            boolean b = dnaSdk.nativevm().dnaId().verifyDnaIdClaim(claim);
            System.out.println(b);

//            System.exit(0);

            Account account = dnaSdk.getWalletMgr().importAccount("blDuHRtsfOGo9A79rxnJFo2iOMckxdFDfYe2n6a9X+jdMCRkNUfs4+C4vgOfCOQ5","111111","AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve",Base64.getDecoder().decode("0hAaO6CT+peDil9s5eoHyw=="));
            AccountInfo info = dnaSdk.getWalletMgr().getAccountInfo(account.address,"111111",account.getSalt());
            com.github.DNAProject.account.Account account1 = new com.github.DNAProject.account.Account(Helper.hexToBytes("75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf"),SignatureScheme.SHA256WITHECDSA);


            String[] claims = claim.split("\\.");

            JSONObject payload = JSONObject.parseObject(new String(Base64.getDecoder().decode(claims[1].getBytes())));

            System.out.println("ClaimId:" + payload.getString("jti"));

//            dnaSdk.neovm().claimRecord().setContractAddress("9a4c79ee4379a0b5d10db03553ca7e61e17a8977");
            //
//            String getstatusRes9 = dnaSdk.neovm().claimRecord().sendGetStatus(payload.getString("jti"));
//            System.out.println("getstatusResBytes:" + getstatusRes9);

            String commitHash = dnaSdk.neovm().claimRecord().sendCommit(dids.get(0).dnaid,password,dids.get(0).controls.get(0).getSalt(),dids.get(1).dnaid,payload.getString("jti"),account1,dnaSdk.DEFAULT_GAS_LIMIT,0);
            System.out.println("commitRes:" + commitHash);
            Thread.sleep(6000);
            Object obj = dnaSdk.getConnect().getSmartCodeEvent(commitHash);
            System.out.println(obj);


            System.out.println(Helper.toHexString(dids.get(0).dnaid.getBytes()));
            System.out.println(Helper.toHexString(dids.get(1).dnaid.getBytes()));
            System.out.println(Helper.toHexString(payload.getString("jti").getBytes()));
            System.out.println(payload.getString("jti"));


            String getstatusRes = dnaSdk.neovm().claimRecord().sendGetStatus(payload.getString("jti"));
            System.out.println("getstatusResBytes:" + getstatusRes);
            Thread.sleep(6000);

//            System.exit(0);

            String revokeHash = dnaSdk.neovm().claimRecord().sendRevoke(dids.get(0).dnaid,password,dids.get(0).controls.get(0).getSalt(),payload.getString("jti"),account1,dnaSdk.DEFAULT_GAS_LIMIT,0);
            System.out.println("revokeRes:" + revokeHash);
            Thread.sleep(6000);
            System.out.println(dnaSdk.getConnect().getSmartCodeEvent(revokeHash));


            String getstatusRes2 = dnaSdk.neovm().claimRecord().sendGetStatus(payload.getString("jti"));

            System.out.println("getstatusResBytes2:" + getstatusRes2);

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DnaSdk getDnaSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        DnaSdk wm = DnaSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("ClaimRecordTxDemo.json");

        return wm;
    }
}

