package file.utils.visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

public class RemoveOldFileVisitor implements FileVisitor<Path> {
    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
    private int days;

    public RemoveOldFileVisitor(int days) {
        this.days = days;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        long now = FileTime.from(Instant.now()).toMillis();
        long fileCreated = attrs.creationTime().toMillis();
        String creationDate = attrs.creationTime().toInstant().toString().substring(0, 10);
        System.out.print(file.toString() + " - " + creationDate + " -");
        long liveTimeInDays = (now - fileCreated) / MILLIS_PER_DAY;
        System.out.print(" " + liveTimeInDays + " days");
        if (liveTimeInDays > days) Files.delete(file);
        System.out.println();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }
}
