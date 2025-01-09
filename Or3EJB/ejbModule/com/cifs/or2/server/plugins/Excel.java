package com.cifs.or2.server.plugins;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

import kz.tamur.SecurityContextHolder;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 06.06.2008
 * Time: 18:22:56
 * To change this template use File | Settings | File Templates.
 */
public class Excel implements SrvPlugin {
    Session s;
    public Session getSession() {
        return s;
    }

    public void setSession(Session session) {
        s = session;
    }
    public void resaveFilter(){
        s.resaveFilters();
    }
    /**
     * Создать рабочую книгу
     * @return книга (.xls формат)
     */
    public HSSFWorkbook createWorkbook(){
        HSSFWorkbook wb = new HSSFWorkbook();
        return wb;
    }
    /**
     * Создать рабочую книгу
     * @return книга (.xlsx формат)
     * @throws IOException
     */
    public XSSFWorkbook createXSSFWorkbook() throws IOException {
    	return new XSSFWorkbook();
    }

	public XSSFWorkbook createXSSFWorkbook(String path) throws IOException {//TODO
    	return new XSSFWorkbook(path);
    }
    /**
     * Сохранить рабочую книгу в файл
     * @param wb рабочая книга
     * @param path путь к файлу
     */
    public void saveWorkBookToFile(Workbook wb,String path){
      // Write the output to a file
        FileOutputStream fileOut = null;
        try {
        	File f = new File(path);
            fileOut = new FileOutputStream(f);
            wb.write(fileOut);
            fileOut.close();
            s.deleteFileOnExit(f);
        } catch (FileNotFoundException e) {
        	SecurityContextHolder.getLog().error(e, e);
        } catch (IOException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
    }
    /**
     * Сохранить рабочую книгу в файл
     * @param wb рабочая книга
     * @param path путь к файлу
     * @param isAppend если true, то запишет в конце файла, а не в начале
     */
    public void saveWorkBookToFile(Workbook wb,String path,boolean isAppend){
        // Write the output to a file
          FileOutputStream fileOut = null;
          try {
      		File f = new File(path);
          	fileOut = new FileOutputStream(f, isAppend);
          	wb.write(fileOut);
          	fileOut.close();
            s.deleteFileOnExit(f);
          } catch (FileNotFoundException e) {
          	SecurityContextHolder.getLog().error(e, e);
          } catch (IOException e) {
          	SecurityContextHolder.getLog().error(e, e);
          }
      }
    /**
     * Сохранить книгу в двоичном виде
     * @param wb рабочая книга
     * @return двоичное представление
     */
    public byte[] saveWorkBookToBlob(Workbook wb){
    	// Write the output to a file
    	ByteArrayOutputStream fileOut = null;
    	try {
    		fileOut = new ByteArrayOutputStream();
    		wb.write(fileOut);
    		fileOut.close();
    		return fileOut.toByteArray();
    	} catch (FileNotFoundException e) {
    		SecurityContextHolder.getLog().error(e, e);
    	} catch (IOException e) {
    		SecurityContextHolder.getLog().error(e, e);
    	}
    	return null;
    }
    /**
     * Загрузить книгу из файла
     * @param path путь к файлу
     * @return рабочая книга (.xls формат)
     */
    public HSSFWorkbook loadWorkBookFromFile(String path){
      // Write the output to a file
        FileInputStream fileIn = null;
        HSSFWorkbook wb=null;
        try {
            fileIn = new FileInputStream(path);
            wb= new HSSFWorkbook(fileIn);
            fileIn.close();
        } catch (FileNotFoundException e) {
        	SecurityContextHolder.getLog().error(e, e);
        } catch (IOException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
        return wb;
    }
    /**
     * Загрузить книгу из файла
     * @param path путь к файлу
     * @return рабочая книга (.xls формат)
     */
    public XSSFWorkbook loadXSSFWorkBookFromFile(String path){
      // Write the output to a file
        FileInputStream fileIn = null;
        XSSFWorkbook wb=null;
        try {
            fileIn = new FileInputStream(path);
            wb= new XSSFWorkbook(fileIn);
            fileIn.close();
        } catch (FileNotFoundException e) {
        	SecurityContextHolder.getLog().error(e, e);
        } catch (IOException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
        return wb;
    }
    /**
     * Преобразует двочное представление книги в xls формат
     * @param data двоичное представление
     * @return рабочая книга (.xls формат)
     */
    public HSSFWorkbook loadWorkBookFromBlob(byte[] data){
        // Write the output to a file
    	ByteArrayInputStream fileIn = null;
    	HSSFWorkbook wb=null;
    	try {
    		fileIn = new ByteArrayInputStream(data);
    		wb = new HSSFWorkbook(fileIn);
    		fileIn.close();
    	} catch (FileNotFoundException e) {
    		SecurityContextHolder.getLog().error(e, e);
    	} catch (IOException e) {
    		SecurityContextHolder.getLog().error(e, e);
    	}
    	return wb;
    }
    /**
     * Преобразует двочное представление книги в xlsx формат
     * @param data двоичное представление
     * @return рабочая книга (.xlsx формат)
     */
    public XSSFWorkbook loadXSSFWorkBookFromBlob(byte[] data){
    	// Write the output to a file	
    	ByteArrayInputStream fileIn = null;
        XSSFWorkbook wb=null;
        try {
        	fileIn = new ByteArrayInputStream(data);
        	wb = new XSSFWorkbook(fileIn);
        	fileIn.close();
        } catch (FileNotFoundException e) {
        	SecurityContextHolder.getLog().error(e, e);
        } catch (IOException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
        return wb;
    }
}
