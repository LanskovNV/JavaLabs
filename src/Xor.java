import java.util.EnumMap;

public class Xor {
    int keylen;
    String key;
    String task;

    public Xor(EnumMap<BaseKeys, String> m) {
        key = m.get(BaseKeys.keyword);
        task = m.get(BaseKeys.type);
        keylen = key.length();
    }

    public byte[] selectTask(byte[] buffer) {
         switch (task) {
             case "encode":
                 return this.encode(buffer);
             case "decode":
                 return this.decode(buffer);
             default:
                 return null;
         }
    }

    public byte[] encode(byte[] buffer) {
        int cnt = 0;

        cnt = buffer.length;
        byte[] encstr = new byte[cnt];

        for (int i = 0, j = 0; i < cnt; i++) {
            encstr[i] = (byte)(buffer[i] ^ key.charAt(j++));
            if (j == keylen) { j = 0; }
        }

        return encstr;
    }

    public byte[] decode(byte[] buffer) {
        return encode(buffer);
    }
}
