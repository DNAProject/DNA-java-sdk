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

package example.api;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.common.Address;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.core.block.Block;
import com.github.DNAProject.core.transaction.Transaction;
import com.github.DNAProject.io.Serializable;
import com.github.DNAProject.smartcontract.neovm.abi.AbiFunction;
import com.github.DNAProject.smartcontract.neovm.abi.AbiInfo;
import com.github.DNAProject.sdk.wallet.Account;
import com.github.DNAProject.sdk.wallet.Identity;
import com.github.DNAProject.sdk.wallet.Wallet;
import com.github.DNAProject.network.websocket.MsgQueue;
import com.github.DNAProject.network.websocket.Result;
import com.alibaba.fastjson.JSON;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */

public class WebsocketDemo {
    public static Object lock = new Object();

    public static void main(String[] args) {
        try {
            DnaSdk dnaSdk = getDnaSdk();
            String password = "passwordtest";
            Account payer = dnaSdk.getWalletMgr().createAccount(password);

            dnaSdk.getWebSocket().startWebsocketThread(false);

            Thread thread = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            waitResult(lock);
                        }
                    });
            thread.start();
            Thread.sleep(5000);

            Wallet oep6 = dnaSdk.getWalletMgr().getWallet();
            System.out.println("oep6:" + JSON.toJSONString(oep6));
            //System.exit(0);

            //System.out.println("================register=================");
//            Identity ident = null;
//            if (dnaSdk.getWalletMgr().getIdentitys().size() == 0) {
//                ident = dnaSdk.neovm().dnaId().sendRegister("passwordtest","payer",0);
//            } else {
//                ident = dnaSdk.getWalletMgr().getIdentitys().get(0);
//            }

//            String dnaid = ident.dnaid;


            for (int i = 0; i >= 0; i++) {

                dnaSdk.getConnect().getNetworkId();
                dnaSdk.getConnect().getGrantOng("AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe");

                //System.out.println(dnaid);




                if (false) {
                    Account info1 = null;
                    Account info2 = null;
                    Account info3 = null;
                    if (dnaSdk.getWalletMgr().getWallet().getAccounts().size() < 3) {
                        info1 = dnaSdk.getWalletMgr().createAccountFromPriKey("passwordtest", "9a31d585431ce0aa0aab1f0a432142e98a92afccb7bcbcaff53f758df82acdb3");
                        info2 = dnaSdk.getWalletMgr().createAccount("passwordtest");
                        info3 = dnaSdk.getWalletMgr().createAccount("passwordtest");
                        dnaSdk.getWalletMgr().writeWallet();
                    }
                    info1 = dnaSdk.getWalletMgr().getWallet().getAccounts().get(0);
                    info2 = dnaSdk.getWalletMgr().getWallet().getAccounts().get(1);
                    Transaction tx = dnaSdk.nativevm().gas().makeTransfer( info1.address, info2.address, 100L,payer.address, dnaSdk.DEFAULT_GAS_LIMIT,0);
                    dnaSdk.signTx(tx, info1.address, password,new byte[]{});
                    System.out.println(tx.toHexString());
                    dnaSdk.getConnect().sendRawTransaction(tx.toHexString());
                }


                //waitResult(dnaSdk, lock);

                if (false) {
                    dnaSdk.getConnect().getBalance("TA63xZXqdPLtDeznWQ6Ns4UsbqprLrrLJk");
                    dnaSdk.getConnect().getBlockJson("c8c165bf0ac6107f7f324b0badb60af4dc4e1157b5eb9d3163c8f332a8612c98");
                    dnaSdk.getConnect().getNodeCount();
                    dnaSdk.getConnect().getContractJson("80e7d2fc22c24c466f44c7688569cc6e6d6c6f92");
                    dnaSdk.getConnect().getSmartCodeEvent("7c3e38afb62db28c7360af7ef3c1baa66aeec27d7d2f60cd22c13ca85b2fd4f3");
                    dnaSdk.getConnect().getBlockHeightByTxHash("7c3e38afb62db28c7360af7ef3c1baa66aeec27d7d2f60cd22c13ca85b2fd4f3");
                    dnaSdk.getConnect().getStorage("ff00000000000000000000000000000000000001", Address.decodeBase58("TA63xZXqdPLtDeznWQ6Ns4UsbqprLrrLJk").toHexString());
                    dnaSdk.getConnect().getTransactionJson("7c3e38afb62db28c7360af7ef3c1baa66aeec27d7d2f60cd22c13ca85b2fd4f3");
                }
                if (false) {

                    InputStream is = new FileInputStream("C:\\ZX\\huguanjun.abi.json");//IdContract
                    byte[] bys = new byte[is.available()];
                    is.read(bys);
                    is.close();
                    String abi = new String(bys);

                    AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                    //System.out.println("Entrypoint:" + abiinfo.getEntrypoint());
                    //System.out.println("Functions:" + abiinfo.getFunctions());

                    AbiFunction func0 = abiinfo.getFunction("Put");
                    Identity did0 = dnaSdk.getWalletMgr().getWallet().getIdentities().get(0);
                    func0.setParamsValue("key".getBytes(), "value".getBytes());
                }
                if(true){
                    Map map = new HashMap();
                    if(i >0) {
                        map.put("SubscribeEvent", true);
                        map.put("SubscribeRawBlock", false);
                    }else{
                        map.put("SubscribeJsonBlock", false);
                        map.put("SubscribeRawBlock", true);
                    }
                    //System.out.println(map);
                    dnaSdk.getWebSocket().setReqId(i);
                    dnaSdk.getWebSocket().sendSubscribe(map);
//                    dnaSdk.getWebSocket().getBlockHeight();
                }
                Thread.sleep(6000);
            }

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void waitResult(Object lock) {
        try {
            synchronized (lock) {
                while (true) {
                    lock.wait();
                    for (String e : MsgQueue.getResultSet()) {
                        System.out.println("RECV: " + e);
                        Result rt = JSON.parseObject(e, Result.class);
                        //TODO
                        MsgQueue.removeResult(e);
                        if (rt.Action.equals("getblockbyheight")) {
                            Block bb = Serializable.from(Helper.hexToBytes((String) rt.Result), Block.class);
                            //System.out.println(bb.json());
                        }
                    }
                }
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
        wm.setWesocket(wsUrl, lock);
        wm.setDefaultConnect(wm.getWebSocket());
        wm.openWalletFile("WsDemo.json");
        return wm;
    }
}
