package javi.tests;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import javi.api.tools.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author VISTALL
 * @since 01/02/2021
 */
public class JaviTest {
    @Rule
    public TestName myTestName = new TestName();

    @Test
    public void testTextBlockSource() throws Exception {
        JavaCompiler javiCompiler = JaviUtil.getJaviCompiler();

        DiagnosticListener<JavaFileObject> diagnosticListener = new DiagnosticListener<>() {
            @Override
            public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
                System.out.println(diagnostic);
            }
        };

        URL location = JaviTest.class.getProtectionDomain().getCodeSource().getLocation();

        File dir = new File(location.toURI());

        String methodName = myTestName.getMethodName();

        File testSource = new File(dir, "testdata/" + methodName.substring(4, methodName.length()) + ".java");

        FileSystem fileSystem = MemoryFileSystemBuilder.newEmpty().build();

        Path targetDir = fileSystem.getPath("target/");
        Files.createDirectories(targetDir);

        StandardJavaFileManager fileManager = javiCompiler.getStandardFileManager(diagnosticListener, Locale.US, StandardCharsets.UTF_8);
        fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, List.of(targetDir));

        Iterable<? extends JavaFileObject> paths = fileManager.getJavaFileObjectsFromPaths(List.of(testSource.toPath()));

        List<String> options = new ArrayList<>();
        options.add("-target");
        options.add("11");
        
        JavaCompiler.CompilationTask task = javiCompiler.getTask(new PrintWriter(System.out), fileManager, diagnosticListener, options, List.of(), paths);

        if(!task.call()) {
            throw new IllegalArgumentException("compilation failed");
        }

        Files.walkFileTree(targetDir, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
