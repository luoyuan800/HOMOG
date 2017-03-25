package com.dufe.abnormal.util;  
  
import java.io.BufferedReader;  
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dufe.abnormal.po.Data;
import com.dufe.abnormal.po.Result;

 
public class CSVFileUtil{
	//图片文件夹
	private static String path="E:/Mi/data";
	
	public static void main(String[] args){
		CSVFileUtil csv=new CSVFileUtil();
    	int i=0;
        List<String> list=new ArrayList<String>();
        try {  
        	//csv文件地址
            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path+"/个股收益率.csv"),"GBK")); 
            String line = null; 
            String test="1";
            //一行行读，直到没有
            while((line=reader.readLine())!=null){  
            	i++;
            	//判断行数，如出错可改变该值继续读取
            	if(i>1){
            		String[] item = line.split(",");
            		if(test.equals("1")){
	            		test=item[0];
            		}
            		if(!item[0].equals(test)){
	            		csv.saveGGSYL(list,test);
	            		list=new ArrayList<String>();
	            		test=item[0];
	            		list.add(line);
            		}else {
            			list.add(line);
            		}
            	}
            }  
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace(); 
        }  
	}
	
	  public void saveGGSYL(List<String> b,String name){
		  try {
			  Writer fw = new BufferedWriter(
					  new OutputStreamWriter(
							  new FileOutputStream(path+"/only/"+name+".csv"), "GBK"));
			  StringBuffer str = new StringBuffer();
			  str.append("Stkcd,Trddt,Dretwd\r\n");
			  for (int j = 0; j < b.size(); j++) {
				  str.append(b.get(j)+"\r\n");
			  }
			  fw.write(str.toString());
			  fw.flush();
			  fw.close();
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
	
	
	  public void saveHZB(List<String[]> b,List<String[]> c){
		  try {
			  Writer fw = new BufferedWriter(
					  new OutputStreamWriter(
							  new FileOutputStream("D:\\学习\\hzb2.csv"), "GBK"));
			  StringBuffer str = new StringBuffer();
			  str.append("公司名称,实验公司,低碳信息发布时间,匹配公司1,匹配公司2,匹配公司3,匹配公司4,匹配公司5,实验公司风险调整后收益率,匹配公司1风险调整后收益率,匹配公司2风险调整后收益率,匹配公司3风险调整后收益率,匹配公司4风险调整后收益率,匹配公司5风险调整后收益率\r\n");
			  for (int j = 0; j < b.size(); j++) {
				  String[] test=b.get(j);
				  String[] test2=c.get(j);
				  for(int i=0;i<test.length;i++){
					  str.append(test[i]+",");
				  }
				  for(int i=0;i<test2.length;i++){
					  str.append(test2[i]+",");
				  }
				  str.append("\r\n");
			  }
			  fw.write(str.toString());
			  fw.flush();
			  fw.close();
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
	
	  public void savePP(List<String[]> b,String name){
		  try {
			  Writer fw = new BufferedWriter(
					  new OutputStreamWriter(
							  new FileOutputStream("D:\\学习\\"+name+".csv"), "GBK"));
			  StringBuffer str = new StringBuffer();
			  str.append("股票代码,实验组与对照组分类,得分,匹配代码1,匹配代码2,匹配代码3,匹配代码4,匹配代码5\r\n");
			  for (int j = 0; j < b.size(); j++) {
				  String[] test=b.get(j);
				  for(int k=0;k<test.length;k++){
					  str.append(test[k]+",");
				  }
				  str.append("\r\n");
			  }
			  fw.write(str.toString());
			  fw.flush();
			  fw.close();
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }

	
	  public List<String[]> readPP(String name) {
	    	
	    	int i=0;
	        List<String[]> list=new ArrayList<String[]>();
	        try {  
	        	//csv文件地址
	            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path+"/"+name),"GBK")); 
	            String line = null; 
	            String test=null;
	            //一行行读，直到没有
	            while((line=reader.readLine())!=null){  
	            	i++;
	            	//判断行数，如出错可改变该值继续读取
	            	if(i>1){
	            		String[] item = line.split(",");
		            		list.add(item);
	            	}
	            }  
	            reader.close();
	        }
	        catch (Exception e) {
	            e.printStackTrace(); 
	        }  
	        return list;
	    }
	
	  public void saveHZB(List<String[]> b){
		  try {
			  Writer fw = new BufferedWriter(
					  new OutputStreamWriter(
							  new FileOutputStream("D:\\学习\\hzb.csv"), "GBK"));
			  StringBuffer str = new StringBuffer();
			  str.append("股票代码,2015PB,2014PB,2013PB,2012PB,2011PB,2010PB\r\n");
			  for (int j = 0; j < b.size(); j++) {
				  String[] test=b.get(j);
				  str.append(test[0]+","+test[1]+","+test[2]+","+test[3]+","+test[4]+","+test[5]+","+test[6]+","+"\r\n");
			  }
			  fw.write(str.toString());
			  fw.flush();
			  fw.close();
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }

	  public List<String[]> readHZB(String name) {
	    	
	    	int i=0;
	        List<String[]> list=new ArrayList<String[]>();
	        try {  
	        	//csv文件地址
	            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path+"/"+name+".csv"),"GBK")); 
	            String line = null; 
	            //一行行读，直到没有
	            while((line=reader.readLine())!=null){  
	            	i++;
	            	//判断行数，如出错可改变该值继续读取
	            	if(i>1){
	            		String[] item = line.split(",");
		            		list.add(item);
	            	}
	            }  
	            reader.close();
	        }
	        catch (Exception e) {
	            e.printStackTrace(); 
	        }  
	        return list;
	    }	
	
	  public List<String> readHZB() {
	    	
	    	int i=0;
	        List<String> list=new ArrayList<String>();
	        try {  
	        	//csv文件地址
	            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path+"/汇总表.csv"),"GBK")); 
	            String line = null; 
	            //一行行读，直到没有
	            while((line=reader.readLine())!=null){  
	            	i++;
	            	//判断行数，如出错可改变该值继续读取
	            	if(i>1){
	            		String[] item = line.split(",");
		            		list.add(item[0]);
	            	}
	            }  
	            reader.close();
	        }
	        catch (Exception e) {
	            e.printStackTrace(); 
	        }  
	        return list;
	    }
	  
	  public List<String[]> readSJL(String name) {
	    	
	    	int i=0;
	        List<String[]> list=new ArrayList<String[]>();
	        try {  
	        	//csv文件地址
	            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path+"/"+name),"GBK")); 
	            String line = null; 
	            String test=null;
	            //一行行读，直到没有
	            while((line=reader.readLine())!=null){  
	            	i++;
	            	//判断行数，如出错可改变该值继续读取
	            	if(i>1){
	            		String[] item = line.split(",");
	            		if(item.length==3&&!item[1].equals(test)){
		            		list.add(item);
		            		test=item[1];
	            		}
	            	}
	            }  
	            reader.close();
	        }
	        catch (Exception e) {
	            e.printStackTrace(); 
	        }  
	        return list;
	    }
	
	  public List<String[]> readZY(String name) {
	    	
	    	CSVFileUtil csv=new CSVFileUtil();
	    	int i=0;
	        List<String[]> list=new ArrayList<String[]>();
	        try {  
	        	//csv文件地址
	            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path+"/"+name),"GBK")); 
	            String line = null; 
	            //一行行读，直到没有
	            while((line=reader.readLine())!=null){  
	            	i++;
	            	//判断行数，如出错可改变该值继续读取
	            	if(i>1){
	            		line=csv.get(line);
	            		String[] item = line.split(",");
	            		list.add(item);
	            	}
	            }  
	            reader.close();
	        }
	        catch (Exception e) {
	            e.printStackTrace(); 
	        }  
	        return list;
	    }
	
    public void newsave(List<Result> b){
    	int size[]={255,254,255,133};
    	
    	for(int i=0;i<4;i++){
    		try {
    			Writer fw = new BufferedWriter(
    					new OutputStreamWriter(
    							new FileOutputStream("D:\\学习\\"+i+".csv"), "GBK"));
    			StringBuffer str1 = new StringBuffer();
    			StringBuffer str2 = new StringBuffer();
    			StringBuffer str3 = new StringBuffer();
    			StringBuffer str4 = new StringBuffer();
    			StringBuffer str5 = new StringBuffer();
    			StringBuffer str6 = new StringBuffer();
    			StringBuffer str7 = new StringBuffer();
    			
    			for (int j = 1; j <= size[i]; j++) {
    				int aa=0;
    				switch(i){
    				case 0:aa=j-1;break;
    				case 1:aa=255+j-1;break;
    				case 2:aa=255+254+j-1;break;
    				case 3:aa=255+254+255+j-1;break;
    				}
    				Result tc=b.get(aa);
    				str1.append(j+",");
    				str2.append(tc.getName()+",");
    				str3.append(tc.getDaima()+",");
    				str4.append(tc.getD11()+",");
     				str5.append(tc.getA()+",");
     				str6.append(tc.getRi()+",");
     				str7.append(tc.getR1()+",");
    			}
				str1.append("\r\n"+str2+"\r\n"+str3+"\r\n"+str4+"\r\n"+str5+"\r\n"+str6+"\r\n"+str7);
				fw.write(str1.toString());
				fw.flush();
    			fw.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    public void save(List<Result> b){
    	int size[]={255,254,255,133};
    	
    	for(int i=0;i<4;i++){
    		try {
    			Writer fw = new BufferedWriter(
    					new OutputStreamWriter(
    							new FileOutputStream("D:\\学习\\"+i+".csv"), "GBK"));
    			StringBuffer str1 = new StringBuffer();
    			StringBuffer str2 = new StringBuffer();
    			StringBuffer str3 = new StringBuffer();
    			StringBuffer str4 = new StringBuffer();
    			StringBuffer str5 = new StringBuffer();
    			StringBuffer str6 = new StringBuffer();
    			StringBuffer str7 = new StringBuffer();
    			StringBuffer str8 = new StringBuffer();
    			StringBuffer str9 = new StringBuffer();
    			StringBuffer str10 = new StringBuffer();
    			StringBuffer str11= new StringBuffer();
    			StringBuffer str12 = new StringBuffer();
    			StringBuffer str13= new StringBuffer();
    			StringBuffer str14 = new StringBuffer();
    			StringBuffer str15 = new StringBuffer();
    			StringBuffer str16 = new StringBuffer();
    			StringBuffer str17 = new StringBuffer();
    			StringBuffer str18 = new StringBuffer();
    			StringBuffer str19 = new StringBuffer();
    			StringBuffer str20 = new StringBuffer();
    			StringBuffer str21 =new StringBuffer();
    			
    			for (int j = 1; j <= size[i]; j++) {
    				int aa=0;
    				switch(i){
    				case 0:aa=j-1;break;
    				case 1:aa=255+j-1;break;
    				case 2:aa=255+254+j-1;break;
    				case 3:aa=255+254+255+j-1;break;
    				}
    				Result tc=b.get(aa);
    				str1.append(j+",");
    				str2.append(tc.getName()+",");
    				str3.append(tc.getDaima()+",");
    				str4.append(tc.getD11()+",");
    				str5.append(tc.getA()+",");
    				str6.append(tc.getB()+",");
    				str7.append(tc.getSi()+",");
    				str8.append(tc.getHi()+",");
    				str9.append(tc.getRi()+",");
    				str10.append(tc.getCi()+",");
    				str11.append(tc.getRf()+",");
    				str12.append(tc.getRp()+",");
    				str13.append(tc.getSmb()+",");
    				str14.append(tc.getHml()+",");
    				str15.append(tc.getRmw()+",");
    				str16.append(tc.getCma()+",");
    				str17.append(tc.getR1()+",");
    				double rr=tc.getR11()-tc.getRf();
    				str18.append(rr+",");
    				str19.append(tc.getR11()+",");
    				double rrr=tc.getR1()-tc.getR11();
    				str20.append(rrr+",");
    				str21.append(tc.getR2()+",");
    			}
				str1.append("\r\n"+str2+"\r\n"+str3+"\r\n"+str4+"\r\n"+"\r\n"+str5+"\r\n"+str6+"\r\n"+str7+"\r\n"+str8+"\r\n"+str9+"\r\n"+str10+"\r\n"+str11+"\r\n"+str12+"\r\n"+str13+"\r\n"+str14+"\r\n"+str15+"\r\n"+str16+"\r\n"+str17+"\r\n"+str18+"\r\n"+str19+"\r\n"+str20+"\r\n"+str21);
				fw.write(str1.toString());
				fw.flush();
    			fw.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }	
	public List<Result> get(String name,String daima,String date,int size) {
	    	
		int i=0;
		List<Result> result=new ArrayList<Result>();
		try {  
			//csv文件地址
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path+"/"+name),"GBK")); 
			String line = null; 
			line=reader.readLine();
			String newdate=date;
			double ri=0;
			//一行行读，直到没有
			while((line=reader.readLine())!=null){  
				//判断行数，如出错可改变该值继续读取
				if(i<size){
					String[] item = line.split(",");
					if((item[1].equals(date)&&item[0].equals(daima))||i>0){
						Result test=new Result();
						test.setDate(item[1]);
						test.setRi(Double.parseDouble(item[2]));
						result.add(test);
						i++;
					}else if((compare_date(date,item[1])==1)&&item[0].equals(daima)){
						Result test=new Result();
						test.setDate(newdate);
						test.setRi(ri);
						result.add(test);
						Result test2 =new Result();
						test2.setDate(item[1]);
						test2.setRi(Double.parseDouble(item[2]));
						result.add(test2);
						i++;
					}
					newdate=item[1];
					ri=Double.parseDouble(item[2]);
				}else break;
			}  
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace(); 
		}  
		return result;
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
		
	   public List<Result> read(String name) {
	    	
	    	int i=0;
	        List<Result> result=new ArrayList<Result>();
            List<String> name2=new ArrayList<String>();
            List<String> daima=new ArrayList<String>();
            List<String> date=new ArrayList<String>();
	        try {  
	        	//csv文件地址
	            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path+"/"+name),"GBK")); 
	            String line = null; 
	            //一行行读，直到没有
	            while((line=reader.readLine())!=null){  
	            	i++;
	            	//判断行数，如出错可改变该值继续读取
	            	if(i%4==2){
	            		String[] item = line.split(",");
	            		for(int j=1;j<item.length;j++){
	            			name2.add(item[j]);
	            		}
	            	}
	            	
	            	if(i%4==3){
	            		String[] item = line.split(",");
	            		for(int j=1;j<item.length;j++){
	            			daima.add(item[j]);
	            		}
	            	}
	            	if(i%4==0){
	            		String[] item = line.split(",");
	            		for(int j=1;j<item.length;j++){
	            			date.add(item[j]);
	            		}
	            	}
	            }  
	            reader.close();
	        }
	        catch (Exception e) {
	            e.printStackTrace(); 
	        }  
	        
	        for(int j=0;j<name2.size();j++){
	        	Result data=new Result();
	        	data.setName(name2.get(j));
	        	data.setDaima(daima.get(j));
	        	data.setDate(date.get(j));
	        	result.add(data);
	        }
	        
	        
	        return result;
	    }

	
    public List<Data> read5(String name) {
    	
    	CSVFileUtil csv=new CSVFileUtil();
    	int i=0;
        List<Data> list=new ArrayList<Data>();
        try {  
        	//csv文件地址
            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path+"/"+name),"GBK")); 
            String line = null; 
            //一行行读，直到没有
            while((line=reader.readLine())!=null){  
            	i++;
            	//判断行数，如出错可改变该值继续读取
            	if(i>1){
            		String[] item = line.split(",");
            		list.add(csv.set(item));
            	}
            }  
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace(); 
        }  
        return list;
    }
    
	private String get(String line) {
		// TODO 自动生成的方法存根
		for(int i=1;i<line.length();i++){
			if(line.substring(i-1, i).equals(",")&&line.substring(i, i).equals(",")){
				line=line.substring(0, i)+"0"+line.substring(i, line.length());
			}
		}
		if(line.substring(line.length()-1, line.length()).equals(",")){
			line=line+"0";
		}
		return line;
	}
	private Data set(String[] item) {
		// TODO 自动生成的方法存根
		Data data=new Data();
		data.setDate(item[0]);
		data.setRf(Double.parseDouble(item[1]));
		data.setRp(Double.parseDouble(item[2]));
		data.setSmb(Double.parseDouble(item[3]));
		data.setHml(Double.parseDouble(item[4]));
		data.setRmw(Double.parseDouble(item[5]));
		data.setCma(Double.parseDouble(item[6]));
		return data;
	}

}