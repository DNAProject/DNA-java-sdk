package DNA.Implementations.Wallets.Mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 账户信息操作类
 * 
[对应数据表结构]
 create table tbl_onchainweb_restful_account (
 	PrivateKeyEncrypted BINARY(112) Not null,
 	PublicKeyHash BINARY(20) Not null,
 	policy varchar(34) Not null
 );
 create table tbl_onchainweb_restful_contract (
 	ScriptHash BINARY(20) Not null,
 	PublicKeyHash BINARY(20) Not null,
 	RawData BINARY(58) Not null,
 	policy varchar(34) Not null
 );
 create table tbl_onchainweb_restful_key (
 	PasswordHash BINARY(32) Not null,
 	IV BINARY(16) Not null,
 	MasterKey BINARY(48) Not null,
 	Version VARBINARY(8) Not null,
 	Height VARBINARY(20) Not null,
 	policy varchar(34) Not null,
 	primary key(policy) 
 );
 
 [调用方法]
  */
public class WebWalletDao {
	
    private String[] sql = new String[] {""
    		,"insert into tbl_onchainweb_restful_account values(?,?,?)"
    		,"insert into tbl_onchainweb_restful_contract values(?,?,?,?)"
    		,"insert into tbl_onchainweb_restful_key values(?,?,?,?,?,?)"
    		,"select * from tbl_onchainweb_restful_account where policy=?"
    		,"select * from tbl_onchainweb_restful_contract where policy=?"
    		,"select * from tbl_onchainweb_restful_key where policy=?"
    };
    protected Connection getConnection() {
    	throw new RuntimeException("No available connection");
    }
    protected void freeConnection(Connection conn) {
    	throw new RuntimeException("No available connection");
    }
    public void insertAccount(Account entity, String policy) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[1]);
    		statement.setBytes(1, entity.privateKeyEncrypted);
    		statement.setBytes(2, entity.publicKeyHash);
    		statement.setString(3, policy);
    		statement.execute();
    	} catch (SQLException ex) {
    		throw new WebWalletException("insertAccount", ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
    
    public void insertContract(Contract entity, String policy) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[2]);
    		statement.setBytes(1, entity.scriptHash);
    		statement.setBytes(2, entity.publicKeyHash);
    		statement.setBytes(3, entity.rawData);
    		statement.setString(4, policy);
    		statement.execute();
    	} catch (SQLException ex) {
    		throw new WebWalletException("insertContract", ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
    public void insertKey(Key2 entity,  String policy) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[3]);
    		statement.setBytes(1, entity.PasswordHash);
    		statement.setBytes(2, entity.IV);
    		statement.setBytes(3, entity.MasterKey);
    		statement.setBytes(4, entity.Version);
    		statement.setBytes(5, entity.Height);
    		statement.setString(6, policy);
    		statement.execute();
    	} catch (SQLException ex) {
    		System.out.println("sql:"+sql[3]);
    		ex.printStackTrace();
    		throw new WebWalletException("insertKey", ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
    
    public Account[] selectAccount(String policy) {
    	List<Account> list = new ArrayList<Account>();
    	Connection conn = null;
    	PreparedStatement statement = null;
    	ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[4]);
    		statement.setString(1, policy);
    		rs = statement.executeQuery();
    		while(rs.next()) {
    			Account aa = new Account();
    			aa.privateKeyEncrypted = rs.getBytes(1);
    			aa.publicKeyHash = rs.getBytes(2);
    			list.add(aa);
    		}
    	} catch (SQLException ex) {
    		throw new WebWalletException("selectAccount", ex);
		} finally {
			close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    	return list.toArray(new Account[list.size()]);
    }
    public Contract[] selectContract(String policy) {
    	List<Contract> list = new ArrayList<Contract>();
    	Connection conn = null;
    	PreparedStatement statement = null;
    	ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[5]);
    		statement.setString(1, policy);
    		rs = statement.executeQuery();
    		while(rs.next()) {
    			Contract aa = new Contract();
    			aa.scriptHash = rs.getBytes(1);
    			aa.publicKeyHash = rs.getBytes(2);
    			aa.rawData = rs.getBytes(3);
    			list.add(aa);
    		}
    	} catch (SQLException ex) {
    		throw new WebWalletException("selectContract", ex);
		} finally {
			close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    	return list.toArray(new Contract[list.size()]);
    }
    public Key2[] selectKey(String policy) {
    	List<Key2> list = new ArrayList<Key2>();
    	Connection conn = null;
    	PreparedStatement statement = null;
    	ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[6]);
    		statement.setString(1, policy);
    		rs = statement.executeQuery();
    		while(rs.next()) {
    			Key2 aa = new Key2();
    			aa.PasswordHash = rs.getBytes(1);
    			aa.IV = rs.getBytes(2);
    			aa.MasterKey = rs.getBytes(3);
    			aa.Version = rs.getBytes(4);
    			aa.Height = rs.getBytes(5);
    			list.add(aa);
    		}
    	} catch (SQLException ex) {
    		throw new WebWalletException("selectContract", ex);
		} finally {
			close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    	return list.toArray(new Key2[list.size()]);
    }
    
    private void close(PreparedStatement pstm) {
    	if(pstm != null) {
    		try {
				pstm.close();
			} catch (SQLException e) {
			}
    	}
    }
    private void close(ResultSet rs) {
    	if(rs != null) {
    		try {
				rs.close();
			} catch (SQLException e) {
			}
    	}
    }
}
