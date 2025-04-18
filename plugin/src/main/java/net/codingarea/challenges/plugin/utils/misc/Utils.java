package net.codingarea.challenges.plugin.utils.misc;

import net.anweisen.utilities.common.collection.IOUtils;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.misc.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public final class Utils {

	private Utils() {
	}

	@Nonnull
	public static List<String> filterRecommendations(@Nonnull String argument, @Nonnull String... recommendations) {
		argument = argument.toLowerCase();
		List<String> list = new ArrayList<>();
		for (String current : recommendations) {
			if (current.toLowerCase().startsWith(argument))
				list.add(current);
		}
		return list;
	}

	@Nonnull
	public static UUID fetchUUID(@Nonnull String name) throws IOException {
		String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
		String content = IOUtils.toString(new URL(url));
		Document document = Document.parseJson(content);
		return Optional.ofNullable(matchUUID(document.getString("id"))).orElseThrow(IOException::new);
	}

	@Nullable
	public static UUID matchUUID(@Nullable String uuid) {
		if (uuid == null) return null;
		Pattern pattern = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
		return UUID.fromString(pattern.matcher(uuid).replaceAll("$1-$2-$3-$4-$5"));
	}

	@Nullable
	@CheckReturnValue
	public static Material getMaterial(@Nullable String name) {
		return ReflectionUtils.getEnumOrNull(name, Material.class);
	}

	@Nullable
	@CheckReturnValue
	public static EntityType getEntityType(@Nullable String name) {
		return ReflectionUtils.getEnumOrNull(name, EntityType.class);
	}

	public static <T extends Enum<?>> void removeEnums(@Nonnull Collection<T> collection, @Nonnull String... names) {
		List<String> nameList = Arrays.asList(names);
		collection.removeIf(element -> nameList.contains(element.name()));
	}

}
