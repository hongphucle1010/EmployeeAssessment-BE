package com.brainnotfound.employeeassessmentbe.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.brainnotfound.employeeassessmentbe.DTO.AssessmentDto;
import com.brainnotfound.employeeassessmentbe.DTO.request.AssessmentReq;
import com.brainnotfound.employeeassessmentbe.DTO.response.AssessmentList;
import com.brainnotfound.employeeassessmentbe.exception.AppException;
import com.brainnotfound.employeeassessmentbe.exception.ErrorCode;
import com.brainnotfound.employeeassessmentbe.models.Assessment;
import com.brainnotfound.employeeassessmentbe.models.Criteria;
import com.brainnotfound.employeeassessmentbe.models.User;
import com.brainnotfound.employeeassessmentbe.repositories.AssessmentRepository;
import com.brainnotfound.employeeassessmentbe.repositories.CriteriaRepository;
import com.brainnotfound.employeeassessmentbe.repositories.UserRepository;

@Service
public class AssessmentService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    public AssessmentDto createAssessment(AssessmentDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Criteria criteria = criteriaRepository.findById(dto.getCriteriaId())
                .orElseThrow(() -> new AppException(ErrorCode.CRITERIA_NOT_EXISTED));

        Assessment assessment = new Assessment();
        assessment.setUser(user);
        assessment.setCriteria(criteria);
        assessment.setScore(dto.getScore());
        assessment.setComment(dto.getComment());

        assessmentRepository.save(assessment);
        return new AssessmentDto(assessment);
    }

    public List<AssessmentDto> getAllAssessments() {
        List<Assessment> assessments = assessmentRepository.findAll();
        return assessments.stream().map(AssessmentDto::new).collect(Collectors.toList());
    }

    public AssessmentDto getAssessment(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSESSMENT_NOT_EXISTED));
        return new AssessmentDto(assessment);
    }

    public AssessmentDto updateAssessment(Long id, AssessmentDto dto) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSESSMENT_NOT_EXISTED));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Criteria criteria = criteriaRepository.findById(dto.getCriteriaId())
                .orElseThrow(() -> new AppException(ErrorCode.CRITERIA_NOT_EXISTED));

        assessment.setUser(user);
        assessment.setCriteria(criteria);
        assessment.setScore(dto.getScore());
        assessment.setComment(dto.getComment());

        assessmentRepository.save(assessment);
        return new AssessmentDto(assessment);
    }

    public List<AssessmentDto> getMyAssessments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Assessment> assessments = assessmentRepository.getAssessmentByUser(user);
        return assessments.stream().map(AssessmentDto::new).collect(Collectors.toList());
    }

    public void deleteAssessment(Long id) {
        assessmentRepository.deleteById(id);
    }

    public List<AssessmentDto> getAssessmentByUserId(Long userId) {
        List<Assessment> assessments = assessmentRepository.findByUserId(userId);
        if (assessments.isEmpty()) {
            throw new AppException(ErrorCode.ASSESSMENT_NOT_EXISTED);
        }
        return assessments.stream().map(AssessmentDto::new).collect(Collectors.toList());
    }

    public List<String> getMyFeedback(Long userId) {
        List<AssessmentDto> assessmentDto = getAssessmentByUserId(userId);
        return assessmentDto.stream()
                .map(AssessmentDto::getComment)
                .collect(Collectors.toList());
    }

    public String postMyFeedback(long userIdLong, AssessmentReq req) {
        AssessmentDto newAssessmentDto = new AssessmentDto(userIdLong, req.getCriteriaId(), req.getScore(), req.getComment());
        createAssessment(newAssessmentDto);
        return newAssessmentDto.getComment();
    }

    public String updateMyFeedback(long assessId, long userIdLong, AssessmentReq req) {
        List<AssessmentDto> assessmentDto = getAssessmentByUserId(userIdLong);

        AssessmentDto newAssessmentDto = new AssessmentDto(userIdLong, req.getCriteriaId(), req.getScore(), req.getComment());
        updateAssessment(assessId, newAssessmentDto);
        return newAssessmentDto.getComment();
    }
    public void deleteMyFeedback(long assessId, long userIdLong) {
        List<AssessmentDto> assessmentDto = getAssessmentByUserId(userIdLong);
        deleteAssessment(assessId);
    }

    public List<AssessmentList> getSuperviseeAssessment(){
        var context = SecurityContextHolder.getContext();
        Long id = Long.parseLong(context.getAuthentication().getName());

        User supvisor = userService.getUserById(id);

        List<User> users = userRepository.findBySupervisor(supvisor);

        // if (users.isEmpty()){
        //     throw new AppException(ErrorCode.INVALID_KEY);
        // }

        List<AssessmentList> response = new ArrayList<AssessmentList>();
        for (User user : users){
            Assessment assessment = assessmentRepository.findByUser(user);
            AssessmentList assessmentList = AssessmentList.builder()
                                                            .id(assessment.getId())
                                                            .user(user)
                                                            .build();
            response.add(assessmentList);
        }
        return response;
    }
}