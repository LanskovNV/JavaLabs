public class Main {

    public static void main(String[] args) {
        if (ErrorLog.Init() != 0)
            return;

        if(args.length > 0)
        {
            BaseParser interpreter = new BaseParser();
            if(interpreter.parseConfig(args[0]) != 0)
                return;

            Manager manager = new Manager();
            if (manager.openStreams(interpreter.getConfig()) != 0)
                return;
            if(manager.createPipeline(interpreter.getConfig()) != 0)
                return;

            manager.run();
            manager.closeStreams();
        }
        else {
            ErrorLog.sendMessage("command line was empty");
        }
        ErrorLog.close();
    }
}
