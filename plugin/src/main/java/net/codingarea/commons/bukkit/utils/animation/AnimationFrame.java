package net.codingarea.commons.bukkit.utils.animation;

import net.codingarea.commons.bukkit.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class AnimationFrame implements Cloneable {

	private final ItemStack[] content;
	private boolean sound = true;

	public AnimationFrame(@Nonnull ItemStack[] content) {
		this.content = Arrays.copyOf(content, content.length);
	}

	public AnimationFrame(int size) {
		this.content = new ItemStack[size];
	}

	@Nonnull
	public AnimationFrame fill(@Nonnull ItemStack item) {
		Arrays.fill(content, item);
		return this;
	}

	@Nonnull
	public AnimationFrame setAccent(int... slots) {
		for (int slot : slots) {
			content[slot] = ItemBuilder.FILL_ITEM_2;
		}
		return this;
	}

	@Nonnull
	public AnimationFrame setItem(int slot, @Nonnull ItemBuilder item) {
		return setItem(slot, item.build());
	}

	@Nonnull
	public AnimationFrame setItem(int slot, @Nonnull ItemStack item) {
		content[slot] = item;
		return this;
	}

	@Nonnull
	public AnimationFrame setSound(boolean play) {
		this.sound = play;
		return this;
	}

	@Nullable
	public ItemStack getItem(int slot) {
		return content[slot];
	}

	@Nullable
	public Material getItemType(int slot) {
		return getItem(slot) == null ? Material.AIR : getItem(slot).getType();
	}

	@Nonnull
	public ItemStack[] getContent() {
		return content;
	}

	public boolean shouldPlaySound() {
		return sound;
	}

	public int getSize() {
		return content.length;
	}

	@Nonnull
	@Override
	public AnimationFrame clone() {
		return new AnimationFrame(content);
	}

}
