package org.clas.cal.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;


import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	
	//variables that should be taken from detector specific classes
	H1D hEC1,hEC2,hEC3,hEC4,hEC5;
	H1D hPCAL1,hPCAL2,hPCAL3,hPCAL4,hPCAL5;
	
	
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
		
		hEC1 = new H1D("EC1", "EC1", 100,0.0,1.0);
		hEC2 = new H1D("EC2", "EC2", 100,0.0,2.0);
		hEC3 = new H1D("EC3", "EC3", 100,0.0,3.0);
		hEC4 = new H1D("EC4", "EC4", 100,0.0,4.0);
		hEC5 = new H1D("EC5", "EC5", 100,0.0,5.0);
		
		hPCAL1 = new H1D("PCAL1", "PCAL1", 100,0.0,1.0);
		hPCAL2 = new H1D("PCAL2", "PCAL2", 100,0.0,2.0);
		hPCAL3 = new H1D("PCAL3", "PCAL3", 100,0.0,3.0);
		hPCAL4 = new H1D("PCAL4", "PCAL4", 100,0.0,4.0);
		hPCAL5 = new H1D("PCAL5", "PCAL5", 100,0.0,5.0);
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
		addChangeListener();
		
		JFrame canframe = new JFrame("Swing");
		canframe.add(canvasTabbedPane);
		canframe.setSize(500, 500);
		canframe.pack();
		canframe.setVisible(true);
	}
	
	public void addChangeListener() {    
	      canvasTabbedPane.addChangeListener(new ChangeListener() {
	         public void stateChanged(ChangeEvent e) {
	         if (e.getSource() instanceof JTabbedPane) {
	           JTabbedPane pane = (JTabbedPane) e.getSource();
	           canTabID = pane.getSelectedIndex();
	           System.out.println(canTabID);
	           //selectedTabName  = (String) pane.getTitleAt(selectedTabIndex);
	         }
	         }
	      });
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
        btnClear.setOnAction(event -> {detGroup.getChildren().clear();isFTOF=0;isEC=0;isPCAL=0;System.out.println("isEC: " + isEC);});
        
        Button  btnLoadFtof = new Button("FTOF");
        btnLoadFtof.setOnAction(event -> {loadDetector("FTOF");isFTOF=1;});
        
        Button  btnLoadEC = new Button("EC");
        btnLoadEC.setOnAction(event -> {testEC();isEC=1;System.out.println("isEC: " + isEC);});
        
        Button  btnLoadECpix = new Button("ECpix");
        btnLoadECpix.setOnAction(event -> {testECpix();isEC=1;});
        
        Button  btnLoadPCALpix = new Button("PCALpix");
        btnLoadPCALpix.setOnAction(event -> {testPCALpix();isPCAL=1;});
        

        toolbar.getChildren().add(btnClear);
        toolbar.getChildren().add(btnLoadFtof);
        toolbar.getChildren().add(btnLoadEC);
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
	
	private final EventHandler<MouseEvent> mouseEventHandler2 = event -> {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            if(event.isPrimaryButtonDown()){
            	PickResult res = event.getPickResult();
            	if (res.getIntersectedNode() instanceof MyMeshView){
            		detID = ((MyMeshView)res.getIntersectedNode()).getId();
            		if(detID.contains("EC"))
            		{
            			//ideally these would be taken from 
            			//detector specific classes
            			//rather than global variables
            			//if class exists and was initiated
            			c1.draw(hEC1);
            			c2.draw(0,hEC2,"");
            			c2.draw(1,hEC3,"");
            			c2.draw(2,hEC4,"");
            			c2.draw(3,hEC5,"");
            			System.out.println(detID);
            			//canvasTabbedPane.getTabComponentAt(canTabID).getParent().getComponent(0).; 	
            			//canvasTabbedPane.repaint();
            		}
            		else if(detID.contains("PCAL"))
            		{
            			//ideally these would be taken from 
            			//detector specific classes
            			//rather than global variables
            			//if class exists and was initiated
            			c1.draw(hPCAL1);
            			c2.draw(0,hPCAL2,"");
            			c2.draw(1,hPCAL3,"");
            			c2.draw(2,hPCAL4,"");
            			c2.draw(3,hPCAL5,"");
            			System.out.println(detID);
            			//canvasTabbedPane.getTabComponentAt(canTabID).getParent().getComponent(0).; 	
            			//canvasTabbedPane.repaint();
            		}
            	}
            }
        } 
    };

    
    private void setListeners(){
        detScene.addEventHandler(MouseEvent.ANY, mouseEventHandler2);
    }

	/*
	private void addButtons(FlowPane toolbar){
		
        Button  btnClear = new Button("Clear");
        btnClear.setOnAction(event -> {detGroup.getChildren().clear();isEC = 0;isPCAL = 0;});
        
        Button  btnLoadFtof = new Button("FTOF");
        btnLoadFtof.setOnAction(event -> {loadDetector("FTOF");});
        
        Button  btnLoadEC = new Button("EC");
        btnLoadEC.setOnAction(event -> {testEC();isEC = 1;});
        
        Button  btnLoadECpix = new Button("ECpix");
        btnLoadECpix.setOnAction(event -> {testECpix();isEC = 1;});
        
        Button  btnLoadPCALpix = new Button("PCALpix");
        btnLoadPCALpix.setOnAction(event -> {testPCALpix();isPCAL = 1;});
        
        //Button  btnClearHits = new Button("Clear Hits");
        //btnClearHits.setOnAction(event -> {clearHits();});
        
        toolbar.getChildren().add(btnClear);
        toolbar.getChildren().add(btnLoadFtof);
        toolbar.getChildren().add(btnLoadEC);
        toolbar.getChildren().add(btnLoadECpix);
        toolbar.getChildren().add(btnLoadPCALpix);        
        //toolbar.getChildren().add(btnClearHits);
        
		
	}
	*/
	
	public static void loadDetector(String detector){
        
        if(detector.compareTo("FTOF")==0){
            MeshStore  store = GeometryLoader.getGeometryGemc();
            for(Map.Entry<String,MeshView> item : store.getMap().entrySet()){
                //item.getValue().setMaterial(mat);
            	detGroup.getChildren().add(item.getValue());
            }
        }
        
    }
	
	public static void testPCALpix()
    {
    	double[][][][][] pcalfront = new double[68][62][62][3][10];
		int[][][] numpoints = new int[68][62][62];
		
		int u, v, w, curpoint;
		Scanner inEcin;
		try 
		{
			inEcin = new Scanner(new File("PCALpixfrontvert.dat"));
			curpoint = 0;
			while(inEcin.hasNextInt())
	    	{
				//point1
				//paddle num
				u = inEcin.nextInt();
				v = inEcin.nextInt();
				w = inEcin.nextInt();
				
				numpoints[u][v][w] = inEcin.nextInt();
				
				//x,y,z
				pcalfront[u][v][w][0][curpoint] = inEcin.nextDouble();
				pcalfront[u][v][w][1][curpoint] = inEcin.nextDouble();
				pcalfront[u][v][w][2][curpoint] = inEcin.nextDouble();
				
				
				++curpoint;
				if(curpoint == numpoints[u][v][w])curpoint = 0;
		
	    	}
		} 
		catch(FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		MeshStore store = new MeshStore();
		float c, s, xx, yy, zz;
		float[] x = new float[20];
    	float[] y = new float[20];
    	float[] z = new float[20];
    	
		for(u = 0; u < 68; u++)
		{
			for(v = 0; v < 62; v++)
			{
				for(w = 0; w < 62; w++)
				{
					//test if valid pixel
					if(Math.abs(pcalfront[u][v][w][1][0]) > 0.00001 || Math.abs(pcalfront[u][v][w][1][1]) > 0.00001  || Math.abs(pcalfront[u][v][w][1][2]) > 0.00001)
					{
						for(int p = 0; p < numpoints[u][v][w]; ++p)
						{
							x[p+numpoints[u][v][w]] = (float) (pcalfront[u][v][w][0][p] - 333.1042);
							x[p] = (float) (pcalfront[u][v][w][0][p] - 333.1042);
							
							y[p+numpoints[u][v][w]] = (float) pcalfront[u][v][w][1][p];
							y[p] = (float) pcalfront[u][v][w][1][p];
							
							z[p+numpoints[u][v][w]] = (float) (pcalfront[u][v][w][2][p] + 697.78);
							z[p] = (float) (pcalfront[u][v][w][2][p] + 697.78 + 14.9);

					
				    		
				    		s = (float) Math.sin(+0.436332313);
				            c = (float) Math.cos(+0.436332313);
				            zz = z[p];
				            z[p] = (float) (c*zz - s*x[p]);
				            x[p] = (float) (s*zz + c*x[p]);
				            
				            /*
				            s = (float) Math.sin(2.094395102);
				            c = (float) Math.cos(2.094395102);
				            xx = x[p];
				            x[p] = c*xx - s*y[p];
				            y[p] = s*xx + c*y[p];
				            */
				            
				            
				            s = (float) Math.sin(+0.436332313);
				            c = (float) Math.cos(+0.436332313);
				            zz = z[p+numpoints[u][v][w]];
				            z[p+numpoints[u][v][w]] = (float) (c*zz - s*x[p+numpoints[u][v][w]]);
				            x[p+numpoints[u][v][w]] = (float) (s*zz + c*x[p+numpoints[u][v][w]]);
				            
				            /*
				            s = (float) Math.sin(2.094395102);
				            c = (float) Math.cos(2.094395102);
				            xx = x[p+numpoints[u][v][w]];
				            x[p+numpoints[u][v][w]] = c*xx - s*y[p+numpoints[u][v][w]];
				            y[p+numpoints[u][v][w]] = s*xx + c*y[p+numpoints[u][v][w]];
				    		*/
				    	}
						
						Prism2Dto3DMesh pixel = new Prism2Dto3DMesh(2*numpoints[u][v][w], x, y, z);
				        MyMeshView rect = new MyMeshView(pixel.getMesh());
				        double alpha = 1.0;
				        int ucolor = 255, vcolor = 255, wcolor = 255;
				        if(u%2 ==0)
				        {
				        	alpha = 1.0;
				        	ucolor = 0;
				        }
				        if(v%2 ==0) vcolor = 0;
				        if(w%2 ==0) wcolor = 0;
				        rect.setMaterial(new PhongMaterial(Color.rgb(ucolor,vcolor,wcolor,alpha)));
				        //store.addMesh(Integer.toString(u*10000+v*100+w), rect,4);
				        String PCALID = String.format("PCALID%06d", u*10000 + v*100 + w);
				        rect.setId(PCALID);
				        //Tooltip t = new Tooltip(PCALID);
				        //Tooltip.install(rect, t);
				        detGroup.getChildren().add(rect);
				        /*
				        for(Map.Entry<String,MeshView> item : store.getMap().entrySet()){
			                //item.getValue().setMaterial(mat);
			                root.getChildren().add(item.getValue());
			            }
			            */
					}
				}
			}
		}
    }
    

    public static void testECpix()
    {
    	double[][][][][] ecinfront = new double[36][36][36][3][3];
		double[][][][][] ecoutfront = new double[36][36][36][3][3];
		double[][][][][] ecback = new double[36][36][36][3][3];
		
		int u, v, w;
		Scanner inEcin;
		try 
		{
			inEcin = new Scanner(new File("ECinpixfrontvert.dat"));
			for(int i = 0; i < 1296; ++i)
	    	{
				//point1
				//paddle num
				u = inEcin.nextInt();
				v = inEcin.nextInt();
				w = inEcin.nextInt();
				
				//x,y,z
				ecinfront[u][v][w][0][0] = inEcin.nextDouble();
				ecinfront[u][v][w][1][0] = inEcin.nextDouble();
				ecinfront[u][v][w][2][0] = inEcin.nextDouble();
				
				//point2
				//paddle num
				u = inEcin.nextInt();
				v = inEcin.nextInt();
				w = inEcin.nextInt();
				
				//x,y,z
				ecinfront[u][v][w][0][1] = inEcin.nextDouble();
				ecinfront[u][v][w][1][1] = inEcin.nextDouble();
				ecinfront[u][v][w][2][1] = inEcin.nextDouble();
				
				
				//point1
				//paddle num
				u = inEcin.nextInt();
				v = inEcin.nextInt();
				w = inEcin.nextInt();
				
				//x,y,z
				ecinfront[u][v][w][0][2] = inEcin.nextDouble();
				ecinfront[u][v][w][1][2] = inEcin.nextDouble();
				ecinfront[u][v][w][2][2] = inEcin.nextDouble();
	    	}
		} 
		catch(FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Scanner inEcout;
		try 
		{
			inEcout = new Scanner(new File("ECpixbackvert.dat"));
			for(int i = 0; i < 1296; ++i)
	    	{
				//point1
				//paddle num
				u = inEcout.nextInt();
				v = inEcout.nextInt();
				w = inEcout.nextInt();
				
				//x,y,z
				ecback[u][v][w][0][0] = inEcout.nextDouble();
				ecback[u][v][w][1][0] = inEcout.nextDouble();
				ecback[u][v][w][2][0] = inEcout.nextDouble();
				
				//point2
				//paddle num
				u = inEcout.nextInt();
				v = inEcout.nextInt();
				w = inEcout.nextInt();
				
				//x,y,z
				ecback[u][v][w][0][1] = inEcout.nextDouble();
				ecback[u][v][w][1][1] = inEcout.nextDouble();
				ecback[u][v][w][2][1] = inEcout.nextDouble();
				
				
				//point1
				//paddle num
				u = inEcout.nextInt();
				v = inEcout.nextInt();
				w = inEcout.nextInt();
				
				//x,y,z
				ecback[u][v][w][0][2] = inEcout.nextDouble();
				ecback[u][v][w][1][2] = inEcout.nextDouble();
				ecback[u][v][w][2][2] = inEcout.nextDouble();
	    	}
		} 
		catch(FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		MeshStore store = new MeshStore();
		float c, s, xx, yy, zz;
		float[] x = {-288.104f,98.442f,98.442f,-281.548f,91.628f,91.628f};
    	float[] y = {0.0f,-197.976f,197.976f,0.0f,-191.128f,191.128f};
    	float[] z = {18.57f,18.57f,18.57f,0.0f,0.0f,0.0f};
    	
		for(u = 0; u < 36; u++)
		{
			for(v = 0; v < 36; v++)
			{
				for(w = 0; w < 36; w++)
				{
					//test if valid pixel
					if(Math.abs(ecinfront[u][v][w][1][0]) > 0.00001 || Math.abs(ecinfront[u][v][w][1][1]) > 0.00001  || Math.abs(ecinfront[u][v][w][1][2]) > 0.00001)
					{
						x[0] = (float) ecback[u][v][w][0][0];
						x[1] = (float) ecback[u][v][w][0][1];
						x[2] = (float) ecback[u][v][w][0][2];
						x[3] = (float) ecinfront[u][v][w][0][0];
						x[4] = (float) ecinfront[u][v][w][0][1];
						x[5] = (float) ecinfront[u][v][w][0][2];
						
						y[0] = (float) ecback[u][v][w][1][0];
						y[1] = (float) ecback[u][v][w][1][1];
						y[2] = (float) ecback[u][v][w][1][2];
						y[3] = (float) ecinfront[u][v][w][1][0];
						y[4] = (float) ecinfront[u][v][w][1][1];
						y[5] = (float) ecinfront[u][v][w][1][2];
						
						
						z[0] = (float) ecback[u][v][w][2][0];
						z[1] = (float) ecback[u][v][w][2][1];
						z[2] = (float) ecback[u][v][w][2][2];
						z[3] = (float) ecinfront[u][v][w][2][0];
						z[4] = (float) ecinfront[u][v][w][2][1];
						z[5] = (float) ecinfront[u][v][w][2][2];
						//six vetices
						for(int i=0; i<6;++i) 
				    	{
				    		x[i] -= 333.1042;
				    		z[i] += 712.723;
				    		
				    		s = (float) Math.sin(+0.436332313);
				            c = (float) Math.cos(+0.436332313);
				            zz = z[i];
				            z[i] = (float) (c*zz - s*x[i]);
				            x[i] = (float) (s*zz + c*x[i]);
				            
				            s = (float) Math.sin(1.047197551);
				            c = (float) Math.cos(1.047197551);
				            xx = x[i];
				            x[i] = c*xx - s*y[i];
				            y[i] = s*xx + c*y[i];
				    	
				    	}
						
						//Prism2Dto3DMesh pixel = new Prism2Dto3DMesh(6, x, y, z);
				        //MeshView rect = new MeshView(pixel.getMesh());
				        MyMeshView rect = new MyMeshView(6, x, y, z);
				        String ECID = String.format("ECID%06d", u*10000 + v*100 + w);
				        rect.setId(ECID);
				        //Tooltip t = new Tooltip(ECID);
				        //Tooltip.install(rect, t);
				        double alpha = 1.0;
				        int ucolor = 255, vcolor = 255, wcolor = 255;
				        if(u%2 ==0)
				        {
				        	alpha = 1.0;
				        	ucolor = 0;
				        }
				        if(v%2 ==0) vcolor = 0;
				        if(w%2 ==0) wcolor = 0;
				        rect.setMaterial(new PhongMaterial(Color.rgb(ucolor,vcolor,wcolor,alpha)));
				        detGroup.getChildren().add(rect);
					}
				}
			}
		}
    }
    
 

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
    
    public static void main(String[] args) {
    	System.out.println("running CLAS12Calibration");
    	CLAS12Calibration cal = new CLAS12Calibration();
    	
    	
    }

}
