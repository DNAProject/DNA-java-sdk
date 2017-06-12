package DNA.Network.Rest;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;


public class RestClient {
	
	public RestClient(String url) {
		Consts.setRestUrl(url);
	}
	
	public String sendTransaction(String authType, String accessToken, String action, String version, String type, String data) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		Map<String, String> body = new HashMap<String, String>();
		body.put("Action", action);
		body.put("Version", version);
		body.put("Type", type);
		body.put("Data", data);
		try {
			return RestHttp.post(Consts.Url_send_transaction, params, body);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getTransaction(String authType, String accessToken, String txid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(Consts.Url_get_transaction + txid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getAsset(String authType, String accessToken, String assetid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(Consts.Url_get_asset + assetid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getBlockHeight(String authType, String accessToken) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(Consts.Url_get_block_height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getBlock(String authType, String accessToken, int height) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(Consts.Url_get_block_By_Height + height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getBlock(String authType, String accessToken, String hash) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(Consts.Url_get_block_By_Hash + hash, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
}

class Consts {
	/**
	 * 服务提供方地址Url
	 */
	private static String[] urls = new String[] {""
		,"http://localhost:8080"
		
	};
	public static  String url = urls[1];
	
	public static void setRestUrl(String url) {
		Url_send_transaction = url + Url_send_transaction;
		Url_get_transaction = url + Url_get_transaction;
		Url_get_asset = url + Url_get_asset;
		Url_get_block_height = url + Url_get_block_height;
		Url_get_block_By_Height = url + Url_get_block_By_Height;
		Url_get_block_By_Hash = url + Url_get_block_By_Hash;
	}

	public static String Url_send_transaction = "/api/v1/transaction";
	public static String Url_get_transaction = "/api/v1/transaction/";
	public static String Url_get_asset = "/api/v1/asset/";
	public static String Url_get_block_height = "/api/v1/block/height";
	public static String Url_get_block_By_Height = "/api/v1/block/details/height/";
	public static String Url_get_block_By_Hash = "/api/v1/block/details/hash/";
}
