package com.github.mouse0w0.deobf.command;

import com.github.mouse0w0.deobf.Main;
import com.github.mouse0w0.deobf.misc.*;
import com.google.common.collect.Streams;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.ParameterNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class Init {

    public static void onCommand(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<String> targetSpec = parser.accepts("target", "Target file, extension name must be jar.").withRequiredArg();
        OptionSpec<String> outputSpec = parser.accepts("output", "Output file prefix.").withRequiredArg();
        OptionSpec<String> configSpec = parser.accepts("config", "Config file, file format is toml.").withOptionalArg();

        OptionSet set = parser.parse(args);

        if (!set.has(targetSpec)) {
            Main.LOGGER.error("Target file hasn't specify.");
            return;
        }

        Path target = Path.of(set.valueOf(targetSpec));
        String outputPrefix = set.has(outputSpec) ? set.valueOf(outputSpec) : "output";

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

        Main.LOGGER.info("Initialing deobf. Jar path: {}.", target.toAbsolutePath());

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
        } catch (IOException e) {
            Main.LOGGER.error("Initialing deobf catch exception.", e);
            return;
        }

        List<ClassEntry> classEntries = new ArrayList<>();
        List<FieldEntry> fieldEntries = new ArrayList<>();
        List<MethodEntry> methodEntries = new ArrayList<>();
        List<ParameterEntry> parameterEntries = new ArrayList<>();
        List<LocalVariableEntry> variableEntries = new ArrayList<>();

        var classUniqueId = 0;
        for (var cn : loadedClasses) {
            var className = Utils.getClassName(cn.name);
            var classPackage = Utils.getClassPackage(cn.name);
            var classUniqueName = classPackage + "/Class_" + ++classUniqueId;
            classEntries.add(new ClassEntry(cn.name, classUniqueName, classUniqueName));

            if (cn.fields != null) {
                var fieldUniqueId = 0;
                for (var fn : cn.fields) {
                    var fieldUniqueName = "field_" + ++fieldUniqueId;
                    fieldEntries.add(new FieldEntry(cn.name, fn.name, fieldUniqueName, fieldUniqueName));
                }
            }

            if (cn.methods != null) {
                var methodUniqueId = 0;
                for (var mn : cn.methods) {
                    if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                        continue;
                    }

                    var methodUniqueName = "method_" + ++methodUniqueId;
                    methodEntries.add(new MethodEntry(cn.name, mn.name, methodUniqueName, methodUniqueName, mn.desc));

                    Type[] parameterTypes = Type.getArgumentTypes(mn.desc);
                    ParameterNode[] parameters = mn.parameters == null ? null : mn.parameters.toArray(new ParameterNode[0]);

                    var parameterUniqueId = 0;
                    for (var pn : parameterTypes) {
                        var parameterUniqueName = "para_" + ++parameterUniqueId;
                        parameterEntries.add(new ParameterEntry(classUniqueName, methodUniqueName, parameters == null ? "@" + (parameterUniqueId - 1) : parameters[parameterUniqueId - 1].name, parameterUniqueName, parameterUniqueName));
                    }

                    if (mn.localVariables != null) {
                        var variableUniqueId = 0;
                        for (var vn : mn.localVariables) {
                            var variableUniqueName = "var_" + ++variableUniqueId;
                            variableEntries.add(new LocalVariableEntry(classUniqueName, methodUniqueName, vn.name, variableUniqueName, variableUniqueName));
                        }
                    }
                }
            }
        }

        try {
            IOUtils.writeEntries(Path.of(outputPrefix + "_classes.csv"), classEntries);
            IOUtils.writeEntries(Path.of(outputPrefix + "_fields.csv"), fieldEntries);
            IOUtils.writeEntries(Path.of(outputPrefix + "_methods.csv"), methodEntries);
            IOUtils.writeEntries(Path.of(outputPrefix + "_parameters.csv"), parameterEntries);
            IOUtils.writeEntries(Path.of(outputPrefix + "_variables.csv"), variableEntries);
        } catch (IOException e) {
            Main.LOGGER.error("Initialing deobf catch exception.", e);
        }

        Main.LOGGER.info("Finished.");
    }
}
