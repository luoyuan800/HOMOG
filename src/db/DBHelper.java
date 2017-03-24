package db;

import model.Goodness;
import model.Industry;
import model.Model;
import model.Rate;
import model.Stock;
import utils.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gluo on 3/22/2017.
 */
public class DBHelper {
    private final Connection connection;

    public DBHelper(String dbFile) throws SQLException {
        System.out.println("Init database");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        createDb();
    }

    public static void main(String... args) throws SQLException {
        DBHelper db = new DBHelper("homog.db");
        System.out.println(db.getStockCount());
        db.close();
    }

    private void createDb() {
        Statement statement;
        try {
            boolean create = false;
            statement = connection.createStatement();
            ResultSet rs = null;
            try {
                rs = statement.executeQuery("select count(*) from stock");
            } catch (Exception e) {
                create = true;
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
            if (create) {
                System.out.println("Creating database...");
                statement.execute("create table stock (id TEXT NOT NULL PRIMARY KEY, industry TEXT)");
                //rate table use to store both stock and industry's yield(回报率)
                //We should first record the stock's rate, and then use them to calculate out the industry's rate.
                statement.execute("create table rate (id TEXT NOT NULL PRIMARY KEY, ext_id TEXT NOT NULL, yield DOUBLE, year INTEGER, month INTEGER)");
                statement.execute("create table industry (id TEXT NOT NULL PRIMARY KEY, name TEXT)");
                statement.execute("create table goodness (id TEXT NOT NULL PRIMARY KEY, normal DOUBLE, fix DOUBLE, year INTEGER, ext_id TEXT, count INTEGER)");
                System.out.println("Finished create database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStockCount() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select  count(*) from stock");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
        return 0;
    }

    public List<Stock> queryStockByIndustry(String industry) {
        Statement statement = null;
        ArrayList<Stock> stocks = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from Stock where industry = '" + industry + "'");
            while (rs.next()) {
                Stock stock = new Stock();
                stock.setId(rs.getString("id"));
                stock.setIndustry(rs.getString("industry"));
                stocks.add(stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
        return stocks;
    }

    public Rate queryRateByExtAndDate(Model ext, int year, int month) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select id, yield from rate where ext_id = '" + ext.getId() + "' and year = '" + year + "' and month = '" + month + "'");
            if (rs.next()) {
                Rate rate = new Rate();
                rate.setId(rs.getString("id"));
                rate.setYear(year);
                rate.setMonth(month);
                rate.setYield(rs.getDouble("yield"));
                rate.setExt(ext);
                return rate;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
        return null;
    }

    public void save(Rate rate) {
        String sql = "insert into rate (id, yield, ext_id, year, month) values(?,?,?,?,?)";
        executeSQL(sql, UUID.randomUUID().timestamp(), rate.getYield(), rate.getExt().getId(), rate.getYear(), rate.getMonth());
    }

    public List<Industry> queryIndustry() {
        Statement statement = null;
        ArrayList<Industry> industries = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from industry");
            while (rs.next()) {
                Industry industry = new Industry();
                industry.setId(rs.getString("id"));
                industries.add(industry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }

        return industries;
    }

    public void save(Stock stock) {
        executeSQL("insert into stock (id, industry) values(?,?)", getId(), stock.getIndustry());
    }

    public void save(Industry industry) {
        executeSQL("insert into industry (id) values(?)", industry.getId());
    }

    public ArrayList<Integer> getYears() {
        Statement statement = null;
        ArrayList<Integer> years = new ArrayList<>(10);
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select year from rate group by year order by year ACES");
            while (rs.next()) {
                years.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
        return years;
    }

    public List<Stock> queryStock() {
        Statement statement = null;
        List<Stock> stocks = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from stock");
            while (rs.next()) {
                Stock stock = new Stock();
                stock.setId(rs.getString("id"));
                stock.setIndustry(rs.getString("industry"));
                stocks.add(stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
        return stocks;
    }

    public List<Rate> getRateByYear(String extId, int year) {
        List<Rate> rates = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from rate where ext_id = '" + extId + "' and year = " + year);
            while (rs.next()) {
                Rate rate = new Rate();
                //rate.setExt(extId);
                rate.setYear(year);
                rate.setMonth(rs.getInt("month"));
                rate.setYield(rs.getDouble("yield"));
                rate.setId(rs.getString("id"));
                rates.add(rate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
        return rates;
    }

    public Goodness queryGoodnessByYear(String extId, Integer year) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from goodness where ext_id = '" + extId + "' and year = " + year);
            if (rs.next()) {
                Goodness goodness = new Goodness();
                goodness.setId(rs.getString("id"));
                goodness.setNormal(rs.getDouble("normal"));
                goodness.setFix(rs.getDouble("fix"));
                goodness.setYear(year);
                goodness.setCount(rs.getInt("count"));
                return goodness;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }
        return null;
    }

    public void update(Goodness goodness) {
        executeSQL("update goodness set fix = ?, normal = ?, ext_id = ? where id = ?", goodness.getFix(), goodness.getNormal(), goodness.getExt().getId(), goodness.getId());
    }

    public void save(Goodness goodness) {
        executeSQL("insert into goodness (id , normal , fix , year , ext_id, count) values(?,?,?.?,?,?)", getId(), goodness.getNormal(), goodness.getFix(), goodness.getYear(), goodness.getExt().getId(), goodness.getCount());
    }

    private void executeSQL(String sql, Object... paras) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            for (int i = 0; i < paras.length; i++) {
                statement.setObject(i + 1, paras[i]);
            }
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
            Log.err("Failed to insert :" + e.getMessage());
        } finally {
            closeStatement(statement);
        }
    }

    private String getId() {
        return UUID.randomUUID().toString();
    }

    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
