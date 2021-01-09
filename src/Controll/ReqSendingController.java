package Controll;

import Consol.ConsoleRun;
import Consol.Response;
import GUI.SettingInfo;
import MainPart.Request;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;



/**
 * this class is responsible for convert request to ConsoleRequest,
 * send request to target url and set the result in Request.
 */
public class ReqSendingController implements ActionListener {
    private Request request;

    /**
     * simple constructor.
     * @param request
     */
    public ReqSendingController(Request request)
    {this.request = request;}

    /**
     * Invoked when an action occurs.
     * invoked when "send" batten got clicked.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        SwingWorker<String,Object> swingWorker = new SwingWorker<String, Object>() {
            @Override
            protected String doInBackground() {
                try {
                    String[] args = makeArgReq();
                    Response response;
                    response = ConsoleRun.startSending(args);
                    request.setResponse(response);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return "finished";
            }
            protected void done() {
                System.out.println("done");
            }
        };
        swingWorker.execute();
    }


    /**
     * make args that is main source of generating ConsoleReq.
     * @return args with info of GUI panels and current request.
     */
    private String[] makeArgReq ()
    {
        ArrayList<String> args = new ArrayList<>();
        args.add("--url");
        args.add(request.getUrl());
        if(!request.getQueryString(request.HQF_TOStringArr("query")).equals(""))
        {
            args.add("-Q");
            args.add(request.getQueryString(request.HQF_TOStringArr("query")).substring(1));
        }
        if(request.getProxy()[0].length()!=0 && request.getProxy()[1].length()!=0 && request.getProxy()[2].equals("true")){
            args.add("--proxy");
            args.add("--ip");
            args.add(request.getProxy()[0]);
            args.add("--port");
            args.add(request.getProxy()[1]);
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
        if((boolean) SettingInfo.flowReDirect.getInfo())
            args.add("-f");
        args.add("-i");
        for (String s : args)
            System.out.println(s);
        return args.toArray(new String[0]);
    }

    /**
     * set request  that must be sent.
     * @param request request  that must be sent
     */
    public void setRequest(Request request) {
        this.request = request;
    }
}

