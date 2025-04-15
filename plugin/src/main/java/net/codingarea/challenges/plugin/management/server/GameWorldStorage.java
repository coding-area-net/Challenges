package net.codingarea.challenges.plugin.management.server;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.management.challenges.entities.GamestateSaveable;
import net.codingarea.challenges.plugin.spigot.generator.VoidMapGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class GameWorldStorage implements GamestateSaveable {

	private final List<World> worlds = new LinkedList<>();
	private World overworld;
	private World nether;
	private World end;
	private World voidWorld;

	public void enable() {
		Challenges.getInstance().getChallengeManager().registerGameStateSaver(this);

		for (int i = 0; i < 3 && i < Bukkit.getWorlds().size(); i++) {
			World world = Bukkit.getWorlds().get(i);
			switch (i) {
				case 0: {
					overworld = world;
					worlds.add(world);
				}
				break;
				case 1: {
					if (world.getEnvironment() == Environment.NETHER) {
						nether = world;
						worlds.add(world);
					} else if (world.getEnvironment() == Environment.THE_END) {
						end = world;
						worlds.add(world);
					}
				}
				break;
				case 2: {
					if (world.getEnvironment() == Environment.THE_END) {
						end = world;
						worlds.add(world);
					}
				}
				break;
			}
		}

	}

	@Override
	public String getUniqueGamestateName() {
		return getClass().getSimpleName().toLowerCase();
	}

	@Override
	public void writeGameState(@NotNull Document document) {
		document.set("void-generated", voidWorld != null);
	}

	@Override
	public void loadGameState(@NotNull Document document) {
		if (document.getBoolean("void-generated")) {
			getOrCreateVoidWorld();
		}
	}

	@Nonnull
	public World getOrCreateVoidWorld() {
		if (voidWorld != null) {
			return voidWorld;
		}

		VoidMapGenerator generator = new VoidMapGenerator();
		voidWorld = new WorldCreator("void").type(WorldType.FLAT).generator(
				generator).createWorld();
		worlds.add(voidWorld);

		return voidWorld;
	}

	public World getWorld(@Nullable Environment environment) {
		if (environment == null) return null;
		switch (environment) {
			case NORMAL:
				return overworld;
			case NETHER:
				return nether;
			case THE_END:
				return end;
			case CUSTOM:
				return Challenges.getInstance().getWorldManager().getExtraWorld();
		}
		return null;
	}

	public List<String> getCustomGeneratedGameWorlds() {
		return Collections.singletonList("void");
	}

	public List<World> getGameWorlds() {
		return Collections.unmodifiableList(worlds);
	}

}
