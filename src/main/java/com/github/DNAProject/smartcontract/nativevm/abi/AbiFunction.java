/*
 * Copyright (C) 2018 The DNA Authors
 * This file is part of The DNA library.
 *
 *  The DNA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The DNA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The DNA.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.DNAProject.smartcontract.nativevm.abi;


import com.alibaba.fastjson.JSON;
import com.github.DNAProject.common.ErrorCode;
import com.github.DNAProject.sdk.exception.SDKException;

import java.util.List;

/**
 * smartcode abi function
 */
public class AbiFunction {
    public String name;
    public String returntype;
    public List<Parameter> parameters;

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }
    public void setParamsValue(Object... objs) throws Exception{
        if(objs.length != parameters.size()){
            throw new SDKException(ErrorCode.ParamError);
        }
        for (int i = 0; i < objs.length; i++) {
            parameters.get(i).setValue(objs[i]);
        }
    }
    public Parameter getParameter(String name) {
        for (Parameter e : parameters) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }
    public void clearParamsValue() {
        for (Parameter e : parameters) {
            e.setValue(null);
        }
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
