package cn;

import cn.logic.CalculateProcess;

import java.sql.SQLException;

/**
 * Created by gluo on 3/24/2017.
 */
public class Runner {
    public static void main(String...args) throws SQLException {
        CalculateProcess process = new CalculateProcess("homog.cn.db","test.csv");
        process.preProcess();
        process.calculateStockGoodness();
        process.calculateStockFixGoodness();
        process.calculateIndustryFixGoodness();
        process.out("result.csv");
        process.close();
    }
}
