package net.codingarea.challenges.plugin.challenges.implementation.challenge.quiz;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.anweisen.utilities.common.collection.IRandom;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.quiz.QuizChallenge.IQuestion.Question;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.spigot.events.PlayerJumpEvent;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import net.codingarea.challenges.plugin.utils.misc.TriFunction;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.0
 */
public class QuizChallenge extends TimedChallenge implements PlayerCommand, TabCompleter {

	private static QuizChallenge instance;

	private final Prefix prefix = Prefix.forName("quiz", "§6Quiz");
	private final IRandom random = IRandom.create();

	private IQuestion currentQuestion;
	private Player currentQuestionedPlayer;
	private int timeLeft;

	public QuizChallenge() {
		super(MenuType.CHALLENGES, 1, 20, 5);
		instance = this;
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.BARRIER);
	}

	@Override
	protected void onEnable() {
		bossbar.setContent((bossbar, player) -> {
			if (currentQuestion == null) {
				bossbar.setColor(BarColor.YELLOW);
				bossbar.setTitle(Message.forName("bossbar-quiz-waiting").asString());
				return;
			}
			String time = "§e" + timeLeft + " §7" + (timeLeft == 1 ? Message.forName("second").asString() : Message.forName("seconds").asString());
			if (currentQuestionedPlayer != player) {
				bossbar.setColor(BarColor.GREEN);
				bossbar.setTitle(Message.forName("bossbar-quiz-question-other").asString(time, NameHelper.getName(currentQuestionedPlayer)));
				return;
			}
			bossbar.setColor(BarColor.RED);
			bossbar.setTitle(Message.forName("bossbar-quiz-question").asString(time));
		});
		bossbar.show();
	}

	@Override
	protected void onDisable() {
		bossbar.hide();
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return ChallengeHelper.getTimeRangeSettingsDescription(this, 60 * 3, 60);
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeSecondsRangeValueChangeTitle(this, getValue() * 60 * 3 - 60, getValue() * 60 * 3 + 60);
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return random.around(getValue() * 60 * 3, 60);
	}
	
	private void broadcastMessage(@Nonnull String questionedPlayerMessage, @Nonnull String othersMessage, Object... args) {
		broadcast(player1 -> {
			if (currentQuestionedPlayer == player1) {
				Message.forName(questionedPlayerMessage).send(player1, prefix, args);
			} else {
				Message.forName(othersMessage).send(player1, prefix, NameHelper.getName(currentQuestionedPlayer));
			}
		});
	}

	@Override
	protected void onTimeActivation() {

		SavedStatistic[] statistics = SavedStatistic.values();
		SavedStatistic statistic = statistics[random.nextInt(statistics.length)];

		IQuestion[] questions = statistic.getQuestions();
		currentQuestion = questions[random.nextInt(questions.length)];

		List<Player> ingamePlayers = ChallengeHelper.getIngamePlayers();
		currentQuestionedPlayer = ingamePlayers.get(random.nextInt(ingamePlayers.size()));

		List<String> answers = currentQuestion.createAnswers(statistic, currentQuestionedPlayer);
		if (answers.isEmpty()) {
			onTimeActivation();
			return;
		}
		Message.forName("quiz-how-to").send(currentQuestionedPlayer, Prefix.CHALLENGES);
		timeLeft = 60;

		bossbar.update();
	}

	@ScheduledTask(ticks = 20, async = false)
	public void onSecond() {
		if (currentQuestion == null) return;
		if (!currentQuestionedPlayer.isOnline() || ignorePlayer(currentQuestionedPlayer)) {
			Message.forName("quiz-cancel").broadcast(prefix);
			cancelQuestion();
		}

		timeLeft--;

		if (timeLeft <= 0) {
			Message.forName("quiz-time-up").broadcast(prefix);
			cancelQuestion();
		}

		bossbar.update();
	}

	private void cancelQuestion() {
		restartTimer();

		List<String> rightAnswers = currentQuestion.getRightAnswers();
		Message.forName("quiz-right-answer-was" + (rightAnswers.size() != 1 ? "-multiple" : "")).send(currentQuestionedPlayer, prefix, StringUtils.getArrayAsString(rightAnswers.toArray(new String[0]), "§7, §e"));
		SoundSample.BREAK.play(currentQuestionedPlayer);
		
		currentQuestion = null;
		AttributeInstance attribute = currentQuestionedPlayer.getAttribute(Attribute.MAX_HEALTH);

		if (attribute.getBaseValue() == 2) {
			kill(currentQuestionedPlayer);
			attribute.setBaseValue(20);
			return;
		}

		attribute.setBaseValue(attribute.getBaseValue() - 2);
	}

	@Override
	public void onCommand(@Nonnull Player player, @Nonnull String[] args) throws Exception {

		if (currentQuestion == null || currentQuestionedPlayer != player) {
			// TODO: REMOVE RESTART TIMER HERE
			restartTimer(3);
			SoundSample.BASS_OFF.play(player);
			return;
		}

		if (args.length == 0) {
			Message.forName("syntax").send(player, prefix, "guess <answer>");
			return;
		}

		String answer = StringUtils.getArrayAsString(args, " ");
		if (!currentQuestion.isRightAnswer(answer)) {
			broadcastMessage("quiz-wrong-answer", "quiz-wrong-answer-other", answer);
			cancelQuestion();
			bossbar.update();
			return;
		}
		
		broadcastMessage("quiz-right-answer", "quiz-right-answer-other", answer);
		SoundSample.LEVEL_UP.play(player);
		currentQuestion = null;
		bossbar.update();
		restartTimer();
	}

	@Override
	public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
		return new ArrayList<>();
	}

	interface IQuestion {

		List<String> createAnswers(@Nonnull SavedStatistic statistic, @Nonnull Player player);
		List<String> getRightAnswers();
		boolean isRightAnswer(@Nonnull String answer);

		static void sendMessage(@Nonnull String question, @Nonnull List<String> answers) {
			Player player = instance.currentQuestionedPlayer;

			player.sendMessage(" ");
			player.sendMessage("§e" + question);
			player.sendMessage(" ");

			for (String answer : answers) {
				player.sendMessage("§8-> §e" + answer);
			}

			player.sendMessage(" ");
		}

		class Question implements IQuestion {

			private final TriFunction<SavedStatistic, Player, List<String>, List<String>> questionCreator;
			private List<String> currentRightAnswers;

			public Question(TriFunction<SavedStatistic, Player, List<String>, List<String>> questionCreator) {
				this.questionCreator = questionCreator;
			}

			@Override
			public List<String> createAnswers(@Nonnull SavedStatistic statistic, @Nonnull Player player) {
				ArrayList<String> answers = new ArrayList<>();
				currentRightAnswers = questionCreator.apply(statistic, player, answers);
				return answers;
			}

			@Override
			public boolean isRightAnswer(@Nonnull String answer) {
				if (currentRightAnswers == null) return false;

				for (String currentRightAnswer : currentRightAnswers) {
					if (currentRightAnswer.equalsIgnoreCase(answer)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public List<String> getRightAnswers() {
				return currentRightAnswers;
			}

		}

	}

	private interface SavedStatistic {

		IntegerStatistic BLOCKS_MOVED = new IntegerStatistic("blocks-traveled", "traveled", "far", "be");
		IntegerStatistic JUMPED = new IntegerStatistic("jumped", "jumped", "often", "be");
		IntegerStatistic SNEAKED = new IntegerStatistic("sneaked", "sneaked", "often", "be");
		DocumentCountStatistic MOB_KILLED = new DocumentCountStatistic("mob-killed", "killed", "often", "have");
		DocumentCountStatistic MOB_DAMAGED = new DocumentCountStatistic("mobs-damaged", "damaged", "damage", "have");
		DocumentCountStatistic BLOCKS_PLACED = new DocumentCountStatistic("blocks-placed", "placed", "often", "have");
		DocumentCountStatistic BLOCKS_DESTROYED = new DocumentCountStatistic("blocks-destroyed", "broken", "often", "have");
		DocumentCountStatistic ITEMS_DROPPED = new DocumentCountStatistic("items-dropped", "dropped", "often", "have");
		DocumentCountStatistic DAMAGE_TAKEN = new DocumentCountStatistic("damage-taken", "suffered", "damage-taken", "have");

		String getKey();
		String getVerb();
		String getQuestionVerb();
		String getQuestionSuffix();
		IQuestion[] getQuestions();

		static SavedStatistic[] values() {
			return new SavedStatistic[] { BLOCKS_MOVED, JUMPED, SNEAKED, MOB_DAMAGED, MOB_KILLED, BLOCKS_PLACED, BLOCKS_DESTROYED, ITEMS_DROPPED, DAMAGE_TAKEN };
		}

		default Message getVerbMessage() {
			return Message.forName("quiz-verb-" + getVerb());
		}

		class IntegerStatistic implements SavedStatistic {

			private final static Question INTEGER_QUESTION = new Question((statistic, player, answers) -> {
				IntegerStatistic integerStatistic = (IntegerStatistic) statistic;
				int rightAnswer = integerStatistic.getStatistic(player);

				for (int i = rightAnswer; i < rightAnswer + 4; i++) {
					answers.add(i + "");
				}

				IQuestion.sendMessage(Message.forName("quiz-question-" + statistic.getQuestionSuffix() + "-" + statistic.getQuestionVerb()).asString(statistic.getVerbMessage(), ""), answers);

				return Collections.singletonList(rightAnswer + "");
			});

			private final String key;
			private final String questionVerb;
			private final String questionSuffix;
			private final String verb;

			public IntegerStatistic(String key, String verbMessage, String questionSuffix, String questionVerb) {
				this.key = key;
				this.verb = verbMessage;
				this.questionSuffix = questionSuffix;
				this.questionVerb = questionVerb;
			}

			@Override
			public String getKey() {
				return key;
			}

			@Override
			public String getVerb() {
				return verb;
			}

			@Override
			public String getQuestionVerb() {
				return questionVerb;
			}

			@Override
			public String getQuestionSuffix() {
				return questionSuffix;
			}

			@Override
			public IQuestion[] getQuestions() {
				return new Question[] { INTEGER_QUESTION };
			}

			public void increaseStatistic(@Nonnull Player player) {
				increaseStatistic(player, 1);
			}

			public void increaseStatistic(@Nonnull Player player, int amount) {
				instance.getPlayerData(player).set(key, getStatistic(player) + amount);
			}

			public int getStatistic(@Nonnull Player player) {
				return instance.getPlayerData(player).getInt(key);
			}

		}

		abstract class DocumentStatistic implements SavedStatistic {

			private final String key;

			public DocumentStatistic(String key) {
				this.key = key;
			}

			@Override
			public String getKey() {
				return key;
			}

			public Document getDocument(@Nonnull Player player) {
				return instance.getPlayerData(player).getDocument(key);
			}

		}

		class DocumentCountStatistic extends DocumentStatistic {

			private final static Question CHOOSE_QUESTION = new Question((statistic, player, answers) -> {
				DocumentCountStatistic documentCountStatistic = (DocumentCountStatistic) statistic;
				Document document = documentCountStatistic.getDocument(player);

				Map<String, Double> entries = new HashMap<>();

				ArrayList<String> keysLeft = new ArrayList<>(document.keys());

				if (keysLeft.isEmpty() || keysLeft.size() == 1) return null;

				Collections.shuffle(keysLeft);
				for (byte i = 0; i < 4; i++) {
					if (keysLeft.isEmpty()) break;
					String key = keysLeft.remove(0);
					entries.put(key, documentCountStatistic.getStatistic(player, key));
				}

				answers.addAll(entries.keySet());

				double highestValue = -1;
				List<String> highestEntries = new ArrayList<>();

				for (Entry<String, Double> entry : entries.entrySet()) {
					if (highestValue == -1) {
						highestEntries.add(entry.getKey());
						highestValue = entry.getValue();
					} else if (highestValue == entry.getValue()) {
						highestEntries.add(entry.getKey());
					} else if (highestValue < entry.getValue()) {
						highestEntries.clear();
						highestEntries.add(entry.getKey());
						highestValue = entry.getValue();
					}
				}

				List<String> newAnswers = answers.stream().map(StringUtils::getEnumName).collect(Collectors.toList());
				answers.clear();
				answers.addAll(newAnswers);

				IQuestion.sendMessage(Message.forName("quiz-question-most").asString(statistic.getVerbMessage(), answers.size()), answers);

				highestEntries = highestEntries.stream().map(StringUtils::getEnumName).collect(Collectors.toList());

				return highestEntries;
			}),
			COUNT_QUESTIONS = new Question((statistic, player, answers) -> {
				DocumentCountStatistic documentCountStatistic = (DocumentCountStatistic) statistic;
				Document document = documentCountStatistic.getDocument(player);

				ArrayList<String> keys = new ArrayList<>(document.keys());

				if (keys.isEmpty()) {
					return null;
				}

				String key = keys.get(instance.random.nextInt(keys.size()));
				int rightAnswer = (int) documentCountStatistic.getStatistic(player, key);

				for (int i = rightAnswer; i < rightAnswer + 4; i++) {
					answers.add(i + "");
				}

				IQuestion.sendMessage(Message.forName("quiz-question-" + statistic.getQuestionSuffix() + "-" + statistic.getQuestionVerb()).asString(StringUtils.getEnumName(key), " " + statistic.getVerbMessage().asString()), answers);

				return new ArrayList<>(Collections.singleton(rightAnswer + ""));
			});

			private final String verb;
			private final String questionSuffix;
			private final String questionVerb;

			public DocumentCountStatistic(String key, String verbMessage, String questionSuffix, String questionVerb) {
				super(key);
				this.verb = verbMessage;
				this.questionSuffix = questionSuffix;
				this.questionVerb = questionVerb;
			}

			public void increaseStatistic(@Nonnull Player player, @Nonnull String key) {
				increaseStatistic(player, key, 1);
			}

			public void increaseStatistic(@Nonnull Player player, @Nonnull String key, double amount) {
				getDocument(player).set(key, getStatistic(player, key) + amount);
			}

			public double getStatistic(@Nonnull Player player, @Nonnull String key) {
				return getDocument(player).getDouble(key);
			}

			@Override
			public String getVerb() {
				return verb;
			}

			@Override
			public String getQuestionVerb() {
				return questionVerb;
			}

			@Override
			public String getQuestionSuffix() {
				return questionSuffix;
			}

			@Override
			public IQuestion[] getQuestions() {
				return new IQuestion[] { CHOOSE_QUESTION, COUNT_QUESTIONS };
			}

		}

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJump(@Nonnull PlayerJumpEvent event) {
		if (!shouldExecuteEffect()) return;
		if (AbstractChallenge.ignorePlayer(event.getPlayer())) return;
		SavedStatistic.JUMPED.increaseStatistic(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJump(@Nonnull PlayerToggleSneakEvent event) {
		if (!shouldExecuteEffect()) return;
		if (!event.isSneaking()) return;
		if (AbstractChallenge.ignorePlayer(event.getPlayer())) return;
		SavedStatistic.SNEAKED.increaseStatistic(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityKill(@Nonnull EntityDamageByEntityEvent event) {
		if (!shouldExecuteEffect()) return;

		if (!(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getDamager();
		if (ignorePlayer(player)) return;

		if (!(event.getEntity() instanceof LivingEntity)) return;

		SavedStatistic.MOB_DAMAGED.increaseStatistic(player, event.getEntity().getType().name(), ChallengeHelper.getFinalDamage(event));

		LivingEntity entity = (LivingEntity) event.getEntity();
		if (entity.getHealth() - ChallengeHelper.getFinalDamage(event) > 0) return;

		SavedStatistic.MOB_KILLED.increaseStatistic(player, event.getEntity().getType().name());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(@Nonnull BlockPlaceEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		SavedStatistic.BLOCKS_PLACED.increaseStatistic(event.getPlayer(), event.getBlockPlaced().getType().name());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockBreak(@Nonnull BlockBreakEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (BukkitReflectionUtils.isAir(event.getBlock().getType())) return;
		SavedStatistic.BLOCKS_DESTROYED.increaseStatistic(event.getPlayer(), event.getBlock().getType().name());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(@Nonnull PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getTo() == null) return;
		if (BlockUtils.isSameBlockIgnoreHeight(event.getFrom(), event.getTo())) return;
		SavedStatistic.BLOCKS_MOVED.increaseStatistic(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(@Nonnull PlayerDropItemEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		SavedStatistic.ITEMS_DROPPED.increaseStatistic(event.getPlayer(), event.getItemDrop().getItemStack().getType().name());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(@Nonnull EntityDamageEvent event) {
		if (!shouldExecuteEffect()) return;
		if (!(event.getEntity() instanceof Player)) return;;
		Player player = (Player) event.getEntity();
		if (ignorePlayer(player)) return;
		SavedStatistic.DAMAGE_TAKEN.increaseStatistic(player, event.getCause().name(), ChallengeHelper.getFinalDamage(event));
	}

}
