package MainPart;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


import Controll.MainControler;
import FileHandler.*;
import HostServer.GuiServer;

/**
 * this is class hold all workshops and is the main source to define the current request .
 * till now the responsibility of this class is limited to this, but in the next version it's likely to
 * become something with more responsibilities.
 */
public class Application implements Serializable {
    
    private ArrayList<Workspace> workspaces;
    private transient Workspace workspace;
    private transient MainControler mainController;
    private transient GuiServer guiServer ;

    /**
     * construct the app by  saved info in files, if the file is empty or their is no such a file, would
     * make a workspace by name of "insomnia".  active workspace is always set as the first workspace.
     */
    public Application()
    {
        workspaces = new ArrayList<>();
        // restore the setting has been saved.
        try {
            FileHandler.readSettings();
        }catch (IOException e){
            FileHandler.defaultSetting();
        }

        // restore the app main info like req and folders.
        try
        {
            addWorkspacesOfNewApp(FileHandler.readApp());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // if there were no workspace.
        if (workspaces.size() == 0)
            workspaces.add(new Workspace("insomnia",this));

        workspace = workspaces.get(0);
        guiServer = new GuiServer(5000,this);
        new Thread( guiServer ).start();
    }


    /**
     *checks if a workspace exist in arrays of workspaces with this name.
     * @param name name of workspace to ba checked.
     * @return true if exist else false.
     */
    private boolean workspaceExistWhitName(String name)
    {
        for (Workspace workspace : workspaces)
            if(workspace.getName().equals(name))
                return true;
        return false;
    }


    /**
     * add a workspaces of this new application to workspaces of current application.
     * @param savedApp application that its content would be added to current application.
     */
    public void addWorkspacesOfNewApp(Application savedApp)
    {

        if(savedApp != null)
        {
            for(Workspace workspace1 : savedApp.getWorkspaces())
            {
                String newWorkspaceName = workspace1.getName();
                while (workspaceExistWhitName(newWorkspaceName))
                    newWorkspaceName = "." + newWorkspaceName;

                Workspace newWorkspace = new Workspace(newWorkspaceName,this);
                workspaces.add(newWorkspace);
                newWorkspace.getFolders().remove(0);
                for(Folder folder : workspace1.getFolders())
                {
                    Folder newFolder = new Folder(folder.getName());
                    newWorkspace.addNewFolder(newFolder);
                    for(Request request : folder.getRequests())
                    {
                        Request newReq = new Request(request.getName(),newFolder);
                        newFolder.addNewRequest(newReq);
                        newReq.setBodyText(request.getBodyText());
                        newReq.setKind(request.getKind());
                        newReq.setMassageBodyKind(request.getMassageBodyKind());
                        newReq.setHeaders(request.getHeaders());
                        newReq.setProxy(request.getProxy());
                        newReq.setQueries(request.getQueries());
                        newReq.setFormDataBody(request.getFormDataBody());
                        newReq.setUrl(request.getUrl());
                        newReq.setFilePath(request.getFilePath());
                        newReq.setTimeElapsed(request.getTimeElapsed());
                        newReq.setResponse(request.getResponse());
                        newReq.setResponseKind(request.getResponseKind());
                    }
                }
            }
        }

    }


    /**
     * add a new workspace to collection of available workspace
     * @param name name of new workspace
     */
    public void addNewWorkspace(String name)
    {
        Workspace workspace = new Workspace(name,this);
        workspaces.add(workspace);
        this.workspace = workspace;
        mainController.setWorkspace(workspace);
    }

    /**
     * set current workspace for Application and set the mainController for it.
     * @param workspace workspace to be replaced as current workspace of application.
     */
    public void setCurrentWorkspace(Workspace workspace) {
        this.workspace = workspace;
        workspace.setMainController(mainController);
        mainController.setWorkspace(workspace);
    }

    /**
     * get all workspace.
     * @return all workspaces.
     */
    public ArrayList<Workspace> getWorkspaces() {
        return workspaces;
    }

    /**
     * get current workspace.
     * @return current workspace.
     */
    public Workspace getCurrentWorkspace() {
        return workspace;
    }

    /**
     * get mainController of this application.
     * @return mainController of this app.
     */
    public MainControler getMainController() {
        return mainController;
    }

    /**
     * set mainController of this app.
     * @param mainController main controller to be set for this app.
     */
    public void setMainController(MainControler mainController) {
        this.mainController = mainController;
    }

    public GuiServer getGuiServer() {
        return guiServer;
    }
}
