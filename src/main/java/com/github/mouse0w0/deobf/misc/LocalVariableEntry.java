package com.github.mouse0w0.deobf.misc;

import java.io.IOException;
import java.io.Writer;

public class LocalVariableEntry implements Entry {

    public String owner;
    public String method;
    public String obfuscatedName;
    public String uniqueObfuscatedName;
    public String deobfucatedName;

    public LocalVariableEntry(String owner, String method, String obfuscatedName, String uniqueObfuscatedName, String deobfucatedName) {
        this.owner = owner;
        this.method = method;
        this.obfuscatedName = obfuscatedName;
        this.uniqueObfuscatedName = uniqueObfuscatedName;
        this.deobfucatedName = deobfucatedName;
    }

    @Override
    public void serialize(Writer writer) throws IOException {
        writer.write(owner);
        writer.write(',');
        writer.write(method);
        writer.write(',');
        writer.write(obfuscatedName);
        writer.write(',');
        writer.write(uniqueObfuscatedName);
        writer.write(',');
        writer.write(deobfucatedName);
    }

    @Override
    public void deserialize(String[] args) {
        owner = args[0];
        method = args[1];
        obfuscatedName = args[2];
        uniqueObfuscatedName = args[3];
        deobfucatedName = args[4];
    }
}
