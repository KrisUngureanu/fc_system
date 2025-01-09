package com.cifs.or2.server.orlang;

import com.cifs.or2.server.Session;

import kz.tamur.SecurityContextHolder;
import kz.tamur.lang.GlobalFunc;
import kz.tamur.util.Funcs;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.07.2005
 * Time: 17:34:24
 * To change this template use File | Settings | File Templates.
 */
public class SrvGlobalFunc implements GlobalFunc {

    private Session session;

    public SrvGlobalFunc(Session session) {
        this.session = session;
    }

    public Object invoke(String name, Object[] params, Stack<String> callStack) throws Exception {
		if ("getNum".equals(name) || "getNum2".equals(name)) return null;
        String expr = session.getGlobalFunc(name);
        if (expr != null) {
            SrvOrLang orLang = new SrvOrLang(session);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("OBJS", params);
            try {
                orLang.evaluate2(expr, vc, null, false, callStack, -1);
            } catch (Exception e) {
                StringBuffer msg = new StringBuffer();
                msg.append(name);
                msg.append('(');
                if (params.length > 0) {
                    msg.append(params[0]);
                }
                for (int i = 1; i < params.length; i++) {
                    msg.append(",");
                    msg.append(params[i]);
                }
                msg.append(')');
                SecurityContextHolder.getLog().error(msg);
                throw e;
            }
            return vc.get("RETURN");
        }
        return null;
    }

	public Object exec(String fileName, List<Object> args, Stack<String> callStack) throws Exception {
		File f = Funcs.getCanonicalFile(fileName);
		byte[] b = Funcs.read(f);
		String func = Funcs.normalizeInput(new String(b, "UTF-8"));
        SrvOrLang lang = new SrvOrLang(session);
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("ARGS", args);
		lang.evaluate2(func, vars, null, false, callStack, -1);
		return vars.get("RETURN");
	}
}
