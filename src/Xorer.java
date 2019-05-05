import javafx.beans.binding.ObjectBinding;

import java.io.*;
import java.util.*;

public class Xorer implements Executor {

    private ExecutorTask task;
    private Xor encoder;
    private byte[] buf;
    private int posShift = 0;

    private DataInputStream  input;
    private DataOutputStream output;

    private Executor consumer;

    public Xorer() {}

    public int setConfig(String config) {
        ExecutorParser parser = new ExecutorParser();
        if (parser.parseConfig(config) != 0)
            return 1;

        if(setXORer(parser) != 0)
            return 1;
        return 0;
    }

    public void setInput(DataInputStream in) { input = in; }

    public void setOutput(DataOutputStream out) { output = out; }

    public int setConsumer(Executor consumer1) {
        if(consumer1 == null) {
            return 1;
        }
        consumer = consumer1;
        return 0;
    }

    public int put(byte[] buffer) {
        int bufLen = buf.length;
        buf = Arrays.copyOf(buffer, bufLen);

        if(consumer == null) {
            buf = encoder.Xor(buf);
            writeBytes(bufLen);
        }
        else {
            byte[] res = Arrays.copyOf(buf, bufLen);
            buf = new byte[bufLen];
            buf = res;
            processBlock();
        }

        return 0;
    }

    public int run() {
        int readBytesCounter;

        while (true) {
            readBytesCounter = readBytes();
            if(readBytesCounter == -1)
                return 1;

            if (readBytesCounter != buf.length) {
                byte[] res = Arrays.copyOf(buf, readBytesCounter);
                buf = new byte[readBytesCounter];
                buf = res;
                processBlock();
                break;
            }

            processBlock();
            Arrays.fill(buf, (byte) 0);
        }
        return 0;
    }

    private int setXORer(ExecutorParser parser) {
        EnumMap<ExecutorGrammar, String> exConfig = parser.getConfig();
        int blockSize = Integer.parseInt(exConfig.get(ExecutorGrammar.bufsize));
        posShift = Integer.parseInt(exConfig.get(ExecutorGrammar.firstSymbolNum));
        if(blockSize <= 0 || posShift < 0) {
            return 1;
        }
        String keyWord = exConfig.get(ExecutorGrammar.keyword);

        this.task = parser.resolveTask();
        buf = new byte[blockSize];
        encoder = new Xor(keyWord, blockSize);
        return 0;
    }

    private int readBytes() {
        try {
            return input.read(buf);
        } catch (Exception e) {
            return -1;
        }
    }

    private void writeBytes(int len) {
        try {
            output.write(buf, 0, len);
        } catch (Exception e) {
            return;
        }
    }

    private int processBlock() {
        int isSuccess;
        buf = encoder.Xor(buf);

        if (consumer != null) {
            isSuccess = consumer.put(buf);
            if (isSuccess != 0)
                return 1;
        }
        else
            return 1;
        return 0;
    }
}
