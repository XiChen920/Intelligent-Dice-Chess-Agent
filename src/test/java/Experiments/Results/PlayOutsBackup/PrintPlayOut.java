package Experiments.Results.PlayOutsBackup;

import java.io.*;

public class PrintPlayOut {
    public static File dir = new File("src/test/java/Experiments/Results/PlayOutsBackup");
    public static void print(Serializable serializable, String name){
        try {
            File file = new File(dir, name);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(serializable);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e){
            System.out.println(e.getStackTrace());
        }
    }
}
