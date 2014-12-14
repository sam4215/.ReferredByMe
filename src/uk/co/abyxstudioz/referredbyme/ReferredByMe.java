package uk.co.abyxstudioz.referredbyme;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.spongepowered.api.entity.EntityInteractionType;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.block.BlockUpdateEvent;
import org.spongepowered.api.event.player.PlayerInteractEvent;
import org.spongepowered.api.event.player.PlayerJoinEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.util.config.ConfigFile;
import org.spongepowered.api.util.event.Subscribe;
import org.spongepowered.api.event.player.PlayerJoinEvent;
import uk.co.abyxstudioz.referredbyme.commands.*;

import java.util.logging.Logger;

@Plugin(name="ReferredByMe Rebooted", id="RBM-R", version = "0.0.1")
public class ReferredByMe {

    private boolean update;
    Logger logger = Logger.getLogger("Minecraft");

    @Subscribe
    public void onEnable(ServerStartedEvent event) {
        this.saveDefaultConfig();
        logger.info("ReferredByMe Rebooted has been enabled.");
        logger.info("Author: Samuel Bird (AKA ButterDev or sam4215)");
        logger.info("Version: 0.0.1");
    }
    @Subscribe
    public void onDisable(ServerStoppingEvent event)
    {

    }

    @Subscribe
    public void playerJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        if (!ConfigFile.getBoolean("Players." + player.getName().toLowerCase() + ".Referred")) {
            if (ConfigFile.getString("Players." + player.getName().toLowerCase() + ".IP") == null) {
                if (!ConfigFile.getString("Messages.WelcomeMessage").equals("")) {
                    String WelcomeMessage = ConfigFile().getString("Messages.WelcomeMessage").replace("{player}",
                            player.getName().toLowerCase());
                    player.sendMessage(ChatColor.RED + "[ReferredByMe] " + ChatColor.GREEN + WelcomeMessage);
                    ConfigFile.set("Players." + player.getName().toLowerCase() + ".IP",
                            player.getAddress().getAddress().getHostAddress());
                }
                this.saveConfig();
            }
            if (!ConfigFile.getString("Messages.WhoReferred").equals("")) {
                String WhoReferred = ConfigFile.getString("Messages.WhoReferred").replace("{player}",
                        player.getName().toLowerCase());
                player.sendMessage(ChatColor.RED + "[ReferredByMe] " + ChatColor.GREEN + WhoReferred);
            }
        }
        if (evt.spongepowered.api.event.player.nfig().getInt("Players." + player.getName().toLowerCase() + ".Rank") == 0) {
            ConfigFile.set("Rank.Ranks", ConfigFile().getInt("Rank.Ranks") + 1);
            ConfigFile().set("Players." + player.getName().toLowerCase() + ".Rank", ConfigFile().getInt("Rank.Ranks"));
            ConfigFile().set("Rank." + ConfigFile().getInt("Rank.Ranks") + ".Name", player.getName().toLowerCase());
            ConfigFile().set("Rank." + ConfigFile().getInt("Rank.Ranks") + ".Referrals",
                    ConfigFile().getInt("Players." + player.getName().toLowerCase() + ".Referrals"));
            this.saveConfig();
        }
        int Rank = ConfigFile.getInt("Players." + player.getName().toLowerCase() + ".Rank");
        int Rankers = Rank - 1;
        if (ConfigFile.getInt("Players." + player.getName().toLowerCase() + ".Referrals") >= ReferredByMe.this.getConfig()
                .getInt("Rank." + Rankers + ".Referrals") && Rankers != 0) {
            ConfigFile.set("Rank." + Rank + ".Name", ConfigFile().getString("Rank." + Rankers + ".Name"));
            ConfigFile.set("Rank." + Rank + ".Referrals", ConfigFile().getInt("Rank." + Rankers + ".Referrals"));
            ConfigFile().set("Players." + ConfigFile().getString("Rank." + Rankers + ".Name") + ".Rank", Rank);
            ConfigFile().set("Rank." + Rankers + ".Name", player.getName().toLowerCase());
            ConfigFile().set("Rank." + Rankers + ".Referrals",
                    ConfigFile().getInt("Players." + player.getName().toLowerCase() + ".Referrals"));
            ConfigFile().set("Players." + player.getName().toLowerCase() + ".Rank", Rankers);
            this.saveConfig();
        }
        if (!ConfigFile().getString("Messages.ReferElse").equals("")) {
            String ReferElse = ConfigFile().getString("Messages.ReferElse").replace("{player}", player.getName().toLowerCase());
            player.sendMessage(ChatColor.RED + "[ReferredByMe] " + ChatColor.GREEN + ReferElse);
        }
        if (update && player.isOp()) {
            player.sendMessage(ChatColor.RED + "[ReferredByMe] " + ChatColor.DARK_RED + "You are using Config Version "
                    + ChatColor.RED + ConfigFile().getDouble("Version") + ChatColor.DARK_RED + " with ReferredByMe Version "
                    + ChatColor.RED + "0.7" + ChatColor.DARK_RED + ". Please update your config to follow " + ChatColor.WHITE
                    + "http://dev.bukkit.org/bukkit-plugins/referredbyme/pages/example-config/");
        }
        for (int i = 0; i < ConfigFile.getString("Rewards.Tiers").toInt; i++) {
            int j = i + 1;
            if (ConfigFile().getBoolean("Players." + player.getName().toLowerCase() + ".Claimable." + j)) {
                player.sendMessage(ChatColor.RED + "[ReferredByMe] " + ChatColor.GREEN
                        + ConfigFile().getString("Messages.Claimable"));
                break;
            }
        }
        if (ConfigFile().getInt("Players." + player.getName().toLowerCase() + ".Claimable.RCount") != 0) {
            player.sendMessage(ChatColor.RED + "[ReferredByMe] " + ChatColor.GREEN + ConfigFile().getString("Messages.Claimable"));
        }
    }

    @Subscribe
    public void onSignChange(BlockUpdateEvent event) {
        if(event.getBlock().equals(ItemTypes.SIGN)) {
        if (event.getLine(0).equalsIgnoreCase("[referrank]")) {
            event.setLine(0, "§1[referrank]");
            event.setLine(2, "§4" + getConfig().getString("Rank." + event.getLine(1) + ".Name"));
            event.setLine(3, "§4" + getConfig().getString("Rank." + event.getLine(1) + ".Referrals"));
            event.setLine(1, "§2" + event.getLine(1));
        }
    }}

    @Subscribe
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getInteractionType() == EntityInteractionType.RIGHT_CLICK) {
            if(event.getBlock(ItemTypes.SIGN)) { //FIXME
                Player player = event.getPlayer();
                Sign sign = (Sign) event.getClickedBlock().getState();
                String signline0 = sign.getLine(0);
                String signLine1 = sign.getLine(1);
                if (signline0.equalsIgnoreCase("[referrank]")) {
                    sign.setLine(0, "§1[referrank]");
                    sign.setLine(1, "§2" + signLine1.replace("§2", ""));
                    sign.setLine(2, "§4" + getConfig().getString("Rank." + signLine1.replace("§2", "") + ".Name"));
                    sign.setLine(3, "§4" + getConfig().getString("Rank." + signLine1.replace("§2", "") + ".Referrals"));
                    player.sendMessage(ChatColor.RED + "[ReferredByMe] " + ChatColor.GREEN + "Updated");
                    sign.update(true);
                }
            }
        }
    }
}
