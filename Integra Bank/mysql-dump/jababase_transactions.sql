-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--

-- ------------------------------------------------------
-- Server version	8.4.5

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(19,2) DEFAULT NULL,
  `sender_id` int DEFAULT NULL,
  `recipient_id` int DEFAULT NULL,
  `timestamp` timestamp NOT NULL,
  `description` text,
  `idempotency_key` varchar(255) NOT NULL,
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `transaction_id` (`transaction_id`),
  UNIQUE KEY `unique_idempotency_key` (`idempotency_key`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_recipient_id` (`recipient_id`),
  KEY `idx_timestamp` (`timestamp`),
  CONSTRAINT `fk_recipient` FOREIGN KEY (`recipient_id`) REFERENCES `user_details` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_sender` FOREIGN KEY (`sender_id`) REFERENCES `user_details` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `transactions_chk_1` CHECK ((`amount` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,5.00,1005,1007,'2025-11-06 14:39:52','','ff974d2c-114c-4992-83c8-465e4f11ffdd'),(2,6.00,1005,1007,'2025-11-06 14:51:23','','b062e997-c854-46d3-ab15-8d3dc484b18d'),(3,23.00,1005,1007,'2025-11-11 13:10:56','','615ab804-3dfd-40a7-b82f-6e176d97fb4f'),(4,89.00,1005,1007,'2025-11-11 13:11:36','','5a4aada8-40f7-4822-9304-ae7ead988a8b'),(5,1.00,1005,1007,'2025-11-12 19:53:58','','ea822af5-cde2-480e-b073-1e64086c577e'),(6,50000.00,1008,1005,'2025-11-12 20:09:32','','123e4567-e89b-12d3-a456-426614174000'),(7,1000000000.00,1008,1005,'2025-11-12 20:12:17','','123e4567-e89b-12d3-a456-426614174010'),(8,1000000000000000.00,1008,1005,'2025-11-12 20:17:53','','123e4567-e89b-12d3-a456-426614174013'),(9,1000000000000000.00,1005,1008,'2025-11-12 20:19:25','','31105a61-530b-4fa6-92e7-1f235157e79a'),(10,1.00,1005,1008,'2025-11-12 20:20:02','','ffa12d9d-5702-4a9f-9eb4-c50e514bfb24'),(11,3.00,1005,1008,'2025-11-12 20:35:06','','b0129813-e831-4dda-aaff-d602f7d1a462'),(12,18.00,1005,1007,'2025-11-19 15:02:15','','b66273d2-f5d4-48d7-8a08-8dcb79d1e01f'),(13,1.00,1005,1008,'2025-11-26 14:58:48','','eb378e1c-a2b0-468d-afbb-326c7c64becc'),(14,1.00,1005,1008,'2025-11-26 14:59:53','','3795604f-7ed3-47b5-b29f-211abc22f879'),(16,1.00,1005,1008,'2025-11-26 15:01:11','','ce548f2d-43f9-4735-b8da-91d971986960'),(18,37000.26,1008,1005,'2025-12-06 00:11:45','','f655aa22-ac73-4172-bb0a-cbbbd1da9241');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-06  0:13:39
