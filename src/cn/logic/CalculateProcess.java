package cn.logic;

import cn.db.DBHelper;
import cn.model.Goodness;
import cn.model.Industry;
import cn.model.Rate;
import cn.model.Stock;
import cn.utils.Log;
import com.dufe.abnormal.service.Regression;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by gluo on 3/23/2017.
 */
public class CalculateProcess {
    private final DBHelper dbHelper;
    private String dataFile;
    private ExecutorService executor;
    private Set<Runnable> futures;

    public CalculateProcess(String db, String data) throws SQLException {
        this.dbHelper = new DBHelper(db);
        dataFile = data;
        executor = Executors.newFixedThreadPool(10);
        futures = new HashSet<>();
    }

    public void calculateIndustryRateOnMonth(Industry industry, int year, int month) {
        List<Stock> stocks = dbHelper.queryStockByIndustry(industry.getId());
        double indRate = 0d;
        for (Stock stock : stocks) {
            Rate rate = dbHelper.queryRateByExtAndDate(stock, year, month);
            if (rate != null) {
                indRate += rate.getYield();
            }
        }
        Rate rate = new Rate();
        rate.setExt(industry);
        rate.setMonth(month);
        rate.setYear(year);
        rate.setYield(indRate);
        dbHelper.save(rate);
    }

    //数据预处理，将csv个股的回报率文件的数据读入数据库，然后计算行业的月回报率
    public void preProcess() {
        Log.info("Pre calculate data");
        readStockDataIntoDB();
        calculateIndustryRate();
        Log.info("finished pre data calculate. Now all data have been ave to database");
    }

    //求行业修正拟合优化度，用个股的修正拟合优度求平均值
    public void calculateIndustryFixGoodness() {
        Log.info("Start calculate industry fix goodness");
        for (Industry industry : dbHelper.queryIndustry()) {
            List<Integer> years = dbHelper.getYears();
            for (int i = 0; i < years.size() - 2; i++) {
                Log.info("calculate fix goodness year on " + years.get(i) + " for " + industry.getId());
                List<Stock> stocks = dbHelper.queryStockByIndustry(industry.getId());
                double total = 0;
                for (Stock stock : stocks) {
                    Goodness stockGoodness = dbHelper.queryGoodnessByYear(stock.getId(), years.get(i));
                    if (stockGoodness != null) {
                        stockGoodness.setExt(stock);
                        total += stockGoodness.getFix();
                    }
                }
                total /= stocks.size();
                Goodness goodness = new Goodness();
                goodness.setFix(total);
                goodness.setExt(industry);
                goodness.setYear(years.get(i));
                goodness.setCount(36);
                dbHelper.save(goodness);
            }
        }
    }

    //求个股的的修正拟合优度
    public void calculateStockFixGoodness() {
        waitForFinished();
        for (Stock stock : dbHelper.queryStock()) {
            List<Integer> years = dbHelper.getYears();
            for (int i = 0; i < years.size() - 2; i++) {
                Log.info("Start to calculate stock fix goodness for " + stock.getId() + " on year " + years.get(i));
                Goodness stockGoodness = dbHelper.queryGoodnessByYear(stock.getId(), years.get(i));
                if (stockGoodness != null) {
                    stockGoodness.setExt(stock);
                    int count = stockGoodness.getCount();
                    stockGoodness.setFix(1d - ((count - 1d) / (count - 2d)) * (1d - stockGoodness.getNormal()));
                    dbHelper.update(stockGoodness);
                }
            }
        }
    }

    private void waitForFinished() {
        while (!futures.isEmpty()){
            Log.info("There are still " + futures.size() + " task for caluculate goodness!" );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.err(e);
            }
        }
    }

    //求个股的拟合优度
    public void calculateStockGoodness() {
        for (Stock stock : dbHelper.queryStock()) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    futures.add(this);
                    try {
                        Log.info("Start to calculate goodness for " + stock.getId());
                        List<Integer> years = dbHelper.getYears();
                        for (int i = 0; i < years.size() - 2; i++) {
                            Log.info("Calculate goodness year on " + years.get(i) + "for " + stock.getId() );
                            Map<String, Rate> rateByMouth = new HashMap<>();
                            for (Rate rate : dbHelper.getRateByYear(stock.getId(), years.get(i))) {
                                rateByMouth.put(rate.getMonth() + "@" + rate.getYear(), rate);
                                rate.setExt(stock);
                            }
                            for (Rate rate : dbHelper.getRateByYear(stock.getId(), years.get(i + 1))) {
                                rateByMouth.put(rate.getMonth() + "@" + rate.getYear(), rate);
                                rate.setExt(stock);
                            }
                            for (Rate rate : dbHelper.getRateByYear(stock.getId(), years.get(i + 2))) {
                                rateByMouth.put(rate.getMonth() + "@" + rate.getYear(), rate);
                                rate.setExt(stock);
                            }
                            Map<String, Rate> industryYearRates = new HashMap<>();
                            for (Rate rate : dbHelper.getRateByYear(stock.getIndustry(), years.get(i))) {
                                industryYearRates.put(rate.getMonth() + "@" + rate.getYear(), rate);
                            }
                            for (Rate rate : dbHelper.getRateByYear(stock.getIndustry(), years.get(i + 1))) {
                                industryYearRates.put(rate.getMonth() + "@" + rate.getYear(), rate);
                            }
                            for (Rate rate : dbHelper.getRateByYear(stock.getIndustry(), years.get(i + 2))) {
                                industryYearRates.put(rate.getMonth() + "@" + rate.getYear(), rate);
                            }
                            double[][] industryYield = new double[rateByMouth.size()][1];//行业收益率作为因变量
                            double[] stockYield = new double[rateByMouth.size()];//个股收益率作为变量结果
                            double[] vars = new double[2];//回归系数
                            ArrayList<Map.Entry<String, Rate>> stockList = new ArrayList<>(rateByMouth.entrySet());
                            for (int y = 0; y < stockList.size(); y++) {
                                Map.Entry<String, Rate> stockEntry = stockList.get(y);
                                Rate stockRate = stockEntry.getValue();
                                Rate industryRate = industryYearRates.get(stockEntry.getKey());
                                if (stockRate != null) {
                                    industryYield[y][0] = industryRate.getYield();
                                    stockYield[i] = stockRate.getYield();
                                }
                            }
                            Regression.LineRegression(industryYield, stockYield, vars, 1, stockList.size());
                            Goodness goodness = new Goodness();
                            goodness.setExt(stock);
                            goodness.setYear(years.get(i));
                            goodness.setCount(rateByMouth.size());
                            double stockTotalYield = 0;
                            for (int j = 0; j < stockList.size(); j++) {
                                stockTotalYield = stockTotalYield + stockYield[j];
                            }
                            double stockYieldAvg = stockTotalYield / stockList.size();
                            double ei = 0, yi = 0;
                            for (int j = 0; j < stockList.size(); j++) {
                                ei = ei + Math.pow(stockYield[j] - (vars[0] + vars[1] * industryYield[j][0]), 2);
                                yi = yi + Math.pow(stockYield[j] - stockYieldAvg, 2);
                            }
                            goodness.setNormal(1 - (ei / yi));
                            dbHelper.save(goodness);
                        }
                    }catch (Exception e){
                        Log.err(e);
                    }finally {
                        futures.remove(this);
                    }
                }
            });
        }
    }

    private void calculateIndustryRate() {
        Log.info("Start to calculate industry data");
        ArrayList<Integer> years = dbHelper.getYears();
        List<Industry> industries = dbHelper.queryIndustry();
        for (int year : years) {
            for (int month = 1; month < 13; month++) {
                for (Industry industry : industries) {
                    calculateIndustryRateOnMonth(industry, year, month);
                }
            }
        }
    }

    private void readStockDataIntoDB() {
        Log.info("Start to read data from " + dataFile);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dataFile));
            String line = bufferedReader.readLine();//Title
            Map<String, Stock> stockIdMap = new HashMap<>();
            Set<String> industryId = new HashSet<>();
            while (line != null) {
                line = bufferedReader.readLine();
                if (line != null) {
                    String[] data = line.split(",");
                    if (data.length >= 4) {
                        String id = data[0];
                        Stock stock = null;
                        if (!stockIdMap.keySet().contains(id)) {
                            Log.info("Reading data for " + id);
                            stock = new Stock();
                            stock.setId(id);
                            stock.setIndustry(data[3]);
                            stockIdMap.putIfAbsent(id, stock);
                            dbHelper.save(stock);
                            if (!industryId.contains(stock.getIndustry())) {
                                Industry industry = new Industry();
                                industry.setId(stock.getIndustry());
                                dbHelper.save(industry);
                                Log.info("Found a new industry: " + industry.getId());
                                industryId.add(industry.getId());
                            }
                        } else {
                            stock = stockIdMap.get(id);
                        }
                        Rate rate = new Rate();
                        rate.setExt(stock);
                        rate.setYield(Double.parseDouble(data[2]));
                        String[] ym = data[1].split("-");//Data should format as 2010-12
                        if (ym.length >= 2) {
                            rate.setYear(Integer.parseInt(ym[0]));
                            rate.setMonth(Integer.parseInt(ym[1]));
                            dbHelper.save(rate);
                        } else {
                            Log.err("Error format of date, will skip: " + line);
                        }
                    }

                } else {
                    Log.err("Error Format data, will skip: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.err("Reading data error!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        waitForFinished();
        executor.shutdown();
        dbHelper.close();
    }

    public void out(String fileName){
        try {
            File file = new File(fileName);
            file.deleteOnExit();
            if(file.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("行业代码,2016年产业同质化值,2015,2014,2013,2012");
                List<Integer> years = dbHelper.getYears();
                for(Industry industry : dbHelper.queryIndustry()){
                    writer.newLine();
                    writer.write(industry.getId());
                    for(int year : years){
                        Goodness goodness = dbHelper.queryGoodnessByYear(industry.getId(), year);
                        if(goodness!=null && goodness.getFix()!=0){
                            writer.write(",");
                            writer.write(String.valueOf(goodness.getFix()));
                        }
                    }
                }
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
