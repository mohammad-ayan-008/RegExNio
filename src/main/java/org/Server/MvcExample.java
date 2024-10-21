package org.Server;

import org.Server.Utils.Modle;
import org.Server.Utils.Response;
import org.Server.configs.*;

import java.util.ArrayList;

@MvcController(value = 8081)
@MvcMapping(value = "/mvc")
public class MvcExample {

    @ParseHtmlFille
    @GETContent("/hello")
    public String getContent2(Response response, Modle modle)  {
        if (response.getParam()!= null){
                String name = null;
            ArrayList<String> list= new ArrayList<>();
            list.add("Ayan");
            list.add("Ayan$123");
            list.add("Ayan$124");

            modle.putAttribute("items",list);
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

}
