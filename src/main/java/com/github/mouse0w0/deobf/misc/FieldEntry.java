package com.github.mouse0w0.deobf.misc;

import java.io.IOException;
import java.io.Writer;

public class FieldEntry implements Entry {

    public String owner;
    public String obfuscatedName;
    public String uniqueObfuscatedName;
    public String deobfucatedName;

    public FieldEntry() {
    }

    public FieldEntry(String owner, String obfuscatedName, String uniqueObfuscatedName, String deobfucatedName) {
        this.owner = owner;
        this.obfuscatedName = obfuscatedName;
        this.uniqueObfuscatedName = uniqueObfuscatedName;
        this.deobfucatedName = deobfucatedName;
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
    }

    @Override
    public void deserialize(String[] args) {
        owner = args[0];
        obfuscatedName = args[1];
        uniqueObfuscatedName = args[2];
        deobfucatedName = args[3];
    }
}
