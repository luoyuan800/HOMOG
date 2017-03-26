package cn.model;

/**
 * Created by gluo on 3/23/2017.
 */
public class Rate implements Model{
    private String id;
    private Model ext;
    private int month;
    private int year;
    private double yield;
    private String industry;
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Model getExt() {
        return ext;
    }

    public void setExt(Model ext) {
        this.ext = ext;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getYield() {
        return yield;
    }

    public void setYield(double yield) {
        this.yield = yield;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
