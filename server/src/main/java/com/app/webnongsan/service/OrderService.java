package com.app.webnongsan.service;
import com.app.webnongsan.domain.Order;
import com.app.webnongsan.domain.User;
import com.app.webnongsan.domain.response.PaginationDTO;
import com.app.webnongsan.domain.response.order.OrderDTO;

import com.app.webnongsan.repository.OrderRepository;
import com.app.webnongsan.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.app.webnongsan.domain.*;

import com.app.webnongsan.domain.response.order.OrderDetailDTO;
import com.app.webnongsan.repository.OrderDetailRepository;

import com.app.webnongsan.repository.ProductRepository;

import com.app.webnongsan.util.PaginationHelper;
import com.app.webnongsan.util.SecurityUtil;
import com.app.webnongsan.util.exception.ResourceInvalidException;



@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final PaginationHelper paginationHelper;

//    public Order create(Order order){
//        return this.orderRepository.save(order);
//    }
    public Order get(long id){
        return this.orderRepository.findById(id).orElse(null);
    }
    public Order save(Order order){
        return this.orderRepository.save(order);
    }
    public void delete (long id){ this.orderRepository.deleteById(id);}


    public List<OrderDTO> getLastFiveOrders() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());
        List<Order> orders = this.orderRepository.findAll(pageable).getContent();
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }


    public Optional<OrderDTO> findOrder(long id){
        OrderDTO res = new OrderDTO();
        Optional<Order> orderOptional = this.orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            res.setId(order.getId());
            res.setOrderTime(order.getOrderTime());
            res.setDeliveryTime(order.getDeliveryTime());
            res.setStatus(order.getStatus());
            res.setPaymentMethod(order.getPaymentMethod());
            res.setAddress(order.getAddress());
            res.setTotal_price(order.getTotal_price()); // Chú ý: có thể cần sửa lại tên phương thức
            res.setUserEmail(order.getUser().getEmail());
            res.setUserId(order.getUser().getId());
            res.setUserName(order.getUser().getName());
            return Optional.of(res);
        } else {
            return Optional.empty();
        }
    }

    public PaginationDTO getAll(Specification<Order> spec, Pageable pageable){
        Page<Order> ordersPage = this.orderRepository.findAll(spec, pageable);

        PaginationDTO p = new PaginationDTO();
        PaginationDTO.Meta meta = new PaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(ordersPage.getTotalPages());
        meta.setTotal(ordersPage.getTotalElements());

        p.setMeta(meta);

        List<OrderDTO> listOrders = ordersPage.getContent().stream().map(this::convertToOrderDTO).toList();
        p.setResult(listOrders);
        return p;
    }
    public OrderDTO convertToOrderDTO(Order order) {
        OrderDTO res = new OrderDTO();
        res.setId(order.getId());
        res.setOrderTime(order.getOrderTime());
        res.setDeliveryTime(order.getDeliveryTime());
        res.setStatus(order.getStatus());
        res.setPaymentMethod(order.getPaymentMethod());
        res.setAddress(order.getAddress());
        res.setTotal_price(order.getTotal_price());
        res.setUserEmail(order.getUser().getEmail());
        res.setUserId(order.getUser().getId());
        res.setUserName(order.getUser().getName());
        return res;
        }

    public Order create(OrderDTO orderDTO) throws ResourceInvalidException {
        String emailLoggedIn = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        // Lấy thông tin người dùng trong db
        User currentUserDB = userService.getUserByUsername(emailLoggedIn);
//        if(orderDTO.getUserId() == currentUserDB.getId()){
//            throw new ResourceInvalidException("User không hợp lệ");
//        }
        Order order = new Order();
        order.setUser(currentUserDB);
        order.setAddress(orderDTO.getAddress());
        order.setTotal_price(orderDTO.getTotalPrice());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setStatus(0);
        Order saveOrder = orderRepository.save(order);
        orderDTO.getItems().forEach(item ->{
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            OrderDetail orderDetail = new OrderDetail();
            OrderDetailId id = new OrderDetailId();
            id.setOrderId(saveOrder.getId());
            id.setProductId(product.getId());
            orderDetail.setId(id);
            orderDetail.setOrder(saveOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setUnit_price(item.getUnit_price());
            orderDetailRepository.save(orderDetail);
        });
        return saveOrder;
    }

    public PaginationDTO getOrderByCurrentUser(Pageable pageable, Integer status) throws ResourceInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User user = this.userRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceInvalidException("User không tồn tại");
        }

        Page<OrderDetailDTO> orderItems = this.orderRepository.findOrderItemsByUserId(user.getId(), status, pageable);
        return this.paginationHelper.fetchAllEntities(orderItems);
    }
}
