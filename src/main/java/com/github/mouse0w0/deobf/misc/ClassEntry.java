package com.github.mouse0w0.deobf.misc;

import java.io.IOException;
import java.io.Writer;

public class ClassEntry implements Entry {

    public String obfuscatedName;
    public String uniqueObfuscatedName;
    public String deobfucatedName;

    public ClassEntry(String obfuscatedName, String uniqueObfuscatedName, String deobfucatedName) {
        this.obfuscatedName = obfuscatedName;
        this.uniqueObfuscatedName = uniqueObfuscatedName;
        this.deobfucatedName = deobfucatedName;
    }

    @Override
    public void serialize(Writer writer) throws IOException {
        writer.write(obfuscatedName);
        writer.write(',');
        writer.write(uniqueObfuscatedName);
        writer.write(',');
        writer.write(deobfucatedName);
    }

    @Override
    public void deserialize(String[] args) {
        obfuscatedName = args[0];
        uniqueObfuscatedName = args[1];
        deobfucatedName = args[2];
    }

    @Override
    public String toString() {
        return "ClassEntry{" +
                "obfuscatedName='" + obfuscatedName + '\'' +
                ", uniqueObfuscatedName='" + uniqueObfuscatedName + '\'' +
                ", deobfucatedName='" + deobfucatedName + '\'' +
                '}';
    }
}
