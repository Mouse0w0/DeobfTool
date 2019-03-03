package com.github.mouse0w0.deobf.misc;

import org.objectweb.asm.commons.Remapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Remapper2 extends Remapper {

    public Map<String, String> classEntryMap = new HashMap<>();
    public Map<String, String> fieldEntryMap = new HashMap<>();
    public Map<String, String> methodEntryMap = new HashMap<>();

    public static Remapper2 createO2U(List<ClassEntry> classEntries, List<FieldEntry> fieldEntries, List<MethodEntry> methodEntries) {
        Remapper2 remapper = new Remapper2();
        classEntries.forEach(classEntry -> remapper.classEntryMap.put(classEntry.obfuscatedName, classEntry.uniqueObfuscatedName));
        fieldEntries.forEach(fieldEntry -> remapper.fieldEntryMap.put(fieldEntry.owner + "/" + fieldEntry.obfuscatedName, fieldEntry.uniqueObfuscatedName));
        methodEntries.forEach(methodEntry -> remapper.methodEntryMap.put(methodEntry.owner + "/" + methodEntry.obfuscatedName + methodEntry.descriptor, methodEntry.uniqueObfuscatedName));
        return remapper;
    }

    public String mapMethodName(final String owner, final String name, final String descriptor) {
        var fullName = owner + "/" + name + descriptor;
        return methodEntryMap.getOrDefault(fullName, name);
    }

    public String mapFieldName(final String owner, final String name, final String descriptor) {
        var fullName = owner + "/" + name;
        return fieldEntryMap.getOrDefault(fullName, name);
    }

    public String map(final String internalName) {
        return classEntryMap.getOrDefault(internalName, internalName);
    }

}
