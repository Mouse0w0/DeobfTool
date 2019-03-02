package com.github.mouse0w0.deobf.misc;

import java.io.IOException;
import java.io.Writer;

public interface Entry {

    void serialize(Writer writer) throws IOException;

    void deserialize(String[] args);
}
