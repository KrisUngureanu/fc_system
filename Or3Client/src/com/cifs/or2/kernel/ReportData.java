package com.cifs.or2.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.jdom.Element;

public class ReportData implements Serializable {
	
	transient public Element xml;
	
	public ReportData(Element xml) {
		this.xml = xml;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(bos);
		zos.setLevel(9);
		zos.putNextEntry(new ZipEntry("1"));
		ObjectOutputStream oos = new ObjectOutputStream(zos);
		oos.writeObject(xml);
		zos.closeEntry();
		oos.close();
		zos.close();
		bos.close();
		out.writeInt(bos.size());
		out.write(bos.toByteArray());
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		int size = in.readInt();
		byte[] data = new byte[size];
		
		for (int i=0; i<size; i++)
			data[i] = in.readByte();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ZipInputStream zis = new ZipInputStream(bis);
		zis.getNextEntry();
		Or3ObjectInputStream ois = new Or3ObjectInputStream(zis);
		xml = (Element)ois.readObject();
		ois.close();
		zis.close();
		bis.close();
	}
}
