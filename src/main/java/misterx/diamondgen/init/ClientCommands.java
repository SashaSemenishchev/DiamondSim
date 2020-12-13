package misterx.diamondgen.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import misterx.diamondgen.commands.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ClientCommands {

    public static final String PREFIX = "diasim";
    public static final List<ClientCommand> COMMANDS = new ArrayList<>();

    public static SeedCommand SEED;
    public static RangeCommand RANGE;
    public static ActiveCommand ACTIVE;
    public static OpaqueCommand OPAQUE;


    static {
        COMMANDS.add(SEED = new SeedCommand());
        COMMANDS.add(RANGE = new RangeCommand());
        COMMANDS.add(ACTIVE = new ActiveCommand());
        COMMANDS.add(OPAQUE = new OpaqueCommand());
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        COMMANDS.forEach(clientCommand -> clientCommand.register(dispatcher));
    }

    public static boolean isClientSideCommand(String[] args) {
        if(args.length < 2)return false;
        if(!PREFIX.equals(args[0]))return false;

        for(ClientCommand command: COMMANDS) {
            if(command.getName().equals(args[1])) {
                return true;
            }
        }

        return false;
    }

    public static int executeCommand(StringReader reader) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        try {
            return player.networkHandler.getCommandDispatcher().execute(reader, new FakeCommandSource(player));
        } catch(CommandException e) {
            ClientCommand.sendFeedback("ur bad, git gud command", Formatting.RED, false);
            e.printStackTrace();
        } catch(CommandSyntaxException e) {
            ClientCommand.sendFeedback("ur bad, git gud syntax", Formatting.RED, false);
            e.printStackTrace();
        } catch(Exception e) {
            ClientCommand.sendFeedback("ur bad, wat did u do", Formatting.RED, false);
            e.printStackTrace();
        }

        return 1;
    }

    /**
     * Magic class by Earthcomputer.
     * https://github.com/Earthcomputer/clientcommands/blob/fabric/src/main/java/net/earthcomputer/clientcommands/command/FakeCommandSource.java
     * */
    public static class FakeCommandSource extends ServerCommandSource {
        public FakeCommandSource(ClientPlayerEntity player) {
            super(player, player.getPos(), player.getRotationClient(), null, 0, player.getEntityName(), player.getName(), null, player);
        }

        @Override
        public Collection<String> getPlayerNames() {
            return MinecraftClient.getInstance().getNetworkHandler().getPlayerList()
                    .stream().map(e -> e.getProfile().getName()).collect(Collectors.toList());
        }
    }

}
