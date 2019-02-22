import javafx.beans.binding.ObjectBinding;

import java.io.*;
import java.util.*;

public class XORer implements Executor {

    /**
     * Creates object
     */
    public XORer() {
        consumers = new ArrayList<>();
        adaptersMap = new HashMap<>();
        adaptersTypesMap = new HashMap<>();
        operatedTypes = new APPROPRIATE_TYPES[2];
        operatedTypes[0] = APPROPRIATE_TYPES.BYTE;
        operatedTypes[1] = APPROPRIATE_TYPES.CHAR;
    }

    /// BEGIN Executor interface
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

    public int setConsumer(Executor consumer) {
        if(consumer == null) {
            return 1;
        }
        if(setConnection(consumer) == 0) {
            consumers.add(consumer);
            return 0;
        }
        return 1;
    }

    public APPROPRIATE_TYPES[] getConsumedTypes() {
        return operatedTypes;
    }

    public void setAdapter(Executor provider, Object adapter, Executor.APPROPRIATE_TYPES type){
        for(int i = 0; i < operatedTypes.length; i++) {
            if(operatedTypes[i] == type) {
                if(adaptersMap.containsKey(provider)) {
                    adaptersMap.remove(provider);
                }
                if(adaptersTypesMap.containsKey(provider)) {
                    adaptersMap.remove(provider);
                }
                adaptersMap.put(provider, adapter);
                adaptersTypesMap.put(provider, type);
                return;
            }
        }
    }

    public int put(Executor provider) {
        int ind = 0, i = 0;
        Arrays.fill(buf, (byte) 0);

        if(adaptersMap.containsKey(provider)) {
            Object undefAdapter = adaptersMap.get(provider);
            APPROPRIATE_TYPES adaptersType = adaptersTypesMap.get(provider);
            switch (adaptersType) {
                case BYTE:
                    InterfaceByteTransfer adapterByte = (InterfaceByteTransfer) undefAdapter;
                    Byte b = adapterByte.getNextByte();
                    while(ind < buf.length && b != null) {
                        if(i >= posShift) {
                            buf[ind] = b;
                            ind++;
                        }
                        i++;
                        b = adapterByte.getNextByte();
                    }
                    break;
                case CHAR:
                    InterfaceCharTransfer adapterChar = (InterfaceCharTransfer) undefAdapter;
                    Character c = adapterChar.getNextChar();
                    while(ind < buf.length && c != null) {
                        if(i >= posShift) {
                            buf[ind] = (byte)(char)c;
                            ind++;
                        }
                        i++;
                        c = adapterChar.getNextChar();
                    }
                    break;
                default:
            }
        }
        else
            return 1;

        if(consumers.size() == 0) {
            buf = encoder.XOR(buf);
            writeBytes(ind);
        }
        else {
            byte[] res = Arrays.copyOf(buf, ind);
            buf = new byte[ind];
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
    /// END Executor interface

    /// BEGIN ByteTransferAdapter
    class ByteTransferAdapter implements InterfaceByteTransfer {
        private int pos = 0;

        public Byte getNextByte() {
            if (pos >= buf.length) {
                return null;
            }
            else {
                return buf[pos++];
            }
        }
    }
    /// END ByteTransferAdapter

    /// BEGIN CharTransferAdapter
    class CharTransferAdapter implements InterfaceCharTransfer {
        private int pos = 0;

        public Character getNextChar() {
            if (pos >= buf.length) {
                return null;
            }
            else {
                return (char)buf[pos++];
            }
        }
    }
    /// END CharTransferAdapter

    /// BEGIN inner methods


    /**
     * The function to initialise class's private fields
     * @param parser - configuration parser of XORer class
     * @return 0 if success, otherwise 1
     */
    private int setXORer(ExecutorParser parser) {
        /* get configuration */
        EnumMap<ExecutorGrammar, String> exConfig = parser.getConfig();
        int blockSize = Integer.parseInt(exConfig.get(ExecutorGrammar.bufsize));
        posShift = Integer.parseInt(exConfig.get(ExecutorGrammar.firstSymbolNum));
        if(blockSize <= 0 || posShift < 0) {
            return 1;
        }
        String keyWord = exConfig.get(ExecutorGrammar.keyword);

        /* initialisation of private fields */
        this.task = parser.resolveTask();
        buf = new byte[blockSize];
        encoder = new XOR(keyWord, blockSize);
        return 0;
    }

    /**
     * @return x number of bytes which were read
     */
    private int readBytes() {
        try {
            return input.read(buf);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Writes bytes in the output stream
     */
    private void writeBytes(int len) {
        try {
            output.write(buf, 0, len);
        } catch (Exception e) {

        }
    }

    /**
     * Function XORes the block & send worker to all consumers
     * @return 0 if success, otherwise 1
     */
    private int processBlock() {
        int isSuccess;
        buf = encoder.XOR(buf);
        if (consumers != null) {
            for(Executor consumer: consumers) {
                if(setConnection(consumer) == 0) {
                    isSuccess = consumer.put(this);
                    if (isSuccess != 0)
                        return 1;
                }
            }
        }
        else
            return 1;
        return 0;
    }

    /**
     * Search for compatible types
     * @param consumersTypes x APPROPRIATE_TYPES array of consumer's types
     * @return APPROPRIATE_TYPES x compatible type
     */
    private APPROPRIATE_TYPES getAppropriateType(APPROPRIATE_TYPES consumersTypes[]) {
        for(int i = 0; i < operatedTypes.length; i++) {
            for(int j = 0; j < consumersTypes.length; j++) {
                if (operatedTypes[i] == consumersTypes[j]) {
                    return operatedTypes[i];
                }
            }
        }
        return null;
    }

    /**
     * Sets connection between consumer and provider
     * @param consumer x the consumer
     * @return 0 if success, otherwise 1
     */
    private int setConnection(Executor consumer) {
        APPROPRIATE_TYPES consumersTypes[] = consumer.getConsumedTypes();
        switch(getAppropriateType(consumersTypes)) {
            case BYTE:
                consumer.setAdapter(this, new ByteTransferAdapter(), APPROPRIATE_TYPES.BYTE);
                return 0;
            case CHAR:
                consumer.setAdapter(this, new CharTransferAdapter(), APPROPRIATE_TYPES.CHAR);
                return 0;
            default:
                return 1;
        }
    }

    /// END inner methods

    /** private fields */
    private ExecutorTask task;
    private XOR encoder;
    private byte[] buf;
    private int posShift = 0;

    private DataInputStream  input;
    private DataOutputStream output;

    private APPROPRIATE_TYPES[] operatedTypes;

    private ArrayList<Executor>                  consumers;
    private HashMap<Executor, Object>            adaptersMap;
    private HashMap<Executor, APPROPRIATE_TYPES> adaptersTypesMap;
}
