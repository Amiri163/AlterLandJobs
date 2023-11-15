package ru.alterlandjobs;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import ru.alterlandjobs.commands.AdminCommand;
import ru.alterlandjobs.commands.PlayerCommand;
import ru.alterlandjobs.jobs.Base;
import ru.alterlandjobs.jobs.BusDriverAdmin;
import ru.alterlandjobs.jobs.BusDriverPlayer;

import java.time.LocalTime;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("alterland_jobs")
public class AlterLandJobs {

    // Directly reference a log4j logger.
    private static final String MOD_ID = "alterland_jobs";

    public AlterLandJobs() {
        IEventBus modEventBus = MinecraftForge.EVENT_BUS;
        LocalTime currentTime = LocalTime.now();
        modEventBus.addListener(this::registerCommands);
    }
    private void registerCommands(RegisterCommandsEvent event) {
        PlayerCommand.register(event.getDispatcher());
        AdminCommand.register(event.getDispatcher());
        BusDriverAdmin.register(event.getDispatcher());
        BusDriverPlayer.register(event.getDispatcher());
        Base.register(event.getDispatcher());
    }
}
