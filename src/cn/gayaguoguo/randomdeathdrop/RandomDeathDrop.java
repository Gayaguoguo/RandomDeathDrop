package cn.gayaguoguo.randomdeathdrop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomDeathDrop extends JavaPlugin implements Listener {
	
	List<Integer> amountList = new ArrayList<>();
	
	String msg;
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		load();
	}
	public void load(){
		saveDefaultConfig();
		reloadConfig();
		amountList.clear();
		msg = getConfig().getString("msg").replace("&", "§");
		int min = getConfig().getInt("min");
		int max = getConfig().getInt("max");
		for (int i = min ; i <= max ; ++i){
			amountList.add(i);
		}
	}
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender.isOp()){
			load();
			sender.sendMessage("§e§l[RandomDeathDrop] §a插件重载完成。");
		}
		return false;
		
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player player = e.getEntity();
		Inventory inv = player.getInventory();
		
		// 获取本次死亡掉落物品数量
		Integer dropamount = amountList.get(new Random().nextInt(amountList.size()));
		
		// 获取背包中非空格的物品的位置
		List<Integer> itemPlaceList = new ArrayList<>();
		for (int i = 0; i < inv.getSize(); ++i){
			ItemStack item = inv.getItem(i);
			if (item == null || item.getType() == Material.AIR){
				continue;
			}
			itemPlaceList.add(i);
		}
		
		// 非空物品数量过少，全部掉落
		if (itemPlaceList.size() <= dropamount){
			for (Integer i : itemPlaceList){
				player.getWorld().dropItem(player.getLocation(), inv.getItem(i));
				inv.setItem(i, null);
			}
			dropamount = itemPlaceList.size();
			player.sendMessage(msg.replace("%amout%", dropamount.toString()));
		}else {
			// 非空数量大于掉落数量，随机抽取掉落
			player.sendMessage(msg.replace("%amout%", dropamount.toString()));
			while (dropamount > 0) {
				dropamount = dropamount - 1;
				Integer i = itemPlaceList.get(new Random().nextInt(itemPlaceList.size()));
				player.getWorld().dropItem(player.getLocation(), inv.getItem(i));
				inv.setItem(i, null);
			}
		}
	}
}
