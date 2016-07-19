package org.clas.cal.EC;

import org.clas.cal.tools.Prism2Dto3DMesh;

import javafx.geometry.Point3D;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;

public class ECMeshView extends MeshView{

	private double volume = 0.f;
	private float length = 0.f;
	
	public ECMeshView() {
		super();
	}
	
	public ECMeshView(Mesh mesh) {
		super(mesh);
	}
	
	public ECMeshView(int np, float[] x, float[] y, float[] z) {
		super();
		Prism2Dto3DMesh premesh = new Prism2Dto3DMesh(np, x, y, z);
		this.volume = VolumeOfMesh(premesh);
		this.length = premesh.length;
		this.setMesh(premesh.getMesh());
		//this.setMaterial(new PhongMaterial(Color.rgb(0,0,(int)(((this.volume-2912.3500)/0.385)*255),1.0)));
	}
	
	public void setVolume(double vol){
		this.volume = vol;
	}
	
	public double getVolume(){
		return this.volume;
	}
	
	public float getLength(){
		return this.length;
	}
	
	public double SignedVolumeOfTriangle(Point3D p1, Point3D p2, Point3D p3) {
        double v321 = p3.getX()*p2.getY()*p1.getZ();
        double v231 = p2.getX()*p3.getY()*p1.getZ();
        double v312 = p3.getX()*p1.getY()*p2.getZ();
        double v132 = p1.getX()*p3.getY()*p2.getZ();
        double v213 = p2.getX()*p1.getY()*p3.getZ();
        double v123 = p1.getX()*p2.getY()*p3.getZ();
        return (1.0/6.0)*(-v321 + v231 + v312 - v132 - v213 + v123);
    }
    
    public double VolumeOfMesh(Prism2Dto3DMesh test) {
        double vols = 0.0;
        
        Point3D point1 = null;
        Point3D point2 = null;
        Point3D point3 = null;
        float[] pointarray;
        int[] facearray;
        

        pointarray = test.findallpoints();
        facearray = test.findallfaces();
        for(int i = 0; i < facearray.length ; i+=6)
        {
            //each face has 6 numbers...3point indices and 3 texture coordinate indices (alternating)
            //this weird finding is getting the right 3 points in the right order to calculate the right signed volume
            //with the right 3 points...x/y/z coordinates need to be obtained from the point array
            //which has three floats per point
            point1 = new Point3D(pointarray[facearray[i   ]*3],pointarray[facearray[i]*3+1],pointarray[facearray[i]*3 +2]);
            point2 = new Point3D(pointarray[facearray[i+2 ]*3],pointarray[facearray[i+2 ]*3+1],pointarray[facearray[i+2]*3 +2]);
            point3 = new Point3D(pointarray[facearray[i+4]*3],pointarray[facearray[i+4]*3+1],pointarray[facearray[i+4]*3 +2]);
            vols += SignedVolumeOfTriangle(point1, point2, point3);
        }
        return Math.abs(vols);
    }

}
