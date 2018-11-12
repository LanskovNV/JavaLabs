import java.io.*;
import java.util.EnumMap;

class Crypt {
    int keylen;
    int buf_len;
    String key;

    Crypt(String params) throws IOException{
        Grammar g = new Grammar(params);
        EnumMap<keys, String> m = g.parser();

        buf_len = Integer.valueOf(m.get(keys.buffer_len));
        key = m.get(keys.keyword);
        keylen = key.length();
    }

    void encode(String input, String output) throws IOException {
         try {
            FileInputStream fin = new FileInputStream(input);
            FileOutputStream fout = new FileOutputStream(output);

            byte[] buffer = new byte[buf_len];
            int cnt = 0;

            do {
                cnt = fin.read(buffer);
                byte[] encstr = new byte[buf_len];

                for (int i = 0, j = 0; i < cnt; i++) {
                    encstr[i] = (byte)(buffer[i] ^ key.charAt(j++));
                    if (j == keylen) { j = 0; }
                }
                fout.write(encstr, 0, cnt);
            } while (cnt == buf_len);

            fin.close();
            fout.close();
        }
        catch (IOException exc) {
             throw exc;
        }
    }

    void decode(String input, String output) throws IOException {
        encode(input, output);
    }
}
