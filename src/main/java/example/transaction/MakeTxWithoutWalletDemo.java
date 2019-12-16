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

package example.transaction;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.common.Address;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.core.asset.Contract;
import com.github.DNAProject.core.asset.State;
import com.github.DNAProject.core.asset.Transfers;
import com.github.DNAProject.core.payload.InvokeCode;
import com.github.DNAProject.core.transaction.Transaction;

public class MakeTxWithoutWalletDemo {
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";
    public static String privatekey2 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey3 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String privatekey4 = "06bda156eda61222693cc6f8488557550735c329bc7ca91bd2994c894cd3cbc8";
    public static String privatekey5 = "f07d5a2be17bde8632ec08083af8c760b41b5e8e0b5de3703683c3bdcfb91549";
    public static String privatekey6 = "6c2c7eade4c5cb7c9d4d6d85bfda3da62aa358dd5b55de408d6a6947c18b9279";
    public static String privatekey7 = "24ab4d1d345be1f385c75caf2e1d22bdb58ef4b650c0308d9d69d21242ba8618";
    public static String privatekey8 = "87a209d232d6b4f3edfcf5c34434aa56871c2cb204c263f6b891b95bc5837cac";
    public static String privatekey9 = "1383ed1fe570b6673351f1a30a66b21204918ef8f673e864769fa2a653401114";

    public static void main(String[] args) {
        try {
            DnaSdk dnaSdk = getDnaSdk();

            String privatekey0 = "523c5fcf74823831756f0bcb3634234f10b3beb1c05595058534577752ad2d9f";
            com.github.DNAProject.account.Account acct0 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey0), dnaSdk.defaultSignScheme);
            System.out.println(Helper.toHexString(acct0.serializePublicKey()));

            com.github.DNAProject.account.Account acct1 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey1), dnaSdk.defaultSignScheme);
            com.github.DNAProject.account.Account acct2 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey2), dnaSdk.defaultSignScheme);
            com.github.DNAProject.account.Account acct3 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey3), dnaSdk.defaultSignScheme);
            com.github.DNAProject.account.Account acct4 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey4), dnaSdk.defaultSignScheme);
            com.github.DNAProject.account.Account acct5 = new com.github.DNAProject.account.Account(Helper.hexToBytes(privatekey5), dnaSdk.defaultSignScheme);


            if (false) {
                //transer
                Address sender = acct0.getAddressU160();
                Address recvAddr = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());
//                Address recvAddr = Address.decodeBase58("TA5SgQXTeKWyN4GNfWGoXqioEQ4eCDFMqE");
                System.out.println("sender:" + sender.toBase58());
                System.out.println("recvAddr:" + recvAddr.toBase58());
                long amount = 100000;

                Transaction tx = dnaSdk.nativevm().gas().makeTransfer(sender.toBase58(),recvAddr.toBase58(), amount,sender.toBase58(),30000,0);

                System.out.println(tx.json());
                dnaSdk.signTx(tx, new com.github.DNAProject.account.Account[][]{{acct0}});
                dnaSdk.addMultiSign(tx,2,new byte[][]{acct0.serializePublicKey(),acct1.serializePublicKey()},acct0);
                dnaSdk.addMultiSign(tx,2,new byte[][]{acct0.serializePublicKey(),acct1.serializePublicKey()},acct0);
                System.out.println(tx.hash().toHexString());

                Object obj = dnaSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
                System.out.println(obj);
                //dnaSdk.getConnect().sendRawTransaction(tx.toHexString());

            }
            if(false){
                Address sender = acct0.getAddressU160();
                Address recvAddr = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());
                System.out.println("sender:" + sender.toBase58());
                System.out.println("recvAddr:" + recvAddr.toBase58());
                long amount = 100000;
                Transaction tx = dnaSdk.nativevm().gas().makeTransfer(sender.toBase58(),recvAddr.toBase58(), amount,sender.toBase58(),30000,0);
                dnaSdk.signTx(tx, new com.github.DNAProject.account.Account[][]{{acct0}});
                //getSmartCodeEvent per 3s, max 60s
                Object object = dnaSdk.getConnect().waitResult(tx.hash().toString());
                System.out.println(object);
            }

            if (false) {
                //sender address From MultiPubKeys
                Address multiAddr = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());
                System.out.println("sender:" + multiAddr.toBase58());
                Address recvAddr = acct0.getAddressU160();
                System.out.println("recvAddr:" + recvAddr.toBase58());
                int amount = 8;

                Transaction tx = dnaSdk.nativevm().gas().makeTransfer(multiAddr.toBase58(),recvAddr.toBase58(), amount,multiAddr.toBase58(),30000,0);
                System.out.println(tx.json());
                //dnaSdk.signTx(tx, new com.github.DNAProject.account.Account[][]{{acct1, acct2}});
                dnaSdk.addMultiSign(tx,2,new byte[][]{acct1.serializePublicKey(),acct2.serializePublicKey()},acct1);
                dnaSdk.addMultiSign(tx,2,new byte[][]{acct1.serializePublicKey(),acct2.serializePublicKey()},acct2);

                System.out.println(tx.hash().toHexString());
                dnaSdk.getConnect().sendRawTransaction(tx.toHexString());

            }

            if (false) {
                //2 sender transfer to 1 reveiver
                Address sender1 = acct0.getAddressU160();
                Address sender2 = Address.addressFromMultiPubKeys(2, acct1.serializePublicKey(), acct2.serializePublicKey());
                Address recvAddr = acct4.getAddressU160();
                System.out.println("sender1:" + sender1.toBase58());
                System.out.println("sender2:" + sender2.toBase58());
                System.out.println("recvAddr:" + recvAddr.toBase58());

                int amount = 1;
                int amount2 = 2;
                State state1 = new State(sender1,recvAddr,amount);
                State state2 = new State(sender2,recvAddr,amount2);
                Transaction tx = dnaSdk.nativevm().gas().makeTransfer(new State[]{state1,state2},sender1.toBase58(),30000,0);
                System.out.println(tx.json());
                dnaSdk.signTx(tx, new com.github.DNAProject.account.Account[][]{{acct0}});
                dnaSdk.addMultiSign(tx,2,new byte[][]{acct1.serializePublicKey(),acct2.serializePublicKey()},acct1);
                dnaSdk.addMultiSign(tx,2,new byte[][]{acct1.serializePublicKey(),acct2.serializePublicKey()},acct2);
                System.out.println(tx.hash().toHexString());
                dnaSdk.getConnect().sendRawTransaction(tx.toHexString());

            }
            if(false){
                //receiver tx from other, and  add sign
                Address sender = acct0.getAddressU160();
                Address recvAddr = Address.decodeBase58("TA5SgQXTeKWyN4GNfWGoXqioEQ4eCDFMqE");
                System.out.println("sender:" + sender.toBase58());
                System.out.println("recvAddr:" + recvAddr.toBase58());
                long amount = 1000;

                Transaction tx = dnaSdk.nativevm().gas().makeTransfer(sender.toBase58(),recvAddr.toBase58(), amount,sender.toBase58(),30000,0);

                //serialize tx data
                String txHex = tx.toHexString();

                //deserialize
                InvokeCode txRx = (InvokeCode)Transaction.deserializeFrom(Helper.hexToBytes(txHex));
                System.out.println(Transfers.deserializeFrom(Contract.deserializeFrom(txRx.code).args).json());
                dnaSdk.addSign(txRx,acct0);
                dnaSdk.addMultiSign(txRx,2,new byte[][]{acct1.serializePublicKey(),acct2.serializePublicKey()},acct1);
                dnaSdk.addMultiSign(txRx,2,new byte[][]{acct1.serializePublicKey(),acct2.serializePublicKey()},acct2);
                //send tx
                //dnaSdk.getConnect().sendRawTransaction(tx.toHexString());

            }
            if(false){
                String sender = acct0.getAddressU160().toBase58();
                Transaction tx = dnaSdk.nativevm().gas().makeWithdrawGas(sender,sender,10,sender,30000,0);
                dnaSdk.signTx(tx, new com.github.DNAProject.account.Account[][]{{acct0}});
                dnaSdk.getConnect().sendRawTransaction(tx.toHexString());
            }
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
        return wm;
    }
}
