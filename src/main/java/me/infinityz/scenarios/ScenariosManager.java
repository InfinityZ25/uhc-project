package me.infinityz.scenarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.infinityz.scenarios.scenarios.*;

/**
 * ScenariosManager
 */
public class ScenariosManager {

    public Map<String, IScenario> scenarioMap;

    public ScenariosManager() {
        scenarioMap = new HashMap<>();
        scenarioMap.putIfAbsent("cutclean", new Cutclean());
        scenarioMap.putIfAbsent("hasteyboys", new HasteyBoys());
        scenarioMap.putIfAbsent("timber", new Timber());
        scenarioMap.putIfAbsent("fireless", new Fireless());
        scenarioMap.putIfAbsent("timebomb", new Timebomb());
        scenarioMap.putIfAbsent("nofall", new NoFall());
        scenarioMap.putIfAbsent("goldless", new Goldless());
        scenarioMap.putIfAbsent("diamondless", new Diamondless());
        scenarioMap.putIfAbsent("rodless", new Rodless());
        scenarioMap.putIfAbsent("bowless", new Bowless());
        scenarioMap.putIfAbsent("gheads", new GHeads());
    }

    public List<IScenario> getActiveScenarios() {
        List<IScenario> scenarios = new ArrayList<>();
        scenarioMap.forEach((str, scenario) -> {
            if (scenario.enabled)
                scenarios.add(scenario);
        });
        return scenarios;
    }

    public List<String> getActiveScenariosNames() {
        List<String> scenarios = new ArrayList<>();
        scenarioMap.forEach((str, scenario) -> {
            if (scenario.enabled)
                scenarios.add(scenario.getClass().getSimpleName());
        });
        return scenarios;
    }
}