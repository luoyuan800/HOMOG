package model;

/**
 * Created by gluo on 3/23/2017.
 */
public class Goodness implements Model {
    private String id;
    private double normal;
    private double fix;
    private int year;
    private Model ext;
    private int count;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getFix() {
        return fix;
    }

    public void setFix(double fix) {
        this.fix = fix;
    }

    public double getNormal() {
        return normal;
    }

    public void setNormal(double normal) {
        this.normal = normal;
    }

    public Model getExt() {
        return ext;
    }

    public void setExt(Model ext) {
        this.ext = ext;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
