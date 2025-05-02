package net.codingarea.commons.database.sql.abstraction.where;

import javax.annotation.Nonnull;
import java.util.Objects;

public class StringIgnoreCaseWhere implements SQLWhere {

	protected final String column;
	protected final String value;

	public StringIgnoreCaseWhere(@Nonnull String column, @Nonnull String value) {
		this.column = column;
		this.value = value;
	}

	@Nonnull
	@Override
	public Object[] getArgs() {
		return new Object[] { value };
	}

	@Nonnull
	@Override
	public String getAsSQLString() {
		return String.format("LOWER(%s) = LOWER(?)", column);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StringIgnoreCaseWhere that = (StringIgnoreCaseWhere) o;
		return column.equals(that.column) && value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(column, value);
	}

}
