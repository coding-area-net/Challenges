package net.codingarea.commons.common.config.document;

import net.codingarea.commons.common.config.Document;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

public class YamlDocument extends AbstractDocument {

	protected final ConfigurationSection config;

	public YamlDocument() {
		this.config = new YamlConfiguration();
	}

	public YamlDocument(@Nonnull ConfigurationSection config) {
		this.config = config;
	}

	public YamlDocument(@Nonnull ConfigurationSection config, @Nonnull Document root, @Nullable Document parent) {
		super(root, parent);
		this.config = config;
	}

	public YamlDocument(@Nonnull File file) {
		this(YamlConfiguration.loadConfiguration(file));
	}

	@Nullable
	@Override
	public String getString(@Nonnull String path) {
		return config.getString(path);
	}

	@Nonnull
	@Override
	public String getString(@Nonnull String path, @Nonnull String def) {
		String string = config.getString(path, def);
		return string;
	}

	@Nullable
	@Override
	public Object getObject(@Nonnull String path) {
		return config.get(path);
	}

	@Nonnull
	@Override
	public Object getObject(@Nonnull String path, @Nonnull Object def) {
		Object value = config.get(path, def);
		return value;
	}

	@Override
	public <T> T getInstance(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return copyJson().getInstance(path, classOfT);
	}

	@Override
	public <T> T toInstanceOf(@Nonnull Class<T> classOfT) {
		return copyJson().toInstanceOf(classOfT);
	}

	@Nonnull
	@Override
	public Document getDocument0(@Nonnull String path, @Nonnull Document root, @Nullable Document parent) {
		ConfigurationSection section = config.getConfigurationSection(path);
		if (section == null) section = config.createSection(path);
		return new YamlDocument(section, root, parent);
	}

	@Nonnull
	@Override
	public List<Document> getDocumentList(@Nonnull String path) {
		List<Map<?, ?>> maps = config.getMapList(path);
		List<Document> documents = new ArrayList<>(maps.size());
		for (Map<?, ?> map : maps) {
			documents.add(new MapDocument((Map<String, Object>) map, root, this));
		}
		return documents;
	}

	@Override
	public long getLong(@Nonnull String path) {
		return config.getLong(path);
	}

	@Override
	public long getLong(@Nonnull String path, long def) {
		return config.getLong(path, def);
	}

	@Override
	public int getInt(@Nonnull String path) {
		return config.getInt(path);
	}

	@Override
	public int getInt(@Nonnull String path, int def) {
		return config.getInt(path, def);
	}

	@Override
	public short getShort(@Nonnull String path) {
		return (short) config.getInt(path);
	}

	@Override
	public short getShort(@Nonnull String path, short def) {
		return (short) config.getInt(path, def);
	}

	@Override
	public byte getByte(@Nonnull String path) {
		return (byte) config.getInt(path);
	}

	@Override
	public byte getByte(@Nonnull String path, byte def) {
		return (byte) config.getInt(path, def);
	}

	@Override
	public char getChar(@Nonnull String path) {
		return getChar(path, (char) 0);
	}

	@Override
	public char getChar(@Nonnull String path, char def) {
		try {
			return Objects.requireNonNull(getString(path)).charAt(0);
		} catch (NullPointerException | StringIndexOutOfBoundsException ex) {
			return def;
		}
	}

	@Override
	public double getDouble(@Nonnull String path) {
		return config.getDouble(path);
	}

	@Override
	public double getDouble(@Nonnull String path, double def) {
		return config.getDouble(path, def);
	}

	@Override
	public float getFloat(@Nonnull String path) {
		return (float) config.getDouble(path);
	}

	@Override
	public float getFloat(@Nonnull String path, float def) {
		return (float) config.getDouble(path, def);
	}

	@Override
	public boolean getBoolean(@Nonnull String path) {
		return config.getBoolean(path);
	}

	@Override
	public boolean getBoolean(@Nonnull String path, boolean def) {
		return config.getBoolean(path, def);
	}

	@Nonnull
	@Override
	public List<String> getStringList(@Nonnull String path) {
		return config.getStringList(path);
	}

	@Nullable
	@Override
	public UUID getUUID(@Nonnull String path) {
		try {
			return UUID.fromString(Objects.requireNonNull(getString(path)));
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	@Override
	public Date getDate(@Nonnull String path) {
		try {
			return DateFormat.getDateTimeInstance().parse(path);
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	@Override
	public OffsetDateTime getDateTime(@Nonnull String path) {
		try {
			return OffsetDateTime.parse(Objects.requireNonNull(getString(path)));
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	@Override
	public Color getColor(@Nonnull String path) {
		try {
			return Color.decode(Objects.requireNonNull(getString(path)));
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	@Override
	public <E extends Enum<E>> E getEnum(@Nonnull String path, @Nonnull Class<E> classOfEnum) {
		try {
			String name = getString(path);
			if (name == null) return null;
			return Enum.valueOf(classOfEnum, name);
		} catch (Throwable ex) {
			return null;
		}
	}

	@Override
	public boolean isList(@Nonnull String path) {
		return config.isList(path);
	}

	@Override
	public boolean isObject(@Nonnull String path) {
		return !isDocument(path) && !isList(path);
	}

	@Override
	public boolean isDocument(@Nonnull String path) {
		return config.get(path) instanceof ConfigurationSection;
	}

	@Override
	public boolean contains(@Nonnull String path) {
		return config.contains(path, true);
	}

	@Override
	public int size() {
		return config.getValues(false).size();
	}

	@Override
	public void set0(@Nonnull String path, @Nullable Object value) {
		if (value instanceof Enum<?>) {
			Enum<?> enun = (Enum<?>) value;
			value = enun.name();
		}
		config.set(path, value);
	}

	@Override
	public void remove0(@Nonnull String path) {
		config.set(path, null);
	}

	@Override
	public void clear0() {
		for (String key : config.getKeys(true)) {
			config.set(key, null);
		}
	}

	@Nonnull
	@Override
	public Map<String, Object> values() {
		return config.getValues(true);
	}

	@Nonnull
	@Override
	public Collection<String> keys() {
		return config.getKeys(false);
	}

	@Override
	public void forEach(@Nonnull BiConsumer<? super String, ? super Object> action) {
		values().forEach(action);
	}

	@Override
	public String toString() {

		DumperOptions yamlOptions = new DumperOptions();
		LoaderOptions loaderOptions = new LoaderOptions();
		Representer yamlRepresenter = new YamlRepresenter();
		Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions, loaderOptions);

		yamlOptions.setIndent(2);
		yamlOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
		yamlRepresenter.setDefaultFlowStyle(FlowStyle.BLOCK);
		String dump = yaml.dump(config.getValues(false));
		if (dump.equals("{}\n")) {
			dump = "";
		}

		return dump;

	}

	@Override
	public void write(@Nonnull Writer writer) throws IOException {
		writer.write(toString());
	}

	@Nonnull
	@Override
	public Document copyJson() {
		return new GsonDocument(values());
	}

	@Nonnull
	@Override
	public String toJson() {
		return copyJson().toJson();
	}

	@Nonnull
	@Override
	public String toPrettyJson() {
		return copyJson().toPrettyJson();
	}

	@Override
	public boolean isReadonly() {
		return false;
	}

}
