package HostServer;

import Consol.ConsoleRun;
import Consol.Response;
import FileHandler.ConsoleFIleHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * a server that is able to operate the received request from another system and give back the response,
 * and also is able to get file from another system.
 */
public class ConsoleServer {
    public static void main(String[] args)
    {
        System.out.println("enter server");
        try {
            ServerSocket serverSocket = new ServerSocket(2500);
            ExecutorService executorService = Executors.newCachedThreadPool();
            while (true)
            {
                try {
                    Socket socket = serverSocket.accept();
                    executorService.execute(new operateReq(socket));
                }catch (Exception e ){
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * get and send response of a request or get files.
     * for send and get response of request ; wright Integer Object by value of 1 at beginning of outPutStream.
     * for sen file ; wright Integer Object by value of 2 at beginning of outPutStream.
     */
    private static class operateReq implements Runnable{

        Socket socket;

        @Override
        public void run() {

            try(ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ){
                if((Integer) inputStream.readObject() == 1) {
                    String[] args = (String[]) inputStream.readObject();
                    Response response = ConsoleRun.startSending(args);
                    outputStream.writeObject(response);
                }
                else
                {
                   File file = (File) inputStream.readObject();
                    System.out.println("content: " + new String( new FileInputStream(file).readAllBytes()));
                   new File(file.getParentFile().getAbsolutePath()).mkdir();
                   File file1  = new File(ConsoleFIleHandler.reqFileDirection  + file.getName().substring(0,file.getName().lastIndexOf(".")) + ".txt");
                   file1.createNewFile();
                   FileOutputStream fileOutputStream = new FileOutputStream(file1);
                   fileOutputStream.write(new FileInputStream(file).readAllBytes());
                   fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public operateReq(Socket socket)
        {
            this.socket = socket;
        }
    }
}

