package com.dufe.abnormal.service;

/*************************************************************************************   
 * @author    
 *   
 *     
 *     
 *    
 ******************************************************************************/     
     
/***********************************************************************************   
 * Desc: The regression analyse is a widely used statistices  method  ,It was always   
 * used for forecast,cause and effect analyse  and so on.   
 * --统计分析-〉回归分析，一般分为线形回归和非线性回归   
 * All of us familiar with the line regression.In fact,this method can extend to non    
 * linear regression ,But the cn.model built based on the analyser's private experience
 * and his knowledge of practice.   
 * ************************************************************************************/     
     
    
     
public  class  Regression      
{     
    public Regression()     
    {     
         
    }     
     
    /***************************************************************************************   
    *模型：多元线性回归   
    *简要说明：根据一个样本集，计算线性回归分析系数。线性回归分析的模型为：Y=k0+k1X1+k2X2+....+knXn,其中，X1,X2,....,Xn为因变量，Y为变量   
    *     k0,k1,....,kn为回归系数，该函数就是要根据一组样本（Xi1,Xi2,...Xin,Yi）i=1,2,...,m[m个样本]，根据最小二乘法得原则，计算出   
    *     最佳得回归分析系数k0,k1,....,kn,从而得到线性回归分析模型，该模型稍加扩展，就可以推广到非线性回归模型   
    *输入参数:   
    *    @param  double[][]  X  自变量样本集   
    *    @param  double[]    Y  变量结果集   
    *    @param  double[]   K  回归系数   
    *    @param  int  n  回归变量个数   
    *    @param  int  m  样本个数   
    *输出参数：   
    *    @return double result  0:失败，其他：成功   
    ****************************************************************************************/     
    public static double  LineRegression(     
                                            double[][] X,     
                                            double[] Y,     
                                            double[] K,     
                                            int n,     
                                            int m    
                                        )     
    {     
        double result = 0 ;     
             
        /*   
        *线性回归问题，最终转换为解一个对称线性方程组的求解问题   
        *线性方程组的系数矩阵为n+1*n+1,常数矩阵为n+1*1   
        */     
        int  XLen = n+1;     
        int  YLen = 1;     
        int i,j,k;     
        double[][]  coeffX = new double[XLen][XLen];     
        double[][]  constY = new double[XLen][1];     
        double[][]  resultK = new double[XLen][1];     
             
        /*   
        *根据参数，计算所要求解方程组的系数矩阵、常数矩阵   
        */     
        double[][] temp = new double[m+1][n+1];     
        for(i =0;i<n+1;i++)     
        {     
            temp[0][i] = 1;     
        }     
        for(i =0;i<m+1;i++)     
        {     
            temp[i][0] = 1;     
        }     
        for( i=1;i<m+1;i++)     
            for( j=1;j<n+1;j++)     
            temp[i][j]= X[i-1][j-1];     
        /*   
        *开始计算每一个系数   
        */     
        for(i=0;i<n+1;i++)     
        {     
            /*   
            *coeffX的第i行和i列的系数，注意，是对称矩阵   
            */     
            for( j= i;j<n+1;j++){     
                double col = 0 ;     
                for(k=1;k<m+1;k++)     
                    col+= ( temp[k][i]*temp[k][j] );     
                coeffX[i][j] = col;     
                coeffX[j][i] = col;              
            }     
     
            /*   
            *constY的第i个元素   
            */     
            double conTemp =0 ;     
            for(k=1;k<m+1;k++)     
                    conTemp+= ( Y[k-1]*temp[k][i]);     
           constY[i][0]=conTemp;     
     
        }     
     
        /*   
        *调用Sequation方法，解线性方程组   
        */     
        result = Sequation.guassEquation(coeffX,constY,resultK,XLen,1);      
        if(result ==0 )     
        {     
            //System.out.println("The regression is failed,please check the sample point \n");     
            return result;     
        }else{     
            for(i= 0;i<n+1;i++)     
                K[i] = resultK[i][0];     
        }     
        return result;     
    }     
     
    /*****************************************************************************************   
    *模型名称：样本自优化线性回归   
    *简要说明：该模型是对简单线性回归模型的改良,其基本的考虑是所提供的样本数据中可能有些异常点使得模型的精度大大降低，系统通过一个度量函数自动   
    *         找那种对模型的拟合度最差的样本点，然后去掉该样本点再进行回归运算.本模型用实际值与模型预测值之间的平均偏差作为度量模型准确性的效   
    *         用函数。   
    *         如果平均偏差减少，标识模型在去掉噪声数据后模型拟合的效果增加，否则停止继续优化的步骤。另外，无论如何优化，都必须至少保留样本保有率   
    *         所确定的最少样本个数   
    *注：该模型尚未得到理论论证，是作者自己的经验总结   
    *函数参数:   
    *    @param  double[][]  X  自变量样本集   
    *    @param  double[]    Y  变量结果集   
    *    @param  int  n  回归变量个数   
    *    @param  double[]   K  最终回归系数（返回）   
    *    @param  int  m  样本个数   
    *    @param   double retainRate  样本最低保有率   
    *    @param  int[] LossPoint 被丢弃的样本点(返回)   
    *输出参数：   
    *    @return double res  -1:失败，1：成功   
    *******************************************************************************************/     
    public static  double optLineRegression( double[][] X,     
                                             double[] Y,     
                                             double[] K,     
                                             int n,     
                                             int m,     
                                             double retainRate ,     
                                             int[] LossPoint){     
             
        double res = -1;     
        if(n<1||m<1){     
            //System.out.println("The parameter is not normal ,please check it\n");     
            return res;          
        }     
        if(retainRate>=1.0||retainRate<=0.0){     
            //System.out.println("The retain parameter is not in 0 and 1 \n");     
            return res;     
        }     
             
        //必须确保的最小样本个数     
         
        Double Dtemp = new Double(m*retainRate);     
        int minsample = Dtemp.intValue();      
     
     
        //被丢弃的样本点的个数     
     
        int lossnum =0 ;     
     
        int[] LossPointTemp = new int[m];     
     
         
        //进行第一次回归     
     
        double temp = LineRegression(X,Y, K,n,m);      
        if(temp == 0){     
            //System.out.println("The regression operation is failed\n");     
            return res;          
        }     
     
        //第一次的平均误差     
     
        double ErrorStd = avgerror(X, Y, K,n, m);     
     
        double[][] SampleX = X;     
        double[]   SampleY = Y;     
        double[]    CoeffK = K;     
        int SampleNum = m;     
     
        /*   
        *记载样本位置的变化情况   
        */     
        int[] change = new int[m];     
        for(int k=0;k<m;k++)     
            change[k]=k;     
        int index_max = -1;     
     
        for (int i=m;i>minsample ;i-- )     
        {     
            /*   
            *找当前回归样本中的误差最大的样本index。   
            */     
            index_max = maxErrorIndex(SampleX,SampleY,CoeffK,n,SampleNum);     
     
            if(index_max == -1)     
                return -1;     
     
            /*   
            *第index_max为误差最大样本，去掉该样本   
            */     
     
            int Loss = change[index_max];     
            lossnum +=1;     
            SampleNum -=1;     
     
     
            double[][] SampleXTemp = SampleX;     
            SampleX = new double[SampleNum][n];     
     
            double[] SampleYTemp = SampleY;     
            SampleY  = new double[SampleNum];     
     
            for(int j= 0;j<index_max;j++){     
                for(int k=0;k<n;k++)     
                {     
                SampleX[j][k]=SampleXTemp[j+1][k];     
                }     
                SampleY[j]=SampleYTemp[j];     
            }     
     
            for(int j= index_max;j<SampleNum;j++){     
                for(int k=0;k<n;k++){     
                    SampleX[j][k]=SampleXTemp[j+1][k];     
                }     
                SampleY[j] = SampleYTemp[j+1];     
                change[j] = change[j+1];     
     
            }     
     
            /*   
            *利用新的样本进行回归   
            */     
            res=LineRegression(SampleX,SampleY,CoeffK,n,SampleNum);     
     
            /*   
            *比较新的预测模型误差与老模型的误差，如果没有改良，结束，否则，记载相关结果，并继续   
            */     
     
            double  ErrorOpt = avgerror(SampleX,SampleY,CoeffK,n,SampleNum);     
     
            if (ErrorOpt>=ErrorStd)//优化过程没有任何改良     
            {     
                return  1;     
            }     
            else     
            {     
                 
            /*   
            *记载样本删减情况，记载回归系数   
            */     
            LossPoint[m-i] = Loss;     
     
            K=CoeffK;     
                             
            }     
        }     
        return 1;                
    }     
     
    /************************************************************************************   
    *简要说明：计算回归模型的平均误差   
    *输入参数:   
    *    @param  double[][]  X  自变量样本集   
    *    @param  double[]    Y  变量结果集   
    *    @param  int  n  回归变量个数   
    *    @param  double[]   K  回归系数   
    *    @param  int  m  样本个数   
    *输出参数：   
    *    @return double Ierror ：-1失败 平均误差   
    *************************************************************************************/     
    public static double avgerror(double[][] X,double[] Y,double[] K,int n,int m){     
        double res = -1;     
        /*   
        *YF用于存放模型的预测结果   
        */     
        double[] YF = new double[m];      
        for(int i=0;i<m;i++){     
            YF[i] = K[0];      
            for(int j=0;j<n;j++)     
                YF[i]+=X[i][j]*K[j+1];               
        }     
             
        /*   
        *计算初始预测与真实值的误差（平均维和距）   
        */     
        res = Base.dimaddavg(Y,YF);      
        return res;     
    }     
     
     
    /************************************************************************************   
    *简要说明：计算回归模型的误差最大的样本点的index_id   
    *输入参数:   
    *    @param  double[][]  X  自变量样本集   
    *    @param  double[]    Y  变量结果集   
    *    @param  int  n  回归变量个数   
    *    @param  double[]   K  回归系数   
    *    @param  int  m  样本个数   
    *输出参数：   
    *    @return int Index_id ：-1失败    
    ************************************************************************************/     
    public static int maxErrorIndex(double[][] X,double[] Y,double[] K,int n,int m){     
        int index_id = -1;     
        /*   
        *YF用于存放模型的预测结果   
        */     
        double[] YF = new double[m];      
        for(int i=0;i<m;i++){     
            YF[i] = K[0];      
            for(int j=0;j<n;j++)     
                YF[i]+=X[i][j]*K[j+1];               
        }     
             
        /*   
        *计算误差最大的样本点的index   
        */     
        index_id = Base.maxerrordim(Y,YF);     
        return index_id;         
    }     
     
}     
