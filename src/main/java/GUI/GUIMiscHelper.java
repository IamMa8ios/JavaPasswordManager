package GUI;

import javax.swing.*;

public final class GUIMiscHelper {

    public static int[] readPartNums(String message){
        //get the part numbers for the key parts
        String reply=JOptionPane.showInputDialog(null, message);

        if(reply!=null){
            //split with :
            String[] partNumsText=reply.split(":");
            //array containing key part locations in db
            int[] partNums=new int[partNumsText.length];
            //convert locations from string to int
            try{
                for(int i=0;i<partNums.length;i++){
                    partNums[i]=Integer.parseInt(partNumsText[i]);
                }
            }catch (NumberFormatException nfe){
                nfe.printStackTrace();
            }

            return partNums;
        }
        return null;
    }

    public static int getPasswordSize(){
        String reply=JOptionPane.showInputDialog(null, "Insert the number of characters for your password");
        int size=-1;

        try {
            size=Integer.parseInt(reply);
        }catch (NumberFormatException nfe){
            nfe.printStackTrace();
        }

        return size;
    }

}
