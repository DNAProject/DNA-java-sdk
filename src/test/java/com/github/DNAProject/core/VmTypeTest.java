
package com.github.DNAProject.core;

import org.junit.Assert;
import org.junit.Test;

public class VmTypeTest {

    @Test
    public void valueOf() throws IllegalArgumentException {
        Assert.assertEquals(VmType.NEOVM, VmType.valueOf((byte) 0x01));
        Assert.assertEquals(VmType.WASMVM, VmType.valueOf((byte) 0x03));
    }

    @Test
    public void value() {
        Assert.assertEquals(1, VmType.NEOVM.value());
        Assert.assertEquals(3, VmType.WASMVM.value());
    }

}
