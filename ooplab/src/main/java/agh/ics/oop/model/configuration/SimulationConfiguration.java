package agh.ics.oop.model.configuration;

public class SimulationConfiguration {
    private final int daysCount;
    private final int energyGain;
    private final int plantGrowth;
    private final PlantVariant plantVariant;
    private final int wellFedEnergy;
    private final int lossCopulateEnergy;
    private final int startAnimalCount;
    private final int startPlantCount;

    SimulationConfiguration(int daysCount, int energyGain, int plantGrowth, PlantVariant plantVariant,
                            int wellFedEnergy, int lossCopulateEnergy, int startAnimalCount, int startPlantCount) {
        this.daysCount = daysCount;
        this.energyGain = energyGain;
        this.plantGrowth = plantGrowth;
        this.plantVariant = plantVariant;
        this.wellFedEnergy = wellFedEnergy;
        this.lossCopulateEnergy = lossCopulateEnergy;
        this.startAnimalCount = startAnimalCount;
        this.startPlantCount = startPlantCount;
    }

    public int getEnergyGain() {
        return energyGain;
    }


    public int getPlantGrowth() {
        return plantGrowth;
    }


    public PlantVariant getPlantVariant() {
        return plantVariant;
    }

    public int getWellFedEnergy() {
        return wellFedEnergy;
    }


    public int getLossCopulateEnergy() {
        return lossCopulateEnergy;
    }

    public int getStartAnimalCount() {
        return startAnimalCount;
    }


    public int getDaysCount() {
        return daysCount;
    }


    public int getStartPlantCount() {
        return startPlantCount;
    }

    @Override
    public String toString() {
        return "SimulationConfiguration{" +
                "energyGain=" + energyGain +
                ", plantGrowth=" + plantGrowth +
                ", plantVariant=" + plantVariant +
                ", wellFedEnergy=" + wellFedEnergy +
                ", lossCopulateEnergy=" + lossCopulateEnergy +
                ", startAnimalCount=" + startAnimalCount +
                ", startPlantCount=" + startPlantCount +
                '}';
    }
}
