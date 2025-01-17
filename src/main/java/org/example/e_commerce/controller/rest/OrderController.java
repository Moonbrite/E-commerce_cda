package org.example.e_commerce.controller.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.e_commerce.model.*;
import org.example.e_commerce.model.DTO.OrderDTO;
import org.example.e_commerce.service.*;
import org.example.e_commerce.util.ReponseApi;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final PaymentService paymentService;

    @GetMapping("/orders")
    public List<Order> getOrders() {
        return orderService.getObjects();
    }

    @GetMapping("/orders/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOneObject(id);
    }

    @PostMapping("/orders")
    @ResponseStatus(code = HttpStatus.CREATED)
    @Transactional
    public Order postOrder(@RequestBody @Valid OrderDTO orderDTO) {
        User user = userService.getOneObject((long) orderDTO.getUserId());

        Order order = new Order();
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setUser(user);
        order.setPaymentStatus("Paye");
        order.setOrderDate(LocalDateTime.now());

        Optional<Cart> optionalCart = cartService.getCartByUserId(user.getId());

        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cartService.deleteObject(cart.getId());
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalPrice());
        payment.setCurrency("EUR");
        payment.setStatus("CONFIRM");
        payment.setPaymentIntentId("No use");

        orderService.postObjectOrUpdate(order);

        paymentService.postObjectOrUpdate(payment);

        return order;
    }

    @DeleteMapping("/orders/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteObject(id);
    }


}
