package Controll;

import GUI.ResponsePanelGui;
import MainPart.Request;
import MainPart.TimeElapse;
import javax.swing.*;

/**
 * this class is for linking request info _ response of request _ with GUI.
 * its implemented to listen to request field for response , so whenever the response is changed,
 * this class starts changing GUI.
 */
public class ResponseController {

    ResponsePanelGui responsePanelGui;
    Request request;

    /**
     * simple constructor.
     * @param request request that this class is listening to.
     */
    public ResponseController(Request request)
    {
        this.request = request;
    }

    /**
     *set a request that this class would change the GUI whenever the response of it got changed.
     * @param request request that this class is listening to.
     */
    public void setRequest(Request request) {
        request.setResponseController(this);
        this.request = request;
    }


    /**
     * would build gui depends on new response of request.
     * must be triggered when a new response is set and is not null.
     */
    public void notifyChangedResponse ()
    {
        new SwingWorker<Void, Object>() {
            @Override
            protected Void doInBackground() throws Exception {
                responsePanelGui.rebuild();
                responsePanelGui.updateUI();
                TimeElapse.end();
                request.setTimeElapsed(TimeElapse.getElapse());
                responsePanelGui.rebuildTopPart();
                return null;
            }
        }.execute();
    }

    /**
     * set response panel for this class.
     * @param responsePanelGui response panel to be set/
     */
    public void setResponsePanelGui(ResponsePanelGui responsePanelGui) {
        this.responsePanelGui = responsePanelGui;
    }
}
