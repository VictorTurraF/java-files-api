import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        Path path = Path.of("assets", "notes.txt");
        System.out.println(path);

        // Print absolute path
        System.out.println("Absolute path: " + path.toAbsolutePath());

        // Shows last modified time of file
        FileTime fileTime = Files.getLastModifiedTime(path);
        System.out.println("Last modified time: " + fileTime);

        // Shows if file exists
        Boolean fileExists = Files.exists(path);
        System.out.println("Exists: " + fileExists);

        // Files comparison
        Path path2 = Path.of("assets", "notes-copy.txt");
        long mismatchIndex = Files.mismatch(path, path2);
        System.out.println("Mismatch index: " + mismatchIndex); // -1 if files are the same

        // Get the owner of the file
        UserPrincipal owner = Files.getOwner(path);
        System.out.println("Owner: " + owner);

        // Create temp file
        Path tempFile = Files.createTempFile("temp", ".txt");
        System.out.println("Temp file: " + tempFile);

        // Create temp directory
        Path tempDir = Files.createTempDirectory("temp");
        System.out.println("Temp directory: " + tempDir);

        // Delete temp file
        Files.delete(tempFile); // Can throw DirectoryNotEmptyException
        System.out.println("Temp file deleted");

        // Delete temp directory
        Files.delete(tempDir); // Can throw DirectoryNotEmptyException
        System.out.println("Temp directory deleted");

        // Create directory
        Path newDir = Path.of("assets", "tasks");

        if (!Files.exists(newDir)) {
            Files.createDirectory(newDir);
        }

        System.out.println("Directory created: " + newDir);

        // Create a file
        Path newFilePath = newDir.resolve("task.txt");

        if (!Files.exists(newFilePath)) {
            Files.createFile(newFilePath);
        }

        System.out.println("File created: " + newFilePath);

        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(path);
        System.out.println("Permissions: " + permissions);

        Files.writeString(newFilePath, "Hello World!");
        System.out.println("File content: " + Files.readString(newFilePath));

        Path autoCreatedFile = newDir.resolve("auto-created.txt");
        Files.write(autoCreatedFile, "this is my string ää öö üü".getBytes(StandardCharsets.UTF_8),
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        System.out.println("File (auto created and edited) content: " + Files.readString(autoCreatedFile));

        // List files in directory
        System.out.println("Files in directory (assets/tasks): ");
        try (var files = Files.list(newDir)) {
            files.forEach(System.out::println);
        }

        // List files in directory using a directory stream with a globe pattern
        System.out.println("Files in directory (assets/tasks) using a directory stream with a globe pattern: ");
        try (var files = Files.newDirectoryStream(newDir, "*.txt")) {
            files.forEach(System.out::println);
        }

        // List files recursively
        System.out.println("Files in directory (assets) recursively: ");
        try (var files = Files.walk(Path.of("assets"))) {
            files.forEach(System.out::println);
        }

        // Relative path
        Path relative = Path.of("./assets/tasks");
        System.out.println("Relative path: " + relative + " is absolute: " + relative.isAbsolute());

        // Absolute path
        Path absolute = relative.toAbsolutePath();
        System.out.println("Absolute path: " + absolute + " is absolute: " + absolute.isAbsolute());

        // Normalize path
        Path normalized = absolute.normalize();
        System.out.println("Normalized path: " + normalized + " is absolute: " + normalized.isAbsolute());

        // Relativize path
        Path relativized = Path.of("/home/victorturra/Documentos/learning").relativize(normalized);
        System.out.println("Relativized path: " + relativized + " is absolute: " + relativized.isAbsolute());

        // Moving file
        Path destination = Path.of("assets", "new-tasks");

        if (!Files.exists(destination)) {
            Files.createDirectory(destination);
        }

        Path movedFile = newDir.resolve("to-be-moved.txt");
        Path movedFileDestination = destination.resolve("moved.txt");

        if (!Files.exists(movedFile) && !Files.exists(movedFileDestination)) {
            Files.createFile(movedFile);
            Files.move(movedFile, movedFileDestination); // destination can have a different name
        }

        System.out.println("Moved file: " + movedFile + " to: " + movedFileDestination);

        // Delete a not empty directory
        try (Stream<Path> walk = Files.walk(newDir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(pathToBeDeleted -> {
                try {
                    Files.delete(pathToBeDeleted);
                } catch (IOException e) {
                    // something could not be deleted
                    e.printStackTrace();
                }
            });
        }
        System.out.println("Directory deleted: " + newDir);

    }
}