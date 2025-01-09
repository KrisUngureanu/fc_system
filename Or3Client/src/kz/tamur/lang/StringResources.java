package kz.tamur.lang;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.10.2005
 * Time: 16:13:55
 * To change this template use File | Settings | File Templates.
 */
public interface StringResources {
    Map<String, String> getStrings(long langId) throws Exception;
    long getDefaultLangId();
    void save() throws Exception;
}
