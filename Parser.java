class Parser {
    private String commandName;
    private String[] args;

    public boolean parse(String input) {
        String[] tokens = input.split("\\s+", 2);

        if (tokens.length > 0) {
            commandName = tokens[0];
            args = tokens.length > 1 ? tokens[1].split("\\s+") : new String[0];
            return true;
        }
        return false;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}