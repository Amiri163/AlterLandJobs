package ru.alterlandjobs.jobs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import ru.alterlandjobs.commands.AdminCommand;
import ru.alterlandjobs.common.JobInfo;

import java.util.ArrayList;
import java.util.List;

public class BusDriverPlayer {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("jobs")
                        .then(Commands.literal("route")
                                .then(Commands.literal("list")
                                        .then(Commands.argument("jobName", StringArgumentType.string())
                                                .executes(context -> routList(context, StringArgumentType.getString(context, "jobName")))))));
    }

    // МАРШРУТЫ К РАБОТЕ КОТОРАЯ УКАЗНА В КОМАНДЕ
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


}
