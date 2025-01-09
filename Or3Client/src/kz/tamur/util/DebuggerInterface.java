package kz.tamur.util;

import java.util.List;
import java.util.Set;

import kz.tamur.lang.ErrRecord;

public interface DebuggerInterface {
	List<ErrRecord> getErrors();
	void debugExpression(String nodeName, String expr, Set<String> vars);
}
