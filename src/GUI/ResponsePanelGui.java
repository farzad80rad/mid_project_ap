package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

import Consol.ReqSender;
import Controll.ResponseController;
import MainPart.TimeElapse;
import MainPart.Workspace;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.components.text.NumberedTextComponent;
import com.github.weisj.darklaf.theme.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;

/**
 * define the response panel where the response is showed.
 * contains : body massage , header panels and overall state of request and respond.
 */
public class ResponsePanelGui extends JPanel {

    Workspace currentWorkspace;
    Font font = new Font("Arial",Font.PLAIN,12);
    Font fontLeader = new Font("Arial",Font.BOLD,12);
    JPanel headerPanel ;
    RSyntaxTextArea jsonTextArea ;
    ResponseController responseController ;


    /**
     * simple constructor.
     * @param currentWorkspace current workspace of this app.
     */
    public ResponsePanelGui(Workspace currentWorkspace, ResponseController responseController)
    {
        this.responseController = responseController;
        this.currentWorkspace = currentWorkspace;
        setLayout( new BorderLayout());
        makeTopPart();
        makeCenterPart();
        responseController.setResponsePanelGui(this);
    }

    /**
     * rebuild this panel.
     */
    public void rebuild()
    {
        removeAll();
        add(new JLabel("preparing to show ... ",JLabel.CENTER));
        updateUI();
        makeTopPart();
        makeCenterPart();
        remove(0);
        updateUI();
    }

    /**
     * rebuild this panel.
     */
    public void rebuildTopPart()
    {
        remove(0);
        makeTopPart();
        updateUI();
    }


    /**
     * rebuild response panel using swing worker.
     * if content of response is large , it would take some seconds to
     * build panel for it. so for large response is recommended to use this method.
     */
    public void rebuildWithSwingWorker()
    {
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    removeAll();
                    add(new JLabel("prepare to show ... ", JLabel.CENTER));
                    updateUI();
                    makeTopPart();
                    makeCenterPart();
                    remove(0);
                    updateUI();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }


    /**
     * make the center part which contains: body massage and header panels.
     * if there is no active request the panel would be blank.
     */
    public void makeCenterPart()
    {
            if (currentWorkspace.getCurrentReq() == null) {
                add(new JPanel(), BorderLayout.CENTER);
                return;
            }
        if(currentWorkspace.getCurrentReq().getResponse() != null) {
            if(currentWorkspace.getCurrentReq().getResponse().getBodyRes().length == 0)
                currentWorkspace.getCurrentReq().getResponse().setBodyRes(" ".getBytes());


            JPanel body = makeBodyTab();
            JPanel header = makeHeaderTab();
            JScrollPane scrollPane = new JScrollPane(header);
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.add(body, "BODY");
            tabbedPane.add(scrollPane, "HEADER");
            add(tabbedPane, BorderLayout.CENTER);
        }
        else
            add(new JPanel(), BorderLayout.CENTER);

    }

    /**
     * make the text part for tab.
     * @return JPanel that has a text field .
     */
    private JPanel makeBodyTab ()
    {
        JPanel jPanel = new JPanel(new BorderLayout());
        JPanel comboHolder = new JPanel(new BorderLayout());
        JComboBox comboBox = new JComboBox(new String[]{"raw","preview","JSON"});
        comboBox.setSelectedIndex(currentWorkspace.getCurrentReq().getResponseKind() - 1);
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    currentWorkspace.getCurrentReq().setResponseKind(e.getItem().equals("preview") ? 2 : e.getItem().equals("raw") ? 1 : 3);
                    rebuildWithSwingWorker();
                    updateUI();
                }
            }
        });
        comboHolder.add(comboBox);
        comboHolder.setBorder( BorderFactory.createEmptyBorder(6,5,15,5));
        jPanel.add(comboHolder,BorderLayout.NORTH);

        if(Objects.equals(comboBox.getSelectedItem(), "preview")) {
            JTextPane editorPane = new JTextPane();
            editorPane.setEditable(false);
            Map<String, List<String>> headers = currentWorkspace.getCurrentReq().getResponse().getHeaderRes();

            if(headers.get("content-type") == null)
                editorPane.setText(new String(currentWorkspace.getCurrentReq().getResponse().getBodyRes()));

            else if(headers.get("content-type").toString().contains("text/html")) {
                editorPane.setContentType("text/html");
                editorPane.setText(new String(currentWorkspace.getCurrentReq().getResponse().getBodyRes()));
            }
            else if(headers.get("content-type").toString().contains("image"))
            {
                ImageIcon imageIcon = new ImageIcon(currentWorkspace.getCurrentReq().getResponse().getBodyRes());
                editorPane.insertIcon(imageIcon);
            }
            else {
                editorPane.setText(new String(currentWorkspace.getCurrentReq().getResponse().getBodyRes()));
            }

            editorPane.setFont(font);
            JScrollPane scrollPane = new JScrollPane(editorPane);
            jPanel.add(scrollPane);
        }
        else if(Objects.equals(comboBox.getSelectedItem(), "JSON"))
        {
            jsonTextArea = new RSyntaxTextArea();
            jsonTextArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JSON);
            jsonTextArea.setText(makeJsonString(new String(currentWorkspace.getCurrentReq().getResponse().getBodyRes())));
            jsonTextArea.setEditable(false);
            jsonTextArea.setCodeFoldingEnabled(true);
            currentWorkspace.getApplication().getMainController().getMainDisplayGui().setTheme((int)SettingInfo.theme.getInfo());
            NumberedTextComponent scrollPane = new NumberedTextComponent(jsonTextArea);
            jPanel.add(scrollPane);
        }
        else {
            JTextArea textArea = new JTextArea();
            textArea.setText(new String(currentWorkspace.getCurrentReq().getResponse().getBodyRes()));
            textArea.setFont(font);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(5,10,5,5));
            jPanel.add(scrollPane);
        }

        return jPanel;
    }


    /**
     * convert raw json string which is in a simple line into readable and in a good shape.
     * @param rawJsonString string to be converted.
     * @return json string that is in style.
     */
    private String makeJsonString(String rawJsonString)
    {
        StringBuilder spaceBuilder = new StringBuilder();
        StringBuilder jsonString = new StringBuilder();
        if(rawJsonString.charAt(rawJsonString.indexOf(",")+1) == '\n')
            return rawJsonString;
        for(char currentChar : rawJsonString.toCharArray())
        {
            switch (currentChar)
            {
                case '{':
                    jsonString.append("\n").append(spaceBuilder);
                    spaceBuilder.append("\t");
                    jsonString.append("{\n").append(spaceBuilder);
                    break;

                case '}':
                    spaceBuilder.deleteCharAt(0);
                    if(jsonString.charAt(jsonString.length() - 1) != '\t')
                        jsonString.append("\n").append(spaceBuilder).append("}");
                    else
                    {
                        jsonString.delete(jsonString.length() - spaceBuilder.length() - 2,jsonString.length());
                        jsonString.append("}");
                    }

                    break;

                case ',':
                    String currentJsonLine = jsonString.toString().trim();
                    if(currentJsonLine.endsWith("\"") || currentJsonLine.endsWith("}") || currentJsonLine.endsWith("]")) {
                        jsonString.append(",").append("\n");
                        jsonString.append(spaceBuilder);
                    }
                    else
                        jsonString.append(currentChar);
                    break;

                default:
                    jsonString.append(currentChar);
            }
        }
        return jsonString.toString();
    }

    /**
     * make header panel to be added to center tab .
     * @return header panel.
     */
    private JPanel makeHeaderTab()
    {
        JPanel mainPanel = new JPanel(new BorderLayout());
        headerPanel = new JPanel();
        buildHeaderPan();
        mainPanel.setBorder( BorderFactory.createEmptyBorder(15,5,5,5));
        mainPanel.add(headerPanel,BorderLayout.NORTH);
        return mainPanel;
    }


    /**
     * build header panel .
     */
    private void buildHeaderPan ()
    {
        headerPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(headerPanel ,BoxLayout.Y_AXIS );
        headerPanel.setLayout(boxLayout);
        JPanel leadingPan = new JPanel( new GridLayout(1,2));
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(fontLeader);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueLabel = new JLabel("Value");
        valueLabel.setFont(fontLeader);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leadingPan.add(nameLabel); leadingPan.add(valueLabel);
        headerPanel.add(leadingPan);

        int count =0;
        for(Map.Entry<String, List<String>> header : currentWorkspace.getCurrentReq().getResponse().getHeaderRes().entrySet()) {
            headerPanel.add(makeHeaderPiece(header.getKey(), header.getValue().toString(),count));
            count ++;
        }


        JButton copyBt =  new JButton("copy to clipboard");
        copyBt.setFont( new Font("Arial",Font.BOLD,12));
        copyBt.setPreferredSize(new Dimension(60,35));
        headerPanel.add(copyBt);
        copyBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder stringCopy = new StringBuilder();
                boolean skipFirstPan = true;
                for(Component component : headerPanel.getComponents())
                {
                    if(!(component instanceof JPanel) || skipFirstPan) {
                        skipFirstPan = false;
                        continue;
                    }

                    stringCopy.append( (((JTextField) ((JPanel) component).getComponent(0)).getText()));
                    stringCopy.append( "=");
                    stringCopy.append( (( (JTextField) ((JPanel)component).getComponent(1) ).getText()) );
                    stringCopy.append("&");

                }
                StringSelection stringSelection = new StringSelection("" + stringCopy.deleteCharAt(stringCopy.length() - 1));
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection,null);
            }
        });

    }


    /**
     * make a piece of header which makes the header panel together.
     * @return piece of header panel.
     */
    private JPanel makeHeaderPiece(String name, String value, int turn)
    {
        JPanel panel = new JPanel( new GridLayout(1,2));
        JTextArea headerLabel = new JTextArea(name);
        headerLabel.setLineWrap(true);
        headerLabel.setFont(font);
        headerLabel.setEditable(false);
        headerLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1,2,1,1),BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(turn%2==0?Color.GRAY:Color.BLACK,2,true),BorderFactory.createEmptyBorder(5,5,5,5))));
        JTextArea valueLabel = new JTextArea(value);
        valueLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1,2,1,1),BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(turn%2==0?Color.GRAY:Color.BLACK,2,true),BorderFactory.createEmptyBorder(5,5,5,5))));
        valueLabel.setLineWrap(true);
        valueLabel.setFont(font);
        valueLabel.setEditable(false);

        panel.add(headerLabel);
        panel.add(valueLabel);

        return panel;
    }


    /**
     * make the status line that shows : status code, time of operation and amount of download.
     */
    private void makeTopPart()
    {
        if(currentWorkspace.getCurrentReq() == null)
            return;
        if(currentWorkspace.getCurrentReq().getResponse() == null)
            return;
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
        flowLayout.setHgap(10);
        JPanel panel = new JPanel(flowLayout);
        panel.setBorder( BorderFactory.createEmptyBorder(5,5,5,5));
        panel.setPreferredSize(new Dimension(50,65));

        System.out.println("1");
        int statusCode = currentWorkspace.getCurrentReq().getResponse().getStatusCode();
        JTextField statusCodeArea = new JTextField( "" + statusCode + " " + ReqSender.statusCodeMessage(statusCode));
        statusCodeArea.setFocusable(false);
        if(currentWorkspace.getCurrentReq().getResponse().getStatusCode() / 100 == 2)
            statusCodeArea.setBackground( new Color(80, 165, 0));
        else
            statusCodeArea.setBackground( new Color(225,82,81));

        System.out.println("2");

        statusCodeArea.setForeground(Color.white);
        statusCodeArea.setFont( new Font("Arial",Font.BOLD,12));
        statusCodeArea.setHorizontalAlignment(SwingConstants.CENTER);
        statusCodeArea.setBorder( BorderFactory.createLineBorder(Color.BLACK,2,true));
        statusCodeArea.setPreferredSize(new Dimension(statusCodeArea.getText().length() * 10,33));

        Color colorLabels =  new Color(224,224,224);

        System.out.println("3");

        JTextField timeTaken = new JTextField("time " + currentWorkspace.getCurrentReq().getTimeElapsed() +" ms");
        timeTaken.setFocusable(false);
        timeTaken.setForeground(Color.BLACK);
        timeTaken.setBackground(colorLabels);
        timeTaken.setFont( new Font("Arial",Font.BOLD,12));
        timeTaken.setHorizontalAlignment(SwingConstants.CENTER);
        timeTaken.setBorder( BorderFactory.createLineBorder(Color.BLACK,2,true));
        timeTaken.setPreferredSize(new Dimension(timeTaken.getText().length() * 10,33));

        System.out.println("4");

        JTextField downloadMount = new JTextField( currentWorkspace.getCurrentReq().getResponse().getBodyRes().length / 1000f + " KB");
        downloadMount.setForeground(Color.BLACK);
        downloadMount.setFocusable(false);
        downloadMount.setBackground(colorLabels);
        downloadMount.setFont( new Font("Arial",Font.BOLD,12));
        downloadMount.setHorizontalAlignment(SwingConstants.CENTER);
        downloadMount.setBorder( BorderFactory.createLineBorder(Color.BLACK,2,true));
        downloadMount.setPreferredSize(new Dimension(downloadMount.getText().length() * 10,33));

        JLabel currentReqName = new JLabel(currentWorkspace.getCurrentReq().getName().trim().substring(1));
        currentReqName.setFont(new Font("simple",Font.BOLD,13));
        currentReqName.setBorder( BorderFactory.createLineBorder(Color.black,2,true));
        currentReqName.setHorizontalAlignment(SwingConstants.CENTER);
        currentReqName.setPreferredSize(new Dimension(currentReqName.getText().trim().length() * 9,33));

        System.out.println("5");
        panel.add(statusCodeArea);
        panel.add(timeTaken);
        panel.add(downloadMount);
        panel.add(currentReqName);

        add(panel,BorderLayout.NORTH);
        System.out.println("6");
    }


    /**
     * get json text area.
     * @return  json text area that response is showed on.
     */
    public RSyntaxTextArea getJsonTextArea() {
        return jsonTextArea;
    }
}
