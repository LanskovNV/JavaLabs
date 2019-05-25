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

        if(exConfigs.size() != numExecutors) {
            ErrorLog.sendMessage("the number of executors is incompatible with the number of configs");
            return 1;
        }

        executors = new Executor[numExecutors];
        if(setExecutors(exConfigs) != 0)
            return 1;
        if(setConsumers() != 0)
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
            ErrorLog.sendMessage("incorrect number of executors in config");
            return 1;
        }
        return 0;
    }

    /**
     * @param exConfigs
     * @return 0 if ok 1 if error
     */
    private int setExecutors(List<String> exConfigs) {
        for(int i = 0; i < numExecutors; i++) {
            executors[i] = new Xorer();
            executors[i].setConfig(exConfigs.get(i));
        }
        return 0;
    }

    private int setConsumers() {
        for (int i = 0; i < numExecutors; i++) {
            if (i == numExecutors - 1) {
                executors[i].setOutput(output);
            }
            else {
                executors[i].setConsumer(executors[i + 1]);
            }
        }
        return 0;
    }

    /**
     * Start pipeline work
     */
    public void run() {
        if(executors[0].run() != 0) {
            ErrorLog.sendMessage("error in pipeline work");
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
            ErrorLog.sendMessage("error in closing streams");
        }
    }
}
