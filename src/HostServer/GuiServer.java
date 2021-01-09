package HostServer;

import Consol.ConsoleRun;
import Consol.Response;
import GUI.HistoryPanGui;
import MainPart.Application;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * this class is for receiving request from another same app , save them or run the request and return the response of it.
 */
public class GuiServer implements Runnable{
    private Application application ;
    private int port;

    /**
     * simple constructor.
     * @param port port of this system.
     * @param application application that this server does belong to.
     */
    public GuiServer(int port , Application application) {
        this.application = application;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ExecutorService executorService = Executors.newCachedThreadPool();
            while (true)
            {
                try {
                    Socket socket = serverSocket.accept();
                    executorService.execute(new ExecuteOperation(socket));
                }catch (Exception e ){
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * execute the received operation.
     * for send and return response of request ; write Integer object with value of 1 at beginning of outPutStream.
     * for sharing workspaces ; write Integer object with value of 2 at beginning of outPutStream.
     */
    private class ExecuteOperation implements Runnable{

        Socket socket;

        @Override
        public void run() {

            try(ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())
            ){
                if((Integer) inputStream.readObject() == 1) {
                    if(JOptionPane.showConfirmDialog(null,"System " + socket.getRemoteSocketAddress() + " wants to send a request via your System.\n Do you accept? ") == JOptionPane.YES_OPTION)
                        {
                            String[] args = (String[]) inputStream.readObject();
                            Response response = ConsoleRun.startSending(args);
                            outputStream.writeObject(response);
                        }
                    else {
                        Response response = new Response();
                        response.setBodyRes("request denied by host system".getBytes());
                        outputStream.writeObject(response);
                    }
                }
                else {
                    if(JOptionPane.showConfirmDialog(null,"System " + socket.getRemoteSocketAddress() + " wants to send new Workspace .\n Do you accept? ") == JOptionPane.YES_OPTION) {
                        Application newApplication = (Application) inputStream.readObject();
                        application.addWorkspacesOfNewApp(newApplication);
                        HistoryPanGui historyPanGui = application.getMainController().getMainDisplayGui().getLeftPan();
                        historyPanGui.rebuild();
                        historyPanGui.updateUI();
                        historyPanGui.showNewWorkspaceReceivedPan();
                    }

                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public ExecuteOperation(Socket socket)
        {
            this.socket = socket;
        }
    }

    public int getPort() {
        return port;
    }
}
