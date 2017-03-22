import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public void createDb() {
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
            if(create) {
                System.out.println("Creating database...");
                statement.execute("create table stock (id TEXT NOT NULL PRIMARY KEY, industry TEXT)");
                //rate table use to store both stock and industry's yield(回报率)
                //We should first record the stock's rate, and then use them to calculate out the industry's rate.
                statement.execute("create table rate (id TEXT NOT NULL PRIMARY KEY, ext_id TEXT NOT NULL, yield DOUBLE, year INTEGER, month INTEGER)");
                statement.execute("create table industry (id TEXT NOT NULL PRIMARY KEY, name TEXT)");
                statement.execute("create table goodness (id TEXT NOT NULL PRIMARY KEY, normal DOUBLE, fix DOUBLE, year INTEGER, ext_id)");
                System.out.println("Finished create database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStockCount(){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select  count(*) from stock");
            if(rs.next()){
                rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeStatement(statement);
        }
        return 0;
    }

    public void getStockRateByMonth(String stockId, int month){
        String sql = "select * from rate where ext_id = ? and month = ?";
    }

    private void closeStatement(Statement statement) {
        if(statement!=null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String...args) throws SQLException {
        DBHelper db = new DBHelper("homog.db");
        System.out.println(db.getStockCount());
        db.close();
    }


}
