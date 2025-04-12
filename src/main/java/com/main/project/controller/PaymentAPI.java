package com.main.project.controller;

import com.main.project.dto.request.MomoIPNRequest;
import com.main.project.service.MomoPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentAPI {
    @Autowired
    private MomoPaymentService momoPaymentService;

    @PostMapping("/momo")
    public ResponseEntity<?> createPayment(@RequestParam String amount) throws Exception {
        Map<String, Object> result = momoPaymentService.createMomoPayment(amount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/momo-success")
    public ResponseEntity<String> momoSuccess(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok("Thanh toán thành công! " + params.toString());
    }

    @PostMapping("/momo-notify")
    public ResponseEntity<String> momoNotify(@RequestBody MomoIPNRequest request) {
        momoPaymentService.processMomoIPN(request);
        return ResponseEntity.ok("IPN received");
    }
}
