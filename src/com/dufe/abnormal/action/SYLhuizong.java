package com.dufe.abnormal.action;

import java.util.ArrayList;
import java.util.List;

import com.dufe.abnormal.util.CSVFileUtil;

public class SYLhuizong {

	public static void main(String[] args){
		CSVFileUtil csv=new CSVFileUtil();
		String name="市净率.csv";
		List<String[]> sjl2010=csv.readSJL("2010"+name);int a2010=0;
		List<String[]> sjl2011=csv.readSJL("2011"+name);int a2011=0;
		List<String[]> sjl2012=csv.readSJL("2012"+name);int a2012=0;
		List<String[]> sjl2013=csv.readSJL("2013"+name);int a2013=0;
		List<String[]> sjl2014=csv.readSJL("2014"+name);int a2014=0;
		List<String[]> sjl2015=csv.readSJL("2015"+name);int a2015=0;
/*		System.out.println(sjl2010.size());
		for(int i=0;i<sjl2010.size();i++){
			System.out.println(sjl2010.get(i)[1]);
		}
		System.out.println(sjl2011.size());
		System.out.println(sjl2012.size());
		System.out.println(sjl2013.size());
		System.out.println(sjl2014.size());
		System.out.println(sjl2015.size());*/

		List<String> hzb=csv.readHZB();
		List<String[]> hzblist=new ArrayList<String[]>();
		for(int i=0;i<hzb.size();i++){
			String[] test=new String[7];
			test[0]=hzb.get(i);
			while(a2010<sjl2010.size()&&Integer.parseInt(sjl2010.get(a2010)[1])<Integer.parseInt(test[0])){
				a2010++;
			}
			while(a2011<sjl2011.size()&&Integer.parseInt(sjl2011.get(a2011)[1])<Integer.parseInt(test[0])){
				a2011++;
			}
			while(a2012<sjl2012.size()&&Integer.parseInt(sjl2012.get(a2012)[1])<Integer.parseInt(test[0])){
				a2012++;
			}
			while(a2013<sjl2013.size()&&Integer.parseInt(sjl2013.get(a2013)[1])<Integer.parseInt(test[0])){
				a2013++;
			}
			while(a2014<sjl2014.size()&&Integer.parseInt(sjl2014.get(a2014)[1])<Integer.parseInt(test[0])){
				a2014++;
			}
			while(a2015<sjl2015.size()&&Integer.parseInt(sjl2015.get(a2015)[1])<Integer.parseInt(test[0])){
				a2015++;
			}
			if(a2010<sjl2010.size()&&sjl2010.get(a2010)[1].equals(test[0])){
				test[6]=sjl2010.get(a2010)[2];
				a2010++;
			}else test[6]="0";
			if(a2011<sjl2011.size()&&sjl2011.get(a2011)[1].equals(test[0])){
				test[5]=sjl2011.get(a2011)[2];
				a2011++;
			}else test[5]="0";
			if(a2012<sjl2012.size()&&sjl2012.get(a2012)[1].equals(test[0])){
				test[4]=sjl2012.get(a2012)[2];
				a2012++;
			}else test[4]="0";
			if(a2013<sjl2013.size()&&sjl2013.get(a2013)[1].equals(test[0])){
				test[3]=sjl2013.get(a2013)[2];
				a2013++;
			}else test[3]="0";
			if(a2014<sjl2014.size()&&sjl2014.get(a2014)[1].equals(test[0])){
				test[2]=sjl2014.get(a2014)[2];
				a2014++;
			}else test[2]="0";
			if(a2015<sjl2015.size()&&sjl2015.get(a2015)[1].equals(test[0])){
				test[1]=sjl2015.get(a2015)[2];
				a2015++;
			}else test[1]="0";
			hzblist.add(test);
		}
		csv.saveHZB(hzblist);
	}
}
