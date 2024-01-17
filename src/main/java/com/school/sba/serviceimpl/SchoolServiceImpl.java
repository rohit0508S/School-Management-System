package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.entity.Users;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.SchoolDataNotFoundException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repositary.SchoolRepositary;
import com.school.sba.repositary.UserRepository;
import com.school.sba.requestdto.SchoolRequestDto;
import com.school.sba.responsedto.SchoolResponseDto;
//import com.school.sba.service.SchoolRequest;
import com.school.sba.service.SchoolService;
import com.school.sba.utility.ResponseStructure;
@Service
public class SchoolServiceImpl implements SchoolService{
	
	@Autowired
	SchoolRepositary schoolRepo;
	@Autowired
	ResponseStructure<SchoolResponseDto> structure;
	@Autowired
	UserRepository userRepository;
	
	private School mapToSchool(SchoolRequestDto schoolRequestDto) {
		return School.builder()
				.schoolName(schoolRequestDto.getSchoolName())
				.contactNo(schoolRequestDto.getContactNo())
				.emailId(schoolRequestDto.getEmailId())
				.address(schoolRequestDto.getAddress())
				.build();
	}
	private SchoolResponseDto mapToSchoolResponseDto(School school) {
		
		return SchoolResponseDto.builder()
				.schoolId(school.getSchoolId())
				.schoolName(school.getSchoolName())
				.contactNo(school.getContactNo())
				.emailId(school.getEmailId())
				.address(school.getAddress())
				.build();
	}
	
	
	
	
	@Override
	public ResponseEntity<ResponseStructure<SchoolResponseDto>> createSchool(SchoolRequestDto schoolRequestDto,int userId) {
		return userRepository.findById(userId).map(u->{
			if(u.getRole().equals(UserRole.ADMIN)) {
				if(u.getSchool()==null) {
					School school=mapToSchool(schoolRequestDto);
					school=schoolRepo.save(school);
					u.setSchool(school);
					userRepository.save(u);
					structure.setStatus(HttpStatus.FOUND.value());
					structure.setMessage("School Data Found by id");
					structure.setData(mapToSchoolResponseDto(school));
					return new ResponseEntity<ResponseStructure<SchoolResponseDto>>(structure,HttpStatus.CREATED);
				}
				else
				{
					throw new IllegalArgumentException("User not found by Id");
				}
			}else
					throw new IllegalArgumentException("User not found by Id");
			
		}).orElseThrow(()-> new UserNotFoundByIdException("User not found by Id"));
	
	
	}
	
	
	
	
	@Override
	public ResponseEntity<ResponseStructure<SchoolResponseDto>> findSchoolById(int schoolId) {
		Optional<School> optional = schoolRepo.findById(schoolId);
		
		if(optional.isPresent())
		{
			School school = optional.get();
			
			SchoolResponseDto schoolResponseDto = mapToSchoolResponseDto(school);
			
			structure.setStatus(HttpStatus.FOUND.value());
			structure.setMessage("School Data Found by id");
			structure.setData(schoolResponseDto);
			
			return new ResponseEntity<ResponseStructure<SchoolResponseDto>>(structure,HttpStatus.FOUND);
		}
		else
			throw new SchoolNotFoundByIdException("School not Found to fetch data for Given Id");
		
	}

	@Override
	public ResponseEntity<ResponseStructure<SchoolResponseDto>> deleteSchoolById(int schoolId) {
		Optional<School> optional = schoolRepo.findById(schoolId);
		if(optional.isPresent())
		{
			School school = optional.get();
			
			schoolRepo.delete(school);
			
			SchoolResponseDto schoolResponseDto = mapToSchoolResponseDto(school);
			
			structure.setStatus(HttpStatus.OK.value());
			structure.setMessage("School Deleted Successfully");
			structure.setData(schoolResponseDto);
			
			return new ResponseEntity<ResponseStructure<SchoolResponseDto>>(structure,HttpStatus.OK);
		}
		else
			throw new SchoolNotFoundByIdException("School Not Found to delete for given Id");
		
	}

	@Override
	public ResponseEntity<ResponseStructure<SchoolResponseDto>> updateSchoolById(SchoolRequestDto schoolRequestDto,int schoolId) {
		Optional<School> optional = schoolRepo.findById(schoolId);
		
		if(optional.isPresent())
		{
			School oldschool = optional.get();
			
			School school = mapToSchool(schoolRequestDto);
			school.setSchoolId(oldschool.getSchoolId());
			
			School school2 = schoolRepo.save(school);
			
			SchoolResponseDto schoolResponseDto = mapToSchoolResponseDto(school2);
			
			structure.setStatus(HttpStatus.ACCEPTED.value());
			structure.setMessage("School Data Updated Successfully");
			structure.setData(schoolResponseDto);
			
			return new ResponseEntity<ResponseStructure<SchoolResponseDto>>(structure,HttpStatus.ACCEPTED);
			
		}
		else
			throw new SchoolNotFoundByIdException("School Not Found to Update for given ID");
	}

	@Override
	public ResponseEntity<ResponseStructure<List<SchoolResponseDto>>> findAllSchools() {
		
		List<School> schholList = schoolRepo.findAll();
		if(!schholList.isEmpty())
		{
			List<SchoolResponseDto> list=new ArrayList<>();
			
			for(School school : schholList)
			{
				SchoolResponseDto schoolResponseDto = mapToSchoolResponseDto(school);
				list.add(schoolResponseDto);
			}
			ResponseStructure<List<SchoolResponseDto>> structure=new ResponseStructure<>();
			
			structure.setStatus(HttpStatus.FOUND.value());
			structure.setMessage("School List Found");
			structure.setData(list);
			
			return new ResponseEntity<ResponseStructure<List<SchoolResponseDto>>>(structure,HttpStatus.FOUND);
		}

		else
			throw new SchoolDataNotFoundException("No Schools Present To fetch the Schools Data");
	}
	

}
