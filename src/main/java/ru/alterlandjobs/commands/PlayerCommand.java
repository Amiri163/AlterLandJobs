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
                    message.append(jobName).append(" - описание: ").append(jobDescription).append("\n");
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
            source.sendFailure(new StringTextComponent("Ты еще не устроился на работу"));
            return 0;
        } else {
            source.sendSuccess(new StringTextComponent("Ты работаешь на " + myJobs), true);
        }
        return 1;
    }
}
