package org.clas.cal.EC;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

//use caldrawdb to get pixels
//create front and back of object
//make an ECMeshView per pixel
//have a function to add all ECMeshViews to the passed group
//right now it just reads in files...
public class ECMeshMaker {

	public ECMeshMaker() {
		// TODO Auto-generated constructor stub
	}
	
	public void addPCALpix(Group detGroup){
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
						
						//Prism2Dto3DMesh pixel = new Prism2Dto3DMesh(2*numpoints[u][v][w], x, y, z);
				        ECMeshView rect = new ECMeshView(2*numpoints[u][v][w], x, y, z);
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
	
	public void addECpix(Group detGroup)
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
				        ECMeshView rect = new ECMeshView(6, x, y, z);
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

}
