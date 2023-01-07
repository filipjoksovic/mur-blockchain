package console;

import java.time.LocalDateTime;

public class Logger {
    public String className;
    public Level level;
    public String message;

    public Logger(String className) {
        this.className = className;
    }


    public void log(String message) {
        System.out.println(ConsoleColor.BLUE_BOLD + "(" + className + ") " + ConsoleColor.CYAN + "[" + Level.INFO.name() + "]" + " {" + LocalDateTime.now() + "} - " + message + ConsoleColor.RESET);
    }

    public void log(Level level, String message) {
        if (level == Level.CRITICAL) {
            System.out.println(ConsoleColor.BLUE_BOLD + "(" + className + ") " + ConsoleColor.RED_BOLD + "[" + Level.CRITICAL.name() + "]" + " {" + LocalDateTime.now() + "} - " + message + ConsoleColor.RESET + Thread.currentThread().getStackTrace()[2].getLineNumber());

        } else if (level == Level.INFO) {
            System.out.println(ConsoleColor.BLUE_BOLD + "(" + className + ") " + ConsoleColor.CYAN + "[" + Level.INFO.name() + "]" + " {" + LocalDateTime.now() + "} - " + message + ConsoleColor.RESET + Thread.currentThread().getStackTrace()[2].getLineNumber());

        } else if (level == Level.ERROR) {
            System.out.println(ConsoleColor.BLUE_BOLD + "(" + className + ") " + ConsoleColor.YELLOW + "[" + Level.ERROR.name() + "]" + " {" + LocalDateTime.now() + "} - " + message + ConsoleColor.RESET + Thread.currentThread().getStackTrace()[2].getLineNumber());

        } else if (level == Level.SUCCESS) {
            System.out.println(ConsoleColor.BLUE_BOLD + "(" + className + ") " + ConsoleColor.GREEN + "[" + Level.SUCCESS.name() + "]" + " {" + LocalDateTime.now() + "} - " + message + ConsoleColor.RESET + Thread.currentThread().getStackTrace()[2].getLineNumber());

        }
    }
}
