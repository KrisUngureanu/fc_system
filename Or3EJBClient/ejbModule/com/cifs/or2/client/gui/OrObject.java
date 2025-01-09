package com.cifs.or2.client.gui;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;

public final class OrObject {
    public KrnClass cls;
    public KrnObject obj;
    public String title;
    public String tattrName;
    public long langId;
    public KrnObject[] allObjs;
    public KrnObject[] objs;
    public String[] titles;

    public OrObject(KrnClass cls, KrnObject obj, String title, String tattrName,
                    long langId) {
        this.cls = cls;
        this.obj = obj;
        this.title = title;
        this.tattrName = tattrName;
        this.langId = langId;
    }

    public OrObject(KrnClass cls, KrnObject[] allObjs, KrnObject[] objs,
                    String[] titles, String tattrName, int langId) {
        this.cls = cls;
        this.allObjs = allObjs;
        this.objs = objs;
        this.titles = titles;
        this.tattrName = tattrName;
        this.langId = langId;
    }

    public String toString() {
        if (titles != null && titles.length > 0) {
            StringBuffer res = new StringBuffer();
            res.append(titles[0]);
            for (int i = 1; i < titles.length; ++i) {
                res.append(";" + titles[i]);
            }
            return res.toString();
        } else
            return title;
    }
}