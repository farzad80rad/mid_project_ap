package Consol;

import FileHandler.ConsoleFIleHandler;

import java.io.*;
import java.net.Socket;

/**
 * this is about a program built in console as http client.
 * in this app you can make requests, send them, receive response and ... .
 * supported methods " GET , POST , PUT , DELETE , PATCH "
 * able user to save response , send multi requests , send saved requests and ...
 */
public class ConsoleRun {
    public static Response startSending (String[] args)
    {
        try {
            ConsoleReq consoleReq = new ConsoleReq(args);
            consoleReq.showReq();
            return new ReqSender(consoleReq).sendReq();
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {

        args = new String[]{"--send","list[list]","--ip","localhost","--port","2500"};
        //args = new String[]{"--url","google.com","-i","--save","list[list]"};
       // args = new String[]{"list","list2"};
        if (args.length == 0)
        {
            System.out.println("no input");
            return;
        }

        switch (args[0]) {
            case "--send":
                try {
                    String server = null;
                    int port = 0;
                    String listName  = args[1];
                    for (int i = 2; i < 5; i++) {
                        if (args[i].equals("--ip") && !args[i + 1].startsWith("-")) {
                            server = args[i + 1];
                            i++;
                        } else if (args[i].equals("--port")) {
                            port = Integer.parseInt(args[i + 1]);
                            i++;
                        } else {
                            System.out.println("must mention proxy server and port.");
                            return;
                        }
                    }
                    Socket socket = new Socket(server,port);
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(2);
                    outputStream.writeObject(new File(ConsoleFIleHandler.reqFileDirection + listName + ".txt"));

                }catch (IndexOutOfBoundsException e)
                {
                    System.out.println("must mention proxy server and port.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "fire":
                if(args.length>2)
                {
                    String listName = args[1];
                    for(int i=0 ; i<args.length - 2; i++)
                    {
                        try {

                            ConsoleReq tempReq = new ConsoleReq(ConsoleFIleHandler.argsOfSavedReq(listName, Integer.parseInt(args[i + 2])));
                            tempReq.showReq();
                            System.out.println();
                            new ReqSender(tempReq).sendReq();
                        }catch (Exception e)
                        {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                else
                    System.out.println("must enter name of list and index of requests");
                break;
            case "--help":
            case "-h":
                showHelp();
                break;
            case "create":
                if (args.length == 1)
                    System.out.println("must enter a name for new list");
                else
                    ConsoleFIleHandler.makeNewList(args[1]);

                break;
            case "list":
                if (args.length == 1)
                    ConsoleFIleHandler.listSavedRequests();
                else if(args.length == 2){
                    ConsoleFIleHandler.listReqsOfList(args[1]);
                }
                else
                    System.out.println("should mention name of list to show its requests");
                break;
            default:
                try {
                    ConsoleReq consoleReq = new ConsoleReq(args);
                    consoleReq.showReq();
                    System.out.println();
                    new ReqSender(consoleReq).sendReq();
                    break;
                }catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
        }


    }

    private static void showHelp()
    {
        System.out.println("-H <e> , --header <e>  : set headers . headers must be paired. e.g [-H \"name:value;name2:value2\"]");
        System.out.println("-Q <e> , --query <e>  : set queries . queries must be paired. e.g [-H \"name:value;name2:value2\"]");
        System.out.println("         --url    <e>  : set uri address. e.g [--url www.google.com]");
        System.out.println("-M <e> , --method <e>  : set method of request. e.g [-M POST]");
        System.out.println("-i                     : show headers of response.");
        System.out.println("-f                     : actives follow redirect. ");
        System.out.println("-O <o> , --output <o>  : save response body in file. e.g [ -O  C:/folder/test.txt]\n" +
                           "                         NOTE: if file not mentioned,will make a default name");
        System.out.println("-S     , --save        : save the request.");
        System.out.println("-d <e> , --data   <e>  : set body massage in form data. e.g [-d \"name=value&name2=value2\"]");
        System.out.println("-j <e> , --json   <e>  : set body massage in json. e.g [-j \"{firstName:nameF,lastName:nameL}\"]");
        System.out.println("       , --upload <e>  : set a file to be upload. e.g [--upload C:/file/test.txt]");
        System.out.println("       ,  list    <e>  : shows all request of folder e . e.g [list myRequests]");
        System.out.println("       ,  fire <e...>  : execute all requests mentioned of folder. e.g [fire myRequests 2 3 1]");
        System.out.println("       ,  create  <e>  : create a new request folder. e.g [create myRequests]");
    }
}

