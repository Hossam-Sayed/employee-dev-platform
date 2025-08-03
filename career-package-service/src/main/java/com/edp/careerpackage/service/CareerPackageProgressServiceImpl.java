package com.edp.careerpackage.service;

import com.edp.careerpackage.data.entity.CareerPackage;
import com.edp.careerpackage.data.entity.CareerPackageProgress;
import com.edp.careerpackage.data.repository.CareerPackageRepository;
import com.edp.careerpackage.mapper.CareerPackageMapper;
import com.edp.careerpackage.model.careerpackage.CareerPackageProgressDto;
import com.edp.careerpackage.security.jwt.JwtUserContext;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CareerPackageProgressServiceImpl implements CareerPackageProgressService {

    private final CareerPackageRepository packageRepository;
    private final CareerPackageMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public CareerPackageProgressDto getCurrentUserProgress() {
        Long userId = JwtUserContext.getUserId();

        CareerPackage careerPackage = packageRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("Active career package not found"));

        CareerPackageProgress progress = careerPackage.getProgress();
        return mapper.toCareerPackageProgressDto(progress);
    }
}
