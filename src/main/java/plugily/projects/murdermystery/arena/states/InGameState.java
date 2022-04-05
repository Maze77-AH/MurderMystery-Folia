/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.murdermystery.arena.states;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginInGameState;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XSound;
import plugily.projects.murdermystery.arena.Arena;
import plugily.projects.murdermystery.arena.role.Role;
import plugily.projects.murdermystery.utils.ItemPosition;
import plugily.projects.thebridge.api.events.game.TBRoundStartEvent;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaUtils;
import plugily.projects.thebridge.arena.base.Base;

import java.util.Random;

/**
 * @author Plajer
 *     <p>Created at 03.06.2019
 */
public class InGameState extends PluginInGameState {

  @Override
  public void handleCall(PluginArena arena) {
    super.handleCall(arena);
    Arena pluginArena = (Arena) getPlugin().getArenaRegistry().getArena(arena.getId());
    if (pluginArena == null) {
      return;
    }
    if (arena.getTimer() <= 0) {
      getPlugin().getArenaManager().stopGame(false, arena);
    }
    int inGameLength = getPlugin().getConfig().getInt("Time-Manager.In-Game", 270);


    if(arena.getTimer() <= (inGameLength - 10) && arena.getTimer() > (inGameLength - 15)) {
      String murdererGetSword = chatManager.colorMessage("In-Game.Messages.Murderer-Get-Sword")
          .replace("%time%", Integer.toString(arena.getTimer() - (inGameLength - 15)));

      for(Player p : arena.getPlayers()) {
        p.sendMessage(murdererGetSword);
        XSound.UI_BUTTON_CLICK.play(p.getLocation(), 1, 1);
      }

      if(arena.getTimer() == (inGameLength - 14)) {
        if(pluginArena.getMurdererList().isEmpty()) ArenaManager.stopGame(false, pluginArena);

        for(Player p : pluginArena.getMurdererList()) {
          User murderer = getPlugin().getUserManager().getUser(p);

          if(murderer.isSpectator() || !p.isOnline())
            continue;

          p.getInventory().setHeldItemSlot(0);
          ItemPosition.setItem(p, ItemPosition.MURDERER_SWORD, plugin.getConfigPreferences().getMurdererSword());
        }
      }
    }

    //every 30 secs survive reward
    if(arena.getTimer() % 30 == 0) {
      new TitleBuilder("IN_GAME_MESSAGES_ARENA_PLAYING_TIME_LEFT").arena(pluginArena).sendArena();
      for(Player p : arena.getPlayersLeft()) {
        if(Role.isRole(Role.INNOCENT, p, pluginArena)) {
          ArenaUtils.addScore(getPlugin().getUserManager().getUser(p), ArenaUtils.ScoreAction.SURVIVE_TIME, 0);
        }
      }
    }

    if (arena.getTimer() <= 30
        || arena.getPlayersLeft().size() == pluginArena.aliveMurderer() + 1) {
      //todo locator config option
      if(getPlugin().getConfigPreferences().getOption("INNOCENT_LOCATOR")) {
        ArenaUtils.updateInnocentLocator(pluginArena);
      }
    }

    // no players - stop game
    if (pluginArena.getPlayersLeft().isEmpty()) {
      getPlugin().getArenaManager().stopGame(false, pluginArena);
    } else {
      // winner check
      if (arena.getPlayersLeft().size() == pluginArena.aliveMurderer()) {
        //todo placeholder check
        for(Player player : arena.getPlayers()) {

          if(pluginArena.getMurdererList().contains(player)) {

          }
        }

        getPlugin().getArenaManager().stopGame(false, pluginArena);
        // murderer speed add
        // todo config otpion
      } else if (getPlugin().getConfigPreferences().getOption("MURDERER_SPEED")
          && arena.getPlayersLeft().size() == pluginArena.aliveMurderer() + 1) {
        int multiplier = getPlugin().getConfig().getInt("Speed-Effect-Murderer.Speed", 3);

        if (multiplier > 1 && multiplier <= 10) {
          for (Player player : pluginArena.getMurdererList()) {
            if (pluginArena.isMurderAlive(player)) {
              // no potion because it adds particles which can be identified
              player.setWalkSpeed(0.1f * multiplier);
            }
          }
        }
      }
      //don't spawn it every time
      if(pluginArena.getSpawnGoldTimer() == pluginArena.getSpawnGoldTime()) {
        spawnSomeGold();
        pluginArena.setSpawnGoldTimer(0);
      } else {
        pluginArena.setSpawnGoldTimer(pluginArena.getSpawnGoldTimer()+1);
      }

    }
  }

  private void spawnSomeGold(Arena arena) {
    int spawnPointsSize = arena.getPlayerSpawnPoints().size();

    if(spawnPointsSize == 0) {
      return;
    }
//todo config option
    //may users want to disable it and want much gold on there map xD
    if(!getPlugin().getConfigPreferences().getOption("DISABLE_GOLD_LIMITER")) {
      //do not exceed amount of gold per spawn
      if(arena.getGoldSpawned().size() >= spawnPointsSize) {
        return;
      }
    }
//todo config option
    if(getPlugin().getConfigPreferences().getOption("SPAWN_GOLD_EVERY_SPAWNER_MODE")) {
      for(Location location : arena.getPlayerSpawnPoints()) {
        arena.getGoldSpawned().add(location.getWorld().dropItem(location, new ItemStack(Material.GOLD_INGOT, 1)));
      }
    } else {
      Location loc = arena.getGoldSpawned().get(spawnPointsSize == 1 ? 0 : new Random().nextInt(spawnPointsSize)).getLocation();
      arena.getGoldSpawned().add(loc.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT, 1)));
    }
  }
}
