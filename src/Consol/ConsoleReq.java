package Consol;
import FileHandler.ConsoleFIleHandler;
import MainPart.Request;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * this class make a new kind of request that as addition of nesesiry info of normal requestss , has som info about
 * settings like "showing response headers, follow redirect and ..."
 */
public class ConsoleReq implements Serializable {
    private Request request;
    private Boolean followRedirect;
    private Boolean headersAreVis;
    private Boolean saveReq;
    private String bodyResDirect;


    /**
     * make console request depends on arg .
     * @param args defines aspects of this request.
     * @throws Exception if arg is not valid , for example if after "--save" not mention the direction.
     */
    public ConsoleReq(String[] args) throws Exception {

        request = new Request("name", null);
        followRedirect = false;
        headersAreVis = false;
        saveReq = false;

            for (int index = 0; index < args.length; index++) {
                switch (args[index]) {
                    case "-i": // show headers.
                        headersAreVis = true;
                        break;

                    case "--url":// set uri
                        urlSetter(args, index);
                        index++;
                        break;

                    case "--proxy":
                        setProxy(args,index);
                        index+=4;
                        break;

                    case "-Q":
                    case "--query":
                        headerQuerySetter(args, index,"query");
                        index++;
                        break;

                    case "-M":// set method
                    case "--method":
                        methodSetter(args, index);
                        index++;
                        break;

                    case "-H":// set headers
                    case "--headers":
                        headerQuerySetter(args, index,"header");
                        index++;
                        break;

                    case "-f": // follow redirect
                        followRedirect = true;
                        break;

                    case "-O":// save response body
                    case "--output":
                        bodyResDirect = "";
                        bodyResDirectSetter(args, index);
                        index++;
                        break;

                    case "-S":// save request
                    case "--save":
                        validPath(args,index);
                        saveReq = true;
                        index++;
                        break;

                    case "-d":// enter form data body
                    case "--data":
                        request.setMassageBodyKind(0);
                        bodySetter(args, index);
                        index++;
                        break;

                    case "-j":// enter json body
                    case "--json":
                        request.setMassageBodyKind(1);
                        bodySetter(args, index);
                        index++;
                        break;

                    case "--upload":// enter file to be uploaded
                        request.setMassageBodyKind(2);
                        uploadFileSetter(args, index);
                        index++;
                        break;

                    case "--auth":
                        request.setProxy( args[index + 1].split(":"));
                        break;


                    default:// not valid operand.
                            throw new Exception("no such a command as \"" + args[index] + "\" found. use --help to see the commands");
                }
            }

            if (saveReq)
                ConsoleFIleHandler.saveReq(args);
    }


    /**
     * make args that could be passed to Console HttpClient for sending request .
     * make the args depend on info of current request.
     * @return args to be send to Console HttpClient.
     */
    public String[] makeArgsOfConsoleReq() {

        ArrayList<String> args = new ArrayList<>();
        args.add("--url");
        args.add(request.getUrl());
        if(!request.getQueryString(request.HQF_TOStringArr("query")).equals(""))
        {
            args.add("-Q");
            args.add(request.getQueryString(request.HQF_TOStringArr("query")).substring(1));
        }
        args.add("-M");
        args.add(""+request.getKind());
        if(!request.getQueryString(request.HQF_TOStringArr("header")).equals("")) {
            args.add("-H");
            args.add(request.getHeaderString(request.HQF_TOStringArr("header")));
        }
        if(request.getMassageBodyKind() == 0 && request.HQF_TOStringArr("Form").length != 0)
        {
            args.add("-d");
            request.setBodyText( request.convertFormDataBodyToString( request.HQF_TOStringArr("Form")));
            args.add(request.getBodyText());
        }
        if(request.getMassageBodyKind() == 2)
        {
            if(request.getFilePath() != null )
                if(!request.getFilePath().equals("")) {
                    args.add("--upload");
                    args.add(request.getFilePath());
                }
        }
        if(request.getMassageBodyKind()== 1 )
        {
            args.add("-j");
            args.add(request.getBodyText());
        }
        if(followRedirect)
            args.add("-f");
        if(headersAreVis)
            args.add("-i");
        return args.toArray(new String[0]);
    }


    /**
     * checks if String in args[index + 1] is valid path or not.
     * @param args defines request info.
     * @param index index of current operator in args.
     * @throws Exception throw exception if args[index + 1] doesnt exist , is another operand or this path is invalid.
     */
    private void validPath (String[] args , int index) throws Exception {
        if (args.length == index + 1)
            throw new Exception("after " + args[index] + " must enter the list name");

        if (args[index + 1].startsWith("-"))
            throw new Exception("after " + args[index] + " must enter the list name");

        if(!new File(ConsoleFIleHandler.reqFileDirection + args[index+1] + ".txt").exists())
            throw new Exception("this list does not exist.");
    }

    /**
     * sets uri coming in args[index + 1] as uri of this request.
     * @param args defines request info.
     * @param index index of current operator in args.
     * @throws Exception throw exception if args[index + 1] doesnt exist of is another operand .
     */
    private void urlSetter(String[] args, int index) throws Exception {
        if (args.length == index + 1)
            throw new Exception("after " + args[index] + " must enter the url");

        if (args[index + 1].startsWith("-"))
            throw new Exception("after " + args[index] + " must enter the url");

        request.setUrl( (args[index + 1].startsWith("http://") || args[index + 1].startsWith("https://")?"":"http://") + args[index + 1]);
    }

    /**
     * sets method coming in args[index + 1] as method of this request.
     * @param args defines request info.
     * @param index index of current operator in args.
     * @throws Exception throw exception if args[index + 1] doesnt exist of is another operand .
     */
    private void methodSetter(String[] args, int index) throws Exception {
        if (args.length == index + 1)
            throw new Exception("after " + args[index] + " must enter the method kind.");

        if (args[index + 1].startsWith("-"))
            throw new Exception("after " + args[index] + " must enter the method kind.");

        switch (args[index + 1]) {
            case "GET":
                request.setKind(Request.Kind.GET);
                break;
            case "POST":
                request.setKind(Request.Kind.POST);
                break;
            case "PATCH":
                request.setKind(Request.Kind.PATCH);
                break;
            case "DELETE":
                request.setKind(Request.Kind.DELETE);
                break;
            case "PUT":
                request.setKind(Request.Kind.PUT);
                break;
        }
    }

    /**
     * sets headers coming in args[index + 1] as headers of this request.
     * @param args defines request info.
     * @param index index of current operator in args.
     * @param HorQ for headers use "header" for query use "query".
     * @throws Exception throw exception if args[index + 1] doesnt exist of is another operand .
     */
    private void headerQuerySetter(String[] args, int index,String HorQ) throws Exception {
        if (args.length == index + 1)
            throw new Exception("after " + args[index] + " must enter the "+HorQ+".");

        if (args[index + 1].startsWith("-"))
            throw new Exception("after " + args[index] + " must enter the "+HorQ+".");

        if(HorQ.equals("header")) {
            ArrayList<String[]> headers = headerQueryParser(args[index + 1],"header");
            request.setHeaders(headers);
        }
        else {
            ArrayList<String[]> queries = headerQueryParser(args[index + 1],"query");
            request.setQueries(queries);
        }
    }


    /**
     * splits string of headers to arraylist of headers used in request info.
     * @param headerQueryString headers in fom of string , like "key1=value1&key2=value2"
     * @param HQ for headers use "header" for query use "query".
     * @return headers in form of arraylist.
     * @throws Exception if headers string is not in valid form.
     */
       private ArrayList<String[]> headerQueryParser(String headerQueryString,String HQ) throws Exception {
        ArrayList<String[]> headersArray = new ArrayList<>();
        try {
            String[] headers = headerQueryString.split((HQ.equals("header")?";":"&"));
            for (String header :headers) {
                String[] headerParsed = header.split((HQ.equals("header")?":":"="));
                headersArray.add(new String[]{headerParsed[0],headerParsed[1],"true"});
            }
        } catch (Exception e) {
            throw new Exception("headers must be paired.");
        }
        return headersArray;
    }

    /**
     * @param args defines request info.
     * @param index index of current operator in args.
     * @throws IndexOutOfBoundsException if proxy server or port of server are not mentioned.
     */
    private void setProxy(String[] args, int index) throws IndexOutOfBoundsException
    {
       try {
           for(int i=index+1 ; i<index + 5 ; i++)
           {
               System.out.println(args[i]);
               if(args[i].equals("--ip") && !args[i+1].startsWith("-")) {
                   request.updateProxy(0, args[i + 1]);
                   i++;
               }
               else if(args[i].equals("--port"))
               {
                   request.updateProxy(1, args[i + 1]);
                   i++;
               }
               else
                   throw new IndexOutOfBoundsException("must mention proxy server and port.");
           }
       }catch (IndexOutOfBoundsException e)
       {
           throw new IndexOutOfBoundsException("must mention proxy server and port.");
       }
    }


    /**
     * sets body coming in args[index + 1] as body of this request.
     * @param args defines request info.
     * @param index index of current operator in args.
     * @throws Exception throw exception if args[index + 1] doesnt exist of is another operand .
     */
    private void bodySetter(String[] args, int index) throws Exception {
        if (args.length == index + 1)
            throw new Exception("after \"" + args[index] + "\" must enter the headers.");

        if (args[index + 1].startsWith("-"))
            throw new Exception("after \"" + args[index] + "\" must enter the headers.");

        request.setBodyText(args[index + 1]);
    }

    /**
     * sets direct of saving of response coming in args[index + 1] for this request.
     * @param args defines request info.
     * @param index index of current operator in args.
     * @throws Exception throw exception if args[index + 1] doesnt exist of is another operand .
     */
    private void bodyResDirectSetter(String[] args, int index) throws Exception {
        if (args.length == index + 1)
            throw new Exception("after \"" + args[index] + "\" must enter the direction of file to be saved.");

        if (args[index + 1].startsWith("-"))
            throw new Exception("after \"" + args[index] + "\" must enter the direction of file to be saved.");

        bodyResDirect = args[index + 1];
    }

    /**
     * sets file to be upload coming in args[index + 1] for  this request.
     * @param args defines request info.
     * @param index index of current operator in args.
     * @throws Exception throw exception if args[index + 1] doesnt exist of is another operand .
     */
    private void uploadFileSetter(String[] args, int index) throws Exception {
        if (args.length == index + 1)
            throw new Exception("after \"" + args[index] + "\" must enter the direction of file to be uploaded.");

        if (args[index + 1].startsWith("-"))
            throw new Exception("after \"" + args[index] + "\" must enter the direction of file to be uploaded.");

        try {
            request.setFilePath( args[index + 1]);
        } catch (NullPointerException e) {
            throw new Exception("no such a file.");
        }
    }

    /**
     * get request of this consoleReq
     * @return request of this consoleReq
     */
    public Request getRequest() {
        return request;
    }

    /**
     * demonstrate that should this request be Follow redirected or not.
     * @return true if should be follow redirected else false
     */
    public Boolean isFlowRedirect() {
        return followRedirect;
    }

    /**
     * demonstrate that should headers of response be showed or not.
     * @return true if should be showed  else false
     */
    public Boolean isHeadersVis() {
        return headersAreVis;
    }

    /**
     * get direction that response should be saved.
     * @return direction that response should be saved.
     */
    public String getBodyResDirect() {
        return bodyResDirect;
    }


    /**
     * returns next index value after given index of key in args.
     * if there is no index with value of keys of indexOutOfBound happen ; return null.
     * @param args args to be probed.
     * @param key first key to catch.
     * @param key2 second key to catch
     * @return value of args[ indexOf(key or key2) + 1].
     */
    public static String findValue(String[] args, String key, String key2)
    {
        try {
            for(int i=0 ; i<args.length ; i++)
                if(args[i].equals(key) || args[i].equals(key2))
                    return args[i+1];
        }catch (IndexOutOfBoundsException e)
        {
            return null;
        }
        return null;
    }


    /**
     * prints info of req like "uri , method, body, headers and booleans of [follow redirect and show headers]".
     */
    public void showReq()
    {
        System.out.println("\n_______ request info  _______");
        System.out.println("uri: " + request.getUrl());
        if(!request.getQueryString( request.HQF_TOStringArr("query")).equals("?"))
            System.out.println("Query : " + request.getQueryString( request.HQF_TOStringArr("query")));
        System.out.println("method : " + request.getKind());
        if(!request.getBodyText().equals(""))
            System.out.println("message body [" + (request.getMassageBodyKind() == 0?"form data]  :  ":"json]  :  ") + request.getBodyText());
        for(int index = 0; index< request.HQF_TOStringArr("header").length ; index+=2) {
            if (index == 0)
                System.out.println("headers : ");
            System.out.println("\"" + request.HQF_TOStringArr("header")[index] + "\" = " + request.HQF_TOStringArr("header")[index + 1]);
        }
        System.out.println("follow redirect :" + followRedirect + " |  show headers :" + headersAreVis );
        System.out.println("____________________________");
    }

}
