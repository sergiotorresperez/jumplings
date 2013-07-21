package com.garrapeta.jumplings.scenario;

import com.garrapeta.jumplings.JumplingsWorld;

/**
 * Class for creating scenarios
 */
public class ScenarioFactory {

    public enum ScenariosIds {
        ROLLING, NATURE, DESERT, JUNGLE, WINTER
    };

    public static IScenario getScenario(JumplingsWorld world, ScenariosIds scenarioId) {
        IScenario scenario;
        switch (scenarioId) {
        case NATURE:
            scenario = new NatureScenario(world);
            break;
        case DESERT:
            scenario = new DesertScenario(world);
            break;
        case JUNGLE:
            scenario = new JungleScenario(world);
            break;
        case WINTER:
            scenario = new WinterScenario(world);
            break;
        case ROLLING:
            scenario = new RollingScenario(world, new ScenarioFactory.ScenariosIds[]  {ScenariosIds.NATURE, ScenariosIds.DESERT, ScenariosIds.WINTER, ScenariosIds.JUNGLE});
            break;
        default:
            throw new IllegalArgumentException("Cannot create scenario " + scenarioId);
        }
        return scenario;
    }
}
