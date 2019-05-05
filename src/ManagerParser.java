import java.util.*;


public class ManagerParser extends AbstractParser {
    private int numExecutors;
    private List<String> exConfigNames;

    static final Map<String, ManagerGrammar> grammarMap = new TreeMap<>();

    static {
        grammarMap.put("num_executors", ManagerGrammar.numExecutors);
        grammarMap.put("exec_config", ManagerGrammar.exConfigFileName);
    }

    public ManagerParser() {
        configSize = 0;
        delimiter = ":";
        numExecutors = 0;
        exConfigNames = new ArrayList<>();
        config = new EnumMap<ManagerGrammar, String>(ManagerGrammar.class);
    }

    protected boolean resolveLine(String[] configStr) {
        ManagerGrammar token = grammarMap.get(configStr[0].trim());
        if (token != null) {
            String res = configStr[1].trim();
            switch(token) {
                case exConfigFileName:
                    exConfigNames.add(res); //if the name of consumer's config file - add to the list
                    break;
                case numExecutors:
                    numExecutors = Integer.parseInt(res);
                    break;
            }
            return true;
        }
        else {
            return false;
        }
    }
    public List<String> getExConfigFilenames() {
        return exConfigNames;
    }

    public int getNumExecutors() {
        return numExecutors;
    }
}
