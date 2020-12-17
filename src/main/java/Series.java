import java.io.Serializable;

public class Series implements Serializable {
    public String name;
    public String href;
    public Integer rating;
    public String description;
    public Float score;


    public Series(String name, String href, Integer rating, String description) {
        this.name = name;
        this.href = href;
        this.rating = rating;
        this.description = description;
        this.score = (float) 0.0;
    }


    public Series(String name, String href, String description, Float score) {
        this.name = name;
        this.href = href;
        this.description = description;
        this.score = score;
        this.rating = 0;
    }

}
