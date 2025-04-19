package net.codingarea.commons.database.sql.abstraction.where;

import javax.annotation.Nonnull;

public interface SQLWhere {

	@Nonnull
	Object[] getArgs();

	@Nonnull
	String getAsSQLString();

}
