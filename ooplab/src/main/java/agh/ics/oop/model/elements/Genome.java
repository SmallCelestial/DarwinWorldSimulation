package agh.ics.oop.model.elements;

import agh.ics.oop.model.configuration.MutationVariant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

public class Genome {
    private final List<Gen> gens;
    private int currentIndex = 0;
    private final MutationVariant mutation;

    public Genome(List<Gen> gens) {
        this(gens, MutationVariant.FULL_RANDOM);
    }

    public Genome(List<Gen> gens, MutationVariant mutation) {
        this.gens = gens;
        this.mutation = mutation;
    }

    //TODO test
    public void mutate(int count) {
        count = Math.min(count, gens.size());

        var indices = new ArrayList<Integer>();
        for (int i = 0; i < gens.size(); i++) {
            indices.add(i);
        }

        Collections.shuffle(indices, RandomGenerator.getDefault());
        for (int i = 0; i < count; i++) {
            gens.get(indices.get(i)).mutate(mutation);
        }
    }


    //TODO test
    public List<Gen> getPartOfGenome(int count, boolean left) {
        count = Math.min(count, gens.size());
        if (left) {
            return gens.subList(0, count);
        }
        return gens.subList(gens.size() - count, gens.size());
    }

    public Gen nextGen() {
        var index = currentIndex % gens.size();
        currentIndex += 1;
        return gens.get(index);
    }

    public Gen getActivatedGen() {
        return gens.get(currentIndex);
    }

}
