package Source;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ManagerGrammar {
    String fileName;
    EnumMap ans;


    static final Map<String, BaseKeys> strEnum = new TreeMap<String, BaseKeys>();
    static {
        strEnum.put("input", BaseKeys.input);
        strEnum.put("output", BaseKeys.output);
        strEnum.put("params", BaseKeys.params );
        strEnum.put("keyword", BaseKeys.keyword);
        strEnum.put("buffer_len", BaseKeys.buffer_len);
    }
    public ManagerGrammar(String s) {
        fileName = "//home//leins275//Projects//JavaLabs//config//" + s;
        ans = new EnumMap<BaseKeys, String>(BaseKeys.class);
    }

    public EnumMap <BaseKeys, String> parser() throws IOException{
        File cfg = new File(fileName);
        Scanner scanner;

        try {
            scanner = new Scanner(cfg);

            while (scanner.hasNextLine()) {
                String tmp = scanner.nextLine();
                String[] parts = tmp.split(":", 2);
                BaseKeys k;

                k = strEnum.get(parts[0].trim());
                if (k == null) {
                    k = BaseKeys.error;
                }
                if (k != BaseKeys.error) {
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
