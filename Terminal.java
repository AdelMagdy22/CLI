import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public class Terminal {
    private Parser parser;
    private String currentDirectory;

    public Terminal() {
        this.parser = new Parser();
        this.currentDirectory = "/";
    }

    public String pwd() {
        return "Current directory: " + currentDirectory;
    }

    public void cd(String[] args) {
        if (args.length == 0) {
            // Case 1: cd takes no arguments and changes the current path to the home directory.
            currentDirectory = System.getProperty("user.home");
            System.out.println("Changed directory to home: " + currentDirectory);
        } else if (args.length == 1) {
            if (args[0].equals("..")) {
                // Case 2: cd takes 1 argument which is “..” and changes the current directory to the previous directory.
                File currentDirFile = new File(currentDirectory);
                currentDirectory = currentDirFile.getParent();
                System.out.println("Changed directory to parent: " + currentDirectory);
            } else {
                // Case 3: cd takes 1 argument which is either the full path or the relative path and changes the current path to that path.
                File newDir = new File(args[0]);

                if (newDir.isAbsolute()) {
                    // Handle full path
                    try {
                        currentDirectory = newDir.getCanonicalPath();
                        System.out.println("Changed directory to: " + currentDirectory);
                    } catch (IOException e) {
                        System.out.println("Invalid path: " + newDir.getPath());
                    }
                } else {
                    // Handle relative path
                    newDir = new File(currentDirectory, args[0]);
                    try {
                        currentDirectory = newDir.getCanonicalPath();
                        System.out.println("Changed directory to: " + currentDirectory);
                    } catch (IOException e) {
                        System.out.println("Invalid path: " + newDir.getPath());
                    }
                }
            }
        } else {
            System.out.println("Invalid arguments for cd command");
        }
    }


    public void echo(String[] args) {
        System.out.println(String.join(" ", args));
    }

    public void ls() {
        File dir = new File(currentDirectory);
        String[] files = dir.list();

        if (files != null) {
            Arrays.sort(files);
            for (String file : files) {
                System.out.println(file);
            }
        } else {
            System.out.println("Error reading directory");
        }
    }

    public void lsR() {
        listFilesRecursive(new File(currentDirectory));
    }

    private void listFilesRecursive(File directory) {
        File[] files = directory.listFiles();

        if (files != null) {
            Arrays.sort(files, (f1, f2) -> f2.getName().compareTo(f1.getName()));
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println(file.getPath());
                    listFilesRecursive(file);
                } else {
                    System.out.println(file.getName());
                }
            }
        }
    }

    public void rmdir(String[] args) {
        if (args.length == 1) {
            if (args[0].equals("*")) {
                // Case 1: rmdir takes 1 argument which is “*” and removes all empty directories in the current directory.
                removeEmptyDirectories(currentDirectory);
            } else {
                // Case 2: rmdir takes 1 argument which is either the full path or the relative path and removes the given directory only if it is empty.
                File dir = new File(args[0]);

                if (dir.exists() && dir.isDirectory()) {
                    if (dir.list().length == 0) {
                        if (dir.delete()) {
                            System.out.println("Removed directory: " + args[0]);
                        } else {
                            System.out.println("Error removing directory: " + args[0]);
                        }
                    } else {
                        System.out.println("Directory is not empty: " + args[0]);
                    }
                } else {
                    System.out.println("Invalid directory: " + args[0]);
                }
            }
        } else {
            System.out.println("Invalid arguments for rmdir command");
        }
    }

    private void removeEmptyDirectories(String directory) {
        File dir = new File(directory);
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.list().length == 0) {
                    if (file.delete()) {
                        System.out.println("Removed empty directory: " + file.getPath());
                    } else {
                        System.out.println("Error removing empty directory: " + file.getPath());
                    }
                }
            }
        }
    }

    public void wc(String[] args) {
        if (args.length == 1) {
            Path filePath = Paths.get(args[0]);

            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                try {
                    long lineCount = Files.lines(filePath).count();
                    long wordCount = Files.lines(filePath)
                            .flatMap(line -> Arrays.stream(line.split("\\s+")))
                            .filter(word -> !word.isEmpty())
                            .count();
                    long charCount = Files.lines(filePath)
                            .mapToLong(String::length)
                            .sum();

                    System.out.println(lineCount + " " + wordCount + " " + charCount + " " + filePath.getFileName());
                } catch (IOException e) {
                    System.out.println("Error reading file: " + e.getMessage());
                }
            } else {
                System.out.println("File not found: " + args[0]);
            }
        } else {
            System.out.println("Invalid arguments for wc command");
        }
    }

    public void chooseCommandAction() {
        String commandName = parser.getCommandName();
        String[] args = parser.getArgs();

        switch (commandName) {
            case "exit":
                System.out.println("Exiting CLI");
                System.exit(0);
                break;
            case "pwd":
                System.out.println(pwd());
                break;
            case "cd":
                cd(args);
                break;
            case "echo":
                echo(args);
                break;
            case "ls":
                ls();
                break;
            case "ls -r":
                lsR();
                break;
            case "rmdir":
                rmdir(args);
                break;
            case "wc":
                wc(args);
                break;
            default:
                System.out.println("Unknown command: " + commandName);
        }
    }

    public static void main(String[] args) {
        Terminal terminal = new Terminal();

        while (true) {
            System.out.print("Enter command: ");
            String input = new java.util.Scanner(System.in).nextLine();

            if (input.equals("exit")) {
                break;
            }

            if (terminal.parser.parse(input)) {
                terminal.chooseCommandAction();
            } else {
                System.out.println("Invalid command");
            }
        }
    }
}
