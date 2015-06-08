package task;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Created by hallvard on 5/26/15.
 */
public class StateGraphColoring implements Serializable {

    private HashMap<Integer, Vertex> vertices;
    public final List<Edge> EDGES;
    public Vertex lastAssumed;

    public StateGraphColoring(HashMap<Integer, Vertex> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.EDGES = edges;
    }

    public HashMap<Integer, Vertex> getVertices() {
        return vertices;
    }

    public StateGraphColoring deepCopy() {
        HashMap<Integer, Vertex> verteciesCopy = new HashMap<>();
        for( Vertex v : vertices.values())
            verteciesCopy.put(v.ID, v.deepCopy());
        return new StateGraphColoring(verteciesCopy, EDGES);
    }

    public Vertex getLastAssumed() {
        return lastAssumed;
    }

    /**
     * Deduces colors from the previous assumtion, and returns a list of new assumptions.
     * @return
     */
    public ArrayList<StateGraphColoring> deduce() {
        if (lastAssumed == null){
            Integer key = (Integer) vertices.keySet().toArray()[0];
            makeAssumption(key, vertices.get(key).getDomain().get(0));
        }
        HashSet<Vertex> candidates = reduce(lastAssumed);

        for (Vertex v: vertices.values()){
            if (v.getDomainSize() == 0){
                return null;
            }
        }

        int smallest = Integer.MAX_VALUE;
        Vertex current = null;
        for (Vertex candidat: candidates){
            if (candidat.getDomainSize() < smallest){
                current = candidat;
                smallest = candidat.getDomainSize();
            }
        }
        if (current == null){
            return new ArrayList<StateGraphColoring>(Collections.singletonList(this));
        }
        return generateChildState(current);
    }

    private ArrayList<StateGraphColoring> generateChildState(Vertex current) {
        System.out.println("generating child states");
        ArrayList<StateGraphColoring> childStates = new ArrayList<>();
        for (Color color: current.getDomain()){
            StateGraphColoring child = deepCopy();
            child.makeAssumption(current.ID, color);
            childStates.add(child);
        }
        return childStates;
    }

    private void makeAssumption(Integer id, Color color) {
        vertices.get(id).assumeColor(color);
        lastAssumed = vertices.get(id);
    }

    private HashSet<Vertex> reduce(Vertex focal){
        ArrayList<Vertex> singletons = new ArrayList<>();
        HashSet<Vertex> notSingletons = new HashSet<>();
        for (Integer neighbourID: focal.getNeighbors()){
            Vertex neighbour = vertices.get(neighbourID);
            if (neighbour.reduceDomain(focal.getColor())){
                singletons.add(neighbour);
            }else {
                if (neighbour.getDomainSize() > 1){
                    notSingletons.add(neighbour);
                }
            }
        }
        for (Vertex v: singletons){
            notSingletons.addAll(reduce(v));
        }
        return notSingletons;
    }

    public boolean isSolution() {
        for (Vertex v : vertices.values()){
            if (!v.isDomainSingleton()){
                return false;
            }
        }
        return true;
    }

    public double getHeuristic() {
        double score = 0;
        for (Vertex v: vertices.values()){
            score += v.getDomainSize()-1;
        }
        return score;
    }
}
