package kz.tamur.fc.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import kz.tamur.util.crypto.CheckSignResult;

public class SignatureCheckingResult {
    
    private static Map<String, CheckSignResult> checkingResultMap;

    public static Map<String, CheckSignResult> getCheckingResultMap() {
        if (checkingResultMap == null) {
            checkingResultMap = Collections.synchronizedMap(new HashMap<String, CheckSignResult>());
        }
        return checkingResultMap;
    }
}