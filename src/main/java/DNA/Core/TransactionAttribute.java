package DNA.Core;

import java.io.IOException;
import java.util.Arrays;

import DNA.Helper;
import DNA.IO.BinaryReader;
import DNA.IO.BinaryWriter;
import DNA.IO.JsonReader;
import DNA.IO.JsonSerializable;
import DNA.IO.JsonWriter;
import DNA.IO.Serializable;
import DNA.IO.Json.JObject;
import DNA.IO.Json.JString;

/**
 *  交易属性
 */
public class TransactionAttribute implements Serializable,JsonSerializable {
	/**
	 * 用途
	 */
	public TransactionAttributeUsage usage;
	/**
	 * 描述
	 */
	public byte[] data;
	
	/**
	 * byte格式数据反序列化
	 */
	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		// usage
        writer.writeByte(usage.value());
        // data
        if (usage == TransactionAttributeUsage.Script) {
            writer.writeVarInt(data.length);
        } else if (usage == TransactionAttributeUsage.CertUrl 
        		|| usage == TransactionAttributeUsage.DescriptionUrl) {
            writer.writeByte((byte)data.length);
        } else if (usage == TransactionAttributeUsage.Description 
        		|| Byte.toUnsignedInt(usage.value()) >= Byte.toUnsignedInt(TransactionAttributeUsage.Remark.value())) {
        	writer.writeVarBytes(data);
        	return;
        }
        
        if (usage == TransactionAttributeUsage.ECDH02 || usage == TransactionAttributeUsage.ECDH03)
            writer.write(data, 1, 32);
        else
            writer.write(data);
	}

	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		// usage
		usage = TransactionAttributeUsage.valueOf(reader.readByte());
		// data
        if (usage == TransactionAttributeUsage.ContractHash 
        		|| (Byte.toUnsignedInt(usage.value()) >= Byte.toUnsignedInt(TransactionAttributeUsage.Hash1.value()) 
        		&& Byte.toUnsignedInt(usage.value()) <= Byte.toUnsignedInt(TransactionAttributeUsage.Hash15.value()))) {
            data = reader.readBytes(32);
        } else if (usage == TransactionAttributeUsage.ECDH02 || usage == TransactionAttributeUsage.ECDH03) {
            data = new byte[33];
            data[0] = usage.value();
            reader.read(data, 1, 32);
        } else if (usage == TransactionAttributeUsage.Script) {
            data = reader.readVarBytes(65535);
        } else if (usage == TransactionAttributeUsage.CertUrl 
        		|| usage == TransactionAttributeUsage.DescriptionUrl) {
            data = reader.readVarBytes(255);
        } else if (usage == TransactionAttributeUsage.Description 
        		|| Byte.toUnsignedInt(usage.value()) >= Byte.toUnsignedInt(TransactionAttributeUsage.Remark.value())) {
            data = reader.readVarBytes(255);
        }  else {
            throw new IOException();
        }
	}
	
	public JObject json() {
        JObject json = new JObject();
        json.set("usage", new JString(usage.toString()));
        json.set("data", new JString(Helper.toHexString(data)));
        return json;
	}
	
	@Override
	public String toString() {
		return "TransactionAttribute [usage=" + usage + ", data="
				+ Arrays.toString(data) + "]";
	}

	/**
	 * json格式数据反序列化
	 */
	@Override
	public void fromJson(JsonReader reader) {
		JObject json = reader.json();
		usage = TransactionAttributeUsage.valueOf((byte)json.get("Usage").asNumber());
		data = Helper.hexToBytes(json.get("Data").asString());
	}
	
	@Override
	public void toJson(JsonWriter writer) {
		// ...
	}
}
