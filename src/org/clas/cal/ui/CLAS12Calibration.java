package org.clas.cal.ui;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.clas.cal.EC.ECCal;
import org.clas.cal.EC.ECMeshMaker;
import org.clas.cal.EC.ECMeshView;
import org.clas.cal.tools.*;
import org.root.basic.EmbeddedCanvas;
import org.root.histogram.H1D;

import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;

public class CLAS12Calibration {

	//fx Variables
	//Stage detpanel = new JFXPanel();
	static Scene detScene = null;
	static Group detGroup = null;
	
	//variable to note which detectors are in use
	static AtomicInteger isEC = new AtomicInteger();
	static AtomicInteger isPCAL = new AtomicInteger();
	static AtomicInteger isFTOF = new AtomicInteger();
	
	//current selected objects
	String detID;
	int canTabID;
	
	
	//Swing variables
	JTabbedPane      canvasTabbedPane = new JTabbedPane();
	H1D h1,h2,h3,h4,h5;
	EmbeddedCanvas c1, c2;
	
	//EC Classes
	static ECMeshMaker ECdet = null;
	static ECCal ECcal = null;
	
	
	//constructor
	//init a JFrame for canvases by calling initCan()
	//init a JFrame for fxcomponents by calling initDet()
	public CLAS12Calibration() {
		initDet();
		initCan();	
	}
	
	//junk/dummy method until a detector group is created 
	//and refered to
	public void initHist(){
		h1 = new H1D("1", "1", 100,0.0,1.0);
		h2 = new H1D("2", "2", 100,0.0,2.0);
		h3 = new H1D("3", "3", 100,0.0,3.0);
		h4 = new H1D("4", "4", 100,0.0,4.0);
		h5 = new H1D("5", "5", 100,0.0,5.0);
		
		
	}
	
	//make all initial swing tabs needed/wanted for general purposes
	public void initCan(){
		initHist();
		c1 = new EmbeddedCanvas();
		c1.draw(h1);
		
		c2 = new EmbeddedCanvas();
		c2.divide(2, 2);
		c2.draw(0, h2, "");
		c2.draw(1, h3, "");
		c2.draw(2, h4, "");
		c2.draw(3, h5, "");
		
		
		canvasTabbedPane.addTab("Hist1", c1);
		canvasTabbedPane.addTab("Hist2", c2);
		
		JFrame canframe = new JFrame("Swing");
		canframe.add(canvasTabbedPane);
		canframe.setSize(500, 500);
		canframe.pack();
		canframe.setVisible(true);
	}
	
	//make swing panel to put fx stuff into
	public void initDet(){
		isEC.set(0);
		isPCAL.set(0);
		isFTOF.set(0);
		JFrame frame = new JFrame("JavaFX");
        final JFXPanel fxPanel = new JFXPanel();
        fxPanel.setScene(Detectorpanel(500, 500));
        setListeners();
        frame.add(fxPanel);
        frame.pack();
        frame.setVisible(true);
	}
	
	//create scene for detector components
	//set scene as static detScene with objects in group detGroup
	//eventually can make subgroups with subdetector components
	public static Scene Detectorpanel(double width, double height)
    { 	

    	ContentModel  content      = new ContentModel(width*0.85,height*0.85,200);   
        BorderPane   mainBorderPane = new BorderPane();      
        detGroup = new Group();
        
        BorderPane pane=new BorderPane();
      
        
        FlowPane  toolbar = new FlowPane();
        
        //these button can eventually turn on and off detector plugins
        Button  btnClear = new Button("Clear");
        btnClear.setOnAction(event -> {
        	setAllDetOff();
        });
        
        Button  btnLoadFtof = new Button("FTOF");
        btnLoadFtof.setOnAction(event -> {
        	loadDetector("FTOF");
        	isFTOF.set(1);
        });
        
        Button  btnLoadECpix = new Button("ECpix");
        btnLoadECpix.setOnAction(event -> {
        	if(isEC.get() == 0)
        	{
        		ECdet = new ECMeshMaker();
        		ECcal = new ECCal();
        	}
        	if(isEC.get() >= 0 && isEC.get() < 6)
        		ECdet.addECpix(detGroup,(int)isEC.incrementAndGet());
        	
        });
        
        Button  btnLoadPCALpix = new Button("PCALpix");
        btnLoadPCALpix.setOnAction(event -> {
        	if(isPCAL.get() == 0 && isEC.get() == 0)
        	{
        		ECdet = new ECMeshMaker();
        	}
        	if(isPCAL.get() >= 0 && isPCAL.get() < 6)
        		ECdet.addPCALpix(detGroup,(int)isPCAL.incrementAndGet());
        });
        

        toolbar.getChildren().add(btnClear);
        toolbar.getChildren().add(btnLoadFtof);
        toolbar.getChildren().add(btnLoadECpix);
        toolbar.getChildren().add(btnLoadPCALpix);        
        
        content.setContent(detGroup);
    
        
        content.getSubScene().heightProperty().bind(pane.heightProperty());
        content.getSubScene().widthProperty().bind(pane.widthProperty());
        
        pane.setCenter(content.getSubScene());
        mainBorderPane.setTop(toolbar);

        mainBorderPane.setCenter(pane);

        HBox statusPane = new HBox();
        
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                content.setBackgroundColor(colorPicker.getValue());
            }            
        });
        
        
        statusPane.getChildren().add(colorPicker);
        mainBorderPane.setBottom(statusPane);
        
        detScene = new Scene(mainBorderPane, width,height, true);
        
        detScene.setFill(Color.ALICEBLUE);
        
        
        return detScene;
    	
    }
	
	//turn off all detector mesh objects from scene
	private static void setAllDetOff(){
		//clear detectorview
		detGroup.getChildren().clear();
		
		//clear classes used to calibrate and draw
		ECdet = null;
		ECcal = null;
		
		//set trackers to 0
		//don't do anything yet
		isFTOF.set(0);
		isEC.set(0);
		isPCAL.set(0);
	}
	
	//connect click on detector to canvas
	private final EventHandler<MouseEvent> mouseEventHandler2 = event -> {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            if(event.isPrimaryButtonDown()){
            	PickResult res = event.getPickResult();
            	if (res.getIntersectedNode() instanceof ECMeshView){
            		ECcal.OccupancyTab((ECMeshView) res.getIntersectedNode(), c1);
            		ECcal.Tab2((ECMeshView) res.getIntersectedNode(), c2);
            	}
            }
        } 
    };

    //set listeners for detector specific things
    //for camera movements and things go to ContentModel.java
    private void setListeners(){
        detScene.addEventHandler(MouseEvent.ANY, mouseEventHandler2);
    }

	public static void loadDetector(String detector){
        
        if(detector.compareTo("FTOF")==0){
            MeshStore  store = GeometryLoader.getGeometryGemc();
            for(Map.Entry<String,MeshView> item : store.getMap().entrySet()){
                //item.getValue().setMaterial(mat);
            	detGroup.getChildren().add(item.getValue());
            }
        }
        
    }

	
    public static void main(String[] args) {
    	CLAS12Calibration cal = new CLAS12Calibration();
    	
    	
    }

}
