public class XOR {
    private static String _keyWord;
    private static int _bufSize;

    public XOR(String keyWord, int bufSize) {
        _keyWord = keyWord;
        _bufSize = bufSize;
    }

    public byte[] encode(byte[] buf, int len) {
        return XOR(buf);
    }

    public byte[] decode(byte[] buf, int len){
        return XOR(buf);
    }

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
