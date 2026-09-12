package com.nebula.command;

import com.nebula.gui.NebulaSettingsScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

public class NebulaCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("nebula")
                .executes(context -> {
                    if (context.getSource().getClient().player != null) {
                        context.getSource().getClient().setScreen(
                            new NebulaSettingsScreen(context.getSource().getClient().currentScreen)
                        );
                    }
                    return 1;
                })
            );
        });
    }
}
