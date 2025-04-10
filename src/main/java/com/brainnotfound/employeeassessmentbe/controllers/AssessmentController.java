package com.brainnotfound.employeeassessmentbe.controllers;

import com.brainnotfound.employeeassessmentbe.DTO.response.AssessmentResponse;
import com.brainnotfound.employeeassessmentbe.DTO.ResponseObject;
import com.brainnotfound.employeeassessmentbe.DTO.request.AssessmentMy;
import com.brainnotfound.employeeassessmentbe.DTO.request.AssessmentRequest;
import com.brainnotfound.employeeassessmentbe.DTO.response.AssessmentList;
import com.brainnotfound.employeeassessmentbe.services.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessment")
public class AssessmentController {
    @Autowired
    private AssessmentService assessmentService;

    @Operation(summary = "Create assessment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created")
    })
    @PostMapping
    public ResponseObject<AssessmentResponse> createAssessment(@RequestBody AssessmentRequest dto) {
        return ResponseObject.<AssessmentResponse>builder()
                .status(201)
                .message("Created")
                .data(assessmentService.createAssessment(dto))
                .build();
    }

    @Operation(summary = "Get all assessments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping("/all")
    public ResponseObject<List<AssessmentResponse>> getAllAssessments() {
        var supervisorId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseObject.<List<AssessmentResponse>>builder()
                .status(200)
                .message("success")
                .data(assessmentService.getAllAssessments(supervisorId))
                .build();
    }


    @Operation(summary = "Get assessment by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping("/{id}")
    public ResponseObject<AssessmentResponse> getAssessmentById(@PathVariable("id") Long id) {
        return ResponseObject.<AssessmentResponse>builder()
                .status(200)
                .message("success")
                .data(assessmentService.getAssessment(id))
                .build();
    }

    @Operation(summary = "Update assessment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updated")
    })
    @PutMapping("/{id}")
    public ResponseObject<AssessmentResponse> updateAssessment(@PathVariable("id") Long id, @RequestBody AssessmentRequest dto) {
        return ResponseObject.<AssessmentResponse>builder()
                .status(200)
                .message("Updated")
                .data(assessmentService.updateAssessment(id, dto))
                .build();
    }

    @Operation(summary = "Delete assessment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deleted")
    })
    @DeleteMapping("/{id}")
    public ResponseObject<Void> deleteAssessment(@PathVariable("id") Long id) {
        assessmentService.deleteAssessment(id);
        return ResponseObject.<Void>builder()
                .status(204)
                .message("Deleted")
                .build();
    }

    @Operation(summary = "Get my assessments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping("/me")
    public ResponseObject<List<AssessmentResponse>> getMyAssessments() {
        var userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        return ResponseObject.<List<AssessmentResponse>>builder()
                .status(200)
                .message("Success")
                .data(assessmentService.getMyAssessments(userId))
                .build();
    }
    @Operation(summary = "Get my feedback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })

    @GetMapping("/my/feedback")
    public ResponseObject<List<String>> getMyFeedback() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userIdLong = Long.parseLong(userId);
        System.out.println(userIdLong);
        return ResponseObject.<List<String>>builder()
                .status(200)
                .message("Success")
                .data(assessmentService.getMyFeedback(userIdLong))
                .build();
    }
    @Operation(summary = "Post my feedback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success created")
    })
    @PostMapping("/my/feedback")
    public ResponseObject<String> postMyFeedback(@RequestBody AssessmentMy req) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userIdLong = Long.parseLong(userId);
        return ResponseObject.<String>builder()
                .status(201)
                .message("Success created")
                .data(assessmentService.postMyFeedback(userIdLong, req))
                .build();
    }
    @Operation(summary = "Update my feedback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success update")
    })
    @PutMapping("/my/feedback/{assessId}")
    public ResponseObject<String> updateMyFeedback(@PathVariable("assessId") Long assessId, @RequestBody AssessmentMy req) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userIdLong = Long.parseLong(userId);
        return ResponseObject.<String>builder()
                .status(200)
                .message("Success updated")
                .data(assessmentService.updateMyFeedback(assessId, userIdLong, req))
                .build();
    }
    @Operation(summary = "Delete my feedback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success deleted")
    })
    @DeleteMapping("/my/feedback/{assessId}")
    public ResponseObject<String> deleteMyFeedback(@PathVariable("assessId") Long assessId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userIdLong = Long.parseLong(userId);
        assessmentService.deleteMyFeedback(assessId, userIdLong);
        return ResponseObject.<String>builder()
                .status(204)
                .message("Deleted")
                .build();
    }

    @GetMapping("/supervisee")
    public ResponseObject<List<AssessmentList>> getMethodName() {
        ResponseObject<List<AssessmentList>> responseObject = ResponseObject.<List<AssessmentList>>builder()
                                                                                    .status(200)
                                                                                    .data(assessmentService.getSuperviseeAssessment())
                                                                                    .build();
        return responseObject;
    }
    @Operation(summary = "Get all assessments of a specific supervisee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/supervisee/{id}")
    public ResponseObject<List<AssessmentResponse>> getAssessmentsBySuperviseeId(@PathVariable("id") Long id) {
        return ResponseObject.<List<AssessmentResponse>>builder()
                .status(200)
                .message("Success")
                .data(assessmentService.getAssessmentsBySuperviseeId(id))
                .build();
    }
}
