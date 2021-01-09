package Controll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import MainPart.Request;
import MainPart.Workspace;
import GUI.BodyPanelGui;

/**
 * this class links the BodyPanelGui with the  information of application.
 * when a thing in gui would cause changes in request info like adding a new header or query,
 * delete them and ... the process must be done throw this class.
 */
public class BodyController extends FocusAdapter implements ActionListener , ItemListener {

    BodyPanelGui panelGui;
    Workspace currentWorkspace;

    /**
     * simple constructor.
     *
     * @param workspace current workspace of app.
     */
    public BodyController(Workspace workspace) {
        currentWorkspace = workspace;
    }

    /**
     * Invoked when a component gains the keyboard focus.
     * this method is for adding new header and query pieces automatically.
     *
     * @param e the focusEvent happened.
     */
    @Override
    public void focusGained(FocusEvent e) {
        String name = e.getComponent().getName();
        int indexTextInPiece = 0;
        if (name.startsWith("header") || name.startsWith("query") || name.startsWith("Form"))
            indexTextInPiece = Integer.parseInt("" + name.charAt(name.length() - 1));

        String[] newInfo = new String[]{"name", "value", "true" };

        // add new header part.
        if (name.startsWith("header")) {
            JPanel newHeader = panelGui.make_HeQu_Piece(newInfo, "headerDel", "textHeader");
            currentWorkspace.getCurrentReq().makeNewHeader();
            panelGui.getHeaderPan().add(newHeader, panelGui.getHeaderPan().getComponentCount() - 1);
            ((JTextField) ((JPanel) newHeader.getComponent(0)).getComponent(indexTextInPiece)).grabFocus();
            panelGui.updateUI();
        }

        // add new line to form data
        if (name.startsWith("Form")) {
            JPanel newHeader = panelGui.make_HeQu_Piece(newInfo, "FormDel", "textForm");
            currentWorkspace.getCurrentReq().makeNewFormDataLine();
            panelGui.getFormDataPan().add(newHeader, panelGui.getFormDataPan().getComponentCount() - 1);
            ((JTextField) ((JPanel) newHeader.getComponent(0)).getComponent(indexTextInPiece)).grabFocus();
            panelGui.updateUI();
        }

        //add new query part.
        if (name.startsWith("query")) {
            JPanel newQuery = panelGui.make_HeQu_Piece(newInfo, "queryDel", "textQuery");
            currentWorkspace.getCurrentReq().makeNewQuery();
            panelGui.getQueryPan().add(newQuery, panelGui.getQueryPan().getComponentCount() - 1);
            ((JTextField) ((JPanel) newQuery.getComponent(0)).getComponent(indexTextInPiece)).grabFocus();
            panelGui.updateUI();
        }
    }


    /**
     * Invoked when a component loses the keyboard focus.
     * this method is for saving data of texts.
     *
     * @param e focusEvent happened.
     */
    @Override
    public void focusLost(FocusEvent e) {
        String name = e.getComponent().getName();

        // save bodyMassage info .
        if (name.equals("body"))
            currentWorkspace.getCurrentReq().setBodyText(((JTextArea) e.getSource()).getText());

        // save textHeaders info.
        if (name.startsWith("textHeader")) {
            int indexText = Integer.parseInt("" + name.charAt(name.length() - 1)); // there are 2 parts: name and value.
            int indexHeader = 0;
            for (Component component : panelGui.getHeaderPan().getComponents()) {
                JPanel textPan = (JPanel) ((JPanel) component).getComponent(0);
                if (textPan.getComponent(indexText) == e.getSource()) {
                    currentWorkspace.getCurrentReq().updateHeader(indexText, indexHeader, ((JTextField) e.getSource()).getText());
                    return;
                }
                indexHeader++;
            }

        }

        if (name.equals("uri")) {
            String url = ((JTextField) (e.getSource())).getText();
            Request currentReq = currentWorkspace.getCurrentReq();
            currentWorkspace.getCurrentReq().setUrl(url);
            if(panelGui.getQueryUrlField() != null)
                panelGui.getQueryUrlField().setText(currentReq.getUrl() + currentReq.getQueryString(currentReq.HQF_TOStringArr("query")));

        }

        if (name.startsWith("textForm")) {
            int indexText = Integer.parseInt("" + name.charAt(name.length() - 1)); // there are 2 parts: name and value.
            int indexLineForm = 0;
            for (Component component : panelGui.getFormDataPan().getComponents()) {
                JPanel textPan = (JPanel) ((JPanel) component).getComponent(0);
                if (textPan.getComponent(indexText) == e.getSource()) {
                    System.out.println("sdfsdfjsdlkfjsdkf");
                    currentWorkspace.getCurrentReq().updateLineFormData(indexText, indexLineForm, ((JTextField) e.getSource()).getText());
                    return;
                }
                indexLineForm++;
            }

        }

        // save textQuery info.
        if (name.startsWith("textQuery")) {
            int indexText = Integer.parseInt("" + name.charAt(name.length() - 1));
            int indexQuery = 0;
            for (Component component : panelGui.getQueryPan().getComponents()) {
                if (((JPanel) component).getComponentCount() <= 1) // because first component is a single JTextField and not a queryPart.
                    continue;

                JPanel textPan = (JPanel) ((JPanel) component).getComponent(0);
                if (textPan.getComponent(indexText) == e.getSource()) {
                    Request currentReq = currentWorkspace.getCurrentReq();
                    currentReq.updateQuery(indexText, indexQuery, ((JTextField) e.getSource()).getText());
                    panelGui.getQueryUrlField().setText(currentReq.getUrl() + currentReq.getQueryString(currentReq.HQF_TOStringArr("query")));
                    return;
                }
                indexQuery++;
            }

        }

        // save changes of auth.
        if (name.startsWith("auth")) {
            currentWorkspace.getCurrentReq().updateProxy(Integer.parseInt("" + name.charAt(name.length() - 1)), ((JTextField) e.getSource()).getText());
        }
    }

    /**
     * Invoked when an action occurs.
     * includes: deleting headers or queries.
     *
     * @param e actionEvent happened.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        // delete a header part.
        if (command.equals("headerDel")) {
            int indexHeader = 0;
            JPanel headerDel = null;
            for (Component component : panelGui.getHeaderPan().getComponents()) {
                headerDel = (JPanel) component;
                if (headerDel.getComponent(2).equals(e.getSource()))
                    break;
                indexHeader++;
            }

            /* to remove the focus from this text. if not be removed, the focus may be transferred to
               " new header button" , therefore a new part would be replaced.
             */
            ((JButton) e.getSource()).transferFocusUpCycle();
            panelGui.getHeaderPan().remove(headerDel);
            currentWorkspace.getCurrentReq().removeHeader(indexHeader);
            panelGui.updateUI();
        }

        // delete a form data line .
        if (command.equals("FormDel")) {
            int indexFormLine = 0;
            JPanel formLineDel = null;
            for (Component component : panelGui.getFormDataPan().getComponents()) {
                formLineDel = (JPanel) component;
                if (formLineDel.getComponent(2).equals(e.getSource()))
                    break;
                indexFormLine++;
            }

            /* to remove the focus from this text. if not be removed, the focus may be transferred to
               " new header button" , therefore a new part would be replaced.
             */
            ((JButton) e.getSource()).transferFocusUpCycle();
            panelGui.getFormDataPan().remove(formLineDel);
            currentWorkspace.getCurrentReq().removeLineFormData(indexFormLine);
            panelGui.updateUI();
        }

        //delete a query part.
        if (command.equals("queryDel")) {
            JPanel queryDel = null;
            int indexQueryDel = 0;
            for (Component component : panelGui.getQueryPan().getComponents()) {
                queryDel = (JPanel) component;
                if (queryDel.getComponentCount() > 1) {
                    indexQueryDel++;
                    if (queryDel.getComponent(2).equals(e.getSource())) {
                        break;
                    }
                }
            }

            /* to remove the focus from this text. if not be removed, the focus may be transferred to
               " new query button" , therefore a new part would be replaced.
             */
            ((JButton) e.getSource()).transferFocusUpCycle();
            panelGui.getQueryPan().remove(queryDel);
            currentWorkspace.getCurrentReq().removeQuery(indexQueryDel - 1);
            Request currentReq = currentWorkspace.getCurrentReq();
            panelGui.getQueryUrlField().setText(currentReq.getUrl() + currentReq.getQueryString(currentReq.HQF_TOStringArr("query")));

            panelGui.updateUI();
        }


        //omit attached file
        if (command.equals("restartFile")) {
            currentWorkspace.getCurrentReq().setBodyText("");
            currentWorkspace.getCurrentReq().setFilePath("");
            panelGui.getSelectedFileField().setText("");
        }

        //add new file to be uploaded.
        if (command.equals("addFile")) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(null);
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                String filePath = file.getAbsolutePath();
                currentWorkspace.getCurrentReq().setFilePath(filePath);
                panelGui.getSelectedFileField().setText(filePath + "        (" + file.length() / 1000f + " KB)");
            }
        }


    }

    /**
     * set bodyPanel gui for this controller.
     *
     * @param panelGui panel to be set.
     */
    public void setBodyPanelGui(BodyPanelGui panelGui) {
        this.panelGui = panelGui;
    }

    /**
     * set current workspace.
     *
     * @param currentWorkspace set the current workspace
     */
    public void setCurrentWorkspace(Workspace currentWorkspace) {
        this.currentWorkspace = currentWorkspace;
    }

    /**
     * Invoked when an item has been selected or deselected by the user.
     * The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     *
     * @param e
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getItem().equals("JSON")) {
                if (JOptionPane.showConfirmDialog(panelGui, "info of body message may be lost if you change the form.\nare you sure to change it?") == JOptionPane.YES_OPTION) {
                    currentWorkspace.getCurrentReq().setMassageBodyKind(1);
                    currentWorkspace.getCurrentReq().setBodyText("");
                }
                panelGui.reBuild();
            }

            if (e.getItem().equals("Upload")) {
                if (JOptionPane.showConfirmDialog(panelGui, "info of body message may be lost if you change the form.\nare you sure to change it?") == JOptionPane.YES_OPTION)
                    currentWorkspace.getCurrentReq().setMassageBodyKind(2);
                panelGui.reBuild();
            } else if (e.getItem().equals("Form Data")) {
                if (JOptionPane.showConfirmDialog(panelGui, "info of body message may be lost if you change the form.\nare you sure to change it?") == JOptionPane.YES_OPTION)
                    currentWorkspace.getCurrentReq().setMassageBodyKind(0);
                panelGui.reBuild();

            } else if (e.getItem().equals(BodyPanelGui.putM)) {
                currentWorkspace.getCurrentReq().setKind(Request.Kind.PUT);
                currentWorkspace.getApplication().getMainController().getHistoryController().changeKindReq(Request.Kind.PUT);
            } else if (e.getItem().equals(BodyPanelGui.getM)) {
                currentWorkspace.getCurrentReq().setKind(Request.Kind.GET);
                currentWorkspace.getApplication().getMainController().getHistoryController().changeKindReq(Request.Kind.GET);
            } else if (e.getItem().equals(BodyPanelGui.postM)) {
                currentWorkspace.getCurrentReq().setKind(Request.Kind.POST);
                currentWorkspace.getApplication().getMainController().getHistoryController().changeKindReq(Request.Kind.POST);
            } else if (e.getItem().equals(BodyPanelGui.deleteM)) {
                currentWorkspace.getCurrentReq().setKind(Request.Kind.DELETE);
                currentWorkspace.getApplication().getMainController().getHistoryController().changeKindReq(Request.Kind.DELETE);
            } else if (e.getItem().equals(BodyPanelGui.patchM)) {
                currentWorkspace.getCurrentReq().setKind(Request.Kind.PATCH);
                currentWorkspace.getApplication().getMainController().getHistoryController().changeKindReq(Request.Kind.PATCH);
            }

        }
    }

}
