package com.browserup.bup.mitmproxy.addons;

import com.browserup.bup.mitmproxy.MitmProxyProcessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractAddon {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAddon.class);
  private static final Map<String, String> ADDONS_CACHE = new HashMap<>();

  public abstract String[] getCommandParams();

  public abstract String getAddOnFileName();

  public String getAddOnFilePath() {
    return ADDONS_CACHE.computeIfAbsent(getAddOnFileName(), (fn) -> {
      try {
        return loadHarDumpAddOn();
      } catch (Exception e) {
        LOGGER.error("Couldn't load add on by file name", e);
        throw new RuntimeException("Couldn't load add on by file name", e);
      }
    });
  }

  private String loadHarDumpAddOn() throws Exception {
    String addOnFileName = getAddOnFileName();
    URL resource = AbstractAddon.class.getResource("/mitmproxy/" + addOnFileName);
    String addOnString = new BufferedReader(new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    File addOnFile = File.createTempFile(addOnFileName, ".py");
    Files.write(addOnFile.toPath(), addOnString.getBytes());

    return addOnFile.getAbsolutePath();
  }
}
