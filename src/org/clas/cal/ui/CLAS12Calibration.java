package org.clas.cal.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;


import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;

public class CLAS12Calibration {

	//fx Variables
	//Stage detpanel = new JFXPanel();
	static Scene detScene = null;
	static Group detGroup = null;
	
	//variable to note which detectors are in use
	static int isEC = 0,isPCAL = 0,isFTOF = 0;
	
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
	
	
	public CLAS12Calibration() {
		initDet();
		initCan();	
	}
	
	public void initHist(){
		h1 = new H1D("1", "1", 100,0.0,1.0);
		h2 = new H1D("2", "2", 100,0.0,2.0);
		h3 = new H1D("3", "3", 100,0.0,3.0);
		h4 = new H1D("4", "4", 100,0.0,4.0);
		h5 = new H1D("5", "5", 100,0.0,5.0);
		
		
	}
	
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
	
	public void initDet(){
		JFrame frame = new JFrame("JavaFX");
        final JFXPanel fxPanel = new JFXPanel();
        fxPanel.setScene(Detectorpanel(500, 500));
        setListeners();
        frame.add(fxPanel);
        frame.pack();
        frame.setVisible(true);
	}
	
	public static Scene Detectorpanel(double width, double height)
    { 	

    	ContentModel  content      = new ContentModel(width*0.85,height*0.85,200);   
        BorderPane   mainBorderPane = new BorderPane();      
        detGroup = new Group();
        
        BorderPane pane=new BorderPane();
      
        
        FlowPane  toolbar = new FlowPane();
        
        //these button can eventually turn on and off detector plugins
        Button  btnClear = new Button("Clear");
        btnClear.setOnAction(event -> {setAllDetOff();});
        
        Button  btnLoadFtof = new Button("FTOF");
        btnLoadFtof.setOnAction(event -> {loadDetector("FTOF");isFTOF=1;});
        
        //Button  btnLoadEC = new Button("EC");
        //btnLoadEC.setOnAction(event -> {testEC();isEC=1;System.out.println("isEC: " + isEC);});
        
        Button  btnLoadECpix = new Button("ECpix");
        btnLoadECpix.setOnAction(event -> {if(isEC==0){ECdet = new ECMeshMaker();ECdet.addECpix(detGroup);ECcal = new ECCal();isEC=1;}});
        
        Button  btnLoadPCALpix = new Button("PCALpix");
        btnLoadPCALpix.setOnAction(event -> {ECdet = new ECMeshMaker();ECdet.addPCALpix(detGroup);isPCAL=1;});
        

        toolbar.getChildren().add(btnClear);
        toolbar.getChildren().add(btnLoadFtof);
        //toolbar.getChildren().add(btnLoadEC);
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
	
	private static void setAllDetOff(){
		//clear detectorview
		detGroup.getChildren().clear();
		
		//clear classes used to calibrate and draw
		ECdet = null;
		ECcal = null;
		
		//set trackers to 0
		//don't do anything yet
		isFTOF=0;isEC=0;isPCAL=0;
	}
	
	
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

	
/*
    public static void testEC(){
        float c, s, xx, yy, zz;
   
    	//EC inner
    	float[] xA1 = {-288.104f,98.442f,98.442f,-281.548f,91.628f,91.628f};
    	float[] yA1 = {0.0f,-197.976f,197.976f,0.0f,-191.128f,191.128f};
    	float[] zA1 = {18.57f,18.57f,18.57f,0.0f,0.0f,0.0f};
 	
    	for(int i=0; i<6;++i) 
    	{
    		//x1[i] -= 333.1042;
    		zA1[i] += 712.723;
    		
    		s = (float) Math.sin(+0.436332313);
            c = (float) Math.cos(+0.436332313);
            zz = zA1[i];
            zA1[i] = (float) (c*zz - s*xA1[i]);
            xA1[i] = (float) (s*zz + c*xA1[i]);
    	
    	}
    	
    	Prism2Dto3DMesh pixelA = new Prism2Dto3DMesh(6, xA1, yA1, zA1);
        final MeshView rectA = new MeshView(pixelA.getMesh());
        String ECID = String.format("ECID%06d", 1);
        rectA.setId(ECID);
        rectA.setMaterial(new PhongMaterial(Color.DARKGREEN));
        detGroup.getChildren().add(rectA);
        
        
        //EC outer
        float[] xB1 = {-298.5936f,109.344f,109.344f,-288.104f,98.442f,98.442f};
    	float[] yB1 = {0.0f,-208.9325f,208.9325f,0.0f,-197.976f,197.976f};
    	float[] zB1 = {48.282f,48.282f,48.282f,18.57f,18.57f,18.57f};
    	
    	for(int i=0; i<6;++i) 
    	{
    		//x2[i] -= 333.1042;
    		zB1[i] += 712.723;
    		
    		//testp.rotateX(0.3838074126117121);
    		//testp.rotateY(-0.43633231299858166);
    		s = (float) Math.sin(+0.436332313);
            c = (float) Math.cos(+0.436332313);
            zz = zB1[i];
            zB1[i] = (float) (c*zz - s*xB1[i]);
            xB1[i] = (float) (s*zz + c*xB1[i]);
   
    	}
    	
    	Prism2Dto3DMesh pixelB = new Prism2Dto3DMesh(6, xB1, yB1, zB1);
        final MeshView rectB = new MeshView(pixelB.getMesh());
        ECID = String.format("ECID%06d", 0);
        rectB.setId(ECID);
        rectB.setMaterial(new PhongMaterial(Color.DARKRED));
        detGroup.getChildren().add(rectB);
    }
    
    */
	
	
    public static void main(String[] args) {
    	System.out.println("running CLAS12Calibration");
    	CLAS12Calibration cal = new CLAS12Calibration();
    	
    	
    }

}
