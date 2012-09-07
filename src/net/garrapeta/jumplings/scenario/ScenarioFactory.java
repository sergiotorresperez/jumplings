package net.garrapeta.jumplings.scenario;

import net.garrapeta.jumplings.JumplingsWorld;

/**
 * Class for creating scenarios
 */
public class ScenarioFactory {

    public enum ScenariosIds {
        ROLLING, NATURE, NATURE_CUBISM, NATURE_OIL
    };

    public static IScenario getScenario(JumplingsWorld world, ScenariosIds scenarioId) {
        IScenario scenario;
        switch (scenarioId) {
        case NATURE:
            scenario = new NatureScenario(world);
            break;
        case NATURE_CUBISM:
            scenario = new NatureCubismScenario(world);
            break;
        case NATURE_OIL:
            scenario = new NatureOilScenario(world);
            break;
        case ROLLING:
            scenario = new RollingScenario(world, new ScenarioFactory.ScenariosIds[]  {ScenariosIds.NATURE, ScenariosIds.NATURE_CUBISM, ScenariosIds.NATURE_OIL});
            break;
        default:
            throw new IllegalArgumentException("Cannot create scenario " + scenarioId);
        }
        return scenario;
    }
}
