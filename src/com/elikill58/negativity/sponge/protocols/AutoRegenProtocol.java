package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.entity.HealEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer.FlyingReason;
import com.elikill58.negativity.sponge.utils.ReportType;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

public class AutoRegenProtocol extends Cheat {

	public AutoRegenProtocol() {
		super("AUTOREGEN", true, ItemTypes.GOLDEN_APPLE, false, true, "regen");
	}

	@Listener
	public void onPlayerInteract(InteractEvent e, @First Player p) {
		ItemType m = p.getItemInHand(HandTypes.MAIN_HAND).get().getType();
		if (m.equals(ItemTypes.GOLDEN_APPLE) || m.equals(ItemTypes.GOLDEN_CARROT))
			SpongeNegativityPlayer.getNegativityPlayer(p).flyingReason = FlyingReason.REGEN;
	}

	@Listener
	public void onRegen(HealEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		boolean hasPotion = false;
		for (PotionEffect pe : p.getOrCreate(PotionEffectData.class).get().effects())
			if (pe.getType().equals(PotionEffectTypes.POISON) || pe.getType().equals(PotionEffectTypes.BLINDNESS)
					|| pe.getType().equals(PotionEffectTypes.WITHER)
					|| pe.getType().equals(PotionEffectTypes.MINING_FATIGUE)
					|| pe.getType().equals(PotionEffectTypes.WEAKNESS) || pe.getType().equals(PotionEffectTypes.GLOWING)
					|| pe.getType().equals(PotionEffectTypes.HUNGER))
				hasPotion = true;
		if (hasPotion)
			np.flyingReason = FlyingReason.POTION;
		else
			np.flyingReason = FlyingReason.REGEN;
		long actual = System.currentTimeMillis(), dif = actual - np.LAST_REGEN;
		if (np.LAST_REGEN != 0
				&& !p.getOrCreate(PotionEffectData.class).get()
						.contains(PotionEffect.of(PotionEffectTypes.REGENERATION, 1, 1))
				&& np.hasDetectionActive(this)) {
			int ping = Utils.getPing(p);
			if (dif < (300 + ping)) {
				boolean mayCancel = SpongeNegativity.alertMod(ReportType.VIOLATION, p, this,
						Utils.parseInPorcent((dif < (50 + ping) ? 200 : 100) - dif - ping), "Player regen, last regen: "
								+ np.LAST_REGEN + " Actual time: " + actual + " Difference: " + dif,
						"Time between two regen: " + dif + " (in milliseconds)");
				if (isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
		np.LAST_REGEN = actual;
	}
}
