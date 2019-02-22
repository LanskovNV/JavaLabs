public class Main {

    //точка входа
    public static void main(String[] args) {
        /* log init */
        if (ErrorLog.Init() != 0) /* if the log wasn't created*/
            return;

        /* if there was not arguments in command line */
        if(args.length > 0)
        {
            /* creation of the main interpreter */
            BaseParser interpreter = new BaseParser();
            /* parse main configuration file */
            if(interpreter.parseConfig(args[0]) != 0)
                return;

            /* creation of manager */
            Manager manager = new Manager();
            if (manager.openStreams(interpreter.getConfig()) != 0) /* if streams weren't opened*/
                return;
            if(manager.createPipeline(interpreter.getConfig()) != 0) /* if the pipeline wasn't created*/
                return;

            /* run pipeline */
            manager.run();
            /* close all files which were opened */
            manager.closeStreams();
        }
        else {
            ErrorLog.sendMessage("command line was empty");
        }
        ErrorLog.close();
    }
}
