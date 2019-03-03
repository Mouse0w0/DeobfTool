package com.github.mouse0w0.deobf;

import com.github.mouse0w0.deobf.command.Init;
import com.github.mouse0w0.deobf.command.Remap;
import com.github.mouse0w0.deobf.misc.Remapper2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Map.entry;

public class Main {

    public static final Logger LOGGER = LoggerFactory.getLogger("Deobf");

    private static final Map<String, Consumer<String[]>> commands = Map.ofEntries(
            entry("init", Init::onCommand),
            entry("o2u", args -> Remap.onCommand("o2u", args)),
//            entry("o2d", null),
//            entry("d2u", null),
//            entry("u2d", null),
            entry("help", Main::help)
    );

    public static void main(String[] args) {
        if (args.length == 0) {
            LOGGER.error("No command.");
            return;
        }

        Consumer<String[]> command = commands.get(args[0].toLowerCase());
        if (command == null) {
            LOGGER.error("Unsupported command {}.", args[0]);
            return;
        }

        command.accept(Arrays.copyOfRange(args, 1, args.length));
    }

    private static void help(String[] args) {
        LOGGER.info("----- Command List -----");
        commands.keySet().forEach(LOGGER::info);
    }
}
