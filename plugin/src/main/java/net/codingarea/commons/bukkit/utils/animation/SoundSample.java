package net.codingarea.commons.bukkit.utils.animation;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class SoundSample {

	public static final SoundSample
			CLICK           = new SoundSample().addSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 0.5f),
			BASS_OFF        = new SoundSample().addSound(Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F),
			BASS_ON         = new SoundSample().addSound(Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F),
			PLING           = new SoundSample().addSound(Sound.BLOCK_NOTE_BLOCK_BELL, 1),
			KLING           = new SoundSample().addSound(Sound.ENTITY_PLAYER_LEVELUP, 0.6F, 2),
			LEVEL_UP        = new SoundSample().addSound(Sound.ENTITY_PLAYER_LEVELUP, 0.6F, 1.1f),
			PLOP            = new SoundSample().addSound(Sound.ENTITY_CHICKEN_EGG, 1, 2),
			LOW_PLOP        = new SoundSample().addSound(Sound.ENTITY_CHICKEN_EGG, 1, 1.3f),
			DEATH           = new SoundSample().addSound(Sound.ENTITY_BAT_DEATH, 0.7F),
			TELEPORT        = new SoundSample().addSound(Sound.ITEM_CHORUS_FRUIT_TELEPORT, 0.9F),
			OPEN            = new SoundSample().addSound(KLING).addSound(PLOP),
			EAT             = new SoundSample().addSound(Sound.ENTITY_PLAYER_BURP, 1),
			BLAST           = new SoundSample().addSound(Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1),
			BREAK           = new SoundSample().addSound(Sound.ENTITY_WITHER_BREAK_BLOCK, 0.7f),
			WIN             = new SoundSample().addSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1),
			DRAGON_BREATH   = new SoundSample().addSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5F);

	public static void playStatusSound(@Nonnull Player player, boolean enabled) {
		(enabled ? BASS_ON : BASS_OFF).play(player);
	}

	private static final class SoundFrame {

		@Getter
        @Getter
        private final float pitch, volume;
		private final Sound sound;

		public SoundFrame(@Nonnull Sound sound, float volume, float pitch) {
			this.volume = volume;
			this.pitch = pitch;
			this.sound = sound;
		}

		public SoundFrame(@Nonnull Sound sound, float volume) {
			this(sound, volume, 1);
		}

		public void play(@Nonnull Player player, @Nonnull Location location) {
			player.playSound(location, sound, volume, pitch);
		}

        @Nonnull
		public Sound getSound() {
			return sound;
		}

	}

	private final List<SoundFrame> frames = new ArrayList<>();

	@Nonnull
	public SoundSample addSound(@Nonnull Sound sound, float volume, float pitch) {
		frames.add(new SoundFrame(sound, volume, pitch));
		return this;
	}

	@Nonnull
	public SoundSample addSound(@Nonnull Sound sound, float volume) {
		frames.add(new SoundFrame(sound, volume));
		return this;
	}

	@Nonnull
	public SoundSample addSound(@Nonnull SoundSample sound) {
		frames.addAll(sound.frames);
		return this;
	}

	public void play(@Nonnull Player player) {
		play(player, player.getLocation());
	}

	public void play(@Nonnull Player player, @Nonnull Location location) {
		for (SoundFrame frame : frames) {
			frame.play(player, location);
		}
	}

	public void playIfPlayer(@Nonnull Object target) {
		if (target instanceof Player)
			play((Player) target);
	}

	public void broadcast() {
		Bukkit.getOnlinePlayers().forEach(this::play);
	}

	public void broadcast(@Nonnull Location location) {
		Bukkit.getOnlinePlayers().forEach(player -> play(player, location));
	}

}
