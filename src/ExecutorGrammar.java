/**
 * enum Executor Grammar
 */
public enum ExecutorGrammar {
    task,                     //executor's task (encode/decode)
    keyword,                  //keyword to XOR with
    bufsize,                  //the buffer's size (to code)
    firstSymbolNum            //the number of first symbol to read from (the shift)
}
