package com.github.DNAProject.smartcontract;

import com.github.DNAProject.DnaSdk;
import com.github.DNAProject.sdk.exception.SDKException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VmTest {

    DnaSdk ontSdk;
    Vm vm;
    @Before
    public void setUp(){
        ontSdk = DnaSdk.getInstance();
        vm = new Vm(ontSdk);

    }

    @Test
    public void buildNativeParams() throws SDKException {
//        Address addr = Address.decodeBase58("TA9MXtwAcXkUMuujJh2iNRaWoXrvzfrmZb");
//        vm.buildNativeParams(addr,"init","1".getBytes(),null,0,0);
    }
}