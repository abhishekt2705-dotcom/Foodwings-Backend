package com.foodwings.service.impl;

import com.foodwings.dto.response.OrderResponse;
import com.foodwings.entity.DeliveryPartner;
import com.foodwings.entity.Order;
import com.foodwings.entity.User;
import com.foodwings.enums.NotificationType;
import com.foodwings.enums.OrderStatus;
import com.foodwings.exception.BadRequestException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.OrderMapper;
import com.foodwings.repository.DeliveryPartnerRepository;
import com.foodwings.repository.OrderRepository;
import com.foodwings.repository.UserRepository;
import com.foodwings.response.PagedResponse;
import com.foodwings.service.DeliveryService;
import com.foodwings.service.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final OrderRepository orderRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public DeliveryServiceImpl(OrderRepository orderRepository,
                               DeliveryPartnerRepository deliveryPartnerRepository,
                               UserRepository userRepository,
                               NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.deliveryPartnerRepository = deliveryPartnerRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAvailableOrders(Pageable pageable) {
        // Orders that are ready to be picked up and not yet assigned to a partner
        return PagedResponse.from(
                orderRepository.findByStatusOrderByIdDesc(OrderStatus.READY, pageable),
                OrderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getMyDeliveries(Long partnerId, Pageable pageable) {
        return PagedResponse.from(
                orderRepository.findByDeliveryPartnerIdOrderByIdDesc(partnerId, pageable),
                OrderMapper::toResponse);
    }

    @Override
    public OrderResponse acceptDelivery(Long partnerId, Long orderId) {
        Order order = findOrder(orderId);
        if (order.getDeliveryPartner() != null) {
            throw new BadRequestException("Order is already assigned to a delivery partner");
        }
        if (order.getStatus() != OrderStatus.READY) {
            throw new BadRequestException("Order is not ready for pickup");
        }
        User partner = findPartner(partnerId);
        order.setDeliveryPartner(partner);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        Order saved = orderRepository.save(order);
        notificationService.send(saved.getCustomer(), NotificationType.OUT_FOR_DELIVERY,
                "Out for delivery", "Your order " + saved.getOrderNumber() + " is out for delivery.");
        return OrderMapper.toResponse(saved);
    }

    @Override
    public OrderResponse rejectDelivery(Long partnerId, Long orderId) {
        Order order = findOrder(orderId);
        if (order.getDeliveryPartner() != null && !order.getDeliveryPartner().getId().equals(partnerId)) {
            throw new BadRequestException("This delivery is assigned to another partner");
        }
        order.setDeliveryPartner(null);
        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            order.setStatus(OrderStatus.READY);
        }
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse updateStatus(Long partnerId, Long orderId, OrderStatus status) {
        if (status != OrderStatus.OUT_FOR_DELIVERY && status != OrderStatus.DELIVERED) {
            throw new BadRequestException("Delivery partners can only set OUT_FOR_DELIVERY or DELIVERED");
        }
        Order order = findAssignedOrder(partnerId, orderId);
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        if (status == OrderStatus.DELIVERED) {
            incrementDeliveries(partnerId);
        }
        notificationService.send(saved.getCustomer(),
                status == OrderStatus.DELIVERED ? NotificationType.DELIVERED : NotificationType.OUT_FOR_DELIVERY,
                "Order " + status, "Your order " + saved.getOrderNumber() + " is now " + status + ".");
        return OrderMapper.toResponse(saved);
    }

    @Override
    public OrderResponse markDelivered(Long partnerId, Long orderId) {
        Order order = findAssignedOrder(partnerId, orderId);
        order.setStatus(OrderStatus.DELIVERED);
        Order saved = orderRepository.save(order);
        incrementDeliveries(partnerId);
        notificationService.send(saved.getCustomer(), NotificationType.DELIVERED,
                "Order delivered", "Your order " + saved.getOrderNumber() + " has been delivered. Enjoy your meal!");
        return OrderMapper.toResponse(saved);
    }

    private void incrementDeliveries(Long partnerId) {
        deliveryPartnerRepository.findByUserId(partnerId).ifPresent(dp -> {
            dp.setTotalDeliveries(dp.getTotalDeliveries() + 1);
            deliveryPartnerRepository.save(dp);
        });
    }

    private Order findAssignedOrder(Long partnerId, Long orderId) {
        Order order = findOrder(orderId);
        if (order.getDeliveryPartner() == null || !order.getDeliveryPartner().getId().equals(partnerId)) {
            throw new BadRequestException("This order is not assigned to you");
        }
        return order;
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    private User findPartner(Long partnerId) {
        DeliveryPartner partner = deliveryPartnerRepository.findByUserId(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner", "userId", partnerId));
        return partner.getUser();
    }
}
