import java.util.*;

public class BaseParser extends AbstractParser {

    static final Map<String, BaseGrammar> grammarMap = new TreeMap<String, BaseGrammar>();
    static {
        grammarMap.put("input", BaseGrammar.input);
        grammarMap.put("output", BaseGrammar.output);
        grammarMap.put("mng_config", BaseGrammar.managerConfig);
    }

    public BaseParser() {
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
        config = new EnumMap<BaseGrammar, String>(BaseGrammar.class);
    }

    protected boolean resolveLine(String[] configStr) {
        BaseGrammar token = grammarMap.get(configStr[0].trim());
        if (token != null) {
            config.put(token, configStr[1].trim());
            return true;
        }
        else {
            return false;
        }
    }
}
