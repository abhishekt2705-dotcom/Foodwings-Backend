package com.foodwings.service.impl;

import com.foodwings.dto.request.RestaurantRequest;
import com.foodwings.dto.response.EarningsResponse;
import com.foodwings.dto.response.RestaurantResponse;
import com.foodwings.entity.Restaurant;
import com.foodwings.entity.User;
import com.foodwings.enums.RestaurantStatus;
import com.foodwings.exception.BadRequestException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.RestaurantMapper;
import com.foodwings.repository.OrderRepository;
import com.foodwings.repository.RestaurantRepository;
import com.foodwings.repository.UserRepository;
import com.foodwings.response.PagedResponse;
import com.foodwings.service.FileStorageService;
import com.foodwings.service.RestaurantService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final FileStorageService fileStorageService;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 UserRepository userRepository,
                                 OrderRepository orderRepository,
                                 FileStorageService fileStorageService) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public RestaurantResponse create(Long ownerId, RestaurantRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .city(request.getCity())
                .phone(request.getPhone())
                .email(request.getEmail())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .status(RestaurantStatus.PENDING)
                .active(true)
                .owner(owner)
                .build();
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public RestaurantResponse update(Long ownerId, Long id, RestaurantRequest request) {
        Restaurant restaurant = findOwned(ownerId, id);
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setPhone(request.getPhone());
        restaurant.setEmail(request.getEmail());
        restaurant.setOpeningTime(request.getOpeningTime());
        restaurant.setClosingTime(request.getClosingTime());
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public void delete(Long ownerId, Long id) {
        Restaurant restaurant = findOwned(ownerId, id);
        restaurantRepository.delete(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getById(Long id) {
        return RestaurantMapper.toResponse(findRestaurant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RestaurantResponse> listApproved(Pageable pageable) {
        return PagedResponse.from(
                restaurantRepository.findByStatusAndActiveTrue(RestaurantStatus.APPROVED, pageable),
                RestaurantMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RestaurantResponse> search(String query, Pageable pageable) {
        return PagedResponse.from(
                restaurantRepository.findByStatusAndActiveTrueAndNameContainingIgnoreCase(
                        RestaurantStatus.APPROVED, query == null ? "" : query, pageable),
                RestaurantMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getOwnerRestaurants(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId).stream()
                .map(RestaurantMapper::toResponse)
                .toList();
    }

    @Override
    public RestaurantResponse uploadLogo(Long ownerId, Long id, MultipartFile file) {
        Restaurant restaurant = findOwned(ownerId, id);
        restaurant.setLogo(fileStorageService.store(file, "restaurants"));
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public RestaurantResponse uploadBanner(Long ownerId, Long id, MultipartFile file) {
        Restaurant restaurant = findOwned(ownerId, id);
        restaurant.setBanner(fileStorageService.store(file, "restaurants"));
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    @Transactional(readOnly = true)
    public EarningsResponse getEarnings(Long ownerId, Long restaurantId) {
        Restaurant restaurant = findOwned(ownerId, restaurantId);
        return EarningsResponse.builder()
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName())
                .totalEarnings(orderRepository.calculateRevenueByRestaurant(restaurant.getId()))
                .deliveredOrders(orderRepository.findByRestaurantIdOrderByIdDesc(restaurant.getId(), Pageable.unpaged())
                        .stream().filter(o -> o.getStatus().name().equals("DELIVERED")).count())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RestaurantResponse> listByStatus(String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            return PagedResponse.from(restaurantRepository.findAll(pageable), RestaurantMapper::toResponse);
        }
        RestaurantStatus parsed = parseStatus(status);
        return PagedResponse.from(restaurantRepository.findByStatus(parsed, pageable), RestaurantMapper::toResponse);
    }

    @Override
    public RestaurantResponse approve(Long id) {
        Restaurant restaurant = findRestaurant(id);
        restaurant.setStatus(RestaurantStatus.APPROVED);
        restaurant.setActive(true);
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public RestaurantResponse reject(Long id) {
        Restaurant restaurant = findRestaurant(id);
        restaurant.setStatus(RestaurantStatus.REJECTED);
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public RestaurantResponse setActive(Long id, boolean active) {
        Restaurant restaurant = findRestaurant(id);
        restaurant.setActive(active);
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    private RestaurantStatus parseStatus(String status) {
        try {
            return RestaurantStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid restaurant status: " + status);
        }
    }

    private Restaurant findRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
    }

    private Restaurant findOwned(Long ownerId, Long id) {
        Restaurant restaurant = findRestaurant(id);
        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new BadRequestException("You do not own this restaurant");
        }
        return restaurant;
    }
}
