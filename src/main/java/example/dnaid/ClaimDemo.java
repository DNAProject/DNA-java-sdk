/*
 * Copyright (C) 2018 The DNA Authors
 * This file is part of The DNA library.
 *
 *  The DNA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The DNA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The DNA.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package example.dnaid;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.account.Account;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.sdk.wallet.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class ClaimDemo {

    public static void main(String[] args) {

        try {
            DnaSdk dnaSdk = getDnaSdk();
            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            Account acct0 = new Account(Helper.hexToBytes(privatekey0), dnaSdk.defaultSignScheme);
            List<Identity> dids = dnaSdk.getWalletMgr().getWallet().getIdentities();
            if (dids.size() < 2) {
                Identity identity = dnaSdk.getWalletMgr().createIdentity("passwordtest");
                dnaSdk.nativevm().dnaId().sendRegister(identity,"passwordtest",acct0,0,0);
                identity = dnaSdk.getWalletMgr().createIdentity("passwordtest");
                dnaSdk.nativevm().dnaId().sendRegister(identity,"passwordtest",acct0,0,0);
                dids = dnaSdk.getWalletMgr().getWallet().getIdentities();
                Thread.sleep(6000);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Issuer", dids.get(0).dnaid);
            map.put("Subject", dids.get(1).dnaid);


            String claim = dnaSdk.nativevm().dnaId().createDnaIdClaim(dids.get(0).dnaid,"passwordtest",new byte[]{}, "claim:context", map, map,map,0);
            System.out.println(claim);
            boolean b = dnaSdk.nativevm().dnaId().verifyDnaIdClaim(claim);
            System.out.println(b);

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

        wm.openWalletFile("ClaimDemo.json");

        return wm;
    }
}
