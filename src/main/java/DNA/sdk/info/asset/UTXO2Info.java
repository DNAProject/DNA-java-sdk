package DNA.sdk.info.asset;

import java.util.List;

public class UTXO2Info {
	public String AssetId;
	public String AssetName;
	public List<UTXOInfo> Utxo;
	
	@Override
	public String toString() {
		return "UTXO2Info [AssetId=" + AssetId + ", AssetName=" + AssetName
				+ ", Utxo=" + Utxo + "]";
	}
}
