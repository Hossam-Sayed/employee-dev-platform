package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.careerpackage.CareerPackageCreationRequestDto;
import com.edp.careerpackage.model.careerpackage.CareerPackageResponseDto;
import com.edp.careerpackage.service.CareerPackageService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class CareerPackageController implements CareerPackageControllerApi {

    private final CareerPackageService careerPackageService;

    @Override
    public ResponseEntity<CareerPackageResponseDto> getCareerPackage() {
        CareerPackageResponseDto response = careerPackageService.getCareerPackage();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CareerPackageResponseDto> createCareerPackage(UriComponentsBuilder uriBuilder) {
        CareerPackageResponseDto created = careerPackageService.createCareerPackage();
        return ResponseEntity.created(uriBuilder
                .path("/api/career-packages/{id}")
                .buildAndExpand(created.getId())
                .toUri()).body(created);
    }
}
