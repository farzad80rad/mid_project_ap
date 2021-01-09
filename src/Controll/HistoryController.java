package Controll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import GUI.HistoryPanGui;
import MainPart.*;
/**
 * this class links the history panel  with the  information of application.
 * when a thing in history would cause changes in request or folders of  application like adding a new folder\req
 *  or removing them ... the process must be done throw this class.
 */
public class HistoryController implements ItemListener  , ActionListener {


    HistoryPanGui historyPan;
    private Workspace workspace;


    /**
     * simple constructor.
     * @param currentWorkshop current workshop of app.
     */
    public HistoryController(Workspace currentWorkshop )
    {
        workspace = currentWorkshop;
    }

    /**
     * Invoked when an action occurs.
     * this method is for adding or removing request\folder and opening a request.
     * @param e action event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton jButton = (JButton)e.getSource();
        // add new folder.
        if(jButton.getText().equals(HistoryPanGui.addNewFolText)) {
            String name =  JOptionPane.showInputDialog("enter name of new folder");
            if(name != (null)) {
                workspace.addNewFolder(Folder.startingFolder + name);
                newFolderInit(Folder.startingFolder + name);
            }
        }

        // add new request.
        else if(jButton.getText().equals(HistoryPanGui.addNewReqText)) {
            String name =  JOptionPane.showInputDialog("enter name of new request");
            if(name != (null)) {
                workspace.addNewRequest(Folder.startingRequest + name,findFolderOfReq(jButton));
                newRequestInit(Folder.startingRequest + name,findParentFolName(jButton),findIndexAddReq(jButton));

            }
        }

        // delete a req or folder
        else if(jButton.getActionCommand().equals("Delete"))
        {
            int indexFolder = 0 ;
            for(Component component : historyPan.getFolderAndReqsPan().getComponents())
            {
                JPanel panel = (JPanel)component;
                if(panel.getComponentCount() >1 )
                    if(panel.getComponent(1) ==  jButton)
                    {
                        JButton folderOrReqBt = ((JButton)panel.getComponent(0));
                        // remove a request
                        if(folderOrReqBt.getText().startsWith(Folder.startingRequest)) {
                           if( JOptionPane.showConfirmDialog(workspace.getApplication().getMainController().getMainDisplayGui().getCenterPan(),
                                   "delete this request?") == JOptionPane.YES_OPTION ) {
                               workspace.removeReq(workspace.findReq(folderOrReqBt.getText(),findParentFolName(folderOrReqBt)));
                               historyPan.getFolderAndReqsPan().remove(panel);
                               historyPan.updateUI();
                           }
                        }
                        // remove a folder.
                        else if( JOptionPane.showConfirmDialog(workspace.getApplication().getMainController().getMainDisplayGui().getCenterPan(),
                                    "delete this folder?") == JOptionPane.YES_OPTION ) {
                            removeFolderGui(indexFolder);
                            workspace.removeFolder(workspace.findFolder(folderOrReqBt.getText()));
                        }

                    }
                indexFolder ++;
            }

        }

        // open a request.
        else if( !jButton.getText().startsWith(Folder.startingFolder)) {
            workspace.getApplication().getMainController().changeReq(workspace.findReq(jButton.getText(), findParentFolName(jButton)));
          //  System.out.println(findParentFolName(jButton));
            //System.out.println(workspace.findReq(jButton.getText(), findParentFolName(jButton)).getParentFol().getName());
        }

    }


    /**
     * find the parent folder of this req by searching in panel GUI.
     * @param reqBut req button of this req.
     * @return name of parent folder.
     */
    private String findParentFolName(JButton reqBut)
    {
        String nameFolder = null;
        for(Component component :historyPan.getFolderAndReqsPan().getComponents())
        {
            JButton button =   (JButton) ((JPanel)component).getComponent(0);
            if(button.getText().startsWith(Folder.startingFolder))
                nameFolder = button.getText();
            if(button == reqBut) {
                break;
            }
        }
        return nameFolder;
    }


    /**
     * remove a folder from the gui. this method would also remove the requests that belong to this folder.
     * @param indexFolder index folder in gui panel.
     */
    private void removeFolderGui(int indexFolder)
    {
        int count =0 ;
        for(Component component : historyPan.getFolderAndReqsPan().getComponents())
        {
            if(count < indexFolder)
            {
                count ++;
                continue;
            }

            if(! ( (JButton)( (JPanel)component).getComponent(0)).getText().equals(HistoryPanGui.addNewReqText) )
                historyPan.getFolderAndReqsPan().remove(component);

            else {
                historyPan.getFolderAndReqsPan().remove(component);
                break;
            }
        }

        historyPan.updateUI();
    }


    /**
     * find name of folder that contains this JButton in GUI panel.
     * @param JButton "addition Request JButton" or a request JButton of this folder in GUI panel.
     * @return name of folder that contains this request or JButton.
     */
    private String findFolderOfReq(JButton JButton)
    {
        String nameFolder = "";
        for(Component component : historyPan.getFolderAndReqsPan().getComponents()) {
            JButton button = (JButton) ((JPanel)component).getComponent(0);
            if (button.getText().startsWith(Folder.startingFolder))
                nameFolder = button.getText().substring(Folder.startingFolder.length() + 1);
            if (button == JButton)
                break;
        }
        return nameFolder;
    }

    /**
     * find index of button in GUI panel.
     * @param jButton the button to be found.
     * @return index of this button in panel
     */
    private int findIndexAddReq(JButton jButton)
    {
        int index = 0;
        for(Component component : historyPan.getFolderAndReqsPan().getComponents()) {
            JButton button = (JButton) ((JPanel)component).getComponent(0);
            if (button == jButton)
                break;
            index ++;
        }
        return index;
    }

    /**
     * set the current workspace as this new workspace.
     * @param workspace new workspace.
     */
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }


    /**
     * Invoked when an item has been selected or deselected by the user.
     * The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     * this method is used to add new workspace or enter to an existing one
     * @param e item event.
     */
    @Override
    public void itemStateChanged(ItemEvent e) {

        if(e.getStateChange() == ItemEvent.SELECTED) {
            // add new workspace.
            if (e.getItem().equals(HistoryPanGui.addnewWorkspace)) {
                String string = JOptionPane.showInputDialog("enter name of workspace");
                if (string == null)
                    return;
                workspace.getApplication().addNewWorkspace(string);
            }
            // enter to an existing workspace.
            else {
                String name = (String) e.getItem();
                Workspace selectedWorkspace = null ;
                for(Workspace tempWorkspace : workspace.getApplication().getWorkspaces())
                    if(tempWorkspace.getName().equals(name)) {
                        selectedWorkspace = tempWorkspace;
                    }
                workspace.setCurrentReq(null);
                workspace.getApplication().setCurrentWorkspace(selectedWorkspace);

            }
        }
    }


    /**
     * show a new folder in the GUI pan.
     * @param name name of new Folder.
     */
    public void newFolderInit(String name){
        historyPan.getFolderAndReqsPan().add( historyPan.makeFolderOrReq(name,null),historyPan.getFolderAndReqsPan().getComponentCount()-1);
        historyPan.getFolderAndReqsPan().add( historyPan.makeFolderOrReq(HistoryPanGui.addNewReqText,null),historyPan.getFolderAndReqsPan().getComponentCount()-1);
        historyPan.updateUI();
    }

    /**
     * show a new request in the GUI pan.
     * @param name name of request.
     * @param parentFolName parent folder name of this req.
     * @param index index of new request to be added in panel.
     */
    public void newRequestInit(String name, String parentFolName ,int index){
        historyPan.getFolderAndReqsPan().add( historyPan.makeFolderOrReq(name,parentFolName),index);
        historyPan.updateUI();
    }

    /**
     * set history panel for this controller.
     * @param historyPan history panel to be set.
     */
    public void setHistoryPan(HistoryPanGui historyPan) {
        this.historyPan = historyPan;
    }

    /**
     * change kind of request.
     * @param kind kind of request among values of "POST,PATCH,PUT,GET,DELETE"
     */
    public void changeKindReq(Request.Kind kind)
    {
        JButton reqBut = null;
        for(Component component : historyPan.getFolderAndReqsPan().getComponents())
        {
            reqBut =  (JButton) (((JPanel)component).getComponent(0));
            if(reqBut.getText().equals(workspace.getCurrentReq().getName()) && workspace.getCurrentReq().getParentFol().getName().equals(findParentFolName(reqBut)))
                break;
        }

        switch (kind)
        {
            case PUT:
                reqBut.setIcon( Request.putI);
                break;
            case POST:
                reqBut.setIcon( Request.postI);
                break;
            case DELETE:
                reqBut.setIcon( Request.deleteI);
                break;
            case PATCH:
                reqBut.setIcon( Request.patchI);
                break;
            case GET:
                reqBut.setIcon( Request.getI);
                break;

        }

        historyPan.getFolderAndReqsPan().updateUI();
    }
}
