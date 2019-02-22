import java.util.*;

public class ExecutorParser extends AbstractParser {

    /** static init */
    ///BEGIN static blocks
    static final Map<String, ExecutorGrammar> grammarMap = new TreeMap<String, ExecutorGrammar>();
    static {
        grammarMap.put("task", ExecutorGrammar.task);
        grammarMap.put("keyword", ExecutorGrammar.keyword);
        grammarMap.put("buf_size", ExecutorGrammar.bufsize);
        grammarMap.put("num_first", ExecutorGrammar.firstSymbolNum);
    }

    static final Map<String, ExecutorTask> taskMap = new TreeMap<String, ExecutorTask>();
    static {
        taskMap.put("encode", ExecutorTask.encode);
        taskMap.put("decode", ExecutorTask.decode);
    }
    ///END static blocks

    /**
     * Creates object
     */
    public ExecutorParser() {
        setConfig();
        setConfigSize();
    }

    ///BEGIN AbstractParser
    void setConfigSize() {
        configSize = grammarMap.size();
    }

    protected void setConfig() {
        config = new EnumMap<ExecutorGrammar, String>(ExecutorGrammar.class);
    }

    protected void setDelimiter() {
        delimiter = ":";
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
    ///END Abstract Parser

    /**
     * Resolves executor's task
     * @return ExecutorTask x executor's task
     */
    public ExecutorTask resolveTask() {
        return taskMap.get(config.get(ExecutorGrammar.task));
    }
}