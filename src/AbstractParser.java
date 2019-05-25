import java.io.*;
import java.util.*;

/**
 * Base class for all parsers
 */
public abstract class AbstractParser {

    protected EnumMap config;
    protected int configSize;
    protected String delimiter;

    /**
     * @param configStr
     * @return 0 if ok 1 if error
     */
    abstract boolean resolveLine(String[] configStr);

    /**
     * @return enum map with lexemes
     */
    public EnumMap getConfig() { return config; }

    /**
     * @param filename
     * @return list of all file strings
     */
    public List<String> ReadCompleteFile (String filename) {
        File textFile = new File(filename);
        List<String> text = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(textFile);
            BufferedReader bufReader = new BufferedReader(fileReader);
            String str = bufReader.readLine();
            while (str != null)
            {
                text.add(str);
                str = bufReader.readLine();
            }
        } catch (IOException e) {
            ErrorLog.sendMessage("Error in ReadCompleteFile func");
        }
        return text;
    }

    /**
     * @param filename
     * @return
     */
    protected int parseConfig(String filename){
        List<String> configFile = ReadCompleteFile(filename);
        if(configFile == null)
            return 1;

        for(String str: configFile) {
            String[] configStr = str.split(delimiter, 2);
            if (!resolveLine(configStr)) {
                return 1;
            }
        }

        if(config.size() != configSize) {
            return 1;
        }
        return 0;
    }
}
