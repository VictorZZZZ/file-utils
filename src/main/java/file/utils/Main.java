package file.utils;


import file.utils.visitors.RemoveEmptyDirsVisitor;
import file.utils.visitors.RemoveOldFileVisitor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Main {
    private static final String REMOVE_EMPTY_FOLDERS = "-removeEmptyFolders";
    private static final String REMOVE_FILES_OLDER = "-removeFilesOlder";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments! Try -h for help.");
            return;
        }
        if (args.length == 1) {
            if (args[0].equals("-h")) {
                showHelp();
            } else {
                wrongArgs();
            }
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(REMOVE_EMPTY_FOLDERS)) {
                try {
                    removeEmptyFolders(args[i + 1]);
                    i++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    wrongArgs();
                }
            } else if(args[i].equals(REMOVE_FILES_OLDER)){
                String folder = args[i + 1];
                int days = Integer.parseInt(args[i + 2]);
                i+=2;
                removeFilesOlder(folder,days);
            }
        }
    }

    private static void removeFilesOlder(String folder, int days) {
        Path path = Paths.get(folder);
        if (Files.isDirectory(path)) {
            System.out.printf("Clear all files older than %d days \n", days);
            try {
                RemoveOldFileVisitor removeOldFilesVisitor = new RemoveOldFileVisitor(days);
                Files.walkFileTree(path, removeOldFilesVisitor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.printf("%s is not a Directory%n", folder);
        }
    }

    private static void removeEmptyFolders(String folder) {
        Path path = Paths.get(folder);
        if (Files.isDirectory(path)) {
            System.out.printf("Clear all empty folders in %s \n", path.toAbsolutePath().toString());
            try {
                RemoveEmptyDirsVisitor removeEmptyFilesVisitor = new RemoveEmptyDirsVisitor();
                Files.walkFileTree(path, removeEmptyFilesVisitor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }  else {
            System.out.printf("%s is not a Directory%n", folder);
        }
    }


    private static void removeEmptyFolders_(String folder) {
        Path path = Paths.get(folder);
        AtomicInteger counter = new AtomicInteger();
        if (Files.isDirectory(path)) {
            System.out.printf("Clear all empty folders in %s \n", path.toAbsolutePath().toString());
            try {
                System.out.println("Folders to delete:");
                Files.list(path).filter(Files::isDirectory).filter(file -> {
                    try {
                        return Files.list(file).count() == 0;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }).forEach(dir -> {
                    try {
                        System.out.println(dir.toAbsolutePath().toString());
                        counter.getAndIncrement();
                        Files.delete(dir);
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.error("Could not delete file:{}", dir.toAbsolutePath());
                    }
                });
                System.out.println("Deleted " + counter + "dirs!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.printf("%s is not a Directory%n", folder);
        }
    }

    private static void showHelp() {
        String helpText = """
                
                File Utils by Zalevskii Victor - zvictormail@gmail.com
                            
                HELP:
                            
                  -removeEmptyFolders "path_to_folder" - removes all empty folders in specified folder
                
                  -removeFilesOlder "path_to_folder" days - remove all files older than X days
                """;
        System.out.println(helpText);
    }

    private static void wrongArgs() {
        System.out.println("Wrong arguments. Try -h for help.");
    }
}
