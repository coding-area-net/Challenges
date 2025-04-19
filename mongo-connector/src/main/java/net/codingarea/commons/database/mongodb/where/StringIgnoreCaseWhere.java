package net.codingarea.commons.database.mongodb.where;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class StringIgnoreCaseWhere implements MongoDBWhere {

	protected final String field;
	protected final String value;

	public StringIgnoreCaseWhere(@Nonnull String field, @Nonnull String value) {
		this.field = field;
		this.value = value;
	}

	@Nonnull
	@Override
	public Bson toBson() {
		return Filters.eq(field, value);
	}

	@Nullable
	@Override
	public Collation getCollation() {
		return Collation.builder().collationStrength(CollationStrength.SECONDARY).locale("en").build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StringIgnoreCaseWhere that = (StringIgnoreCaseWhere) o;
		return field.equals(that.field) && value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, value);
	}

}
