package example.gas;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.account.Account;
import com.github.DNAProject.common.Address;
import com.github.DNAProject.common.Helper;
import com.github.DNAProject.core.asset.State;
import com.github.DNAProject.core.transaction.Transaction;

import java.math.BigInteger;
import java.util.*;

class UserAcct{
    String id;
    String address;
    String withdrawAddr;
    byte[] privkey;
    BigInteger gasBalance;
}

class Balance{
    @JSONField(name="gas")
    String gas;


    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }
}


class States{
    @JSONField(name="States")
    Object[] states;

    @JSONField(name="ContractAddress")
    String contractAddress;

    public Object[] getStates() {
        return states;
    }

    public void setStates(Object[] states) {
        this.states = states;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

}

class Event{
    @JSONField(name="GasConsumed")
    int gasConsumed;


    @JSONField(name="TxHash")
    String txHash;

    @JSONField(name="State")
    int state;

    @JSONField(name="Notify")
    States[] notify;

    public int getGasConsumed() {
        return gasConsumed;
    }

    public void setGasConsumed(int gasConsumed) {
        this.gasConsumed = gasConsumed;
    }


    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public States[] getNotify() {
        return notify;
    }

    public void setNotify(States[] notify) {
        this.notify = notify;
    }
}



public class ExchangeDemo {

    //init account should have some onts
    public static final String INIT_ACCT_ADDR = "Ad4pjz2bqep4RhQrUAzMuZJkBC3qJ1tZuT";
    public static final String INIT_ACCT_SALT = "OkX96EG0OaCNUFD3hdc50Q==";

    public static final String FEE_PROVIDER = "AS3SCXw8GKTEeXpdwVw7EcC4rqSebFYpfb";
    public static final String FEE_PROVIDER_SALT = "KvKkxNOGm4q4bLkD8TS2PA==";

    //for test all account's pwd is the same
    public static final String PWD = "123456";

    //for generate a multi-sig address
    public static final String MUTI_SIG_ACCT_SEED1_ADDR = "AK98G45DhmPXg4TFPG1KjftvkEaHbU8SHM";
    public static final String MUTI_SIG_ACCT_SEED1_SALT = "rD4ewxv4qHH8FbUkUv6ePQ==";

    public static final String MUTI_SIG_ACCT_SEED2_ADDR = "ALerVnMj3eNk9xe8BnQJtoWvwGmY3x4KMi";
    public static final String MUTI_SIG_ACCT_SEED2_SALT = "1K8a7joYQ+iwj3/+wGICrw==";

    public static final String MUTI_SIG_ACCT_SEED3_ADDR = "AKmowTi8NcAMjZrg7ZNtSQUtnEgdaC65wG";
    public static final String MUTI_SIG_ACCT_SEED3_SALT = "b9oBYBIPvZMw66q1ky+JDQ==";

    //withdraw address for test user
    public static final String WITHDRAW_ADDRESS = "AZbcPX7HyJTWjqogZhnr2qDTh6NNksGSE6";


    public static  String GAS_NATIVE_ADDRESS = "";

    public static void main(String[] args) {
        try{
            //simulate a database using hashmap
            HashMap<String,UserAcct> database = new HashMap<String,UserAcct>();

            DnaSdk dnaSdk = getDnaSdk();
            GAS_NATIVE_ADDRESS = Helper.reverse(dnaSdk.nativevm().gas().getContractAddress());

            printlog("++++ starting simulate exchange process ...========");
            printlog("++++ 1. create a random account for user ====");
            String id1 = "id1";
            Account acct1 = new Account(dnaSdk.defaultSignScheme);
            String pubkey =  acct1.getAddressU160().toBase58();
            byte[] privkey = acct1.serializePrivateKey();
            printlog("++++ public key is " + acct1.getAddressU160().toBase58());

            UserAcct usr =getNewUserAcct(id1,pubkey,privkey,BigInteger.valueOf(0),BigInteger.valueOf(0));
            usr.withdrawAddr = WITHDRAW_ADDRESS;
            database.put(acct1.getAddressU160().toBase58(),usr);
            //all transfer fee is provide from this account
            Account feeAct = dnaSdk.getWalletMgr().getAccount(FEE_PROVIDER,PWD,Base64.getDecoder().decode(FEE_PROVIDER_SALT));

            //create a multi-sig account as a main account
            Account mutiSeedAct1 = dnaSdk.getWalletMgr().getAccount(MUTI_SIG_ACCT_SEED1_ADDR,PWD,Base64.getDecoder().decode(MUTI_SIG_ACCT_SEED1_SALT));
            Account mutiSeedAct2 = dnaSdk.getWalletMgr().getAccount(MUTI_SIG_ACCT_SEED2_ADDR,PWD,Base64.getDecoder().decode(MUTI_SIG_ACCT_SEED2_SALT));
            Account mutiSeedAct3 = dnaSdk.getWalletMgr().getAccount(MUTI_SIG_ACCT_SEED3_ADDR,PWD,Base64.getDecoder().decode(MUTI_SIG_ACCT_SEED3_SALT));

            Address mainAccountAddr = Address.addressFromMultiPubKeys(3,mutiSeedAct1.serializePublicKey(),mutiSeedAct2.serializePublicKey(),mutiSeedAct3.serializePublicKey());
            printlog("++++ Main Account Address is :" + mainAccountAddr.toBase58());


            //monitor the charge and withdraw thread
            Thread t = new Thread(new Runnable() {

                long lastblocknum = 0 ;

                @Override
                public void run() {
                    while(true){
                        try{
                            //get latest blocknum:
                            //TODO fix lost block
                            int height = dnaSdk.getConnect().getBlockHeight();
                            if (height > lastblocknum){
                                printlog("====== new block sync :" + height);

                                Object  event = dnaSdk.getConnect().getSmartCodeEvent(height);
                                if(event == null){
                                    lastblocknum = height;
                                    Thread.sleep(1000);
                                    continue;
                                }
                                printlog("====== event is " + event.toString());

                                List<Event> events = JSON.parseArray(event.toString(), Event.class);
                                if(events == null){
                                    lastblocknum = height;
                                    Thread.sleep(1000);
                                    continue;
                                }
                                if (events.size()> 0){
                                    for(Event ev:events){
                                        printlog("===== State:" + ev.getState());
                                        printlog("===== TxHash:" + ev.getTxHash());
                                        printlog("===== GasConsumed:" + ev.getGasConsumed());

                                        for(States state:ev.notify){

                                            printlog("===== Notify - ContractAddress:" + state.getContractAddress());
                                            printlog("===== Notify - States[0]:" + state.getStates()[0]);
                                            printlog("===== Notify - States[1]:" + state.getStates()[1]);
                                            printlog("===== Notify - States[2]:" + state.getStates()[2]);
                                            printlog("===== Notify - States[3]:" + state.getStates()[3]);

                                            if (ev.getState() == 1){  //exec succeed
                                                Set<String> keys = database.keySet();
                                                //
                                                if ("transfer".equals(state.getStates()[0]) && keys.contains(state.getStates()[2])){
                                                    BigInteger amount = new BigInteger(state.getStates()[3].toString());
                                                    if (GAS_NATIVE_ADDRESS.equals(state.getContractAddress())){
                                                        printlog("===== charge GAS :"+state.getStates()[2] +" ,amount:"+amount);
                                                        database.get(state.getStates()[2]).gasBalance = amount.add(database.get(state.getStates()[2]).gasBalance);
                                                    }
                                                }

                                                //withdraw case
                                                if("transfer".equals(state.getStates()[0]) && mainAccountAddr.toBase58().equals(state.getStates()[1])){

                                                    for(UserAcct ua: database.values()){
                                                        if (ua.withdrawAddr.equals((state.getStates()[2]))){
                                                            BigInteger amount = new BigInteger(state.getStates()[3].toString());
                                                            if (GAS_NATIVE_ADDRESS.equals(state.getContractAddress())){
                                                                printlog("===== widtdraw "+ amount +" gas to " + ua.withdrawAddr + " confirmed!");
                                                            }

                                                        }
                                                    }

                                                }

                                            }

                                        }

                                    }
                                }

                                lastblocknum = height;


                            }
                            Thread.sleep(1000);

                        }catch(Exception e){
                            printlog("exception 1:"+ e.getMessage());
                        }
                    }
                }
            });

            //monitor the collect
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        while (true){

                            Set<String> keys = database.keySet();

                            List<Account> gasAccts = new ArrayList<Account>() ;
                            List<State> gasStates = new ArrayList<State>();


                            for(String key:keys){
                                Object balance = dnaSdk.getConnect().getBalance(key);
                                printlog("----- balance of " + key + " : " + balance);
                                Balance b = JSON.parseObject(balance.toString(),Balance.class);
                                BigInteger gasbalance = new BigInteger(b.gas);


                                if (gasbalance.compareTo(new BigInteger("0")) > 0){
                                    //transfer gas to main wallet
                                    UserAcct ua = database.get(key);
                                    Account acct = new Account(ua.privkey,dnaSdk.defaultSignScheme);
                                    gasAccts.add(acct);
                                    State st = new State(Address.addressFromPubKey(acct.serializePublicKey()),mainAccountAddr,ua.gasBalance.longValue());
                                    gasStates.add(st);
                                }
                            }

                            //construct gas transfer tx
                            if(gasStates.size() > 0) {
                                printlog("----- Will collect gas to main wallet");
                                Transaction gasTx = dnaSdk.nativevm().gas().makeTransfer(gasStates.toArray(new State[gasStates.size()]), FEE_PROVIDER, 30000, 0);
                                for (Account act : gasAccts) {
                                    dnaSdk.addSign(gasTx, act);
                                }
                                //add fee provider account sig
                                dnaSdk.addSign(gasTx, feeAct);
                                dnaSdk.getConnect().sendRawTransaction(gasTx.toHexString());
                            }

                            Thread.sleep(10000);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        printlog("exception 2:"+e.getMessage());
                    }

                }
            });

            t.start();
            t2.start();

            Thread.sleep(2000);
            printlog("++++ 2. charge some gas to acct1 from init account");
            Account initAccount = dnaSdk.getWalletMgr().getAccount(INIT_ACCT_ADDR,PWD,Base64.getDecoder().decode(INIT_ACCT_SALT));
            State st = new State(initAccount.getAddressU160(),acct1.getAddressU160(),1000L);
            Transaction tx = dnaSdk.nativevm().gas().makeTransfer(new State[]{st}, FEE_PROVIDER, 30000, 0);
            dnaSdk.addSign(tx,initAccount);
            dnaSdk.addSign(tx, feeAct);

            dnaSdk.getConnect().sendRawTransaction(tx.toHexString());
            // test is the tx in txpool
            String txhash = tx.hash().toHexString();
            printlog("++++ txhash :"+txhash);
            Object event = dnaSdk.getConnect().getMemPoolTxState(txhash);
            printlog(event.toString());


            printlog("++++ 3. charge some gas to acct1 from init account");
            st = new State(initAccount.getAddressU160(),acct1.getAddressU160(),1200L);
            tx = dnaSdk.nativevm().gas().makeTransfer(new State[]{st}, FEE_PROVIDER, 30000, 0);
            dnaSdk.addSign(tx,initAccount);
            dnaSdk.addSign(tx, feeAct);
            dnaSdk.getConnect().sendRawTransaction(tx.toHexString());

            Thread.sleep(15000);

            //simulate a withdraw
            //todo must add check the user balance of database
            printlog("++++ withdraw 500 onts to " + usr.withdrawAddr );
            //reduce the withdraw amount first
            BigInteger wdAmount = new BigInteger("500");


            //simulate a withdraw
            printlog("++++ withdraw 500 gas to " + usr.withdrawAddr );
            wdAmount = new BigInteger("500");
            //reduce the withdraw amount first
            if(usr.gasBalance.compareTo(wdAmount) > 0) {
                database.get(usr.address).gasBalance = database.get(usr.address).gasBalance.subtract(wdAmount);
                printlog("++++  " + usr.address + " gas balance : " + database.get(usr.address).gasBalance);

                State wdSt = new State(mainAccountAddr, Address.decodeBase58(usr.withdrawAddr), 500);
                Transaction wdTx = dnaSdk.nativevm().gas().makeTransfer(new State[]{wdSt}, FEE_PROVIDER, 30000, 0);
                dnaSdk.addMultiSign(wdTx, 3, new byte[][]{mutiSeedAct1.serializePublicKey(),mutiSeedAct2.serializePublicKey(),mutiSeedAct3.serializePublicKey()},mutiSeedAct1);
                dnaSdk.addMultiSign(wdTx, 3, new byte[][]{mutiSeedAct1.serializePublicKey(),mutiSeedAct2.serializePublicKey(),mutiSeedAct3.serializePublicKey()},mutiSeedAct2);
                dnaSdk.addMultiSign(wdTx, 3, new byte[][]{mutiSeedAct1.serializePublicKey(),mutiSeedAct2.serializePublicKey(),mutiSeedAct3.serializePublicKey()},mutiSeedAct3);
                dnaSdk.addSign(wdTx, feeAct);
                dnaSdk.getConnect().sendRawTransaction(wdTx.toHexString());
            }



            //claim gas
            Object balance = dnaSdk.getConnect().getBalance(mainAccountAddr.toBase58());
            printlog("++++ before claime gas ,balance of "+ mainAccountAddr.toBase58() +" is " + balance);
            String uGasAmt = dnaSdk.nativevm().gas().unboundGas(mainAccountAddr.toBase58());
            printlog("++++ unclaimed gas is " + uGasAmt);
            if(new BigInteger(uGasAmt).compareTo(new BigInteger("0")) > 0) {
                tx = dnaSdk.nativevm().gas().makeWithdrawGas(mainAccountAddr.toBase58(), mainAccountAddr.toBase58(), new BigInteger(uGasAmt).longValue(), FEE_PROVIDER, 30000, 0);
                dnaSdk.addMultiSign(tx, 3, new byte[][]{mutiSeedAct1.serializePublicKey(),mutiSeedAct2.serializePublicKey(),mutiSeedAct3.serializePublicKey()},mutiSeedAct1);
                dnaSdk.addMultiSign(tx, 3, new byte[][]{mutiSeedAct1.serializePublicKey(),mutiSeedAct2.serializePublicKey(),mutiSeedAct3.serializePublicKey()},mutiSeedAct2);
                dnaSdk.addMultiSign(tx, 3, new byte[][]{mutiSeedAct1.serializePublicKey(),mutiSeedAct2.serializePublicKey(),mutiSeedAct3.serializePublicKey()},mutiSeedAct3);
                dnaSdk.addSign(tx, feeAct);
                dnaSdk.getConnect().sendRawTransaction(tx.toHexString());
                balance = dnaSdk.getConnect().getBalance(mainAccountAddr.toBase58());

                Thread.sleep(10000);
                printlog("++++ after claime gas ,balance of " + mainAccountAddr.toBase58() + " is " + balance);
                //distribute gas to users in database
            }

            t.join();
            t2.join();
        }catch (Exception e){
            e.printStackTrace();
            printlog("exception 3:" + e.getMessage());
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

        String walletfile = "wallet.dat";
        wm.openWalletFile(walletfile);

        return wm;
    }

    public static void printlog(String msg){
        System.out.println(msg);
    }

    public  static UserAcct getNewUserAcct(String id ,String pubkey,byte[] privkey,BigInteger ont,BigInteger gas){
        UserAcct acct = new UserAcct();
        acct.id = id;
        acct.privkey = privkey;
        acct.address = pubkey;
        acct.gasBalance = gas;

        return acct;
    }
}
