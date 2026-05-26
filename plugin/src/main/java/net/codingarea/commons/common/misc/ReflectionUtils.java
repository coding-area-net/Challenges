package net.codingarea.commons.common.misc;

import net.codingarea.commons.common.collection.ArrayWalker;
import net.codingarea.commons.common.collection.ClassWalker;
import net.codingarea.commons.common.collection.PublicSecurityManager;
import net.codingarea.commons.common.collection.WrappedException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public final class ReflectionUtils {

	private ReflectionUtils() {}

	@Nonnull
	public static Collection<Method> getPublicMethodsAnnotatedWith(@Nonnull Class<?> clazz, @Nonnull Class<? extends Annotation> classOfAnnotation) {
		List<Method> annotatedMethods = new ArrayList<>();
		for (Method method : clazz.getMethods()) {
			if (!method.isAnnotationPresent(classOfAnnotation)) continue;
			annotatedMethods.add(method);
		}
		return annotatedMethods;
	}

	@Nonnull
	public static Collection<Method> getMethodsAnnotatedWith(@Nonnull Class<?> clazz, @Nonnull Class<? extends Annotation> classOfAnnotation) {
		List<Method> annotatedMethods = new ArrayList<>();
		for (Class<?> currentClass : ClassWalker.walk(clazz)) {
			for (Method method : currentClass.getDeclaredMethods()) {
				if (!method.isAnnotationPresent(classOfAnnotation)) continue;
				annotatedMethods.add(method);
			}
		}
		return annotatedMethods;
	}

	@Nonnull
	public static Method getInheritedPrivateMethod(@Nonnull Class<?> clazz, @Nonnull String name, @Nonnull Class<?>... parameterTypes) throws NoSuchMethodException {
		for (Class<?> current : ClassWalker.walk(clazz)) {
			try {
				return current.getDeclaredMethod(name, parameterTypes);
			} catch (Throwable ignored) {
			}
		}

		throw new NoSuchMethodException(name);
	}

	@Nonnull
	public static Field getInheritedPrivateField(@Nonnull Class<?> clazz, @Nonnull String name) throws NoSuchFieldException {
		for (Class<?> current : ClassWalker.walk(clazz)) {
			try {
				return current.getDeclaredField(name);
			} catch (Throwable ignored) {
			}
		}

		throw new NoSuchFieldException(name);
	}

	/**
	 * @param classOfEnum The class containing the enum constants
	 * @return The first enum found by the given names
	 */
	@Nonnull
	public static <E extends Enum<E>> E getFirstEnumByNames(@Nonnull Class<E> classOfEnum, @Nonnull String... names) {
		for (String name : names) {
			try {
				return Enum.valueOf(classOfEnum, name);
			} catch (IllegalArgumentException | NoSuchFieldError ignored) { }
		}
		throw new IllegalArgumentException("No enum found in " + classOfEnum.getName() + " for " + Arrays.toString(names));
	}

	/**
	 * Iterates through an array which may contain primitive data types or non primitive data types and performs the given action on each element.
	 * Because we can't just cast such an array to {@code Object[]}, we have to use some reflections.
	 *
	 * @param array The target array, as {@link Object}; Can't use an array type here.
	 * @param <T> The type of data we will cast the content to. Use {@link Object} if the it's unknown.
	 *
	 * @throws IllegalArgumentException
	 *         If the {@code array} is not an actual array
	 *
	 * @see Array
	 * @see Array#getLength(Object)
	 * @see Array#get(Object, int)
	 */
	public static <T> void forEachInArray(@Nonnull Object array, @Nonnull Consumer<T> action) {
		ReflectionUtils.<T>iterableArray(array).forEach(action);
	}

	@Nonnull
	@CheckReturnValue
	public static <T> Iterable<T> iterableArray(@Nonnull Object array) {
		return ArrayWalker.walk(array);
	}

	@CheckReturnValue
	public static Class<?> getCaller(int index) {
		try {
			return new PublicSecurityManager().getPublicClassContext()[index + 2];
		} catch (Exception ex) {
			throw new WrappedException(ex);
		}
	}

	@CheckReturnValue
	public static Class<?> getCaller() {
		return getCaller(2);
	}

	@Nonnull
	public static String getCallerName() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		StackTraceElement element = trace[3];

		String className = StringUtils.getAfterLastIndex(element.getClassName(), ".");
		return className + "." + element.getMethodName();
	}

	/**
	 * Takes a {@link Enum} and returns the corresponding {@link Field} using {@link Class#getField(String)}
	 *
	 * @see Class#getField(String)
	 */
	@Nonnull
	public static Field getEnumAsField(@Nonnull Enum<?> enun) {
		Class<?> classOfEnum = enun.getClass();

		try {
			return classOfEnum.getField(enun.name());
		} catch (NoSuchFieldException ex) {
			throw new WrappedException(ex);
		}
	}

	/**
	 * @see Field#getAnnotations()
	 */
	@Nonnull
	public static <E extends Enum<?>> Annotation[] getEnumAnnotations(@Nonnull E enun) {
		Field field = getEnumAsField(enun);
		return field.getAnnotations();
	}

	/**
	 * @return Returns {@code null} if no annotation of this class is present
	 *
	 * @see Field#getAnnotation(Class)
	 */
	public static <E extends Enum<?>, A extends Annotation> A getEnumAnnotation(@Nonnull E enun, Class<A> classOfAnnotation) {
		Field field = getEnumAsField(enun);
		return field.getAnnotation(classOfAnnotation);
	}

	@Nullable
	public static <E extends Enum<E>> E getEnumOrNull(@Nullable String name, @Nonnull Class<E> classOfEnum) {
		try {
			if (name == null) return null;
			return Enum.valueOf(classOfEnum, name);
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClassOrNull(@Nullable String name) {
		try {
			if (name == null) return null;
			return (Class<T>) Class.forName(name);
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClassOrNull(@Nullable String name, boolean initialize, @Nonnull ClassLoader classLoader) {
		try {
			if (name == null) return null;
			return (Class<T>) Class.forName(name, initialize, classLoader);
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethodOrNull(@Nullable Object instance, @Nonnull Method method) {
		try {
			if (!method.isAccessible()) method.setAccessible(true);
			return (T) method.invoke(instance);
		} catch (Throwable ex) {
			return null;
		}
	}

	@Nullable
	public static <T> T invokeStaticMethodOrNull(@Nonnull Class<?> clazz, @Nonnull String method) {
		try {
			return invokeMethodOrNull(null, clazz.getMethod(method));
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	@Nullable
	public static <T> T invokeMethodOrNull(@Nonnull Object instance, @Nonnull String method) {
		try {
			return invokeMethodOrNull(instance, instance.getClass().getDeclaredMethod(method));
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	@Nullable
	public static <T> T getAnnotationValue(@Nonnull Annotation annotation) {
		return invokeMethodOrNull(annotation, "value");
	}

	@Nullable
	public static <E extends Enum<?>> E getEnumByAlternateNames(@Nonnull Class<E> classOfE, @Nonnull String input) {
		E[] values = invokeStaticMethodOrNull(classOfE, "values");
		String[] methodNames = { "getName", "getNames", "getAlias", "getAliases", "getKey", "getKeys", "name", "toString", "ordinal", "getId", "id" };
        assert values != null;
        for (E value : values) {
			for (String method : methodNames) {
				if (check(input, invokeMethodOrNull(value, method)))
					return value;
			}
		}

		return null;
	}

	private static boolean check(@Nonnull String input, @Nullable Object value) {
		if (value == null) return false;
		if (value.getClass().isArray()) {
			for (Object key : iterableArray(value)) {
				if (input.equalsIgnoreCase(String.valueOf(key)))
					return true;
			}
		}
		return input.equalsIgnoreCase(String.valueOf(value));
	}

}
