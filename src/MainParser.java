import java.util.*;

public class MainParser extends AbstractParser {

    static final Map<String, MainGrammar> grammarMap = new TreeMap<String, MainGrammar>();
    static {
        grammarMap.put("input", MainGrammar.input);               // input file name
        grammarMap.put("output", MainGrammar.output);             // output file name
        grammarMap.put("mng_config", MainGrammar.managerConfig);  // manager config file name
    }

    public MainParser() {
        configSize = grammarMap.size();
        config = new EnumMap<MainGrammar, String>(MainGrammar.class);
        delimiter = ":";
    }

    protected boolean resolveLine(String[] configStr) {
        MainGrammar token = grammarMap.get(configStr[0].trim());
        if (token != null) {
            config.put(token, configStr[1].trim());
            return true;
        }
        else {
            return false;
        }
    }
}
