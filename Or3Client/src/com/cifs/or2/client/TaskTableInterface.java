package com.cifs.or2.client;

import com.cifs.or2.kernel.SystemNote;

public interface TaskTableInterface {

    public void taskReload(long flowId, long ifsPar);

    public void doOnNotification(SystemNote note);
}
