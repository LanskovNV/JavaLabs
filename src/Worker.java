import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;

public class Worker implements Executor {
    Executor consumer;
    DataInputStream input;
    DataOutputStream output;
    Xor alg;
    boolean isFirst, isLast;
    byte[] buffer;

    public Worker(String params, boolean first, boolean last) throws IOException {
        input = null;
        output = null;
        consumer = null;
        isFirst = first;
        isLast = last;

        ManagerGrammar g = new ManagerGrammar(params);
        EnumMap<BaseKeys, String> m = g.parser();
        alg = new Xor(m);
    }

    public int setInput(DataInputStream stream) {
        if (isFirst)
            input = stream;
        return 0;
    }

    public int setOutput(DataOutputStream stream) {
        if (isLast)
            output = stream;
        return 0;
    }

    public int setConsumer(Executor next) {
        consumer = next;
        return 0;
    }

    public int put(Object obj) throws IOException{
        byte[] newBuffer = (byte[])obj;

        int nIter = newBuffer.length / buffer.length;
        int cnt = (newBuffer.length % buffer.length) == 0 ? nIter : ++nIter;

        for (int i = 0 ; i < cnt ; i++) {
            Arrays.fill(buffer, (byte) 0);
            byte[] buf = Arrays.copyOfRange(buffer, i*)
            alg.selectTask();

        }
        if (isLast) {
            output.write(buffer);
        } else {
            consumer.put(buffer);
        }

        return 0;
    }

    public int run() {

        return 0;
    }
}
