package DNA.Core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import DNA.UInt160;
import DNA.Core.Scripts.Script;
import DNA.IO.BinaryReader;
import DNA.IO.BinaryWriter;
import DNA.IO.Serializable;
import DNA.Wallets.ContractParameterType;

public class FunctionCode implements ICode, Serializable{
	private byte[] script;
	private ContractParameterType[] parameterList;
	private ContractParameterType returnType;
	private UInt160 scriptHash;
	
	public void setScript(byte[] script) {
		this.script = script;
	}
	
	public void setParameterList(ContractParameterType[] parameterList) {
		this.parameterList = parameterList;
	}
	
	public void setReturnType(ContractParameterType returnType) {
		this.returnType = returnType;
	}
	
	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		script = reader.readVarBytes();
		parameterList = toEnum(reader.readVarBytes());
		returnType = toEnum(reader.readByte());
	}

	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		writer.writeVarBytes(script);
		writer.writeVarBytes(toByte(parameterList));
		writer.writeByte(toByte(returnType));
	}
	
	private ContractParameterType toEnum(byte bt) {
		return Arrays.stream(ContractParameterType.values()).filter(p -> p.ordinal() == bt).findAny().get();
	}
	private ContractParameterType[] toEnum(byte[] bt) {
		if(bt == null) {
			return null;
		}
		List<ContractParameterType> list = new ArrayList<ContractParameterType>();
		for(byte b: bt) {
			ContractParameterType type = toEnum(b);
			list.add(type);
		}
		return list.stream().toArray(ContractParameterType[]::new);
	}
	private byte toByte(ContractParameterType type) {
		return (byte) type.ordinal();
	}
	private byte[] toByte(ContractParameterType[] types) {
		if(types == null) {
			return new byte[0];
		}
		int len = types.length;
		byte[] bt = new byte[len];
		for(int i=0; i<len; ++i) {
			bt[i] = (byte) types[i].ordinal();
		}
		return bt;
	}
	

	@Override
	public byte[] getScript() {
		return script;
	}

	@Override
	public ContractParameterType[] getParameterList() {
		return null;
	}

	@Override
	public ContractParameterType getReturnType() {
		return null;
	}

	@Override
	public UInt160 getScriptHash() {
		if(scriptHash == null) {
			scriptHash = Script.toScriptHash(getScript());
		}
		return scriptHash;
	}
}
