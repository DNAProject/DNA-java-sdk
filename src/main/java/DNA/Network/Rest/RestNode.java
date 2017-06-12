package DNA.Network.Rest;

import java.io.IOException;

import DNA.Core.Block;
import DNA.Core.Transaction;
import DNA.IO.JsonReader;
import DNA.IO.JsonSerializable;
import DNA.IO.Json.JObject;

import com.alibaba.fastjson.JSON;

public class RestNode {
	private RestClient restClient;
	private String accessToken="token001", authType="OAuth2.0";
	
	public RestNode(String restUrl) {
		restClient = new RestClient(restUrl);
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	
	public boolean sendRawTransaction(String action, String version, String type, String data) throws RestException {
		String rs = restClient.sendTransaction(authType, accessToken, action, version, type, data);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return true;
		}
		throw new RestRuntimeException(rr.toString());
	}
	
	public Transaction getRawTransaction(String txid) throws RestException {
		String rs = restClient.getTransaction(authType, accessToken, txid);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			try {
				return Transaction.fromJsonD(new JsonReader(JObject.parse(rr.Result)));
			} catch (IOException e) {
				throw new RestRuntimeException("Transaction.fromJsonD(txid) failed", e);
			}
		}
		throw new RestRuntimeException(rr.toString());
	}
	
	public Transaction getAsset(String assetid) throws RestException {
		String rs = restClient.getAsset(authType, accessToken, assetid);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		try {
			return Transaction.fromJsonD(new JsonReader(JObject.parse(rr.Result)));
		} catch (IOException e) {
			throw new RestRuntimeException("Transaction.fromJsonD(assetid) failed", e);
		}
		
	}
	
	public int getBlockHeight() throws RestException {
		String rs = restClient.getBlockHeight(authType, accessToken);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		return Integer.valueOf(rr.Result).intValue();
		
	}
	public Block getBlock(int height) throws RestException {
		String rs = restClient.getBlock(authType, accessToken, height);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		try {
			return JsonSerializable.from(JObject.parse(rr.Result), Block.class);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RestRuntimeException("Block.deserialize(height) failed", e);
		}
	}
		
	public Block getBlock(String hash) throws RestException {
		String rs = restClient.getBlock(authType, accessToken, hash);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		try {
			return JsonSerializable.from(JObject.parse(rr.Result), Block.class);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RestRuntimeException("Block.deserialize(hash) failed", e);
		}
		
	}
}
class Result {
	public String Action;
	public long Error;
	public String Desc;
	public String Result;
	public String Version;
	
	@Override
	public String toString() {
		return "Result [Action=" + Action + ", Error=" + Error + ", Desc="
				+ Desc + ", Result=" + Result + ", Version=" + Version + "]";
	}	
}


