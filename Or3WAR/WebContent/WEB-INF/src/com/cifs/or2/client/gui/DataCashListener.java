package com.cifs.or2.client.gui;

public interface DataCashListener extends java.util.EventListener {
    void beforeCommitted();
    void cashCommitted();

    void cashRollbacked();

    void cashCleared();
}