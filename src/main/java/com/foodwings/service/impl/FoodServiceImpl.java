package com.foodwings.service.impl;

import com.foodwings.dto.request.FoodRequest;
import com.foodwings.dto.response.FoodResponse;
import com.foodwings.entity.Category;
import com.foodwings.entity.FoodItem;
import com.foodwings.entity.Restaurant;
import com.foodwings.exception.BadRequestException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.FoodMapper;
import com.foodwings.repository.CategoryRepository;
import com.foodwings.repository.FoodItemRepository;
import com.foodwings.repository.RestaurantRepository;
import com.foodwings.response.PagedResponse;
import com.foodwings.service.FileStorageService;
import com.foodwings.service.FoodService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class FoodServiceImpl implements FoodService {

    private final FoodItemRepository foodItemRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final FileStorageService fileStorageService;

    public FoodServiceImpl(FoodItemRepository foodItemRepository,
                           CategoryRepository categoryRepository,
                           RestaurantRepository restaurantRepository,
                           FileStorageService fileStorageService) {
        this.foodItemRepository = foodItemRepository;
        this.categoryRepository = categoryRepository;
        this.restaurantRepository = restaurantRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public FoodResponse create(Long ownerId, boolean isAdmin, FoodRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId()));
        verifyOwnership(restaurant, ownerId, isAdmin);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        FoodItem food = FoodItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discount(request.getDiscount())
                .foodType(request.getFoodType())
                .available(request.isAvailable())
                .bestSeller(request.isBestSeller())
                .popular(request.isPopular())
                .category(category)
                .restaurant(restaurant)
                .build();
        return FoodMapper.toResponse(foodItemRepository.save(food));
    }

    @Override
    public FoodResponse update(Long ownerId, boolean isAdmin, Long id, FoodRequest request) {
        FoodItem food = findFood(id);
        verifyOwnership(food.getRestaurant(), ownerId, isAdmin);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setDiscount(request.getDiscount());
        food.setFoodType(request.getFoodType());
        food.setAvailable(request.isAvailable());
        food.setBestSeller(request.isBestSeller());
        food.setPopular(request.isPopular());
        food.setCategory(category);
        return FoodMapper.toResponse(foodItemRepository.save(food));
    }

    @Override
    public void delete(Long ownerId, boolean isAdmin, Long id) {
        FoodItem food = findFood(id);
        verifyOwnership(food.getRestaurant(), ownerId, isAdmin);
        foodItemRepository.delete(food);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodResponse getById(Long id) {
        return FoodMapper.toResponse(findFood(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FoodResponse> list(Pageable pageable) {
        return PagedResponse.from(foodItemRepository.findByAvailableTrue(pageable), FoodMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FoodResponse> search(String query, Pageable pageable) {
        return PagedResponse.from(
                foodItemRepository.findByAvailableTrueAndNameContainingIgnoreCase(query == null ? "" : query, pageable),
                FoodMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodResponse> getByRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantId(restaurantId).stream()
                .map(FoodMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodResponse> getBestSellers() {
        return foodItemRepository.findByBestSellerTrueAndAvailableTrue().stream()
                .map(FoodMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodResponse> getPopular() {
        return foodItemRepository.findByPopularTrueAndAvailableTrue().stream()
                .map(FoodMapper::toResponse)
                .toList();
    }

    @Override
    public FoodResponse uploadImage(Long ownerId, boolean isAdmin, Long id, MultipartFile file) {
        FoodItem food = findFood(id);
        verifyOwnership(food.getRestaurant(), ownerId, isAdmin);
        food.setImagePath(fileStorageService.store(file, "foods"));
        return FoodMapper.toResponse(foodItemRepository.save(food));
    }

    private FoodItem findFood(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", id));
    }

    private void verifyOwnership(Restaurant restaurant, Long ownerId, boolean isAdmin) {
        if (!isAdmin && !restaurant.getOwner().getId().equals(ownerId)) {
            throw new BadRequestException("You do not own the restaurant for this food item");
        }
    }
}
