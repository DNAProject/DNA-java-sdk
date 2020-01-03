package com.github.DNAProject.smartcontract.nativevm.abi;

import java.util.ArrayList;
import java.util.List;


public class Struct {
    public List list = new ArrayList();
    public Struct(){

    }
    public Struct add(Object... objs){
        for(int i=0;i<objs.length;i++){
            list.add(objs[i]);
        }
        return this;
    }
}
