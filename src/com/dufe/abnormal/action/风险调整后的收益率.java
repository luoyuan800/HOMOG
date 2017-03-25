package com.dufe.abnormal.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dufe.abnormal.po.Data;
import com.dufe.abnormal.po.Result;
import com.dufe.abnormal.service.Regression;
import com.dufe.abnormal.util.CSVFileUtil;

public class 风险调整后的收益率 {

	public static void main(String[] args){
		风险调整后的收益率 ee=new 风险调整后的收益率();
		CSVFileUtil csv=new CSVFileUtil();
		List<String[]> hzb=csv.readHZB("trajectory超额收益率hzb");
		List<String[]> hbl=csv.readHZB("三个月定存无风险利率");
		List<String[]> getall=new ArrayList<String[]>();
		for(int i=0;i<hzb.size();i++){
			String[] test=hzb.get(i);
			if(test.length>5){
				getall.add(ee.get(test,hbl));
			}else {
				String[] gz={"","","","","",""};
				getall.add(gz);
			}
		}
		csv.saveHZB(hzb,getall);
	}
	
	private String[] get(String[] daima,List<String[]> hbl) {
		String[] result=new String[6];
		CSVFileUtil csv=new CSVFileUtil();
		// TODO 自动生成的方法存根
		Result today=new Result();
		today=csv.get("only/"+daima[1]+".csv", daima[1], daima[2],1).get(0);
		String drhbl=null;
		for(int i=0;i<hbl.size();i++){
			if(today.getDate().equals(hbl.get(i)[0])){
				drhbl=hbl.get(i)[1];
				break;
			}
		}
		System.out.println(today.getDate()+today.getRi()+drhbl);
		result[0]=String.valueOf(today.getRi()-Double.parseDouble(drhbl));
		for(int i=3;i<=7;i++){
			System.out.println(daima[i]+daima[2]);
			today=csv.get("only/"+daima[i]+".csv",daima[i], daima[2],1).get(0);
			for(int j=0;j<hbl.size();j++){
				if(today.getDate().equals(hbl.get(j)[0])){
					drhbl=hbl.get(j)[1];
					break;
				}
			}
			result[i-2]=String.valueOf(today.getRi()-Double.parseDouble(drhbl));
		}
		return result;
	} 

}
