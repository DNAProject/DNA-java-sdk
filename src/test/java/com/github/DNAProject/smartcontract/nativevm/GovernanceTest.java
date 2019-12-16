
package com.github.DNAProject.smartcontract.nativevm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GovernanceTest {
    @Test
    public void newPeerAttributes() {
        PeerAttributes peerAttributes1 = new PeerAttributes();
        assertNull(peerAttributes1.peerPubkey);
        assertEquals(0, peerAttributes1.maxAuthorize);
        assertEquals(0, peerAttributes1.t1PeerCost);
        assertEquals(0, peerAttributes1.t2PeerCost);
        assertEquals(0, peerAttributes1.tPeerCost);
        String peerPubKey = "0379eff8cc07441daad01234291ba3f3da3e323119d97d6f1875da5f414be470b9";
        PeerAttributes peerAttributes2 = new PeerAttributes(peerPubKey);
        assertEquals(peerPubKey, peerAttributes2.peerPubkey);
        assertEquals(0, peerAttributes2.maxAuthorize);
        assertEquals(100, peerAttributes2.t1PeerCost);
        assertEquals(100, peerAttributes2.t2PeerCost);
        assertEquals(100, peerAttributes2.tPeerCost);
    }
}
