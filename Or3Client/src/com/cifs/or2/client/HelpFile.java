package com.cifs.or2.client;

import java.util.Map;

public class HelpFile {
	private byte[] content;
	private String fileName;
	private String fsFileName;

	private Map<Long, String> titles;

	public HelpFile(byte[] content, String fileName, Map<Long, String> titles) {
		super();
		this.content = content;
		this.fileName = fileName;
		this.titles = titles;
	}

	public byte[] getContent() {
		return content;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileExtension() {
		return (fileName != null && fileName.lastIndexOf('.') > -1) ? fileName.substring(fileName.lastIndexOf('.')) : ".txt";
	}

	//Имя файла подготовленного в файловой системе
	public String getFSFileName() {
		return fsFileName;
	}

	public void setFSFileName(String name) {
		this.fsFileName = name;
	}

	public String getTitle(Long langId) {
		return titles.get(langId);
	}
}
