import java.io.*;
import java.util.*;

public abstract class AbstractParser {

    protected EnumMap config;
    protected int configSize;
    protected String delimiter;

    abstract boolean resolveLine(String[] configStr);

    public EnumMap getConfig() { return config; }

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
