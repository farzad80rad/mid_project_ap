package MainPart;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * defines folder which contains requests.
 */
public class Folder implements Serializable {
    private ArrayList<Request> requests;
    private String name;
    public static transient final String startingFolder = "     \uD83D\uDCC2 ";
    public static transient final  String startingRequest = ".";

    /**
     * simple constructor.
     * @param name name of this folder.
     */
    public Folder(String name)
    {
        this.name = name;
        requests = new ArrayList<>();
    }

    /**
     * get name of this folder.
     * @return name of this folder
     */
    public String getName() {
        return name;
    }

    /**
     * get all requests of this folder.
     * @return all requests.
     */
    public ArrayList<Request> getRequests() {
        return requests;
    }

    /**
     * add a new request.
     * @param name name of new request.
     */
    public void addNewRequest(String name)
    {
        requests.add(new Request(name,this));
    }

    /**
     * add new Req to Reqs of this folder .
     * @param req req to be added.
     */
    public void addNewRequest(Request req)
    {
        requests.add(req);
    }

}
