import java.io.IOException;

public class MainClass {
    public static void main(String[] Args) {
        Error log = new Error();

        if (Args.length < 0) {
            log.message("incorrect cmd arguments, should be config file name");
        } else {
            try {

            }
            catch (IOException exc) {
                log.message(exc.toString());
            }
        }

        log.close();
    }
}
