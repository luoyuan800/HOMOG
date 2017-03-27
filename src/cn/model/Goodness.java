package cn.model;

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
    private String industry;
    private int type;
    private double intercept;
    private double coefficient;

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

    public String  getIndustry() {
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

    public double getIntercept() {
        return intercept;
    }

    public void setIntercept(double intercept) {
        this.intercept = intercept;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }
}
