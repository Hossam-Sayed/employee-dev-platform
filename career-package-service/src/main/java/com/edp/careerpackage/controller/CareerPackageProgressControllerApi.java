package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.careerpackage.CareerPackageProgressDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Career Package Progress", description = "Track total progress of current user's career package")
@RequestMapping("/api/career-package-progress")
public interface CareerPackageProgressControllerApi {

    @Operation(summary = "Get total progress", description = "Returns the total percent progress of the current user's active career package")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Career package not found")
    })
    @GetMapping
    ResponseEntity<CareerPackageProgressDto> getPackageProgress();
}
