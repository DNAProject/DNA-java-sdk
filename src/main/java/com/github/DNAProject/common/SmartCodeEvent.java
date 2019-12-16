package com.github.DNAProject.common;

import java.util.List;

public class SmartCodeEvent {
    public String TxHash;
    public int State;
    public long GasConsumed;
    public List<NotifyEventInfo> Notify;

    public String getTxHash() {
        return TxHash;
    }

    public void setTxHash(String txHash) {
        TxHash = txHash;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public long getGasConsumed() {
        return GasConsumed;
    }

    public void setGasConsumed(long gasConsumed) {
        GasConsumed = gasConsumed;
    }

    public List<NotifyEventInfo> getNotify() {
        return Notify;
    }

    public void setNotify(List<NotifyEventInfo> notify) {
        Notify = notify;
    }

}
