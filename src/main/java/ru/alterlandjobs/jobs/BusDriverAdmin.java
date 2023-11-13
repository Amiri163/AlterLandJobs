package ru.alterlandjobs.jobs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import ru.alterlandjobs.commands.AdminCommand;

import java.util.ArrayList;
import java.util.List;

public class BusDriverAdmin {
    public static List<String> nameJobe = new ArrayList();
    public static List<String> routeJobe = new ArrayList();
    public static List<Integer> awards = new ArrayList();
    public static boolean redcatMod = false;


    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("jobs")
                        .then(Commands.literal("route")
                                .then(Commands.literal("add")
                                        .then(Commands.argument("jobsName", StringArgumentType.string())
                                                .then(Commands.argument("routeName", StringArgumentType.greedyString())
                                                        .executes(context -> addRout(context, (StringArgumentType.getString(context, "jobsName")),
                                                                StringArgumentType.getString(context, "routeName"))))
                                                ))
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("jobName", StringArgumentType.string())
                                                .then(Commands.argument("routeName", StringArgumentType.greedyString())
                                                        .executes(context -> removeRout(context, StringArgumentType.getString(context, "jobName"),
                                                                StringArgumentType.getString(context, "routeName")))
                                                )))
                                .then(Commands.literal("edit")
                                        .then(Commands.argument("jobName", StringArgumentType.string())
                                                .then(Commands.argument("routeName", StringArgumentType.greedyString())
                                                        .executes(context -> editModRouts(context, StringArgumentType.getString(context, "jobName"),
                                                                StringArgumentType.getString(context, "routeName")))
                                                )))));

    }

    private static int editModRouts(CommandContext<CommandSource> context, String jobName, String routeName) {
        CommandSource source = context.getSource();
        if (routeJobe.contains(routeName)) {
            redcatMod = true;
            source.sendSuccess(new StringTextComponent("Режим редактирования маршрута включен"), true);
        } else {
            source.sendSuccess(new StringTextComponent("Такой маршрут не найден"), true);
        }
        return 1;
    }


    private static int addRout(CommandContext<CommandSource> context, String jobName, String routeName) {
        CommandSource source = context.getSource();
        if (AdminCommand.listJobs.contains(jobName)) {

            nameJobe.add(jobName);
            routeJobe.add(routeName);

            source.sendSuccess(new StringTextComponent("Новый маршрут создан"), true);
        }
        else{
            source.sendSuccess(new StringTextComponent("Вы не можете создать маршрут для работы которой нет"), true);

        }
        return 1;
    }

    private static int removeRout(CommandContext<CommandSource> context, String jobName, String routeName) {
        CommandSource source = context.getSource();
        if (!redcatMod) {

            if (routeJobe.contains(routeName) && nameJobe.contains(jobName)) {
                routeJobe.remove(routeName);
                source.sendSuccess(new StringTextComponent("Удалено"), true);
                return 1;
            } else {
                source.sendSuccess(new StringTextComponent("Нельзя удалить маршрут, потому что его не существует"), true);
            }
        } else {
            source.sendSuccess(new StringTextComponent("Включен режим редактирования маршрута"), true);
        }
        return 1;

    }
}