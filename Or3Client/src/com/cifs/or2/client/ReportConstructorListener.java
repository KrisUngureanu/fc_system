package com.cifs.or2.client;

import com.cifs.or2.kernel.ReportNote;

public interface ReportConstructorListener {
	String executeCommand(ReportNote note);
}