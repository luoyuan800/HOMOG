package com.dufe.abnormal.action;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.dufe.abnormal.util.CSVFileUtil;

public class GetYear {

	public static void main(String[] args){
		
		CSVFileUtil csv=new CSVFileUtil();
		GetYear get=new GetYear();
		List<String[]> ybl=csv.readZY("因变量.csv");
		List<String[]> gsgm=csv.readZY("公司规模.csv");
		List<String[]> sjl=csv.readZY("市净率.csv");
		List<String[]> cwgg=csv.readZY("财务杠杆.csv");
		List<String[]> zcbcl=csv.readZY("资产报酬率.csv");
		List<String[]> chzzts=csv.readZY("存货周转天数.csv");
		List<String[]> hyfl=csv.readZY("行业分类.csv");
		for(int i=1;i<=5;i++){
			List<List<String>> year=new ArrayList<List<String>>();
			for(int j=0;j<ybl.size();j++){
				List<String> data=new ArrayList<String>();
				data.add(ybl.get(j)[0]);
				data.add(ybl.get(j)[i]);
				data.add(get.find(i,ybl.get(j)[0],hyfl));
				data.add(get.find(i,ybl.get(j)[0],gsgm));
				data.add(get.find(i,ybl.get(j)[0],sjl));
				data.add(get.find(i,ybl.get(j)[0],cwgg));
				data.add(get.find(i,ybl.get(j)[0],zcbcl));
				data.add(get.find(i,ybl.get(j)[0],chzzts));
				int a=0;
				for(int k=2;k<=7;k++){
					if(data.get(k).equals("0")||data.get(k).length()<1){
						a=1;
					}
				}
				if(a==0){
					year.add(data);
				}
				
			}
			get.save(year,2016-i);
		}
	}
	private String find(int i, String string, List<String[]> list) {
		// TODO 自动生成的方法存根
		for(int j=0;j<list.size();j++){
			if(list.get(j)[0].equals(string)){
				return list.get(j)[i];
			}
		}
		return "0";
	}
	public void save(List<List<String>> yuce,int name) {
		// TODO 自动生成的方法存根
    	try {
   		 Writer fw = new BufferedWriter(
   				 			new OutputStreamWriter(
   				 			new FileOutputStream("D:\\学习\\future data\\appfdata\\"+name+".csv"), "UTF-8"));
   		   for (int i = 0; i <1; i++) {
   		    StringBuffer str = new StringBuffer();
   		    str.append("股票代码,实验组与对照组分类,行业分类,公司规模,市净率,财务杠杆,资产报酬率,存货周转天数\r\n");
   		    for (int j = 0; j < yuce.size(); j++) {
   		    	List<String> tc=yuce.get(j);
   		    	str.append(tc.get(0)+","+tc.get(1)+","+tc.get(2)+","+tc.get(3)+","+tc.get(4)+","+tc.get(5)+","+tc.get(6)+","+tc.get(7)+"\r\n");
   		    }
   		    fw.write(str.toString());
   		    fw.flush();
   		   }
   		   fw.close();
   		  } catch (IOException e) {
   		   e.printStackTrace();
   		  }
	}
}
