# Prescription Microservice

This service handles the lifecycle of medical prescriptions and medication orders.

## Features
- **Prescription Management**: Doctors can create and issue prescriptions.
- **Order Management**: Patients can order medicines from issued prescriptions.
- **Security**: 
  - Trusts API Gateway for Authentication (JWT validation).
  - Enforces Role-Based Access Control (RBAC) at the method level.
- **Event-Driven**: Publishes Kafka events for `PrescriptionIssued`, `OrderCreated`, etc.
- **Auditing**: Tracks creation and modification of records.

## API Endpoints

### Doctor
- `POST /api/v1/prescriptions` - Create a draft or issued prescription
- `POST /api/v1/prescriptions/{id}/issue` - Issue a draft prescription

### Patient
- `GET /api/v1/prescriptions/me` - List my prescriptions
- `GET /api/v1/prescriptions/{id}` - View prescription details
- `POST /api/v1/orders` - Create an order from a prescription
- `GET /api/v1/orders/me` - List my orders

### Admin
- `PATCH /api/v1/admin/orders/{id}/status` - Update order status

## Events
- `prescription-issued`: Emitted when a prescription is finalized.
- `medicine-order-created`: Emitted when a patient places an order.
- `medicine-order-status-updated`: Emitted when order status changes.

## Configuration
- Port: `8084`
- Database: `medibridge_prescription` (MySQL)
- Kafka: `localhost:9092`
