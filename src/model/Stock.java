package model;


/**
 * Created by gluo on 3/23/2017.
 */
public class Stock implements Model{
    private String id;
    private String industry;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
