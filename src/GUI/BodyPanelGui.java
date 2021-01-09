package GUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import Controll.BodyController;
import Controll.ReqSendingController;
import MainPart.Request;
import MainPart.TimeElapse;
import MainPart.Workspace;
import com.github.weisj.darklaf.components.text.NumberedTextComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * this gui panel is where the customer makes his request. contains URl text , header,query, bodyMassage and auth.
 */
public class BodyPanelGui extends JPanel {

    JComboBox comboBox;
    JTextField topUrlField;
    JTextField queryUrlField;
    BodyController controller ;
    ReqSendingController reqSendingController;
    JPanel headerPan;
    JPanel formDataPan;
    JPanel queryPan;
    RSyntaxTextArea jsonTextArea ;
    ArrayList<JPanel> queryPieces;
    Workspace currentWorkspace;
    JTextField selectedFileField;
    Font font = new Font("Arial",Font.PLAIN,13);
    Font fontLeader = new Font("Arial",Font.BOLD,12);

    public static final ImageIcon getM = new ImageIcon(new ImageIcon(".\\icons\\getM.png").getImage().getScaledInstance(35,10, Image.SCALE_DEFAULT));
    public static final ImageIcon putM = new ImageIcon(new ImageIcon(".\\icons\\putM.png").getImage().getScaledInstance(35,10, Image.SCALE_DEFAULT));
    public static final ImageIcon patchM = new ImageIcon(new ImageIcon(".\\icons\\patcM.png").getImage().getScaledInstance(35,10, Image.SCALE_DEFAULT));
    public static final ImageIcon postM =new ImageIcon(new ImageIcon(".\\icons\\postM.png").getImage().getScaledInstance(35,10, Image.SCALE_DEFAULT));
    public static final ImageIcon deleteM = new ImageIcon(new ImageIcon(".\\icons\\delM.png").getImage().getScaledInstance(35,10, Image.SCALE_DEFAULT));


    /**
     * constructor that makes the panel depends on info of current request of current workspace.
     * @param bodyController body controller that would control this panel.
     * @param currentWorkspace current workspace.
     */
    public BodyPanelGui(BodyController bodyController,ReqSendingController reqSendingController, Workspace currentWorkspace)
    {
        this.currentWorkspace = currentWorkspace;
        controller = bodyController;
        this.reqSendingController = reqSendingController;
        queryPieces = new ArrayList<>();

        setLayout(new BorderLayout());
        makeTopPart();
        makeCenterPart();
        bodyController.setBodyPanelGui(this);
    }


    /**
     * rebuild this panel depends on current request.
     */
    public void reBuild()
    {
        this.removeAll();
        makeTopPart();
        makeCenterPart();
        updateUI();
    }


    /**
     * make the center part of panel which contains : header, query, massageBody and auth.
     * if there is no active request it wold make a leading panel.
     */
    private void makeCenterPart()
    {
        if(currentWorkspace.getCurrentReq() == null) {
            add(leadingPan(), BorderLayout.CENTER);
            return;
        }

        JPanel bodyTab = makeBodyPan();
        JScrollPane headerTab = makeHeaderTab();
        JPanel authTab = makeProxyPan();
        JScrollPane queryTab = makeQueryPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(bodyTab,"Body");
        tabbedPane.add(headerTab,"Header");
        tabbedPane.add(authTab,"Proxy");
        tabbedPane.add(queryTab,"Query");

        add(tabbedPane,BorderLayout.CENTER);
    }


    /**
     * makes a leading pan which is like a grating and help panel.
     * @return leading panel that been made.
     */
    private JPanel leadingPan()
    {

        JPanel panel = new JPanel( new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();


        JLabel I = new JLabel("I");
        I.setFont(new Font("simple",Font.BOLD,17));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(I,gbc);

        JLabel N = new JLabel("_N_");
        N.setFont(new Font("simple",Font.BOLD,17));
        gbc.gridy = 1;
        panel.add(N,gbc);

        JLabel S = new JLabel("__S__");
        S.setFont(new Font("simple",Font.BOLD,17));
        gbc.gridy = 2;
        panel.add(S,gbc);

        JLabel O = new JLabel("___O___");
        O.setFont(new Font("simple",Font.BOLD,17));
        gbc.gridy = 3;
        panel.add(O,gbc);

        JLabel M = new JLabel("____M____");
        M.setFont(new Font("simple",Font.BOLD,17));
        gbc.gridy = 4;
        panel.add(M,gbc);

        JLabel N2 = new JLabel("_____N_____");
        N2.setFont(new Font("simple",Font.BOLD,17));
        gbc.gridy = 5;
        panel.add(N2,gbc);

        JLabel I2 = new JLabel("______I______");
        I2.setFont(new Font("simple",Font.BOLD,17));
        gbc.gridy = 6;
        panel.add(I2,gbc);

        JLabel A = new JLabel("_______A_______");
        A.setFont(new Font("simple",Font.BOLD,17));
        gbc.gridy = 7;
        panel.add(A,gbc);


        JLabel label = new JLabel("Welcome to Insomnia");
        label.setFont(new Font("simple",Font.BOLD,17));
        gbc.gridy = 9;
        panel.add(label,gbc);

        JLabel label1 = new JLabel("Make a new Folder or request from the left panel");
        label1.setFont(new Font("simple",Font.BOLD,16));
        gbc.gridy = 10;
        panel.add(label1,gbc);


        JLabel label2 = new JLabel("Choose a request to start your work");
        label2.setFont(new Font("simple",Font.BOLD,15));
        gbc.gridy = 11;
        panel.add(label2,gbc);

        JLabel label3 = new JLabel("Options    ctrl + O ");
        label3.setFont(new Font("simple",Font.BOLD,14));
        gbc.gridy = 12;
        panel.add(label3,gbc);

        JLabel label4 = new JLabel("Help  ctrl + H");
        label4.setFont(new Font("simple",Font.BOLD,13));
        gbc.gridy = 13;
        panel.add(label4,gbc);

        return panel;
    }


    /**
     * make the query panel.
     * @return scrollable query panel.
     */
    private JScrollPane makeQueryPanel()
    {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane jScrollPane = new JScrollPane(mainPanel);
        queryPan = new JPanel();
        BoxLayout boxLayout = new BoxLayout(queryPan ,BoxLayout.Y_AXIS );

        JPanel textPan = new JPanel(new BorderLayout());
        queryUrlField = new JTextField(currentWorkspace.getCurrentReq().getUrlQuery());
        queryUrlField.setFont(font);
        textPan.setBorder( BorderFactory.createEmptyBorder(15,10,10,10));
        textPan.setPreferredSize(new Dimension(10,65));
        queryUrlField.setEditable(false);
        Request currentReq = currentWorkspace.getCurrentReq();
        queryUrlField.setText(currentReq.getUrl() + currentReq.getQueryString(currentReq.HQF_TOStringArr("query")));

        textPan.add(queryUrlField);
        queryPan.setLayout(boxLayout);
        queryPan.add(textPan );

        for(String[] info : currentWorkspace.getCurrentReq().getQueries())
        {
            JPanel newQueryPiece = make_HeQu_Piece(info,"queryDel","textQuery");
            queryPan.add(newQueryPiece);
            queryPieces.add(newQueryPiece);
        }

        queryPan.add(make_HeQu_LastPiece("query"));

        mainPanel.add(queryPan,BorderLayout.NORTH);
        return jScrollPane;
    }

    /**
     * make the scrollable header pan.
     * @return scrollable header pan
     */
    private JScrollPane makeHeaderTab()
    {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane mainScrol = new JScrollPane(mainPanel);
        headerPan = new JPanel();
        BoxLayout boxLayout = new BoxLayout(headerPan,BoxLayout.Y_AXIS );
        headerPan.setLayout(boxLayout);


        for(String[] info : currentWorkspace.getCurrentReq().getHeaders())
        {
            JPanel headerTempPan = make_HeQu_Piece(info, "headerDel","textHeader");
            headerPan.add(headerTempPan );
        }

        headerPan.add(make_HeQu_LastPiece("header"));

        mainPanel.add(headerPan,BorderLayout.NORTH);

        return mainScrol;
    }

    /**
     * make the last piece of header or query that is responsible for adding new header/query pieces.
     * @param kind  "header" or "query"
     * @return last piece of header/query pan.
     */
    private JPanel make_HeQu_LastPiece(String kind)
    {

        JPanel panel = new JPanel( new GridBagLayout());
        panel.setBorder( BorderFactory.createEmptyBorder(5,5,5,5));
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel textPanel = new JPanel( new GridLayout(1,2,10,5));

        JTextField headertext = new JTextField("new name");
        headertext.setForeground(Color.GRAY);
        headertext.setFont(font);
        headertext.setName(kind + "_0");
        headertext.addFocusListener(controller);

        JTextField valuetext = new JTextField("new value");
        valuetext.setForeground(Color.GRAY);
        valuetext.setFont(font);
        valuetext.setName(kind + "_1");
        valuetext.addFocusListener(controller);

        textPanel.add(headertext);
        textPanel.add(valuetext);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0;
        gbc.ipady = 10;
        gbc.weightx = 1;
        gbc.gridwidth = 8 ;
        panel.add(textPanel,gbc);


        JLabel emptyLable = new JLabel("                      ");
        gbc.gridx = 8;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        panel.add(emptyLable,gbc);


        return panel;
    }


    /**
     * make normal piece of header/query that contains 2 textFields , one checkBox and one button.
     * @param info info of this header from the request. {name,value,"true" or "false"}.
     * @param actionCommandDel action name for delete button.
     * @param actionTextCommand action name for textFields.
     * @return a piece of header/query .
     */
    public JPanel make_HeQu_Piece(String[] info,String actionCommandDel,String actionTextCommand)
    {
        String name = info[0];
        String value  = info[1];
        String valueCheck =  info[2];
        JPanel panel = new JPanel( new GridBagLayout());
        panel.setBorder( BorderFactory.createEmptyBorder(5,5,5,5));
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel textPanel = new JPanel( new GridLayout(1,2,10,5));

        HoverTextField headerLabel = new HoverTextField("name",name);
        textPanel.add(headerLabel);
        headerLabel.setFont(font);
        headerLabel.setName(actionTextCommand + "_0");
        headerLabel.addFocusListener(controller);

        HoverTextField valueLabel = new HoverTextField("value",value);
        textPanel.add(valueLabel);
        valueLabel.setFont(font);
        valueLabel.setName(actionTextCommand + "_1");
        valueLabel.addFocusListener(controller);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0;
        gbc.ipady = 10;
        gbc.weightx = 1;
        gbc.gridwidth = 4 ;
        panel.add(textPanel,gbc);

        JCheckBox checkBox = new JCheckBox("",valueCheck.equals("true"));
        if( !valueCheck.equals("true")) {
            headerLabel.setEnabled(false);
            valueLabel.setEnabled(false);
        }
        gbc.gridx = 8;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(checkBox,gbc);
        checkBox.addItemListener(new ItemListener() {
             @Override
             public void itemStateChanged(ItemEvent e) {
                 if(e.getStateChange() == ItemEvent.SELECTED) {
                     info[2] = "true";
                     if(actionCommandDel.startsWith("query")) {
                         Request currentReq = currentWorkspace.getCurrentReq();
                         queryUrlField.setText(currentReq.getUrl() + currentReq.getQueryString(currentReq.HQF_TOStringArr("query")));
                     }
                     headerLabel.setEnabled(true);
                     valueLabel.setEnabled(true);
                 }

                 else {
                     info[2] = "false";
                     if(actionCommandDel.startsWith("query")) {
                         Request currentReq = currentWorkspace.getCurrentReq();
                         queryUrlField.setText(currentReq.getUrl() + currentReq.getQueryString(currentReq.HQF_TOStringArr("query")));
                     }
                     headerLabel.setEnabled(false);
                     valueLabel.setEnabled(false);
                 }
             }
         }
        );

        JButton button = new JButton("\u2716");
        button.setActionCommand(actionCommandDel);
        button.addActionListener(controller);
        gbc.gridx = 9;
        panel.add(button);

        return panel;
    }


    /**
     * make proxy pan depends on info of req.
     * @return panel of proxy.
     */
    private JPanel makeProxyPan()
    {
        JPanel mainPanel = new JPanel( new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder( BorderFactory.createEmptyBorder(10,10,10,10));
        panel.setPreferredSize(new Dimension(10,200));
        GridBagConstraints gbc = new GridBagConstraints();


        JTextField tokenText = new JTextField();
        tokenText.setName("auth_0");
        tokenText.setText(currentWorkspace.getCurrentReq().getProxy()[0]);
        tokenText.addFocusListener(controller);
        tokenText.setFont(font);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.ipadx = 20;
        gbc.ipady = 20;
        gbc.weightx = 1;
        panel.add(tokenText,gbc);

        JTextField prefixText = new JTextField();
        prefixText.setText(currentWorkspace.getCurrentReq().getProxy()[1]);
        prefixText.setName("auth_1");
        prefixText.addFocusListener(controller);
        prefixText.setFont(font);
        gbc.gridy = 1;
        panel.add(prefixText,gbc);

        JLabel tokenLapel = new JLabel("SERVER");
        tokenLapel.setFont(fontLeader);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx =0;
        panel.add(tokenLapel,gbc);

        JLabel prefixLabel = new JLabel("PORT");
        prefixLabel.setFont(fontLeader);
        gbc.gridy = 1;
        panel.add(prefixLabel,gbc);

        JLabel enableLabel = new JLabel("ENABLE");
        enableLabel.setFont(fontLeader);
        gbc.gridy=2;
        panel.add(enableLabel,gbc);

        JCheckBox enableCheckBox = new JCheckBox("",currentWorkspace.getCurrentReq().getProxy()[2].equals("true"));
        if(currentWorkspace.getCurrentReq().getProxy()[2].equals("false"))
        {
            tokenText.setEnabled(false);
            prefixText.setEnabled(false);
        }
        gbc.gridx = 1;
        panel.add(enableCheckBox,gbc);
        enableCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    tokenText.setEnabled(true);
                    prefixText.setEnabled(true);
                    currentWorkspace.getCurrentReq().updateProxy(2,"true");
                }
                else {
                    tokenText.setEnabled(false);
                    prefixText.setEnabled(false);
                    currentWorkspace.getCurrentReq().updateProxy(2,"false");
                }

            }
        });

        mainPanel.add(panel,BorderLayout.NORTH);
        return mainPanel;
    }


    /**
     * make massage body panel depends on info of req.
     * @return massage body panel.
     */
    private JPanel makeBodyPan()
    {
        JPanel comboHolder = new JPanel(new BorderLayout());
        JComboBox comboBox = new JComboBox(new String[]{"Form Data","JSON","Upload"});
        comboBox.setSelectedIndex(currentWorkspace.getCurrentReq().getMassageBodyKind());
        comboHolder.setBorder( BorderFactory.createEmptyBorder(5,5,10,5));
        comboHolder.add(comboBox);
        comboBox.addItemListener(controller);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(comboHolder,BorderLayout.NORTH);

        if(Objects.equals(comboBox.getSelectedItem(), "Form Data")) {
            JScrollPane scrollPane = formDataPan();
            jPanel.add(scrollPane);
        }

        else if(Objects.equals(comboBox.getSelectedItem(), "Upload")) {
            JPanel scrollPane = uploadPan();
            jPanel.add(scrollPane);
        }

        else if (currentWorkspace.getCurrentReq().getMassageBodyKind() == 1)
        {
            jsonTextArea = new RSyntaxTextArea(currentWorkspace.getCurrentReq().getBodyText());
            jsonTextArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JSON);
            jsonTextArea.setCodeFoldingEnabled(true);
            jsonTextArea.setBackground( currentWorkspace.getApplication().getMainController().getMainDisplayGui().themeTextColor((int)SettingInfo.theme.info));
            jsonTextArea.setName("body");
            jsonTextArea.addFocusListener(controller);
            jsonTextArea.setFont(font);
            jsonTextArea.setBorder(BorderFactory.createEmptyBorder(5,10,5,5));
            currentWorkspace.getApplication().getMainController().getMainDisplayGui().setTheme((int)SettingInfo.theme.getInfo());
            NumberedTextComponent lineNumber = new NumberedTextComponent(jsonTextArea);
            jPanel.add(lineNumber,BorderLayout.CENTER);
        }

        return jPanel;
    }


    /**
     * make upload panel.
     * @return upload panel.
     */
    private JPanel uploadPan()
    {
        JPanel mainPan = new JPanel(new BorderLayout());
        GridBagLayout gridBagLayout = new GridBagLayout();
        JPanel panel = new JPanel(gridBagLayout);
        GridBagConstraints gbc = new GridBagConstraints();

        selectedFileField =  new JTextField();
        selectedFileField.setEditable(false);
        if(currentWorkspace.getCurrentReq().getFilePath() != null) {
            String filePath = currentWorkspace.getCurrentReq().getFilePath();
            selectedFileField.setText(filePath + "        (" + new File(filePath).length() / 1000f + " KB)");
        }
        selectedFileField.setBorder( BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Selected file", TitledBorder.LEADING ,TitledBorder.LEFT));
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridy = 0;
        gbc.ipady = 15;
        gbc.gridx = 0;
        gbc.weightx = 10;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(selectedFileField,gbc);

        JLabel emptyLabel = new JLabel();
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(emptyLabel,gbc);

        JLabel emptyLabel2 = new JLabel();
        gbc.gridx = 3;
        panel.add(emptyLabel2,gbc);


        JButton removeBt = new JButton("Restart file");
        removeBt.setActionCommand("restartFile");
        removeBt.addActionListener(controller);
        gbc.weightx = 0;
        gbc.gridx = 1;
        panel.add(removeBt,gbc);

        JButton add = new JButton("Choose file");
        add.setActionCommand("addFile");
        add.addActionListener(controller);
        gbc.gridx = 2;
        panel.add(add,gbc);


        mainPan.add(panel,BorderLayout.NORTH);
        return mainPan;
    }


    /**
     * build form data panel.
     * @return form data panel.
     */
    private JScrollPane formDataPan()
    {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane mainScroll = new JScrollPane(mainPanel);
        formDataPan = new JPanel();
        BoxLayout boxLayout = new BoxLayout(formDataPan,BoxLayout.Y_AXIS );
        formDataPan.setLayout(boxLayout);

        for(String[] info : currentWorkspace.getCurrentReq().getFormDataBody())
        {
            JPanel headerTempPan = make_HeQu_Piece(info, "FormDel","textForm");
            formDataPan.add(headerTempPan );
        }

        formDataPan.add(make_HeQu_LastPiece("Form"));

        mainPanel.add(formDataPan,BorderLayout.NORTH);

        return mainScroll;
    }


    /**
     * make top part of body panel which contains url input text , sending button and operation choosing.
     */
    private void makeTopPart()
    {
        if(currentWorkspace.getCurrentReq() == null)
            return;
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(5,5,20,5));
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        topUrlField = new JTextField();
        topUrlField.setFont(font);
        topUrlField.setText(currentWorkspace.getCurrentReq().getUrl());
        topUrlField.setName("uri");
        topUrlField.addFocusListener(controller);
        comboBox = makeComboBox();
        JButton sendBt = new JButton("send");
        sendBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TimeElapse.start();
                ResponsePanelGui responsePanelGui =  currentWorkspace.getApplication().getMainController().getMainDisplayGui().getRightPan();
                responsePanelGui.removeAll();
                responsePanelGui.add(new JLabel("sending request ...",JLabel.CENTER));
                responsePanelGui.updateUI();
            }
        });
        sendBt.addActionListener(reqSendingController);


        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.ipadx = 20;
        gbc.ipady = 15;
        gbc.gridwidth = 5;
        topPanel.add(topUrlField,gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        topPanel.add(comboBox,gbc);

        gbc.gridx = 6;
        topPanel.add(sendBt,gbc);


        add(topPanel,BorderLayout.NORTH);
    }

    /**
     * make "operation choosing" combo box.
     * @return "operation choosing" combo box
     */
    private JComboBox makeComboBox()
    {
        ImageIcon[] options = new ImageIcon[]{getM,putM,patchM,postM,deleteM};
        JComboBox comboBox =  new JComboBox(options);
        ((JLabel)comboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
        comboBox.setSelectedIndex(currentWorkspace.getCurrentReq().getKind().ordinal());
        comboBox.setPreferredSize(new Dimension(70,15));
        comboBox.addItemListener(controller);
        return comboBox;
    }


    /**
     * get header panel .
     * @return header panel
     */
    public JPanel getHeaderPan() {
        return headerPan;
    }


    /**
     * get query panel.
     * @return query panel
     */
    public JPanel getQueryPan() {
        return queryPan;
    }

    /**
     * get json text area that user in writhing on.
     * @return json text area.
     */
    public RSyntaxTextArea getJsonTextArea() {
        return jsonTextArea;
    }

    /**
     * get form data panel.
     * @return form data panel.
     */
    public JPanel getFormDataPan() {
        return formDataPan;
    }

    /**
     * get selected file field .
     * @return selected file field that shows the selected file path to be uploaded.
     */
    public JTextField getSelectedFileField() {
        return selectedFileField;
    }

    /**
     * get query url field that show url + query .
     * @return query url field that show url + query .
     */
    public JTextField getQueryUrlField() {
        return queryUrlField;
    }
}
