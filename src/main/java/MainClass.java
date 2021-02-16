import DBUtils.DBManager;
import GUI.HomePage;

public final class MainClass {

    public static void main(String[] args) {
        DBManager.initDB();
        new HomePage();
    }

}
