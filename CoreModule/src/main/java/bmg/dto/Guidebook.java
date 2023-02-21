package bmg.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class Guidebook {
    private String propertyID;
    private String propertyName;
    private String propertyType;

    private Integer capacity;

    private String pets;

    private List<String> amenities;

    private String propertyBio;

    private ArrayList<String> faq;

    private List<String> policies;

    private List<String> propertySpecificQ;

    private List<String> hostRecommended;

    private List<String> hostServices;

    private String checkininstr;

    private String checkoutinstr;


}
