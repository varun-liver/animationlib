package com.animationlib.impl;

import com.animationlib.api.BossBar;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * {@code /animationbossbar show <id> <target> <color> <image|none> <title>}: shows a boss bar named
 * {@code id} for one living entity, filled by its health (see {@link BossBar#bossbar}), e.g.
 * {@code /animationbossbar show boss1 @e[type=zombie,limit=1] red none Big Zombie}. The image is
 * the id of a PNG in {@code assets/<namespace>/animationlib/bossbar/} (or {@code none} for the
 * vanilla bar), the title is the rest of the command, and showing the same id again updates that
 * bar.
 * {@code /animationbossbar remove <id>} removes it again.
 *
 * <p>Named differently from vanilla's {@code /bossbar}, which manages its own, separate bars.
 */
public final class BossBarCommand {
    private BossBarCommand() {
    }

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) ->
                dispatcher.register(Commands.literal("animationbossbar")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("show")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests(BossBarCommand::suggestIds)
                                        .then(Commands.argument("target", EntityArgument.entity())
                                                .then(Commands.argument("color", StringArgumentType.word())
                                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                                Arrays.stream(BossEvent.BossBarColor.values())
                                                                        .map(BossEvent.BossBarColor::getName), builder))
                                                        .then(Commands.literal("none")
                                                                .then(Commands.argument("title", StringArgumentType.greedyString())
                                                                        .executes(BossBarCommand::show)))
                                                        .then(Commands.argument("image", IdentifierArgument.id())
                                                                .then(Commands.argument("title", StringArgumentType.greedyString())
                                                                        .executes(BossBarCommand::show)))))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests(BossBarCommand::suggestIds)
                                        .executes(BossBarCommand::remove)))));
    }

    private static int show(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String colorName = StringArgumentType.getString(context, "color");
        BossEvent.BossBarColor color = Arrays.stream(BossEvent.BossBarColor.values())
                .filter(candidate -> candidate.getName().equalsIgnoreCase(colorName))
                .findFirst().orElse(null);
        if (color == null) {
            source.sendFailure(Component.literal("Unknown boss bar color: " + colorName));
            return 0;
        }

        String id = StringArgumentType.getString(context, "id");
        String title = StringArgumentType.getString(context, "title");
        if (!(EntityArgument.getEntity(context, "target") instanceof LivingEntity entity)) {
            source.sendFailure(Component.literal("That entity is not alive, so it has no health to show"));
            return 0;
        }
        // "none" is its own literal, so the image argument only exists when an image was given.
        Identifier image = context.getNodes().stream().anyMatch(node -> node.getNode().getName().equals("image"))
                ? IdentifierArgument.getId(context, "image") : null;
        BossBar.bossbar(id, color, title, image, entity);
        source.sendSuccess(() -> Component.literal("Showing boss bar " + id + " (\"" + title + "\""
                + (image != null ? ", image " + image : "") + ") on " + entity.getName().getString()), true);
        return 1;
    }

    private static int remove(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        if (!BossBar.remove(id)) {
            context.getSource().sendFailure(Component.literal("No boss bar with id " + id));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal("Removed boss bar " + id), true);
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestIds(CommandContext<CommandSourceStack> context,
                                                             SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(BossBarManager.ids(), builder);
    }
}
