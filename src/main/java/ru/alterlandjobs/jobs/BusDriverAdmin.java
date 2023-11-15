package ru.alterlandjobs.jobs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import ru.alterlandjobs.commands.AdminCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusDriverAdmin {
    public static List<String> routeJobe = new ArrayList();
    public static Map<String, List<String>> routesByJob = new HashMap<>();

    public static List<Integer> awards = new ArrayList();
    public static boolean redcatMod = false;

    private static int indexSt;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("jobs")
                        .then(Commands.literal("item")
                                .then(Commands.literal("add")
                                        .executes(BusDriverAdmin::addItem)
                                ))
                        .then(Commands.literal("point")
                                .then(Commands.literal("add")
                                        .then(Commands.argument("index", IntegerArgumentType.integer())
                                                .executes(context -> setPiont(context, IntegerArgumentType.getInteger(context, "index")))
                                        )
                                        .executes(context -> {
                                                    indexSt++;
                                                    return setPiont(context, indexSt);
                                                }
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("indexPoint", IntegerArgumentType.integer())
                                                .executes(context -> deletePiont(context, IntegerArgumentType.getInteger(context, "indexPoint"))
                                                ))
                                ))
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
                                                )))

                                .then(Commands.literal("reward")
                                        .then(Commands.argument("reward", IntegerArgumentType.integer())
                                                .executes(context -> setReward(context, IntegerArgumentType.getInteger(context, "reward")

                                                ))))));
    }

    private static int addRout(CommandContext<CommandSource> context, String jobName, String routeName) {
        CommandSource source = context.getSource();
        if (!AdminCommand.listJobs.contains(jobName)) {
            source.sendFailure(new StringTextComponent("Вы не можете создать маршрут для работы, которой нет"));
            return 0;
        }
        routesByJob.putIfAbsent(jobName, new ArrayList<>());
        List<String> routesForJob = routesByJob.get(jobName);

        routesForJob.add(routeName);

        source.sendSuccess(new StringTextComponent("Новый маршрут " + routeName + " создан для работы " + jobName), true);
        return 1;
    }

    // УСТАНАВЛИВАЕТ ТОЧКУ НА КАРТЕ ИНДЕКС АВТОМАТИЧЕСК
    private static int setPiont(CommandContext<CommandSource> context, int index) {
        CommandSource source = context.getSource();
        if (!redcatMod) {
            source.sendFailure(new StringTextComponent("Вы должны находиться в режими редактирования маршрута "));
            return 0;
        }
        PlayerEntity player = Minecraft.getInstance().player;

        Double playerX = player.getX();
        Double playerY = player.getY();
        Double playerZ = player.getZ();

        source.sendSuccess(new StringTextComponent(""), true);

        return 1;
    }

    private static int deletePiont(CommandContext<CommandSource> context, int index) {
        CommandSource source = context.getSource();
        //  source.sendSuccess(new StringTextComponent(String.valueOf(index)), true);

        return 1;
    }

    // ИТЕМ ВЫДАЕТСЯ ДЛЯ МАРШРУТА ДЛЯ НУЖНОЙ РАБОТЫ
    private static int addItem(CommandContext<CommandSource> context) {
        PlayerEntity player = Minecraft.getInstance().player;
        String itemName = String.valueOf(player.getItemInHand(Hand.MAIN_HAND));
        context.getSource().sendSuccess(new StringTextComponent(itemName), true);
        return 1;
    }

    // РЕДАКТИРОВАНИЯ К РАБОТЕ МАРШРУТА КОТОРАЯ УКАЗНА В КОМАНДЕ
    private static int editModRouts(CommandContext<CommandSource> context, String jobName, String routeName) {
        CommandSource source = context.getSource();
        if (!routeJobe.contains(routeName) || !AdminCommand.listJobs.contains(jobName)) {
            source.sendFailure(new StringTextComponent("Такой маршрут или работа не найдена"));
            return 0;
        }
        redcatMod = true;
        source.sendSuccess(new StringTextComponent("Режим редактирования для этого маршрута включен"), true);
        return 1;
    }

    // через проверку в массиве
    private static int setReward(CommandContext<CommandSource> context, int reward) {
        CommandSource source = context.getSource();
        if (!redcatMod) {
            source.sendFailure(new StringTextComponent("Вы должны быть в режими редактирования маршрута"));
            return 0;
        } else if(reward < 0){
            source.sendFailure(new StringTextComponent("Сумма не может быть отрицательной"));
            return 0;
        }
        awards.add(reward);
        source.sendSuccess(new StringTextComponent("Сумма установлена"), true);

        return 1;
    }


    // ДОЛЖНО УДАЛЯТЬСЯ ВМЕСТЕ С ТОЧКАМИ МАРШРУТА
    private static int removeRout(CommandContext<CommandSource> context, String jobName, String routeName) {
        CommandSource source = context.getSource();
        if (redcatMod) {
            source.sendFailure(new StringTextComponent("Включен режим редактирования маршрута"));
            return 0;
        }
        if (routeJobe.contains(routeName) && AdminCommand.listJobs.contains(jobName)) {
            routeJobe.remove(routeName);
            source.sendSuccess(new StringTextComponent("Удалено"), true);
            return 1;
        } else {
            source.sendSuccess(new StringTextComponent("Нельзя удалить маршрут, потому что его не существует"), true);
        }


        return 1;

    }
}