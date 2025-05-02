package net.codingarea.challenges.plugin.management.bstats;

import net.codingarea.commons.bukkit.utils.bstats.Metrics;
import net.codingarea.commons.bukkit.utils.bstats.chart.AdvancedPie;
import net.codingarea.commons.bukkit.utils.bstats.chart.SimplePie;
import net.codingarea.commons.bukkit.utils.bstats.chart.SingleLineChart;
import net.codingarea.commons.common.misc.StringUtils;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.utils.misc.MemoryConverter;

import java.util.HashMap;

public class MetricsLoader {

  public void start() {
    Challenges plugin = Challenges.getInstance();

    Metrics metrics = new Metrics(plugin, 11494);
    metrics.addCustomChart(new SimplePie("language", () -> {
      LanguageLoader loader = Challenges.getInstance().getLoaderRegistry()
        .getFirstLoaderByClass(LanguageLoader.class);
      if (loader == null) return "NULL";
      return loader.getLanguage();
    }));
    metrics.addCustomChart(new SimplePie("cloudType", () -> StringUtils.getEnumName(plugin.getCloudSupportManager().getType())));
    metrics.addCustomChart(new SimplePie("databaseType", () -> StringUtils.getEnumName(plugin.getDatabaseManager().getType())));
    metrics.addCustomChart(new SingleLineChart("totalMemory", this::getMemory));
    metrics.addCustomChart(new SingleLineChart("totalCores", () -> Runtime.getRuntime().availableProcessors()));
    metrics.addCustomChart(new AdvancedPie("maxMemory", () -> {
      HashMap<String, Integer> map = new HashMap<>();
      if (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE) return map;
      map.put(String.valueOf(getMemory()), 1);
      return map;
    }));

  }

  private int getMemory() {
    return MemoryConverter.getGB(Runtime.getRuntime().maxMemory());
  }

}
