import java.io.*;
import java.util.*;

public abstract class AbstractParser {

    /**
     * Setting expected config size
     */
    abstract void setConfigSize();

    /**
     * Setting the type of config structure
     */
    abstract void setConfig();

    /**
     * Getting final configuration
     * @return x parsed config
     */
    public EnumMap getConfig() { return config; }

    /**
     * Setting delimiter to split strings read from file
     */
    abstract void setDelimiter();

    /**
     * Parsing the line
     * @param configStr x string from file to resolve
     * @return 0 if is correct, not 0 otherwise
     */
    abstract boolean resolveLine(String[] configStr);

    /**
     * Opens buffered reader to read the file pointed in 'File' private field
     * and read the complete file saved in the class
     * @param filename x the name of input file
     * @return List<String> contains the input file strings
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
        } catch (IOException e) { }
        return text;
    }

    /**
     * Parsing config file
     * @param filename x the name of file to parse
     * @return 0 if success, otherwise 1
     */
    protected int parseConfig(String filename){
        List<String> configFile = ReadCompleteFile(filename);
        if(configFile == null)
            return 1;

        setDelimiter();
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

    /** private fields*/

    protected EnumMap config;
    protected int configSize;
    protected String delimiter;
}
