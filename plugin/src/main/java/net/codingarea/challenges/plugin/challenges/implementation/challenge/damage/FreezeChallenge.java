package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;

@Since("2.0.2")
public class FreezeChallenge extends SettingModifier {

	public FreezeChallenge() {
		super(MenuType.CHALLENGES, 5, 60, 20);
		setCategory(SettingCategory.DAMAGE);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.BLUE_ICE, Message.forName("item-freeze-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-time-seconds-description").asArray(getValue());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(@Nonnull EntityDamageEvent event) {
		if (!shouldExecuteEffect()) return;
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (ignorePlayer(player)) return;
		if (ChallengeHelper.finalDamageIsNull(event)) return;
		double damage = ChallengeHelper.getFinalDamage(event);

		setFreeze(player, damage);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onMove(@Nonnull PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getTo() == null) return;
		if (event.getTo().getY() == event.getFrom().getY() && event.getTo().getX() == event.getFrom().getX() && event.getTo().getZ() == event.getFrom().getZ())
			return;
		PotionEffect effect = event.getPlayer().getPotionEffect(MinecraftNameWrapper.SLOWNESS);
		if (effect == null || effect.getAmplifier() != 255) return;
		event.setCancelled(true);
	}

	public void setFreeze(LivingEntity entity, double damage) {
		int time = (int) (damage * getValue() * 20 / 2);
		PotionEffect effect = entity.getPotionEffect(MinecraftNameWrapper.SLOWNESS);
		entity.removePotionEffect(MinecraftNameWrapper.SLOWNESS);

		if (effect != null && effect.getAmplifier() == 255) time += effect.getDuration();

		entity.addPotionEffect(new PotionEffect(MinecraftNameWrapper.SLOWNESS, time, 255));
	}

}
