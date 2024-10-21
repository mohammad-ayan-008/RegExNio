package org.Server.Utils;

import java.util.HashMap;
import java.util.Map;

public class Response {
    String Method;
    String URL;
    QuerryParams param;
    Map<String,String> headers= new HashMap<>();

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public QuerryParams getParam() {
        return param;
    }

    public void setParam(QuerryParams param) {
        this.param = param;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
