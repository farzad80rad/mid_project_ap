package MainPart;
import java.io.Serializable;
import java.util.ArrayList;
import Controll.MainControler;

/**
 * this class at this step acts as a information holder.
 * stores folders and current request.
 */
public class Workspace implements Serializable {
    private ArrayList<Folder> folders;
    private String name ;
    private transient MainControler mainController;
    private transient Application application;
    private Request currentReq;

    /**
     * simple constructor. by default an empty folder with name of "Main" is build.
     * @param name name of this workspace
     * @param application application of this program,
     */
    public Workspace(String name, Application application)
    {
        this.application = application;
        this.name = name;
        folders = new ArrayList<>();
        folders.add(new Folder(Folder.startingFolder + "Main"));
        currentReq = null;
    }

    /**
     * add new folder to folders of this workspace.
     * @param name name of new folder.
     */
    public void addNewFolder(String name)
    {
        folders.add(new Folder(name));
    }

    /**
     * add new folder to folders of this workspace.
     * @param folder  new folder.
     */
    public void addNewFolder(Folder folder)
    {
        folders.add(folder);
    }

    /**
     * add new request to folder of this workspace.
     * @param nameRequest name of new request.
     * @param nameFolder name of folder that request is added to.
     */
    public void addNewRequest(String nameRequest, String nameFolder)
    {
        Folder thisFolder = null;
        for(Folder folder : folders) {
            thisFolder = folder;
            if (folder.getName().equals(nameFolder))
                break;
        }
        if (thisFolder == null)
            return;

        thisFolder.addNewRequest(nameRequest);
    }

    /**
     * get all folders of this workspace.
     * @return all folders of this workspace.
     */
    public ArrayList<Folder> getFolders() {
        return folders;
    }

    /**
     * get application of this workspace.
     * @return application of this workspace.
     */
    public Application getApplication() {
        return application;
    }

    /**
     * get name of this workspace.
     * @return name of this workspace.
     */
    public String getName() {
        return name;
    }

    /**
     * set main controller for this workspace.
     * @param mainController main controller of app.
     */
    public void setMainController(MainControler mainController) {
        this.mainController = mainController;
    }


    /**
     * get current request.
     * @return current request.
     */
    public Request getCurrentReq() {
        return currentReq;
    }

    /**
     * set current request.
     * @param currentReq request to be set as current req.
     */
    public void setCurrentReq(Request currentReq) {
        this.currentReq = currentReq;
    }

    /**
     * remove the request from the requests of folder.
     * @param request request name to be deleted.
     */
    public void removeReq( Request request)
    {
        for(Folder folder : folders)
            folder.getRequests().remove(request);
    }

    /**
     * remove a folder from folders.
     * @param folder folder to be deleted.
     */
    public void removeFolder( Folder folder)
    {
        folders.remove(folder);
    }


    /**
     * find a request by name and parent Folder name.
     * @param name name of request to be found.
     * @return request found by name.
     */
    public Request findReq(String name, String parentFolName)
    {
        for(Folder folder : folders)
            if(parentFolName.equals(folder.getName()))
                for(Request request : folder.getRequests())
                    if (request.getName().equals(name))
                        return request;

        return null;
    }

    /**
     * find a folder by name
     * @param name name of folder to be found
     * @return folder found by name.
     */
    public Folder findFolder(String name)
    {
        for(Folder folder : folders)
                if (folder.getName().equals(name))
                    return folder;

        return null;
    }

    /**
     * get name of this workspace.
     * @param name name of this workspace.
     */
    public void setName(String name) {
        this.name = name;
    }
}

