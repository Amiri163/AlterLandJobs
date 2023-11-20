package ru.alterlandjobs.jobs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import ru.alterlandjobs.commands.AdminCommand;
import ru.alterlandjobs.common.EditModeInfo;
import ru.alterlandjobs.common.JobInfo;
import ru.alterlandjobs.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.alterlandjobs.commands.AdminCommand.listJobs;
import static ru.alterlandjobs.jobs.BusDriverAdmin.*;

public class BusDriverPlayer {
    public static boolean playerWork = true;
    public static String routeNameG;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("jobs")
                        .then(Commands.literal("route")

                                .then(Commands.literal("leave")
                                        .executes(BusDriverPlayer::leaveJobs))

                                .then(Commands.literal("join")
                                        .then(Commands.argument("jobName", StringArgumentType.string())
                                                .then(Commands.argument("routeName", StringArgumentType.string())
                                                        .executes(context -> joinJobsAndRoute(context, StringArgumentType.getString(context, "jobName"),
                                                                StringArgumentType.getString(context, "routeName"))))))

                                .then(Commands.literal("list")
                                        .then(Commands.argument("jobName", StringArgumentType.string())
                                                .executes(context -> routList(context, StringArgumentType.getString(context, "jobName")))))));
    }

    private static int routList(CommandContext<CommandSource> context, String jobName) {

        CommandSource source = context.getSource();
        List<String> routesForJob = routesByJob.getOrDefault(jobName, new ArrayList<>());

        if (listJobs.contains(jobName) && routesByJob.containsKey(jobName)) {
            int var1 = 1;
            source.sendSuccess(new StringTextComponent("Список маршрутов к работе " + jobName), true);

            for (String element : routesForJob) {
                List<String> pointsForRoute = BusDriverAdmin.routePoints.getOrDefault(element, new ArrayList<>());
                int pointsCount = pointsForRoute.size();

                JobInfo list = new JobInfo(var1, element, pointsCount);
                var1++;
                source.sendSuccess(new StringTextComponent(list.toString()), true);
            }

        } else {
            source.sendFailure(new StringTextComponent("Список маршрутов для работы " + jobName + " пуст или такой работы нет"));
            return 0;
        }
        return 1;
    }

    private static int joinJobsAndRoute(CommandContext<CommandSource> context, String jobName, String routeName) throws CommandSyntaxException {
        routeNameG = routeName;
        CommandSource source = context.getSource();
        if (routesByJob.containsKey(jobName)) {
            if (BusDriverAdmin.redcatMod) {
                source.sendFailure(new StringTextComponent("Вы находитесь в режиме редактирования"));
                return 0;
            }

            if (!listJobs.contains(jobName) || !routesByJob.containsKey(jobName)) {
                source.sendFailure(new StringTextComponent("Список маршрутов для работы " + jobName + " пуст или такой работы нет"));
                return 0;
            }

            if (!playerWork) {
                source.sendFailure(new StringTextComponent("Чтобы присоединиться к новому маршруту - уволитесь со старого"));
                return 0;
            }
            if (!routeItemMap.containsKey(routeName)) {
                playerWork = false;
                EventHandler.flag = false;
                EventHandler.flag2 = true;
                source.sendSuccess(new StringTextComponent("Вы успешно присоединились к маршруту " + routeName), true);
                return 1;
            }
            ServerPlayerEntity player = source.getPlayerOrException();
            EventHandler.flag = false;
            EventHandler.flag2 = true;

            for (ResourceLocation item : routeItemMap.get(routeName)) {
                ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(item));
                player.addItem(itemStack);
            }
            playerWork = false;
            source.sendSuccess(new StringTextComponent("Вы успешно присоединились к маршруту " + routeName), true);
            return 1;
        }

        source.sendFailure(new StringTextComponent("Маршрут " + routeName + " для работы " + jobName + " не найден"));
        return 0;
    }

    private static int leaveJobs(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        if (playerWork) {
            source.sendFailure(new StringTextComponent("Вы еще не присоеденились ни к одному маршруту"));
            return 0;
        }
        ServerPlayerEntity player = source.getPlayerOrException();

        EventHandler.flag = true;
        EventHandler.flag2 = false;
        playerWork = true;

        for (ResourceLocation item : routeItemMap.get(routeNameG)) {
            ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(item));
            ItemStack airStack = ItemStack.EMPTY;

            itemStack.getContainerItem();

            player.inventory.setItem(0, airStack);
            player.inventory.setItem(1, airStack);
            System.out.println(item);
        }
        source.sendSuccess(new StringTextComponent("Вы уволились с маршрута"), true);

        return 1;
    }


}
