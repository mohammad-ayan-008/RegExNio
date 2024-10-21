package org.Server.Utils;

import java.util.HashMap;
import java.util.Map;

public class QuerryParams {
    private Map<String,String> querry = new HashMap<>();

    public String getParamater(String key)throws Exception{
        String s = querry.get(key);
        if (s == null){
            throw new RuntimeException("param not Exist");
        }
        return s;
    }

    public void putParam(String key,String value){
        querry.put(key,value);
    }
}
