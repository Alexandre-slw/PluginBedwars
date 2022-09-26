package com.alexandre.bedwars.party;

import com.alexandre.bedwars.utils.TimeMessage;
import com.alexandre.core.TimerPlugin;
import com.alexandre.core.players.GamePlayer;
import com.alexandre.core.players.GamePlayersRegistry;
import com.alexandre.core.timers.Timer;
import com.alexandre.core.utils.BroadcastUtils;
import com.alexandre.core.utils.BungeeCordUtils;
import com.alexandre.core.utils.Title;
import com.alexandre.bedwars.Main;
import com.alexandre.bedwars.elements.SpecialOreGenerator;
import com.alexandre.bedwars.players.BedwarsPlayer;
import com.alexandre.bedwars.players.team.BedwarsTeam;
import com.alexandre.bedwars.players.team.TeamColor;
import com.alexandre.bedwars.utils.Reset;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class Party {

	private static String map = "";

	private long ticks = 0L;
	
	private boolean isStart = false;
	private int task = 0;

	private boolean isStop = false;
	private boolean isPartyEnd = false;

	private final CopyOnWriteArrayList<BedwarsTeam> teams = new CopyOnWriteArrayList<>();
	private final ArrayList<TimeMessage> messages = new ArrayList<>();

	private int spawnX = 0;
	private int spawnY = 130;
	private int spawnZ = 0;

	private int buildLimit = 120;

	private final CopyOnWriteArrayList<SpecialOreGenerator> generators = new CopyOnWriteArrayList<>();
	private final CopyOnWriteArrayList<Timer> timers = new CopyOnWriteArrayList<>();

	public Party() {
		if (this.isStop()) return;
		Party.map = Bukkit.getWorlds().get(0).getName();

		FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(this.getWorldFolder(), "bedwars.yml"));
		this.spawnX = configuration.getInt("spawn.x", 0);
		this.spawnY = configuration.getInt("spawn.y", 130);
		this.spawnZ = configuration.getInt("spawn.z", 0);
		this.buildLimit = configuration.getInt("built_limit", 120);

		int diamond_generators = configuration.getInt("diamond_generators.number", 0);
		for (int i = 0; i < diamond_generators; i++) {
			String key = "diamond_generators." + i + ".";
			long spawnTime = configuration.getLong(key + "spawnTime", 30_000);
			int x = configuration.getInt(key + "x", 0);
			int y = configuration.getInt(key + "y", 0);
			int z = configuration.getInt(key + "z", 0);
			this.generators.add(new SpecialOreGenerator(spawnTime, new Location(Bukkit.getWorlds().get(0), x + 0.5F, y + 0.5F, z + 0.5F), "§bDiamond", Material.DIAMOND, Material.DIAMOND_BLOCK));
		}

		int emerald_generators = configuration.getInt("emerald_generators.number", 0);
		for (int i = 0; i < emerald_generators; i++) {
			String key = "emerald_generators." + i + ".";
			long spawnTime = configuration.getLong(key + "spawnTime", 60_000);
			int x = configuration.getInt(key + "x", 0);
			int y = configuration.getInt(key + "y", 0);
			int z = configuration.getInt(key + "z", 0);
			this.generators.add(new SpecialOreGenerator(spawnTime, new Location(Bukkit.getWorlds().get(0), x + 0.5F, y + 0.5F, z + 0.5F), "§aEmerald", Material.EMERALD, Material.EMERALD_BLOCK));
		}

		Reset.resetPlayers();
		new Waiting(this);

		int playerCount = 0;
		for (TeamColor color : TeamColor.values()) {
			if (playerCount >= Main.getInstance().getServer().getMaxPlayers()) break;
			this.teams.add(new BedwarsTeam(color, Main.getMaxTeamMembers(), configuration));
			playerCount += Main.getMaxTeamMembers();
		}

		this.messages.add(new TimeMessage(0, "\n§bDiamond generators §f-> §eTier §cII\n", 7200));
		this.messages.add(new TimeMessage(1, "\n§aEmerald generators §f-> §eTier §cII\n", 7200 * 2));
		this.messages.add(new TimeMessage(2, "\n§bDiamond generators §f-> §eTier §cIII\n", 7200 * 3));
		this.messages.add(new TimeMessage(3, "\n§aEmerald generators §f-> §eTier §cIII\n", 7200 * 4));
		this.messages.add(new TimeMessage(4, "\n§cAll beds have been destroyed!\n", 7200 * 5));
		this.messages.add(new TimeMessage(5, "\n§cSudden death\n", 7200 * 5 + 12000));
		this.messages.add(new TimeMessage(6, "\n§6Party END! §7(the party has been too long)\n", 7200 * 5 + 12000 * 2));
	}
	
	public void start() {
		if (this.isStop()) return;
		if (this.isStart()) return;
		this.isStart = true;
		BungeeCordUtils.setCanAutoJoin(false);

		BroadcastUtils.chat("\n§9------------\n§e§lBED WARS §7- §fProtect your bed and destroy the bed of the other teams.\n§9------------\n");
		Reset.resetPlayers();
		for (GamePlayer player : GamePlayersRegistry.getPlayers()) {
			BedwarsPlayer bedwarsPlayer = (BedwarsPlayer) player;
			if (bedwarsPlayer.getTeam() != null) {
				bedwarsPlayer.respawn();
				continue;
			}

			for (BedwarsTeam team : this.teams) {
				if (team.getMembers().size() >= team.getMaxMembers()) continue;
				team.addMember(bedwarsPlayer);
				break;
			}
			bedwarsPlayer.respawn();
		}

		for (SpecialOreGenerator generator : this.generators) {
			generator.resetTime();
		}

		for (BedwarsTeam team : this.teams) {
			team.init();
		}
		this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), Party.this::update, 0L, 1L);

		for (int x = -40; x < 80; x++) {
			for (int y = -4; y < 20; y++) {
				for (int z = -40; z < 80; z++) {
					Location l = new Location(Bukkit.getWorlds().get(0), this.spawnX, this.spawnY, this.spawnZ).add(x, y, z);
					l.getBlock().setType(Material.AIR);
				}
			}
		}

		this.timers.add(TimerPlugin.getTimerApi().createTickTimer("Diamond II", new ItemStack(Material.DIAMOND), false, 7200));
		this.timers.add(TimerPlugin.getTimerApi().createTickTimer("Emerald II", new ItemStack(Material.EMERALD), false, 7200 * 2));
		this.timers.add(TimerPlugin.getTimerApi().createTickTimer("Diamond III", new ItemStack(Material.DIAMOND), false, 7200 * 3));
		this.timers.add(TimerPlugin.getTimerApi().createTickTimer("Emerald III", new ItemStack(Material.EMERALD), false, 7200 * 4));
		this.timers.add(TimerPlugin.getTimerApi().createTickTimer("Bed gone", new ItemStack(Material.BED), false, 7200 * 5));
		this.timers.add(TimerPlugin.getTimerApi().createTickTimer("Sudden death", new ItemStack(Material.DRAGON_EGG), false, 7200 * 5 + 12000));
		this.timers.add(TimerPlugin.getTimerApi().createTickTimer("End of the game", new ItemStack(Material.BARRIER), false, 7200 * 5 + 12000 * 2));
		for (Timer timer : this.timers) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				timer.addReceiver(player);
			}
		}
	}

	public void stop() {
		if (this.isStop()) return;

		Bukkit.getScheduler().cancelTask(this.task);
		this.isStop = true;

		for (SpecialOreGenerator generator : this.generators) {
			generator.clean();
		}

		for (BedwarsTeam team : this.teams) {
			team.clean();
		}

		for (Timer timer : this.timers) {
			TimerPlugin.getTimerApi().removeTimer(timer);
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), Party.this::endParty, 20);
	}
	
	public void endParty() {
		if (this.isPartyEnd) return;
		this.isPartyEnd = true;

		int aliveTeam = 0;
		for (BedwarsTeam team : this.getTeams()) {
			team.checkEliminated();
			if (team.isEliminated()) continue;
			aliveTeam++;
		}
		if (aliveTeam > 1) {
			BroadcastUtils.chat("\n§c§lParty ends with " + aliveTeam + " teams still alive.\n");
		} else {
			for (BedwarsTeam team : this.getTeams()) {
				if (team.isEliminated()) {
					for (BedwarsPlayer member : team.getMembers()) {
						Title.sendTitle(member.getPlayer(), "§c§lGAME OVER!", "", 0, 100, 20);
					}
					continue;
				}
				String name = StringUtils.capitalize(team.getColor().name().toLowerCase(Locale.ROOT));
				name = team.getColor().getTextColor() + "§l" + name;
				BroadcastUtils.chat("\n§f§lPARTY END > " + name + " team §fwon the game!\n");
				for (BedwarsPlayer member : team.getMembers()) {
					Title.sendTitle(member.getPlayer(), "§6§lVICTORY!", "", 0, 100, 20);
				}
			}
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), BungeeCordUtils::sendCloseRequest, 200L);
	}
	
	public void update() {
		if (this.isStop()) return;

		this.ticks++;

		for (SpecialOreGenerator generator : this.generators) {
			generator.update();
		}

		for (BedwarsTeam team : this.getTeams()) {
			team.update();
		}

		this.messages.forEach(message -> message.sendInTime(this.ticks));

		if (Bukkit.getOnlinePlayers().size() <= 1) {
			this.stop();
			return;
		}

		int aliveTeam = 0;
		for (BedwarsTeam team : this.getTeams()) {
			team.checkEliminated();
			if (team.isEliminated()) continue;
			aliveTeam++;
		}
		if (aliveTeam <= 1) this.stop();
	}
	
	public void onMessageSend(int id) {
		switch (id) {
			case 0:
			case 2:
				for (SpecialOreGenerator generator : this.generators) {
					if (generator.getMaterial() != Material.DIAMOND) continue;
					generator.upgrade();
				}
				break;

			case 1:
			case 3:
				for (SpecialOreGenerator generator : this.generators) {
					if (generator.getMaterial() != Material.EMERALD) continue;
					generator.upgrade();
				}
				break;

			case 4:
				for (BedwarsTeam team : this.teams) {
					team.destroyBed();
				}
				break;

			case 5:
				// sudden death?
				break;

			case 6:
				this.endParty();
				break;
		}
	}

	public String getMapName() {
		return Party.map;
	}
	
	public World getMap() {
		return Bukkit.getWorld(this.getMapName());
	}
	
	public boolean isStart() {
		return this.isStart;
	}
	
	public boolean isStop() {
		return this.isStop;
	}

	public File getWorldFolder() {
		return new File(Main.getInstance().getServer().getWorldContainer(), Party.map);
	}

	public int getSpawnX() {
		return this.spawnX;
	}

	public int getSpawnY() {
		return this.spawnY;
	}

	public int getSpawnZ() {
		return this.spawnZ;
	}

	public int getBuildLimit() {
		return this.buildLimit;
	}

	public CopyOnWriteArrayList<BedwarsTeam> getTeams() {
		return this.teams;
	}

	public CopyOnWriteArrayList<SpecialOreGenerator> getGenerators() {
		return this.generators;
	}

	public ArrayList<TimeMessage> getMessages() {
		return this.messages;
	}

	public long getTicks() {
		return this.ticks;
	}
}
