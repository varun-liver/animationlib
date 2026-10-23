package com.animationlib.impl;

import com.animationlib.api.Animation;
import com.animationlib.api.AnimationPlayer;
import com.animationlib.api.AnimationRegistry;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * {@code /animation play <targets> <id> [thirdperson]}: plays any loaded animation on every entity
 * the selector matches, e.g. {@code /animation play @s animationtest:wave} or
 * {@code /animation play @e[type=pig,limit=1] mymod:headaction}. Ids are tab-completed from every
 * animation currently loaded. If a mod declared an {@link Animation} handle for that id, it plays
 * with that handle's options (camera follow, forced third person, hidden inventory). The optional
 * {@code thirdperson} flag forces third-person view while it plays, on top of those.
 *
 * <p>{@code /animation stop <targets>} stops the animation (and its sound) on every entity the
 * selector matches.
 *
 * <p>A server command, so it takes the same selectors as vanilla commands and needs the same
 * permission level as {@code /effect}.
 */
public final class AnimationCommand {
    private AnimationCommand() {
    }

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) ->
                dispatcher.register(Commands.literal("animation")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("play")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("id", IdentifierArgument.id())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                        Stream.concat(AnimationRegistry.ids().stream(),
                                                                        Animation.registeredIds().stream())
                                                                .distinct().map(Identifier::toString), builder))
                                                .executes(context -> play(context, false))
                                                .then(Commands.literal("thirdperson")
                                                        .executes(context -> play(context, true))))))
                        .then(Commands.literal("stop")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(AnimationCommand::stop)))));
    }

    private static int play(CommandContext<CommandSourceStack> context, boolean thirdPerson) throws
            com.mojang.brigadier.exceptions.CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Identifier id = IdentifierArgument.getId(context, "id");
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        if (AnimationRegistry.get(id).isEmpty()) {
            source.sendFailure(Component.literal("Unknown animation: " + id));
            return 0;
        }

        // A handle declared in code (Animation.init) plays with its own options; the thirdperson
        // flag only adds to those, on a copy, so it doesn't change the handle for later plays.
        Animation animation = Animation.registered(id).map(Animation::copy).orElseGet(() -> Animation.init(id));
        if (thirdPerson) {
            animation.forceThirdPerson(true);
        }
        targets.forEach(animation::play);
        source.sendSuccess(() -> Component.literal("Playing " + id + " on " + describe(targets)), true);
        return targets.size();
    }

    private static int stop(CommandContext<CommandSourceStack> context) throws
            com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        targets.forEach(AnimationPlayer::stop);
        context.getSource().sendSuccess(() -> Component.literal("Stopped animation on " + describe(targets)), true);
        return targets.size();
    }

    private static String describe(Collection<? extends Entity> targets) {
        return targets.size() == 1 ? targets.iterator().next().getName().getString() : targets.size() + " entities";
    }
}
