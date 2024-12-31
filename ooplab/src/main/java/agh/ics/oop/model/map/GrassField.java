package agh.ics.oop.model.map;

import agh.ics.oop.model.Boundary;
import agh.ics.oop.model.move.MoveDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.map.elements.Animal;
import agh.ics.oop.model.map.elements.Grass;
import agh.ics.oop.model.map.elements.WorldElement;
import agh.ics.oop.model.util.RandomPositionGenerator;

import java.util.*;

public class GrassField extends AbstractWorldMap {
  private final RandomPositionGenerator randomizer;
  private final Map<Vector2d, Grass> grasses;
  private Vector2d displayLeftBotCorner;
  private Vector2d displayRightTopCorner;

  public GrassField(int grassCount) {
    this(new RandomPositionGenerator(grassCount, (int)Math.sqrt(grassCount * 10) + 1, (int)Math.sqrt(grassCount * 10) + 1));
  }

  GrassField(RandomPositionGenerator randomizer) { //constructor for testing purposes
    super();
    this.grasses = new HashMap<>();
    this.randomizer = randomizer;
    placeGrass();
  }

  @Override
  public void move(Animal animal, MoveDirection direction) {
    super.move(animal, direction);
  }

  @Override
  public boolean isOccupied(Vector2d position) {
    return super.isOccupied(position) || grasses.containsKey(position);
  }

  @Override
  public WorldElement objectAt(Vector2d position) {
    var animal = super.objectAt(position);
    return animal != null ? animal : grasses.get(position);
  }

  @Override
  public Collection<WorldElement> getElements() {
    var elements = new ArrayList<WorldElement>();
    elements.addAll(grasses.values());
    elements.addAll(super.getElements());

    return Collections.unmodifiableCollection(elements);
  }

  @Override
  public boolean canMoveTo(Vector2d position) {
    var objectAt = objectAt(position);
    return !(objectAt instanceof Animal);
  }

  public void placeGrass(Grass grass) {
    grasses.put(grass.getPosition(), grass);
  }

  private void placeGrass() {
    randomizer.forEach(position -> {
      var grass = new Grass(position);
      grasses.put(grass.getPosition(), grass);
    });
  }

  @Override
  public Boundary getCurrentBounds() {
    adjustDisplayCorners();
    return new Boundary(displayLeftBotCorner, displayRightTopCorner);
  }

  void adjustDisplayCorners() {
    var botLeft = new Vector2d(Integer.MAX_VALUE, Integer.MAX_VALUE);
    var rightTop = new Vector2d(-Integer.MAX_VALUE, -Integer.MAX_VALUE);

    for (var el : getElements()) {
      botLeft = botLeft.lowerLeft(el.getPosition());
      rightTop = rightTop.upperRight(el.getPosition());
    }
    displayLeftBotCorner = botLeft;
    displayRightTopCorner = rightTop;
  }


}
