package kz.tamur.lang;

import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.07.2005
 * Time: 16:19:27
 * To change this template use File | Settings | File Templates.
 */
public interface GlobalFunc {
    public Object invoke(String name, Object[] params, Stack<String> callStack) throws Exception;
    public Object exec(String fileName, List<Object> args, Stack<String> callStack) throws Exception;
}
