CREATE TABLE sent_emails (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    email VARCHAR(255),
    sended_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    failed BOOLEAN DEFAULT FALSE,
    method VARCHAR(20) DEFAULT 'manually',
    order_id VARCHAR(255),
    CONSTRAINT unique_email UNIQUE (email),
    CONSTRAINT unique_orderId UNIQUE (order_id)
);