package vn.edu.fpt.capstone.controller;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.edu.fpt.capstone.constant.Message;
import vn.edu.fpt.capstone.dto.PostDto;
import vn.edu.fpt.capstone.dto.ResponseObject;
import vn.edu.fpt.capstone.dto.SearchDto;
import vn.edu.fpt.capstone.dto.TransactionDto;
import vn.edu.fpt.capstone.dto.UserDto;
import vn.edu.fpt.capstone.model.PostModel;
import vn.edu.fpt.capstone.model.UserModel;
import vn.edu.fpt.capstone.response.PageableResponse;
import vn.edu.fpt.capstone.response.PostResponse;
import vn.edu.fpt.capstone.service.HouseService;
import vn.edu.fpt.capstone.service.PostService;
import vn.edu.fpt.capstone.service.PostTypeService;
import vn.edu.fpt.capstone.service.RoomService;
import vn.edu.fpt.capstone.service.TransactionService;
import vn.edu.fpt.capstone.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PostController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class.getName());

	@Autowired
	PostService postService;

	@Autowired
	RoomService roomService;

	@Autowired
	private HouseService houseService;

	@Autowired
	private PostTypeService postTypeService;
	
	@Autowired
	private UserService userService;

	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private TransactionService transactionService;

	public static int TIMESTAMP_DAY = 86400000;

	@GetMapping(value = "/post/{id}")
	public ResponseEntity<ResponseObject> getById(@PathVariable String id) {
		ResponseObject responseObject = new ResponseObject();
		try {
			Long lId = Long.valueOf(id);
			if (postService.isExist(lId)) {
				PostDto postDto = postService.findById(lId);
				responseObject.setResults(postDto);
				responseObject.setCode("200");
				responseObject.setMessageCode(Message.OK);
				LOGGER.info("getById: {}", postDto);
				return new ResponseEntity<>(responseObject, HttpStatus.OK);
			} else {
				LOGGER.error("getById: {}", "ID Post is not exist");
				responseObject.setCode("404");
				responseObject.setMessageCode(Message.NOT_FOUND);
				return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
			}
		} catch (NumberFormatException e) {
			LOGGER.error("getById: {}", e);
			responseObject.setCode("404");
			responseObject.setMessageCode(Message.NOT_FOUND);
			return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
		} catch (Exception ex) {
			LOGGER.error("getById: {}", ex);
			responseObject.setCode("500");
			responseObject.setMessageCode(Message.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/post-by-token")
	public ResponseEntity<ResponseObject> getAllByToken(@RequestHeader(value = "Authorization") String jwtToken) {
		ResponseObject responseObject = new ResponseObject();
		try {
			List<PostResponse> postDtos = postService.findAllByToken(jwtToken);
			if (postDtos == null || postDtos.isEmpty()) {
				responseObject.setResults(new ArrayList<>());
			} else {
				responseObject.setResults(postDtos);
			}
			LOGGER.info("getAll: {}", postDtos);
			responseObject.setCode("200");
			responseObject.setMessageCode(Message.OK);
			return new ResponseEntity<>(responseObject, HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("getAll: {}", ex);
			responseObject.setCode("500");
			responseObject.setMessageCode(Message.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/post")
	public ResponseEntity<ResponseObject> getAll() {
		ResponseObject responseObject = new ResponseObject();
		try {
			List<PostResponse> postDtos = postService.findAll();
			if (postDtos == null || postDtos.isEmpty()) {
				responseObject.setResults(new ArrayList<>());
			} else {
				responseObject.setResults(postDtos);
			}
			LOGGER.info("getAll: {}", postDtos);
			responseObject.setCode("200");
			responseObject.setMessageCode(Message.OK);
			return new ResponseEntity<>(responseObject, HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("getAll: {}", ex);
			responseObject.setCode("500");
			responseObject.setMessageCode(Message.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_LANDLORD') || hasRole('ROLE_USER')")
	@PostMapping(value = "/post")
	// DungTV29
	@Transactional(rollbackFor = {Exception.class, Throwable.class})
	public ResponseEntity<ResponseObject> postCreatePost(@RequestBody PostDto postDto,
			@RequestHeader(value = "Authorization") String jwtToken) {
		try {
			LOGGER.info("postHouseCreate: {}", postDto);
			if (postDto.getHouse().getId() == null || !houseService.isExist(postDto.getHouse().getId())) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ResponseObject.builder().code("406")
						.message("Create post: id house null or not exits").messageCode("CREATE_POST_FAILED").build());
			}

			if (postDto.getRoom().getId() == null || !roomService.isExist(postDto.getRoom().getId())) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ResponseObject.builder().code("406")
						.message("Create post: id room null or not exits").messageCode("CREATE_POST_FAILED").build());
			}

			if (postDto.getPostType().getId() == null || !postTypeService.isExist(postDto.getPostType().getId())) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ResponseObject.builder().code("406")
						.message("Create post: id post type null").messageCode("CREATE_POST_FAILED").build());
			}

			if (postDto.getStartDate() == null) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ResponseObject.builder().code("406")
						.message("Create post: start date null").messageCode("CREATE_POST_FAILED").build());
			}

			if (postDto.getNumberOfDays() <= 0) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ResponseObject.builder().code("406")
						.message("Create post: number of day < 0").messageCode("CREATE_POST_FAILED").build());
			}

			// set cost
			int costPerDay = postTypeService.getById(postDto.getPostType().getId()).getPrice();
			int cost = postDto.getNumberOfDays() * costPerDay;

			UserModel userModel = userService.getUserInformationByToken(jwtToken);
			if (userModel == null) {
				throw new Exception();
			}
			if(cost > userModel.getBalance()) {
				LOGGER.error("postPost: {}", "Not enough money for post");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().code("500")
						.message("Create post: not enough money!").messageCode("MONEY_NOT_ENOUGH").build());
			}else {
				userModel.setBalance(userModel.getBalance() - cost);
			}
			
			//Update balance in user
			UserModel user2 = userService.updateUser(modelMapper.map(userModel, UserDto.class));
			if (user2 == null) {
				LOGGER.error("postPost: {}", "update user fail");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().code("500")
						.message("Create post: update user fail").messageCode("CREATE_POST_FAILED").build());
			}
			
			
			TransactionDto transactionDto = new TransactionDto();
			transactionDto.setAction("MINUS");
			transactionDto.setAmount(cost);
			transactionDto.setLastBalance(userModel.getBalance());
			transactionDto.setStatus("PENDING");
			transactionDto.setTransferType("POSTING");
			transactionDto.setTransferContent("Đăng tin");
			transactionDto.setUser(modelMapper.map(user2, UserDto.class));
			
			
			//Create transaction for post
			TransactionDto transactionDto2 = transactionService.createTransaction(transactionDto);
			if (transactionDto2 == null) {
				LOGGER.error("postPost: {}", "Transaction fail");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().code("500")
						.message("Create post: transaction fail").messageCode("CREATE_POST_FAILED").build());
			}
			
			
			postDto.setCost(cost);
			
			//Create post
			PostDto model = postService.createPost(postDto);
			if (model == null) {
				throw new Exception();
			}

			return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder().code("200")
					.message("Create post: successfully").messageCode("CREATE_POST_SUCCESSFULLY").build());
		} catch (Exception e) {
			LOGGER.error("postPost: {}", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().code("500")
					.message("Create post: " + e.getMessage()).messageCode("CREATE_POST_FAILED").build());
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_LANDLORD') || hasRole('ROLE_USER')")
	@PutMapping(value = "/post-extend")
	// DungTV29
	public ResponseEntity<?> extendPost(@RequestBody PostDto postDto) {
		try {
			LOGGER.info("postExtend: {}", postDto);
			if (postDto.getId() == null || !postService.isExist(postDto.getId())) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ResponseObject.builder().code("406")
						.message("Extend post: id house null or not exits").messageCode("EXTEND_POST_FAILED").build());
			}

			if (postDto.getNumberOfDays() <= 0) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ResponseObject.builder().code("406")
						.message("Extend post: number of day < 0").messageCode("EXTEND_POST_FAILED").build());
			}

			PostModel model = postService.extendPost(postDto);
			if (model != null) {
				return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder().code("200")
						.message("Extend post: successfully").messageCode("EXTEND_POST_SUCCESSFULLY").build());
			}
			throw new Exception();
		} catch (Exception e) {
			LOGGER.error("postExtend: {}", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().code("500")
					.message("Extend post: " + e.getMessage()).messageCode("EXTEND_POST_FAILED").build());
		}
	}

	@PostMapping(value = "/posting")
	public ResponseEntity<?> getAllPosting(@RequestBody SearchDto searchDto) {
		try {
			PageableResponse pageableResponse = postService.findAllPosting(searchDto);
			LOGGER.info("get All posting: {}", pageableResponse.getResults());
			return ResponseEntity.status(HttpStatus.OK)
					.body(ResponseObject.builder().code("200").message("Get posting successfully")
							.messageCode("GET_POSTING_SUCCESSFULLY").results(pageableResponse).build());
		} catch (Exception e) {
			LOGGER.error("getAll posting: {}", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().code("500")
					.message("Get posting: " + e.getMessage()).messageCode("GET_POSTING_FAILED").build());
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_LANDLORD') || hasRole('ROLE_USER')")
	@DeleteMapping(value = "/post/{id}")
	public ResponseEntity<ResponseObject> deletePost(@PathVariable String id) {
		ResponseObject response = new ResponseObject();
		try {
			if (id == null || id.isEmpty() || !postService.isExist(Long.valueOf(id))) {
				LOGGER.error("deletePost: {}", "ID Post is not exist");
				response.setCode("404");
				response.setMessageCode(Message.NOT_FOUND);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			response.setCode("200");
			response.setMessageCode(Message.OK);
			postService.removePost(Long.valueOf(id));
			LOGGER.error("deletePost: {}", "DELETED");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (NumberFormatException ex) {
			LOGGER.error("deletePost: {}", ex);
			response.setCode("404");
			response.setMessageCode(Message.NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			LOGGER.error("deletePost: {}", e);
			response.setCode("500");
			response.setMessageCode(Message.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
