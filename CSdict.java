
// You can use this file as a starting point for your dictionary client
// The file contains the code for command line parsing and it also
// illustrates how to read and partially parse the input typed by the user. 
// Although your main class has to be in this file, there is no requirement that you
// use this template or hav all or your classes in this file.

import java.lang.System;
import java.io.IOException;
import java.util.Arrays;

//
// This is an implementation of a simplified version of a command
// line dictionary client. The only argument the program takes is
// -d which turns on debugging output.
//


public class CSdict {
    static final int MAX_LEN = 255;
    static Boolean debugOn = false;

    private static final int PERMITTED_ARGUMENT_COUNT = 1;
    private static String command;
    private static String[] arguments;

    public static DictConnection connect;

    public static void main(String [] args) {
        byte cmdString[] = new byte[MAX_LEN];
        int len;
        // Verify command line arguments

        if (args.length == PERMITTED_ARGUMENT_COUNT) {
            debugOn = args[0].equals("-d");
            if (debugOn) {
                System.out.println("Debugging output enabled");
            } else {
                System.out.println("997 Invalid command line option - Only -d is allowed");
                return;
            }
        } else if (args.length > PERMITTED_ARGUMENT_COUNT) {
            System.out.println("996 Too many command line options - Only -d is allowed");
            return;
        }

        while (true) {
            try {
                System.out.print("csdict> ");
                System.in.read(cmdString);

                // Convert the command string to ASII
                String inputString = new String(cmdString, "ASCII");

                // BUG Note: using cmdString is so wired, it will remember the last command.
                Arrays.fill(cmdString, (byte) 0);

                // Split the string into words
                String[] inputs = inputString.trim().split("( |\t)+");
                // Set the command
                command = inputs[0].toLowerCase().trim();
                // Remainder of the inputs is the arguments.
                arguments = Arrays.copyOfRange(inputs, 1, inputs.length);

                // Empty lines and lines starting with the character '#' are to be silently ignored, and a new prompt displayed.
                if (command.isEmpty() || command.startsWith("#")) continue;

//                System.out.println(command);
                switch (command) {
                    case "open":
                        openToHost();
                        break;
                    case "dict":
                        dict();
                        break;
                    case "set":
                        setDict();
                        break;
                    case "define":
                        defineWord();
                        break;
                    case "match":
                        matchWord();
                        break;
                    case "prefixmatch":
                        prefixmatchWord();
                        break;
                    case "close":
                        close();
                        break;
                    case "quit":
                        if (!validNumArguments(0)) break;
                        quit();
                        System.exit(-1);
                        break;
                    // debug for set Dict
                    case "get":
                        System.out.println(connect.getDict());
                        break;
                    default:
                        System.out.println("900 Invalid command.");
                }
            } catch (IOException exception) {
                System.err.println("998 Input error while reading commands, terminating.");
                System.exit(-1);
            }
        } // while
    }

    // same as netcat
    public static void openToHost() {
        if (!validNumArguments(2)) return;

        String server = arguments[0];
        int portNumber;
        try {
            portNumber = Integer.parseInt(arguments[1]);
        } catch (NumberFormatException e) {
            System.out.println("902 Invalid argument.");
            return;
        }

        connect = new DictConnection(server, portNumber, debugOn);
    }

    // SHOW DB in dict.org 2628
    public static void dict() {

        if (!validConnect()) return;

        if (!validNumArguments(0)) return;

        try {
            connect.dict();
        } catch (NullPointerException e) {
            connect = null;
            System.err.println("903 Supplied command not expected at this time.");
        }

    }

    // set dictionary for using define and match strategies in the future
    // default dictionary is *
    private static void setDict() {

        if (!validConnect()) return;

        if (!validNumArguments(1)) return;

        String dictionary = arguments[0];

        try {
            connect.setDict(dictionary);
        } catch (NullPointerException e) {
            connect = null;
            System.err.println("903 Supplied command not expected at this time.");
        }
    }

    private static boolean validNumArguments(int numArg) {
        if (arguments.length != numArg) {
            System.err.println("901 Incorrect number of arguments");
            return false;
        }
        return true;
    }

    private static  boolean validConnect() {
        if (connect == null) {
            System.err.println("903 Supplied command not expected at this time.");
            return false;
        }
        return true;
    }

    // DEFINE database word
    private static void defineWord() {

        if (!validConnect()) return;

        if (!validNumArguments(1)) return;

        String word = arguments[0];

        try {
            connect.defineWord(word);
        } catch (NullPointerException e) {
            connect = null;
            System.err.println("903 Supplied command not expected at this time.");
        }

    }

    // MATCH database exact word
    private static void matchWord() {

        if (!validConnect()) return;

        if (!validNumArguments(1)) return;

        String word = arguments[0];

        try {
            connect.matchWord(word);
        } catch (NullPointerException e) {
            connect = null;
            System.err.println("903 Supplied command not expected at this time.");
        }
    }

    private static void prefixmatchWord() {

        if (!validConnect()) return;

        if (!validNumArguments(1)) return;

        String word = arguments[0];

        try {
            connect.prefixWord(word);
        } catch (NullPointerException e) {
            connect = null;
            System.err.println("903 Supplied command not expected at this time.");
        }
    }

    // close the server dict.org 2628
    private static void close() {

        if (!validConnect()) return;

        if (!validNumArguments(0)) return;

        try {
            connect.close();
            connect = null;
        } catch (NullPointerException e) {
            connect = null;
            System.err.println("903 Supplied command not expected at this time.");
        }
    }

    private static void quit() {

        if (connect == null) return;

        try {
            connect.quit();
        } catch (NullPointerException e) {
            System.err.println("903 Supplied command not expected at this time.");
        }
    }
}
    
    
