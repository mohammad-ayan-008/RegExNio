package org.Server;

import org.Server.Utils.Modle;
import org.Server.Utils.Response;
import org.Server.configs.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

@MvcController(value = 8081)
@MvcMapping(value = "/mvc")
public class MvcExample {

    @ParseHtmlFille
    @GETContent("/hello")
    public String getContent2(Response response, Modle modle)  {
        if (response.getParam()!= null){
            String name = null;
            String resp = "";
            HttpRequest httpRequest
                    = HttpRequest.newBuilder().GET().uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
                    .build();
            try {
                String body = HttpClient.newBuilder().build().send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
                System.out.println(body);
                modle.putAttribute("response",body);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
             try {
                 name = response.getParam().getParamater("name");
                 modle.putAttribute("name", name);
                 System.out.println(name);
             }catch (Exception e){
                 e.printStackTrace();
             }
        }
        return "Test.ftl";
    }

    @PostContent("/post")
    public String getContent(Response data){
        return data.getURL();
    }
    @GETContent("/h")
    public String getResponse(Response response){
        return "<h1>hellow</h1>";
    }

}
