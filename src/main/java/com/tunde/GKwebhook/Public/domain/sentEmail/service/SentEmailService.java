package com.tunde.GKwebhook.Public.domain.sentEmail.service;

import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.domain.order.entity.MethodType;
import com.tunde.GKwebhook.Public.domain.order.service.OrderService;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.FindAllSentEmailResponseDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailResponseDTO;
import com.tunde.GKwebhook.Public.domain.order.entity.SentEmail;
import com.tunde.GKwebhook.Public.domain.sentEmail.repository.SentEmailRepository;
import com.tunde.GKwebhook.Public.infra.providers.MailSenderProvider;
import com.tunde.GKwebhook.Public.infra.providers.StoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SentEmailService {
    private static final Logger logger = LoggerFactory.getLogger(SentEmailService.class);

    @Autowired
    private SentEmailRepository repository;

    @Autowired
    private StoreProvider storeProvider;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MailSenderProvider mailSenderProvider;


    public SentEmail saveEmail(SentEmailDTO dto) {
        SentEmail newSentEmail = new SentEmail(dto);
        return this.repository.save(newSentEmail);
    }

    public SentEmail findByEmail(String email) {
        Optional<SentEmail> sentEmail = this.repository.findByEmail(email);
        return sentEmail.orElse(null);
    }

    public void updateEmailStatus(UUID id) {
        this.repository.updateStatusById(false, id);
    }

    public FindAllSentEmailResponseDTO findAll() {
        logger.info("Fetching all emails");
        List<SentEmail> emails = this.repository.findAll();
        logger.info("Emails fetched: "+ emails.size());
        List<SentEmailResponseDTO> responseArray = new ArrayList<>();

        for (SentEmail email : emails) {
            responseArray.add(this.createSentEmailResponseDTO(email));
        }

        return new FindAllSentEmailResponseDTO(
                responseArray,
                responseArray.size()
        );
    }

    public SentEmailResponseDTO sendEmail(String id) throws Exception {
        var order = this.storeProvider.findOrderById(id);
        logger.info("Order found: " + order.numero());
        var verifyOrderDTO = VerifyOrderDTO.fromOrderDTO(order, Optional.of(MethodType.manually));
        System.out.println(verifyOrderDTO.methodType());
        logger.info("DTO created");
        return this.sendValidationEmail(verifyOrderDTO);
    }

    public SentEmailResponseDTO sendValidationEmail(VerifyOrderDTO order) throws Exception {
        if (order.methodType() != MethodType.manually) {
            order = VerifyOrderDTO.setMethodType(order, MethodType.webhook);
            this.verifyEmailFields(order);

        };
        logger.info(order.methodType().toString());
        this.orderService.verifyOrderStatus(order);

        logger.info("Check if email already stored");
        var emailAlreadySent = this.findByEmail(order.cliente().email());
        logger.info("emailAlreadySent is null: "+ (emailAlreadySent == null));
        if (emailAlreadySent != null) {
            logger.error("Email already in the DB: OrderId: "+ order.numero() + ".Email: " + order.cliente().email());
            throw new Exception("Já foi enviado um email para esse cliente.");
        }

        var sentEmail = this.sendEmailAndUpdateDb(order);

        return this.createSentEmailResponseDTO(sentEmail);
    }
    private SentEmail sendEmailAndUpdateDb(VerifyOrderDTO order) {
        var sentEmail = new SentEmail();
        AtomicReference<Boolean> emailSent = new AtomicReference<>(false);

        try {
            logger.info("Calling mail sender provider");
            Boolean emailSentResponse = this.mailSenderProvider.sendEmail(order);
            emailSent.set(emailSentResponse);
            logger.info("Email was sent: " + emailSent.get());

            logger.info("Creating new email sent on DB");
            SentEmailDTO sentEmailDTO = SentEmailDTO.fromVerifyDTO(order, !emailSent.get());
            logger.info("MethodType: " + sentEmailDTO.methodType());
            sentEmail = this.saveEmail(sentEmailDTO);
            logger.info("Email stored in DB: " + sentEmail.getId());
        } catch (Exception err) {
            logger.error("An error occurred while sending or storing the email");
            logger.error(err.getMessage());
            if (err.getMessage().equals("Sending the email to the following server failed : email-ssl.com.br:465")) {
                logger.info("This error was from the sendEmail lib");
                return sentEmail;
            }
            logger.info("This error was not from the SendEmail lib ");
            SentEmailDTO sentEmailDTO = SentEmailDTO.fromVerifyDTO(order, !emailSent.get());
            sentEmail = this.saveEmail(sentEmailDTO);
            logger.error("Error saved in DB: " + sentEmail.getId());
        }

        return sentEmail;
    }

    public void verifyEmailFields(VerifyOrderDTO order) throws Exception {
        if(
                order.pagamentos() == null
        ) {
            logger.error("This order does not have all the payments fields, orderId: " + order.numero());
            throw new Exception("This order does not have all the payments fields.");
        }

        if(
                order.itens() == null
        ) {
            logger.error("This order does not have any item.");
            throw new Exception("This order does not have any item.");
        }
    }

    public SentEmailResponseDTO createSentEmailResponseDTO(SentEmail email) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return  new SentEmailResponseDTO(
                email.getEmail(),
                email.getMethod(),
                email.getFailed(),
                email.getOrder_id(),
                email.getSended_at().format(formatter)
        );
    }
}
