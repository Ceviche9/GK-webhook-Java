#   GK-WEBHOOK JAVA
## Overview

This project is a Java-based API designed to integrate an anti-fraud system for credit card purchases within an e-commerce platform. The API offers endpoints to interact with the anti-fraud service, enabling the e-commerce platform to conduct fraud checks on incoming orders. Additionally, the API facilitates sending emails to buyers to validate certain information. The API also includes specific endpoints for handling webhook calls from the e-commerce platform and managing emails sent to buyers.

## Features
The webhook route has several features, including:
- Check if the data sent by the webhook has all the necessary fields; these are the fields that have previously caused errors for being empty.
```java
    public void verifyEmailFields(VerifyOrderDTO order) throws Exception {
        if(
                order.pagamentos() == null ||
                order.pagamentos().get(0).valor_parcela() == null ||
                order.pagamentos().get(0).forma_pagamento() == null ||
                order.pagamentos().get(0).valor() == null
        ) {
            logger.error("This order does not have all the payments fields");
            throw new Exception("This order does not have all the payments fields.");
        }

        if(
                order.itens() == null
        ) {
            logger.error("This order does not have any item.");
            throw new Exception("This order does not have any item.");
        }
    }
```
- Check if the payment was made by card and if it's approved.
```java
     public void verifyOrderStatus(VerifyOrderDTO order) throws Exception {
        logger.info("Check order status");
        if (!order.pagamentos().isEmpty() && !"mercadopagov1".equals(order.pagamentos().get(0).forma_pagamento().codigo())) {
            logger.error("This order was not payed with credit card: "+ order.pagamentos().get(0).forma_pagamento().codigo());
            throw new Exception("Esse pedido não não foi pago no cartão.");
        }

        if (!order.situacao().aprovado()) {
            logger.error("Order not approved: "+ order.numero());
            throw new Exception("Esse pedido ainda não foi aprovado!");
        }
    }
```

## Requirements

-   Java JDK 8 or higher
-   Apache Maven for dependency management
-   SMTP server for sending emails

## Installation

1.  Clone the repository from GitHub Repository Link.
2.  Build the project using Maven: `mvn clean install`.
3.  Deploy the generated artifact to your desired application server or run it locally.

## Usage

1.  Include the API into your e-commerce platform project as a dependency.
2.  Configure the API endpoints and integrate them into your platform's order processing flow.
3.  Utilize the API endpoints to conduct fraud checks on incoming orders.
4.  Implement email validation logic using the provided API endpoints.
5.  Configure webhook integration to handle automatic calls from the e-commerce platform.
6.  Monitor and analyze the results provided by the anti-fraud system.

## API Endpoints

-   `/orders/webhook`: Webhook endpoint called automatically by the e-commerce platform.
-   `/orders/send-email/{orderId}`: Endpoint to send an email by the order ID.
-   `/emails`: Endpoint to retrieve all emails that have been sent.

## Configuration

-   Configure the API to communicate with your preferred anti-fraud service provider.
-   Set up authentication and authorization mechanisms to secure the API endpoints.
-   Configure the SMTP server for sending emails.
-   Customize email templates and content as per your requirements.
