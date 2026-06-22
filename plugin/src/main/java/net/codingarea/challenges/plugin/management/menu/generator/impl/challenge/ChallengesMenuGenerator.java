package net.codingarea.challenges.plugin.management.menu.generator.impl.challenge;

import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.management.menu.generator.IDynamicMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.MultiPageMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.categorised.CategorisedMenuGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @see ChallengeListMenuGenerator
 * @see CategorisedMenuGenerator
 */
public abstract class ChallengesMenuGenerator extends MultiPageMenuGenerator implements IDynamicMenuGenerator<IChallenge> {

  public abstract boolean hasAnyNewChallenges();

  public abstract boolean hasAnyUpdatedChallenges();

  public abstract int getEnabledCount();

  @NotNull
  public abstract Optional<IChallenge> getFirstEnabledChallenge();

}
