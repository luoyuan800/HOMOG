package cn.db;

import cn.model.Goodness;
import cn.model.Industry;
import cn.model.Rate;
import cn.model.Stock;
import cn.utils.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gluo on 3/22/2017.
 */
public class DBHelper {
    private final Connection connection;

    public DBHelper(String dbFile) throws SQLException {
        Log.info("Init database");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        createDb();
    }

    public static void main(String... args) throws SQLException {
        DBHelper db = new DBHelper("homog.cn.db");
        System.out.println(db.queryStockNumber());
        db.close();
    }

    public List<Rate> queryStockRateByIndustryAndDate(String industry, int year, int month) {
        List<Rate> rates = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from rate where industry = '" + industry + "' and year = " + year + " and month = " + month + " and type = " + 0);
            while (rs.next()) {
                Rate rate = new Rate();
                rate.setId(rs.getString("id"));
                rate.setIndustry(industry);
                rate.setMonth(month);
                rate.setYear(year);
                rate.setType(0);
                rate.setYield(rs.getDouble("yield"));
                rates.add(rate);
            }
        } catch (SQLException e) {
            Log.err(e);
        } finally {
            closeStatement(statement);
        }
        return rates;
    }

    public Stock queryStockByNumberAndDate(String stockId, int year) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from stock where number = '" + stockId + "' and year = " + year);
            if (rs.next()) {
                return buildStock(rs);
            }
        } catch (SQLException e) {
            Log.err(e);
        } finally {
            closeStatement(statement);
        }
        return null;
    }

    public List<Stock> queryStockByIndustryAndDate(String industryId, Integer year) {
        List<Stock> stocks = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from stock where industry = '" + industryId + "' and year = " + year);
            while (rs.next()) {
                stocks.add(buildStock(rs));
            }
        } catch (SQLException e) {
            Log.err(e);
        } finally {
            closeStatement(statement);
        }
        return stocks;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            Log.err(e);
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
            Log.err(e);
        } finally {
            closeStatement(statement);
        }
        return 0;
    }

    public void save(Rate rate) {
        String sql = "insert into rate (id, yield, ext_id, year, month, industry, type) values(?,?,?,?,?,?,?)";
        executeSQL(sql, getId(), rate.getYield(), rate.getExt().getId(), rate.getYear(), rate.getMonth(), rate.getIndustry(),rate.getType());
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
            Log.err(e);
        } finally {
            closeStatement(statement);
        }

        return industries;
    }

    public void save(Stock stock) {
        stock.setId(getId());
        executeSQL("insert into stock (id, industry, year, number) values(?,?,?,?)", stock.getId(), stock.getIndustry(), stock.getYear(), stock.getNumber());
    }

    public void save(Industry industry) {
        executeSQL("insert into industry (id) values(?)", industry.getId());
    }

    public ArrayList<Integer> getYears() {
        Statement statement = null;
        ArrayList<Integer> years = new ArrayList<>(10);
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select year from rate group by year order by year DESC");
            while (rs.next()) {
                years.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            Log.err(e);
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
                stocks.add(buildStock(rs));
            }
        } catch (SQLException e) {
            Log.err(e);
        } finally {
            closeStatement(statement);
        }
        return stocks;
    }

    public List<String> queryStockNumber() {
        Statement statement = null;
        List<String> stocks = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select number from stock group by number");
            while (rs.next()) {
                stocks.add(rs.getString("number"));
            }
        } catch (SQLException e) {
            Log.err(e);
        } finally {
            closeStatement(statement);
        }
        return stocks;
    }

    public List<Rate> getRateByYear(String extId, int year, int type) {
        List<Rate> rates = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from rate where ext_id = '" + extId + "' and year = " + year + " and type = " + type);
            while (rs.next()) {
                Rate rate = new Rate();
                //rate.setExt(extId);
                rate.setYear(year);
                rate.setMonth(rs.getInt("month"));
                rate.setYield(rs.getDouble("yield"));
                rate.setId(rs.getString("id"));
                switch (type) {
                    case 0:
                        Stock stock = new Stock();
                        stock.setId(extId);
                        rate.setExt(stock);
                        break;
                    case 1:
                        Industry industry = new Industry();
                        industry.setId(extId);
                        rate.setExt(industry);
                        break;
                }
                rates.add(rate);
            }
        } catch (SQLException e) {
            Log.err(e);
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
            Log.err(e);
        } finally {
            closeStatement(statement);
        }
        return null;
    }

    public void update(Goodness goodness) {
        executeSQL("update goodness set fix = ?, normal = ?, ext_id = ? where id = ?",
                goodness.getFix(), goodness.getNormal(), goodness.getExt().getId(), goodness.getId());
    }

    public void save(Goodness goodness) {
        executeSQL("insert into goodness (id , normal , fix , year , ext_id, count, industry, type, intercept, coefficient) values(?,?,?,?,?,?,?,?,?,?)",
                getId(), goodness.getNormal(), goodness.getFix(), goodness.getYear(), goodness.getExt().getId(),
                goodness.getCount(), goodness.getIndustry(), goodness.getType(), goodness.getIntercept(), goodness.getCoefficient());
    }

    private Stock buildStock(ResultSet rs) throws SQLException {
        Stock stock = new Stock();
        stock.setId(rs.getString("id"));
        stock.setNumber(rs.getString("number"));
        stock.setYear(rs.getInt("year"));
        stock.setIndustry(rs.getString("industry"));
        return stock;
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
                statement.execute("create table stock (id TEXT NOT NULL PRIMARY KEY, industry TEXT, year INTEGER, number TEXT)");
                //rate table use to store both stock and industry's yield(回报率)
                //We should first record the stock's rate, and then use them to calculate out the industry's rate.
                statement.execute("create table rate (id TEXT NOT NULL PRIMARY KEY, ext_id TEXT NOT NULL, yield DOUBLE, year INTEGER, month INTEGER, industry TEXT, type INTEGER)");
                statement.execute("create table industry (id TEXT NOT NULL PRIMARY KEY, name TEXT)");
                statement.execute("create table goodness (id TEXT NOT NULL PRIMARY KEY, normal DOUBLE, fix DOUBLE, " +
                        "year INTEGER, ext_id TEXT, count INTEGER, industry TEXT, type INTEGER, intercept DOUBLE, coefficient DOUBLE)");
                Log.info("Finished create database");
            }
        } catch (SQLException e) {
            Log.err(e);
        }
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
            Log.err(e);
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
                Log.err(e);
            }
        }
    }

}
