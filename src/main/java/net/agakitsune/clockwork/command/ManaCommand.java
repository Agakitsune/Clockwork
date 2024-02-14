package net.agakitsune.clockwork.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.agakitsune.clockwork.Clockwork;
import net.agakitsune.clockwork.entity.player.ManaEntity;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

public class ManaCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher
                .register(CommandManager.literal("mana").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        ManaCommand.executeAdd(
                                                                context.getSource(),
                                                                EntityArgumentType.getPlayers(context, "targets"),
                                                                IntegerArgumentType.getInteger(context, "amount"),
                                                                ManaCommand.Component.POINTS
                                                        )
                                                )
                                                .then(CommandManager.literal("points")
                                                        .executes(context ->
                                                                ManaCommand.executeAdd(
                                                                        context.getSource(),
                                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                                        IntegerArgumentType.getInteger(context, "amount"),
                                                                        ManaCommand.Component.POINTS
                                                                )
                                                        )
                                                )
                                                .then(CommandManager.literal("max")
                                                        .executes(context ->
                                                                ManaCommand.executeAdd(
                                                                        context.getSource(),
                                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                                        IntegerArgumentType.getInteger(context, "amount"),
                                                                        ManaCommand.Component.MAX
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(context ->
                                                        ManaCommand.executeSet(
                                                                context.getSource(),
                                                                EntityArgumentType.getPlayers(context, "targets"),
                                                                IntegerArgumentType.getInteger(context, "amount"),
                                                                ManaCommand.Component.POINTS
                                                        )
                                                )
                                                .then(CommandManager.literal("points")
                                                        .executes(context ->
                                                                ManaCommand.executeSet(
                                                                        context.getSource(),
                                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                                        IntegerArgumentType.getInteger(context, "amount"),
                                                                        ManaCommand.Component.POINTS
                                                                )
                                                        )
                                                )
                                                .then(CommandManager.literal("max")
                                                        .executes(context ->
                                                                ManaCommand.executeSet(
                                                                        context.getSource(),
                                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                                        IntegerArgumentType.getInteger(context, "amount"),
                                                                        ManaCommand.Component.MAX
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("query")
                                .then(CommandManager.argument("targets", EntityArgumentType.player())
                                        .then(CommandManager.literal("points")
                                                .executes(context ->
                                                        ManaCommand.executeQuery(
                                                                context.getSource(),
                                                                EntityArgumentType.getPlayer(context, "targets"),
                                                                ManaCommand.Component.POINTS
                                                        )
                                                )
                                        )
                                        .then(CommandManager.literal("max")
                                                .executes(context ->
                                                        ManaCommand.executeQuery(
                                                                context.getSource(),
                                                                EntityArgumentType.getPlayer(context, "targets"),
                                                                ManaCommand.Component.MAX
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }

    private static int executeQuery(ServerCommandSource source, PlayerEntity player, ManaCommand.Component component) {
        int i = component.getter.applyAsInt((ManaEntity) player);
        source.sendFeedback(() -> Text.translatable("commands.mana.query." + component.name, player.getDisplayName(), i), false);
        return i;
    }

    private static int executeAdd(ServerCommandSource source, Collection<? extends PlayerEntity> targets, int amount, ManaCommand.Component component) {
        for (PlayerEntity player : targets) {
            component.adder.accept((ManaEntity) player, amount);
        }
        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.mana.add." + component.name + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.mana.add." + component.name + ".success.multiple", amount, targets.size()), true);
        }
        return targets.size();
    }

    private static int executeSet(ServerCommandSource source, Collection<? extends PlayerEntity> targets, int amount, ManaCommand.Component component) {
        for (PlayerEntity player : targets) {
            component.setter.accept((ManaEntity) player, amount);
        }
        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.mana.set." + component.name + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.mana.set." + component.name + ".success.multiple", amount, targets.size()), true);
        }
        return targets.size();
    }

    enum Component {
        POINTS("points",
                ManaEntity::addMana,
                ManaEntity::setMana,
                ManaEntity::getMana
        ),
        MAX("max",
                ManaEntity::addMaxMana,
                ManaEntity::setMaxMana,
                ManaEntity::getMaxMana
        );

        public final BiConsumer<ManaEntity, Integer> adder;
        public final BiConsumer<ManaEntity, Integer> setter;
        public final String name;
        final ToIntFunction<ManaEntity> getter;

        Component(String name, BiConsumer<ManaEntity, Integer> adder, BiConsumer<ManaEntity, Integer> setter, ToIntFunction<ManaEntity> getter) {
            this.adder = adder;
            this.name = name;
            this.setter = setter;
            this.getter = getter;
        }
    }
}
