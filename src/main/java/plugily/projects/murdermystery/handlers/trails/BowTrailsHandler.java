package plugily.projects.murdermystery.handlers.trails;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.murdermystery.Main;

/**
 * @author 2Wild4You, Tigerpanzer_02
 * <p>
 * Created at 19.02.2021
 */
public class BowTrailsHandler implements Listener {

    private final Main plugin;

    public BowTrailsHandler(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArrowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow)) {
            return;
        }

        Entity projectile = event.getProjectile();

        if (projectile.isDead() || projectile.isOnGround()) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!plugin.getArenaRegistry().isInArena(player) || !plugin.getTrailsManager().gotAnyTrails(player)) {
            return;
        }

        Trail trail = plugin.getTrailsManager().getRandomTrail(player);
        plugin.getDebugger().debug("Spawning particle with perm {0} for player {1}", trail.getPermission(), player.getName());

        if (Bukkit.getRegionScheduler() != null) { // ✅ Detects Folia
            RegionScheduler scheduler = Bukkit.getRegionScheduler();
            scheduler.runAtFixedRate(plugin, projectile.getLocation(), task -> {
                if (projectile.isDead() || projectile.isOnGround()) {
                    plugin.getDebugger().debug("Stopped spawning particle with perm {0} for player {1}",
                            trail.getPermission(), player.getName());
                    task.cancel(); // ✅ Properly cancels the task in Folia
                    return;
                }
                try {
                    VersionUtils.sendParticles(trail.getName(), player, projectile.getLocation(), 3);
                } catch (Exception ignored) {
                }
            }, 0L, 1L);
        } else { // ✅ Paper fallback
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (projectile.isDead() || projectile.isOnGround()) {
                        plugin.getDebugger().debug("Stopped spawning particle with perm {0} for player {1}",
                                trail.getPermission(), player.getName());
                        cancel();
                        return;
                    }
                    try {
                        VersionUtils.sendParticles(trail.getName(), player, projectile.getLocation(), 3);
                    } catch (Exception ignored) {
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
    }
}
