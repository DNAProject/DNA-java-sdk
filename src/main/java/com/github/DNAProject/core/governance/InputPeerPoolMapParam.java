package com.github.DNAProject.core.governance;

import com.github.DNAProject.core.sidechaingovernance.NodeToSideChainParams;

import java.util.Map;

public class InputPeerPoolMapParam {
    public Map<String, PeerPoolItem> peerPoolMap;
    public Map<String, NodeToSideChainParams> nodeInfoMap;
    public InputPeerPoolMapParam(Map<String, PeerPoolItem> peerPoolMap, Map<String, NodeToSideChainParams> nodeInfoMap){
        this.peerPoolMap = peerPoolMap;
        this.nodeInfoMap = nodeInfoMap;
    }
}
