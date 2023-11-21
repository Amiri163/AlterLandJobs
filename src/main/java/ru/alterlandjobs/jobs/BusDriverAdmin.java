package ru.alterlandjobs.jobs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import ru.alterlandjobs.commands.AdminCommand;
import ru.alterlandjobs.common.EditModeInfo;
import ru.alterlandjobs.event.EventHandler;

import java.util.*;

public class BusDriverAdmin {
    private static Map<String, EditModeInfo> editModes = new HashMap<>(); // Хранение информации о режиме редактирования
    public static Map<String, List<String>> routePoints = new HashMap<>(); // Хранит массив с точками
    public static Map<String, List<String>> routesByJob = new HashMap<>(); // Хранит название работы + маршрут по ключу
    public static Map<String, List<ResourceLocation>> routeItemMap = new HashMap<>(); // Хранит предметы для каждого маршрута

    public static List<ResourceLocation> itemsForRoute = new ArrayList<>(); // предмет от маршрута
    public static List<Integer> awards = new ArrayList(); // Награда когда игрок на точке
    public static List<String> points = new ArrayList<>(); // Сами точки - элеметн массива с точками для путей
    public static List<String> routeJob = new ArrayList<>(); // хранение маршрутов для значения из массива

    public static boolean redcatMod = false;
    private static int indexSt;

    // не нужно сохранять
    public static Map<Integer, List<String>> pointsAndIndex = new HashMap<>(); // хранит ключ в виде индекса точки и корды точки

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
                                        .executes(context -> setPoint(context, 0)
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
        routeJob = routesByJob.get(jobName);
        routeJob.add(routeName);

        source.sendSuccess(new StringTextComponent("Новый маршрут " + routeName + " создан для работы " + jobName), true);
        return 1;
    }

    private static int pointsShow(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        // Проверяем наличие маршрута в routePoints
                if (!redcatMod) {
            source.sendFailure(new StringTextComponent("Вы должны находиться в режиме редактирования маршрута "));
            return 0;
        }
        if (routePoints.containsKey(EditModeInfo.getRouteName())) {

            if (!points.isEmpty()) {
                pointsAndIndex = new HashMap<>(); // хранит индекс для точки в show

                for (int i = 0; i < points.size(); i++)
                    pointsAndIndex.put(i + 1, Collections.singletonList(points.get(i)));

                for (Map.Entry<Integer, List<String>> entry : pointsAndIndex.entrySet()) {
                    Integer key = entry.getKey();
                    List<String> value = entry.getValue();

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(key).append(") ");
                    for (String str : value) {
                        stringBuilder.append(str).append(" ");
                    }

                    source.sendSuccess(new StringTextComponent(stringBuilder.toString()), true);
                }
            } else {
                source.sendFailure(new StringTextComponent("Для маршрута " + EditModeInfo.getRouteName() + " точки не найдены"));
                return 0;
            }
        } else {
            source.sendFailure(new StringTextComponent("Маршрут " + EditModeInfo.getRouteName() + " не содержит точек"));
            return 0;
        }

        return 1;
    }

    // УСТАНАВЛИВАЕТ ТОЧКУ НА КАРТЕ ИНДЕКС АВТОМАТИЧЕСК
    private static int setPoint(CommandContext<CommandSource> context, int index) {
        indexSt = index;
        CommandSource source = context.getSource();
        if (!redcatMod) {
            source.sendFailure(new StringTextComponent("Вы должны находиться в режиме редактирования маршрута "));
            return 0;
        }
        PlayerEntity player = Minecraft.getInstance().player;

        int playerX = (int) Math.floor(player.getX());
        int playerY = (int) Math.floor(player.getY());
        int playerZ = (int) Math.floor(player.getZ());
        String mess = playerX + " " + playerY + " " + playerZ;

        if (routePoints.containsKey(EditModeInfo.getRouteName())) {
            // Получаем список точек для указанного маршрута и добавляем в него новую точку
            points = routePoints.get(EditModeInfo.getRouteName());
            points.add(mess);
        } else {
            // Если маршрута еще не существует, создаем новый список точек для этого маршрута
            List<String> points = new ArrayList<>();
            points.add(mess);
            routePoints.put(EditModeInfo.getRouteName(), points);
        }

        source.sendSuccess(new StringTextComponent("Точка добавлена для маршрута " + EditModeInfo.getRouteName()), true);
        return 1;
    }


    private static int deletePiont(CommandContext<CommandSource> context, int index) {
        CommandSource source = context.getSource();
//        if (!redcatMod) {
//            source.sendFailure(new StringTextComponent("Вы должны находиться в режиме редактирования маршрута"));
//            return 0;
//        }

        String currentRoute = EditModeInfo.getRouteName();
        if (!routePoints.containsKey(currentRoute)) {
            source.sendFailure(new StringTextComponent("Маршрут не найден"));
            return 0;
        }

        int listSize = points.size();
        if (index >= listSize || index < 0) {
            source.sendFailure(new StringTextComponent("Указан недопустимый индекс точки"));
            return 0;
        }

        //routePoints.getOrDefault(EditModeInfo.getRouteName(), EditModeInfo.getRouteName());
        points.remove(index);
        //pointsAndIndex.remove(index);

        source.sendSuccess(new StringTextComponent("Точка с индексом " + index + " удалена"), true);
        return 1;
    }

    private static int addItem(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        if (!redcatMod) {
            source.sendFailure(new StringTextComponent("Вы должны быть в режиме редактирования маршрута"));
            return 0;
        }
        PlayerEntity player = Minecraft.getInstance().player;
        ItemStack itemNameStack = player.getItemInHand(Hand.MAIN_HAND);


        Item item = itemNameStack.getItem();
        ResourceLocation itemNameLocation = item.getRegistryName();
        itemsForRoute = routeItemMap.getOrDefault(EditModeInfo.getRouteName(), new ArrayList<>());
        itemsForRoute.add(itemNameLocation);
        routeItemMap.put(EditModeInfo.getRouteName(), itemsForRoute);
        source.sendSuccess(new StringTextComponent("Добавлены предмет: " + itemsForRoute), true);

        return 1;
    }

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
            EventHandler.flag = true;
            EventHandler.flag2 = false;
            return 0;
        }
        source.sendFailure(new StringTextComponent("Вы не находитесь в режиме редактирования"));

        return 1;
    }


    private static int setReward(CommandContext<CommandSource> context, int reward) {
        CommandSource source = context.getSource();
        if (!redcatMod) {
            source.sendFailure(new StringTextComponent("Вы должны быть в режиме редактирования маршрута"));
            return 0;
        } else if (reward < 0) {
            source.sendFailure(new StringTextComponent("Сумма не может быть отрицательной"));
            return 0;
        }
        awards.add(reward);
        source.sendSuccess(new StringTextComponent("Сумма установлена"), true);

        return 1;
    }

    private static int removeRout(CommandContext<CommandSource> context, String jobName, String routeName) {
        CommandSource source = context.getSource();
        if (redcatMod) {
            source.sendFailure(new StringTextComponent("Включен режим редактирования маршрута"));
            return 0;
        }
        if (routesByJob.containsKey(jobName)) {
            List<String> routesForJob = routesByJob.get(jobName);
            if (routesForJob.contains(routeName)) {
                routesForJob.remove(routeName);
                routePoints.remove(routeName); // Удаление точек, связанных с этим маршрутом
                // Проверяем, остались ли еще маршруты для этой работы
                if (routesForJob.isEmpty()) {
                    routesByJob.remove(jobName);
                }
                source.sendSuccess(new StringTextComponent("Маршрут и все его точки удалены"), true);
                return 1;
            } else {
                source.sendFailure(new StringTextComponent("Маршрут не существует для указанной работы"));
                return 0;
            }
        } else {
            source.sendFailure(new StringTextComponent("Указанная работа не найдена"));
            return 0;
        }
    }
}