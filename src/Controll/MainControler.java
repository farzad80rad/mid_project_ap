package Controll;

import GUI.MainDisplayGui;
import MainPart.*;

/**
 * this class control the main panel of application. thing like changing the workspace and request are
 * done throw this class.
 */
public class MainControler {
    private MainDisplayGui mainDisplayGui;
    private HistoryController historyController;
    private BodyController bodyController;
    private Application application;
    private ReqSendingController reqSendingController;
    private ResponseController responseController;




    /**
     * make a new main controller and set the application as the controlled thing.
     * also add itself to the application as controller.
     * @param application application to be controlled.
     */
    public MainControler(Application application)
    {

        historyController = new HistoryController(application.getCurrentWorkspace());
        bodyController = new BodyController( application.getCurrentWorkspace() );
        reqSendingController = new ReqSendingController(application.getCurrentWorkspace().getCurrentReq());
        responseController = new ResponseController(application.getCurrentWorkspace().getCurrentReq());
        application.setMainController(this);
        this.application = application;
        mainDisplayGui = new MainDisplayGui(this);
        mainDisplayGui.setVisible(true);


    }


    /**
     * set work space as current workspace.
     * @param workspace workspace to be set.
     */
   public void setWorkspace(Workspace workspace)
    {
        bodyController.setCurrentWorkspace(workspace);
        historyController.setWorkspace(workspace);
        if(workspace.getCurrentReq() != null) {
            reqSendingController.setRequest(workspace.getCurrentReq());
            responseController.setRequest(workspace.getCurrentReq());
        }
        mainDisplayGui.rebuild();
    }

    /**
     * rebuild the body panel and response panel depends on new req.
     * @param req request to be set.
     */
    public void changeReq(Request req){
        application.getCurrentWorkspace().setCurrentReq(req);
        mainDisplayGui.getCenterPan().reBuild();
        mainDisplayGui.getRightPan().rebuildWithSwingWorker();
        reqSendingController.setRequest(req);
        responseController.setRequest(req);
    }

    /**
     * get application of this controller.
     * @return application of this controller
     */
    public Application getApplication() {
        return application;
    }


    /**
     * set the main display for this controller.
     * @param mainDisplayGui main display to be set.
     */
    public void setMainDisplayGui(MainDisplayGui mainDisplayGui) {
        this.mainDisplayGui = mainDisplayGui;
    }

    /**
     * get the body controller.
     * @return body controller.
     */
    public BodyController getBodyController() {
        return bodyController;
    }

    /**
     * get the history controller.
     * @return history controller.
     */
    public HistoryController getHistoryController() {
        return historyController;
    }


    /**
     * get the main display.
     * @return main display.
     */
    public MainDisplayGui getMainDisplayGui() {
        return mainDisplayGui;
    }

    /**
     * get reqSendingController.
     * @return reqSendingController
     */
    public ReqSendingController getReqSendingController() {
        return reqSendingController;
    }

    /**
     * get response Controller.
     * @return response controller.
     */
    public ResponseController getResponseController() {
        return responseController;
    }

}
