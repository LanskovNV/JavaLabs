public class Xor {
    private static String keyword;

    public Xor(String keyWord) {
        keyword = keyWord;
    }

    public byte[] Xor(byte[] buf) {
        int cnt = 0, keyWordLen = keyword.length();
        int bufsize = buf.length;

        for (int i = 0; i < bufsize; i++) {
            buf[i] = (byte) (buf[i] ^ keyword.charAt(cnt++));
            if (cnt == keyWordLen)
                cnt = 0;
        }

        return buf;
    }
}
