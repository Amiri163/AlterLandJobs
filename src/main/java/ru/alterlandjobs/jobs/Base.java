package ru.alterlandjobs.jobs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import ru.alterlandjobs.commands.AdminCommand;
import ru.alterlandjobs.commands.PlayerCommand;

public class Base {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("jobs")
                        .then(Commands.literal("join")
                                .then(Commands.argument("joinJobs", StringArgumentType.string())
                                        .executes(context -> joinJobs(context, StringArgumentType.getString(context, "joinJobs")))))

                        .then(Commands.literal("leave")
                                .executes(Base::leaveJobs))

        );

    }

    private static int joinJobs(CommandContext<CommandSource> context, String joinJobs) {
        CommandSource source = context.getSource();
        if (!AdminCommand.listJobs.contains(joinJobs)) {
            source.sendFailure(new StringTextComponent("Похоже, что такой работы не существует"));
            return 0;
        }

        source.sendSuccess(new StringTextComponent("Ты устроился работать " + joinJobs), true);
        PlayerCommand.myJobs = joinJobs;

        return 1;
    }

    private static int leaveJobs(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        if (PlayerCommand.myJobs != null) {
            source.sendSuccess(new StringTextComponent("Ты уволился с работы " + PlayerCommand.myJobs), true);
            PlayerCommand.myJobs = null;
            return 1;
        } else {
            source.sendFailure(new StringTextComponent("Чтобы уволиться с работы, ты должен где-то работать"));
            return 0;
        }
    }
}
