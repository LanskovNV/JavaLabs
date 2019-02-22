public class XOR {
    private static String _keyWord;
    private static int _bufSize;

    /**
     * Creates object
     * @param keyWord x keyword to XOR with
     * @param bufSize x size of the input buffer
     */
    public XOR(String keyWord, int bufSize) {
        _keyWord = keyWord;
        _bufSize = bufSize;
    }

    /**
     * Encodes input buffer
     * @param buf x input buffer
     * @param len x the length of the input buffer
     * @return the result of XOR operation
     */
    public byte[] encode(byte[] buf, int len) {
        return XOR(buf);
    }

    /**
     * Decodes input buffer
     * @param buf x input buffer
     * @param len x the length of the input buffer
     * @return the result of XOR operation
     */
    public byte[] decode(byte[] buf, int len){
        return XOR(buf);
    }

    /**
     * Applies XOR operation on the full input buffer
     * @param buf x input buffer
     * @return the result of XOR operation
     */
    public byte[] XOR(byte[] buf) {
        int cnt = 0, keyWordLen = _keyWord.length();

        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) (buf[i] ^ _keyWord.charAt(cnt));
            cnt++;
            if (cnt == keyWordLen)
                cnt = 0;
        }

        return buf;
    }
}
