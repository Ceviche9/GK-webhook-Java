#   GK-WEBHOOK JAVA
## Overview

This project is a Java-based API designed to integrate an anti-fraud system for credit card purchases within an e-commerce platform. The API offers endpoints to interact with the anti-fraud service, enabling the e-commerce platform to conduct fraud checks on incoming orders. Additionally, the API facilitates sending emails to buyers to validate certain information. The API also includes specific endpoints for handling webhook calls from the e-commerce platform and managing emails sent to buyers.

## Features

-   Integration with an anti-fraud system for credit card purchases.
-   Enables sending emails to buyers for validating information.
-   Supports webhook integration for automatic communication with the e-commerce platform.
-   Allows retrieval of all sent emails and sending emails by order ID.

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
