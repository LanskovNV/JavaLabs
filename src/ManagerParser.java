import java.util.*;

public class ManagerParser extends AbstractParser {

    /** static init */
    static final Map<String, ManagerGrammar> grammarMap = new TreeMap<>();
    //BEGIN static init block
    static {
        grammarMap.put("ex_cnt", ManagerGrammar.numExecutors);
        grammarMap.put("ex_class", ManagerGrammar.exClass);
        grammarMap.put("ex_config", ManagerGrammar.exConfigFileName);
        grammarMap.put("ex_consumers", ManagerGrammar.exConsumers);
    }
    //END static init block

    /**
     * Creates object
     */
    public ManagerParser() {
        setConfig();
        setConfigSize();
        exConfigNames = new ArrayList<>();
        exClasses = new ArrayList<>();
        exCnt = -1;
        numExecutors = 0;
    }

    //BEGIN AbstractParser
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
                case exConsumers:
                    if(numExecutors == 0 || exCnt == numExecutors) { //if the number of executors is incorrect
                        ErrorLog.sendMessage("the number of executors in undefined");
                        return false;
                    }
                    String[] exNumbers = res.split(numDelimiter); //split numbers of consumers using the set delimiter
                    exConsumers[exCnt] = new int[exNumbers.length]; //
                    for(int i = 0; i < exNumbers.length; i++)
                        exConsumers[exCnt][i] = Integer.parseInt(exNumbers[i]);
                    break;
                case exClass:
                    exClasses.add(res); //if the name of consumer's class - add to the list
                    exCnt++;
                    break;
                case numExecutors:
                    numExecutors = Integer.parseInt(res);
                    exConsumers = new int[numExecutors][]; //save numbers of consumers in the pipeline
                    break;
            }
            return true;
        }
        else {
            return false;
        }
    }
    ///END AbstractParser

    ///BEGIN inner functions

    /**
     * Function for getting executors's configuration files names
     * @return List<String> x list of executors's configuration files names
     */
    public List<String> getExConfigFilenames() {
        return exConfigNames;
    }

    /**
     * Function for getting executors's classes names
     * @return List<String> x list of executors's classes names
     */
    public List<String> getExClasses() {
        return exClasses;
    }

    /**
     * Function for getting fixed relations between executors in pipeline
     * @return int[][] x massive of number executors in the expected pipeline
     * who will consume provider[i] data (i - number i the expected pipeline)
     */
    public int[][] getExConsumers() {
        return exConsumers;
    }

    /**
     * Function to get the exp
     * ected number of executors in the pipeline
     * @return int x number of executors in the pipeline
     */
    public int getNumExecutors() {
        return numExecutors;
    }
    ///END inner functions

    /** private fields */
    private int exCnt;
    private int numExecutors;
    private List<String> exConfigNames;
    private int[][] exConsumers;
    private List<String> exClasses;

    private String numDelimiter = " ";
}
