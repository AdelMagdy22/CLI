import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.util.Arrays;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;


public class Terminal {
    private Parser parser;
    private String currentDirectory;
    private List<String> commandHistory = new ArrayList<>();

    public Terminal() {
        this.parser = new Parser();
        this.currentDirectory = "/";
    }

    public void setHistory(String str)
    {
        this.commandHistory.add(str); 
    }

    public String pwd() {
        return "Current directory: " + currentDirectory;
    }

    
public void cd(String[] args) {
        if (args.length == 0) {
            currentDirectory = System.getProperty("user.home");
            System.out.println("Changed directory to home: " + currentDirectory);
        } 
        else if (args.length >= 1) {
            if (args[0].equals("..")) {
                File currentDirFile = new File(currentDirectory);
                if (currentDirFile.getParent() != null) {
                    currentDirectory = currentDirFile.getParent();
                    System.out.println("Changed directory to parent: " + currentDirectory);
                } else {
                    System.out.println("You are in the parent directory: " + currentDirFile);
                }
            } else {
                String new_args = "";
                for (int i = 0; i < args.length - 1; i++) {
                    new_args += args[i] + " ";
                }
                new_args += args[args.length - 1];
                File newDir = new File(new_args);
        
                if (newDir.isAbsolute() || newDir.getPath().startsWith("..")) {
                    if (newDir.exists() && newDir.isDirectory()) {
                        try {
                            currentDirectory = newDir.getCanonicalPath();
                            System.out.println("Changed directory to: " + currentDirectory);
                        } catch (IOException e) {
                            System.out.println("Invalid path: " + newDir.getPath());
                        }
                    } else {
                        System.out.println("Invalid directory: " + newDir.getPath());
                    }
                } else {
                    newDir = new File(currentDirectory,new_args);
                    if (newDir.exists() && newDir.isDirectory()) {
                        try {
                            currentDirectory = newDir.getCanonicalPath();
                            System.out.println("Changed directory to: " + currentDirectory);
                        } catch (IOException e) {
                            System.out.println("Invalid path: " + newDir.getPath());
                        }
                    } else {
                        System.out.println("Invalid directory: " + newDir.getPath());
                    }
                }
            }
        } 
        else {
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
        File dir = new File(currentDirectory);
        String[] files = dir.list();
        if (files != null) {
            Arrays.sort(files, Comparator.reverseOrder());
            for (String file : files) {
                System.out.println(file);
            }
        } else {
            System.out.println("Error reading directory");
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

    public boolean isPath(String inputString) 
    {
        return inputString.contains("\\");
    }
    public void wc(String[] args) {
        Path filePath;       
        if (args.length == 1) {
            if (isPath(args[0]))
            {
                filePath = Paths.get(args[0]);
            }
            else
            {
                filePath = Paths.get(currentDirectory+'\\'+args[0]);
            }
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
            } 
            catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
                }
            } 
            else {
            System.out.println("File not found: " + args[0]);
            }
            
        } else {
            System.out.println("Invalid arguments for wc command");
        }
    }

    public void cp(String[] args) {
        if (args.length == 2) {
            Path source = Paths.get(args[0]);
            Path target = Paths.get(args[1]);

            try {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Copied " + args[0] + " to " + args[1]);
            } catch (IOException e) {
                System.out.println("Error copying file: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid arguments for cp command");
        }
    }

    public static String targetPath(String str1,String str2)
    {
        String[] strArray = str1.split("\\\\");
        return str2+"\\"+strArray[strArray.length-1];
    }
    public void cpR(String[] args) {
        Path source;
        Path target;
        if (args.length == 3) {
            if(isPath(args[1]) && isPath(args[2]))
            {
                source = Paths.get(args[1]);
                target = Paths.get(targetPath(args[1],args[2]));
            }
            else if (!isPath(args[1]) && !isPath(args[2]))
            {
                source = Paths.get(currentDirectory+'\\'+args[1]);
                target = Paths.get(targetPath(currentDirectory+'\\'+args[1],currentDirectory+'\\'+args[2]));
            }
            else if (!isPath(args[1]) && isPath(args[2]))
            {
                source = Paths.get(currentDirectory+'\\'+args[1]);
                target = Paths.get(targetPath(args[1],args[2]));
            }
            else
            {
                source = Paths.get(args[1]);
                target = Paths.get(targetPath(currentDirectory+'\\'+args[1],currentDirectory+'\\'+args[2]));
            }

            try {
                Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path targetDir = target.resolve(source.relativize(dir));
                        Files.createDirectories(targetDir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Recursively copied " + args[1] + " to " + args[2]);
            } catch (IOException e) {
                System.out.println("Error copying directory: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid arguments for cp -r command");
        }
    }

    public void mkdir(String[] args) {
        Path directory;
        if (args.length >= 1) {
            for (int i=0 ; i<args.length;i++){
                if (isPath(args[i]))
                {
                    directory = Paths.get(args[i]);
                }
                else
                {
                    directory = Paths.get(currentDirectory+'\\'+args[i]);
                }
                if (Files.exists(directory) && Files.isDirectory(directory))
                {
                    System.out.println("this directory exist already");
                }
                else
                {
                    try {

                    Files.createDirectories(directory);
                    System.out.println("Created directory: " + args[i]);
                    } 
                    catch (IOException e) {
                        System.out.println("Error creating directory: " + e.getMessage());
                    }
                }
            }
            
            
        } else {
            System.out.println("Invalid arguments for mkdir command");
        }
    }
    public void touch(String[] args) {
        if (args.length >= 1) {
            Path filePath;
            for (String arg : args) {
                if (arg.contains("\\")) {
                    filePath = Paths.get(arg);
                } else{
                    filePath = Paths.get(currentDirectory + arg);
                }
                try {
                    if (Files.exists(filePath)) {
                        System.out.println("File already exists in: " + filePath);
                    } else {
                        Files.createFile(filePath);
                        System.out.println("Created a new file: " + filePath);
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while creating the file: " + e.getMessage());
                }
            }
        } else {
            System.out.println("An error occurred while creating the file");
        }
    }
    public void rm(String[] args) {
        if (args.length == 1) {
            Path filePath;
            if (args[0].contains("\\")) {
                filePath = Paths.get(args[0]);
            } else {
                filePath = Paths.get(currentDirectory + args[0]);
            }
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("File at: " + filePath + " has been deleted");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while deleting the file: " + e.getMessage());
            }
        } else {
            System.out.println("An error occurred while deleting the file");
        }
    }
    
    public void cat(String[] args) {
        if (args.length >= 1) {
            for (String arg : args) {
                Path filePath;
                if (arg.contains("\\")) {
                    filePath = Paths.get(arg);
                } else {
                    filePath = Paths.get(currentDirectory, arg);
                }
                if (Files.exists(filePath)) {
                    try {
                        List<String> lines = Files.readAllLines(filePath);
                        for (String line : lines) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        System.out.println("An error occurred while reading the file: " + e.getMessage());
                    }
                } else {
                    System.out.println("File not found: " + filePath);
                }
            }
        }
    }

    public void history() 
    {
        
        for(int i=0; i<this.commandHistory.size()-1;i++)
            {
                System.out.println(this.commandHistory.get(i));
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
                if(args.length == 0)
                    ls();
                else
                    lsR();
                break;
            case "rmdir":
                rmdir(args);
                break;
            case "wc":
                wc(args);
                break;
            case "mkdir":
                mkdir(args);
                break;
            case "history":
                history();
                break;
            case "cp":
                if(args.length==3)
                {
                    cpR(args);
                }
                else
                {
                    cp(args);
                }
                
                break;
            case "touch":
                touch(args);
                break;
            case "rm":
                rm(args);
                break;
            case "cat":
                cat(args);
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
            terminal.setHistory(input);
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
