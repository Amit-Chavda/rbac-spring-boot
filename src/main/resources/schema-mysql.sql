DROP TABLE IF EXISTS `privilege`;
CREATE TABLE `privilege` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS `page`;
CREATE TABLE `page` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS `pages_privileges`;
CREATE TABLE `pages_privileges` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `page_id` bigint DEFAULT NULL,
  `privilege_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN  KEY (`privilege_id`) REFERENCES privilege(id),
  FOREIGN KEY (`page_id`) REFERENCES page(id)
);

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enabled` BOOLEAN NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `special_privileges` BOOLEAN NOT NULL,
  PRIMARY KEY (`id`)
);



DROP TABLE IF EXISTS `users_roles`;
CREATE TABLE `users_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  FOREIGN KEY (`role_id`) REFERENCES role(id),
  FOREIGN KEY (`user_id`) REFERENCES users(id)
);


DROP TABLE IF EXISTS `role_pages_privileges`;
CREATE TABLE `role_pages_privileges` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pages_privileges_id` bigint DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN  KEY (`pages_privileges_id`) REFERENCES pages_privileges(id),
  FOREIGN KEY  (`role_id`) REFERENCES role(id),
  FOREIGN KEY (`user_id`) REFERENCES users(id)
);

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `picture_url` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`)
);
