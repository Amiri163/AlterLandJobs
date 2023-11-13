package ru.alterlandjobs.jobs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import ru.alterlandjobs.common.JobInfo;

public class BusDriverPlayer {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("jobs")
                        .then(Commands.literal("route")
                                .then(Commands.literal("list")
                                        .executes(context -> routList(context)))));
    }

    private static int routList(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        if (!BusDriverAdmin.routeJobe.isEmpty()) {
            int var1 = 1;
            for (String element : BusDriverAdmin.routeJobe) {
                JobInfo list = new JobInfo(var1, element, 0);
                var1++;
                source.sendSuccess(new StringTextComponent(list.toString()), true);
            }
        } else {
            source.sendSuccess(new StringTextComponent("Список маршрутов пуст"), true);
        }
        return 1;
    }
}