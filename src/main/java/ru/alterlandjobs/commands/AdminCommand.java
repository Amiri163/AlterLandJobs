package ru.alterlandjobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import ru.alterlandjobs.jobs.BusDriverAdmin;

import java.util.ArrayList;
import java.util.List;

public class AdminCommand {
    public static List<String> listJobs = new ArrayList<>();
    public static List<String> descriptionJobs = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("jobs")
                        .then(Commands.literal("edit")
                                .then(Commands.argument("jobName", StringArgumentType.string())
                                        .then(Commands.argument("descriptionNEW", StringArgumentType.greedyString())
                                                .executes(context -> editJobs(context, StringArgumentType.getString(context, "jobName"), StringArgumentType.getString(context, "descriptionNEW")))
                                        )
                                )
                        )

                        .then(Commands.literal("delete")
                                .then(Commands.argument("deleteJobs", StringArgumentType.string())
                                        .executes(context -> deleteJobs(context, StringArgumentType.getString(context, "deleteJobs")
                                                )
                                        )
                                )
                        )

                        .then(Commands.literal("create")
                                .then(Commands.argument("jobName", StringArgumentType.string())
                                        .then(Commands.argument("description", StringArgumentType.greedyString())
                                                .executes(context -> createJobs(context, StringArgumentType.getString(context, "jobName"), StringArgumentType.getString(context, "description")))
                                        )
                                        .executes(context -> createJobs(context, StringArgumentType.getString(context, "jobName"), "без описания")
                                        )
                                )
                        )
        );
    }

    private static int editJobs(CommandContext<CommandSource> context, String jobName, String descriptionNEW) {
        CommandSource source = context.getSource();
        if (BusDriverAdmin.redcatMod) {
            source.sendFailure(new StringTextComponent("Включен режим редактирования маршрута"));
            return 0;
        }
        if (AdminCommand.listJobs.contains(jobName)) {
            source.sendSuccess(new StringTextComponent("Изменение принято"), true);
            descriptionJobs.set(0, descriptionNEW);
            return 0;
        }
        source.sendSuccess(new StringTextComponent("Работа не найдена"), true);

        return 1;
    }

    private static int deleteJobs(CommandContext<CommandSource> context, String deleteJobs) {
        CommandSource source = context.getSource();
        if (BusDriverAdmin.redcatMod) {
            source.sendFailure(new StringTextComponent("Включен режим редактирования маршрута"));
        }
        if (!listJobs.contains(deleteJobs)) {
            source.sendFailure(new StringTextComponent("Такой работы нет, её нельзя удалить"));
            return 0;
        }
        source.sendSuccess(new StringTextComponent("Работа " + deleteJobs + " удалена"), true);
        int index = listJobs.indexOf(deleteJobs);
        listJobs.remove(index);
        descriptionJobs.remove(index);

        return 1;
    }

    private static int createJobs(CommandContext<CommandSource> context, String jobName, String description) {
        CommandSource source = context.getSource();

        if (BusDriverAdmin.redcatMod) {
            source.sendFailure(new StringTextComponent("Включен режим редактирования маршрута"));
            return 0;
        }
        if (description.contains("без описания")) {

            source.sendSuccess(new StringTextComponent("Работа " + jobName + " создана " + description), true);
            listJobs.add(jobName);
            AdminCommand.descriptionJobs.add(description);
        } else {

            source.sendSuccess(new StringTextComponent("Работа " + jobName + " создана с описанием: " + description), true);
            listJobs.add(jobName);
            descriptionJobs.add(description);
        }
        return 1;
    }
}
