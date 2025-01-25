package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationContext;
import agh.ics.oop.SimulationEngine;
import agh.ics.oop.listener.MapChangeListener;
import agh.ics.oop.listener.SimulationFinishedListener;
import agh.ics.oop.model.Boundary;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.configuration.Configuration;
import agh.ics.oop.model.elements.Animal;
import agh.ics.oop.model.elements.Plant;
import agh.ics.oop.model.elements.WorldElement;
import agh.ics.oop.model.event.EventType;
import agh.ics.oop.model.event.MapChangedEvent;
import agh.ics.oop.model.exceptions.PresenterHasNoConfigurationException;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.map.simulation.SimulationWorldMap;
import agh.ics.oop.model.repository.CsvStatisticsRepositoryAdapter;
import agh.ics.oop.model.repository.StatisticsRepositoryPort;
import agh.ics.oop.presenter.components.AnimalComponent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Setter;

import java.io.IOException;
import java.util.stream.Collectors;

public class SimulationPresenter implements MapChangeListener, SimulationFinishedListener, PositionClickHandler {
    private static final int GRID_SIZE = 20;


    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private VBox statisticsContainer;
    @FXML
    private Label currentDayLabel;
    @FXML
    private GridPane mapGrid;
    @FXML
    private Button startStopButton;

    @FXML
    private Button highlightAnimalsWithMostPopularGenotypeButton;

    //    Stats
    @FXML
    private Label animalCountLabel;
    @FXML
    private Label plantCountLabel;
    @FXML
    private Label freeFieldsLabel;
    @FXML
    private Label popularGenotypeLabel;
    @FXML
    private Label avgEnergyLabel;
    @FXML
    private Label avgLifespanLabel;
    @FXML
    private Label avgChildrenLabel;

    private SimulationWorldMap worldMap;
    @Setter
    private Configuration configuration;
    private SimulationEngine simulationEngine;
    private SimulationContext simulationContext;
    private StatisticsRepositoryPort statisticsRepository;
    private Animal selectedAnimal;
    private AnimalStatisticsView animalStatisticsViewController;
    private AnimalListView animalListViewController;
    private boolean shouldHighlightAnimalsWithMostPopularGenotype = false;

    private double scaleFactor = 1.0;
    private double initialX;
    private double initialY;
    private boolean isRunning;


    @FXML
    void initialize() {
        System.out.println("initialize()");
        setGridOnScrollEvent();
        onGridDrag();
    }

    public void drawMap() {
        var mapBoundary = worldMap.getCurrentBounds();
        clearGrid();
        fillGrid(mapBoundary);
        drawElements();
    }

    @Override
    public void mapChanged(WorldMap worldMap, MapChangedEvent event) {
        if (event.getEventType() == EventType.DAY_ENDS) {
            Platform.runLater(this::drawMap);
            Platform.runLater(this::updateStatisticsDisplay);
            saveStatistics();
        }
    }

    @Override
    public void onSimulationFinished() {
        closeRepositoryPort();
        isRunning = false;
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource("simulation_summary_view.fxml"));
                Parent finishedView = loader.load();
                mainBorderPane.setCenter(finishedView);
                mainBorderPane.setLeft(null);

                SimulationSummaryPresenter presenter = loader.getController();
                presenter.setGraphData(simulationContext.getGraphData());
            } catch (IOException e) {
                System.out.println("Couldn't load simulation summary view, e=" + e.getMessage());
            }
        });
    }

    public void onSimulationStartClicked() {
        if (configuration == null) {
            throw new PresenterHasNoConfigurationException("Presenter has no configuration!");
        }

        isRunning = true;

        var simulationContext = new SimulationContext(configuration);
        worldMap = simulationContext.getWorldMap();
        simulationContext.addMapChangedListener(this);
        this.simulationContext = simulationContext;
        this.statisticsRepository = new CsvStatisticsRepositoryAdapter();
//        simulationContext.addMapChangedListener(new LoggerListener());

        this.simulationContext.addSimulationFinishedListener(this);
        var simulation = new Simulation(simulationContext, configuration.getSimulationConfiguration().getDaysCount());


        simulationEngine = new SimulationEngine(simulation);
        simulationEngine.runAsyncInThreadPool();

        startStopButton.setText("Stop");
        startStopButton.setOnAction(event -> stopSimulation());
    }

    public void endSimulation() {
        if (simulationEngine != null) {
            simulationEngine.stopAll();
        }
    }

    public void selectAnimal(Animal animal) {
        this.selectedAnimal = animal;
        Platform.runLater(this::drawMap);
    }


    private void stopSimulation() {
        startStopButton.setText("Start");
        startStopButton.setOnAction(event -> resumeSimulation());
        simulationEngine.pauseAll();
        isRunning = false;
    }

    private void resumeSimulation() {
        startStopButton.setText("Stop");
        simulationEngine.resumeAll();
        startStopButton.setOnAction(event -> stopSimulation());
        isRunning = true;
    }

    void displayAnimalStatistics() {
        Platform.runLater(() -> {
            try {
                startStopButton.setDisable(false);
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource("animal_statistics_view.fxml"));
                Parent animalsView = loader.load();

                animalStatisticsViewController = loader.getController();
                animalStatisticsViewController.setAnimal(selectedAnimal);
                animalStatisticsViewController.updateLabels(simulationContext.getStatistics().getCurrentDay());
                mainBorderPane.setRight(animalsView);
            } catch (IOException e) {
                System.out.println("Couldn't load animal statistics view, e=" + e.getMessage());
            }
        });
    }


    @Override
    public void onPositionClick(Vector2d position) {
        if (isRunning) {
            return;
        }

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource("animals_list_view.fxml"));
                Parent animalsView = loader.load();
                animalListViewController = loader.getController();
                animalListViewController.setPresenter(this);
                animalListViewController.setAnimals(worldMap.getAnimals()
                        .stream()
                        .filter(animal -> animal.getPosition() == position)
                        .collect(Collectors.toSet()));
                mainBorderPane.setRight(animalsView);
            } catch (IOException e) {
                System.out.println("Couldn't load animal statistics view, e=" + e.getMessage());
            }

            selectAnimal(null);
            startStopButton.setDisable(true);
        });
    }

    void onCancelSelectingAnimal() {
        mainBorderPane.setRight(null);
        selectedAnimal = null;
        startStopButton.setDisable(false);
    }

    private void clearGrid() {
        if (mapGrid.getChildren() != null) {
            mapGrid.getChildren().retainAll(mapGrid.getChildren().getFirst());
        }

        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }


    private void fillGrid(Boundary mapBoundary) {
        int width = calculateGridWidth(mapBoundary.leftBottomCorner(), mapBoundary.rightTopCorner());
        int height = calculateGridHeight(mapBoundary.leftBottomCorner(), mapBoundary.rightTopCorner());

        for (int i = 0; i <= width; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(GRID_SIZE));
        }

        for (int i = 0; i <= height; i++) {
            mapGrid.getRowConstraints().add(new RowConstraints(GRID_SIZE));
        }

    }

    private void drawElements() {
        worldMap.getElements().stream()
                .collect(Collectors.groupingBy(WorldElement::getPosition))
                .forEach((position, elements) -> {
                    int x = position.getX();
                    int y = position.getY();

                    StackPane positionContainer = new StackPane();

                    for (WorldElement element : elements) {
                        if (element instanceof Plant) {
                            positionContainer.getChildren().add(createPlantRectangle());
                        } else if (element instanceof Animal animal) {
                            positionContainer.getChildren().add(createAnimalComponent(animal));
                        } else {
                            positionContainer.getChildren().add(createFireRectangle());
                        }
                    }
                    mapGrid.add(positionContainer, x, y);
                });
    }

    private int calculateGridWidth(Vector2d leftBot, Vector2d rightTop) {
        return Math.abs(leftBot.subtract(rightTop).getX());
    }

    private int calculateGridHeight(Vector2d leftBot, Vector2d rightTop) {
        return Math.abs(leftBot.subtract(rightTop).getY());
    }

    private void updateStatisticsDisplay() {
        var statistics = simulationContext.getStatistics();
        animalCountLabel.setText(String.valueOf(statistics.getAnimalCount()));
        plantCountLabel.setText(String.format("%d", statistics.getPlantCount()));
        freeFieldsLabel.setText(String.format("%d", statistics.getFreeFieldsCount()));
        avgEnergyLabel.setText(String.format("%.2f", statistics.getAverageEnergy()));
        var mostPopularGenotype = statistics.getMostPopularGenotype();
        if (mostPopularGenotype != null && !mostPopularGenotype.isEmpty()) {
            popularGenotypeLabel.setText(mostPopularGenotype.toString());
        } else {
            popularGenotypeLabel.setText("No animals");
        }
        avgLifespanLabel.setText(String.format("%.2f", statistics.getAverageLifespan()));
        avgChildrenLabel.setText(String.format("%.2f", statistics.getAverageChildren()));
        currentDayLabel.setText(String.format("%d", statistics.getCurrentDay()));
        if (animalStatisticsViewController != null) {
            animalStatisticsViewController.updateLabels(statistics.getCurrentDay());
        }
    }

    private void saveStatistics() {
        if (configuration.getSimulationConfiguration().isSaveStatisticsCsv()) {
            statisticsRepository.save(simulationContext.getStatistics(), worldMap.getId().toString());
        }
    }

    private void closeRepositoryPort() {
        if (configuration.getSimulationConfiguration().isSaveStatisticsCsv()) {
            try {
                statisticsRepository.close();
            } catch (IOException e) {
                System.out.println("Error closing statistics repository");
            }
        }
    }

    private void setGridOnScrollEvent() {
        mapGrid.setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY();
            double zoomFactor = 1.1;

            if (delta < 0) {
                zoomFactor = 0.9;
            }

            scaleFactor *= zoomFactor;

            mapGrid.setScaleX(scaleFactor);
            mapGrid.setScaleY(scaleFactor);
        });
    }

    private void onGridDrag() {
        mapGrid.setOnMousePressed(event -> {
            initialX = event.getSceneX();
            initialY = event.getSceneY();
            mapGrid.setCursor(Cursor.CLOSED_HAND);

        });

        mapGrid.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - initialX;
            double deltaY = event.getSceneY() - initialY;
            double newTranslateX = mapGrid.getTranslateX() + deltaX;
            double newTranslateY = mapGrid.getTranslateY() + deltaY;

            mapGrid.setTranslateX(newTranslateX);
            mapGrid.setTranslateY(newTranslateY);

            initialX = event.getSceneX();
            initialY = event.getSceneY();
        });

        mapGrid.setOnMouseReleased(event -> mapGrid.setCursor(Cursor.DEFAULT));
    }

    public void onHighlightAnimalsWithMostPopularGenotypeClicked() {
        shouldHighlightAnimalsWithMostPopularGenotype = !shouldHighlightAnimalsWithMostPopularGenotype;
        if (shouldHighlightAnimalsWithMostPopularGenotype) {
            highlightAnimalsWithMostPopularGenotypeButton.setText("Disable Highlighting Popular Genotype");
        } else {
            highlightAnimalsWithMostPopularGenotypeButton.setText("Enable Highlighting Popular Genotype");
        }
    }


    private Rectangle createPlantRectangle() {
        var rectangle = new Rectangle(GRID_SIZE, GRID_SIZE);
        rectangle.setFill(Color.LIGHTGREEN);
        return rectangle;
    }


    private Rectangle createFireRectangle() {
        var rectangle = new Rectangle(GRID_SIZE, GRID_SIZE);
        rectangle.setFill(Color.RED);
        return rectangle;
    }

    private AnimalComponent createAnimalComponent(Animal animal) {
        boolean isSelected = animal.equals(selectedAnimal);
        boolean isHighlighted = simulationContext.getStatistics().getMostPopularGenotype().equals(animal.getGenome().getGens()) && shouldHighlightAnimalsWithMostPopularGenotype;
        return new AnimalComponent(animal, isSelected, GRID_SIZE, isHighlighted, this);
    }


}
