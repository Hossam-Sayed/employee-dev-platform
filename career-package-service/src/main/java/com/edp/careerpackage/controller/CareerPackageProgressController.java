package com.edp.careerpackage.controller;

import com.edp.careerpackage.model.careerpackage.CareerPackageProgressDto;
import com.edp.careerpackage.service.CareerPackageProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CareerPackageProgressController implements CareerPackageProgressControllerApi {

    private final CareerPackageProgressService progressService;

    @Override
    public ResponseEntity<CareerPackageProgressDto> getPackageProgress() {
        CareerPackageProgressDto dto = progressService.getCurrentUserProgress();
        return ResponseEntity.ok(dto);
    }
}
