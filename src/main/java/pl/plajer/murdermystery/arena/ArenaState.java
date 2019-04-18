/*
 * MurderMystery - Find the murderer, kill him and survive!
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.murdermystery.arena;

/**
 * @author TomTheDeveloper
 * <p>
 * Contains all GameStates.
 */
public enum ArenaState {
  WAITING_FOR_PLAYERS("Waiting"), STARTING("Starting"), IN_GAME("Playing"), ENDING("Finishing"), RESTARTING("Restarting");

  String formattedName;

  ArenaState(String formattedName) {
    this.formattedName = formattedName;
  }

  public String getFormattedName() {
    return formattedName;
  }
}
