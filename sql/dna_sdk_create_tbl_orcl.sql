drop table tbl_onchainweb_policy cascade constraints ;
drop table tbl_onchainweb_account cascade constraints ;
drop table tbl_onchainweb_contract cascade constraints ;
drop table tbl_onchainweb_key cascade constraints ;
drop table tbl_onchainweb_Coin cascade constraints ;
drop table tbl_onchainweb_Transaction cascade constraints ;

CREATE TABLE tbl_onchainweb_policy (
  username varchar2(20) NOT NULL,
  password varchar2(20) NOT NULL,
  policy varchar2(128) NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE tbl_onchainweb_account (
  PrivateKeyEncrypted varchar2(224) NOT NULL,
  PublicKeyHash varchar2(64) NOT NULL,
  policy varchar2(128) NOT NULL
); 

CREATE TABLE tbl_onchainweb_contract (
  ScriptHash varchar2(64) NOT NULL,
  PublicKeyHash varchar2(64) NOT NULL,
  RawData varchar2(1024) NOT NULL,
  policy varchar2(128) NOT NULL
);

CREATE TABLE tbl_onchainweb_key (
  name varchar2(32) NOT NULL,
  val varchar2(1024) NOT NULL,
  policy varchar2(128) NOT NULL,
  PRIMARY KEY (name, policy)
); 

CREATE TABLE tbl_onchainweb_Coin (
  txid varchar2(64) NOT NULL,
  tx_index NUMBER(10) NOT NULL,
  asset_id varchar2(64) NOT NULL,
  script_hash varchar2(64) NOT NULL,
  amount varchar2(32) NOT NULL,
  coin_state NUMBER(3) NOT NULL,
  policy varchar2(128) NOT NULL
); 

CREATE TABLE tbl_onchainweb_Transaction (
  txid varchar2(64) NOT NULL,
  tx_type NUMBER(10) NOT NULL,
  raw_data varchar2(1024) NOT NULL,
  height NUMBER(10) NOT NULL,
  block_time NUMBER(32) NOT NULL,
  policy varchar2(128) NOT NULL
);