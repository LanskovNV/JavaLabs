public class Xor {
    private static String keyword;
    private static int bufsize;

    public Xor(String keyWord, int bufSize) {
        keyword = keyWord;
        bufsize = bufSize;
    }

    public byte[] encode(byte[] buf, int len) {
        return Xor(buf);
    }

    public byte[] decode(byte[] buf, int len){
        return Xor(buf);
    }

    public byte[] Xor(byte[] buf) {
        int cnt = 0, keyWordLen = keyword.length();

        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) (buf[i] ^ keyword.charAt(cnt++));
            if (cnt == keyWordLen)
                cnt = 0;
        }

        return buf;
    }
}
