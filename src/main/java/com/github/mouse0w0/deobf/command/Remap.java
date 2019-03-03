package com.github.mouse0w0.deobf.command;

import com.github.mouse0w0.deobf.Main;
import com.github.mouse0w0.deobf.misc.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Remap {

    public static void onCommand(String command, String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<String> targetSpec = parser.accepts("target", "Target file, extension name must be jar.").withRequiredArg();
        OptionSpec<String> outputSpec = parser.accepts("output", "Output file, extension name must be jar.").withRequiredArg();
        OptionSpec<String> mappingSpec = parser.accepts("mapping", "Mapping file perfix.").withRequiredArg();
        OptionSpec<String> configSpec = parser.accepts("config", "Config file, file format is toml.").withRequiredArg();

        OptionSet set = parser.parse(args);

        if (!set.has(targetSpec)) {
            Main.LOGGER.error("Target file hasn't specify.");
            return;
        }

        Path target = Path.of(set.valueOf(targetSpec));

        if (!Files.exists(target)) {
            Main.LOGGER.error("Target file isn't exists.");
            return;
        }

        if (Files.isDirectory(target)) {
            Main.LOGGER.error("Target file isn't file.");
            return;
        }

        String targetFileName = target.getFileName().toString();
        if (!targetFileName.endsWith(".jar")) {
            Main.LOGGER.error("Target file isn't jar.");
            return;
        }

        Path output = Path.of(set.has(outputSpec) ? set.valueOf(outputSpec) : target.toAbsolutePath().getParent().toString() + "/" + targetFileName.substring(0, targetFileName.lastIndexOf('.')) + "_output.jar");

        String mappingPrefix = set.has(mappingSpec) ? set.valueOf(mappingSpec) : "output";

        Main.LOGGER.info("Mapping jar. Jar path: {} . Output path: {} . Mapping file prefix: {} .", target.toAbsolutePath(), output.toAbsolutePath(), mappingPrefix);

        try {
            Remapper2 remapper;

            List<ClassEntry> classEntries = IOUtils.readEntries(Path.of(mappingPrefix + "_classes.csv"), ClassEntry::new);
            List<FieldEntry> fieldEntries = IOUtils.readEntries(Path.of(mappingPrefix + "_fields.csv"), FieldEntry::new);
            List<MethodEntry> methodEntries = IOUtils.readEntries(Path.of(mappingPrefix + "_methods.csv"), MethodEntry::new);
            switch (command) {
                case "o2u":
                    remapper = Remapper2.createO2U(classEntries, fieldEntries, methodEntries);
                    break;
                default:
                    return;
            }

            if (!Files.exists(output)) {
                Files.createFile(output);
            }

            try (ZipFile ji = new ZipFile(target.toFile()); ZipOutputStream jo = new ZipOutputStream(Files.newOutputStream(output))) {
                Enumeration<? extends ZipEntry> entries = ji.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.isDirectory()) {
                        jo.putNextEntry(new ZipEntry(entry.getName()));
                    } else if (entry.getName().endsWith(".class")) {
                        try (InputStream input = ji.getInputStream(entry)) {
                            String className = entry.getName().substring(0, entry.getName().lastIndexOf('.'));
                            String newClassName = remapper.map(className);
                            ClassReader cr = new ClassReader(input);
                            ClassWriter cw = new ClassWriter(0);
                            ClassRemapper classRemapper = new ClassRemapper(cw, remapper);
                            cr.accept(classRemapper, 0);
                            jo.putNextEntry(new ZipEntry(newClassName + ".class"));
                            jo.write(cw.toByteArray());
                        }
                    } else {
                        try (InputStream input = ji.getInputStream(entry)) {
                            jo.putNextEntry(new ZipEntry(entry.getName()));
                            IOUtils.copy(input, jo);
                        }
                    }
                }
            }
        } catch (IOException e) {
            Main.LOGGER.error("Mapping jar catch exception.", e);
            return;
        }

        Main.LOGGER.info("Finish remap {} .", command);
    }

}
