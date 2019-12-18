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

import java.util.*;


/**
 *
 */
public class ClaimDemo {

    public static void main(String[] args) {

        try {
            DnaSdk dnaSdk = getDnaSdk();
            String privatekey0 = "523c5fcf74823831756f0bcb3634234f10b3beb1c05595058534577752ad2d9f";
            Account acct0 = new Account(Helper.hexToBytes(privatekey0), dnaSdk.defaultSignScheme);
            System.out.println(dnaSdk.getConnect().getBalance(acct0.getAddressU160().toBase58()));
            dnaSdk.getWalletMgr().getWallet().clearIdentity();
            dnaSdk.getWalletMgr().writeWallet();
            List<Identity> dids = dnaSdk.getWalletMgr().getWallet().getIdentities();
            if (dids.size() < 2) {
                Identity identity = dnaSdk.getWalletMgr().createIdentity("passwordtest");
                dnaSdk.nativevm().dnaId().sendRegister(identity,"passwordtest",acct0,20000,500);
                identity = dnaSdk.getWalletMgr().createIdentity("passwordtest");
                dnaSdk.nativevm().dnaId().sendRegister(identity,"passwordtest",acct0,20000,500);
                dids = dnaSdk.getWalletMgr().getWallet().getIdentities();
                dnaSdk.getWalletMgr().writeWallet();
                Thread.sleep(6000);
            }


            Map<String, Object> metaDataMap = new HashMap<>();
            metaDataMap.put("Issuer", dids.get(0).dnaid);
            metaDataMap.put("Subject", dids.get(1).dnaid);

            Map<String, Object> clvMap = new HashMap<String, Object>();
            clvMap.put("typ", "AttestContract");
            clvMap.put("addr", "8055b362904715fd84536e754868f4c8d27ca3f6");

            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.YEAR, 1);
            long expires = c.getTimeInMillis() / 1000L;

            Map<String, Object>  claimInfoMap = new HashMap<String, Object>();
            claimInfoMap.put("name","Bob Dylan");
            claimInfoMap.put("age","22");


            String claim = dnaSdk.nativevm().dnaId().createDnaIdClaim(dids.get(0).dnaid,"passwordtest",dids.get(0).controls.get(0).getSalt(), "claim:context", claimInfoMap, metaDataMap,clvMap,expires);
            System.out.println(claim);
            System.out.println(new String(Base64.getDecoder().decode(claim.split("\\.")[1])));
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
