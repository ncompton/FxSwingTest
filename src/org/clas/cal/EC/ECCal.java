package org.clas.cal.EC;

import org.root.basic.EmbeddedCanvas;
import org.root.histogram.H1D;

public class ECCal {

	H1D hEC1,hEC2,hEC3,hEC4,hEC5;
	H1D hPCAL1,hPCAL2,hPCAL3,hPCAL4,hPCAL5;
	
	public ECCal() {
		initcan();
	}
	
	private void initcan(){
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
	
	public void OccupancyTab(ECMeshView det, EmbeddedCanvas can){
		if(det.getId().contains("EC"))
			can.draw(hEC1);
		else if(det.getId().contains("PCAL"))
			can.draw(hPCAL1);
		
	}
	
	public void Tab2(ECMeshView det, EmbeddedCanvas can){
		if(det.getId().contains("EC")){
			can.draw(0,hEC2,"");
			can.draw(1,hEC3,"");
			can.draw(2,hEC4,"");
			can.draw(3,hEC5,"");
			System.out.println(det.getVolume());
		}
		else if(det.getId().contains("PCAL")){
			can.draw(0,hPCAL2,"");
			can.draw(1,hPCAL3,"");
			can.draw(2,hPCAL4,"");
			can.draw(3,hPCAL5,"");
			System.out.println(det.getVolume());
		}
	}

}
