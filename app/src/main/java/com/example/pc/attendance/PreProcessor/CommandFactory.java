package com.example.pc.attendance.PreProcessor;

import java.util.HashMap;

/**
 * Created by azaudio on 4/27/2018.
 */

public class CommandFactory {
    private final HashMap<String, Command> commands;

    public CommandFactory() {
        commands = new HashMap<>();
    }

    public void addCommand(String name, Command command) {
        commands.put(name, command);
    }

    public PreProcessor executeCommand(String name, PreProcessor preProcessor) {
        if (commands.containsKey(name)) {
            return commands.get(name).preprocessImage(preProcessor);
        } else {
            return null;
        }
    }
}
