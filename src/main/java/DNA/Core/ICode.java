package DNA.Core;

import DNA.UInt160;
import DNA.Wallets.ContractParameterType;

public interface ICode {
	public byte[] getScript(); 
	public ContractParameterType[] getParameterList();
	public ContractParameterType getReturnType();
	public UInt160 getScriptHash();
}
