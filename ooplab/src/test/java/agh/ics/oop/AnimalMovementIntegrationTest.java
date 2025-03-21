package agh.ics.oop;

import agh.ics.oop.model.MapDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.elements.Animal;
import agh.ics.oop.model.exceptions.IncorrectPositionException;
import agh.ics.oop.model.map.rectangular.RectangularMap;
import agh.ics.oop.model.move.MoveDirection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class AnimalMovementIntegrationTest {
    private RectangularMap map;

    @BeforeEach
    void setup() {
        map = new RectangularMap(5, 5);
    }

    @Test
    void shouldChangeAnimalsOrientationToNorth() {
        //given
        String[] args = new String[]{"l", "r", "l", "r", "l", "r", "l", "r"};
        var animalPositions = List.of(new Vector2d(2, 2), new Vector2d(0, 0));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(8, moveDirections.size());
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(3));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(4));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(5));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(6));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(7));

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.NORTH, firstAnimal.getOrientation());
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.NORTH, secondAnimal.getOrientation());
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    @Test
    void shouldChangeAnimalsOrientationToSouth() {
        //given
        String[] args = new String[]{"l", "r", "l", "r"};
        var animalPositions = List.of(new Vector2d(2, 2), new Vector2d(0, 0));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(4, moveDirections.size());
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(3));

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.SOUTH, firstAnimal.getOrientation());
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.SOUTH, secondAnimal.getOrientation());
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    @Test
    void shouldChangeAnimalsOrientationToWest() {
        //given
        String[] args = new String[]{"l", "r", "f", "r", "f", "r"};
        var animalPositions = List.of(new Vector2d(2, 2), new Vector2d(0, 0));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(6, moveDirections.size());
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(3));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(4));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(5));

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.WEST, firstAnimal.getOrientation());
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.WEST, secondAnimal.getOrientation());
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    @Test
    void shouldChangeAnimalsOrientationToEast() {
        //given
        String[] args = new String[]{"r", "l", "f", "l", "f", "l"};
        var animalPositions = List.of(new Vector2d(2, 2), new Vector2d(0, 0));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(6, moveDirections.size());
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(3));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(4));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(5));

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.EAST, firstAnimal.getOrientation());
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.EAST, secondAnimal.getOrientation());
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    @Test
    void shouldNotChangeXWhenTryingMoveOutsideMap() {
        //given
        String[] args = new String[]{"l", "r", "f", "f", "f", "f"};
        var animalPositions = List.of(new Vector2d(0, 0), new Vector2d(4, 4));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(6, moveDirections.size());
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(3));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(4));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(5));

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.WEST, firstAnimal.getOrientation());
        Assertions.assertTrue(firstAnimal.isAt(new Vector2d(0, 0)));
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.EAST, secondAnimal.getOrientation());
        Assertions.assertTrue(secondAnimal.isAt(new Vector2d(4, 4)));
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    @Test
    void shouldNotChangeYWhenTryingMoveOutsideMap() {
        //given
        String[] args = new String[]{"f", "b", "f", "b"};
        var animalPositions = List.of(new Vector2d(4, 4), new Vector2d(0, 0));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(4, moveDirections.size());
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.get(3));

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.NORTH, firstAnimal.getOrientation());
        Assertions.assertTrue(firstAnimal.isAt(new Vector2d(4, 4)));
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.NORTH, secondAnimal.getOrientation());
        Assertions.assertTrue(secondAnimal.isAt(new Vector2d(0, 0)));
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    @Test
    void shouldDecreaseAnimalsX() {
        //given
        String[] args = new String[]{"l", "r", "f", "b", "f", "b"};
        var animalPositions = List.of(new Vector2d(2, 2), new Vector2d(2, 3));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(6, moveDirections.size());
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.get(3));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(4));
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.get(5));

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.WEST, firstAnimal.getOrientation());
        Assertions.assertTrue(firstAnimal.isAt(new Vector2d(0, 2)));
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.EAST, secondAnimal.getOrientation());
        Assertions.assertTrue(secondAnimal.isAt(new Vector2d(0, 3)));
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    @Test
    void shouldIncreaseAnimalsX() {
        //given
        String[] args = new String[]{"r", "l", "f", "b", "f", "b"};
        var animalPositions = List.of(new Vector2d(2, 2), new Vector2d(2, 3));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);
        //then
        Assertions.assertEquals(6, moveDirections.size());
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.get(3));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(4));
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.get(5));

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.EAST, firstAnimal.getOrientation());
        Assertions.assertTrue(firstAnimal.isAt(new Vector2d(4, 2)));
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.WEST, secondAnimal.getOrientation());
        Assertions.assertTrue(secondAnimal.isAt(new Vector2d(4, 3)));
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    @Test
    void shouldIncreaseAnimalsY() {
        //given
        String[] args = new String[]{"f", "r", "f", "r", "f", "b"};
        var animalPositions = List.of(new Vector2d(2, 2), new Vector2d(3, 2));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(6, moveDirections.size());
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(3));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(4));
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.get(5));

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.NORTH, firstAnimal.getOrientation());
        Assertions.assertTrue(firstAnimal.isAt(new Vector2d(2, 4)));
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.SOUTH, secondAnimal.getOrientation());
        Assertions.assertTrue(secondAnimal.isAt(new Vector2d(3, 3)));
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    @Test
    void shouldDecreaseAnimalsY() {
        //given
        String[] args = new String[]{"b", "l", "b", "l", "b", "f"};
        var animalPositions = List.of(new Vector2d(2, 2), new Vector2d(3, 2));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(6, moveDirections.size());
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(3));
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.get(4));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(5));

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.NORTH, firstAnimal.getOrientation());
        Assertions.assertTrue(firstAnimal.isAt(new Vector2d(2, 0)));
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.SOUTH, secondAnimal.getOrientation());
        Assertions.assertTrue(secondAnimal.isAt(new Vector2d(3, 1)));
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    @Test
    void shouldReturnToTheSamePosition() {
        //given
        String[] args = new String[]{"f", "r", "f", "l", "f", "l", "f", "f", "l", "f", "f", "f", "l", "f", "l", "f"};
        var animalPositions = List.of(new Vector2d(2, 2));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(16, moveDirections.size());
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(0));
        Assertions.assertEquals(MoveDirection.RIGHT, moveDirections.get(1));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(2));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(3));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(4));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(5));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(6));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(7));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(8));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(9));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(10));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(11));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(12));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(13));
        Assertions.assertEquals(MoveDirection.LEFT, moveDirections.get(14));
        Assertions.assertEquals(MoveDirection.FORWARD, moveDirections.get(15));

        var animal = animals.getFirst();
        Assertions.assertEquals(MapDirection.NORTH, animal.getOrientation());
        Assertions.assertTrue(animal.isAt(new Vector2d(2, 2)));
        Assertions.assertTrue(map.objectAt(animal.getPosition()).isPresent());
        Assertions.assertEquals(animal, map.objectAt(animal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(animal.getPosition()));
    }

    @Test
    void shouldNotCreateAnimalAtTheSamePosition() {
        //given
        String[] args = new String[]{};
        var animalPositions = List.of(new Vector2d(2, 2), new Vector2d(2, 2));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        var animal = animals.getFirst();
        Assertions.assertTrue(map.objectAt(animal.getPosition()).isPresent());
        Assertions.assertEquals(animal, map.objectAt(animal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(animal.getPosition()));
    }

    @Test
    void shouldNotMoveWhenTryToMoveAtOccupied() {
        //given
        String[] args = new String[]{"b"};
        var animalPositions = List.of(new Vector2d(2, 3), new Vector2d(2, 2));
        var animals = createAndPlaceAnimals(animalPositions);
        var moveDirections = OptionsParser.parse(args);

        //when
        moveAnimals(moveDirections, animals);

        //then
        Assertions.assertEquals(MoveDirection.BACKWARD, moveDirections.getFirst());

        var firstAnimal = animals.getFirst();
        Assertions.assertEquals(MapDirection.NORTH, firstAnimal.getOrientation());
        Assertions.assertEquals(new Vector2d(2, 3), firstAnimal.getPosition());
        Assertions.assertTrue(map.objectAt(firstAnimal.getPosition()).isPresent());
        Assertions.assertEquals(firstAnimal, map.objectAt(firstAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(firstAnimal.getPosition()));

        var secondAnimal = animals.getLast();
        Assertions.assertEquals(MapDirection.NORTH, secondAnimal.getOrientation());
        Assertions.assertEquals(new Vector2d(2, 2), secondAnimal.getPosition());
        Assertions.assertTrue(map.objectAt(secondAnimal.getPosition()).isPresent());
        Assertions.assertEquals(secondAnimal, map.objectAt(secondAnimal.getPosition()).get());
        Assertions.assertTrue(map.isOccupied(secondAnimal.getPosition()));
    }

    private void moveAnimals(List<MoveDirection> directions, List<Animal> animals) {
        var size = animals.size();
        for (int i = 0; i < directions.size(); i++) {
            var index = i % size;
            map.move(animals.get(index), directions.get(i));
        }
    }

    private List<Animal> createAndPlaceAnimals(List<Vector2d> animalsPositions) {
        List<Animal> animals = new ArrayList<>();
        animalsPositions.forEach(position -> {
            var animal = Animal.builder()
                    .orientation(MapDirection.NORTH)
                    .position(position)
                    .build();
            try {
                map.place(animal);
                animals.add(animal);
            } catch (IncorrectPositionException e) {
                System.out.println("createAndPlaceAnimals(), animal not placed: message=" + e.getMessage());
            }

        });
        return animals;
    }

}