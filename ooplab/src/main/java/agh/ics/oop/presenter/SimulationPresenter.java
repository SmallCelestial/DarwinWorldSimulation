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
import agh.ics.oop.model.event.EventType;
import agh.ics.oop.model.event.MapChangedEvent;
import agh.ics.oop.model.exceptions.PresenterHasNoConfigurationException;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.map.simulation.SimulationWorldMap;
import agh.ics.oop.model.repository.CsvStatisticsRepositoryAdapter;
import agh.ics.oop.model.repository.StatisticsRepositoryPort;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import lombok.Setter;

import java.io.IOException;

public class SimulationPresenter implements MapChangeListener, SimulationFinishedListener {
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
    @FXML
    private Button chooseAnimalButton;

    private SimulationWorldMap worldMap;
    @Setter
    private Configuration configuration;
    private SimulationEngine simulationEngine;
    private SimulationContext simulationContext;
    private StatisticsRepositoryPort statisticsRepository;
    private Animal selectedAnimal;
    private AnimalStatisticsView animalStatisticsViewController;

    private double scaleFactor = 1.0;
    private double initialX;
    private double initialY;


    @FXML
    void initialize() {
        System.out.println("initialize()");

        setGridOnScrollEvent();
        onGridDrag();
    }

    public void drawMap() {
        synchronized (simulationContext) {
            var mapBoundary = worldMap.getCurrentBounds();
            clearGrid();
            fillGrid(mapBoundary);
            drawElements();
        }
    }

    @Override
    public void mapChanged(WorldMap worldMap, MapChangedEvent event) {
        if (event.getEventType() == EventType.DAY_ENDS) {
            Platform.runLater(this::drawMap);
            Platform.runLater(this::updateStatisticsDisplay);
            Platform.runLater(this::saveStatistics);
        }
    }

    @Override
    public void onSimulationFinished() {
        closeRepositoryPort();
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

//        startStopButton.setDisable(true);
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
        drawMap();
    }


    private void stopSimulation() {
        startStopButton.setText("Start");
        startStopButton.setOnAction(event -> resumeSimulation());
        chooseAnimalButton.setDisable(false);
        simulationEngine.pauseAll();
    }

    private void resumeSimulation() {
        startStopButton.setText("Stop");
        simulationEngine.resumeAll();
        chooseAnimalButton.setDisable(true);
        startStopButton.setOnAction(event -> stopSimulation());
        chooseAnimalButton.setText("Choose animal");
        chooseAnimalButton.setOnAction(event -> onChooseAnimalClicked());
    }

    public void onChooseAnimalClicked() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource("animals_list_view.fxml"));
                Parent animalsView = loader.load();


                AnimalsListView controller = loader.getController();
                controller.setPresenter(this);
                controller.setAnimals(worldMap.getAnimals());
                mainBorderPane.setRight(animalsView);
            } catch (IOException e) {
                System.out.println("Couldn't load animal statistics view, e=" + e.getMessage());
            }
            startStopButton.setDisable(true);
            chooseAnimalButton.setText("Confirm");
            chooseAnimalButton.setOnAction(event -> displayAnimalStatistics());
        });
    }

    private void displayAnimalStatistics(){
        Platform.runLater(() -> {
            try {
                startStopButton.setDisable(false);
                chooseAnimalButton.setDisable(true);
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
        worldMap.getElements().forEach(element -> {
            int x = element.getPosition().getX();
            int y = element.getPosition().getY();

            Shape rectangle = new Rectangle(GRID_SIZE, GRID_SIZE);

            if (element instanceof Plant) {
                rectangle.setFill(Color.LIGHTGREEN);
                mapGrid.add(rectangle, x, y);
            } else if (element instanceof Animal animal) {
                var animalDrawing = createAnimalDrawing(animal.getEnergy());

                if (animal.equals(selectedAnimal)) {
                    Shape border = new Rectangle(GRID_SIZE, GRID_SIZE);
                    border.setFill(Color.TRANSPARENT); // Środek przezroczysty
                    border.setStroke(Color.BLUE); // Niebieska ramka
                    border.setStrokeWidth(2.0);

                    StackPane stack = new StackPane(); // Umieść elementy w stosie
                    stack.getChildren().addAll(animalDrawing, border);

                    mapGrid.add(stack, x, y); // Dodaj stos do siatki
                } else {
                    mapGrid.add(animalDrawing, x, y);
                }
            } else {
                rectangle.setFill(Color.RED);
                mapGrid.add(rectangle, x, y);
            }

        });
    }

    private Pane createAnimalDrawing(int animalEnergy) {
        Pane pane = new Pane();
        double centerX = (double) GRID_SIZE / 2;
        double centerY = (double) GRID_SIZE / 2;
        double radius = (double) GRID_SIZE / 2 - 2;
        Circle head = new Circle(centerX, centerY, radius);
        head.setFill(calculateColor(animalEnergy));
        pane.getChildren().add(head);
        return pane;
    }

    private Color calculateColor(int animalEnergy) {
        int maxEnergy = 40;
        animalEnergy = Math.max(0, Math.min(animalEnergy, maxEnergy));
        double energyFactor = (double) animalEnergy / maxEnergy;

        int red = 0;
        int green = (int) (255 * energyFactor);
        int blue = (int) (128 + (127 * energyFactor));

        return Color.rgb(red, green, blue);
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
        if (animalStatisticsViewController != null){
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

}
