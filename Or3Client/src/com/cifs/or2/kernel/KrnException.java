package com.cifs.or2.kernel;

import kz.tamur.common.ErrorCodes;

public final class KrnException extends Exception {
    
    /** Код ошибки. */
    public int code = (int) 0;
    /** Тип ошибки. Если ErrorCodes.TYPE_ERROR - печатать стек-трейс, иначе просто предупреждение вывести */
    public int type = ErrorCodes.TYPE_ERROR;
    /** Объект который вызвал ошибку. */
    public KrnObject object;

    public KrnException() {
        this(0, (KrnObject) null);
    }

    public KrnException(int code) {
        this(code, (KrnObject) null);
    }

    public KrnException(int code, String message) {
        this(code, null, message);
    }

    public KrnException(int code, String message, int type) {
        this(code, null, message, type);
    }

    public KrnException(int code, KrnObject object, String message) {
        super(message);
        this.code = code;
        this.object = object;
    }

    public KrnException(int code, KrnObject object, String message, int type) {
        super(message);
        this.code = code;
        this.object = object;
        this.type = type;
    }

    public KrnException(int code, KrnObject object) {
        super();
        this.code = code;
        this.object = object;
    }
    public KrnException(String message, int code, Throwable cause) {
        super(message, cause);
        this.code=code;
    }
}
