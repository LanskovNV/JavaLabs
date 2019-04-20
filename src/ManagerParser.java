import java.util.*;


public class ManagerParser extends AbstractParser {

    private int exCnt;
    private int numExecutors;
    private List<String> exConfigNames;

    private String numDelimiter = " ";

    static final Map<String, ManagerGrammar> grammarMap = new TreeMap<>();

    static {
        grammarMap.put("ex_cnt", ManagerGrammar.numExecutors);
        grammarMap.put("ex_config", ManagerGrammar.exConfigFileName);
    }

    public ManagerParser() {
        setConfig();
        setConfigSize();
        exConfigNames = new ArrayList<>();
        exCnt = -1;
        numExecutors = 0;
    }

    void setConfigSize() {
        configSize = 0;
    }

    protected void setConfig() {
        config = new EnumMap<ManagerGrammar, String>(ManagerGrammar.class);
    }

    protected void setDelimiter() {
        delimiter = ":";
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
