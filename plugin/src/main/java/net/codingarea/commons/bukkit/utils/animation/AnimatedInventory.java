package net.codingarea.commons.bukkit.utils.animation;

import net.codingarea.commons.bukkit.core.BukkitModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimatedInventory {

	private final List<AnimationFrame> frames = new ArrayList<>();
	private final InventoryHolder holder;
	private final int size;
	private final String title;
	private SoundSample frameSound = SoundSample.CLICK, endSound = SoundSample.OPEN;
	private int frameDelay = 1;

	public AnimatedInventory(@Nonnull String title, int size) {
		this(title, size, null);
	}

	public AnimatedInventory(@Nonnull String title, int size, @Nullable InventoryHolder holder) {
		this.title = title;
		this.size = size;
		this.holder = holder;
	}

	public void open(@Nonnull Player player) {
		open(player, BukkitModule.getFirstInstance());
	}

	public void open(@Nonnull Player player, @Nonnull JavaPlugin plugin) {
		if (!Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTask(plugin, () -> open(player, plugin));
			return;
		}

		Inventory inventory = createInventory();
		applyFrame(inventory, 0, player);
		player.openInventory(inventory);

		AtomicInteger index = new AtomicInteger(1);
		Bukkit.getScheduler().runTaskTimer(plugin, task -> {

			boolean opened = inventory.getViewers().contains(player);
			if (index.get() >= frames.size() || !opened) {
				task.cancel();
				return;
			}

			applyFrame(inventory, index.get(), player);

			index.incrementAndGet();

		}, frameDelay, frameDelay);

	}

	public void openNotAnimated(@Nonnull Player player, boolean playSound) {
		openNotAnimated(player, playSound, BukkitModule.getFirstInstance());
	}

	public void openNotAnimated(@Nonnull Player player, boolean playSound, @Nonnull JavaPlugin plugin) {
		if (!Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTask(plugin, () -> openNotAnimated(player, playSound, plugin));
			return;
		}

		Inventory inventory = createInventory();

		if (!frames.isEmpty()) {
			AnimationFrame frame = getLastFrame();
			inventory.setContents(frame.getContent());
		}

		player.openInventory(inventory);

		if (playSound && endSound != null) endSound.play(player);

	}

	private void applyFrame(@Nonnull Inventory inventory, int index, @Nonnull Player viewer) {
		AnimationFrame frame = frames.get(index);
		inventory.setContents(frame.getContent());

		if (index == frames.size() - 1 && endSound != null) endSound.play(viewer);
		else if (frameSound != null && frame.shouldPlaySound()) frameSound.play(viewer);
	}

	@Nonnull
	public AnimatedInventory addFrame(@Nonnull AnimationFrame frame) {
		if (size != frame.getSize()) throw new IllegalArgumentException("AnimationFrame must have the same size (Expected " + size + "; Got " + frame.getSize() + ")");
		frames.add(frame);
		return this;
	}

	@Nonnull
	public AnimationFrame createAndAdd() {
		AnimationFrame frame = new AnimationFrame(size);
		addFrame(frame);
		return frame;
	}

	@Nonnull
	public AnimationFrame getFrame(int index) {
		return frames.get(index);
	}

	@Nonnull
	public AnimationFrame getOrCreateFrame(int index) {
		while (frames.size() <= index) {
			cloneLastAndAdd();
		}
		return getFrame(index);
	}

	@Nonnull
	public AnimationFrame cloneAndAdd(int index) {
		AnimationFrame frame = getFrame(index).clone();
		addFrame(frame);
		return frame;
	}

	@Nonnull
	public AnimationFrame getLastFrame() {
		if (frames.isEmpty()) throw new IllegalStateException("Frames are empty");
		return getFrame(frames.size() - 1);
	}

	@Nonnull
	public AnimationFrame cloneLastAndAdd() {
		AnimationFrame frame = getLastFrame().clone();
		addFrame(frame);
		return frame;
	}

	@Nonnull
	public AnimatedInventory setEndSound(@Nullable SoundSample endSound) {
		this.endSound = endSound;
		return this;
	}

	@Nonnull
	public AnimatedInventory setFrameSound(@Nullable SoundSample frameSound) {
		this.frameSound = frameSound;
		return this;
	}

	@Nonnull
	public AnimatedInventory setFrameDelay(int delay) {
		if (delay < 1) throw new IllegalArgumentException("Delay cannot be smaller than 1");
		this.frameDelay = delay;
		return this;
	}

	@Nonnull
	private Inventory createInventory() {
		return Bukkit.createInventory(holder, size, title);
	}

}
