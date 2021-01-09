package GUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import Controll.HistoryController;
import MainPart.*;

/**
 * this panel is showing all folders , requests and workspace and as addition , a search panel.
 */
public class HistoryPanGui extends JPanel {

    private JToggleButton filterOnOff;
    private JTextField filterText;
    HistoryController controller;
    private Workspace currentWorkspace;
    Font font = new Font("Arial",Font.PLAIN,15);
    private JPanel folderAndReqsPan;
    public final static String addNewFolText = "    Add new folder";
    public final static String addNewReqText = "         Add new request";
    public final static String addnewWorkspace = "  ____Add new Workspace____";
    Font folderOrReqFont = new Font("simple",Font.BOLD,16);


    /**
     * constructor that makes the panel from info of workspace
     * @param historyController history controller for this panel
     * @param currentWorkspace current workspace for this panel.
     */
    public HistoryPanGui(HistoryController historyController, Workspace currentWorkspace)
    {
        this.currentWorkspace = currentWorkspace;
        controller = historyController;
        setLayout(new BorderLayout());
        makeCenterPart();
        makeTopPart();
        historyController.setHistoryPan(this);
    }

    /**
     * rebuild this panel
     */
    public void rebuild()
    {
        removeAll();
        makeCenterPart();
        makeTopPart();
        updateUI();
    }

    /**
     * make top part of panel which dedicated to workspace selector
     */
    private void makeTopPart()
    {

        JComboBox menuBt = new JComboBox(makeWorkspaces());
        ((JLabel)menuBt.getRenderer()).setHorizontalAlignment( JLabel.CENTER);
        menuBt.setSelectedIndex(findIndexWorkspace());
        menuBt.setFont(new Font("Arial",Font.BOLD,20));
        menuBt.addItemListener(controller);
        menuBt.setPreferredSize(new Dimension(20,45));
        JPanel panel = new JPanel(new GridLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panel.add(menuBt);
        add(panel,BorderLayout.NORTH);
    }


    /**
     * show a message dialog to make the user aware of new work spaces arrival.
     */
    public void showNewWorkspaceReceivedPan ()
    {
        JOptionPane.showMessageDialog(this,"New data received");
    }

    /**
     * find index of this workspace in workspaces of app .
     * @return index of workspace in workspace of app.
     */
    private int findIndexWorkspace()
    {
        ArrayList<Workspace> workspaces = currentWorkspace.getApplication().getWorkspaces();
        return workspaces.indexOf(currentWorkspace);
    }

    /**
     * make a string[] from all workspaces' name.
     * @return all names of workspaces.
     */
    private String[] makeWorkspaces()
    {
        Object[] objects = currentWorkspace.getApplication().getWorkspaces().toArray();
        String[] names = new String[objects.length + 1];
        for(int i=0 ; i<objects.length ; i++)
            names[i] = ((Workspace)objects[i]).getName();
        names[objects.length] = addnewWorkspace;
        return names;
    }


    /**
     * make the center part which is for filter panel and folder\request panel depends on current workspace's info.
     */
    public void makeCenterPart()
    {
        JPanel filterPart = makeFilterpan();
        JPanel historyHolder = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(historyHolder);
        folderAndReqsPan =  makeFolderRequestPan();
        JPanel mainPan = new JPanel(new BorderLayout());

        mainPan.add(filterPart,BorderLayout.NORTH);
        historyHolder.add(folderAndReqsPan,BorderLayout.NORTH);
        mainPan.add(scrollPane,BorderLayout.CENTER);
        add(mainPan,BorderLayout.CENTER);
        updateUI();
    }

    /**
     * make folder and request panel by info of current workspace.
     * @return folder and request panel
     */
    private JPanel makeFolderRequestPan()
    {
        JPanel panel = new JPanel();
        for(Folder folder : currentWorkspace.getFolders()) {
            panel.add(makeFolderOrReq(folder.getName(),null));
            for(Request request : folder.getRequests())
                panel.add(makeFolderOrReq(request.getName(),folder.getName()));
            panel.add(makeFolderOrReq(addNewReqText,null));
        }
        panel.add(makeFolderOrReq(addNewFolText,null));
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        return panel;
    }

    /**
     * make a piece of folder or request panel .
     * @param name name of this folder or request
     * @param parentFolName parent folder which contains this req. you are making a folder , inter null or everything you want.
     * @return piece of folder or request.
     */
    public JPanel makeFolderOrReq(String name,String parentFolName)
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JButton button = new JButton(name);

        //Request.Kind reqKind = Request.Kind.get;
        if(name.startsWith(Folder.startingRequest)) {
            Request.Kind reqKind = currentWorkspace.findReq(name,parentFolName).getKind();
            button.setIcon(reqKind == Request.Kind.GET ? Request.getI : reqKind == Request.Kind.PUT ? Request.putI :
                    reqKind == Request.Kind.PATCH ? Request.patchI : reqKind == Request.Kind.POST ? Request.postI : Request.deleteI);

        }
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFont(folderOrReqFont);
        button.addActionListener( controller);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.ipadx = 5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(button,gbc);

        if(name.startsWith(Folder.startingFolder))
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton button1 = (JButton)e.getSource();
                    int index = 0;
                    for(int j=0; j <folderAndReqsPan.getComponentCount() ; j++) {
                        if ( ((JPanel) folderAndReqsPan.getComponent(j)).getComponent(0) == button1 )
                            break;
                        index ++;
                    }
                    index++;


                    // closing an open folder
                    if(( folderAndReqsPan.getComponent(index)).isVisible()) {
                        for (int i = index; i < folderAndReqsPan.getComponentCount(); i++) {
                            if (!((JButton) ((JPanel) folderAndReqsPan.getComponent(i)).getComponent(0)).getText().startsWith(Folder.startingFolder)
                                    & !((JButton) ((JPanel) folderAndReqsPan.getComponent(i)).getComponent(0)).getText().equals(addNewFolText))
                                (folderAndReqsPan.getComponent(i)).setVisible(false);
                            else
                                break;
                        }
                    }

                    // opening an close folder
                    else  {
                        for (int i = index; i < folderAndReqsPan.getComponentCount(); i++) {
                            if (!folderAndReqsPan.getComponent(i).isVisible()) {
                                if (((JButton) ((JPanel) folderAndReqsPan.getComponent(i)).getComponent(0)).getText().equals(addNewReqText)){
                                    folderAndReqsPan.getComponent(i).setVisible(true);
                                    break;
                                }
                                folderAndReqsPan.getComponent(i).setVisible(true);
                            }
                            else
                                break;
                        }
                    }

                }
            });

        if(!name.equals(addNewReqText) && !name.equals(addNewFolText)) {
            JButton delBt = new JButton("\u2716");
            delBt.setFont( folderOrReqFont);
            delBt.setActionCommand("Delete");
            gbc.gridx = 1;
            gbc.weightx = 0;
            panel.add(delBt, gbc);
            delBt.addActionListener(controller);
        }

        return panel;
    }


    /**
     * make filter panel which contains filter part and an on\off toggleButton.
     * @return filter panel;
     */
    private JPanel makeFilterpan()
    {
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(10,5,5,5));
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();



        filterOnOff =  new JToggleButton();
        filterOnOff.setText("off");
        filterOnOff.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    filterOnOff.setText("on");
                    doSearch(filterText.getForeground()==Color.GRAY?"":filterText.getText());
                }
                else {
                    filterOnOff.setText("off");
                    for(Component component : folderAndReqsPan.getComponents())
                        component.setVisible(true);
                }
            }
        });


        filterText = makeFilterPart();
        filterText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(filterOnOff.getText().equals("on"))
                    doSearch(filterText.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(filterOnOff.getText().equals("on"))
                    doSearch(filterText.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.ipady = 10;
        gbc.gridwidth = 5;
        topPanel.add(filterText,gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        topPanel.add(filterOnOff,gbc);

        return topPanel;
    }


    /**
     * this part is used in filtering proses.
     * @param name name to be searched.
     */
    private void doSearch(String name)
    {
        ArrayList<JPanel> panels = new ArrayList<>();
        for(Component component : folderAndReqsPan.getComponents())
        {
            JButton button = (JButton) ((JPanel)component).getComponent(0);
            if( button.getText().contains(name))
                component.setVisible(true);

            if( ! (button.getText().startsWith(Folder.startingRequest) || button.getText().startsWith(Folder.startingFolder))
                    || !button.getText().contains(name))
                component.setVisible(false);

        }
    }

    /**
     * make filter part which is an TextField.
     * @return filter part.
     */
    private JTextField makeFilterPart(){
        HoverTextField textField = new HoverTextField("search","search");
        textField.setFont(font);
        return textField;
    }

    /**
     * get folders and request panel.
     * @return folders and request panel.
     */
    public JPanel getFolderAndReqsPan() {
        return folderAndReqsPan;
    }
}
