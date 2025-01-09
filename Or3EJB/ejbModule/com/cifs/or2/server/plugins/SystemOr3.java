package com.cifs.or2.server.plugins;

import com.cifs.or2.server.orlang.SrvPlugin;

import kz.tamur.SecurityContextHolder;

import com.cifs.or2.server.Session;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 17.03.2007
 * Time: 16:59:43
 * To change this template use File | Settings | File Templates.
 */
public class SystemOr3 implements SrvPlugin {
    Session s;
    public Session getSession() {
        return s;
    }

    public void setSession(Session session) {
        s = session;
    }
    /**
     * Пересохранить фильтры
     */
    public void resaveFilter(){
        s.resaveFilters();
    }

    public void resaveFilter(long filterId) throws KrnException {
        s.saveFilter(filterId, 0);
    }

    public void resaveFilter(KrnObject filter) throws KrnException {
        s.saveFilter(filter.id, 0);
    }
    /**
     * Перезагрузить содержимое фильтров
     */
    public void reloadFilters(){
        s.reloadFilters();
    }
    /**
     * Перезагрузить содержимое данного фильтра
     * @param id фильтр
     */
    public void reloadFilter(long id){
        try {
			s.reloadFilter(id);
		} catch (KrnException e) {
        	SecurityContextHolder.getLog().error(e, e);
		}
    }
    /**
     * Пересохранить триггеры
     * @throws KrnException
     */
	public void resaveTriggers() throws KrnException {
		s.resaveTriggers();
	}
	/**
	 * Выполнить запрос
	 * @param sql строка запроса
	 * @param isUpdate если true - запросы INSERT, UPDATE, или DELETE 
	 * @return список
	 * @see java.sql.Statement#executeUpdate(String)
	 */
    public List runSql(String sql,boolean isUpdate){
        return s.runSql(sql,isUpdate);
    }
    /**
     * Начать отправку
     * @param transportId
     */
    public void startTransport(int transportId){
        try {
            s.startTransport(transportId);
        } catch (KrnException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
    }
    /**
     * Начать репликацию
     */
    public void runReplication(){
        try {
            s.runReplication();
        } catch (KrnException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
    }
    public void setMessageStatus(String initId,String prodId,String text){
        s.setMessageStatus(initId,prodId,text);
    }
    /**
     * Возвращает входной поток буферизации
     * @param path путь к файлу
     * @return
     */
    public BufferedReader getBufferedReader(String path){
        BufferedReader res=null;
        try{
            File file =new File(path);
            if(!file.exists()) return res;
            FileReader fr= new FileReader(file);
            res = new BufferedReader(fr);
        }catch(Exception e){
        	SecurityContextHolder.getLog().error(e, e);
        }
        return res;
    }
}
