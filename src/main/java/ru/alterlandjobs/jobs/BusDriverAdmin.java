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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import ru.alterlandjobs.commands.AdminCommand;
import ru.alterlandjobs.common.EditModeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusDriverAdmin {
    private static Map<String, EditModeInfo> editModes = new HashMap<>(); // Хранение информации о режиме редактирования
    public static Map<String, List<String>> routePoints = new HashMap<>(); // Хранит массив с точками
    static List<String> points = new ArrayList<>(); // Сами точки - элеметн массива с точками для путей
    static List<String> routesForJob = new ArrayList<>(); // хранение маршрутов для значения из массива
    public static Map<String, List<String>> routesByJob = new HashMap<>(); // Хранит название работы + маршрут по ключу
    public static List<String> routeJobs = new ArrayList<>();
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
                                                .executes(context -> setPoint(context, IntegerArgumentType.getInteger(context, "index")))
                                        )
                                        .executes(context -> {
                                                    indexSt++;
                                                    return setPoint(context, indexSt);
                                                }
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("indexPoint", IntegerArgumentType.integer())
                                                .executes(context -> deletePiont(context, IntegerArgumentType.getInteger(context, "indexPoint"))
                                                ))
                                ))
                        .then(Commands.literal("points")
                                .then(Commands.literal("show")
                                        .executes(BusDriverAdmin::pointsShow))
                        )
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
                                .then(Commands.literal("edit")
                                        .then(Commands.literal("leave")
                                                .executes(BusDriverAdmin::leaveEditModRouts)
                                        ))

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
        routesForJob = routesByJob.get(jobName);
        routeJobs.add(routeName);
        routesForJob.add(routeName);

        source.sendSuccess(new StringTextComponent("Новый маршрут " + routeName + " создан для работы " + jobName), true);
        return 1;
    }

    private static int pointsShow(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        if (!redcatMod) {
            source.sendFailure(new StringTextComponent("Вы должны находиться в режими редактирования маршрута "));
            return 0;
        }
        if (routePoints.containsKey(EditModeInfo.getRouteName())) {
            List<String> points = routePoints.get(EditModeInfo.getRouteName());
            if (!points.isEmpty()) {
                source.sendSuccess(new StringTextComponent("Точки для маршрута " + EditModeInfo.getRouteName() + ": " + points), true);
            } else {
                source.sendFailure(new StringTextComponent("Для маршрута " + EditModeInfo.getRouteName() + " точки не найдены"));
                return 0;
            }
        } else {
            source.sendFailure(new StringTextComponent("Маршрут " + EditModeInfo.getRouteName() + " не cодержит точек"));
            return 0;
        }

        return 1;
    }

    // УСТАНАВЛИВАЕТ ТОЧКУ НА КАРТЕ ИНДЕКС АВТОМАТИЧЕСК
    private static int setPoint(CommandContext<CommandSource> context, int index) {
        CommandSource source = context.getSource();
        if (!redcatMod) {
            source.sendFailure(new StringTextComponent("Вы должны находиться в режими редактирования маршрута "));
            return 0;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        long playerX = Math.round(player.getX());
        long playerY = Math.round(player.getY());
        long playerZ = Math.round(player.getZ());

        String currentRoute = EditModeInfo.getRouteName();

        // Добавляем точку к текущему маршруту
        addPoint(currentRoute, playerX + " " + playerY + " " + playerZ);

        source.sendSuccess(new StringTextComponent("Точка добавлена для маршрута " + currentRoute), true);
        return 1;
    }

    private static void addPoint(String routeName, String point) {
        if (routePoints.containsKey(routeName)) {
            // Получаем список точек для указанного маршрута и добавляем в него новую точку
            points = routePoints.get(routeName);
            points.add(point);
        } else {
            // Если маршрута еще не существует, создаем новый список точек для этого маршрута
            List<String> points = new ArrayList<>();
            points.add(point);
            routePoints.put(routeName, points);
        }
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

        if (!routesByJob.containsKey(jobName) || !routesByJob.get(jobName).contains(routeName)) {
            source.sendFailure(new StringTextComponent("Такой маршрут или работа не найдена"));
            return 0;
        }
        if (redcatMod) {
            source.sendFailure(new StringTextComponent("Вы уже находитесь в режиме редактирования"));
            return 0;
        }

        EditModeInfo modeInfo = editModes.getOrDefault(jobName + routeName, new EditModeInfo(jobName, routeName));

        editModes.put(jobName + routeName, modeInfo);
        redcatMod = true;

        source.sendSuccess(new StringTextComponent("Режим редактирования для маршрута " + routeName + " в работе " + jobName + " включен"), true);
        return 1;
    }

    private static int leaveEditModRouts(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        if (redcatMod) {
            source.sendSuccess(new StringTextComponent("Вы вышли с режима редактирования"), true);
            redcatMod = false;
            return 0;
        }
        source.sendFailure(new StringTextComponent("Вы не находитесь в режиме редактирования"));

        return 1;
    }


    // через проверку в массиве
    private static int setReward(CommandContext<CommandSource> context, int reward) {
        CommandSource source = context.getSource();
        if (!redcatMod) {
            source.sendFailure(new StringTextComponent("Вы должны быть в режими редактирования маршрута"));
            return 0;
        } else if (reward < 0) {
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
        if (routesByJob.containsKey(jobName)) {
            routeJobs.remove(routeName);
            routesForJob.remove(routeName);

            source.sendSuccess(new StringTextComponent("Маршрут и все его точки удалены"), true);
            return 1;
        } else {
            source.sendFailure(new StringTextComponent("Маршрут не существует для указанной работы"));
            return 0;
        }
    }
}