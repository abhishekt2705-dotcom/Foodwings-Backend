package com.foodwings.service.impl;

import com.foodwings.dto.request.PlaceOrderRequest;
import com.foodwings.dto.response.OrderResponse;
import com.foodwings.entity.Address;
import com.foodwings.entity.Cart;
import com.foodwings.entity.CartItem;
import com.foodwings.entity.Order;
import com.foodwings.entity.OrderItem;
import com.foodwings.entity.Payment;
import com.foodwings.entity.Restaurant;
import com.foodwings.enums.NotificationType;
import com.foodwings.enums.OrderStatus;
import com.foodwings.enums.PaymentMethod;
import com.foodwings.enums.PaymentStatus;
import com.foodwings.exception.BadRequestException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.OrderMapper;
import com.foodwings.repository.AddressRepository;
import com.foodwings.repository.OrderRepository;
import com.foodwings.response.PagedResponse;
import com.foodwings.service.CartService;
import com.foodwings.service.CouponService;
import com.foodwings.service.NotificationService;
import com.foodwings.service.OrderService;
import com.foodwings.util.AppConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;
    private final CouponService couponService;
    private final NotificationService notificationService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            AddressRepository addressRepository,
                            CartService cartService,
                            CouponService couponService,
                            NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.cartService = cartService;
        this.couponService = couponService;
        this.notificationService = notificationService;
    }

    @Override
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        Cart cart = cartService.getOrCreateCartEntity(userId);
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot place an order with an empty cart");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", request.getAddressId()));
        if (!address.getUser().getId().equals(userId)) {
            throw new BadRequestException("Delivery address does not belong to you");
        }

        // Optional coupon supplied at checkout overrides any coupon already on the cart
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            cart.setCoupon(couponService.validate(request.getCouponCode(), cart.getTotalAmount()));
            cartService.recalculate(cart);
        }

        Restaurant restaurant = cart.getItems().get(0).getFoodItem().getRestaurant();

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customer(cart.getUser())
                .restaurant(restaurant)
                .coupon(cart.getCoupon())
                .deliveryAddress(address)
                .deliveryAddressSnapshot(formatAddress(address))
                .status(OrderStatus.PLACED)
                .totalAmount(cart.getTotalAmount())
                .discountAmount(cart.getDiscountAmount())
                .deliveryFee(AppConstants.DELIVERY_FEE)
                .finalAmount(cart.getFinalAmount().add(AppConstants.DELIVERY_FEE))
                .build();

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .foodItem(cartItem.getFoodItem())
                    .foodName(cartItem.getFoodItem().getName())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .subtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();
            order.addItem(orderItem);
        }

        Payment payment = buildPayment(order, request.getPaymentMethod());
        order.setPayment(payment);

        Order saved = orderRepository.save(order);

        notificationService.send(saved.getCustomer(), NotificationType.ORDER_PLACED,
                "Order placed", "Your order " + saved.getOrderNumber() + " has been placed successfully.");
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            notificationService.send(saved.getCustomer(), NotificationType.PAYMENT_SUCCESS,
                    "Payment successful", "Payment of " + payment.getAmount() + " for order " + saved.getOrderNumber() + " succeeded.");
        }

        cartService.clearCart(userId);
        return OrderMapper.toResponse(saved);
    }

    @Override
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = findOrder(orderId);
        if (!order.getCustomer().getId().equals(userId)) {
            throw new BadRequestException("You can only cancel your own orders");
        }
        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY
                || order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order cannot be cancelled in its current state: " + order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        notificationService.send(saved.getCustomer(), NotificationType.ORDER_CANCELLED,
                "Order cancelled", "Your order " + saved.getOrderNumber() + " has been cancelled.");
        return OrderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse track(Long userId, Long orderId) {
        Order order = findOrder(orderId);
        if (!order.getCustomer().getId().equals(userId)) {
            throw new BadRequestException("You can only track your own orders");
        }
        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getMyOrders(Long userId, Pageable pageable) {
        return PagedResponse.from(orderRepository.findByCustomerIdOrderByIdDesc(userId, pageable), OrderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getRestaurantOrders(Long ownerId, Long restaurantId, Pageable pageable) {
        return PagedResponse.from(
                orderRepository.findByRestaurantIdOrderByIdDesc(restaurantId, pageable),
                order -> {
                    if (!order.getRestaurant().getOwner().getId().equals(ownerId)) {
                        throw new BadRequestException("You do not own this restaurant");
                    }
                    return OrderMapper.toResponse(order);
                });
    }

    @Override
    public OrderResponse updateStatusByOwner(Long ownerId, Long orderId, OrderStatus status) {
        Order order = findOrder(orderId);
        if (!order.getRestaurant().getOwner().getId().equals(ownerId)) {
            throw new BadRequestException("You do not own the restaurant for this order");
        }
        if (status != OrderStatus.ACCEPTED && status != OrderStatus.PREPARING
                && status != OrderStatus.READY && status != OrderStatus.CANCELLED) {
            throw new BadRequestException("Restaurant owners can only set ACCEPTED, PREPARING, READY or CANCELLED");
        }
        return applyStatus(order, status);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrders(Pageable pageable) {
        return PagedResponse.from(orderRepository.findAll(pageable), OrderMapper::toResponse);
    }

    @Override
    public OrderResponse updateStatusByAdmin(Long orderId, OrderStatus status) {
        return applyStatus(findOrder(orderId), status);
    }

    private OrderResponse applyStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        notifyStatus(saved);
        return OrderMapper.toResponse(saved);
    }

    private void notifyStatus(Order order) {
        NotificationType type = switch (order.getStatus()) {
            case ACCEPTED -> NotificationType.ORDER_ACCEPTED;
            case PREPARING -> NotificationType.ORDER_PREPARING;
            case READY -> NotificationType.ORDER_READY;
            case OUT_FOR_DELIVERY -> NotificationType.OUT_FOR_DELIVERY;
            case DELIVERED -> NotificationType.DELIVERED;
            case CANCELLED -> NotificationType.ORDER_CANCELLED;
            default -> NotificationType.ORDER_PLACED;
        };
        notificationService.send(order.getCustomer(), type,
                "Order " + order.getStatus(), "Your order " + order.getOrderNumber() + " is now " + order.getStatus() + ".");
    }

    private Payment buildPayment(Order order, PaymentMethod method) {
        boolean online = method != PaymentMethod.CASH_ON_DELIVERY;
        return Payment.builder()
                .order(order)
                .method(method)
                .amount(order.getFinalAmount())
                .status(online ? PaymentStatus.SUCCESS : PaymentStatus.PENDING)
                .transactionId(online ? "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase() : null)
                .paidAt(online ? LocalDateTime.now() : null)
                .build();
    }

    private String formatAddress(Address a) {
        StringBuilder sb = new StringBuilder();
        sb.append(a.getLine1());
        if (a.getLine2() != null && !a.getLine2().isBlank()) {
            sb.append(", ").append(a.getLine2());
        }
        sb.append(", ").append(a.getCity());
        if (a.getState() != null) {
            sb.append(", ").append(a.getState());
        }
        sb.append(" - ").append(a.getPincode());
        return sb.toString();
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "FW-" + datePart + "-" + randomPart;
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }
}
