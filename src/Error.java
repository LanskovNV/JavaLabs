package Source;

import java.io.*;

public class Error {

    public Error() {
        String logName = "log.txt";
        File log = new File(logName);

        if (!log.exists()) {
            try {
                log.createNewFile();
            }
            catch (IOException exc) {
                exc.printStackTrace();
            }
        }

        try {
            fileWriter = new FileWriter(logName);
            bufWriter = new BufferedWriter(fileWriter);
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void message(String msg) {
        try {
            bufWriter.write("ERROR: " + msg + "\n");
            bufWriter.flush();
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void close() {
        try {
            bufWriter.close();
            fileWriter.close();
        } catch (IOException ex) {
            try {
                bufWriter.write("Err:" + ex.getMessage());
                bufWriter.flush();
            }
            catch (IOException exc) {
                exc.printStackTrace();
            }
        }
    }

    private static BufferedWriter bufWriter;
    private static FileWriter fileWriter;

}
