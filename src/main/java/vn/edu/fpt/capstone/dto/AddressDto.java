package vn.edu.fpt.capstone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AddressDto extends BaseDto{
    @JsonProperty("id")
    private Long id;
    @JsonProperty("xaId")
    private Long xaId;
    @JsonProperty("street")
    private Long street;
    @JsonProperty("longiude")
    private String longiude;
    @JsonProperty("latitude")
    private String latitude;
}
