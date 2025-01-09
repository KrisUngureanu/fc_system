package com.cifs.or2.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ModelChanges implements Serializable {
	
	public long changeId;
	
	public transient List<ModelChange> changes;
	
	public ModelChanges() {
		this(0);
	}
	
	public ModelChanges(long changeId) {
		this.changeId = changeId;
		changes = new ArrayList<ModelChange>();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(bos);
		zos.setLevel(9);
		zos.putNextEntry(new ZipEntry("1"));
		ObjectOutputStream oos = new ObjectOutputStream(zos);
		oos.writeObject(changes);
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
		changes = (List<ModelChange>)ois.readObject();
		ois.close();
		zis.close();
		bis.close();
	}
}
