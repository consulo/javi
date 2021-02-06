package javi.tests;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import javi.api.tools.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
        doTest();
    }

    @Test
    public void testSwitchYieldSource() throws Exception {
        doTest();
    }

    @Test
    public void testRecordPointSource() throws Exception {
        doTest();
    }

    @Test
    public void testRecordSuper() throws Exception {
        Path path = doTest();

        String[] superClass = new String[1];
        boolean[] enterRecordComponent = new boolean[1];

        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
                enterRecordComponent[0] = true;
                return null;
            }

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                superClass[0] = superName;
            }
        };

        ClassReader cr = new ClassReader(Files.readAllBytes(path));

        cr.accept(visitor, ClassReader.SKIP_CODE);

        if(enterRecordComponent[0]) {
            throw new IllegalArgumentException("Unexpected record component");
        }
        
        if("java/lang/Record".equals(superClass[0])) {
            throw new IllegalArgumentException("Unexpected super record");
        }
    }

    private Path doTest() throws Exception {
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

        String fileName = methodName.substring(4, methodName.length());
        File testSource = new File(dir, "testdata/" + fileName + ".java");

        FileSystem fileSystem = MemoryFileSystemBuilder.newEmpty().build();

        Path targetDir = fileSystem.getPath("target/");
        Files.createDirectories(targetDir);

        StandardJavaFileManager fileManager = javiCompiler.getStandardFileManager(diagnosticListener, Locale.US, StandardCharsets.UTF_8);
        fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, List.of(targetDir));

        Iterable<? extends JavaFileObject> paths = fileManager.getJavaFileObjectsFromPaths(List.of(testSource.toPath()));

        List<String> options = new ArrayList<>();
        options.add("-target");
        options.add("11");

        options.add("-source");
        options.add("11");

        JavaCompiler.CompilationTask task = javiCompiler.getTask(new PrintWriter(System.out), fileManager, diagnosticListener, options, List.of(), paths);

        if (!task.call()) {
            throw new IllegalArgumentException("compilation failed");
        }

        Path[] ref = new Path[1];
        Files.walkFileTree(targetDir, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String filePath = file.toString();

                if(file.getFileName().toString().equals(fileName + ".class")) {
                    ref[0] = file;
                }

                byte[] bytes = Files.readAllBytes(file);

                ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);

                int magic = buffer.getInt();
                if (magic != 0xCAFEBABE) {
                    throw new IOException(filePath + " magic failed");
                }

                short minor = buffer.getShort();
                short major = buffer.getShort();

                if (major != 55) {
                    throw new IOException(filePath + "major faile");
                }
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

        if(ref[0] == null) {
            throw new IllegalArgumentException("Can't find result");
        }

        return ref[0];
    }
}
