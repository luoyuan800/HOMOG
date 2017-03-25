package cn.model;

/**
 * Created by gluo on 3/23/2017.
 */
public class Industry implements Model {
    private String id;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
