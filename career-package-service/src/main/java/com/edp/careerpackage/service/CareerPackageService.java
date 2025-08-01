package com.edp.careerpackage.service;

import com.edp.careerpackage.model.careerpackage.CareerPackageCreationRequestDto;
import com.edp.careerpackage.model.careerpackage.CareerPackageResponseDto;

public interface CareerPackageService {

    CareerPackageResponseDto getCareerPackage();

    CareerPackageResponseDto createCareerPackage(CareerPackageCreationRequestDto request);
}
