package com.github.mouse0w0.deobf.misc;

import java.io.IOException;
import java.io.Writer;

public class MethodEntry implements Entry {

    public String owner;
    public String obfuscatedName;
    public String uniqueObfuscatedName;
    public String deobfucatedName;
    public String descriptor;

    public MethodEntry() {
    }

    public MethodEntry(String owner, String obfuscatedName, String uniqueObfuscatedName, String deobfucatedName, String descriptor) {
        this.owner = owner;
        this.obfuscatedName = obfuscatedName;
        this.uniqueObfuscatedName = uniqueObfuscatedName;
        this.deobfucatedName = deobfucatedName;
        this.descriptor = descriptor;
    }

    @Override
    public void serialize(Writer writer) throws IOException {
        writer.write(owner);
        writer.write(',');
        writer.write(obfuscatedName);
        writer.write(',');
        writer.write(uniqueObfuscatedName);
        writer.write(',');
        writer.write(deobfucatedName);
        writer.write(",");
        writer.write(descriptor);
    }

    @Override
    public void deserialize(String[] args) {
        owner = args[0];
        obfuscatedName = args[1];
        uniqueObfuscatedName = args[2];
        deobfucatedName = args[3];
        descriptor = args[4];
    }
}
