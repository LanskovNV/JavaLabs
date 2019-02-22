import java.io.*;

public class ErrorLog {

    /**
     * Inits log
     * @return 0 if success, otherwise 1
     */
    public static int Init() {
        log = new File(fileLogName);

        if(!log.exists()) {
            try {
                log.createNewFile();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return 1;
            }
        }
        if (createLogWriter() != 0)
            return 1;
        return 0;
    }

    /**
     * Opens output streams
     * @return 0 if success, otherwise 1
     */
    private static int createLogWriter() {
        try {
            logWriter = new FileWriter(log);
            logBufWriter = new BufferedWriter(logWriter);
            return 0;
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
            return 1;
        }
    }

    /**
     * Prints message to log
     * @param message x message which needs to be sent to the log file
     */
    public static void sendMessage(String message) {
        try {
            logBufWriter.write("Err: " + message + "\n");
            logBufWriter.flush();
        }
        catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
    }

    /**
     * Closes log file
     */
    public static void close() {
        try {
            logBufWriter.close();
        } catch (IOException ex) {
            try {
                logBufWriter.write("Err:" + ex.getMessage());
                logBufWriter.flush();
            }
            catch (IOException exc) {
                System.out.println(exc.getMessage());
            }
        }
    }

    /**
     * Gets the log's name
     * @return String x log name
     */
    public static String getLogName() {
        return fileLogName;
    }

    /** private fields */
    private static FileWriter logWriter;
    private static BufferedWriter logBufWriter;

    private static File log;
    private static String fileLogName = "log.txt";
}
