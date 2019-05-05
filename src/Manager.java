import java.io.*;
import java.util.*;


public class Manager {

    private Executor executors[];
    private int numExecutors;
    private DataInputStream input;
    private DataOutputStream output;

    public int openStreams(EnumMap<BaseGrammar, String> baseConfig) {
        try {
            String in = baseConfig.get(BaseGrammar.input);
            String out = baseConfig.get(BaseGrammar.output);

            input = new DataInputStream(new FileInputStream(in));
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

        if(exConfigs.size() != numExecutors) {
            ErrorLog.sendMessage("the number of executors is incompatible with the number of configs");
            return 1;
        }

        executors = new Executor[numExecutors];
        if(setExecutors(exConfigs) != 0)
            return 1;
        if(setConsumers() != 0)
            return 1;
        executors[0].setInput(input); //set input to the first executor in pipeline
        return 0;
    }

    private int setNumExecutors(ManagerParser mngParser) {
        numExecutors = mngParser.getNumExecutors();
        if(numExecutors < 1) {
            ErrorLog.sendMessage("incorrect number of executors in config file");
            return 1;
        }
        return 0;
    }

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
