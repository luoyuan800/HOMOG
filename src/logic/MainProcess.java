package logic;

import db.DBHelper;
import model.Industry;
import model.Rate;
import model.Stock;
import utils.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by gluo on 3/23/2017.
 */
public class MainProcess {
    private final DBHelper dbHelper;
    private String dataFile;

    public MainProcess(String db, String data) throws SQLException {
        this.dbHelper = new DBHelper(db);
        dataFile = data;
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

    public void preProcess() {
        Log.info("Pre calculate data");
        readStockDataIntoDB();
        calculateIndustryRate();
        Log.info("finished pre data calculate. Now all data have been ave to database");
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
}
