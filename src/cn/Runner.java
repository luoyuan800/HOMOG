package cn;

import cn.logic.CalculateProcess;
import cn.utils.Log;

import java.sql.SQLException;

/**
 * Created by gluo on 3/24/2017.
 */
public class Runner {
    public static void main(String...args) throws SQLException {
        try {
            CalculateProcess process = new CalculateProcess("homog.cn.db", "data.csv");
            //process.preProcess();
            process.calculateIndustryRate();
            process.calculateStockGoodness();
            process.calculateStockFixGoodness();
            process.calculateIndustryFixGoodness();
            process.out("result.csv");
            process.close();
        }catch (Exception e){
            Log.err(e);
        }
    }
}
