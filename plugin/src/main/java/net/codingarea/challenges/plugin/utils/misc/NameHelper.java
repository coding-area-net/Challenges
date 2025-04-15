package net.codingarea.challenges.plugin.utils.misc;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.management.cloud.CloudSupportManager;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public final class NameHelper {

	private NameHelper() {
	}

	@Nonnull
	public static String getName(@Nonnull OfflinePlayer player) {
		CloudSupportManager cloudSupport = Challenges.getInstance().getCloudSupportManager();
		if (cloudSupport.isNameSupport() && cloudSupport.hasNameFor(player.getUniqueId())) {
			if (player.isOnline() && player.getPlayer() != null) {
				return cloudSupport.getColoredName(player.getPlayer());
			} else {
				return cloudSupport.getColoredName(player.getUniqueId());
			}
		}

		if (player.getName() == null) {
			return "";
		}

		return player.getName();
	}

}
