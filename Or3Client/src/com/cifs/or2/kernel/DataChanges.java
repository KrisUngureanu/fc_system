package com.cifs.or2.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DataChanges implements Serializable {

	public long changeId;
	public transient List<Object[]> rows;
	
	public DataChanges() {
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(bos);
		zos.setLevel(9);
		zos.putNextEntry(new ZipEntry("1"));
		ObjectOutputStream oos = new ObjectOutputStream(zos);
		oos.writeObject(rows);
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
		rows = (List<Object[]>)ois.readObject();
		ois.close();
		zis.close();
		bis.close();
	}
}
