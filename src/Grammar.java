import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Grammar {
    String fileName;
    EnumMap ans;

    static final Map<String, keys> strEnum = new TreeMap<String, keys>();
    static {
        strEnum.put("input", keys.input);
        strEnum.put("output", keys.output);
        strEnum.put("params", keys.params );
        strEnum.put("keyword", keys.keyword);
        strEnum.put("buffer_len", keys.buffer_len);
    }
    Grammar(String s) {
        fileName = s;
        ans = new EnumMap<keys, String>(keys.class);
    }

    EnumMap <keys, String> parser() throws IOException{
        File cfg = new File("D:\\YandexDisk\\JavaProj\\config\\" + fileName );
        Scanner scanner;

        try {
            scanner = new Scanner(cfg);

            while (scanner.hasNextLine()) {
                String tmp = scanner.nextLine();
                String[] parts = tmp.split(":", 2);
                keys k;

                k = strEnum.get(parts[0].trim());
                if (k == null) {
                    k = keys.error;
                }
                if (k != keys.error) {
                    ans.put(k, parts[1].trim());
                }
            }
            scanner.close();
        }
        catch (IOException exc) {
            throw exc;
        }

        return ans;
    }
}
