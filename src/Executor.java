import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface Executor {

    int setConfig(String config);

    void setInput(DataInputStream input);

    void setOutput(DataOutputStream output);

    int setConsumer(Executor consumer);

    int run();

    int put(Executor provider);
}
