import java.io.*;

public class ErrorLog {

    private static File log;
    private static String fileLogName = "log.txt";

    private static FileWriter logWriter;
    private static BufferedWriter logBufWriter;

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

    public static void sendMessage(String message) {
        try {
            logBufWriter.write("Err: " + message + "\n");
            logBufWriter.flush();
        }
        catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
    }

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


}
