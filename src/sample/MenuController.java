package sample;

import com.jfoenix.controls.JFXToggleButton;
import graphEngine.algos.Dijsktra;
import graphEngine.algos.Kruskal;
import graphEngine.algos.Prim;
import graphEngine.context.Context;
import graphEngine.graph.DirectedGraph;
import graphEngine.graph.TreeMapGraph;
import graphEngine.graph.UndirectedGraph;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.StrokeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class MenuController implements Initializable {
    @FXML
    private Group paneGroup;
    @FXML
    private Button backButton, clearButton;
    @FXML
    private JFXToggleButton primButton, kruButton, dijkButton, addEdgeButton, addNodeButton;
    @FXML
    private Line edgeLine;
    @FXML
    private ArrowGraph arrow;
    @FXML
    private Pane viewer;
    @FXML
    private Label weight;

    boolean addNode = true, addEdge = false;

    // List node graph
    List<NodeFX> nodes = new ArrayList<>();
    // Context
    Context context;

    private final boolean direct = MainController.directed;
    private final boolean undirect = MainController.undirected;

    public TreeMapGraph makeGraph(){
        if(direct) return new DirectedGraph();
        return new UndirectedGraph();
    }

    public void initContext(){
        context = new Context();
        context.setGraph(makeGraph());
        context.edgefx = new ArrayList<>();
    }

    int nNode = 0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        primButton.setDisable(true);
        kruButton.setDisable(true);
        dijkButton.setDisable(true);
        addEdgeButton.setDisable(true);
        addNodeButton.setSelected(true);
        addNodeButton.setDisable(false);
        clearButton.setDisable(true);
        initContext();
        System.out.println("Init success");
    }

    /**
     * Handle events for mouse clicks.
     * @param ev
     */
    @FXML
    public void handle(MouseEvent ev){
        if (ev.getEventType() == MouseEvent.MOUSE_RELEASED && ev.getButton() == MouseButton.PRIMARY) {
            NodeFX circle = new NodeFX(ev.getX(), ev.getY(), 1.5, String.valueOf(nNode));
            if(addNode) {
                System.out.print("Add Node: ");
                paneGroup.getChildren().add(circle);
                paneGroup.getChildren().add(circle.id);
                nodes.add(circle);
                System.out.print("X=" + circle.getCenterX() + ", ");
                System.out.print("Y=" + circle.getCenterY() + "\n");
                circle.setOnMousePressed(mouseEventEventHandler);
                circle.setOnMouseReleased(mouseEventEventHandler);
                //Change size circle
                ScaleTransition tr = new ScaleTransition(Duration.millis(100), circle);
                tr.setByX(7f);
                tr.setByY(7f);
                tr.setInterpolator(Interpolator.EASE_OUT);
                tr.play();
                //Add node on tree graph
                context.graph.addVertex(nNode);
                //Handle button
                nNode++;
                if(nNode >= 1){
                    clearButton.setDisable(false);
                }
                if (nNode >= 2) {
                    addEdgeButton.setDisable(false);
                    dijkButton.setDisable(false);
                    if (undirect) {
                        kruButton.setDisable(false);
                        primButton.setDisable(false);
                    }
                }
            }
        }
    }

    //Check exits edge
    boolean checkEdge(NodeFX source, NodeFX target){
        return context.graph.getAdjacentMap().get(Integer.valueOf(source.node.name)).containsKey(Integer.valueOf(target.node.name));
    }

    //Add edge
    NodeFX selectedNode = null;
    EdgeGraph temp = null;
    EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            NodeFX circle = (NodeFX) event.getSource();
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.getButton() == MouseButton.PRIMARY) {
                if(!circle.isSelected) {
                    if (selectedNode != null) {
                        if(addEdge && !checkEdge(selectedNode,circle)){
                            weight = new Label();
                            System.out.println("Adding Edge");
                            if(undirect){
                                //Add weight
                                TextInputDialog dialog = new TextInputDialog();
                                dialog.setTitle(null);
                                dialog.setHeaderText("Add weight :");
                                dialog.setContentText(null);
                                Optional<String> result = dialog.showAndWait();
                                if (result.isPresent()) {
                                    // Set up visual edge
                                    weight.setText(result.get());
                                    weight.setLayoutX(((selectedNode.point.x) + (circle.point.x)) / 2);
                                    weight.setLayoutY(((selectedNode.point.y) + (circle.point.y)) / 2);
                                    paneGroup.getChildren().add(weight);

                                    edgeLine = new Line(selectedNode.point.x, selectedNode.point.y, circle.point.x, circle.point.y);
                                    edgeLine.setId("Line");
                                    edgeLine.setStyle("-fx-stroke-width: 4; -fx-opacity: 0.6;");
                                    paneGroup.getChildren().add(edgeLine);
                                    /*
                                    //Add edge to arraylist
                                    temp = new EdgeGraph(selectedNode.node, circle.node, Integer.valueOf(weight.getText()), edgeLine, weight);
                                    selectedNode.node.adjacents.add(new EdgeGraph(selectedNode.node, circle.node, Double.valueOf(weight.getText()), edgeLine, weight));
                                    circle.node.adjacents.add(new EdgeGraph(circle.node, selectedNode.node, Double.valueOf(weight.getText()), edgeLine, weight));
                                    edges.add(selectedNode.node.adjacents.get(selectedNode.node.adjacents.size() - 1));
                                    edges.add(circle.node.adjacents.get(circle.node.adjacents.size() - 1));
                                    // ^
                                     */
                                    temp = new EdgeGraph(selectedNode.node, circle.node, Integer.valueOf(weight.getText()), edgeLine, weight);
                                    context.edgefx.add(temp);
                                    context.graph.addEdge(Integer.valueOf(selectedNode.node.name), Integer.valueOf(circle.node.name), Integer.valueOf(weight.getText()));
                                    // to check treemap on console
                                    context.graph.print();
                                }
                            }else if(direct){
                                // Prompt weight
                                TextInputDialog dialog = new TextInputDialog();
                                dialog.setTitle(null);
                                dialog.setHeaderText("Add weight :");
                                dialog.setContentText(null);
                                Optional<String> result = dialog.showAndWait();
                                if (result.isPresent()) {
                                    // Set up visual edge
                                    weight.setText(result.get());
                                    weight.setLayoutX(((selectedNode.point.x) + (circle.point.x)) / 2);
                                    weight.setLayoutY(((selectedNode.point.y) + (circle.point.y)) / 2);
                                    paneGroup.getChildren().add(weight);

                                    arrow = new ArrowGraph(selectedNode.point.x, selectedNode.point.y, circle.point.x, circle.point.y);
                                    arrow.setId("arrow");
                                    arrow.setStyle("-fx-stroke-width: 4; -fx-opacity: 0.6;");
                                    paneGroup.getChildren().add(arrow);

                                    //Add edge to arraylist
                                    context.graph.addEdge(Integer.valueOf(selectedNode.node.name), Integer.valueOf(circle.node.name), Integer.valueOf(weight.getText()));
                                    temp = new EdgeGraph(selectedNode.node, circle.node, Double.valueOf(weight.getText()), arrow, weight);
                                    //edges.add(temp);
                                    context.edgefx.add(temp);
                                    //To check treemap on console
                                    context.graph.print();
                                }
                            }
                            if (addNode || addEdge) {
                                selectedNode.isSelected = false;
                                FillTransition ft1 = new FillTransition(Duration.millis(300), selectedNode, Color.RED, Color.GRAY);
                                ft1.play();
                            }
                            selectedNode = null;
                            return;
                        }
                    }
                    circle.isSelected = true;
                    selectedNode = circle;
                    for (NodeFX n : nodes) {
                        n.isSelected = false;
                        FillTransition ft1 = new FillTransition(Duration.millis(300), n);
                        ft1.setToValue(Color.GRAY);
                        ft1.play();
                    }
                }else{
                    circle.isSelected = false;
                    FillTransition ft1 = new FillTransition(Duration.millis(300), circle, Color.RED, Color.GRAY);
                    ft1.play();
                    selectedNode = null;
                }
            }
            FillTransition ft = new FillTransition(Duration.millis(300), circle, Color.GRAY, Color.RED);
            ft.play();

        }
    };

    //Create NodeGraph(id)
    public class NodeFX extends Circle {
        NodeGraph node;
        Label id;
        Point point;
        Boolean isSelected = false;
        public NodeFX(double x, double y, double rad, String name) {
            super(x, y, rad);
            point = new Point((int) x, (int) y);
            node = new NodeGraph(name,this);
            setFill(Color.GRAY);
            id = new Label(name);
            id.setLayoutX(x - 5);
            id.setLayoutY(y - 35);
        }
    }

    //Handle clear button
    @FXML
    public void clearClick(ActionEvent event){
        System.out.println("Clear");
        paneGroup.getChildren().clear();
        paneGroup.getChildren().addAll(viewer);
        //nodes = new ArrayList<>();
        addNode = true;
        addEdge = false;
        selectedNode = null;
        addEdgeButton.setDisable(true);
        addEdgeButton.setSelected(false);
        addNodeButton.setDisable(false);
        addNodeButton.setSelected(true);
        primButton.setDisable(true);
        kruButton.setDisable(true);
        dijkButton.setDisable(true);
        nNode = 0;
        context.setGraph(makeGraph());
    }
    //Handle back button
    @FXML
    public void backClick(ActionEvent actionEvent){
        /*Stage newHome = new Stage();
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        newHome.setScene(new Scene(anchorPane));
        newHome.show();*/
        MainController.menu.hide();
        Main.home.show();
    }
    //Handle addEdge button
    @FXML
    public void addEdgeHandle() {
        addNode = false;
        addEdge = true;
        addNodeButton.setSelected(false);
        addEdgeButton.setSelected(true);
    }

    //Handle addNode button
    @FXML
    public void addNodeHandle() {
        selectedNode = null;
        addNode = true;
        addEdge = false;
        addNodeButton.setSelected(true);
        addEdgeButton.setSelected(false);
    }

    // Handle Prim
    @FXML
    public void PrimActivate(){
        ClearColor();
        addNode = false;
        addEdge = false;
        selectedNode = null;
        primButton.setDisable(false);
        context.setAlgo(new Prim());
        context.execute();
    }

    // Handle Kruskal
    @FXML
    public void KruskalActivate(){
        ClearColor();
        addNode = false;
        addEdge = false;
        selectedNode = null;
        kruButton.setDisable(false);
        context.setAlgo(new Kruskal());
        context.execute();
    }

    // Handle Dijkstra
    @FXML
    public void dijsktraActivate(){
        ClearColor();
        addNode = false;
        addEdge = false;
        selectedNode = null;
        dijkButton.setDisable(false);
        context.setAlgo(new Dijsktra());
        context.execute();
    }

    //clearColor ---> SET TO BUTTON
    public void ClearColor(){
        for (EdgeGraph eg: context.edgefx){
            FillTransition ft1 = new FillTransition(Duration.millis(50), eg.s1.circle);
            ft1.setToValue(Color.GRAY);
            ft1.play();

            FillTransition ft2 = new FillTransition(Duration.millis(50), eg.s2.circle);
            ft2.setToValue(Color.GRAY);
            ft2.play();

            StrokeTransition ftEdge = new StrokeTransition(Duration.millis(50), eg.line);
            ftEdge.setToValue(Color.GRAY);
            ftEdge.play();
        }
    }
}
