package vn.edu.fpt.capstone.service.impl;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.fpt.capstone.dto.ListIdDto;
import vn.edu.fpt.capstone.dto.MapPositionDto;
import vn.edu.fpt.capstone.model.MapPositionModel;
import vn.edu.fpt.capstone.repository.MapPositionRepository;
import vn.edu.fpt.capstone.service.MapPositionService;

@Service
public class MapPositionServiceImpl implements MapPositionService{
	@Autowired
	private MapPositionRepository mapPositionRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public List<MapPositionDto> getAll() {
		return Arrays.asList(modelMapper.map(mapPositionRepo.findAll(), MapPositionDto[].class));
	}

	@Override
	public void createOrUpdate(MapPositionDto mapPositionDto) {
		MapPositionModel mapPositionModel = modelMapper.map(mapPositionDto, MapPositionModel.class);
		mapPositionRepo.save(mapPositionModel);
		
	}

	@Override
	public void delete(MapPositionDto mapPositionDto) {
		mapPositionRepo.deleteById(mapPositionDto.getId());;
		
	}

	@Override
	public void deleteByListId(ListIdDto list) {
		for (long id : list.getList()) {
			mapPositionRepo.deleteById(id);
		}
		
	}

}