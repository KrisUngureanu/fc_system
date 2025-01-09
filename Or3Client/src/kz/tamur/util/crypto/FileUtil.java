package kz.tamur.util.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import kz.tamur.common.CommonFileFilter;
import kz.tamur.util.Funcs;

/**
 * класс для работы с файловой системой
 */
public class FileUtil {

    /**
     * получает массив байтов из файла
     * @param file путь к файлу
     * @return массив байтов из файла
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static byte[] getBytesFromFile(String file) throws FileNotFoundException, IOException {
    	if (file.matches(".+")) {
    		return Funcs.read(file);
    	}
    	return null;
    }

    /**
     * сохраняет строку в файл
     * @param str строка для сохранения
     * @param name имя файла
     * @param charset кодировка
     * @throws IOException
     */
    public static void saveToFile(String str, String name, String charset) throws IOException {
    	if (name.matches(".+")) {
	        FileOutputStream fos = new FileOutputStream(Funcs.getCanonicalFile(name));
	        fos.write(str.getBytes(charset));
	        fos.flush();
	        fos.close();
    	}
    }

    /**
     * сохраняет в файл массив байтов
     * @param bytes байты для сохранения
     * @param name имя файла
     * @throws IOException
     */
    public static void saveToFile(byte[] bytes, String name) throws IOException {
    	if (name.matches(".+")) {
	        FileOutputStream fos = new FileOutputStream(Funcs.getCanonicalFile(name));
	        fos.write(bytes);
	        fos.flush();
	        fos.close();
    	}
    }

    /**
     * получает пути ко всем файлам внутри папки
     * @param folder
     * @param extension
     * @return
     */
    public static ArrayList<String> getFilePaths(String folder, String extension){
    	if (folder.matches(".+")) {
	        File directory = Funcs.getCanonicalFile(folder);
	        File[] files = directory.listFiles();
	        ArrayList<String> fileList = new ArrayList<String>();
	
	        for (File file : files) {
	            if (getExtension(file.getName()).equals(extension)) fileList.add(file.getPath());
	        }
	
	        return fileList;
    	}
    	return null;
    }

    /**
     * получает путь к файлу
     * @param fileAbolutePath
     * @return
     */
    public static String getFilePath(String fileAbolutePath){
        File file = Funcs.getCanonicalFile(fileAbolutePath);
        return file.getParent();
    }

    /**
     * получает расширение файла
     * @param fileName
     * @return
     */
    public static String getExtension(String fileName){
        int dotPos = fileName.lastIndexOf(".");
        return fileName.substring(dotPos+1);
    }

    /**
     * получает имя файла без расширения
     * @param fileAbolutePath
     * @return
     */
    public static String getFileNameWithoutExtension(String fileAbolutePath){
        File f = Funcs.getCanonicalFile(fileAbolutePath);
        String fileName = f.getName();
        int dotPos = fileName.lastIndexOf(".");
        return fileName.substring(0, dotPos);
    }


    /**
     * очищает папку
     * @param folder
     */
    public static void clearFolder(String folder) {
        for (File file : Funcs.getCanonicalFile(folder).listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }
    
	public static String selectFile(final String initPath) throws Exception {
		return selectFile("Укажите путь к ключевому файлу", "Выбрать", "p12",
				"Файлы ключей ЭЦП в формате PKCS12", initPath);
	}

	public static String selectFile(final String dialogTitle,
			final String buttonTitle, final String extensions,
			final String description, final String initPath) throws Exception {
		JFileChooser chooser = new JFileChooser();
		String filePath = null;
		int selectionMode = 0;
		chooser.setDialogTitle(dialogTitle);
		if (initPath != null && initPath.indexOf(File.separator) > 0) {
			File f = Funcs.getCanonicalFile(initPath);
			if (f.exists())
				chooser.setSelectedFile(f);
		}
		chooser.setFileSelectionMode(selectionMode);
		String extArray[] = extensions.split(",");
		for (int i = 0; i < extArray.length; i++) {
			extArray[i] = (String) extArray[i];
		}

		CommonFileFilter keyFileFilter = new CommonFileFilter(extArray,
				description);
		keyFileFilter.setExtensionListInDescription(false);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(keyFileFilter);

		int result = chooser.showDialog(null, buttonTitle);
		if (result == 0) {
			File selectedFile = chooser.getSelectedFile();
			if (selectedFile.isFile()) {
				filePath = null;
				try {
					filePath = selectedFile.getCanonicalPath();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return filePath;
	}

}
