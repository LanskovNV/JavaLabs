import java.io.*;

public class Error {
    File log;
    BufferedWriter fileLog;

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

    void message(String msg) {
        try {
            fileLog.write(msg);
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
