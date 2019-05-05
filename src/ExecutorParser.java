import java.util.*;

public class ExecutorParser extends AbstractParser {

    static final Map<String, ExecutorGrammar> grammarMap = new TreeMap<String, ExecutorGrammar>();
    static {
        grammarMap.put("keyword", ExecutorGrammar.keyword);
        grammarMap.put("buf_size", ExecutorGrammar.bufsize);
    }

    public ExecutorParser() {
        delimiter = ":";
        configSize = grammarMap.size();
        config = new EnumMap<ExecutorGrammar, String>(ExecutorGrammar.class);
    }

    protected boolean resolveLine(String[] configStr) {
        ExecutorGrammar token = grammarMap.get(configStr[0].trim());
        if (token != null) {
            config.put(token, configStr[1].trim());
            return true;
        }
        else {
            return false;
        }
    }
}