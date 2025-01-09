package com.cifs.or2.server.plugins;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class TabFilePlugin implements SrvPlugin {
    Session s;
	public Session getSession() {
		return s;
	}

	public void setSession(Session session) {
		s=session;
	}
	/**
	 * Загрузить данные
	 * @param fileName имя файла, туда загрузятся данные
	 * @param enc кодировка
	 * @param delimiter разделитель
	 * @return данные
	 * @throws IOException
	 */
	public List<String[]> loadData(String fileName, String enc, String delimiter) throws IOException {
		List<String[]> res = new ArrayList<String[]>();
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), enc));
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			res.add(line.split(delimiter));
		}
		r.close();
		return res;
	}
	/**
	 * Загрузить данные, указывая границы
	 * @param fileName имя файла, туда загрузятся данные
	 * @param enc кодировка
	 * @param delimiter разделитель
	 * @param number начиная с этой строки будет идти запись
	 * @param count до этой строки
	 * @return данные
	 * @throws IOException
	 */
	public List<String[]> loadData(String fileName, String enc, String delimiter,long number,long count) throws IOException {
		List<String[]> res = new ArrayList<String[]>();
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), enc));
		long i=0;
		for (String line = r.readLine(); (line != null) && i<(number+count); line = r.readLine()) {
			if(i>=number && i<(number+count))
				res.add(line.split(delimiter));
			i++;
		}
		r.close();
		return res;
	}
	/**
	 * Загрузить двоичные данные, указывая границы
	 * @param file путь к двоичному файлу
	 * @param enc кодировка
	 * @param delimiter разделитель
	 * @param number начиная с этой строки будет считвание
	 * @param count до этой строки
	 * @return данные
	 * @throws IOException
	 */
	public List<String[]> loadData(byte[] file, String enc, String delimiter, long number, long count) throws IOException {
		List<String[]> res = new ArrayList<String[]>();
		BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file), enc));
		if (number > 0) {
			// пропускаем (number - 1) строк
			int i = 0;
			for (String line = r.readLine(); line != null && i < number; line = r.readLine(), i++);
		}
		int i = 0;
		for (String line = r.readLine(); (line != null) && (count == 0 || i < count); line = r.readLine(), i++) {
			res.add(delimiter != null ? line.split(delimiter) : new String[] {line});
		}
		r.close();
		return res;
	}
	
}
