package ru.alterlandjobs.jobs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import ru.alterlandjobs.commands.AdminCommand;
import ru.alterlandjobs.common.JobInfo;

import java.util.ArrayList;
import java.util.List;

import static ru.alterlandjobs.jobs.BusDriverAdmin.routeItemMap;

public class BusDriverPlayer {
    public static boolean playerWork = true;

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
        List<String> routesForJob = BusDriverAdmin.routesByJob.getOrDefault(jobName, new ArrayList<>());

        if (AdminCommand.listJobs.contains(jobName) && BusDriverAdmin.routesByJob.containsKey(jobName)) {
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

    private static int joinJobsAndRoute(CommandContext<CommandSource> context, String jobName, String routeName) {
        CommandSource source = context.getSource();
        if (AdminCommand.listJobs.contains(jobName) && BusDriverAdmin.routesByJob.containsKey(jobName)) {
            if (!playerWork) {
                source.sendFailure(new StringTextComponent("Чтобы присоединиться на новый маршрут - уволетесь со старого"));
                return 0;
            }
            PlayerEntity player = Minecraft.getInstance().player;
            playerWork = false;

             List<ItemStack> items = new ArrayList();
            items.add(new ItemStack(Items.DIAMOND_BLOCK));


//            String itemName = "grass_block"; // Название предмета
//            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", itemName));


            player.addItem(items.get(0));

            source.sendSuccess(new StringTextComponent("Вы успешно присоединились к маршруту " + routeName), true);
            return 1;
        } else {
            source.sendFailure(new StringTextComponent("Список маршрутов для работы " + jobName + " пуст или такой работы нет"));
            return 0;
        }
    }

    private static int leaveJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        if (playerWork) {
            source.sendFailure(new StringTextComponent("Вы еще не присоеденились ни к одному маршруту"));
            return 0;
        }

        playerWork = true;
        source.sendSuccess(new StringTextComponent("Вы уволились с маршрута"), true);

        return 1;
    }


}
