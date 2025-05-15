package com.souf.soufwebsite.global.common.category;

import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.exception.NotFoundFirstCategoryException;
import com.souf.soufwebsite.global.common.category.exception.NotFoundSecondCategoryException;
import com.souf.soufwebsite.global.common.category.exception.NotFoundThirdCategoryException;
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
        return secondCategoryRepository.findById(secondCategoryId).orElseThrow(NotFoundSecondCategoryException::new);
    }

    public ThirdCategory findIfThirdIdExists(Long thirdCategoryId){
        return thirdCategoryRepository.findById(thirdCategoryId).orElseThrow(NotFoundThirdCategoryException::new);
    }
}
