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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private void calculateIndustryRateOnMonth(Industry industry, int year, int month) {
        List<Rate> rates = dbHelper.queryStockRateByIndustryAndDate(industry.getId(),year, month);
        double indRate = 0d;
        for (Rate rate : rates) {
            if (rate != null) {
                indRate += rate.getYield();
            }
        }
        Rate rate = new Rate();
        rate.setExt(industry);
        rate.setMonth(month);
        rate.setYear(year);
        rate.setType(1);
        rate.setIndustry(industry.getId());
        if(!rates.isEmpty()) {
            rate.setYield(indRate / rates.size());
        }
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
                List<Stock> stocks = dbHelper.queryStockByIndustryAndDate(industry.getId(),years.get(i));
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
                goodness.setIndustry(industry.getId());
                goodness.setType(1);
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
                Log.info("Start to calculate stock fix goodness for " + stock.getNumber() + " on year " + years.get(i));
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

    //求个股的拟合优度
    public void calculateStockGoodness() {
        for (String stockId : dbHelper.queryStockId()) {
            Runnable command = new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.info("Start to calculate goodness for " + stockId);
                        List<Integer> years = dbHelper.getYears();
                        for (int i = 0; i < years.size() - 2; i++) {
                            Stock stock = dbHelper.queryStockByNumberAndDate(stockId, years.get(i));
                            Log.info("Calculate goodness year on " + years.get(i) + " for " + stock.getNumber());
                            Map<String, Rate> stockRateByMouth = new HashMap<>();
                            Map<String, Rate> industryRateByMonth = new HashMap<>();
                            ArrayList<String> stockKeyByOrder = new ArrayList<>();
                            for (Rate rate : dbHelper.getRateByYear(stock.getId(), years.get(i), 0)) {
                                stockRateByMouth.put(getRateKey(rate), rate);
                                stockKeyByOrder.add(getRateKey(rate));
                            }
                            for (Rate rate : dbHelper.getRateByYear(stock.getIndustry(), years.get(i), 1)) {
                                industryRateByMonth.put(getRateKey(rate), rate);
                            }
                            stock = dbHelper.queryStockByNumberAndDate(stockId, years.get(i + 1));
                            for (Rate rate : dbHelper.getRateByYear(stock.getId(), years.get(i + 1), 0)) {
                                stockRateByMouth.put(getRateKey(rate), rate);
                                stockKeyByOrder.add(getRateKey(rate));
                            }
                            for (Rate rate : dbHelper.getRateByYear(stock.getIndustry(), years.get(i + 1), 1)) {
                                industryRateByMonth.put(getRateKey(rate), rate);
                            }
                            stock = dbHelper.queryStockByNumberAndDate(stockId, years.get(i + 2));
                            for (Rate rate : dbHelper.getRateByYear(stock.getId(), years.get(i + 2), 0)) {
                                stockRateByMouth.put(getRateKey(rate), rate);
                                stockKeyByOrder.add(getRateKey(rate));
                            }
                            for (Rate rate : dbHelper.getRateByYear(stock.getIndustry(), years.get(i + 2), 1)) {
                                industryRateByMonth.put(getRateKey(rate), rate);
                            }
                            stock = dbHelper.queryStockByNumberAndDate(stockId, years.get(i));
                            double[][] industryYield = new double[stockRateByMouth.size()][1];//行业收益率作为因变量
                            double[] stockYield = new double[stockRateByMouth.size()];//个股收益率作为变量结果
                            double[] vars = new double[2];//回归系数
                            for (int y = 0; y < stockKeyByOrder.size(); y++) {
                                Rate stockRate = stockRateByMouth.get(stockKeyByOrder.get(y));
                                Rate industryRate = industryRateByMonth.get(stockKeyByOrder.get(y));
                                if (stockRate != null && industryRate!=null) {
                                    industryYield[y][0] = industryRate.getYield();
                                    stockYield[i] = stockRate.getYield();
                                }
                            }
                            Regression.LineRegression(industryYield, stockYield, vars, 1, stockKeyByOrder.size());
                            Log.info("Stock " + stockId + " on " + years.get(i) + " regression var: " + Arrays.toString(vars));
                            Goodness goodness = new Goodness();
                            goodness.setExt(stock);
                            goodness.setYear(years.get(i));
                            goodness.setCount(stockRateByMouth.size());
                            goodness.setIndustry(stock.getIndustry());
                            goodness.setType(0);
                            double stockTotalYield = 0;
                            for (int j = 0; j < stockKeyByOrder.size(); j++) {
                                stockTotalYield = stockTotalYield + stockYield[j];
                            }
                            double stockYieldAvg = stockTotalYield / stockKeyByOrder.size();
                            double ei = 0, yi = 0;
                            for (int j = 0; j < stockKeyByOrder.size(); j++) {
                                ei = ei + Math.pow(stockYield[j] - (vars[0] + vars[1] * industryYield[j][0]), 2);
                                yi = yi + Math.pow(stockYield[j] - stockYieldAvg, 2);
                            }
                            goodness.setNormal(1 - (ei / yi));
                            dbHelper.save(goodness);
                        }
                    } catch (Exception e) {
                        Log.err(e);
                    } finally {
                        futures.remove(this);
                    }
                }
            };
            futures.add(command);
            executor.execute(command);
        }
    }

    private String getRateKey(Rate rate) {
        return rate.getMonth() + "@" + rate.getYear();
    }

    public void close() {
        waitForFinished();
        executor.shutdown();
        dbHelper.close();
    }

    public void out(String fileName) {
        try {
            waitForFinished();
            File file = new File(fileName);
            file.deleteOnExit();
            if (file.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("行业代码,2016年产业同质化值,2015,2014,2013,2012");
                List<Integer> years = dbHelper.getYears();
                for (Industry industry : dbHelper.queryIndustry()) {
                    writer.newLine();
                    writer.write(industry.getId());
                    for (int year : years) {
                        Goodness goodness = dbHelper.queryGoodnessByYear(industry.getId(), year);
                        if (goodness != null && goodness.getFix() != 0) {
                            writer.write(",");
                            writer.write(String.valueOf(goodness.getFix()));
                        }
                    }
                }
                writer.flush();
                writer.close();
            }else{
                Log.err("failed to output result");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForFinished() {
        while (!futures.isEmpty()) {
            Log.info("There are still " + futures.size() + " task for caluculate goodness!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.err(e);
            }
        }
    }

    public void calculateIndustryRate() {
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
                    if (data.length >= 4 && data[2]!=null && !data[2].isEmpty()) {
                        int year ;
                        int month ;
                        String[] ym = data[1].split("-");//Data should format as 2010-12
                        if (ym.length >= 2) {
                            year = Integer.parseInt(ym[0]);
                            month = Integer.parseInt(ym[1]);
                        } else {
                            Log.err("Error format of date, will skip: " + line);
                            continue;
                        }
                        String id = data[0];
                        Stock stock;
                        if (!stockIdMap.keySet().contains(id + "@" + year)) {
                            Log.info("Reading data for " + id + "@" + year);
                            stock = new Stock();
                            stock.setNumber(id);
                            stock.setIndustry(data[3]);
                            stock.setYear(year);
                            stockIdMap.putIfAbsent(id + "@" + year, stock);
                            dbHelper.save(stock);
                            if (!industryId.contains(stock.getIndustry())) {
                                Industry industry = new Industry();
                                industry.setId(stock.getIndustry());
                                dbHelper.save(industry);
                                Log.info("Found a new industry: " + industry.getId());
                                industryId.add(industry.getId());
                            }
                        } else {
                            stock = stockIdMap.get(id + "@" + year);
                        }
                        Rate rate = new Rate();
                        rate.setExt(stock);
                        rate.setYield(Double.parseDouble(data[2]));
                        rate.setYear(year);
                        rate.setMonth(month);
                        rate.setIndustry(stock.getIndustry());
                        rate.setType(0);
                        dbHelper.save(rate);

                    } else {
                        Log.err("Error Format data, will skip: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.err("Reading data error!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
