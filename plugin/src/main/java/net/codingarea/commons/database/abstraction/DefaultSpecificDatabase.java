package net.codingarea.commons.database.abstraction;

import net.codingarea.commons.database.Database;
import net.codingarea.commons.database.SpecificDatabase;
import net.codingarea.commons.database.action.*;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DefaultSpecificDatabase implements SpecificDatabase {

	protected final Database parent;
	protected final String name;

	public DefaultSpecificDatabase(@Nonnull Database parent, @Nonnull String name) {
		this.parent = parent;
		this.name = name;
	}

	@Override
	public boolean isConnected() {
		return parent.isConnected();
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Nonnull
	@Override
	public DatabaseCountEntries countEntries() {
		return parent.countEntries(name);
	}

	@Nonnull
	@Override
	public DatabaseQuery query() {
		return parent.query(name);
	}

	@Nonnull
	@Override
	public DatabaseUpdate update() {
		return parent.update(name);
	}

	@Nonnull
	@Override
	public DatabaseInsertion insert() {
		return parent.insert(name);
	}

	@Nonnull
	@Override
	public DatabaseInsertionOrUpdate insertOrUpdate() {
		return parent.insertOrUpdate(name);
	}

	@Nonnull
	@Override
	public DatabaseDeletion delete() {
		return parent.delete(name);
	}

	@Nonnull
	@Override
	public Database getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return "SpecificDatabase[" + name + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultSpecificDatabase that = (DefaultSpecificDatabase) o;
		return Objects.equals(parent, that.parent) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parent, name);
	}
}
