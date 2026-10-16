package com.animationlib.client;

import com.animationlib.api.Animation;
import com.animationlib.api.AnimationPlayer;
import com.animationlib.api.AnimationRegistry;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.stream.Stream;

/**
 * {@code /animation <id> [thirdperson]}: plays any loaded animation on your own player. Ids are
 * tab-completed from every animation currently loaded, e.g. {@code /animation animationtest:wave}.
 * If a mod declared an {@link Animation} handle for that id, it plays with that handle's options
 * (camera follow, forced third person, hidden inventory). The optional {@code thirdperson} flag
 * forces third-person view while it plays, on top of those.
 *
 * <p>{@code /animation stop [entityId]} stops the animation (and its sound) on your own player, or
 * on the entity with that id; like any client request, the server only honors another entity if
 * you can see it.
 */
final class AnimationCommand {
    private AnimationCommand() {
    }

    static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) ->
                dispatcher.register(ClientCommands.literal("animation")
                        .then(ClientCommands.literal("stop")
                                .executes(context -> stop(context.getSource(), null))
                                .then(ClientCommands.argument("entityId", IntegerArgumentType.integer(0))
                                        .executes(context -> stop(context.getSource(),
                                                IntegerArgumentType.getInteger(context, "entityId")))))
                        .then(ClientCommands.argument("id", IdentifierArgument.id())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                        Stream.concat(AnimationRegistry.ids().stream(), Animation.registeredIds().stream())
                                                .distinct().map(Identifier::toString), builder))
                                .executes(context -> play(context.getSource(),
                                        context.getArgument("id", Identifier.class), false))
                                .then(ClientCommands.literal("thirdperson")
                                        .executes(context -> play(context.getSource(),
                                                context.getArgument("id", Identifier.class), true))))));
    }

    private static int stop(FabricClientCommandSource source, Integer entityId) {
        Entity entity = entityId == null ? source.getPlayer() : source.getPlayer().level().getEntity(entityId);
        if (entity == null) {
            source.sendError(Component.literal("No entity with id " + entityId));
            return 0;
        }
        AnimationPlayer.stop(entity);
        source.sendFeedback(Component.literal("Stopped animation on entity " + entity.getId()));
        return 1;
    }

    private static int play(FabricClientCommandSource source, Identifier id, boolean thirdPerson) {
        if (AnimationRegistry.get(id).isEmpty()) {
            source.sendError(Component.literal("Unknown animation: " + id));
            return 0;
        }
        // A handle declared in code (Animation.init) plays with its own options; the thirdperson
        // flag only adds to those, on a copy, so it doesn't change the handle for later plays.
        Animation animation = Animation.registered(id).map(Animation::copy).orElseGet(() -> Animation.init(id));
        if (thirdPerson) {
            animation.forceThirdPerson(true);
        }
        animation.play(source.getPlayer());
        source.sendFeedback(Component.literal("Playing " + id));
        return 1;
    }
}
