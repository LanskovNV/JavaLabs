package Pipeline;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Executor {
    int setInput(DataInputStream input);
    int setOutput(DataOutputStream output);
    int setConsumer(Executor consumer);
    int put(Object obj) throws IOException;
    int run() throws IOException;
}
