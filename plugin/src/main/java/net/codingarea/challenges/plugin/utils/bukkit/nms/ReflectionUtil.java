package net.codingarea.challenges.plugin.utils.bukkit.nms;

import lombok.Getter;
import net.codingarea.challenges.plugin.Challenges;
import org.bukkit.Bukkit;

import java.lang.reflect.*;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * @author TobiasDeBruijn | <a href="https://github.com/TobiasDeBruijn">.<a href="..</a>
 * ">* @source https://github.com/TobiasDeBruijn/Bu</a>kkitReflectionUtil
 */
public class ReflectionUtil {

  public static String SERVER_VERSION;
  /**
   * -- GETTER --
   * Check if the new way of packaging Spigot is used<br>
   * For >=1.17 this will be true, for =<1.16 this will be false.<br>
   * <p>
   * This dictates if you should use
   * (<=1.16) or
   * (>=1.17).
   */
  @Getter
  private static boolean useNewSpigotPackaging;

  /**
   * -- GETTER --
   * Get the current major Minecraft version.
   * <p>
   * E.g for Minecraft 1.18 this is 18.
   */
  @Getter
  private static int majorVersion;
  /**
   * -- GETTER --
   * Get the current minor Minecraft version.
   * <p>
   * E.g. for Minecraft 1.18.2 this is 2.
   */
  @Getter
  private static int minorVersion;

  static {
    try {
      // Legacy support
      Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
      Object serverObject = getMethod(bukkitClass, "getServer").invoke(null);
      String serverPackageName = serverObject.getClass().getPackage().getName();

      SERVER_VERSION = serverPackageName.substring(serverPackageName.lastIndexOf('.') + 1);

      // example: Bukkit version: 3638-Spigot-d90018e-7dcb59b (MC: 1.19.3)
      String version = Bukkit.getVersion();

      String[] parts = version.split(Pattern.quote("(MC: "));
      String[] versionParts = parts[1].split(Pattern.quote("."));

      // There's a minor version
      String major, minor;
      if (versionParts.length > 2) {
        major = versionParts[1];
        minor = versionParts[2].replace(")", "");
      } else {
        major = versionParts[1].replace(")", "");
        minor = "0";
      }

      majorVersion = Integer.parseInt(major);
      minorVersion = Integer.parseInt(minor);

      useNewSpigotPackaging = majorVersion >= 17;

    } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
             NoSuchMethodException e) {
      Challenges.getInstance().getLogger().error("Failed to initialize the ReflectionUtil:", e);
    }
  }

  /**
   * Get a Class from the org.bukkit.craftbukkit.SERVER_VERSION. package
   *
   * @param className The name of the class
   * @return Returns the Class
   * @throws ClassNotFoundException Thrown when the Class was not found
   */
  public static Class<?> getBukkitClass(String className) throws ClassNotFoundException {
    return Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + "." + className);
  }

  /**
   * <strong>1.16 and older only</strong>
   * <p>
   * Get a Class from the net.minecraft.server.SERVER_VERSION. package
   *
   * @param className The name of the class
   * @return Returns the Class
   * @throws ClassNotFoundException Thrown when the Class was not found
   */
  @Deprecated
  public static Class<?> getNmsClass(String className) throws ClassNotFoundException {
    return Class.forName("net.minecraft.server." + SERVER_VERSION + "." + className);
  }

  /**
   * <strong>1.17 and newer only</strong>
   * <p>
   * Get a Class from the net.minecraft package
   *
   * @param className The name of the class
   * @return Returns the class
   * @throws ClassNotFoundException Thrown when the Class was not found
   */
  public static Class<?> getMinecraftClass(String className) throws ClassNotFoundException {
    return Class.forName("net.minecraft." + className);
  }

  /**
   * Get the Constructor of a Class
   *
   * @param clazz The Class in which the Constructor is defined
   * @param args  Arguments taken by the Constructor
   * @return Returns the Constructor
   * @throws NoSuchMethodException Thrown when no Constructor in the Class was found with the provided combination of arguments
   */
  public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) throws NoSuchMethodException {
    Constructor<?> con = clazz.getConstructor(args);
    con.setAccessible(true);

    return con;
  }

  /**
   * Get an Enum from an Enum constant
   *
   * @param clazz    The Class in which the Enum is defined
   * @param constant The name of the Enum Constant
   * @return Returns the Enum or null if the Enum does not have a member called <i>constant</i>
   */
  public static Enum<?> getEnum(Class<?> clazz, String constant) {
    Enum<?>[] constants = (Enum<?>[]) clazz.getEnumConstants();

    for (Enum<?> e : constants) {
      if (e.name().equalsIgnoreCase(constant)) {
        return e;
      }
    }

    return null;
  }

  /**
   * Get an Enum constant by it's name and constant
   *
   * @param clazz    The Class in which the Enum is defined
   * @param enumname The name of the Enum
   * @param constant The name of the Constant
   * @return Returns the Enum or null if the Enum does not have a member called <i>constant</i>
   * @throws ClassNotFoundException If the Class does not have an Enum called <i>enumname</i>
   */
  public static Enum<?> getEnum(Class<?> clazz, String enumname, String constant) throws ClassNotFoundException {
    Class<?> c = Class.forName(clazz.getName() + "$" + enumname);
    return getEnum(c, constant);
  }

  /**
   * Get a Field
   *
   * @param clazz     The Class in which the Field is defined
   * @param fieldName The name of the Field
   * @return Returns the Field
   * @throws NoSuchFieldException Thrown when the Field was not present in the Class
   */
  public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
    Field f = clazz.getDeclaredField(fieldName);
    f.setAccessible(true);
    return f;
  }

  /**
   * Sets the value of a field
   *
   * @param instance  The instance of the class in which the field is defined
   * @param fieldName The name of the field
   * @param value     The value the field should be set to
   */
  public static void setFieldValue(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
    Field field = instance.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(instance, value);
  }

  /**
   * Get a Method
   *
   * @param clazz      The Class in which the Method is defined
   * @param methodName The name of the method
   * @param args       The argument types the method takes
   * @return Returns the Method
   */
  public static Method getMethod(Class<?> clazz, String methodName, Class<?>... args) throws NoSuchMethodException {
    Method m = clazz.getDeclaredMethod(methodName, args);
    m.setAccessible(true);
    return m;
  }

  /**
   * Invoke a Method which takes no arguments. The Class in which the Method is defined is derived from the provided Object
   *
   * @param obj        The object to invoke the method on
   * @param methodName The name of the Method
   * @return Returns the result of the method, can be null if the method returns void
   */
  public static Object invokeMethod(Object obj, String methodName) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    return invokeMethod(obj.getClass(), obj, methodName);
  }

  /**
   * Invoke a Method where the argument types are derived from the provided arguments. The Class in which the Method is defined is derived from the provided Object
   *
   * @param obj        The object to invoke the method on
   * @param methodName The name of the Method
   * @param args       The arguments to pass to the Method
   * @return Returns the result of the method, can be null if the method returns void
   */
  public static Object invokeMethod(Object obj, String methodName, Object[] args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    return invokeMethod(obj.getClass(), obj, methodName, args);
  }

  /**
   * Invoke a Method where the argument types are explicitly given (Helpful when working with primitives). The Class in which the Method is defined is derived from the provided Object.
   *
   * @param obj        The Object to invoke the method on
   * @param methodName The name of the Method
   * @param argTypes   The types of arguments as a Class array
   * @param args       The arguments as an object array
   * @return Returns the result of the method, can be null if the method returns void
   */
  public static Object invokeMethod(Object obj, String methodName, Class<?>[] argTypes, Object[] args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    return invokeMethod(obj.getClass(), obj, methodName, argTypes, args);
  }

  /**
   * Invoke a Method where the Class where to find the method is explicitly given (Helpful if the method is located in a superclass). The argument types are derived from the provided arguments
   *
   * @param clazz      The Class where the method is located
   * @param obj        The Object to invoke the method on
   * @param methodName The name of the method
   * @param args       The arguments to be passed to the method
   * @return Returns the result of the method, can be null if the method returns void
   */
  public static Object invokeMethod(Class<?> clazz, Object obj, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Class<?>[] argTypes = new Class<?>[args.length];
    for (int i = 0; i < args.length; i++) {
      argTypes[i] = args[i].getClass();
    }

    return invokeMethod(clazz, obj, methodName, argTypes, args);
  }

  /**
   * Invoke a Method where the Class where the Method is defined is explicitly given, and the argument types are explicitly given
   *
   * @param clazz      The Class in which the Method is located
   * @param obj        The Object on which to invoke the Method
   * @param methodName The name of the Method
   * @param argTypes   Argument types
   * @param args       Arguments to pass to the method
   * @return Returns the result of the method, can be null if the method returns void
   */
  public static Object invokeMethod(Class<?> clazz, Object obj, String methodName, Class<?>[] argTypes, Object[] args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Method m = getMethod(clazz, methodName, argTypes);
    return m.invoke(obj, args);
  }

  /**
   * Get the value of a Field, where the Class in which the field is defined is derived from the provided Object
   *
   * @param obj  The object in which the field is located, and from which to get the value
   * @param name The name of the Field to get the value from
   * @return Returns the value of the Field
   */
  public static Object getObject(Object obj, String name) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    return getObject(obj.getClass(), obj, name);
  }

  /**
   * Get the value of a Field, where the Class in which the Field is defined is explicitly given. (Helpful when the Field is in a superclass)
   *
   * @param obj   The Object to get the value from
   * @param clazz The Class in which the Field is defined
   * @param name  The name of the Field
   * @return Returns the value of the Field
   * @deprecated Use {@link #getObject(Class, Object, String)} instead
   */
  @Deprecated
  public static Object getObject(Object obj, Class<?> clazz, String name) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    return getObject(clazz, obj, name);
  }

  /**
   * Get the value of a Field, where the Class in which the Field is defined is explicitly given. (Helpful when the Field is in a superclass)
   *
   * @param clazz The Class in which the Field is defined
   * @param obj   The Object to get the value from
   * @param name  The name of the Field
   * @return Returns the value of the Field
   */
  public static Object getObject(Class<?> clazz, Object obj, String name) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    Field f = getField(clazz, name);
    f.setAccessible(true);
    return f.get(obj);
  }

  /**
   * Invoke a Class' constructor. The argument types are derived from the provided arguments
   *
   * @param clazz The Class in which the Constructor is defined
   * @param args  The arguments to pass to the Constructor
   * @return Returns an instance of the provided Class in which the Constructor is located
   */
  public static Object invokeConstructor(Class<?> clazz, Object... args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Class<?>[] argTypes = new Class<?>[args.length];
    for (int i = 0; i < args.length; i++) {
      argTypes[i] = args[i].getClass();
    }

    return invokeConstructor(clazz, argTypes, args);
  }

  /**
   * Invoke a Class' Constructor, where the argument types are explicitly given (Helpful when working with primitives)
   *
   * @param clazz    The Class in which the Constructor is defined
   * @param argTypes The argument types
   * @param args     The arguments to pass to the constructor
   * @return Returns an instance of the provided Class in which the Constructor is located
   */
  public static Object invokeConstructor(Class<?> clazz, Class<?>[] argTypes, Object[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Constructor<?> con = getConstructor(clazz, argTypes);
    return con.newInstance(args);
  }

  /**
   * Print all Methods in a Class with their parameters. Will print the Method's modifiers, return type, name and arguments and their types
   *
   * @param clazz The class to get methods from
   */
  public static void printMethodsInClass(Class<?> clazz) {
    System.out.println("Methods in " + clazz.getName() + ":");

    for (Method m : clazz.getDeclaredMethods()) {
      StringBuilder print = new StringBuilder(128);
      print.append(getModifiers(m.getModifiers())).append(" ");
      print.append(m.getReturnType().getName()).append(" ");
      print.append(m.getName()).append("(");

      Class<?>[] parameterTypes = m.getParameterTypes();
      int parameterTypesLength = parameterTypes.length;
      for (int i = 0; i < parameterTypesLength; i++) {
        print.append(parameterTypes[i].getName());

        if (i != parameterTypesLength - 1) {
          print.append(", ");
        }
      }

      print.append(")");
      System.out.println(print.toString().trim());
    }
  }

  /**
   * Print all Fields in a Class. Will print the Field's modifiers, type and name
   *
   * @param clazz The class to get fields from
   */
  public static void printFieldsInClass(Class<?> clazz) {
    System.out.println("Fields in " + clazz.getName() + ":");
    for (Field f : clazz.getDeclaredFields()) {
      String print = getModifiers(f.getModifiers()) + " " + f.getType().getName() + " " + f.getName();
      System.out.println(print.trim());
    }
  }

  /**
   * Get modifiers as a String
   *
   * @param modifiers int value of the modifiers
   * @return Returns the modifiers as a String
   * @see Field#getModifiers()
   * @see Class#getModifiers()
   * @see Method#getModifiers()
   */
  private static String getModifiers(int modifiers) {
    StringJoiner modifiersStr = new StringJoiner(" ");
    if (Modifier.isPrivate(modifiers)) {
      modifiersStr.add("private");
    }

    if (Modifier.isProtected(modifiers)) {
      modifiersStr.add("protected");
    }

    if (Modifier.isPublic(modifiers)) {
      modifiersStr.add("public");
    }

    if (Modifier.isAbstract(modifiers)) {
      modifiersStr.add("abstract");
    }

    if (Modifier.isStatic(modifiers)) {
      modifiersStr.add("static");
    }

    if (Modifier.isFinal(modifiers)) {
      modifiersStr.add("final");
    }

    if (Modifier.isTransient(modifiers)) {
      modifiersStr.add("transient");
    }

    if (Modifier.isVolatile(modifiers)) {
      modifiersStr.add("volatile");
    }

    if (Modifier.isNative(modifiers)) {
      modifiersStr.add("native");
    }

    if (Modifier.isStrict(modifiers)) {
      modifiersStr.add("strictfp");
    }

    if (Modifier.isSynchronized(modifiers)) {
      modifiersStr.add("synchronized");
    }

    if (Modifier.isInterface(modifiers)) {
      modifiersStr.add("interface");
    }

    return modifiersStr.toString().trim();
  }
}
