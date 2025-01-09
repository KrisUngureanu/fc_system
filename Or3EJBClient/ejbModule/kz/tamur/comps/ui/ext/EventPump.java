package kz.tamur.comps.ui.ext;

import java.awt.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import kz.tamur.lang.parser.LangUtils;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class EventPump implements InvocationHandler {
    private Frame frame;

    public EventPump(Frame frame) {
        this.frame = frame;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return frame.isShowing() ? Boolean.TRUE : Boolean.FALSE;
    }

    // when the reflection calls in this method has to be
    // replaced once Sun provides a public API to pump events.
    public void start() throws Exception {
        Class clazz = LangUtils.getType("java.awt.Conditional", null);
        Object conditional = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);
        Method pumpMethod = LangUtils.getType("java.awt.EventDispatchThread", null).getDeclaredMethod("pumpEvents", new Class[] { clazz });
        pumpMethod.setAccessible(true);
        pumpMethod.invoke(Thread.currentThread(), conditional);
    }
}