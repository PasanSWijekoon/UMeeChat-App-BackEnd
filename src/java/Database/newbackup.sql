-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for smart_chat
CREATE DATABASE IF NOT EXISTS `smart_chat` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `smart_chat`;

-- Dumping structure for table smart_chat.chat
CREATE TABLE IF NOT EXISTS `chat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `from_user_id` int NOT NULL,
  `to_user_id` int NOT NULL,
  `message` text NOT NULL,
  `date_time` datetime NOT NULL,
  `chat_status_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_chat_user1_idx` (`from_user_id`),
  KEY `fk_chat_user2_idx` (`to_user_id`),
  KEY `fk_chat_chat_status1_idx` (`chat_status_id`),
  CONSTRAINT `fk_chat_chat_status1` FOREIGN KEY (`chat_status_id`) REFERENCES `chat_status` (`id`),
  CONSTRAINT `fk_chat_user1` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_chat_user2` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smart_chat.chat: ~20 rows (approximately)
INSERT INTO `chat` (`id`, `from_user_id`, `to_user_id`, `message`, `date_time`, `chat_status_id`) VALUES
	(1, 1, 8, 'Hi', '2024-10-08 15:43:57', 1),
	(2, 1, 14, 'Hi Oyat Kohomad', '2024-10-08 16:01:56', 1),
	(3, 14, 1, 'Awulk nh', '2024-10-08 16:02:46', 1),
	(4, 1, 8, 'Hello', '2024-10-08 18:39:29', 1),
	(5, 1, 8, 'Hello', '2024-10-08 18:59:54', 1),
	(6, 8, 1, 'Hi', '2024-10-08 19:20:45', 1),
	(7, 1, 8, 'Hi', '2024-10-08 19:26:51', 1),
	(8, 8, 1, 'Hello', '2024-10-08 19:27:52', 1),
	(9, 1, 8, 'Hello', '2024-10-08 19:29:33', 1),
	(10, 8, 1, 'hello\n', '2024-10-08 19:42:46', 1),
	(11, 8, 1, 'Hi', '2024-10-08 19:54:10', 1),
	(12, 1, 8, 'Kohomada', '2024-10-08 19:54:52', 1),
	(13, 1, 8, 'Hodin innwaoo', '2024-10-08 20:21:44', 1),
	(14, 1, 8, 'Hi', '2024-10-08 20:27:36', 1),
	(15, 1, 8, 'Hello', '2024-10-08 20:34:21', 1),
	(16, 8, 1, 'kohomd\n', '2024-10-08 20:36:45', 1),
	(17, 1, 8, 'Bosa', '2024-10-08 20:37:59', 1),
	(18, 23, 1, 'Hi master oogwee', '2024-10-08 23:01:02', 1),
	(19, 23, 1, 'Gg', '2024-10-08 23:01:58', 1),
	(20, 1, 23, 'Hello', '2024-10-08 23:03:40', 1),
	(21, 24, 23, 'Hi Criss', '2024-10-08 23:06:37', 2),
	(22, 1, 25, 'Hi', '2024-10-08 23:09:21', 2),
	(23, 1, 8, 'Hi ada viva tiyenawa ne', '2024-10-09 09:27:30', 1),
	(24, 8, 1, 'ow mt 10.30 tiyenne', '2024-10-09 09:31:46', 1),
	(25, 1, 8, 'hello', '2024-10-09 09:32:07', 1),
	(26, 1, 8, 'hi', '2024-10-09 11:03:53', 1),
	(27, 8, 1, 'mn awa', '2024-10-09 11:04:50', 1),
	(28, 1, 8, 'message ek awa', '2024-10-09 11:05:06', 1);

-- Dumping structure for table smart_chat.chat_status
CREATE TABLE IF NOT EXISTS `chat_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smart_chat.chat_status: ~2 rows (approximately)
INSERT INTO `chat_status` (`id`, `name`) VALUES
	(1, 'Seen'),
	(2, 'Sent');

-- Dumping structure for table smart_chat.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mobile` varchar(10) NOT NULL,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `registered_date` datetime NOT NULL,
  `user_status_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_user_status_idx` (`user_status_id`),
  CONSTRAINT `fk_user_user_status` FOREIGN KEY (`user_status_id`) REFERENCES `user_status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smart_chat.user: ~9 rows (approximately)
INSERT INTO `user` (`id`, `mobile`, `first_name`, `last_name`, `password`, `registered_date`, `user_status_id`) VALUES
	(1, '0775512786', 'Master', 'Oogwe', 'Java@8828', '2024-10-08 02:44:43', 1),
	(8, '0779376447', 'Harsha', 'Silva', 'JavaQ@8828', '2024-10-08 03:15:28', 1),
	(12, '0775498828', 'Anjana', 'Induwara', 'Java@8828', '2024-10-08 03:22:34', 2),
	(14, '0719376557', 'Nathsha', 'Perera', 'Java@8828', '2024-10-08 16:00:34', 2),
	(17, '0776677889', 'Jehan', 'peris', 'Java@992', '2024-10-08 16:53:36', 2),
	(18, '0778898782', 'Jaliya', 'Senarathna', 'Java@8828', '2024-10-08 17:04:57', 2),
	(19, '0785512782', 'Moriss', 'Wilson', 'Java@8828', '2024-10-08 17:23:05', 2),
	(23, '0775512782', 'Cristopeter', 'Benjamin', 'Java@8828', '2024-10-08 23:00:27', 2),
	(24, '0789376557', 'Stepfany', 'Kenny', 'Java@8828', '2024-10-08 23:05:59', 2),
	(25, '0765498829', 'Eimi', 'Nyaha', 'Java@8828', '2024-10-08 23:08:59', 2),
	(26, '0769908892', 'Rei', 'takashi', 'Java@8828', '2024-10-09 09:25:41', 2),
	(27, '0778908890', 'Ashen', 'Herath', 'Java@8828', '2024-10-09 11:01:47', 2);

-- Dumping structure for table smart_chat.user_status
CREATE TABLE IF NOT EXISTS `user_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smart_chat.user_status: ~2 rows (approximately)
INSERT INTO `user_status` (`id`, `name`) VALUES
	(1, 'online'),
	(2, 'offline');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
