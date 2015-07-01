package Server;

import java.io.*;

/**
 * Created by tareq.aziz on 6/30/15.
 */
public class FileServiceUtility {

    public static String readFile(String filePath) {
        String fileContent = "";
        try {

            FileReader fileReader = new FileReader(filePath);
            fileContent = readFile(fileReader);

        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + filePath + "'");
        } catch (IOException ex) {
            System.out.println("Error : Can not read " + filePath);
        } finally {
            return fileContent;
        }
    }


    private static String readFile(FileReader fileReader) throws IOException {
        BufferedReader reader = new BufferedReader(fileReader);
        String temp = "";
        String fileContent = "";
        while ((temp = reader.readLine()) != null) {
            fileContent += temp;
        }
        reader.close();
        return fileContent;
    }
}
