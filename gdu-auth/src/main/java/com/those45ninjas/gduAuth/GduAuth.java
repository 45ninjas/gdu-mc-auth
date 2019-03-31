package com.those45ninjas.gduAuth;
import org.bukkit.plugin.java.JavaPlugin;

public class GduAuth extends JavaPlugin
{
	MixerFunctions mixer;
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new GduListener(this), this);
		mixer = new MixerFunctions(this);
		
		if(!mixer.ConfigValid())
		{
			getLogger().severe("Mixer client_ID is incorrect");
			getServer().getPluginManager().disablePlugin(this);
		}
	}
	@Override
	public void onDisable() {
		
	}
}
