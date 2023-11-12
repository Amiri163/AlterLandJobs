package ru.alterlandjobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class PlayerCommand {
    public static String myJobs;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("jobs")
                        .then(Commands.literal("list")
                                .executes(PlayerCommand::listJobs))

                        .then(Commands.literal("my")
                                .executes(PlayerCommand::myJobs))

                        .then(Commands.literal("join")
                                .then(Commands.argument("joinJobs", StringArgumentType.string())
                                        .executes(context -> joinJobs(context, StringArgumentType.getString(context, "joinJobs")))))

                        .then(Commands.literal("leave")
                                .executes(PlayerCommand::leaveJobs))

        );
    }


    private static int listJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();

        if (AdminCommand.listJobs.size() == AdminCommand.descriptionJobs.size() && !AdminCommand.listJobs.isEmpty()) {
            StringBuilder message = new StringBuilder("Список всех работ:\n\n");

            for (int i = 0; i < AdminCommand.listJobs.size(); i++) {
                String jobName = AdminCommand.listJobs.get(i);
                String jobDescription = AdminCommand.descriptionJobs.get(i);
                if (!AdminCommand.descriptionJobs.contains("без описания")) {
                    message.append(jobName).append(" - описание: ").append(jobDescription);
                } else {
                    message.append(jobName).append(" - ").append(jobDescription).append("\n");

                }

            }

            source.sendSuccess(new StringTextComponent(message.toString()), true);
        } else {
            source.sendSuccess(new StringTextComponent("Список всех работ:  \n"), true);
            source.sendSuccess(new StringTextComponent("Здесь пока нет работ"), true);

        }
        return 1;
    }

    private static int myJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        if (myJobs == null) {
            source.sendSuccess(new StringTextComponent("Ты еще не устроился на работу"), true);
        } else {
            source.sendSuccess(new StringTextComponent("Ты работаешь на " + myJobs), true);
        }
        return 1;
    }

    private static int joinJobs(CommandContext<CommandSource> context, String joinJobs) {
        CommandSource source = context.getSource();
        if (AdminCommand.listJobs.contains(joinJobs)) {

            source.sendSuccess(new StringTextComponent("Ты устроился работать " + joinJobs), true);
            PlayerCommand.myJobs = joinJobs;
            return 1;
        } else {
            source.sendSuccess(new StringTextComponent("Похоже, что такой работы не существует"), true);

        }
        return 0;
    }

    private static int leaveJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        if (myJobs != null) {
            source.sendSuccess(new StringTextComponent("Ты уволился с работы " + myJobs), true);
            myJobs = null;
            return 1;
        } else {
            source.sendSuccess(new StringTextComponent("Чтобы уволиться с работы, ты должен где-то работать"), true);
            return 1;
        }
    }
}
