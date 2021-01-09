package FileHandler;
import Consol.ConsoleReq;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *Handel all actions about saving and loading request, response and ...
 */
public class ConsoleFIleHandler {
    public static final String reqFileDirection = "././consoleReqList/";
    public static final String responseFilesDirect = "././Responses/";
    private static final String divider = "   :&:   ";


    /**
     * save response body in folder defend by responseFilesDirect.
     * @param body body to be saved.
     * @param nameOfFile name of direction of file to be saved.
     */
    public static void saveResponseBody (byte[] body,String nameOfFile)
    {
        new File(responseFilesDirect).mkdir();
        try (FileOutputStream outputStream = new FileOutputStream( responseFilesDirect + nameOfFile);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream))
        {
            bufferedOutputStream.write(body);
            bufferedOutputStream.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * save a request in folder defined by reqFileDirection.
     * @param args args that defines this request.
     */
    public static void saveReq(String[] args)
    {
        String nameList = ConsoleReq.findValue(args,"-S" , "--save");
        try (FileWriter writer = new FileWriter( reqFileDirection +nameList +".txt",true);){
            writer.write((argsToStringSave(args)+ "\n"));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * make a String to be saved from the args that defines request.
     * @param args args that defines this request.
     * @return string that is able to be read.
     */
    private static String argsToStringSave(String[] args)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for(int index=0 ; index<args.length ; index++)
        {
            switch (args[index])
            {
                case "-i":
                    stringBuilder.append("show Headers:").append(divider);
                    break;

                case "--url":
                    stringBuilder.append("uri : ").append(args[index + 1]).append(divider);
                    index++;
                    break;

                case "-M":
                case "--method":
                    stringBuilder.append("method : ").append(args[index + 1]).append(divider);
                    index++;
                    break;

                case "-H":
                case "--headers":
                    stringBuilder.append("headers : ").append(args[index + 1]).append(divider);
                    index++;
                    break;

                case "-Q":
                case "--query":
                    stringBuilder.append("queries : ").append(args[index + 1]).append(divider);
                    index++;
                    break;

                case "-f":
                    stringBuilder.append("follow redirect:").append(divider);
                    break;

                case "-O":
                case "--output":
                    stringBuilder.append("save response in :").append(args[index + 1]).append(divider);
                    index++;
                    break;

                case "-d":
                case "--data":
                    stringBuilder.append("body message [form data]: ").append(args[index + 1]).append(divider);
                    index++;
                    break;

                case "-j":
                case "--json":
                    stringBuilder.append("body message [json]: ").append(args[index + 1]).append(divider);

                    index++;
                    break;

                case "--upload":
                    stringBuilder.append("file upload [binary upload]: ").append(args[index + 1]).append(divider);
                    index++;
                    break;

                case "--proxy":
                    stringBuilder.append("proxy :" );
                    if(args[index + 1].equals("--ip"))
                        stringBuilder.append(" server Ip := ").append(args[index + 2]).append(";      port ;= ").append(args[index + 4]);
                    else
                        stringBuilder.append(" server Ip := ").append(args[index + 4]).append("; port ;= ").append(args[index + 2]);
                    index += 4;
                    stringBuilder.append(divider);

            }
        }
        return stringBuilder.toString().substring(0,stringBuilder.length() - divider.length());
    }


    /**
     * convert string that was saved into args that defines request.
     * @param listName name of list that this request belongs.
     * @param indexReq index of this request in list.
     * @return  args that defines this request.
     */
    public static String[] argsOfSavedReq (String listName , int indexReq)
    {
        String[] args ;
        ArrayList<String> argsList = new ArrayList<>();
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(reqFileDirection + "/" + listName + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(stream);
        String currentReq ;
        int line = 1;
        while ((currentReq = scanner.nextLine()) != null)
        {
            if(line == indexReq)
                break;
            line++;
        }
        if (line != indexReq)
        {
            System.out.println("not valid index for req. requests' count : " + line);
            return null;
        }
        args = currentReq.split(divider);

        for (String arg : args) {
            switch (arg.substring(0, arg.indexOf(":")).trim()) {
                case "show Headers":
                    argsList.add("-i");
                    break;

                case "uri":
                    argsList.add("--url");
                    argsList.add(arg.substring(arg.indexOf(":") + 1).trim());
                    break;

                case "method":
                    argsList.add("-M");
                    argsList.add(arg.substring(arg.indexOf(":") + 1).trim());
                    break;

                case "headers":
                    argsList.add("-H");
                    argsList.add(arg.substring(arg.indexOf(":") + 1).trim());
                    break;

                case "queries":
                    argsList.add("-Q");
                    argsList.add(arg.substring(arg.indexOf(":") + 1).trim());
                    break;

                case "follow redirect":
                    argsList.add("-f");
                    break;

                case "save response in":
                    argsList.add("-O");
                    argsList.add(arg.substring(arg.indexOf(":") + 1).trim());
                    break;

                case "body message [form data]":
                    argsList.add("-d");
                    argsList.add(arg.substring(arg.indexOf(":") + 1).trim());
                    break;

                case "body message [json]":
                    argsList.add("-j");
                    argsList.add(arg.substring(arg.indexOf(":") + 1).trim());
                    break;

                case "file upload [binary upload]":
                    argsList.add("--upload");
                    argsList.add(arg.substring(arg.indexOf(":") + 1).trim());
                    break;

                case "proxy":
                case "--proxy":
                    argsList.add("--proxy");
                    argsList.add("--ip");
                    argsList.add(arg.substring(arg.indexOf(":=") + 2,arg.indexOf(";")).trim());
                    argsList.add("--port");
                    argsList.add(arg.substring(arg.indexOf(";=") + 2).trim());
            }
        }
        return argsList.toArray(new String[argsList.size()]);
    }


    /**
     * make a new list ( make a new file in direction of reqFIleDirection )
     * @param name name of new list.
     */
    public static void makeNewList(String name)
    {
        new File(reqFileDirection).mkdir();
        try {
            new File(reqFileDirection + "/" + name + ".txt").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * show all lists that been made.
     */
    public static void listSavedRequests()
    {
       File[] files =  new File(reqFileDirection).listFiles();
        System.out.println("name of all saved lists:  [" + files.length+ "]");
       for(int i=1 ; i<= files.length ; i++)
           System.out.println(i + " . " + files[i-1].getName().substring(0,files[i-1].getName().lastIndexOf(".")));
    }

    /**
     * list all requests of this list.
     * @param listName name of list .
     */
    public static void listReqsOfList(String listName)
    {
        String path = reqFileDirection + "/" + listName + ".txt";
        try {
            Scanner scanner = new Scanner(new FileInputStream(path));
            String tempString ;
            int index = 1;
            System.out.println("reqs of list [" + listName + "] :");
            while (scanner.hasNextLine())
            {
                tempString = scanner.nextLine();
                System.out.println(index + " . " + tempString);
                index ++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("there is no list with this name.");
        }
    }

}
