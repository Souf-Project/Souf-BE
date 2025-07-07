package com.souf.soufwebsite.global.common.category.service;

import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.exception.*;
import com.souf.soufwebsite.global.common.category.repository.FirstCategoryRepository;
import com.souf.soufwebsite.global.common.category.repository.SecondCategoryRepository;
import com.souf.soufwebsite.global.common.category.repository.ThirdCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final FirstCategoryRepository firstCategoryRepository;
    private final SecondCategoryRepository secondCategoryRepository;
    private final ThirdCategoryRepository thirdCategoryRepository;

    public FirstCategory findIfFirstIdExists(Long firstCategoryId){
        return firstCategoryRepository.findById(firstCategoryId).orElseThrow(NotFoundFirstCategoryException::new);
    }

    public SecondCategory findIfSecondIdExists(Long secondCategoryId){
        return secondCategoryId == null ? null
                : secondCategoryRepository.findById(secondCategoryId).orElseThrow(NotFoundSecondCategoryException::new);
    }

    public ThirdCategory findIfThirdIdExists(Long thirdCategoryId){
        return thirdCategoryId == null ? null
                : thirdCategoryRepository.findById(thirdCategoryId).orElseThrow(NotFoundThirdCategoryException::new);
    }

    public void validate(Long firstId, Long secondId, Long thirdId) {
        if (thirdId != null) {
            if (secondId == null) {
                throw new NotIncludedSecondCategoryException();
            }
            ThirdCategory third = findIfThirdIdExists(thirdId);
            if (!third.getSecondCategory().getId().equals(secondId)) {
                throw new NotMatchedCategoryException("소분류와 중분류의 조합이 유효하지 않습니다.");
            }
        }

        if (secondId != null) {
            if (firstId == null) {
                throw new NotIncludedFirstCategoryException();
            }
            SecondCategory second = findIfSecondIdExists(secondId);
            if (!second.getFirstCategory().getId().equals(firstId)) {
                throw new NotMatchedCategoryException("중분류와 대분류의 조합이 유효하지 않습니다.");
            }
        }

        if (firstId != null && !firstCategoryRepository.existsById(firstId)) {
            throw new NotFoundFirstCategoryException();
        }
    }
}
