package net.codingarea.challenges.plugin.utils.misc;

import net.codingarea.commons.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;

import java.util.*;
import java.util.Map.Entry;

public class MapUtils {

  public static Map<String, String> createStringMap(String... data) {
    Map<String, String> map = new HashMap<>();
    for (int i = 1; i < data.length; i += 2) {
      String key = data[i - 1];
      String value = data[i];
      map.put(key, value);
    }
    return map;
  }

  public static Map<String, String[]> createStringArrayMap(String key, String... values) {
    Map<String, String[]> map = new HashMap<>();
    map.put(key, values);
    return map;
  }

  public static Map<String, List<String>> createStringListMap(String key, String... values) {
    Map<String, List<String>> map = new HashMap<>();
    map.put(key, new ArrayList<>(Arrays.asList(values)));
    return map;
  }

  public static Map<String, String[]> createSubSettingsMapFromDocument(Document document) {
    if (document == null) return new HashMap<>();
    Map<String, String[]> map = new HashMap<>();
    for (Entry<String, Object> entry : document.entrySet()) {
      try {
        map.put(entry.getKey(), document.getStringArray(entry.getKey()));
      } catch (Exception exception) {
        Challenges.getInstance().getILogger().error("", exception);
      }
    }
    return map;
  }

}
