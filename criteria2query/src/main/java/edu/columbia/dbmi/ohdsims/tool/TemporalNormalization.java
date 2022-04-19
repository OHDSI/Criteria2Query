package edu.columbia.dbmi.ohdsims.tool;

import edu.columbia.dbmi.ohdsims.pojo.TemporalConstraint;
import edu.columbia.dbmi.ohdsims.util.TemporalNormalize;
import jnr.ffi.annotations.In;

import java.util.List;

public class TemporalNormalization {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TemporalNormalization tn=new TemporalNormalization();
		TemporalConstraint[] tc=tn.convertFromString("within 3 months");
		System.out.println(tc[0].getStart_days());
		System.out.println(tc[1]);

	}
	
	public TemporalConstraint[] convertFromString(String plaintext){
		TemporalConstraint[] tc = new TemporalConstraint[2];
		tc[0]=new TemporalConstraint();
		tc[1]=new TemporalConstraint();
		TemporalNormalize tn = new TemporalNormalize();
		//Integer days = tn.temporalNormalizeforNumberUnit(plaintext);
		List<Integer> days = tn.temporalNormalizeforNumberUnit(plaintext);
		if(days.size()==1){
			tc[0].setStart_days(days.get(0));
			tc[0].setStart_offset(-1);
			tc[0].setEnd_days(0);
			tc[0].setEnd_offset(1);
		}else{
			tc[0].setStart_days(days.get(0));
			tc[0].setStart_offset(-1);
			tc[0].setEnd_days(days.get(1));
			tc[0].setEnd_offset(1);
		}
		return tc;
	}
	
	public void evaluate(){
		
	}
	

}
