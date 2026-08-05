package com.foodwings.service.impl;

import com.foodwings.dto.request.CouponRequest;
import com.foodwings.dto.response.CouponResponse;
import com.foodwings.entity.Coupon;
import com.foodwings.exception.BadRequestException;
import com.foodwings.exception.DuplicateResourceException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.CouponMapper;
import com.foodwings.repository.CouponRepository;
import com.foodwings.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public CouponResponse create(CouponRequest request) {
        if (couponRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new DuplicateResourceException("Coupon already exists with code: " + request.getCode());
        }
        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .discountPercentage(request.getDiscountPercentage())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscount(request.getMaxDiscount())
                .expiryDate(request.getExpiryDate())
                .active(request.isActive())
                .build();
        return CouponMapper.toResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAll() {
        return couponRepository.findAll().stream().map(CouponMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getById(Long id) {
        return CouponMapper.toResponse(findCoupon(id));
    }

    @Override
    public void delete(Long id) {
        Coupon coupon = findCoupon(id);
        couponRepository.delete(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon validate(String code, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", code));
        if (!coupon.isActive()) {
            throw new BadRequestException("Coupon is not active");
        }
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Coupon has expired");
        }
        if (orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Minimum order amount for this coupon is " + coupon.getMinOrderAmount());
        }
        return coupon;
    }

    @Override
    public BigDecimal computeDiscount(Coupon coupon, BigDecimal orderAmount) {
        BigDecimal discount = orderAmount
                .multiply(coupon.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        if (discount.compareTo(coupon.getMaxDiscount()) > 0) {
            discount = coupon.getMaxDiscount();
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private Coupon findCoupon(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
    }
}
