package org.Server;

import freemarker.template.TemplateException;
import org.Server.Utils.Modle;
import org.Server.Utils.QuerryParams;
import org.Server.Utils.Response;
import org.Server.configs.*;
import org.Server.markerEngine.FreeMarkerTemplateEngine;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Statement;
import java.util.*;



public class Main {
    private static HashMap<String, Method> METHOD_GET = new HashMap<>();
    private static HashMap<String, Method> METHOD_POST = new HashMap<>();
    private static  Class<MvcExample> mvcExampleClass;
    private static MvcExample instance;
    private static int PORT;
    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, URISyntaxException {
        mvcExampleClass = MvcExample.class;
        instance = mvcExampleClass.getConstructor().newInstance();
        PORT = mvcExampleClass.getAnnotation(MvcController.class).value();
        ExtractMethods(mvcExampleClass);
        ExtractPOStMethods(mvcExampleClass);
        //Initialize a server
        ServerSocketChannel socketChannel =  ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(PORT));
        socketChannel.configureBlocking(false);

        //Selector for multiplex channels
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server Started At port "+PORT);

        while(true){
            //Detect for events via
            if (selector.select()==0){
                continue;
            }

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey next = iterator.next();
                iterator.remove();

                if (next.isAcceptable()){
                    if(next.channel() instanceof ServerSocketChannel sock){
                        SocketChannel accept = sock.accept();
                        accept.configureBlocking(false);
                        accept.register(selector,SelectionKey.OP_READ);
                    }
                }
                if (next.channel() instanceof  SocketChannel accept){
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int bytesRead = accept.read(buffer);
                    buffer.flip();
                    String response =new String(buffer.array()).trim();
                    parseResponse(response);
                    String MEthodType = ExtractMethodType(response);
                    if (MEthodType.equals("GET")) {
                       HandleGetRequest(accept,response);
                    }
                    if (MEthodType.equals("POST")){
                        handlePostRequest(accept,response);
                    }
                    next.cancel();
                }

            }

        }
    }
    private static Response parseResponse(String resp) throws URISyntaxException {
        Response response = new Response();
        final String[] split = resp.trim().split("\n");
        response.setMethod(split[0].split(" ")[0]);
        response.setURL(split[0].split(" ")[1]);
        for(String line: split){
            if (line.isEmpty()) continue;
            if (line.contains("HTTP/1.1")) continue;
            if (!line.contains("&") ){
                String key = line.split(":\\s")[0];
                String value = line.split(":\\s")[1];
                response.getHeaders().put(key, value);
            }
        }
        String url = response.getURL();
        QuerryParams params = new QuerryParams();
        if (!url.startsWith("/favicon.ico")){
            String query = new URI(url).getQuery();

            if (query != null && query.contains("&")){
                String[] split1 = query.split("&");
                for (String q: split1){
                    String key = q.split("=")[0];
                    String value = q.split("=")[1];
                    params.putParam(key,value);
                }
            }else {
                String key = query.split("=")[0];
                String value = query.split("=")[1];
                params.putParam(key,value);
            }
            response.setParam(params);

        }
        return response;
    }


    public static void writeFileToSocket(SocketChannel channel, String pth, Modle modle) throws IOException {

        URL resourceUrl = Main.class.getClassLoader().getResource(pth);
        Path path = Paths.get(resourceUrl.getPath().substring(3));
        try {

            String fileContent = Files.readString(path);
            fileContent = new  FreeMarkerTemplateEngine().render(pth,modle.getMap());
            // 2. Create the HTTP response header with the correct Content-Length
            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + fileContent.length() + "\r\n" +
                    "\r\n";  // Extra newline after headers

            // 3. Write the HTTP headers and file content to the channel
            ByteBuffer buffer = ByteBuffer.allocate(httpResponse.length() + fileContent.length());
            buffer.put(httpResponse.getBytes(StandardCharsets.UTF_8));  // Add headers
            buffer.put(fileContent.getBytes(StandardCharsets.UTF_8));  // Add file content

            // Flip the buffer to prepare it for reading
            buffer.flip();

            // Write the entire buffer to the SocketChannel
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }

            // 4. Close the connection
            channel.close();
            System.out.println("File sent successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            // Ensure the channel is closed in case of any exceptions
            channel.close();
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }
    public static String ExtractRoute(String reponse){
        return reponse.split("\n")[0].split(" ")[1];
    }
    public static String ExtractMethodType(String reponse){
        return reponse.split("\n")[0].split(" ")[0];
    }
    public static void writeToSocket(SocketChannel accept,String html)throws IOException{
        String data = "HTTP/1.1 200 Ok \r\n"
                +"Content-Type:text/html \r\n"
                +"Content-Length: 38\r\n"+"\r\n"+
                html;
        accept.write(ByteBuffer.wrap(data.getBytes()));
        accept.close();
    }
    public static void ExtractMethods(Class<?> clss){
        if (!clss.isAnnotationPresent(MvcController.class)){
            throw new RuntimeException("Class is not a mvc Controller");
        }

        final String[] route = {""};
        final String[] route_post = {""};

        if (clss.isAnnotationPresent(MvcMapping.class)){
            route[0] += clss.getAnnotation(MvcMapping.class).value();
        }

        Arrays.stream(clss.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(GETContent.class))
                .forEach(method -> {
                    GETContent annotation = method.getAnnotation(GETContent.class);
                    route[0] += annotation.value();
                    METHOD_GET.put(route[0],method);
                });



    }

    public static void ExtractPOStMethods(Class<?> clss){
        if (!clss.isAnnotationPresent(MvcController.class)){
            throw new RuntimeException("Class is not a mvc Controller");
        }

        final String[] route = {""};

        if (clss.isAnnotationPresent(MvcMapping.class)){
            route[0] += clss.getAnnotation(MvcMapping.class).value();
        }

        Arrays.stream(clss.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(PostContent.class))
                .forEach(method -> {
                    PostContent annotation = method.getAnnotation(PostContent.class);
                    route[0] += annotation.value();
                    METHOD_POST.put(route[0],method);
                });

    }

    public static void HandleGetRequest(SocketChannel accept,String response) throws InvocationTargetException, IllegalAccessException, IOException, URISyntaxException {
        String s = ExtractRoute(response);
        if (s.equals("/favicon.ico")) {
            accept.close();
            return;
        }
        Response response1 = parseResponse(response);
        String rout = null;
        if (s.contains("?")) {
            rout = s.substring(0,s.indexOf('?'));
            //System.out.println(rout);
            Method method = METHOD_GET.get(rout);
            if (method != null && !method.isAnnotationPresent(ParseHtmlFille.class)) {
                String data = (String) method.invoke(instance,response1);
                writeToSocket(accept, data);
            } else {
                if (method != null) {
                    Modle modle = new Modle();
                    String path = (String) method.invoke(instance,response1,modle);
                    writeFileToSocket(accept, path,modle);
                }
            }
        }else{
            Method method = METHOD_GET.get(s);
            if (method != null && !method.isAnnotationPresent(ParseHtmlFille.class)) {
                String data = (String) method.invoke(instance,response1);
                writeToSocket(accept, data);
            } else {
                if (method != null) {
                    Modle modle = new Modle();
                    String path = (String) method.invoke(instance,response1,modle);
                    writeFileToSocket(accept, path,modle);
                }
            }
        }
    }

    public static void handlePostRequest(SocketChannel accept,String response) throws IOException, InvocationTargetException, IllegalAccessException {
        System.out.println("Entered");
        Method method = METHOD_POST.get(ExtractRoute(response));
        if (method != null && !method.isAnnotationPresent(ParseHtmlFille.class)) {
            System.out.println("Entered 2");
            String[] split = response.split("\n");
            String s = URLDecoder.decode(split[split.length - 1], StandardCharsets.UTF_8.name());
            String data = (String) method.invoke(instance,s);
            writeToSocket(accept, data);
        }
    }

}