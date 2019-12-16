package example.api;

import com.github.DNAProject.DnaSdk;

/**
 * @Description:
 * @date 2019/12/12
 */
public class ApiDemo {

    public static void main(String[] args) {

        try {
            DnaSdk dnaSdk = getDnaSdk();
            if (true) {
                System.out.println(dnaSdk.getConnect().getBalance("AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe"));
                System.out.println(dnaSdk.getConnect().getNodeSyncStatus());
                System.exit(0);
            }
        }catch (Exception ex){
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
        wm.setDefaultConnect(wm.getRpc());
        wm.openWalletFile("wallet2.dat");

        return wm;
    }
}
