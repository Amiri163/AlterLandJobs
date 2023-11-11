package ru.alterlandjobs.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlayerCommand {
    public static ArrayList <String> listJobs;
    public static String myJobs;
    public static String joinJobs;
    public static String leaveJobs;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("jobs")
                        .then(Commands.literal("list")
                                .executes(PlayerCommand::listJobs))
                        .then(Commands.literal("my")
                                .executes(PlayerCommand::myJobs))
                        .then(Commands.literal("join")
                                .executes(PlayerCommand::joinJobs))
                        .then(Commands.literal("leave")
                                .executes(PlayerCommand::leaveJobs))


//                        .then(Commands.argument("nameJobs", StringArgumentType.string())
//                                .executes(PlayerCommand::executeTestCommand))
        );
    }

//    private static int executeTestCommand(CommandContext<CommandSource> context) {
//        CommandSource source = context.getSource();
//        String argumentValue = context.getArgument("nameJobs", String.class);
//        source.sendSuccess(new StringTextComponent("Command executed with argument: " + argumentValue), true);
//        return 1;
//    }

    private static int listJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        source.sendSuccess(new StringTextComponent("Список всех работ: " + listJobs), true);
        return 1;
    }
    private static int myJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        source.sendSuccess(new StringTextComponent("Ты работаешь " + myJobs), true);
        return 1;
    }
    private static int joinJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        source.sendSuccess(new StringTextComponent("Ты устроился работать " + joinJobs), true);
        return 1;
    }
    private static int leaveJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        source.sendSuccess(new StringTextComponent("Ты уволился с работы " + leaveJobs), true);
        return 1;
    }
}
