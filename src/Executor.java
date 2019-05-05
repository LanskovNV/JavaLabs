import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface Executor {

    enum APPROPRIATE_TYPES {  CHAR, BYTE, DOUBLE }

    int setConfig(String config);

    void setInput(DataInputStream input);

    void setOutput(DataOutputStream output);

    int setConsumer(Executor consumer);

    APPROPRIATE_TYPES[] getConsumedTypes();

    void setAdapter(Executor provider, Object adapter, APPROPRIATE_TYPES type);

    int run();

    int put(Executor provider);
}