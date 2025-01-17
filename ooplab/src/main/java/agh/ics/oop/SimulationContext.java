package agh.ics.oop;

import agh.ics.oop.factory.AnimalFactory;
import agh.ics.oop.factory.WorldMapFactory;
import agh.ics.oop.model.MapChangeListener;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.configuration.Configuration;
import agh.ics.oop.model.elements.Animal;
import agh.ics.oop.model.exceptions.AnimalNotBirthException;
import agh.ics.oop.model.exceptions.IncorrectPositionException;
import agh.ics.oop.model.map.AbstractWorldMap;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.map.simulation.SimulationWorldMap;
import agh.ics.oop.model.util.RandomPositionGenerator;

import java.util.OptionalDouble;

public class SimulationContext {
    private final Configuration configuration;
    private final AnimalFactory animalFactory;
    private final SimulationWorldMap worldMap;
    private int currentDay;

    public SimulationContext(Configuration configuration) {
        this.configuration = configuration;
        this.animalFactory = new AnimalFactory(configuration.getAnimalConfiguration());
        WorldMapFactory worldMapFactory = new WorldMapFactory(configuration.getWorldMapConfiguration(), this::breedAnimals);
        this.worldMap = worldMapFactory.createWorldMap();
        currentDay = 1;

        initAnimals();
    }

    public void handleDayEnds() {
        System.out.println("Current day: " + currentDay + ", animalsCount=" + getAnimalCount());
        worldMap.handleDayEnds(currentDay);
        currentDay++;
    }


    private Animal breedAnimals(Animal parent1, Animal parent2) {
        var lossCopulateEnergy = configuration.getSimulationConfiguration().getLossCopulateEnergy();
        try {
            var child = animalFactory.birthAnimal(parent1, parent2, 2 * lossCopulateEnergy, currentDay);
            parent1.decreaseEnergy(lossCopulateEnergy);
            parent2.decreaseEnergy(lossCopulateEnergy);
            return child;
        } catch (AnimalNotBirthException e) {
            System.out.println("handleCopulate(), animal could not be born: message=" + e.getMessage());
            return null;
        }
    }


    private void initAnimals() {
        var boundary = worldMap.getCurrentBounds();
        var randomizer = new RandomPositionGenerator(
                configuration.getSimulationConfiguration().getStartAnimalCount(),
                boundary.rightTopCorner().getX(),
                boundary.rightTopCorner().getY());

        for (Vector2d position : randomizer) {
            var animal = animalFactory.createAnimal(position, currentDay);
            try {
                worldMap.place(animal);
            } catch (IncorrectPositionException e) {
                System.out.println("createAnimals(), animal not placed: message=" + e.getMessage());
            }
        }
    }


    public void setMapChangeListener(MapChangeListener listener) {
        ((AbstractWorldMap) worldMap).addListener(listener);
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public int getAnimalCount() {
        return worldMap.getAnimals().size();
    }

    public OptionalDouble getAverageAnimalEnergy() {
        return worldMap.getAnimals().stream()
                .mapToDouble(Animal::getEnergy)
                .average();
    }

    public OptionalDouble getAverageDeadAnimalTimeLife() {
        return worldMap.getAnimals().stream()
                .mapToDouble(animal -> animal.getEndDay() - animal.getStartDay())
                .average();
    }

    public OptionalDouble getAverageAnimalCountOfChildren() {
        return worldMap.getAnimals().stream()
                .mapToInt(Animal::getCountOfChildren)
                .average();
    }

    //TODO: getMostPopularGenotype
}
