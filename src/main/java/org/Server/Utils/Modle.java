package org.Server.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Modle {
    private Map<String, Object> key_val= new HashMap<>();

    public void putAttribute(String key,Object  value){
        key_val.put(key,value);
    }

    public Map<String,Object> getMap(){
        return key_val;
    }
}
