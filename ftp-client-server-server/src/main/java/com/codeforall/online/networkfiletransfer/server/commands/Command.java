package com.codeforall.online.networkfiletransfer.server.commands;

public enum Command {

    BYE(1,"Terminate the connection"),
    DISCONNECT(2,"Terminate the connection"),
    QUIT(3,"Terminate the connection"),
    HELP(4,"See available commands"),
    LIST(5,"List files available on the server"),
    PUT(6,"Upload a file to the server"),
    GET(7,"Get a file from the server"),
    MKDIR(8,"Create directory in the server");

    private final int choiceNum;
    private final String description;

    Command(int choice, String description) {
        this.description = description;
        this.choiceNum = choice;
    }

    public String getDescription() {
        return description;
    }

    public int getChoice() {
        return choiceNum;
    }
//  Method used for text only files, deprecated
//    public static Command fromNumber(int number) {
//        for (Command cmd : Command.values()) {
//            if (cmd.choiceNum == number) {
//                return cmd;
//            }
//        }
//        return null;
//    }

    public static Command fromString(String text) {
        if (text == null) return null;
        String command = text.toLowerCase().split("\\s+")[0];
        for (Command cmd : Command.values()) {
            if (cmd.name().equalsIgnoreCase(command)) {
                return cmd;
            }
        }
        return null;
    }
}

