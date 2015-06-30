package Server;

import java.io.*;

/**
 * Created by tareq.aziz on 6/30/15.
 */
public class FileServiceUtility {

    public static boolean writeFile(String filepath, String toBeWritten) {
        File file = new File(filepath);
        boolean writeSuccess = false;
        try {
            writeSuccess = writeFile(file, toBeWritten);
        } catch (FileNotFoundException e) {
            System.out.println("Error : could not found file " + filepath);
        } catch (IOException e) {
            System.out.println("Error : could not create file " + filepath);
        }
        return writeSuccess;
    }
    private static boolean writeFile(File file, String toBeWritten) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(toBeWritten.getBytes());
        outputStream.close();
        return true;
    }
}
