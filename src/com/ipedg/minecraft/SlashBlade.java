package com.ipedg.minecraft;

import net.minecraft.server.v1_7_R4.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;


public class SlashBlade extends JavaPlugin implements Listener {


    @Override
    public void onEnable() {
        File config = new File(getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
        }
        saveDefaultConfig();

        new BukkitRunnable(){

            @Override
            public void run() {
                for (Player player: getServer().getOnlinePlayers()){
                    for (int a=0;a<=35;a++){
                        ItemStack item = player.getInventory().getItem(a);
                        if (item!=null&&item.getType()!= Material.AIR){
                            if (item.containsEnchantment(Enchantment.ARROW_DAMAGE)&&!player.hasPermission("slashblade.power")){
                                item.removeEnchantment(Enchantment.ARROW_DAMAGE);
                                player.sendMessage(getConfig().getString("Msg"));
                            }
                            int i = ItemGetNbtTagKey(item);
                            if (i!=0){
                                int number = compareLimitLevel(player);
                                if (i>number){
                                    item = ChangeItemStackNbtDate(item, number);
                                }
                            }
                            player.setItemInHand(item);
                        }

                    }
                    player.updateInventory();
                }
            }
        }.runTaskTimer(this,1L,20L*getConfig().getInt("time"));
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        super.onEnable();
    }

    @EventHandler
    public void PlayerWapper(PlayerInteractEvent event){
        ItemStack item = event.getPlayer().getItemInHand();
        if (item!=null&&item.getType()!= Material.AIR){
            if (item.containsEnchantment(Enchantment.ARROW_DAMAGE)&&!event.getPlayer().hasPermission("slashblade.power")){
                item.removeEnchantment(Enchantment.ARROW_DAMAGE);
                event.getPlayer().sendMessage(getConfig().getString("Msg"));
            }
            int i = ItemGetNbtTagKey(item);
            if (i!=0){
                int number = compareLimitLevel(event.getPlayer());
                if (i>number){
                    item = ChangeItemStackNbtDate(item, number);
                }
            }
            event.getPlayer().setItemInHand(item);
        }
    }



    @EventHandler
    public void PlayerClickInventory(InventoryClickEvent event){
        int slot = event.getSlot();
        ItemStack item = event.getInventory().getItem(slot);
        if (item!=null&&item.getType()!= Material.AIR){
            if (item.containsEnchantment(Enchantment.ARROW_DAMAGE)&&!event.getWhoClicked().hasPermission("slashblade.power")){
                item.removeEnchantment(Enchantment.ARROW_DAMAGE);
                ((Player)event.getWhoClicked()).sendMessage(getConfig().getString("Msg"));
            }
            int i = ItemGetNbtTagKey(item);
            if (i!=0){
                int number = compareLimitLevel((Player) event.getWhoClicked());
                if (i>number){
                    item = ChangeItemStackNbtDate(item, number);
                }
            }
            event.getInventory().setItem(slot,item);
        }
    }

    public static ItemStack ChangeItemStackNbtDate(ItemStack item, int value)
    {
        net.minecraft.server.v1_7_R4.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nmsItem.getTag();
        compound.setInt("RepairCounter", value);
        nmsItem.setTag(compound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }


    public static int ItemGetNbtTagKey(ItemStack item)
    {
        net.minecraft.server.v1_7_R4.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem.hasTag())
        {
            NBTTagCompound compound = nmsItem.getTag();
            if (compound.hasKey("RepairCounter")) {
                return compound.getInt("RepairCounter");
            }
        }
        //啊
        return 0;
    }

    private int compareLimitLevel(Player player)
    {
        int bMaxLevel = getConfig().getInt("MaxRepairCounter");
        int defaultRepair = getConfig().getInt("defaultMaxRepairCounter");
        for (int i = 0; i < bMaxLevel; i++) {
            if (player.hasPermission("slashblade." + i))
            {
                return i;
            }
        }
        return defaultRepair;
    }

}
