package User.Agent.ANN.MLP;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

class ExperimentLogger
{
    static String fileName;
    /**
     *
     * @param experimentName Name of the file to write to. If not present, a new file will be created
     * @param data Comma seperated strings contained within an array, such that each comma seperates a block of data
     */
    public static void logToCSV(String experimentName, String[] data)
    {
        try
        {
            fileName = experimentName.concat(".csv");
            String filePath = findFilePath(fileName);
            File file = new File(filePath);

            if(!file.exists())
            {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file, true);
            for(int i = 0; i < data.length; i++)
            {
                writer.write(data[i] + ",");
            }
            writer.append("\n");
            writer.close();
        }
        catch(IOException e)
        {
            System.out.println("File not found");
            e.printStackTrace();
        }
    }

    public static String findFilePath(String fileName)
    {
        FileSystem fileSystem = FileSystems.getDefault();
        String path = fileSystem.getPath("").toAbsolutePath().toString();
        return path.concat("/src/main/java/User/Agent/ANN/MLP/HyperParameterTesting/" + fileName);
    }
}