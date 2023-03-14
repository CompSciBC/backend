package bmg.dto;

import lombok.Data;
@Data
public class Guidebook {
    private String propertyID;
    private String propertyName;
    private String propertyType;
    private Integer capacity;
    private String pets;
    private String[] amenities;
    private String[] propertyBio;
    private Faq[] faq;
    private String[] policies;
    private String[] askGuestTheseQuestionsinSurvey;
    private String[] hostRecommended;
    private String[] hostServices;
    private String checkininstr;
    private String checkoutinstr;

}
