package com.cifs.or2.client;

import com.cifs.or2.kernel.Time;

public interface ReplProgressListener {
	void replFilesProgress(int type, int filesCount, int currentFileNumber, String currentFileName, Time importTime);
	void replChangesProgress(int type, int currentChangeNumber, int changesCount, String changeType, String changeId);
}