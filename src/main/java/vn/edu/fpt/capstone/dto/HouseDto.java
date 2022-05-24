package vn.edu.fpt.capstone.dto;

import javax.persistence.Column;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.edu.fpt.capstone.model.Auditable;

@Data
@EqualsAndHashCode(callSuper = false)
public class HouseDto extends Auditable<String>{
	private Long id;
    private String name;
    private Long addressId;
    private boolean enable;
    private String description;
    private Long typeOfRentalId;
    private Long userId;
}