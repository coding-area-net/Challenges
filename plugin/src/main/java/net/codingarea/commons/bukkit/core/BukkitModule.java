package net.codingarea.commons.bukkit.core;

import com.google.common.base.Charsets;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import net.codingarea.commons.bukkit.utils.menu.MenuPositionListener;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.commons.bukkit.utils.wrapper.ActionListener;
import net.codingarea.commons.bukkit.utils.wrapper.SimpleEventExecutor;
import net.codingarea.commons.common.annotations.DeprecatedSince;
import net.codingarea.commons.common.annotations.ReplaceWith;
import net.codingarea.commons.common.collection.NamedThreadFactory;
import net.codingarea.commons.common.collection.WrappedException;
import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.FileDocument;
import net.codingarea.commons.common.config.document.YamlDocument;
import net.codingarea.commons.common.logging.ILogger;
import net.codingarea.commons.common.logging.internal.BukkitLoggerWrapper;
import net.codingarea.commons.common.logging.lib.JavaILogger;
import net.codingarea.commons.common.version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class BukkitModule extends JavaPlugin {

	private static volatile BukkitModule firstInstance;
	private static boolean setFirstInstance = true;
	private static boolean wasShutdown;

	private final Map<String, CommandExecutor> commands = new HashMap<>();
	private final List<Listener> listeners = new ArrayList<>();
	private final SimpleConfigManager configManager = new SimpleConfigManager(this);

	private JavaILogger logger;
	private ExecutorService executorService;
	private Document config, pluginConfig;
	private Version version;
	private boolean devMode;
	private boolean firstInstall;
	private boolean isReloaded;
	private boolean isLoaded;

	private boolean requirementsMet = true;

	@Override
	public final void onLoad() {
		isLoaded = true;

		if (!requirementsMet || !(requirementsMet = new RequirementsChecker(this).checkBoolean(getPluginDocument().getDocument("require"))))
			return;

		if (setFirstInstance || firstInstance == null) {
			setFirstInstance(this);
		}

		ILogger.setConstantFactory(this.getILogger());
		trySaveDefaultConfig();
		if (wasShutdown) isReloaded = true;
		if (firstInstall = !getDataFolder().exists()) {
			getILogger().info("Detected first install!");
		}
		if (devMode = getConfigDocument().getBoolean("dev-mode") || getConfigDocument().getBoolean("dev-mode.enabled")) {
			getILogger().setLevel(Level.ALL);
			getILogger().debug("Devmode is enabled: Showing debug messages. This can be disabled in the plugin.yml ('dev-mode')");
		} else {
			getILogger().setLevel(Level.INFO);
		}

		injectInstance();

		try {
			handleLoad();
		} catch (Exception ex) {
			throw new WrappedException(ex);
		}
	}

	@Override
	public final void onEnable() {
		if (!requirementsMet) return;

		commands.forEach((name, executor) -> registerCommand0(executor, name));
		listeners.forEach(this::registerListener);


		try {
			handleEnable();
		} catch (Exception ex) {
			throw new WrappedException(ex);
		}
	}

	@Override
	public final void onDisable() {
		Throwable error = null;
		try {
			handleDisable();
		} catch (Throwable ex) {
			error = ex;
		}

		setFirstInstance = true;
		wasShutdown = true;
		isLoaded = false;
		commands.clear();
		listeners.clear();

		if (executorService != null)
			executorService.shutdown();

		for (Player player : Bukkit.getOnlinePlayers()) {
			InventoryView view = player.getOpenInventory();
			Inventory inventory = view.getTopInventory();
			if (inventory.getHolder() == MenuPosition.HOLDER)
				view.close();
		}

		if (error != null)
			throw new WrappedException(error);
	}

	protected void handleLoad() throws Exception {}
	protected void handleEnable() throws Exception {}
	protected void handleDisable() throws Exception {}

	public boolean isDevMode() {
		return devMode;
	}

	public final boolean isFirstInstall() {
		return firstInstall;
	}

	public final boolean isReloaded() {
		return isReloaded;
	}

	public final boolean isLoaded() {
		return isLoaded;
	}

	public final boolean isFirstInstance() {
		return firstInstance == this;
	}

	@Nonnull
	public JavaILogger getILogger() {
		return logger != null ? logger : (logger = new BukkitLoggerWrapper(super.getLogger()));
	}

	@Nonnull
	public Document getConfigDocument() {
		checkLoaded();
		return config != null ? config : (config = new YamlDocument(super.getConfig()));
	}

	@Override
	public void reloadConfig() {
		config = null;
		super.reloadConfig();
	}

	/**
	 * @return the plugin configuration (plugin.yml) as document
	 */
	@Nonnull
	public Document getPluginDocument() {
		return pluginConfig != null ? pluginConfig :
			  (pluginConfig = new YamlDocument(YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("plugin.yml"), Charsets.UTF_8))));
	}

	@Nonnull
	public FileDocument getConfig(@Nonnull String filename) {
		return configManager.getDocument(filename);
	}

	@Nonnull
	public Version getVersion() {
		return version != null ? version : (version = Version.parse(getDescription().getVersion()));
	}

	@Nonnull
	@Deprecated
	@DeprecatedSince("1.3.0")
	@ReplaceWith("MinecraftVersion.current()")
	public MinecraftVersion getServerVersion() {
		return MinecraftVersion.current();
	}

	@Nonnull
	@Deprecated
	@DeprecatedSince("1.3.0")
	@ReplaceWith("MinecraftVersion.currentExact()")
	public Version getServerVersionExact() {
		return MinecraftVersion.currentExact();
	}

	@Nonnull
	@Override
	@Deprecated
	@ReplaceWith("getConfigDocument()")
	public FileConfiguration getConfig() {
		return super.getConfig();
	}

	@Override
	@Deprecated
	public void saveConfig() {
		super.saveConfig();
	}

	public void setRequirementsFailed() {
		this.requirementsMet = false;
	}

	public final <T extends CommandExecutor & Listener> void registerListenerCommand(@Nonnull T listenerAndExecutor, @Nonnull String... names) {
		registerCommand(listenerAndExecutor, names);
		registerListener(listenerAndExecutor);
	}

	public final void registerCommand(@Nonnull CommandExecutor executor, @Nonnull String... names) {
		for (String name : names) {
			if (isEnabled()) {
				registerCommand0(executor, name);
			} else {
				commands.put(name, executor);
			}
		}
	}

	private void registerCommand0(@Nonnull CommandExecutor executor, @Nonnull String name) {
		PluginCommand command = getCommand(name);
		if (command == null) {
			getILogger().warn("Tried to register invalid command '{}'", name);
		} else {
			command.setExecutor(executor);
		}
	}

	public final void registerListener(@Nonnull Listener... listeners) {
		if (isEnabled()) {
			for (Listener listener : listeners) {
				registerListener0(listener);
			}
		} else {
			this.listeners.addAll(Arrays.asList(listeners));
		}
	}

	private void registerListener0(@Nonnull Listener listener) {
		if (listener instanceof ActionListener) {
			ActionListener<?> actionListener = (ActionListener<?>) listener;
			getServer().getPluginManager().registerEvent(
					actionListener.getClassOfEvent(), actionListener, actionListener.getPriority(),
					new SimpleEventExecutor(actionListener.getClassOfEvent(), actionListener.getListener()), this, actionListener.isIgnoreCancelled()
			);
		} else {
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}

	public final <E extends Event> void on(@Nonnull Class<E> classOfEvent, @Nonnull Consumer<? super E> action) {
		on(classOfEvent, EventPriority.NORMAL, action);
	}

	public final <E extends Event> void on(@Nonnull Class<E> classOfEvent, @Nonnull EventPriority priority, @Nonnull Consumer<? super E> action) {
		on(classOfEvent, priority, false, action);
	}

	public final <E extends Event> void on(@Nonnull Class<E> classOfEvent, @Nonnull EventPriority priority, boolean ignoreCancelled, @Nonnull Consumer<? super E> action) {
		registerListener(new ActionListener<>(classOfEvent, action, priority, ignoreCancelled));
	}

	public final void disablePlugin() {
		getServer().getPluginManager().disablePlugin(this);
	}

	@Nonnull
	public final File getDataFile(@Nonnull String filename) {
		return new File(getDataFolder(), filename);
	}

	@Nonnull
	public final File getDataFile(@Nonnull String subfolder, @Nonnull String filename) {
		return new File(getDataFile(subfolder), filename);
	}

	@Nonnull
	public ExecutorService getExecutor() {
		return executorService != null ? executorService : (executorService = Executors.newCachedThreadPool(new NamedThreadFactory(threadId -> String.format("%s-Task-%s", this.getName(), threadId))));
	}

	public void runAsync(@Nonnull Runnable task) {
		getExecutor().submit(task);
	}

	public final void checkLoaded() {
		if (!isLoaded())
			throw new IllegalStateException("Plugin (" + getName() + ") is not loaded yet");
	}

	public final void checkEnabled() {
		if (!isEnabled())
			throw new IllegalStateException("Plugin (" + getName() + ") is not enabled yet");
	}

	private void registerAsFirstInstance() {
		getILogger().info(getName() + " was loaded as the first BukkitModule");
		registerListener(
			new MenuPositionListener()
		);
		getILogger().info("Detected server version {} -> {}", getServerVersionExact(), getServerVersion());
	}

	private void trySaveDefaultConfig() {
		try {
			saveDefaultConfig();
		} catch (IllegalArgumentException ex) {
			// No default config exists
		}
	}

	private void injectInstance() {
		try {
			Field instanceField = this.getClass().getDeclaredField("instance");
			instanceField.setAccessible(true);
			instanceField.set(null, this);
		} catch (Throwable ex) {
		}
	}

	@Nonnull
	public static BukkitModule getFirstInstance() {
		if (firstInstance == null) {
			JavaPlugin provider = JavaPlugin.getProvidingPlugin(BukkitModule.class);
			if (!(provider instanceof BukkitModule)) throw new IllegalStateException("No BukkitModule was initialized yet & BukkitModule class was not loaded by a BukkitModule");
			firstInstance = (BukkitModule) provider;
		}

		return firstInstance;
	}

	private static synchronized void setFirstInstance(@Nonnull BukkitModule module) {
		setFirstInstance = false;
		firstInstance = module;
		module.registerAsFirstInstance();
	}

	@Nonnull
	public static BukkitModule getProvidingModule(@Nonnull Class<?> clazz) {
		JavaPlugin provider = JavaPlugin.getProvidingPlugin(clazz);
		if (!(provider instanceof BukkitModule)) throw new IllegalStateException(clazz.getName() + " is not provided by a BukkitModule");
		return (BukkitModule) provider;
	}

}
