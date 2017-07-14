package DNA.Implementations.Wallets.Oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DNA.Helper;
import DNA.sdk.dbpool.DBResource;

public class UserDao {
	private DBResource dbRs = DBResource.getInstance();
	
    private String[] sql = new String[] {""
    		,"select policy from tbl_onchainweb_policy where username=?"
    		,"insert into tbl_onchainweb_policy values(?,?,?)"
    		
    		,"insert into tbl_onchainweb_account values(?,?,?)"
    		,"insert into tbl_onchainweb_contract values(?,?,?,?)"
    		,"insert into tbl_onchainweb_key values(?,?,?)"
    		,"select * from tbl_onchainweb_account where policy=?"
    		,"select * from tbl_onchainweb_contract where policy=?"
    		,"select * from tbl_onchainweb_key where policy=? and name=?"
    };
    
    public String hasPolicy(String username) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[1]);
    		statement.setString(1, username);
    		rs = statement.executeQuery();
    		while(rs.next()) {
    			return rs.getString(1);
    		}
    		return "";
    	} catch (SQLException ex) {
    		throw new UserDaoException("hasPolicy", ex);
		} finally {
			close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
    public void addPolicy(Policy entity) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[2]);
    		statement.setString(1, entity.username);
    		statement.setString(2, Helper.toHexString(entity.password));
    		statement.setString(3, entity.policy);
    		statement.execute();
    	} catch (SQLException ex) {
    		throw new UserDaoException("addPolicy", ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
    public void insertAccount(Account entity, String policy) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[3]);
    		statement.setString(1, Helper.toHexString(entity.privateKeyEncrypted));
    		statement.setString(2, Helper.toHexString(entity.publicKeyHash));
    		statement.setString(3, policy);
    		statement.execute();
    	} catch (SQLException ex) {
    		throw new UserDaoException("insertAccount", ex);
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
    		statement = conn.prepareStatement(sql[4]);
    		statement.setString(1, Helper.toHexString(entity.scriptHash));
    		statement.setString(2, Helper.toHexString(entity.publicKeyHash));
    		statement.setString(3, Helper.toHexString(entity.rawData));
    		statement.setString(4, policy);
    		statement.execute();
    	} catch (SQLException ex) {
    		throw new UserDaoException("insertContract", ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
    public void insertKey(Key entity, String policy) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[5]);
    		statement.setString(1, entity.name);
    		statement.setString(2, Helper.toHexString(entity.value));
    		statement.setString(3, policy);
    		statement.execute();
    	} catch (SQLException ex) {
    		throw new UserDaoException("insertKey", ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
    public void updateKey(Key entity, String policy) {
    	String sql = "update tbl_onchainweb_key set val=? where name=? and policy=?";
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql);
    		statement.setString(1, Helper.toHexString(entity.value));
    		statement.setString(2, entity.name);
    		statement.setString(3, policy);
    		statement.execute();
    	} catch (SQLException ex) {
    		throw new UserDaoException("updateKey", ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
    public boolean hasKey(Key entity, String policy) {
    	String sql = "select count(*) from tbl_onchainweb_key where name=? and policy=?";
    	Connection conn = null;
    	PreparedStatement statement = null;
    	ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql);
    		statement.setString(1, entity.name);
    		statement.setString(2, policy);
    		rs = statement.executeQuery();
    		while(rs.next()) {
    			return rs.getInt(1) > 0;
    		}
    		return false;
    	} catch (SQLException ex) {
    		throw new UserDaoException("hasKey", ex);
		} finally {
			close(rs);
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
    		statement = conn.prepareStatement(sql[6]);
    		statement.setString(1, policy);
    		rs = statement.executeQuery();
    		while(rs.next()) {
    			Account aa = new Account();
    			aa.privateKeyEncrypted = Helper.hexToBytes(rs.getString(1));
    			aa.publicKeyHash = Helper.hexToBytes(rs.getString(2));
    			list.add(aa);
    		}
    	} catch (SQLException ex) {
    		throw new UserDaoException("selectAccount", ex);
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
    		statement = conn.prepareStatement(sql[7]);
    		statement.setString(1, policy);
    		rs = statement.executeQuery();
    		while(rs.next()) {
    			Contract aa = new Contract();
    			aa.scriptHash = Helper.hexToBytes(rs.getString(1));
    			aa.publicKeyHash = Helper.hexToBytes(rs.getString(2));
    			aa.rawData = Helper.hexToBytes(rs.getString(3));
    			list.add(aa);
    		}
    	} catch (SQLException ex) {
    		throw new UserDaoException("selectContract", ex);
		} finally {
			close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    	return list.toArray(new Contract[list.size()]);
    }
    public Key selectKey(String name, String policy) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sql[8]);
    		statement.setString(1, policy);
    		statement.setString(2, name);
    		rs = statement.executeQuery();
    		while(rs.next()) {
    			Key aa = new Key();
    			aa.name = rs.getString(1);
    			aa.value = Helper.hexToBytes(rs.getString(2));
    			return aa;
    		}
    		throw new RuntimeException("ee=name:"+name+",policy:"+policy);
    	} catch (SQLException ex) {
    		throw new UserDaoException("selectKey", ex);
		} finally {
			close(rs);
			close(statement);
    		freeConnection(conn);
    	}
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
    
    
    
    private String[] sqlCoin = new String[] {"",
    		"select * from tbl_onchainweb_Coin where policy=?",
    		"insert into tbl_onchainweb_Transaction values (?,?,?,?,?,?)",
    		"insert into tbl_onchainweb_Coin values(?,?,?,?,?,?,?)",
    		"update tbl_onchainweb_Coin set coin_state=? where txid=? and tx_index=? and policy=?",
    		"delete from tbl_onchainweb_Coin where txid=? and tx_index=? and policy=?"
    		
    };
    public Coin[] selectCoin(String policy) {
    	List<Coin> list = new ArrayList<Coin>();
    	Connection conn = null;
    	PreparedStatement statement = null;
    	ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sqlCoin[1]);
    		statement.setString(1, policy);
    		rs = statement.executeQuery();
    		while(rs.next()) {
    			Coin aa = new Coin();
    			aa.txid = Helper.hexToBytes(rs.getString(1));
    			aa.index = rs.getInt(2);
    			aa.assetId = Helper.hexToBytes(rs.getString(3));
    			aa.scriptHash = Helper.hexToBytes(rs.getString(4));
    			aa.value = Long.parseLong(rs.getString(5));
    			aa.state = rs.getInt(6);
    			list.add(aa);
    		}
    		return list.toArray(new Coin[list.size()]);
    	} catch (SQLException ex) {
    		throw new UserDaoException("selectCoin", ex);
		} finally {
			close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
    
	public void insertTransaction(Transaction entity, String policy) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sqlCoin[2]);
    		statement.setString(1, Helper.toHexString(entity.hash));
    		statement.setInt(2, entity.type);
    		statement.setString(3, Helper.toHexString(entity.rawData));
    		statement.setInt(4, entity.height);
    		statement.setInt(5, entity.time);
    		statement.setString(6, policy);
    		statement.execute();
    	} catch (SQLException ex) {
    		throw new UserDaoException("insertTransaction,time="+entity.time, ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
	public void insertCoin(Coin[] coins, String policy) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sqlCoin[3]);
    		for(Coin cc : coins) {
    			statement.setString(1, Helper.toHexString(cc.txid));
        		statement.setInt(2, cc.index);
        		statement.setString(3, Helper.toHexString(cc.assetId));
        		statement.setString(4, Helper.toHexString(cc.scriptHash));
        		statement.setString(5, String.valueOf(cc.value));
        		statement.setInt(6, cc.state);
        		statement.setString(7, policy);
        		statement.addBatch();
    		}
    		statement.executeBatch();
    	} catch (SQLException ex) {
    		throw new UserDaoException("insertCoin", ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
	public void updateCoin(Coin[] coins, String policy) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sqlCoin[4]);
    		for(Coin cc : coins) {
    			statement.setInt(1, cc.state);
        		statement.setString(2, Helper.toHexString(cc.txid));
        		statement.setInt(3, cc.index);
        		statement.setString(4, policy);
        		statement.addBatch();
    		}
    		statement.executeBatch();
    	} catch (SQLException ex) {
    		throw new UserDaoException("updateCoin", ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
	public void deleteCoin(Coin[] coins, String policy) {
    	Connection conn = null;
    	PreparedStatement statement = null;
    	//ResultSet rs = null;
    	try {
    		conn = getConnection();
    		statement = conn.prepareStatement(sqlCoin[5]);
    		for(Coin cc : coins) {
    			statement.setString(1, Helper.toHexString(cc.txid));
        		statement.setInt(2, cc.index);
        		statement.setString(3, policy);
        		statement.addBatch();
    		}
    		statement.executeBatch();
    	} catch (SQLException ex) {
    		throw new UserDaoException("deleteCoin", ex);
		} finally {
			//close(rs);
			close(statement);
    		freeConnection(conn);
    	}
    }
	
	private Connection getConnection() {
    	return dbRs.getConnection();
    }
    private void freeConnection(Connection conn) {
    	dbRs.freeConnection(conn);
    }
}
