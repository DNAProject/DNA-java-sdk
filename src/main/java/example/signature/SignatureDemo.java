package example.signature;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.core.DataSignature;

public class SignatureDemo {
    public static void main(String[] args) {
        try {
            DnaSdk dnaSdk = getDnaSdk();
            if(true) {
                com.github.DNAProject.account.Account acct = new com.github.DNAProject.account.Account(dnaSdk.defaultSignScheme);
                byte[] data = "12345".getBytes();
                byte[] signature = dnaSdk.signatureData(acct, data);

                System.out.println(dnaSdk.verifySignature(acct.serializePublicKey(), data, signature));
            }
            if(true) {
                com.github.DNAProject.account.Account acct = new com.github.DNAProject.account.Account(dnaSdk.defaultSignScheme);
                byte[] data = "12345".getBytes();
                DataSignature sign = new DataSignature(dnaSdk.defaultSignScheme, acct, data);
                byte[] signature = sign.signature();


                com.github.DNAProject.account.Account acct2 = new com.github.DNAProject.account.Account(false,acct.serializePublicKey());
                DataSignature sign2 = new DataSignature();
                System.out.println(sign2.verifySignature(acct2, data, signature));
            }
            System.exit(0);
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

        wm.openWalletFile("AccountDemo.json");

        return wm;
    }
}
