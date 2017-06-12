package DNA.Core;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bouncycastle.math.ec.ECPoint;

import DNA.Helper;
import DNA.UInt160;
import DNA.Core.Scripts.Script;
import DNA.Core.Scripts.ScriptBuilder;
import DNA.Cryptography.ECC;
import DNA.IO.Json.JArray;
import DNA.IO.Json.JBoolean;
import DNA.IO.Json.JObject;
import DNA.IO.Json.JString;
import DNA.Wallets.Contract;
import DNA.Wallets.ContractParameterType;

/**
 *  签名上下文
 *  
 */
public class SignatureContext {
    /**
     *  要签名的数据
     */
    public final Signable signable;
    /**
     *  要验证的脚本散列值
     */
    public final UInt160[] scriptHashes;
    /**
     * 合约脚本代码
     */
    private final byte[][] redeemScripts;
    /**
     * 公钥-签名数据
     */
    private final Map<ECPoint, byte[]>[] signatures;
    
    private final boolean[] completed;

    /**
     *  判断签名是否完成
     */
    public boolean isCompleted() {
        for (boolean b : completed) {
        	if (!b) {
        		return false;
        	}
        }
        return true;
    }

    /**
     *  对指定的数据构造签名上下文
     *  <param name="signable">要签名的数据</param>
     */
    @SuppressWarnings("unchecked")
	public SignatureContext(Signable signable) {
        this.signable = signable;
        this.scriptHashes = signable.getScriptHashesForVerifying();
        this.redeemScripts = new byte[scriptHashes.length][];
        this.signatures = (Map<ECPoint, byte[]>[]) Array.newInstance(Map.class, scriptHashes.length);
        this.completed = new boolean[scriptHashes.length];
    }

    /**
     *  添加一个签名
     *  <param name="contract">该签名所对应的合约</param>
     *  <param name="pubkey">该签名所对应的公钥</param>
     *  <param name="signature">签名</param>
     *  <returns>返回签名是否已成功添加</returns>
     */
    public boolean add(Contract contract, ECPoint pubkey, byte[] signature) {
        for (int i = 0; i < scriptHashes.length; i++) {
            if (scriptHashes[i].equals(contract.scriptHash())) {
                if (redeemScripts[i] == null) {
                    redeemScripts[i] = contract.redeemScript;
                }
                if (signatures[i] == null) {
                	signatures[i] = new HashMap<ECPoint, byte[]>();
                }
                signatures[i].put(pubkey, signature);
                completed[i] |= 
                        contract.parameterList.length == signatures[i].size()
                        && Arrays.stream(contract.parameterList).allMatch(
                                p -> p == ContractParameterType.Signature);
                return true;
            }
        }
        return false;
    }

    /**
     *  从签名上下文中获得完整签名的合约脚本
     *  <returns>返回合约脚本</returns>
     */
    public Script[] getScripts() {
        if (!isCompleted()) throw new IllegalStateException();
        Script[] scripts = new Script[signatures.length];
        for (int i = 0; i < scripts.length; i++) {
            try (ScriptBuilder sb = new ScriptBuilder()) {
	            for (byte[] signature : signatures[i].entrySet().stream()
	            		.sorted((a, b) -> ECC.compare(a.getKey(), b.getKey()))
	            		.map(p -> p.getValue()).toArray(byte[][]::new)) {
	                sb.push(signature);
	            }
	            scripts[i] = new Script();
	            scripts[i].stackScript = sb.toArray();
	            scripts[i].redeemScript = redeemScripts[i];
            }
        }
        return scripts;
    }

    /**
     *  把签名上下文转为json对象
     *  <returns>返回json对象</returns>
     */
    public JObject json() {
        JObject json = new JObject();
        json.set("type", new JString(signable.getClass().getTypeName()));
        json.set("hex", new JString(Helper.toHexString(signable.getHashData())));
        JArray scripts = new JArray();
        for (int i = 0; i < signatures.length; i++) {
            if (signatures[i] == null) {
                scripts.add(null);
            } else {
                scripts.add(new JObject());
                scripts.get(i).set("redeem_script", new JString(Helper.toHexString(redeemScripts[i])));
                JArray sigs = new JArray();
                for (Entry<ECPoint, byte[]> pair : signatures[i].entrySet()) {
                    JObject signature = new JObject();
                    signature.set("pubkey", new JString(Helper.toHexString(pair.getKey().getEncoded(true))));
                    signature.set("signature", new JString(Helper.toHexString(pair.getValue())));
                    sigs.add(signature);
                }
                scripts.get(i).set("signatures", sigs);
                scripts.get(i).set("completed", new JBoolean(completed[i]));
            }
        }
        json.set("scripts", scripts);
        return json;
    }

    @Override 
    public String toString() {
        return json().toString();
    }
}
