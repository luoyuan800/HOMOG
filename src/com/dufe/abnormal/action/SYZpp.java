package com.dufe.abnormal.action;

import java.util.ArrayList;
import java.util.List;

import com.dufe.abnormal.util.CSVFileUtil;

public class SYZpp {

	public static void main(String[] args){
		SYZpp syz=new SYZpp();
		CSVFileUtil csv=new CSVFileUtil();
		String name="匹配.csv";
		List<String[]> pp2011=csv.readPP("2011"+name);
		List<String[]> pp2012=csv.readPP("2012"+name);
		List<String[]> pp2013=csv.readPP("2013"+name);
		List<String[]> pp2014=csv.readPP("2014"+name);
		List<String[]> pp2015=csv.readPP("2015"+name);
		syz.get(pp2011,"2011");
		syz.get(pp2012,"2012");
		syz.get(pp2013,"2013");
		syz.get(pp2014,"2014");
		syz.get(pp2015,"2015");

	}
	
	public void get(List<String[]> pp2011,String name){
		CSVFileUtil csv=new CSVFileUtil();
		List<String[]> ppa2011=new ArrayList<String[]>();
		for(int i=0;i<pp2011.size();i++){
			if(pp2011.get(i).length>2){
				ppa2011.add(pp2011.get(i));
			}
		}
		for(int i=0;i<pp2011.size();i++){
			if(pp2011.get(i)[1].equals("1")&&pp2011.get(i).length>2){
				String[] newdata=new String[8];
				newdata[0]=pp2011.get(i)[0];
				newdata[1]=pp2011.get(i)[1];
				newdata[2]=pp2011.get(i)[2];
				double data=Double.parseDouble(pp2011.get(i)[2]);
				double[] min={0,0,0,0,0};
				String[] mindm={"0","0","0","0","0"};
 				for(int j=0;j<ppa2011.size();j++){
 					if(ppa2011.get(j)[1].equals("1")){}
 					else 
					for(int k=0;k<5;k++){
						if((Math.abs(Double.parseDouble(ppa2011.get(j)[2])-data)<min[k]||mindm[k].equals("0"))
								&&((Integer.valueOf(ppa2011.get(j)[0])<400000&&Integer.valueOf(newdata[0])<400000)||(Integer.valueOf(ppa2011.get(j)[0])>600000&&Integer.valueOf(newdata[0])>600000))){
							for(int l=4;l>k;l--){
								min[l]=min[l-1];
								mindm[l]=mindm[l-1];
							}
							min[k]=Math.abs(Double.parseDouble(ppa2011.get(j)[2])-data);
							mindm[k]=ppa2011.get(j)[0];
							break;
						}
					}
				}
 				System.out.println(data+"\t"+newdata[0]);
				for(int j=0;j<5;j++){
					newdata[j+3]=mindm[j];
					System.out.println(min[j]+"\t"+mindm[j]);
				}
				pp2011.set(i, newdata);
			}
		}
		csv.savePP(pp2011,name);
	}
	
}
