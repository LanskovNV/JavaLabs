import java.io.*;
import java.util.*;

/**
 * Manager that's a main class to pipeline work
 */
public class Manager {

    private Executor executors[];
    private int numExecutors;
    private DataInputStream input;
    private DataOutputStream output;

    /**
     * @param baseConfig
     * @return 0 if ok 1 if error
     */
    public int openStreams(EnumMap<MainGrammar, String> baseConfig) {
        try {
            String in = baseConfig.get(MainGrammar.input);
            String out = baseConfig.get(MainGrammar.output);

            input = new DataInputStream(new FileInputStream(in));
            output = new DataOutputStream(new FileOutputStream(out));
        }
        catch (IOException e) {
            ErrorLog.sendMessage("file streams wasn't founded\n");
            return 1;
        }
        return 0;
    }

    /**
     * @param baseConfig
     * @return 0 if ok 1 if error
     */
    public int createPipeline(EnumMap<MainGrammar, String> baseConfig) {
        ManagerParser mngParser = new ManagerParser();
        if(mngParser.parseConfig(baseConfig.get(MainGrammar.managerConfig)) != 0)
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
        executors[0].setInput(input);
        return 0;
    }

    /**
     * @param mngParser
     * @return 0 if ok 1 if error
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
     * @param exConfigs list of executors config file names
     * @param exClasses list of executors classes
     * @return 0 if ok 1 if error
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
     *
     * @param consumers all executors with their consumers,
     *                 consumers[i][j] j consumer of i executor
     * @return 0 if ok 1 if error
     */
    private int setConsumers(int[][] consumers) {
        boolean gotLast = false;
        for(int i = 0; i < consumers.length; i++) {
            if(consumers[i] == null) {
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
                    executors[i].setConsumer(executors[consumers[i][j]]);
                }
        }
        return 0;
    }

    private void setOutput(int i) {
        executors[i].setOutput(output);
    }

    /**
     * Start pipeline work
     */
    public void run() {
        if(executors[0].run() != 0) {
            ErrorLog.sendMessage("the pipeline work was incorrect");
        }
    }

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
