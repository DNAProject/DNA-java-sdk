package DNA.sdk.helper;

import java.util.Arrays;
import java.util.Date;

import org.bouncycastle.math.ec.ECPoint;

import DNA.UInt256;
import DNA.Core.Block;
import DNA.Core.Blockchain;
import DNA.Core.RecordTransaction;
import DNA.Core.RegisterTransaction;
import DNA.Core.Transaction;
import DNA.Core.TransactionAttribute;
import DNA.Core.TransactionInput;
import DNA.Core.TransactionOutput;
import DNA.Core.Scripts.Script;
import DNA.IO.Caching.TrackableCollection;
import DNA.Implementations.Wallets.SQLite.UserWallet;
import DNA.Wallets.Account;
import DNA.Wallets.Coin;
import DNA.Wallets.Contract;
import DNA.Wallets.Wallet;

/**
 * SDK帮助类
 * 
 * @author 12146
 *
 */
public class OnChainSDKHelper {
	
	public static String byte2str(byte[] bt) {
		if(bt == null || bt.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<bt.length; ++i) {
			sb.append(",").append(Byte.toUnsignedInt(bt[i]));
		}
		return "len="+bt.length+",dat="+sb.substring(1).toString();
	}
	
	public static byte[] hexToBytes(String value) {
        if (value == null || value.length() == 0)
            return new byte[0];
        if (value.length() % 2 == 1)
            throw new IllegalArgumentException();
        byte[] result = new byte[value.length() / 2];
        for (int i = 0; i < result.length; i++)
            result[i] = (byte) Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16);
        return result;
    }
	public static String toHexString(byte[] value) {
        StringBuilder sb = new StringBuilder();
        for (byte b : value) {
            int v = Byte.toUnsignedInt(b);
            sb.append(Integer.toHexString(v >>> 4));
            sb.append(Integer.toHexString(v & 0x0f));
        }
        return sb.toString();
    }
	public static String toHexString2(byte[] bs) {
		StringBuilder sb2 = new StringBuilder();
		for(byte b: bs) {
			int v = Byte.toUnsignedInt(b);
			sb2.append(Integer.toHexString(v));
		}
		System.out.println(sb2);
		
		return sb2.toString();
	}
	
	public static void printBlockByHeight(int height) throws Exception {
		print(getBlock(height));
	}
	public static void printBlockByHash(String blockhash) throws Exception {
		print(getBlock(blockhash));
	}
	public static void printBlockByHash(UInt256 blockhash) throws Exception {
		print(getBlock(blockhash));
	}
	public static void printBlock(Block blk) {
		print(blk);
	}
	public static void printBlock2(Block blk) {
		print2(blk);
	}
	private static Block getBlock(int height) throws Exception {
		return Blockchain.current().getBlock(height);
	}
	private static Block getBlock(String blockhash) throws Exception {
		return getBlock(UInt256.parse(blockhash));
	}
	private static Block getBlock(UInt256 hash) throws Exception {
		return Blockchain.current().getBlock(hash);
	}
	
	
	public static void printTransactionByTxid(String txid) throws Exception {
		print(getTransaction(txid));
	}
	public static void printTransactionByTxid(UInt256 txid) throws Exception {
		print(getTransaction(txid));
	}
	public static void printTransaction(Transaction tx) {
		print(tx);
	}
	private static Transaction getTransaction(String txid) throws Exception {
		return getTransaction(UInt256.parse(txid));
	}
	private static Transaction getTransaction(UInt256 txid) throws Exception {
		return Blockchain.current().getTransaction(txid);
	}
	
	
	private static void print(Transaction tx) {
		System.out.println("\ttype:"+tx.type);
		System.out.println("\tattrs:"+tx.attributes.length);
		for(TransactionAttribute txAttr: tx.attributes) {
			System.out.println("\t\tattr.usage:"+txAttr.usage);
			System.out.println("\t\tattr.toStr:"+txAttr.toString()+"\n\t\t    newStr:"+new String(txAttr.data));
		}
		System.out.println("\tinputs:"+tx.inputs.length);
		for(TransactionInput in: tx.inputs) {
			System.out.println("\t\tinput.prevHash:"+byte2str(in.prevHash.toArray()));
			System.out.println("\t\tinput.prevHash:"+in.prevHash.toString());
			System.out.println("\t\tinput.prevIndx:"+in.prevIndex);
		}
		System.out.println("\toutputs:"+tx.outputs.length);
		for(TransactionOutput out: tx.outputs) {
			System.out.println("\t\tout.assetId:"+out.assetId);
			System.out.println("\t\tout.scriptHash:"+out.scriptHash);
			System.out.println("\t\tout.value:"+out.value);
		}
		System.out.println("\tscripts:"+tx.scripts.length);
		for(Script sc: tx.scripts) {
			System.out.println("\t\tsc:"+sc.toString()+"\n\t\tsc.redeem:"+toHexString(sc.redeemScript) + "\n\t\tsc.stack :"+toHexString(sc.stackScript));
			System.out.println("\t\tsc:"+sc.toString()+"\n\t\tsc.redeem.byte:"+byte2str(sc.redeemScript) + "\n\t\tsc.stack.byte :"+byte2str(sc.stackScript));
		}
		System.out.println("\ttx.hash():"+tx.hash());
		System.out.println("\ttx.sysF():"+tx.systemFee());
		if(tx instanceof RegisterTransaction) {
			RegisterTransaction reg = (RegisterTransaction) tx;
			System.out.println("\ttx.amount:"+reg.amount);
			System.out.println("\ttx.type:"+reg.assetType);
			System.out.println("\ttx.name:"+reg.name);
			System.out.println("\ttx.nonce:"+reg.nonce);
			System.out.println("\ttx.precision:"+reg.precision);
			System.out.println("\ttx.pubkey(true):"+byte2str(reg.issuer.getEncoded(true)));
			System.out.println("\ttx.pubkey(false):"+byte2str(reg.issuer.getEncoded(false)));
			System.out.println("\ttx.admin:"+reg.admin);
			System.out.println("\ttx.unsign:"+byte2str(reg.getHashData()));
			System.out.println("\ttx.unsign:"+byte2str(reg.toArray()));
		}
		if(tx instanceof RecordTransaction) {
			RecordTransaction rr = (RecordTransaction) tx;
			System.out.println("rr.type:"+rr.recordType.toString());
			System.out.println("rr.data:"+new String(rr.recordData));
		}
		System.out.println();
	
	}
	private static void print2(Block blk) {
		String dat = String.format("Height:%6s, txs.len:%6s, blockTime:%s", new Object[] {
				blk.height, blk.transactions.length, new Date(blk.timestamp * 1000L)
		});
		System.out.println(dat);
	}

	public static String toString(Block bb, int cc) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n-----------------------------------------------------------------------hh:"+cc);
		sb.append("\n").append("version:"+bb.version);
		sb.append("\n").append("prevBlock:"+bb.prevBlock.toString());
		sb.append("\n").append("merkleRoot:"+bb.merkleRoot.toString());
		sb.append("\n").append("timestamp:"+new Date(bb.timestamp * 1000L));
		sb.append("\n").append("height:"+bb.height);
		sb.append("\n").append("nonce:"+bb.nonce);
		sb.append("\n").append("nextMiner:"+bb.nextMiner.toString());
		sb.append("\n").append("script:\n\t"+toHexString(bb.script.redeemScript) + "\n\t"+toHexString(bb.script.stackScript));
		sb.append("\n").append("transactions:..."+bb.transactions.length);
		if(bb.transactions.length > 0) {
			Arrays.stream(bb.transactions).forEach(p -> System.out.println(p));
			Arrays.stream(bb.transactions).forEach(p -> {
				sb.append("\n\t").append("type:"+ p.type.toString());
				sb.append("\n\t").append("version:"+p.version);
				sb.append("\n\t").append("nonce:"+p.nonce);
				sb.append("\n\t").append("attrs:"+p.attributes.length);
				if(p.attributes.length > 0) {
					Arrays.stream(p.attributes).forEach(i -> {
						sb.append("\n\t\t").append("usage:"+i.usage.toString());
						sb.append("\n\t\t").append("data:"+toHexString(i.data));
					});
				}
				sb.append("\n\t").append("inputs:"+p.inputs.length);
				if(p.inputs.length > 0) {
					Arrays.stream(p.inputs).forEach(j -> {
						sb.append("\n\t\t").append("prevHash:"+j.prevHash.toString());
						sb.append("\n\t\t").append("prevIndx:"+j.prevIndex);
					});
				}
				sb.append("\n\t").append("outputs:"+p.outputs.length);
				if(p.outputs.length > 0) {
					Arrays.stream(p.outputs).forEach(k -> {
						sb.append("\n\t\t").append("assetId:"+k.assetId.toString());
						sb.append("\n\t\t").append("amount:"+k.value.toLong());
						sb.append("\n\t\t").append("scriptHash:"+k.scriptHash.toString());
					});
				}
				sb.append("\n\t").append("scripts:"+p.scripts.length);
				if(p.inputs.length > 0) {
					Arrays.stream(p.scripts).forEach(i -> {
						sb.append("\n\t\t").append("redeemScript:"+toHexString(i.redeemScript));
						sb.append("\n\t\t").append("redeemScript:"+toHexString(i.redeemScript));
					});
				}
			});
		}
		return sb.toString();
	}
	private static void print(Block blk) {
		System.out.println("Height:"+blk.height);
		System.out.println("nonce :"+blk.nonce);
		System.out.println("versio:"+blk.version);
		System.out.println("hash  :"+blk.hash());
		System.out.println("nextM :"+blk.nextMiner);
		System.out.println("prevB :"+blk.prevBlock);
		System.out.println("prevB :"+blk.timestamp);
		System.out.println("prevB :"+System.currentTimeMillis());
		System.out.println("bkTm  :"+new Date(blk.timestamp * 1000L));
		System.out.println("sc.toS:\n\t"+toHexString(blk.script.redeemScript) + "\n\t"+toHexString(blk.script.stackScript));
		System.out.println("bk.mer:"+blk.merkleRoot);
		System.out.println("blk.tx.len:"+blk.transactions.length);
		for(Transaction tx: blk.transactions) {
			print(tx);
		}
	}
	
	public static void printWallet(UserWallet userWallet) {
		print(userWallet);
	}
	private static void print(UserWallet userWallet) {
		System.out.println("\nts.printWallet.......................................................................[st]");
		System.out.println("path:"+userWallet.getWalletPath());
		int c = 0;
		
		// 1
		System.out.println("accs.......................start");c = 0;
		for(Account acc: userWallet.getAccounts()) {
			System.out.println("acc......................."+(++c));
			print(acc);
		}
		System.out.println();
		// 2
		System.out.println("contracts.......................start");c = 0;
		for(Contract con: userWallet.getContracts()) {
			System.out.println("contract......................."+(++c));
			print(con);
		}
		System.out.println();
		// 3
		System.out.println("trans....................start");c = 0;
		for(Transaction tx: userWallet.LoadTransactions().keySet()) {
			System.out.println("tran...................."+(++c));
			print(tx);
		}
		System.out.println();
		// 4
		long amount = 0L;
		System.out.println("findCoins........................start"); c=0;
		for(Coin coin: userWallet.findCoins()) {
			System.out.println("coin............."+(++c));
			print(coin);
			amount += coin.value.toLong();
		}
		System.out.println("SpentAmount:"+amount);
		System.out.println();
		// 5
		amount  = 0L;
		System.out.println("findUnspentCoins.................start");c=0;
		for(Coin coin: userWallet.findUnspentCoins()) {
			System.out.println("unspent........."+(++c));
			print(coin);
			amount += coin.value.toLong();
			System.out.println(coin.value + "----" + amount);
		}
		System.out.println("UnspentAmount:"+amount);
		System.out.println();
		
		System.out.println("\nts.printWallet.......................................................................[ed]");
	
	}

	public static String getbyteStr(byte[] bs)  {
    	StringBuilder sb = new StringBuilder();
    	for(byte b: bs) {
    		sb.append(" ").append(Byte.toUnsignedInt(b));
    		
    	}
    	return sb.substring(1);
    }
	
	public static void print(Account acc) {
		// addr
		System.out.println("acc..................st");
		System.out.println("acc.addr:"+Wallet.toAddress(Script.toScriptHash(Contract.createSignatureRedeemScript(acc.publicKey))));
		System.out.println("acc.uint:"+Script.toScriptHash(Contract.createSignatureRedeemScript(acc.publicKey)).toString());
		System.out.println("acc.uint(byte):"+getbyteStr(Script.toScriptHash(Contract.createSignatureRedeemScript(acc.publicKey)).toArray()));
		System.out.println("acc.uint(hex):"+toHexString(Script.toScriptHash(Contract.createSignatureRedeemScript(acc.publicKey)).toArray()));
		// pubKey
		ECPoint pubKey = acc.publicKey;
		String pubKeyStr = toHexString(acc.publicKey.getEncoded(true));
		System.out.println(String.format("acc.PubKey:\n\tECPoint: %s\n\tPubKStr:%s", pubKey, pubKeyStr));
		
		// priKey
		String priKey = toHexString(acc.privateKey);
		System.out.println(String.format("acc.PriKey:\n\tHEX:%s", priKey));
		
		String wif = acc.export();
		System.out.println(String.format("\tWIF:%s", wif));
		
		// pubKeyHash
		System.out.println("acc.PubKeyHash:"+acc.publicKeyHash.toString());
		System.out.println("acc..................ed");
	}

	public static void print(Contract con) {
		System.out.println("contract.address:"+con.address());
		System.out.println("contract.publicKey:"+con.publicKeyHash.toString());
	}

	public static void print(Coin coin) {
		System.out.println("coin.addr:"+coin.address());
		System.out.println("coin.toSt:"+coin.toString());
		System.out.println("coin.assetId:"+coin.assetId.toString());
		System.out.println("coin.scriptHash:"+coin.scriptHash);
		System.out.println("coin.value:"+coin.value.toString());
		System.out.println("coin.state:"+coin.getState());
		System.out.println("coin.TrackState:"+coin.getTrackState());
		System.out.println("coin.input.prevHash:"+coin.input.prevHash);
	}
	
   public static void print(TrackableCollection<TransactionInput, Coin> coins, String key) {
		System.out.println("Wallet.print.coins.......................[start]............."+key);
		coins.forEach(p -> {
			System.out.println("1:"+p.assetId);
			System.out.println("2:"+p.input.prevHash.toString());
			System.out.println("3:"+p.scriptHash.toString());
			System.out.println("4:"+p.value);
			System.out.println("5:"+p.address());
			System.out.println("6:"+p.getState());
			System.out.println("7:"+p.getTrackState());
			System.out.println("8:"+p.key().prevHash);
		});
		System.out.println("Wallet.print.coins.......................[end]................"+key);
	}
   
   
   
   public static void print(String str) {
	   System.out.println(str);
   }
}
