import java.util.*;

public class ManagerParser extends AbstractParser {

    static final Map<String, ManagerGrammar> grammarMap = new TreeMap<>();
    static {
        grammarMap.put("ex_cnt", ManagerGrammar.numExecutors);
        grammarMap.put("ex_class", ManagerGrammar.exClass);
        grammarMap.put("ex_config", ManagerGrammar.exConfigFileName);
        grammarMap.put("ex_consumers", ManagerGrammar.exConsumers);
    }

    private int exCnt;
    private int numExecutors;
    private String consDelimiter = " ";
    private List<String> exConfigNames;
    private int[][] exConsumers;
    private List<String> exClasses;

    public ManagerParser() {
        delimiter = ":";
        configSize = 0;
        config = new EnumMap<ManagerGrammar, String>(ManagerGrammar.class);
        exConfigNames = new ArrayList<>();
        exClasses = new ArrayList<>();
        exCnt = -1;
        numExecutors = 0;
    }

    public List<String> getExConfigFilenames() {
        return exConfigNames;
    }

    public List<String> getExClasses() {
        return exClasses;
    }

    public int[][] getExConsumers() {
        return exConsumers;
    }

    public int getNumExecutors() {
        return numExecutors;
    }

    protected boolean resolveLine(String[] configStr) {
        ManagerGrammar token = grammarMap.get(configStr[0].trim());
        if (token != null) {
            String res = configStr[1].trim();
            switch(token) {
                case exConfigFileName:
                    exConfigNames.add(res);
                    break;
                case exConsumers:
                    if(numExecutors == 0 || exCnt == numExecutors) {
                        ErrorLog.sendMessage("incorrect number of executors");
                        return false;
                    }
                    String[] exNumbers = res.split(consDelimiter);
                    exConsumers[exCnt] = new int[exNumbers.length];
                    for(int i = 0; i < exNumbers.length; i++)
                        exConsumers[exCnt][i] = Integer.parseInt(exNumbers[i]);
                    break;
                case exClass:
                    exClasses.add(res);
                    exCnt++;
                    break;
                case numExecutors:
                    numExecutors = Integer.parseInt(res);
                    exConsumers = new int[numExecutors][];
                    break;
            }
            return true;
        }
        else {
            return false;
        }
    }
}
