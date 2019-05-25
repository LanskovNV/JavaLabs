import java.util.*;

public class ExecutorParser extends AbstractParser {

    static final Map<String, ExecutorGrammar> grammarMap = new TreeMap<String, ExecutorGrammar>();
    static {
        // encode or decode, etc
        grammarMap.put("task", ExecutorGrammar.task);
        grammarMap.put("keyword", ExecutorGrammar.keyword);
        grammarMap.put("buf_size", ExecutorGrammar.bufsize);
        // it is shift
        grammarMap.put("num_first", ExecutorGrammar.firstSymbolNum);
    }

    static final Map<String, ExecutorTask> taskMap = new TreeMap<String, ExecutorTask>();
    static {
        taskMap.put("encode", ExecutorTask.encode);
        taskMap.put("decode", ExecutorTask.decode);
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

    public ExecutorTask resolveTask() {
        return taskMap.get(config.get(ExecutorGrammar.task));
    }
}