package graphEngine.context;
import graphEngine.algos.AbstractAlgo;
import graphEngine.graph.TreeMapGraph;
import sample.EdgeGraph;

import java.util.List;

public class Context {
    public TreeMapGraph graph;
    public AbstractAlgo algo;
    public List<EdgeGraph> edgefx;
    private int count = 0;

    public void setGraph(TreeMapGraph graph){
        this.graph = graph;
    }
    public void setAlgo(AbstractAlgo algo) {
        this.algo = algo;
    }
    public void setCount(int count) { this.count = count; }
    public int getCount() { return count; }

    public void runAuto(){
        algo.setGraph(this.graph);
        algo.setEdgefx(this.edgefx);
        algo.init();
        Thread t = new Thread(algo);
        t.start();
    }

    public void runStepByStep(){
        if (count == 0 ){
            algo.setGraph(this.graph);
            algo.setEdgefx(this.edgefx);
            algo.init();

            EdgeGraph eg = this.algo.getResultEdges().get(count);
            AbstractAlgo.edgeColoring(eg,this.algo.getColor());
        }
        else if (this.count >= algo.getResultEdges().size()){
            System.out.println("Number of step(s) SURPASSED Edges' size! ");
        }
        else{
            EdgeGraph eg = this.algo.getResultEdges().get(count);
            AbstractAlgo.edgeColoring(eg,this.algo.getColor());
        }
    }
}
