package dev.bjarne.playerMilk;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class eventHandler implements Listener {

	public static main Main;
	public Boolean enableEffect;

	public eventHandler(Boolean ee) {
		this.enableEffect = ee;
	}

	public void setMain(main m) {
		eventHandler.Main = m;
		m.eH = this;

	}

	@EventHandler
	public void onPlayerInteractEventEntity(PlayerInteractEntityEvent event) {
		if (!event.getHand().equals(EquipmentSlot.HAND)) {
			// only fire event if interacted with main hand
			return;
		}
		Player p = event.getPlayer(); // player
		Entity e = event.getRightClicked(); // target
		if (p.getInventory().getItemInMainHand().getType() == Material.BUCKET && (e instanceof Player)) {
			Player t = (Player) e;
			// p.sendMessage("[PM] debug: " + t.getDisplayName());
			// p.sendMessage("[PM] debug: " + e.getEntityId());

			if (p.getGameMode() != GameMode.CREATIVE) {
				// remove 1 bucket
				p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
			}

			ItemStack item = new ItemStack(Material.MILK_BUCKET);
			ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.STICK);
			List<String> metalist = new ArrayList<String>();
			metalist.add(main.makeColors("&7&oMmmmh, so tasty"));

			meta.setDisplayName(main.makeColors("&f&l" + t.getDisplayName() + "&f's Milk"));
			meta.setLore(metalist);
			item.setItemMeta(meta);
			// if inv full and not CREATIVE -> drop bucket
			if (p.getInventory().firstEmpty() == -1 && p.getGameMode() != GameMode.CREATIVE) {
				p.getWorld().dropItem(p.getLocation(), item); // or 'dropItemNaturallly'
			} else {
				p.getInventory().addItem(item);
			}
			p.playSound(p.getLocation(), Sound.ENTITY_COW_MILK, 0.65F, 0.75F);
			// play the sound for milked player too
			t.playSound(p.getLocation(), Sound.ENTITY_COW_MILK, 0.35F, 0.75F);
			Main.incrPlayersMilked();
		}
		return;
	}

	@EventHandler
	public void onPotionDrink(PlayerItemConsumeEvent event) {
		Player p = event.getPlayer();
		ItemStack item = event.getItem();

		if (item.getType() == Material.MILK_BUCKET && item.hasItemMeta() && item.getItemMeta().hasLore()) {
			List<String> lore = item.getItemMeta().getLore();
			// check if its our bucket
			if (lore.get(0).contains("Mmmmh, so tasty")) {
				if (enableEffect) {
					// schedule the potion effect to be added on next tick
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main, new Runnable() {

						@Override
						public void run() {
							p.addPotionEffect(PotionEffectType.CONFUSION.createEffect(10 * 20, 2));
						}
					}, 1L);
				}

				// show "milk" particles in players face
				Vector direction = p.getEyeLocation().getDirection().normalize();
				direction = direction.multiply(0.25);
				Location pos = p.getEyeLocation().add(direction);
				p.spawnParticle(Particle.SNOWBALL, pos, 12);
			}
		}
		return;
	}

}