package kz.tamur.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;

public class DataUtil {
	
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + DataUtil.class);

	public static byte[] compress(byte[] data, int level) throws IOException {
		if (data == null)
			return null;
		if (data.length == 0)
			return data;
		ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
		ZipOutputStream zos = new ZipOutputStream(os);
		zos.setLevel(level);
		zos.putNextEntry(new ZipEntry("1"));
		zos.write(data);
		zos.closeEntry();
		zos.close();
		return os.toByteArray();
	}

	public static byte[] decompress(byte[] data) throws IOException {
		if (data == null)
			return null;
		if (data.length == 0)
			return data;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data));
        zis.getNextEntry();

		try {
            Funcs.writeStream(zis, os, Constants.MAX_ARCHIVED_SIZE);
		} catch (IOException e) {
			log.error(e, e);
	        os.close();
	        zis.close();
	        throw e;
		}
        os.close();
        zis.close();
        return os.toByteArray();
	}
}
