package Security;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Date;

public final class SimpleLogger {

    private static final String path="Logs\\";

    public static void logAction(String className, String action){
        File logFile=new File(path+new Date().getTime()+".txt");
        ObjectOutputStream output;
        FileOutputStream fos = null;

        try {

            //System.out.println("Creating new log file");
            Files.createDirectories(Path.of(path));
            fos = new FileOutputStream(logFile, true);
            output = new ObjectOutputStream(fos){
                @Override
                public void writeStreamHeader() {//removing stream header to keep log content clean
                }
            };

            output.writeObject(LocalDateTime.now().toString());
            output.writeObject("Class - "+className);
            output.writeObject("Action - "+action);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
