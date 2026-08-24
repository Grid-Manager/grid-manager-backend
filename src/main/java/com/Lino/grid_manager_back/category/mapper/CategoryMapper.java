package com.Lino.grid_manager_back.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import com.Lino.grid_manager_back.category.dto.CategoryResponse;
import com.Lino.grid_manager_back.category.dto.CreateCategoryRequest;
import com.Lino.grid_manager_back.category.dto.UpdateCategoryRequest;
import com.Lino.grid_manager_back.category.entity.Category;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    Category toEntity(CreateCategoryRequest request);
    CategoryResponse toResponse(Category category);
    void update(UpdateCategoryRequest request, @MappingTarget Category category);
}
