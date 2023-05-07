package bmg.dto;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class Place {
    private String name;
    private Double rating;
    private Coordinates loc;
    private String[] types;
    private String vicinity;
    private Integer priceLvl;
    private Boolean openNow;
    private String placeID;
    private String userPhotoReference;
}