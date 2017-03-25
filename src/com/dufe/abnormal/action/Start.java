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

public class Start {

	public static void main(String[] args){
		
	/*	System.out.println(compare_date("2016/6/4","2016/5/5"));*/
		Start start=new Start();
		CSVFileUtil csv=new CSVFileUtil();
		List<Data> data5=csv.read5("五因子.csv");
		List<Result> data=csv.read("报数据.csv");
		List<Result> result=new ArrayList<Result>();
		for(int i=0;i<data.size();i++){
			Result test=new Result();
			String daima=data.get(i).getDaima();
			/*System.out.print(daima);*/
			String name=data.get(i).getName();
			String date=data.get(i).getDate();
			test=start.get(daima,name,date,data5);
			result.add(test);
		}
		csv.save(result);
	}

	private Result get(String daima,String name,String date,List<Data> data5) {
		CSVFileUtil csv=new CSVFileUtil();
		// TODO 自动生成的方法存根
		Result today=new Result();
		String newdate="";
		int i=0;
		String ydate=date;
		for(i=0;i<data5.size();i++){
			if(compare_date(date,data5.get(i).getDate())==0){
				newdate=data5.get(i+11).getDate();
				break;
			}else if(i>0){
				if((compare_date(date,data5.get(i-1).getDate())==-1)&&(compare_date(date,data5.get(i).getDate())==1)){
					newdate=data5.get(i+10).getDate();
					date=data5.get(i-1).getDate();
					i=i-1;
					break;
				}
			}
		}
		today=csv.get("个股收益率.csv", daima, date,1).get(0);
		today.setR1(today.getRi());
		today.setD11(ydate);
		today.setRf(data5.get(i).getRf());
		today.setRp(data5.get(i).getRp());
		today.setSmb(data5.get(i).getSmb());
		today.setHml(data5.get(i).getHml());
		today.setRmw(data5.get(i).getRmw());
		today.setCma(data5.get(i).getCma());
		List<Result> test=csv.get("个股收益率.csv", daima, newdate,236);
		/*System.out.println(test.get(0).getDate());*/
		List<Result> result=new ArrayList<Result>();
		int k=0;
		for(int j=0;j<236;j++){
			if(compare_date(data5.get(i+11).getDate(),test.get(k).getDate())==-1){
				i--;
			}else break;
		}
		for(int j=0;j<236;j++){
			if(data5.get(j+i+11).getDate().equals(test.get(k).getDate())){
				Result temp=new Result();
				temp.setRi(test.get(k).getRi());
				temp.setRf(data5.get(j+i+11).getRf());
				temp.setRp(data5.get(j+i+11).getRp());
				temp.setSmb(data5.get(j+i+11).getSmb());
				temp.setHml(data5.get(j+i+11).getHml());
				temp.setRmw(data5.get(j+i+11).getRmw());
				temp.setCma(data5.get(j+i+11).getCma());
				k++;
				result.add(temp);
			}
		}
		int size=result.size();
		/*System.out.println(size);*/
		if(size<40){
		System.out.println(daima+"\t"+newdate+"\t");}
		double x[][]=new double[size][5];
		double y[]=new double[size];
		double kk[]=new double[6];
		for(int j=0;j<size;j++){
			x[j][0]=result.get(j).getRp();
			x[j][1]=result.get(j).getSmb();
			x[j][2]=result.get(j).getHml();
			x[j][3]=result.get(j).getRmw();
			x[j][4]=result.get(j).getCma();
			y[j]=result.get(j).getRi()-result.get(j).getRf();
		}
		double yz=0;
		for(int j=0;j<size;j++){
			yz=yz+y[j];
		}
		double yp=yz/size;
		Regression.LineRegression(x,y,kk,3,size);
		double ei=0,yi=0;
		for(int j=0;j<size;j++){
			ei=ei+Math.pow(y[j]-(kk[0]+kk[1]*x[j][0]+kk[2]*x[j][1]+kk[3]*x[j][2]+kk[4]*x[j][3]+kk[5]*x[j][4]), 2);
			yi=yi+Math.pow(y[j]-yp,2);
		}
		today.setR2(1-(ei/yi));
		double yy=kk[0]+kk[1]*today.getRp()+kk[2]*today.getSmb()+kk[3]*today.getHml()+kk[4]*today.getRmw()+kk[5]*today.getCma();
		today.setR11(yy+today.getRf());
		today.setA(kk[0]);
		today.setB(kk[1]);
		today.setSi(kk[2]);
		today.setHi(kk[3]);
		today.setRi(kk[4]);
		today.setCi(kk[5]);
		today.setDaima(daima);
		today.setName(name);
		return today;
	} 
    public int compare_date(String DATE1, String DATE2) {
        
        
        DateFormat df = new SimpleDateFormat("yyyy/M/d");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
               /* System.out.println("dt1 在dt2前");*/
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                /*System.out.println("dt1在dt2后");*/
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
	
}
