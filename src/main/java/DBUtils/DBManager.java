package DBUtils;

import GUI.GUIMiscHelper;
import GUI.Row;
import Security.MattSecurityHelper;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public final class DBManager {

    private static final int NUMBER_OF_DUMMY_DATA=1000;

    /**
     * Set up a new Database if no database previously existed
     */
    public static boolean initDB(){
        try{
            Class.forName("org.sqlite.JDBC");

            Connection conn= DriverManager.getConnection("jdbc:sqlite:data.db");

            Statement stat = conn.createStatement();

            //System.out.println ("initializing db");

            conn.setAutoCommit(false);

            //stat.executeUpdate("DROP table if exists KeyTable;");
            //stat.executeUpdate("DROP table if exists DataTable;");
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS KeyTable(serialNo INT, part VARBINARY);");
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS DataTable(appName VARCHAR, username VARCHAR, pass VARBINARY);");
            conn.commit();

            PreparedStatement statement=conn.prepareStatement("SELECT COUNT(serialNo) FROM Keytable;");
            ResultSet resultSet=statement.executeQuery();
            
            if(resultSet.next()){
                if(resultSet.getInt(1)==0) {
                    byte[] key=MattSecurityHelper.createSymmetricKey();
                    //System.out.println("size before storage "+key.length);
                    //System.out.println("size before storage "+key.length);
                    storeKey(conn, key);
                }else if(resultSet.getInt(1)!=NUMBER_OF_DUMMY_DATA){
                    JOptionPane.showMessageDialog(null, "Someone has tampered with the encryption data", "WARNING", JOptionPane.WARNING_MESSAGE);
                    byte[] key=MattSecurityHelper.createSymmetricKey();
                    //System.out.println("size before storage "+key.length);
                    //System.out.println("size before storage "+key.length);
                    storeKey(conn, key);
                }
            }


            conn.close();

            return true;

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Store the encryption key in the database. First the the user is asked for the positions to store the key.
     * Depending on the answer the key is split in the number of parts needed and each part is given a number to be
     * stored in the database.
     * @param conn - the connection that provides the database
     * @param key - Encryption key in byte[] format in order to be split
     */
    private static void storeKey(Connection conn, byte[] key){

        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO KeyTable(serialNo, part) VALUES(?, ?)");

            //get the numbers typed from the user
            int[] partNums=GUIMiscHelper.readPartNums("Insert a sequence of numbers for your new encryption key (E.g. 1:2:3:4)");

            if(partNums!=null){

                //split the key in the parts determined by the user
                ArrayList<byte[]> parts=splitKey(key, partNums.length);
                //list containing both key parts and dummy data
                ArrayList<byte[]> partsList=new ArrayList<>();

                //add dummy data
                SecureRandom secureRandom=new SecureRandom();
                byte[] temp;
                for(int i=0;i<NUMBER_OF_DUMMY_DATA;i++){
                    temp=new byte[10];
                    secureRandom.nextBytes(temp);
                    partsList.add(temp);
                }

                //add key parts in appropriate positions
                for(int i=0;i<partNums.length;i++){
                    partsList.set(partNums[i], parts.get(i));
                }

                //store parts in database
                for(int i=0;i<NUMBER_OF_DUMMY_DATA;i++){
                    preparedStatement.setInt(1, i);
                    preparedStatement.setBytes(2, partsList.get(i));
                    preparedStatement.executeUpdate();
                    conn.commit();
                }

            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    /**
     * Check if the array of part numbers contains a specific part number
     * @param partNum - the part number to check
     * @param partNums - array of necessary part numbers
     * @return location of part number in the array or -1 if the part number is not in the array
     */
    private static int partNumExists(int partNum, int[] partNums){
        for(int i=0;i<partNums.length;i++){
            if(partNum==partNums[i]){
                return i;
            }
        }
        return -1;
    }

    /**
     * Split the key in parts and store in ArrayList
     * @param key - key to split
     * @param numOfParts - number of parts to split into
     * @return ArrayList containing all parts
     */
    private static ArrayList<byte[]> splitKey(byte[] key, int numOfParts){
        ArrayList<byte[]>parts=new ArrayList<>();
        //System.out.println("initial size "+key.length);
        //System.out.println("splitting in ");
        int totalSize=0;
        for(int i=0;i<numOfParts;i++){
            if(i==numOfParts-1){
                parts.add(Arrays.copyOfRange(key, (key.length/numOfParts)*i, key.length));
                //System.out.println((key.length/numOfParts)*i +" and "+ key.length);
                totalSize+=parts.get(i).length;
                continue;
            }
            parts.add(Arrays.copyOfRange(key, (key.length/numOfParts)*i, (key.length/numOfParts)*(i+1)));
            totalSize+=parts.get(i).length;
            //System.out.println((key.length/numOfParts)*i + " and " + (key.length/numOfParts)*(i+1));
        }
        //System.out.println("total size "+totalSize);

        return parts;
    }

    /**
     * Retrieve a key part with the specified part number
     * @param partNo - part number for the requested key
     * @return a key part
     */
    private static byte[] getKeyPart(int partNo){

        try{
            Class.forName("org.sqlite.JDBC");

            Connection conn= DriverManager.getConnection("jdbc:sqlite:data.db");

            //System.out.println ("Getting key parts");

            //get key from part number
            PreparedStatement stat = conn.prepareStatement("SELECT part FROM KeyTable WHERE serialNo=?");
            stat.setInt(1, partNo);
            ResultSet result=stat.executeQuery();

            //part of key with given partNum
            byte[] part=null;

            if(result.next()){
                part=result.getBytes(1);
            }

            conn.close();

            return part;

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieve the full key in the byte[] format that comes from combining all key parts with the part numbers provided
     * @param partNums - array of part numbers to retrieve from database
     * @return a key in byte[] format
     */
    private static byte[] loadKey(int[] partNums){

        ByteArrayOutputStream parts = new ByteArrayOutputStream();

        //System.out.println(partNums.length);

        //get parts from db
        for(int i=0;i<partNums.length;i++){
            try {
                parts.write(getKeyPart(partNums[i]));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        byte[] key=parts.toByteArray();

        return key;
    }

    /**
     * Load all the data that the user has created in the database
     * @return a list containing the user's data split in display rows
     */
    public static ArrayList<Row> loadData(){
        ArrayList<Row> rows=new ArrayList<>();

        try{
            Class.forName("org.sqlite.JDBC");

            Connection conn= DriverManager.getConnection("jdbc:sqlite:data.db");

            //System.out.println ("loading data");

            PreparedStatement stat = conn.prepareStatement("SELECT * FROM DataTable");
            ResultSet result=stat.executeQuery();

            while(result.next()){
                rows.add(new Row(result.getString(1), result.getString(2), result.getBytes(3)));
            }

            stat.close();
            conn.close();

            return rows;

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Store data given from user.
     * @param appName
     * @param username
     * @param pass
     * @param partNums
     * @param editMode - whether the db should update existing data or insert new
     * @return a boolean to indicate if the data was stored successfully
     */
    public static boolean saveData(String appName, String username, String pass, int[] partNums, boolean editMode){
        try{
            Class.forName("org.sqlite.JDBC");

            Connection conn= DriverManager.getConnection("jdbc:sqlite:data.db");

            //System.out.println ("inserting data");

            //create Secret Key
            SecretKey secretKey=new SecretKeySpec(loadKey(partNums), "AES");
            byte[] encryptedPass=MattSecurityHelper.encrypt(pass, secretKey);

            if((dataExists(appName, username) && editMode)){
                PreparedStatement stat = conn.prepareStatement("UPDATE DataTable SET pass=? WHERE appName=? AND username=?;");
                stat.setString(2, appName);
                stat.setString(3, username);
                stat.setBytes(1, encryptedPass);
                stat.executeUpdate();

                stat.close();
                conn.close();
                return true;
            }else if(!editMode && !dataExists(appName, username)){
                PreparedStatement stat = conn.prepareStatement("INSERT INTO DataTable(appName, username, pass) VALUES(?, ?, ?);");
                stat.setString(1, appName);
                stat.setString(2, username);
                stat.setBytes(3, encryptedPass);
                stat.executeUpdate();

                stat.close();
                conn.close();
                conn.close();
                return true;
            }

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Delete data matching the parameters
     * @param appName
     * @param username
     * @return a boolean to indicate if the data was deleted successfully
     */
    public static boolean deleteData(String appName, String username){

        try{
            Class.forName("org.sqlite.JDBC");

            Connection conn= DriverManager.getConnection("jdbc:sqlite:data.db");

            //System.out.println ("deleting data");

            if(dataExists(appName, username)){
                PreparedStatement stat = conn.prepareStatement("DELETE FROM DataTable WHERE appName=? AND username=?");

                stat.setString(1, appName);
                stat.setString(2, username);
                stat.executeUpdate();

                stat.close();
                conn.close();

                return true;
            }

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Check if parameters exist in database
     * @param appName
     * @param username
     * @return a boolean to indicate if the data exists
     */
    private static boolean dataExists(String appName, String username){

        try{
            Class.forName("org.sqlite.JDBC");

            Connection conn= DriverManager.getConnection("jdbc:sqlite:data.db");

            //System.out.println ("checking for existing data");

            PreparedStatement stat = conn.prepareStatement("SELECT username FROM DataTable WHERE appName=? AND username=?");

            stat.setString(1, appName);
            stat.setString(2, username);
            ResultSet results=stat.executeQuery();

            if(results.next()){
                stat.close();
                conn.close();
                return true;
            }

            stat.close();
            conn.close();

            return false;

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return true;
    }

    /**
     * Retrieve password from database and copy to clipboard for parameters
     * @param appName
     * @param username
     * @return a plaintext String if data exists or null
     */
    public static String getPass(String appName, String username, int[] serialNums){
        try{
            Class.forName("org.sqlite.JDBC");

            Connection conn= DriverManager.getConnection("jdbc:sqlite:data.db");

            //System.out.println ("loading password");

            PreparedStatement stat = conn.prepareStatement("SELECT pass FROM DataTable WHERE appName=? AND username=?;");
            stat.setString(1, appName);
            stat.setString(2, username);
            ResultSet result=stat.executeQuery();

            SecretKey secretKey=new SecretKeySpec(loadKey(serialNums), "AES");
            String decryptedPass=null;

            if(result.next()){
                decryptedPass=(String) MattSecurityHelper.decrypt(result.getBytes(1), secretKey);
                StringSelection selection = new StringSelection(decryptedPass);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }

            stat.close();
            conn.close();

            return decryptedPass;

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
