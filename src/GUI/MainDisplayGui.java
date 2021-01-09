package GUI;

import Controll.MainControler;
import MainPart.Workspace;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import FileHandler.*;
import org.fife.ui.rsyntaxtextarea.Theme;


/**
 * main display of this program. this frame contains history, body and response panels.
 */
public class MainDisplayGui extends JFrame {

    private ResponsePanelGui rightPan;
    private BodyPanelGui centerPan;
    private HistoryPanGui leftPan;
    private JDialog optionDialog;
    private JDialog transferFileDialog;
    private JDialog aboutDialog;
    private JSplitPane leftSplit;
    private MainControler mainControler;
    private Workspace currentWorkspace;
    private JPanel mainPanUpdate;
    int theme;

    /**
     * construct the frame by info of workspace that is found in main controller.
     * if there is no active request it would make an leading\grating panel.
     * @param mainControler main controller of this app.
     */
    public MainDisplayGui(MainControler mainControler)
    {
        setTheme((int)SettingInfo.theme.getInfo());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanUpdate = new JPanel(new BorderLayout());
        add(mainPanUpdate, BorderLayout.CENTER);
        mainControler.setMainDisplayGui(this);
        this.mainControler = mainControler;
        this.currentWorkspace = mainControler.getApplication().getCurrentWorkspace();

        theme = 1;
        rightPan = new ResponsePanelGui(currentWorkspace,mainControler.getResponseController());
        rightPan.setSize(10,20);
        leftPan =new HistoryPanGui(mainControler.getHistoryController(), currentWorkspace);
        leftPan.setSize(10,20);
        centerPan = new BodyPanelGui(mainControler.getBodyController(),mainControler.getReqSendingController(), currentWorkspace);
        centerPan.setSize(20,20);
        makeMenubar();
        makeOptionD();
        makeAboutD();
        makeTransferFileD();
        makeFrame();
        makeSpitedPans();
    }

    /**
     * rebuild the hole frame by info of workspace.
     * its commonly used after changing workspace.
     */
    public void rebuild(){
        this.currentWorkspace = mainControler.getApplication().getCurrentWorkspace();
        rightPan = new ResponsePanelGui(currentWorkspace,mainControler.getResponseController());
        rightPan.setSize(10,20);
        leftPan =new HistoryPanGui(mainControler.getHistoryController(), currentWorkspace);
        leftPan.setSize(10,20);
        centerPan = new BodyPanelGui(mainControler.getBodyController(),mainControler.getReqSendingController(), currentWorkspace);
        centerPan.setSize(20,20);
        makeSpitedPans();
        rightPan.updateUI();
        centerPan.updateUI();
        leftPan.updateUI();

    }


    /**
     * build the frame by size.
     */
    private void makeFrame()
    {
        setBounds((int)SettingInfo.frameX.getInfo(),(int)SettingInfo.frameY.getInfo(),(int)SettingInfo.weight.getInfo(),(int)SettingInfo.height.getInfo());
        addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             *
             * @param e
             */
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    FileHandler.saveSettings();
                    FileHandler.saveApp(currentWorkspace.getApplication());
                    System.exit(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    /**
     * make the menu bar for this frame.
     */
    private void makeMenubar ()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.BLACK);

        JMenu apMenu = makeApMenu();
        menuBar.add(apMenu);

        JMenu viewMenu = makeViewMenu();
        menuBar.add(viewMenu);

        JMenu helpMenu = makeHelpMenu();
        menuBar.add(helpMenu);

        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * show the help frame.
     */
    private void showHelpD()
    {
        JDialog helpD = new JDialog(this,"Help");
        helpD.setBounds(400,280,600,400);
        JTextArea textArea = new JTextArea("\"-H <e> , --header <e>  : set headers . headers must be paired. e.g [-H \"name:value;name2:value2\\\"]\")\n" +
                "  \"-Q <e> , --query <e>  : set queries . queries must be paired. e.g [-H \"name:value;name2:value2\"]\")\n" +
                "  \"         --url    <e>  : set uri address. e.g [--url www.google.com]\")\n" +
                "  \"-M <e> , --method <e>  : set method of request. e.g [-M POST]\")\n" +
                "  \"-i                     : show headers of response.\")\n" +
                "  \"-f                     : actives follow redirect. \")\n" +
                "  \"-O <o> , --output <o>  : save response body in file. e.g [ -O  C:/folder/test.txt]\\n\" +\n" +
                "  \"                         NOTE: if file not mentioned,will make a default name\")\n" +
                "  \"-S     , --save        : save the request.\")\n" +
                "  \"-d <e> , --data   <e>  : set body massage in form data. e.g [-d \"name=value&name2=value2\"]\")\n" +
                "  \"-j <e> , --json   <e>  : set body massage in json. e.g [-j \"{firstName:nameF,lastName:nameL}\"]\")\n" +
                "  \"       , --upload <e>  : set a file to be upload. e.g [--upload C:/file/test.txt]\")\n" +
                "  \"       ,  list    <e>  : shows all request of folder e . e.g [list myRequests]\")\n" +
                "  \"       ,  fire <e...>  : execute all requests mentioned of folder. e.g [fire myRequests 2 3 1]\")\n" +
                "  \"       ,  create  <e>  : create a new request folder. e.g [create myRequests]\")");
        JScrollPane scrollPane = new JScrollPane(textArea);
        helpD.add(scrollPane);
        textArea.setEditable(false);
        helpD.setVisible(true);
    }


    /**
     * make the help menu to be added to menu bar.
     * this menu contains : help and about
     * @return help menu
     */
    private JMenu makeHelpMenu()
    {
        JMenu helpMenu =  new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuItem item1 = new JMenuItem("About");
        item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A , InputEvent.CTRL_MASK));
        item1.addActionListener(e -> aboutDialog.setVisible(true));
        helpMenu.add(item1);

        JMenuItem item3 = new JMenuItem("Help");
        item3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.CTRL_MASK));
        item3.addActionListener(e -> showHelpD());
        helpMenu.add(item3);
        return helpMenu;
    }


    /**
     * make the view menu to be added to menu bar.
     * this contains: toggle full screen and toggle sidebar
     * @return view menu.
     */
    private JMenu makeViewMenu(){
        JMenu viewMenu =  new JMenu("View");
        viewMenu.setMnemonic('V');

        JMenuItem item1 = new JMenuItem("Toggle Full Screen");
        item1.setMnemonic('T');
        item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T , InputEvent.CTRL_MASK));
        if((boolean)SettingInfo.fullScreen.getInfo()) {
            Dimension DimMax = Toolkit.getDefaultToolkit().getScreenSize();
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    setExtendedState(JFrame.NORMAL);
                    SettingInfo.fullScreen.setInfo(false);
                }
                else {
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                    SettingInfo.fullScreen.setInfo(true);
                }
            }
        });


        viewMenu.add(item1);

        JMenuItem item3 = new JMenuItem("Toggle Sidebar");
        item3.setMnemonic('S');
        item3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
        item3.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (leftPan.isVisible()) {
                    leftPan.setVisible(false);
                    SettingInfo.toggleSideBar.setInfo(true);
                } else {
                    leftSplit.setDividerLocation( -2);
                    leftPan.setVisible(true);
                    SettingInfo.toggleSideBar.setInfo(false);
                }
            }
        });
        if((boolean)SettingInfo.toggleSideBar.getInfo()) {
            item3.doClick();
        }
        viewMenu.add(item3);
        return viewMenu;
    }


    /**
     * make the about dialog that shows info of programmer.
     */
    private void makeAboutD()
    {
        aboutDialog = new JDialog(this,"About");
        aboutDialog.setBounds(400,280,300,180);
        JTextArea textArea = new JTextArea("Midterm project \nprogrammer:  Farzad Radnia\nStudentId: 9831024\nEmail address: f.radnia2@gamil.com");
        textArea.setFont( new Font("Arial",Font.PLAIN,15));
        textArea.setEditable(false);
        aboutDialog.add(textArea);
    }


    /**
     * make option frame that contains settings of : follow redirect, terminate on exit and themes.
     */
    private void makeOptionD ()
    {
        optionDialog = new JDialog(this,"Options");
        JPanel panel = new JPanel();
        optionDialog.setBounds(400,280,300,400);
        panel.setLayout( new GridLayout(9,1));

        JCheckBox followRedirect = new JCheckBox("follow redirect",(boolean)SettingInfo.flowReDirect.getInfo());
        followRedirect.setFont(new Font("Arial",Font.PLAIN,19));
        followRedirect.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                    SettingInfo.flowReDirect.setInfo(true);

                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    SettingInfo.flowReDirect.setInfo(false);

            }
        });
        if((boolean)SettingInfo.flowReDirect.getInfo())
            followRedirect.setSelected(true);
        else
            followRedirect.setSelected(false);
        panel.add(followRedirect);

        JCheckBox exitOption =  new JCheckBox("terminate on exit",(boolean)SettingInfo.terminateOnExit.getInfo());
        exitOption.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
               if (e.getStateChange() == ItemEvent.DESELECTED) {
                   setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                   SettingInfo.terminateOnExit.setInfo(false);
               }
               else {
                   setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                   SettingInfo.terminateOnExit.setInfo(true);
               }
            }
        });
        exitOption.setFont(new Font("Arial",Font.PLAIN,19));
        panel.add(exitOption);


        JRadioButton lightTheme = new JRadioButton("light theme",(int)SettingInfo.theme.getInfo() == 0);
        lightTheme.setFont(new Font("Arial",Font.PLAIN,19));
        lightTheme.addActionListener(e -> setTheme(0));
        panel.add(lightTheme);

        JRadioButton gray_theme = new JRadioButton("solarized dark theme",(int)SettingInfo.theme.getInfo() == 1);
        gray_theme.setFont(new Font("Arial",Font.PLAIN,19));
        gray_theme.addActionListener(e ->  setTheme(1));
        panel.add(gray_theme);

        JRadioButton solarized_light_theme = new JRadioButton("Solarized light theme",(int)SettingInfo.theme.getInfo() == 2);
        solarized_light_theme.setFont(new Font("Arial",Font.PLAIN,19));
        solarized_light_theme.addActionListener(e -> setTheme(2) );
        panel.add(solarized_light_theme);

        JRadioButton highContrastDarkTheme = new JRadioButton("High Contrast Dark Theme",(int)SettingInfo.theme.getInfo() == 3);
        highContrastDarkTheme.setFont(new Font("Arial",Font.PLAIN,19));
        highContrastDarkTheme.addActionListener(e ->  setTheme(3));
        panel.add(highContrastDarkTheme);


        JRadioButton highContrastLightTheme = new JRadioButton("High Contrast Light Theme",(int)SettingInfo.theme.getInfo() == 4);
        highContrastLightTheme.setFont(new Font("Arial",Font.PLAIN,19));
        highContrastLightTheme.addActionListener(e -> setTheme(4));
        panel.add(highContrastLightTheme);

        JRadioButton darkTheme = new JRadioButton("dark theme",(int)SettingInfo.theme.getInfo() == 5);
        darkTheme.setFont(new Font("Arial",Font.PLAIN,19));
        darkTheme.addActionListener(e -> setTheme(5));
        panel.add(darkTheme);


        ButtonGroup themes = new ButtonGroup();
        themes.add(lightTheme); themes.add(darkTheme);themes.add(gray_theme);themes.add(solarized_light_theme);
        themes.add(highContrastDarkTheme); themes.add(highContrastLightTheme);themes.add(darkTheme);


        panel.setBorder(BorderFactory.createEmptyBorder(5,10,5,5));
        optionDialog.add(panel);
    }


    /**
     * make the application menu to be added to menu bar.
     * this menu contains:  options and exit.
     * @return application menu
     */
    private JMenu makeApMenu()
    {
       JMenu apMenu =  new JMenu("Application");
        apMenu.setMnemonic('A');


        JMenuItem item1 = new JMenuItem("Options");
        item1.addActionListener(e -> optionDialog.setVisible(true));
        item1.setMnemonic('O');
        item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O , InputEvent.CTRL_MASK));
        apMenu.add(item1);


        JMenuItem item2 = new JMenuItem("Transfer Files");
        item2.addActionListener(e -> transferFileDialog.setVisible(true));
        item2.setMnemonic('F');
        item2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F , InputEvent.CTRL_MASK));
        apMenu.add(item2);


        JMenuItem item3 = new JMenuItem("Exit");
        item3.setMnemonic('E');
        item3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_MASK));

        item3.addActionListener(new ActionListener() {
            private  TrayIcon trayIcon;
            private  SystemTray systemTray;

            @Override
            public void actionPerformed(ActionEvent e) {
                if(getDefaultCloseOperation() == WindowConstants.EXIT_ON_CLOSE)
                {
                    MainDisplayGui.this.dispose();
                    try {
                        SettingInfo.weight.setInfo(getWidth());
                        SettingInfo.height.setInfo(getHeight());
                        SettingInfo.frameX.setInfo(getX());
                        SettingInfo.frameY.setInfo(getY());
                        FileHandler.saveSettings();
                        FileHandler.saveApp(currentWorkspace.getApplication());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    System.exit(0);
                }
                else {
                    configTrayIcon();
                    systemTray = SystemTray.getSystemTray();
                    MainDisplayGui.this.setVisible(false);
                    try {
                        systemTray.add(trayIcon);
                    } catch (AWTException ex) {
                        ex.printStackTrace();
                    }

                }
            }
            private void configTrayIcon() {
                Image icon = Toolkit.getDefaultToolkit().getImage(".\\icons\\icon3.png");
                trayIcon = new TrayIcon(icon, "Insomnia");
                trayIcon.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MainDisplayGui.this.setVisible(true);
                        systemTray.remove(trayIcon);
                    }
                });
                trayIcon.setImageAutoSize(true);
            }
        });

        apMenu.add(item3);
        return apMenu;
    }


    /**
     * make transfer file dialog.
     */
    private void makeTransferFileD()
    {
        transferFileDialog = new JDialog();
        transferFileDialog.setBounds(500,200,600,230);
        transferFileDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();


        JTextField serverText = new JTextField();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.ipadx = 20;
        gbc.ipady = 15;
        gbc.weightx = 1;
        transferFileDialog.add(serverText,gbc);

        JTextField portText = new JTextField();
        gbc.gridy = 1;
        transferFileDialog.add(portText,gbc);

        JLabel tokenLapel = new JLabel("SERVER");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx =0;
        transferFileDialog.add(tokenLapel,gbc);

        JLabel prefixLabel = new JLabel("PORT");
        gbc.gridy = 1;
        transferFileDialog.add(prefixLabel,gbc);

        JButton sendButton = new JButton("send");
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 2;
        transferFileDialog.add(sendButton,gbc);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String server = serverText.getText();
                    int port = Integer.parseInt(portText.getText());
                    new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            Socket socket = new Socket(server,port);
                            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                            outputStream.writeObject(2);
                            outputStream.writeObject(currentWorkspace.getApplication());
                            return "finished";
                        }
                    }.execute();
                    transferFileDialog.setVisible(false);
                }catch (Exception eee)
                {
                    eee.printStackTrace();
                }


            }
        });
    }


    /**
     * make the split panel that is actually the main panel that contains history,body and response panels.
     */
    private void makeSpitedPans()
    {
        leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftPan,centerPan);
        leftSplit.setResizeWeight(0.25);
        leftSplit.setContinuousLayout(true);
        leftSplit.setDividerSize(1);
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftSplit,rightPan);
        splitPane1.setResizeWeight(0.65);
        splitPane1.setDividerSize(1);
        splitPane1.setContinuousLayout(true);
        mainPanUpdate.removeAll();
        mainPanUpdate.add(splitPane1, BorderLayout.CENTER);
        mainPanUpdate.updateUI();
    }


    /**
     * set theme .
     */
    public void setTheme(int indexTheme)
    {
        try {
            switch (indexTheme) {
                case 0:
                case 2:
                    Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/default.xml")).apply(mainControler.getMainDisplayGui().getRightPan().getJsonTextArea());
                    Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/default.xml")).apply(mainControler.getMainDisplayGui().getCenterPan().getJsonTextArea());
                    break;
                case 1:
                    Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/druid.xml")).apply(mainControler.getMainDisplayGui().getRightPan().getJsonTextArea());
                    Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/druid.xml")).apply(mainControler.getMainDisplayGui().getCenterPan().getJsonTextArea());
                    break;
                case 3:
                case 5:
                    Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml")).apply(mainControler.getMainDisplayGui().getRightPan().getJsonTextArea());
                    Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml")).apply(mainControler.getMainDisplayGui().getCenterPan().getJsonTextArea());
                    break;
                case 4:
                    Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml")).apply(mainControler.getMainDisplayGui().getRightPan().getJsonTextArea());
                    Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml")).apply(mainControler.getMainDisplayGui().getCenterPan().getJsonTextArea());
                    break;
            }
        }catch (NullPointerException | IOException ignored){ }finally {
            switch (indexTheme) {
                case 0:
                    LafManager.install(new IntelliJTheme());
                    break;
                case 1:
                    LafManager.install(new SolarizedDarkTheme());
                    break;
                case 2:
                    LafManager.install(new SolarizedLightTheme());
                    break;
                case 3:
                    LafManager.install(new HighContrastDarkTheme());
                    break;
                case 4:
                    LafManager.install(new HighContrastLightTheme());
                    break;
                case 5:
                    LafManager.install(new OneDarkTheme());
            }
        }

        SettingInfo.theme.setInfo(indexTheme);
    }


    /**
     * get background color for text area in each kind of theme.
     * @param kind kind of them. there are 6 kind of theme.
     * @return color of text area for this theme.
     */
    public Color themeTextColor (int kind)
    {
        switch (kind) {
            case 0:
                return  new Color(255, 255, 255);
            case 1:
                return  new Color(0, 43, 54);
            case 2:
                return  new Color(253, 246, 227);
            case 3:
                return  new Color(19, 19, 20);
            case 4:
                return  new Color(235, 235, 236);
            case 5:
                return  new Color(40, 44, 52);
        }
        return null;
    }


    /**
     * get center panel _ body panel.
     * @return center panel _ body panel
     */
    public BodyPanelGui getCenterPan() {
        return centerPan;
    }

    /**
     * get right panel _ response panel.
     * @return right panel _ response panel.
     */
    public ResponsePanelGui getRightPan() {
        return rightPan;
    }

    /**
     * get left panel _ history panel.
     * @return left panel _ history panel.
     */
    public HistoryPanGui getLeftPan() {
        return leftPan;
    }

}
