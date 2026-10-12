package com.animationlib.client;

import com.animationlib.api.Animation;
import com.animationlib.api.AnimationRegistry;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * {@code /animation <id> [thirdperson]}: plays any loaded animation on your own player. Ids are
 * tab-completed from every animation currently loaded, e.g. {@code /animation animationtest:wave}.
 * The optional {@code thirdperson} flag forces third-person view while it plays.
 */
final class AnimationCommand {
    private AnimationCommand() {
    }

    static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) ->
                dispatcher.register(ClientCommands.literal("animation")
                        .then(ClientCommands.argument("id", IdentifierArgument.id())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                        AnimationRegistry.ids().stream().map(Identifier::toString), builder))
                                .executes(context -> play(context.getSource(),
                                        context.getArgument("id", Identifier.class), false))
                                .then(ClientCommands.literal("thirdperson")
                                        .executes(context -> play(context.getSource(),
                                                context.getArgument("id", Identifier.class), true))))));
    }

    private static int play(FabricClientCommandSource source, Identifier id, boolean thirdPerson) {
        if (AnimationRegistry.get(id).isEmpty()) {
            source.sendError(Component.literal("Unknown animation: " + id));
            return 0;
        }
        Animation.init(id).forceThirdPerson(thirdPerson).play(source.getPlayer());
        source.sendFeedback(Component.literal("Playing " + id));
        return 1;
    }
}
