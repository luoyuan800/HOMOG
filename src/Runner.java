import logic.CalculateProcess;

import java.sql.SQLException;

/**
 * Created by gluo on 3/24/2017.
 */
public class Runner {
    public static void main(String...args) throws SQLException {
        CalculateProcess process = new CalculateProcess("homog.db","data.csv");
        process.preProcess();
        process.calculateStockGoodness();
        process.calculateStockFixGoodness();
        process.calculateIndustryFixGoodness();
    }
}
