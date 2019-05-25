import java.io.*;

/**
 * Class to support logging errors
 */
public class Error {
    File log;
    BufferedWriter fileLog;

    /**
     * @param fileName name of log file
     */
    Error(String fileName) {
        log = new File(fileName);

        if (!log.exists()) {
            try {
                log.createNewFile();
            }
            catch (IOException exc) {
                exc.printStackTrace();
            }
        }
        try {
            FileWriter f = new FileWriter(fileName);
            fileLog = new BufferedWriter(f);
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * @param msg error message
     */
    void message(String msg) {
        try {
            fileLog.write(msg);
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
