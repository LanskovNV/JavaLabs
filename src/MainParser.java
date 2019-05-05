import java.util.*;

public class MainParser extends AbstractParser {

    static final Map<String, MainGrammar> grammarMap = new TreeMap<String, MainGrammar>();
    static {
        grammarMap.put("input", MainGrammar.input);
        grammarMap.put("output", MainGrammar.output);
        grammarMap.put("mng_config", MainGrammar.managerConfig);
    }

    public MainParser() {
        setConfig();
        setConfigSize();
    }

    void setConfigSize() {
        configSize = grammarMap.size();
    }

    protected void setDelimiter() {
        delimiter = ":";
    }

    protected void setConfig() {
        config = new EnumMap<MainGrammar, String>(MainGrammar.class);
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
