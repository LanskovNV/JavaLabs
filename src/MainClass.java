import Algoritms.Xor;
import Source.Error;
import Source.Grammar;
import Source.keys;

import java.io.IOException;
import java.util.EnumMap;

public class MainClass {
    public static void main(String Args[]) {
        Error log = new Error("//home//leins275//Projects//JavaLabs//config//log.txt");

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

            /* here we have to create manager and start */
            String s = "11.txt";
            Xor xor = new Xor(params_fn);
            xor.encode(input_fn, output_fn);
            xor.decode(output_fn, s);
        }
        catch (IOException exc) {
            log.message(exc.toString());
        }
    }
}
