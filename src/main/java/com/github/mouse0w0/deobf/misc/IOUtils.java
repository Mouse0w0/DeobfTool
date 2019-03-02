package com.github.mouse0w0.deobf.misc;

import com.github.mouse0w0.deobf.Main;
import com.google.common.collect.Streams;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class IOUtils {

    public static List<ClassNode> readAllClass(Path target) throws IOException {
        List<ClassNode> loadedClasses = new ArrayList<>();
        try (var jarFile = new JarFile(target.toFile())) {
            var it = Streams.stream(jarFile.entries().asIterator())
                    .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                    .iterator();
            while (it.hasNext()) {
                try (var inputStream = jarFile.getInputStream(it.next())) {
                    var cr = new ClassReader(inputStream);
                    var cn = new ClassNode();
                    cr.accept(cn, 0);
                    loadedClasses.add(cn);
                }
            }
            return loadedClasses;
        }
    }

    public static <T extends Entry> void writeEntries(Path path, List<T> entries) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            for (var entry : entries) {
                entry.serialize(writer);
                writer.write("\n");
            }
        }
    }

    public static <T extends Entry> List<T> readEntries(Path path, Supplier<T> supplier) throws IOException {
        return Files.readAllLines(path, StandardCharsets.UTF_8).parallelStream()
                .map(s -> s.split(","))
                .map(strings -> {
                            T entry = supplier.get();
                            entry.deserialize(strings);
                            return entry;
                        }
                )
                .collect(Collectors.toList());
    }
}
