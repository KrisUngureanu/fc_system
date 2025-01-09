package kz.tamur.rt.orlang;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import kz.tamur.lang.GlobalFunc;

public class ClientGlobalFunc implements GlobalFunc {

	public Object invoke(String name, Object[] params, Stack<String> callStack) throws Exception {
		if ("getNum".equals(name) || "getNum2".equals(name)) return null;
		throw new Exception("Not implemented.");
	}

	public Object exec(String fileName, List<Object> args, Stack<String> callStack) throws Exception {
		StringBuilder sb = new StringBuilder();
		Reader fr = new InputStreamReader(new FileInputStream(fileName));
		char[] cbuf = new char[4096];
		for(int n = 0; (n = fr.read(cbuf)) > 0; sb.append(cbuf, 0, n));
		fr.close();
		ClientOrLang lang = new ClientOrLang(null);
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("ARGS", args);
		lang.evaluate2(sb.toString(), vars, null, false, callStack);
		return vars.get("RETURN");
	}

}
