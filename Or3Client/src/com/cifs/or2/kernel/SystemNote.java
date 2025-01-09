package com.cifs.or2.kernel;

import java.util.Date;

public class SystemNote extends Note {

    public final Object data;
    public final int type;
    public final String title;

    public SystemNote(Date time, UserSessionValue from, Object data, int type, String title) {
        super(time, from);
        this.data = data;
        this.type = type;
        this.title = title;
    }

}
