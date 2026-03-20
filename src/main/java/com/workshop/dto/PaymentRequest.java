// FILE: src/main/java/com/workshop/dto/PaymentRequest.java
package com.workshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;   // CARD, UPI, NET_BANKING, CASH

    // Card fields
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;

    // UPI field
    private String upiId;

    // Net Banking field
    private String bankName;
}
