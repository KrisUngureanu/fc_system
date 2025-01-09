package kz.tamur.rt.orlang;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import kz.tamur.comps.OrFrame;
import kz.tamur.lang.GlobalFunc;
import kz.tamur.util.Funcs;

public class ClientGlobalFunc implements GlobalFunc {

	public Object invoke(String name, Object[] params, Stack<String> callStack) throws Exception {
		if ("getNum".equals(name) || "getNum2".equals(name)) return null;
		throw new Exception("Not implemented.");
	}

	public Object exec(String fileName, List<Object> args, Stack<String> callStack) throws Exception {
		File f = Funcs.getCanonicalFile(fileName);
		byte[] b = Funcs.read(f);
		ClientOrLang lang = new ClientOrLang((OrFrame)null);
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("ARGS", args);
		lang.evaluate2(new String(b, "UTF-8"), vars, null, false, callStack);
		return vars.get("RETURN");
	}

}
