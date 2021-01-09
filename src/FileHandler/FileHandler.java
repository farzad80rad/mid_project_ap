package FileHandler;


import java.io.*;
import GUI.SettingInfo;
import MainPart.Application;


/**
 * this class is for saving the app info in a file and also read those info.
 */
public class FileHandler {

    private final static String PATH_DIRECTION = "././FileSaving";

    /**
     * save the hole info of app includes reqs, folders and workspaces.
     * the saving file's path is ".\\FileSaving\\appInfo.bin".
     * @param application application to be saved.
     * @throws IOException this may happen because you are working with files.
     */
    public static void saveApp(Application application) throws IOException  {
        new File(PATH_DIRECTION).mkdir();

        try (FileOutputStream file = new FileOutputStream(PATH_DIRECTION + "/appInfo.bin");
             ObjectOutputStream outputStream =  new ObjectOutputStream(file);){
            outputStream.writeObject(application);
        }
    }

    /**
     * load the app that has been saved in path of ".\\FileSaving\\appInfo.bin".
     * @return the saved application.
     * @throws IOException if any failure happen.
     * @throws ClassNotFoundException this method temps to read an object, if the saved object doesnt match with
     *      * this program "Application" class, this Exception would happen.
     */
    public static Application readApp () throws IOException, ClassNotFoundException {
        try ( FileInputStream file = new FileInputStream(PATH_DIRECTION + "\\appInfo.bin");
            ObjectInputStream inputStream = new ObjectInputStream(file);){
            return (Application) inputStream.readObject();
        }
    }


    /**
     * save settings of this program  gui pan  like size of panel, activation of toggle side bar , theme and ...
     * the saving path is ".\\FileSaving\\settings.bin".
     * @throws IOException if any trouble happen.
     */
    public static void saveSettings() throws IOException {

        new File(PATH_DIRECTION).mkdir();

        try (FileOutputStream file = new FileOutputStream(PATH_DIRECTION + "\\settings.bin");
             DataOutputStream outputStream = new DataOutputStream(file);){

            outputStream.writeInt((int) SettingInfo.theme.getInfo());
            outputStream.writeInt((int) SettingInfo.weight.getInfo());
            outputStream.writeInt((int) SettingInfo.height.getInfo());
            outputStream.writeInt((int) SettingInfo.frameX.getInfo());
            outputStream.writeInt((int) SettingInfo.frameY.getInfo());
            outputStream.writeBoolean((boolean) SettingInfo.terminateOnExit.getInfo());
            outputStream.writeBoolean((boolean) SettingInfo.flowReDirect.getInfo());
            outputStream.writeBoolean((boolean) SettingInfo.toggleSideBar.getInfo());
            outputStream.writeBoolean((boolean) SettingInfo.fullScreen.getInfo());
        }
    }


    /**
     * load settings of this program  gui pan  like size of panel, activation of toggle side bar , theme and ...
     * the loading path is ".\\FileSaving\\settings.bin".
     * @throws IOException if eny trouble happen.
     */
    public static void readSettings( ) throws IOException {

        File makingFile = new File(PATH_DIRECTION + "\\settings.bin");
        makingFile.createNewFile();

        try (FileInputStream file = new FileInputStream(makingFile.getAbsolutePath());
             DataInputStream  dataInputStream = new DataInputStream(file);){

            SettingInfo.theme.setInfo(dataInputStream.readInt());
            SettingInfo.weight.setInfo(dataInputStream.readInt());
            SettingInfo.height.setInfo(dataInputStream.readInt());
            SettingInfo.frameX.setInfo(dataInputStream.readInt());
            SettingInfo.frameY.setInfo(dataInputStream.readInt());
            SettingInfo.terminateOnExit.setInfo(dataInputStream.readBoolean());
            SettingInfo.flowReDirect.setInfo(dataInputStream.readBoolean());
            SettingInfo.toggleSideBar.setInfo(dataInputStream.readBoolean());
            SettingInfo.fullScreen.setInfo(dataInputStream.readBoolean());

        }

    }

    /**
     * set default settings for settings of this program.
     */
    public static  void defaultSetting()
    {
        SettingInfo.theme.setInfo(2);
        SettingInfo.weight.setInfo(1600) ;
        SettingInfo.height.setInfo(1000);
        SettingInfo.frameY.setInfo(50);
        SettingInfo.frameX.setInfo(100);
        SettingInfo.terminateOnExit.setInfo(true) ;
        SettingInfo.flowReDirect.setInfo(true) ;
        SettingInfo.toggleSideBar.setInfo(false);
        SettingInfo.fullScreen.setInfo(false);
    }


}
