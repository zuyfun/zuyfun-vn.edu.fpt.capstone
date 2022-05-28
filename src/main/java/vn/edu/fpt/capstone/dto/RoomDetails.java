package vn.edu.fpt.capstone.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoomDetails {
	private int roomCount = 0;
	private int roomAvailability = 0;
}
