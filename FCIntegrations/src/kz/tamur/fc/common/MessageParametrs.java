package kz.tamur.fc.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageParametrs {
	
	private static Map<String, List<String>> paramsMap;
    private static Set<String> OIPMessages;
    private static Set<String> NCCMessages;
    public static Set<String> TransportSignature = Collections.synchronizedSet(new HashSet<String>());
    public static Set<String> DataNamespace = Collections.synchronizedSet(new HashSet<String>());
    public static Map<String, Integer> DataSignature = Collections.synchronizedMap(new HashMap<String, Integer>());
    public static Map<String, String> EmptyPrefixNamespace = Collections.synchronizedMap(new HashMap<String, String>());
    public static Map<String, String> BusinessDataSignature = Collections.synchronizedMap(new HashMap<String, String>());
    public static Set<String> BusinessDataCDATA = Collections.synchronizedSet(new HashSet<String>());
    
    public static Map<String, List<String>> getParamsMap() {
        if (paramsMap == null) {
            paramsMap = Collections.synchronizedMap(new HashMap<String, List<String>>());
        }
        return paramsMap;
    }
    
    public static Set<String> getOIPMessagesSet() {
        if (OIPMessages == null) {
            OIPMessages = Collections.synchronizedSet(new HashSet<String>());
        }
        return OIPMessages;
    }
    
    public static Set<String> getNCCMessagesSet() {
        if (NCCMessages == null) {
            NCCMessages = Collections.synchronizedSet(new HashSet<String>());
        }
        return NCCMessages;
    }
}