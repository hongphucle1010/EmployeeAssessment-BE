package com.brainnotfound.employeeassessmentbe.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.brainnotfound.employeeassessmentbe.DTO.AssessmentDto;
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
    private UserRepository userRepository;

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    public AssessmentDto createAssessment(AssessmentDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Criteria criteria = criteriaRepository.findById(dto.getCriteriaId())
                .orElseThrow(() -> new RuntimeException("Criteria not found"));

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

    public AssessmentDto getAssessmentById(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        return new AssessmentDto(assessment);
    }

    public void deleteAssessment(Long id) {
        assessmentRepository.deleteById(id);
    }

    public List<AssessmentList> getSuperviseeAssessment(){
        var context = SecurityContextHolder.getContext();
        Long id = Long.parseLong(context.getAuthentication().getName());

        List<User> users = userRepository.findBySupervisor(id);

        if (users.isEmpty()){
            throw new AppException(ErrorCode.INVALID_KEY);
        }

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