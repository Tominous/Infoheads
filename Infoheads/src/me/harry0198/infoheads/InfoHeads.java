package me.harry0198.infoheads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.harry0198.infoheads.commands.general.conversations.InfoHeadsConversationPrefix;
import me.harry0198.infoheads.commands.general.conversations.NamePrompt;
import me.harry0198.infoheads.inventorys.Inventory;
import me.harry0198.infoheads.listeners.EntityListeners;
import net.milkbowl.vault.permission.Permission;

public class InfoHeads extends JavaPlugin implements CommandExecutor, ConversationAbandonedListener {

	// Creation process Lists & Maps
	public Map<Player, String> name = new HashMap<>();
	public List<Player> namedComplete = new ArrayList<>();

	// Data Storage lists & Maps
	public List<String> infoheads = new ArrayList<>();
	public Map<String, Integer> x = new HashMap<>();
	public Map<String, Integer> y = new HashMap<>();
	public Map<String, Integer> z = new HashMap<>();
	public Map<String, String> messages = new HashMap<>();
	public Map<String, String> commands = new HashMap<>();
	
	// Inventory Storage
	public Map<UUID, ItemStack[]> items = new HashMap<UUID, ItemStack[]>();
	public Map<UUID, ItemStack[]> armor = new HashMap<UUID, ItemStack[]>();
	
	public static String version;
	

	// Vault
	private static Permission perms = null;

	// Conversations
	private ConversationFactory conversationFactory;

	@Override
	public void onEnable() {

		setupPermissions();

		getConfig().options().copyDefaults();
		saveDefaultConfig();

		getServer().getPluginManager().registerEvents(new EntityListeners(this), this);

		infoHeadsData();
		getCommand("infoheads").setExecutor(this);
		
	}

	/**
	 * Stores the value in a list to prevent constant checking from config file.
	 * (More efficient)
	 */

	public void infoHeadsData() {
		infoheads.clear();

		// Add names to a List
		for (String s : getConfig().getStringList("Names")) {
			infoheads.add(s);
		}
		// Add names' data to a map
		for (String s : infoheads) {
			x.put(s, getConfig().getInt(s + ".x"));
			y.put(s, getConfig().getInt(s + ".y"));
			z.put(s, getConfig().getInt(s + ".z"));
			messages.put(s, getConfig().getString(s + ".message"));
			commands.put(s, getConfig().getString(s + ".command"));
		}

	}

	/**
	 * Vault API Hook
	 * 
	 * @return perms
	 */
	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public static Permission getPermissions() {
		return perms;
	}

	/**
	 * Command > This section is built using the Conversation API
	 * This is good practice compared to AsyncChatEvent as this event
	 * can conflict with chat management plugins!
	 * 
	 * It is also much easier imo :)
	 */

	public InfoHeads() {
        this.conversationFactory = new ConversationFactory(this)
                .withModality(true)
                .withPrefix(new InfoHeadsConversationPrefix())
                .withFirstPrompt(new NamePrompt(this))
                .withEscapeSequence("/quit")
                .withTimeout(60)
                .thatExcludesNonPlayersWithMessage("Console is not supported by this command")
                .addConversationAbandonedListener(this);
    }
	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		if (sender instanceof Conversable) {
			// add inventory storage + etc
			Inventory iv = new Inventory(this);
			iv.storeAndClearInventory((Player) sender);
			iv.infoHeadsInventory((Player) sender);
			
			conversationFactory.buildConversation((Conversable) sender).begin();
			return true;
		} else {
			return false;
		}
	}

	public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
		if (abandonedEvent.gracefulExit()) {
		}
	}
	
	

}
