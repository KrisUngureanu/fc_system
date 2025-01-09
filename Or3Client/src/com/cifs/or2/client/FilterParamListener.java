package com.cifs.or2.client;

import java.util.EventListener;
import java.util.List;

public interface FilterParamListener extends EventListener {
	void filterParamChanged(String fuid, String pid, List<?> value);
	void clearParam();
}
