package com.dufe.abnormal.action;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dufe.abnormal.po.Data;
import com.dufe.abnormal.po.Result;
import com.dufe.abnormal.service.Regression;
import com.dufe.abnormal.util.CSVFileUtil;

public class GGSYS {

	public static void main(String[] args){
		
	/*	System.out.println(compare_date("2016/6/4","2016/5/5"));*/
		GGSYS start=new GGSYS();
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
		csv.newsave(result);
	}

	private Result get(String daima,String name,String date,List<Data> data5) {
		CSVFileUtil csv=new CSVFileUtil();
		// TODO 自动生成的方法存根

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

		List<Result> test=csv.get("个股交易数.csv", daima, newdate,246);
		/*System.out.println(test.get(0).getDate());*/
		List<Result> result=new ArrayList<Result>();
		int k=0;
		for(int j=0;j<236;j++){
			if(compare_date(data5.get(i+11).getDate(),test.get(k).getDate())==-1){
				k++;
			}else break;
		}
		for(int j=0;j<236;j++){

			if(data5.get(j+i+11).getDate().equals(test.get(k).getDate())){
				Result temp=new Result();
				//ri 当日交易量
				temp.setRi(test.get(k).getRi());
				
				k++;
				result.add(temp);
			}
		}
		int size=result.size();
		/*System.out.println(size);*/
		if(size<40){System.out.println(daima+"\t"+newdate+"\t");}
		double pingjun=0;
		for(int j=0;j<size;j++){
			pingjun=pingjun+result.get(j).getRi();
		}
		DecimalFormat df=new DecimalFormat("#0.00");
		pingjun=pingjun/size;
		System.out.println(daima+"\t"+data5.get(i+11).getDate()+"\t"+test.get(0).getDate()+"\t"+df.format(pingjun)+"\t"+size);
		
		Result today=new Result();
		today=csv.get("个股交易数.csv", daima, date,1).get(0);
		today.setD11(ydate);
		//超额量
		today.setR1((today.getRi()-pingjun)/pingjun);
		//平均数
		today.setA(pingjun);
		//当天交易股数量Ri
		
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
