package Consol;
import FileHandler.ConsoleFIleHandler;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * this class is for generating , sending and receiving http request.
 * this class supports :
 * methods of "GET,PUT,POST,PATCH,DELETE".
 * forms of "form data , json , x-www-form-urlencoded"
 * and version "HTTP_1_1"
 */
public class ReqSender {

    ConsoleReq consoleReq;

    /**
     * simple constructor .
     * @param req request that this class is build on its info.
     */
    public ReqSender(ConsoleReq req) {
        this.consoleReq = req;
    }

    /**
     * generates , sends and shows response of request.
     */
    public Response sendReq() throws IOException, ClassNotFoundException {

        String[] proxyReq = consoleReq.getRequest().getProxy();
        if ( proxyReq[0].length() != 0 && proxyReq[1].length() != 0 && proxyReq[2].equals("true"))
        {
            Socket socket = new Socket(proxyReq[0],Integer.parseInt(proxyReq[1]));
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            consoleReq.getRequest().updateProxy(2,"false");
            outputStream.writeObject(1);
            outputStream.writeObject(consoleReq.makeArgsOfConsoleReq());
            Response response = (Response) inputStream.readObject();
            return response;
        }

        HttpResponse<byte[]> response = null;
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(consoleReq.isFlowRedirect() ? HttpClient.Redirect.ALWAYS : HttpClient.Redirect.NEVER)
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            HttpRequest request = httpReqMaker();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            showHttpResponse(response);

            if (consoleReq.getBodyResDirect() != null)
                if (consoleReq.getBodyResDirect().equals(""))
                    new Thread(new ResponseWriter(response.body(), "output_[" + System.currentTimeMillis() + "]")).start();
                else
                    new Thread(new ResponseWriter(response.body(), consoleReq.getBodyResDirect())).start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("time out");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Response(response);
    }


    /**
     * makes HttpRequest depends on request info.
     * @return http request built on info .
     * @throws IOException if client temps to send a file that doesnt exist.
     */
    private HttpRequest httpReqMaker() throws IOException {

        long boundary = new Random().nextLong();
        HttpRequest.Builder builder = HttpRequest.newBuilder();

        String query = consoleReq.getRequest().getQueryString( consoleReq.getRequest().HQF_TOStringArr("query"));
        if(!query.equals("?"))
            builder = builder.uri(URI.create(consoleReq.getRequest().getUrl() +query ));
        else
            builder = builder.uri(URI.create(consoleReq.getRequest().getUrl()));

        if((consoleReq.getRequest().getMassageBodyKind() == 0 && consoleReq.getRequest().getBodyText().length()!=0) ||
        consoleReq.getRequest().getFilePath() != null ) {
            builder = builder.header("Content-Type", "multipart/form-data; boundary=" + boundary);
        }
        else if (consoleReq.getRequest().getMassageBodyKind() == 0 )
        {
            builder = builder.headers("Content-Type","application/x-www-form-urlencoded");
        }
        else
            builder = builder.headers("Content-Type","application/json");



        if (consoleReq.getRequest().HQF_TOStringArr("header").length != 0)
            builder = builder.headers(consoleReq.getRequest().HQF_TOStringArr("header"));


        switch (consoleReq.getRequest().getKind()) {
            case GET:
                builder = builder.GET();
                break;

            case POST:
                if(consoleReq.getRequest().getMassageBodyKind() == 0 && consoleReq.getRequest().getBodyText().length()!=0)
                    builder = builder.POST(publishFormData(bodyToMap(),"" + boundary));
                else if(consoleReq.getRequest().getFilePath() != null)
                    builder = builder.POST(publishFormData(fileBinaryToMap(),"" + boundary));
                else if(consoleReq.getRequest().getMassageBodyKind() == 1) {
                    builder = builder.POST(HttpRequest.BodyPublishers.ofString(consoleReq.getRequest().getBodyText()));
                }
                else
                    builder = builder.POST(HttpRequest.BodyPublishers.noBody());
            break;

            case PUT:
                if(consoleReq.getRequest().getMassageBodyKind() == 0 && consoleReq.getRequest().getBodyText().length()!=0)
                    builder = builder.PUT(publishFormData(bodyToMap(), "" + boundary));

                else if(consoleReq.getRequest().getFilePath() != null)
                    builder = builder.PUT(publishFormData(fileBinaryToMap(), "" + boundary));
                else if(consoleReq.getRequest().getMassageBodyKind() == 1) {
                    builder = builder.PUT(HttpRequest.BodyPublishers.ofString(consoleReq.getRequest().getBodyText()));
                }

                else
                    builder = builder.PUT(HttpRequest.BodyPublishers.noBody());
            break;

            case DELETE:
                builder = builder.DELETE();
                break;

            case PATCH:
                if(consoleReq.getRequest().getMassageBodyKind() == 0 && consoleReq.getRequest().getBodyText().length()!=0)
                    builder = builder.method("PATCH",publishFormData(bodyToMap(), "" + boundary));

                else if(consoleReq.getRequest().getFilePath() != null)
                    builder = builder.method("PATCH",publishFormData(fileBinaryToMap(), "" + boundary));

                else if(consoleReq.getRequest().getMassageBodyKind() == 1) {
                    builder = builder.method("PATCH",HttpRequest.BodyPublishers.ofString(consoleReq.getRequest().getBodyText()));
                }
                else
                    builder = builder.method("PATCH",HttpRequest.BodyPublishers.noBody());
        }
        return builder.build();
    }


    /**
     * make a publisher for form data depends on data and boundary.
     * uses "UTF_8".
     * for sending a file , the key part of header must contain String of "file" or "File".
     * @param data this map is like headers .
     * @param boundary boundary to make the form data
     * @return publisher for form data.
     * @throws IOException  if the file which is attached doesnt exist.
     */
    private static HttpRequest.BodyPublisher publishFormData(Map<String, String> data, String boundary) throws IOException {
        var byteArrays = new ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
                .getBytes(StandardCharsets.UTF_8);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            byteArrays.add(separator);

            if (entry.getKey().contains("file") || entry.getKey().contains("File")) {
                String path = entry.getValue();
                String contentType = Files.probeContentType(Paths.get(path));
                byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + Paths.get(path).getFileName()
                        + "\"\r\nContent-Type: " + contentType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                byteArrays.add(Files.readAllBytes(Paths.get(path)));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            } else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
                        .getBytes(StandardCharsets.UTF_8));
            }
        }
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }


    /**
     * makes a map depends depends on body text that is in form of "name1=value1&name2=value2"
     * @return hash map built on body of request.
     */
    private HashMap<String,String> bodyToMap()
    {
        HashMap<String,String> finalBody = new HashMap<>();
        String rawBody = consoleReq.getRequest().getBodyText();
        String[] semiRawBody = rawBody.split("&");
        for(String string : semiRawBody)
        {
            String[] tempSplit = string.split("=");
            if(tempSplit.length == 2)
            finalBody.put(tempSplit[0], tempSplit[1]);
        }
        return finalBody;
    }

    /**
     * make a hash map for binary files.
     * @return hash map built on attached file in request.
     */
    private HashMap<String,String> fileBinaryToMap (){
        HashMap<String,String> finalBody = new HashMap<>();
        finalBody.put("soloFile", consoleReq.getRequest().getFilePath());
        return finalBody;
    }


    /**
     * shows response.
     * @param response response to be showed.
     */
    private void showHttpResponse(HttpResponse<byte[]> response) {
        String string = new String(response.body());
        System.out.println("status code : " + response.statusCode() + " " + statusCodeMessage(response.statusCode()));
        System.out.println();
        if (consoleReq.isHeadersVis()) {
            System.out.println("headers:  ");
            response.headers().map().entrySet().forEach(System.out::println);
            System.out.println();
        }
        System.out.println("response : ["+ response.body().length/1000f +" KB]\n" + string);
        System.out.println();
    }




    /**
     * this class is for saving the response bodies
     */
    private class ResponseWriter implements Runnable {
        private byte[] bodyString;
        private String fileName;

        public ResponseWriter(byte[] bodyString, String fileName) {
            this.bodyString = bodyString;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            ConsoleFIleHandler.saveResponseBody(bodyString, fileName);
        }
    }


    /**
     * give the proper message of status code.
     * @param statusCode status code of http response.
     * @return message of status code.
     */
    public static String statusCodeMessage (int statusCode)
    {
        String[] codes = new String[]{
                "100 ;Continue",
                "101 ;Switching Protocols",
                "102 ;Processing",
                "200 ;OK",
                "201 ;Created",
                "202 ;Accepted",
                "203 ;Non-authoritative Information",
                "204 ;No Content",
                "205 ;Reset Content",
                "206 ;Partial Content",
                "207 ;Multi-Status",
                "208 ;Already Reported",
                "226 ;IM Used",
                "300 ;Multiple Choices",
                "301 ;Moved Permanently",
                "302 ;Found",
                "303 ;See Other",
                "304 ;Not Modified",
                "305 ;Use Proxy",
                "307 ;Temporary Redirect",
                "308 ;Permanent Redirect",
                "400 ;Bad Request",
                "401 ;Unauthorized",
                "402 ;Payment Required",
                "403 ;Forbidden",
                "404 ;Not Found",
                "405 ;Method Not Allowed",
                "406 ;Not Acceptable",
                "407 ;Proxy Authentication Required",
                "408 ;Request Timeout",
                "409 ;Conflict",
                "410 ;Gone",
                "411 ;Length Required",
                "412 ;Precondition Failed",
                "413 ;Payload Too Large",
                "414 ;Request-URI Too Long",
                "415 ;Unsupported Media Type",
                "416 ;Requested Range Not Satisfiable",
                "417 ;Expectation Failed",
                "418 ;I'm a teapot",
                "421 ;Misdirected Request",
                "422 ;Unprocessable Entity",
                "423 ;Locked",
                "424 ;Failed Dependency",
                "426 ;Upgrade Required",
                "428 ;Precondition Required",
                "429 ;Too Many Requests",
                "431 ;Request Header Fields Too Large",
                "444 ;Connection Closed Without Response",
                "451 ;Unavailable For Legal Reasons",
                "499 ;Client Closed Request",
                "500 ;Internal Server Error",
                "501 ;Not Implemented",
                "502 ;Bad Gateway",
                "503 ;Service Unavailable",
                "504 ;Gateway Timeout",
                "505 ;HTTP Version Not Supported",
                "506 ;Variant Also Negotiates",
                "507 ;Insufficient Storage",
                "508 ;Loop Detected",
                "510 ;Not Extended",
                "511 ;Network Authentication Required",
                "599 ;Network Connect Timeout Error"
        };
        for(String code : codes)
        {
            if(code.startsWith("" + statusCode))
                return code.substring(code.indexOf(";") + 1 );
        }
        return "unknown";
    }

}
