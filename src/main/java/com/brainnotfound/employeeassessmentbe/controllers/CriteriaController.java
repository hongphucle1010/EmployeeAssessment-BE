package com.brainnotfound.employeeassessmentbe.controllers;

import com.brainnotfound.employeeassessmentbe.DTO.ResponseObject;
import com.brainnotfound.employeeassessmentbe.models.Criteria;
import com.brainnotfound.employeeassessmentbe.services.CriteriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/criteria")
public class CriteriaController {

    @Autowired
    private CriteriaService criteriaService;

    @Operation(summary = "Create criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created")
    })
    @PostMapping
    public ResponseObject<Criteria> createCriteria(@RequestBody Criteria criteria) {
        return ResponseObject.<Criteria>builder()
                .status(201)
                .message("created")
                .data(criteriaService.save(criteria))
                .build();
    }

    @Operation(summary = "Get all criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping
    public ResponseObject<List<Criteria>> getCriteria() {
        return ResponseObject.<List<Criteria>>builder()
                .status(200)
                .message("success")
                .data(criteriaService.findAll())
                .build();
    }

    @Operation(summary = "Get criteria by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping("/{id}")
    public ResponseObject<Criteria> getCriteriaById(@PathVariable("id") Long id) {
        return ResponseObject.<Criteria>builder()
                .status(200)
                .message("success")
                .data(criteriaService.findById(id))
                .build();
    }
}
