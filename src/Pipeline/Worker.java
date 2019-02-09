package Pipeline;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import Algoritms.Xor;

public class Worker implements Executor {

    Executor consumer;
    DataInputStream input;
    DataOutputStream output;
    int order;

    public Worker() {
        input = null;
        output = null;
        consumer = null;

    }

    public int setInput(DataInputStream input) {
        return 0;
    }

    public int setOutput(DataOutputStream output) {
        return 0;
    }

    public int setConsumer(Executor consumer) {
        return 0;
    }

    public int put(Object obj) {
        return 0;
    }

    public int run() {
        return 0;
    }
}
