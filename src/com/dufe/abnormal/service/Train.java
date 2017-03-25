package com.dufe.abnormal.service;

import java.util.ArrayList;
import java.util.List;


public class Train {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    	Train train=new Train();
    	
    }
    
    public List<Double> get(List<Double> x,List<Double> y){
    	List<List<Double>> list=new ArrayList<List<Double>>();
    	List<Double> x1=x;
    	List<double[]> kkk=new ArrayList<double[]>();
    	List<Double> result=new ArrayList<Double>();
    	int k=0;
    	List<Double> rr=new ArrayList<Double>();
     		int size=2*(x.size()-k)/3;
    		double[][] xx=new double[size][k+1];
    		double[] yyy=new double[size];
    		for(int i=0;i<size;i++){	
    			yyy[i]=y.get(i+k);
     			for(int j=0;j<=k;j++){
     				xx[i][j]=y.get(i-j+k);
    			}

    		}  		
    		double[] kk=new double[k+2];
    		Regression.LineRegression(xx,yyy,kk,k+1,size);
    	/*	System.out.println(kk[0]+"\t"+kk[1]);*/
    		
    		list=new ArrayList<List<Double>>();

            kkk.add(kk);
            double yy=0;
            for(int i=size+k;i<x.size();i++){
            	yy=kk[0];
            	if(k>1){
            		for(int j=0;j<k-1;j++){
            			yy=yy+kk[j+1]*y.get(i-j-1);
            		}
            	}


    	}
    	double min=rr.get(0);
    	int j=0;
    	if(rr.size()>1){
    		for(int i=1;i<kkk.size();i++)
    		{
    			if(rr.get(i)<min){
    				min=rr.get(i);
    				j=i;
    			}
    		}
    	}
    	System.out.println(kkk.get(j)[0]+"\t"+rr.get(j));
    	for(int d=1;d<=31;d++){
    		x.add((double) d);
        	double result1=kkk.get(j)[0];
        	double max=0;min=1000;
        	if(kkk.get(j).length>2){
         		for(int i=1;i<kkk.get(j).length;i++){
        			
         			result1=result1+kkk.get(j)[i]*y.get(x.size()-i-1);
        			
        			
        		}    		
        	}
    		if(result1>max)max=result1;
    		if(result1<min)min=result1;
        	if(max>=1.2*min||min<0){
        		k=0;
        		result=new ArrayList<Double>();
        		for(int l=1;l<=31;l++){
        			result.add(x1.get(x1.size()-1));
        		}
        		break;
        	}
        	result.add(result1);
        	y.add(result1);
    	}

    	
    	return result;
    }
}