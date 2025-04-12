package com.main.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.project.dto.request.MomoIPNRequest;
import com.main.project.dto.request.MomoPaymentRequest;
import com.main.project.entities.MomoTransaction;
import com.main.project.enums.TransactionEnum;
import com.main.project.repository.MomoTransactionRepository;
import com.main.project.utils.MomoSignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.UUID;

@Service
public class MomoPaymentService {

    @Value("${momo.partnerCode}")
    private String partnerCode;

    @Value("${momo.accessKey}")
    private String accessKey;

    @Value("${momo.secretKey}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.redirectUrl}")
    private String redirectUrl;

    @Value("${momo.ipnUrl}")
    private String ipnUrl;


    @Autowired
    private MomoTransactionRepository momoTransactionRepository;


    public Map<String, Object> createMomoPayment(String amount) throws Exception {
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toán đơn hàng #" + orderId;
        String extraData = "";

        MomoTransaction transaction = new MomoTransaction();
        transaction.setOrderId(orderId);
        transaction.setRequestId(requestId);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionEnum.PENDING);

        momoTransactionRepository.save(transaction);

        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=captureWallet";

        String signature = MomoSignatureUtil.signSHA256(rawSignature, secretKey);

        MomoPaymentRequest request = new MomoPaymentRequest(
                partnerCode, accessKey, requestId, amount,
                orderId, orderInfo, redirectUrl, ipnUrl,
                extraData, "captureWallet", signature, "vi"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MomoPaymentRequest> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.readValue(response.getBody(), new TypeReference<>() {});

        return result;
    }


    public void processMomoIPN(MomoIPNRequest request) {
        String orderId = request.getOrderId();
        String resultCode = request.getResultCode();

        MomoTransaction transaction = momoTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("0".equals(resultCode)) {
            transaction.setStatus(TransactionEnum.SUCCESS);
        } else {
            transaction.setStatus(TransactionEnum.FAILED);
        }

        transaction.setMessage(request.getMessage());
        momoTransactionRepository.save(transaction);
    }
}
