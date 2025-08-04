package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.careerpackage.CareerPackageResponseDto;
import com.edp.careerpackage.model.careerpackage.CareerPackageCreationRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(
        name = "Career Package",
        description = "View or initialize career package"
)
@Validated
@RequestMapping("/api/career-package")
public interface CareerPackageControllerApi {

    @Operation(
            summary = "Get current user's career package",
            description = "Fetches the existing career package for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Career package found"),
            @ApiResponse(responseCode = "404", description = "Career package not found")
    })
    @GetMapping
    ResponseEntity<CareerPackageResponseDto> getCareerPackage();

    @Operation(
            summary = "Create a new career package",
            description = "Initializes a career package for the current user using department and position relevant template"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Career package created"),
            @ApiResponse(responseCode = "400", description = "Invalid department or position"),
            @ApiResponse(responseCode = "409", description = "Career package already exists")
    })
    @PostMapping
    ResponseEntity<CareerPackageResponseDto> createCareerPackage(UriComponentsBuilder uriBuilder);
}
