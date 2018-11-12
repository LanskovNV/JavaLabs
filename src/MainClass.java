import java.io.IOException;
import java.util.EnumMap;

public class MainClass {
    public static void main(String Args[]) {
        Error log = new Error("C:\\POLY\\JavaLabs\\config\\log.txt");

        if (Args.length != 1) {
            log.message("incorrect cmd arguments, should be config file name");
            return;
        }

        try {
            Grammar gr = new Grammar(Args[0]);
            EnumMap<keys, String> map = gr.parser();
            String params_fn = map.get(keys.params);
            String input_fn = map.get(keys.input);
            String output_fn = map.get(keys.output);
            String s = "11.txt";
            Crypt xor = new Crypt(params_fn);
            xor.encode(input_fn, output_fn);
            xor.decode(output_fn, s);
        }
        catch (IOException exc) {
            log.message(exc.toString());
        }
    }
}
