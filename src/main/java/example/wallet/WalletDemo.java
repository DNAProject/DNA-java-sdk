package example.wallet;

import com.github.DNAProject.DnaSdk;

public class WalletDemo {
    public static void main(String[] args) {
        try {
            DnaSdk dnaSdk = getDnaSdk();
            if (dnaSdk.getWalletMgr().getWallet().getAccounts().size() > 0) {
                dnaSdk.getWalletMgr().getWallet().clearAccount();
                dnaSdk.getWalletMgr().getWallet().clearIdentity();
                dnaSdk.getWalletMgr().writeWallet();
            }
            dnaSdk.getWalletMgr().createAccounts(1, "passwordtest");
            dnaSdk.getWalletMgr().writeWallet();

            System.out.println("init size: "+dnaSdk.getWalletMgr().getWallet().getAccounts().size()+" " +dnaSdk.getWalletMgr().getWalletFile().getAccounts().size());
            System.out.println(dnaSdk.getWalletMgr().getWallet().toString());
            System.out.println(dnaSdk.getWalletMgr().getWalletFile().toString());

            System.out.println();
            dnaSdk.getWalletMgr().getWallet().removeAccount(dnaSdk.getWalletMgr().getWallet().getAccounts().get(0).address);
            dnaSdk.getWalletMgr().getWallet().setVersion("2.0");
            System.out.println("removeAccount size: "+dnaSdk.getWalletMgr().getWallet().getAccounts().size()+" " +dnaSdk.getWalletMgr().getWalletFile().getAccounts().size());
            System.out.println(dnaSdk.getWalletMgr().getWallet().toString());
            System.out.println(dnaSdk.getWalletMgr().getWalletFile().toString());

            System.out.println();
            dnaSdk.getWalletMgr().resetWallet();
            System.out.println("resetWallet size: "+dnaSdk.getWalletMgr().getWallet().getAccounts().size()+" " +dnaSdk.getWalletMgr().getWalletFile().getAccounts().size());
            System.out.println(dnaSdk.getWalletMgr().getWallet().toString());
            System.out.println(dnaSdk.getWalletMgr().getWalletFile().toString());


            System.out.println();
            dnaSdk.getWalletMgr().getWallet().removeAccount(dnaSdk.getWalletMgr().getWallet().getAccounts().get(0).address);
            dnaSdk.getWalletMgr().getWallet().setVersion("2.0");
            System.out.println("removeAccount size: "+dnaSdk.getWalletMgr().getWallet().getAccounts().size()+" " +dnaSdk.getWalletMgr().getWalletFile().getAccounts().size());
            System.out.println(dnaSdk.getWalletMgr().getWallet().toString());
            System.out.println(dnaSdk.getWalletMgr().getWalletFile().toString());

            //write wallet
            dnaSdk.getWalletMgr().writeWallet();
            System.out.println();
            System.out.println("writeWallet size: "+dnaSdk.getWalletMgr().getWallet().getAccounts().size()+" " +dnaSdk.getWalletMgr().getWalletFile().getAccounts().size());
            System.out.println(dnaSdk.getWalletMgr().getWallet().toString());
            System.out.println(dnaSdk.getWalletMgr().getWalletFile().toString());
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

        wm.openWalletFile("WalletDemo.json");

        return wm;
    }
}
