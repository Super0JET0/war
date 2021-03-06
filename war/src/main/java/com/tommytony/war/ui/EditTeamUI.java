package com.tommytony.war.ui;

import com.google.common.collect.ImmutableList;
import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.TeamConfig;
import com.tommytony.war.config.TeamConfigBag;
import com.tommytony.war.mapper.WarzoneYmlMapper;
import com.tommytony.war.volume.Volume;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

/**
 * Created by Connor on 7/27/2017.
 */
public class EditTeamUI extends ChestUI {
	private final Team team;

	public EditTeamUI(Team team) {
		super();
		this.team = team;
	}

	@Override
	public void build(final Player player, Inventory inv) {
		ItemStack item;
		ItemMeta meta;
		int i = 0;
		item = new ItemStack(Material.GOLD_SPADE, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("Add additional spawn");
		item.setItemMeta(meta);
		this.addItem(inv, i++, item, new Runnable() {
			@Override
			public void run() {
				if (team.getZone().getVolume().contains(player.getLocation())) {
					team.addTeamSpawn(player.getLocation());
					player.sendTitle("", "Additional spawn added", 10, 20, 10);
				} else {
					player.sendTitle("", ChatColor.RED + "Can't add a spawn outside of the zone!", 10, 20, 10);
				}
			}
		});
		item = new ItemStack(Material.TNT, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("Delete");
		item.setItemMeta(meta);
		this.addItem(inv, 9*3-1, item, new Runnable() {
			@Override
			public void run() {
				if (team.getFlagVolume() != null) {
					team.getFlagVolume().resetBlocks();
				}
				for (Volume spawnVolume : team.getSpawnVolumes().values()) {
					spawnVolume.resetBlocks();
				}
				final Warzone zone = team.getZone();
				zone.getTeams().remove(team);
				if (zone.getLobby() != null) {
					zone.getLobby().setLocation(zone.getTeleport());
					zone.getLobby().initialize();
				}
				WarzoneYmlMapper.save(zone);
				War.war.msg(player, "Team " + team.getName() + " removed.");
			}
		});
		for (final TeamConfig option : TeamConfig.values()) {
			if (option.getTitle() == null) {
				continue;
			}
			if (option.getConfigType() == Boolean.class) {
				item = new Wool(team.getTeamConfig().resolveBoolean(option) ? DyeColor.LIME : DyeColor.RED).toItemStack(1);
				meta = item.getItemMeta();
				meta.setDisplayName(option.getTitle());
				meta.setLore(ImmutableList.of(option.getDescription()));
				item.setItemMeta(meta);
				this.addItem(inv, i++, item, new Runnable() {
					@Override
					public void run() {
						team.getTeamConfig().put(option, !team.getTeamConfig().resolveBoolean(option));
						TeamConfigBag.afterUpdate(team, player, option.name() + " set to " + team.getTeamConfig().resolveBoolean(option), false);
						War.war.getUIManager().assignUI(player, new EditTeamUI(team));
					}
				});
			} else {
				item = new ItemStack(Material.COMPASS, 1);
				meta = item.getItemMeta();
				meta.setDisplayName(option.getTitle());
				meta.setLore(ImmutableList.of(option.getDescription()));
				item.setItemMeta(meta);
				this.addItem(inv, i++, item, new Runnable() {
					@Override
					public void run() {
						player.sendTitle(option.getTitle(), team.getTeamConfig().resolveValue(option).toString(), 10, 70, 20);
						War.war.getUIManager().assignUI(player, new EditTeamUI(team));
					}
				});
			}
		}
	}

	@Override
	public String getTitle() {
		return ChatColor.RED + "Warzone \"" + team.getZone().getName() + "\": Team \"" + team.getName() + "\"";
	}

	@Override
	public int getSize() {
		return 9*3;
	}
}
