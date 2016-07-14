package org.clas.cal.tools;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;


public class Prism2Dto3DMesh extends TriangleMesh{
	
	private int numpoints;
	private float[] x;
	private float[] y;
	private float[] z;
	public float length;

	public Prism2Dto3DMesh() {
		// TODO Auto-generated constructor stub
		numpoints = 10;
		x = new float[10];
		x[0] = 0.0f;
		x[1] = -100.0f;
		x[2] = -100.0f;
		x[3] = 100.0f;
		x[4] = 100.0f;
		x[5] = -100.0f;
		x[6] = -100.0f;
		x[7] = 100.0f;
		x[8] = 100.0f;
		x[9] = 0.0f;
		y = new float[10];
		y[0] = 0.0f;
		y[1] = 100.0f;
		y[2] = -100.0f;
		y[3] = -100.0f;
		y[4] = 100.0f;
		y[5] = 100.0f;
		y[6] = -100.0f;
		y[7] = -100.0f;
		y[8] = 100.0f;
		y[9] = 0.0f;
		z = new float[10];
		z[0] = 100.0f;
		z[1] = 100.0f;
		z[2] = 100.0f;
		z[3] = 100.0f;
		z[4] = 100.0f;
		z[5] = -100.0f;
		z[6] = -100.0f;
		z[7] = -100.0f;
		z[8] = -100.0f;
		z[9] = -100.0f;
	}
	
	
	//assume positive z points are handed first...
	public Prism2Dto3DMesh(int n, float[] x1, float[] y1, float[] z1) {
		// TODO Auto-generated constructor stub
		numpoints = n + 2;
		x = new float[numpoints];
		y = new float[numpoints];
		z = new float[numpoints];
		System.arraycopy( (float[])x1, 0, x, 1, n);
		System.arraycopy( (float[])y1, 0, y, 1, n);
		System.arraycopy( (float[])z1, 0, z, 1, n);
		
		//find top center
		x[0] = 0.0f;
		y[0] = 0.0f;
		z[0] = 0.0f;
		for(int i=0;i<n/2;++i)
		{
			x[0] += x1[i];
			y[0] += y1[i];
			z[0] += z1[i];
		}
		x[0] /= (float)n/2.0;
		y[0] /= (float)n/2.0;
		z[0] /= (float)n/2.0;
		
		//find bottom center
		x[numpoints-1] = 0.0f;
		y[numpoints-1] = 0.0f;
		z[numpoints-1] = 0.0f;
		for(int i=n/2;i<n;++i)
		{
			x[numpoints-1] += x1[i];
			y[numpoints-1] += y1[i];
			z[numpoints-1] += z1[i];
		}
		x[numpoints-1] /= (float)n/2.0;
		y[numpoints-1] /= (float)n/2.0;
		z[numpoints-1] /= (float)n/2.0;
		
		float templength = (x[0] -x[numpoints-1]) * (x[0] -x[numpoints-1]);
		templength += (y[0] -y[numpoints-1]) * (y[0] -y[numpoints-1]);
		templength += (z[0] -z[numpoints-1]) * (z[0] -z[numpoints-1]);
		this.length = (float) Math.sqrt(templength);
	}
	
	public TriangleMesh getMesh()
	{
			// Create a TriangleMesh
			TriangleMesh mesh = new TriangleMesh();
			mesh.getPoints().addAll(findallpoints());
			mesh.getTexCoords().addAll(0,0);
			mesh.getFaces().addAll(findallfaces());

			return mesh;
	}
	
	
	public float[] findallpoints()
	{
		int count = 0;
		float[] temp3p = new float[numpoints * 3];
		for(int i = 0; i < numpoints * 3; i+=3)
		{
			
			temp3p[i] = x[count];
			temp3p[i+1] = y[count];
			temp3p[i+2] = z[count];
			++count;
		}
		return temp3p;
	}
	
	
	public int[] findallfaces()
	{
		int numtopvert = numpoints/2;
		int numtopfaces = (numpoints-2)/2;
		int numbotfaces = (numpoints-2)/2;
		int numsidefaces = (numpoints/2  - 1) * 2; //remove center vertex
		
		int count = 1;
		int faceindex = 0;
		int[] temp3f = new int[(numtopfaces + numbotfaces + numsidefaces)*6];
		
		//find top faces
		for(int i = 0; i < numtopfaces; ++i)
		{
			temp3f[faceindex] = 0; //center point
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			temp3f[faceindex] = 0; //texture
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			temp3f[faceindex] = count;
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			temp3f[faceindex] = 0; //texture
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			if(count+1 == numpoints/2) temp3f[faceindex] = 1;
			else temp3f[faceindex] = count + 1;
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			temp3f[faceindex] = 0; //texture
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			++count;
		}
		
		//find side faces
		int topcount = 1; //one more than center
		int botcount = numpoints/2; //start of bottom
		for(int i = 0; i < numsidefaces/2; ++i)
		{
			temp3f[faceindex]   = topcount; //center point
			++faceindex;
			temp3f[faceindex] = 0; //texture
			++faceindex;
			temp3f[faceindex] = botcount;
			++faceindex;
			temp3f[faceindex] = 0; //texture
			++faceindex;
			if(botcount+1 == numpoints-1) temp3f[faceindex] = numpoints/2;
			else temp3f[faceindex] = botcount + 1;
			++faceindex;
			temp3f[faceindex] = 0; //texture
			++faceindex;
			
			temp3f[faceindex]   = topcount; //center point
			++faceindex;
			temp3f[faceindex] = 0; //texture
			++faceindex;
			if(botcount+1 == numpoints-1) temp3f[faceindex] = numpoints/2;
			else temp3f[faceindex] = botcount + 1;
			++faceindex;
			temp3f[faceindex] = 0; //texture
			++faceindex;
			if(topcount+1 == numpoints/2) temp3f[faceindex] = 1;
			else temp3f[faceindex] = topcount + 1;
			++faceindex;
			temp3f[faceindex] = 0; //texture
			++faceindex;
			++topcount;
			++botcount;
		}
		
		
		//find bottom faces
		botcount = numpoints/2; //start of bottom
		for(int i = 0; i < numbotfaces; ++i)
		{
			temp3f[faceindex] = numpoints - 1; //center point
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			temp3f[faceindex] = 0; //texture
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			if(botcount+1 == numpoints-1) temp3f[faceindex] = numpoints/2;
			else temp3f[faceindex] = botcount + 1;
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			temp3f[faceindex] = 0; //texture
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			temp3f[faceindex] = botcount;
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			temp3f[faceindex] = 0; //texture
			//System.out.print(temp3f[faceindex] + " ");
			++faceindex;
			++botcount;
		}
		
		/*
		int[] temp3f =
		{
			//top
			0,0, 1,0, 2,0, 
			2,0, 3,0, 0,0, 
			0,0, 3,0, 4,0, 
			4,0, 1,0, 0,0, 

			
			//sides
			1,0, 5,0, 6,0, 
			1,0, 6,0, 2,0, 
			2,0, 6,0, 7,0, 
			2,0, 7,0, 3,0, 
			3,0, 7,0, 8,0, 
			3,0, 8,0, 4,0, 
			4,0, 8,0, 5,0, 
			4,0, 5,0, 1,0, 
			
			//bottom
			9,0, 6,0, 5,0, 
			9,0, 7,0, 6,0, 
			9,0, 8,0, 7,0, 
			9,0, 5,0, 8,0 
			
			
		};
		*/
		return temp3f;
		
	}
}

