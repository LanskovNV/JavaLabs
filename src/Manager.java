import java.io.*;
import java.util.*;


public class Manager {

    private Executor executors[];
    private int numExecutors;
    private DataInputStream input;
    private DataOutputStream output;

    public int openStreams(EnumMap<BaseGrammar, String> baseConfig) {
        try {
            String in = baseConfig.get(BaseGrammar.input); //get data from config
            String out = baseConfig.get(BaseGrammar.output);

            input = new DataInputStream(new FileInputStream(in)); //open streams
            output = new DataOutputStream(new FileOutputStream(out));
        }
        catch (IOException e) {
            ErrorLog.sendMessage("file streams wasn't founded\n");
            return 1;
        }
        return 0;
    }

    public int createPipeline(EnumMap<BaseGrammar, String> baseConfig) {
        ManagerParser mngParser = new ManagerParser();
        if(mngParser.parseConfig(baseConfig.get(BaseGrammar.managerConfig)) != 0)
            return 1;

        if(setNumExecutors(mngParser) != 0)
            return 1;

        List<String> exConfigs = mngParser.getExConfigFilenames();
        List<String> exClasses = mngParser.getExClasses();
        int[][] consumers = mngParser.getExConsumers();

        if(exConfigs.size() != numExecutors || exClasses.size() != numExecutors) {
            ErrorLog.sendMessage("the number of executors is incompatible with the number of configs");
            return 1;
        }

        executors = new Executor[numExecutors];
        if(setExecutors(exConfigs, exClasses) != 0)
            return 1;
        if(setConsumers(consumers) != 0)
            return 1;
        executors[0].setInput(input); //set input to the first executor in pipeline
        return 0;
    }

    /**
     * Sets the number of executors which were read & check if the data is correct
     * @param mngParser x the manager's interpreter
     * @return 0 if success, otherwise 1
     */
    private int setNumExecutors(ManagerParser mngParser) {
        numExecutors = mngParser.getNumExecutors();
        if(numExecutors < 1) {
            ErrorLog.sendMessage("incorrect number of executors in config file");
            return 1;
        }
        return 0;
    }

    /**
     * Sets executors in the pipeline
     * @param exConfigs x list of executors's config files
     * @param exClasses x list of executor's classes
     * @return 0 if success, otherwise 1
     */
    private int setExecutors(List<String> exConfigs, List<String> exClasses) {
        for(int i = 0; i < numExecutors; i++) {
            try {
                executors[i] = (Executor) Class.forName(exClasses.get(i)).getDeclaredConstructor().newInstance();
                executors[i].setConfig(exConfigs.get(i));
            }
            catch(ReflectiveOperationException e) {
                ErrorLog.sendMessage("executor's class " + exClasses.get(i) + " not founded");
                return 1;
            }
        }
        return 0;
    }

    /**
     * Sets consumers
     * @param consumers x int[][] which handles the oriented graph which describing the
     *                  relationships between executors
     * @return 0 if success, otherwise 1
     */
    private int setConsumers(int[][] consumers) {
        boolean gotLast = false;
        for(int i = 0; i < consumers.length; i++) {
            if(consumers[i] == null) { //if null - this executor is the last one in the pipeline
                if(gotLast == true) {
                    ErrorLog.sendMessage("there's more that one last worker");
                    return 1;
                }
                gotLast = true;
                setOutput(i);
            }
            else
                for(int j = 0; j < consumers[i].length; j++) {
                    if(consumers[i][j] >= executors.length) {
                        ErrorLog.sendMessage("incorrect executors sequence");
                        return 1;
                    }
                    executors[i].setConsumer(executors[consumers[i][j]]); //set consumers
                }
        }
        return 0;
    }

    /**
     * Gives output stream to the last executor
     * @param i x sets output to the executor which has i number in the executors massive
     *          (not in the pipeline!)
     */
    private void setOutput(int i) {
        executors[i].setOutput(output);
    }

    /**
     * Runs the pipeline
     */
    public void run() {
        if(executors[0].run() != 0) {
            ErrorLog.sendMessage("the pipeline work was incorrect");
        }
    }

    /**
     * Closes all streams which were opened
     */
    public void closeStreams() {
        try {
            if (input != null)
                input.close();
            if (output != null)
                output.close();
        }
        catch (IOException e) {
            ErrorLog.sendMessage("problem with streams closing");
        }
    }
}
