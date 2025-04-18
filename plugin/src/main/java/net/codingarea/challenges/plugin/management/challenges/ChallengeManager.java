package net.codingarea.challenges.plugin.management.challenges;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.config.FileDocument;
import net.anweisen.utilities.common.config.document.GsonDocument;
import net.anweisen.utilities.common.config.document.wrapper.FileDocumentWrapper;
import net.anweisen.utilities.database.exceptions.DatabaseException;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.CustomChallenge;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.management.challenges.entities.GamestateSaveable;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public final class ChallengeManager {

	private final List<IChallenge> challenges = new LinkedList<>();
	private final List<GamestateSaveable> additionalSaver = new LinkedList<>();

	private IGoal currentGoal;

	@Nonnull
	public List<IChallenge> getChallenges() {
		return Collections.unmodifiableList(challenges);
	}

	public void registerGameStateSaver(@Nonnull GamestateSaveable savable) {
		additionalSaver.add(savable);
	}

	public void register(@Nonnull IChallenge challenge) {
		if (!challenge.getType().isUsable()) throw new IllegalArgumentException("Invalid MenuType");
		challenges.add(challenge);
	}

	public void unregister(@Nonnull IChallenge challenge) {
		challenges.remove(challenge);
	}

	public void unregisterIf(@Nonnull Predicate<IChallenge> predicate) {
		challenges.removeIf(predicate);
	}

	public void shutdownChallenges() {
		for (IChallenge challenge : challenges) {
			try {
				challenge.handleShutdown();
			} catch (Exception ex) {
				Logger.error("Could not handle shutdown for {}", challenge.getClass().getSimpleName(), ex);
			}
		}
	}

	public void clearChallengeCache() {
		challenges.clear();
		currentGoal = null;
	}

	public void restoreDefaults() {
		Logger.debug("Restoring default settings..");
		Challenges.getInstance().getCustomChallengesLoader().resetChallenges();
		for (IChallenge challenge : challenges) {
			try {
				challenge.restoreDefaults();
			} catch (Exception ex) {
				Logger.error("Could not restore defaults for {}", challenge.getClass().getSimpleName(), ex);
			}
		}
	}

	public void saveSettings(@Nonnull Player player) throws DatabaseException {
		Document document = new GsonDocument();
		saveSettingsInto(document);
		Challenges.getInstance().getDatabaseManager().getDatabase()
				.insertOrUpdate("challenges")
				.where("uuid", player.getUniqueId())
				.set("config", document)
				.execute();
	}

	public void saveCustomChallenges(@Nonnull Player player) throws DatabaseException {
		Document document = new GsonDocument();
		saveCustomChallengesInto(document);
		Challenges.getInstance().getDatabaseManager().getDatabase()
				.insertOrUpdate("challenges")
				.where("uuid", player.getUniqueId())
				.set("custom_challenges", document)
				.execute();
	}

	public void enable() {
		loadGamestate(Challenges.getInstance().getConfigManager().getGamestateConfig().readonly());
		loadSettings(Challenges.getInstance().getConfigManager().getSettingsConfig().readonly());
		loadCustomChallenges(Challenges.getInstance().getConfigManager().getCustomChallengesConfig().readonly());
	}

	public synchronized void loadSettings(@Nonnull Document config) {

		for (IChallenge challenge : challenges) {
			if (challenge instanceof CustomChallenge) continue;

			String name = challenge.getUniqueName();
			if (!config.contains(name)) continue;
			try {
				Document document = config.getDocument(name);
				challenge.loadSettings(document);
			} catch (Exception ex) {
				Logger.error("Could not load setting for challenge {}", challenge.getClass().getSimpleName(), ex);
			}
		}
	}

	public synchronized void loadGamestate(@Nonnull Document config) {
		LinkedList<GamestateSaveable> list = new LinkedList<>(challenges);
		list.addAll(additionalSaver);
		for (GamestateSaveable challenge : list) {
			String name = challenge.getUniqueGamestateName();
			if (!config.contains(name)) continue;
			try {
				Document document = config.getDocument(name);
				challenge.loadGameState(document);

				if (challenge instanceof AbstractChallenge) {
					AbstractChallenge abstractChallenge = (AbstractChallenge) challenge;
					if (abstractChallenge.getBossbar().isShown()) {
						abstractChallenge.getBossbar().update();
					}
					if (abstractChallenge.getScoreboard().isShown()) {
						abstractChallenge.getScoreboard().update();
					}
				}

			} catch (Exception ex) {
				Logger.error("Could not load gamestate for {}", challenge.getClass().getSimpleName(), ex);
			}
		}
	}

	public synchronized void loadCustomChallenges(@Nonnull Document config) {
		Challenges.getInstance().getCustomChallengesLoader().loadCustomChallengesFrom(config);
	}

	public void resetGamestate() {
		LinkedList<GamestateSaveable> list = new LinkedList<>(challenges);
		list.addAll(additionalSaver);
		for (GamestateSaveable challenge : list) {
			try {
				challenge.loadGameState(Document.empty());

				if (challenge instanceof AbstractChallenge) {
					AbstractChallenge abstractChallenge = (AbstractChallenge) challenge;
					if (abstractChallenge.getBossbar().isShown()) {
						abstractChallenge.getBossbar().update();
					}
					if (abstractChallenge.getScoreboard().isShown()) {
						abstractChallenge.getScoreboard().update();
					}
				}
			} catch (Exception ex) {
				Logger.error("Could not load gamestate for {}", challenge.getClass().getSimpleName(), ex);
			}
		}
	}

	public void saveGameStateInto(@Nonnull Document config) {
		LinkedList<GamestateSaveable> list = new LinkedList<>(challenges);
		list.addAll(additionalSaver);
		for (GamestateSaveable challenge : list) {
			try {
				Document document = config.getDocument(challenge.getUniqueGamestateName());
				challenge.writeGameState(document);
			} catch (Exception ex) {
				Logger.error("Could not write gamestate of {}", challenge.getClass().getSimpleName(), ex);
			}
		}
	}

	public synchronized void saveGamestate(boolean async) {
		FileDocument config = Challenges.getInstance().getConfigManager().getGamestateConfig();
		saveGameStateInto(config);
		config.save(async);
	}

	public void saveSettingsInto(@Nonnull Document config) {
		for (IChallenge challenge : challenges) {
			if (challenge instanceof CustomChallenge) continue;
			try {
				Document document = config.getDocument(challenge.getUniqueGamestateName());
				challenge.writeSettings(document);
			} catch (Exception ex) {
				Logger.error("Could not write settings of {}", challenge.getClass().getSimpleName(), ex);
			}
		}
	}

	public synchronized void saveLocalSettings(boolean async) {
		FileDocument config = Challenges.getInstance().getConfigManager().getSettingsConfig();
		saveSettingsInto(config);
		config.save(async);
	}

	public void saveCustomChallengesInto(@Nonnull Document config) {
		Collection<CustomChallenge> customChallenges = Challenges.getInstance().getCustomChallengesLoader()
				.getCustomChallenges().values();

		for (IChallenge challenge : customChallenges) {
			try {
				Document document = config.getDocument(challenge.getUniqueName());
				challenge.writeSettings(document);
				challenge.writeGameState(document);
			} catch (Exception ex) {
				Logger.error("Could not write settings of {}", challenge.getClass().getSimpleName(), ex);
			}
		}
	}

	public synchronized void saveLocalCustomChallenges(boolean async) {
		FileDocument config = new FileDocumentWrapper(Challenges.getInstance().getDataFile("internal/custom_challenges.json"), new GsonDocument());
		saveCustomChallengesInto(config);
		config.save(async);
	}


	@Nullable
	public IGoal getCurrentGoal() {
		return currentGoal;
	}

	public void setCurrentGoal(@Nullable IGoal goal) {

		IGoal oldGoal = currentGoal;
		currentGoal = goal;

		if (oldGoal != null)
			oldGoal.setEnabled(false);

		if (goal != null)
			goal.setEnabled(true);

	}

}
