package GUI;
import MainPart.Application;
import Controll.MainControler;
/**
 * this program is a HTTP client, enable user to do whatever he could do in http client of console in GUI.
 * @author farzad
 */
public class Run {
    public static void main(String[] args) {

        Application application = new Application();
        MainControler mainControler =  new MainControler(application);
    }
}
