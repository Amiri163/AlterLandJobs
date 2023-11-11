package ru.alterlandjobs.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class AdminCommand {
    public static String createJobs;
    public static String redactJobs;
    public static String deleteJobs;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("jobs")
                        .then(Commands.literal("edit")
                                .executes(AdminCommand::editJobs)

                        .then(Commands.literal("delete")
                                .executes(AdminCommand::deleteJobs)))

                        .then(Commands.literal("create")
                                .then(Commands.argument("jobName", StringArgumentType.string())
                                        .executes(context -> createJobs(context, StringArgumentType.getString(context, "jobName")))))
        );
    }
    private static int editJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        source.sendSuccess(new StringTextComponent(createJobs + " изменение принято"), true);
        return 1;
    }
    private static int deleteJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        source.sendSuccess(new StringTextComponent(createJobs + " работа удалена"), true);
        return 1;
    }
    private static int createJobs(CommandContext<CommandSource> context, String jobName) {
        CommandSource source = context.getSource();
        PlayerCommand.listJobs.add(jobName);
        source.sendSuccess(new StringTextComponent(jobName + " работа создана"), true);
        return 1;
    }
}
