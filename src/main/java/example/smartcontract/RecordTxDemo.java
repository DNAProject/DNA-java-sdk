package example.smartcontract;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.sdk.wallet.Identity;

public class RecordTxDemo {


    public static void main(String[] args){
        try {
            DnaSdk dnaSdk = getDnaSdk();

            if(dnaSdk.getWalletMgr().getWallet().getIdentities().size() < 1) {

                dnaSdk.getWalletMgr().createIdentity("passwordtest");
                dnaSdk.getWalletMgr().writeWallet();
            }


            Identity id = dnaSdk.getWalletMgr().getWallet().getIdentities().get(0);

            String hash = dnaSdk.neovm().record().sendPut(id.dnaid,"passwordtest",new byte[]{},"key","value-test",0,0);
            System.out.println(hash);
            Thread.sleep(6000);
            String res = dnaSdk.neovm().record().sendGet(id.dnaid,"passwordtest",new byte[]{},"key");
            System.out.println("result:"+res);

            //System.out.println(dnaSdk.getConnectMgr().getSmartCodeEvent(hash));

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

        wm.openWalletFile("RecordTxDemo.json");

        wm.neovm().record().setContractAddress("80f6bff7645a84298a1a52aa3745f84dba6615cf");
        return wm;
    }
}
