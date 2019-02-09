package Algoritms;

import java.io.IOException;

public interface Crypt {
    void encode(String input, String output) throws IOException;
    void decode(String input, String output) throws IOException;
}
