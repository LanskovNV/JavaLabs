import java.io.*;
import java.util.*;

/**
 * Worker with xor algorithm
 */
public class Xorer implements Executor {

    private byte[] buf;
    private int posShift = 0;

    private String keyword;
    private DataInputStream  input;
    private DataOutputStream output;

    private APPROPRIATE_TYPES[] operatedTypes;

    private ArrayList<Executor>                  consumers;
    private HashMap<Executor, Object>            adaptersMap;
    private HashMap<Executor, APPROPRIATE_TYPES> adaptersTypesMap;

    private int pos = 0;

    public Xorer() {
        consumers = new ArrayList<>();
        adaptersMap = new HashMap<>();
        adaptersTypesMap = new HashMap<>();
        operatedTypes = new APPROPRIATE_TYPES[2];
        operatedTypes[0] = APPROPRIATE_TYPES.BYTE;
        operatedTypes[1] = APPROPRIATE_TYPES.CHAR;
    }

    /**
     * Encoding algorithm
     * @param buf string to encode
     * @return encoded buffer
     */
    public byte[] XOR(byte[] buf) {
        int cnt = 0, keyWordLen = keyword.length();
        int buflen = buf.length;
        for (int i = 0; i < buflen; i++) {
            buf[i] = (byte) (buf[i] ^ keyword.charAt(cnt));
            cnt++;
            if (cnt == keyWordLen)
                cnt = 0;
        }
        return buf;
    }

    /**
     * @param config config file name
     * @return 1 if ok 0 if error
     */
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

    /**
     * @param consumer
     * @return 1 if ok 0 if error
     */
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

    /**
     * Create correct adapter for current executor
     * @param provider
     * @param adapter
     * @param type
     */
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

    /**
     * Select correct adapter type
     * @param consumer
     * @return 0 if ok 1 if error
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

    /**
     * Process block for all executors
     * @return 0 if ok 1 if error
     */
    private int processBlock() {
        buf = XOR(buf);
        if (consumers != null) {
            for(Executor consumer: consumers) {
                this.pos = 0;
                if (consumer.put(this) != 0)
                    return 1;
            }
        }
        else
            return 1;
        return 0;
    }

    /**
     * process buffer and give it to consumer
     * @param provider
     * @return 0 if ok 1 if error
     */
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
                        if (i >= posShift) {
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

        giveBuffNext(ind);
        return 0;
    }

    /**
     * Get new data to executor
     * @param ind
     */
    private void giveBuffNext(int ind) {
        if(consumers.size() == 0) {
            buf = XOR(buf);
            writeBytes(ind);
        }
        else {
            byte[] res = Arrays.copyOf(buf, ind);
            buf = new byte[ind];
            buf = res;
            processBlock();
        }
    }

    /**
     * Init pipeline work
     * @return 0 if ok 1 if error
     */
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

    class ByteTransferAdapter implements InterfaceByteTransfer {
        public Byte getNextByte() {
            if (pos >= buf.length) {
                return null;
            }
            return buf[pos++];
        }
    }
    class CharTransferAdapter implements InterfaceCharTransfer {
        public Character getNextChar() {
            if (pos >= buf.length) {
                return null;
            }
            else {
                return (char)buf[pos++];
            }
        }
    }

    /**
     * Setting up xor params
     * @param parser
     * @return 0 if ok 1 if error
     */
    private int setXORer(ExecutorParser parser) {
        EnumMap<ExecutorGrammar, String> exConfig = parser.getConfig();
        int blockSize = Integer.parseInt(exConfig.get(ExecutorGrammar.bufsize));
        posShift = Integer.parseInt(exConfig.get(ExecutorGrammar.firstSymbolNum));
        if(blockSize <= 0 || posShift < 0) {
            return 1;
        }
        keyword = exConfig.get(ExecutorGrammar.keyword);
        buf = new byte[blockSize];
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

        }
    }

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
}
