
INSERT INTO `page` (`name`) VALUES ('ROLE');
INSERT INTO `page` (`name`) VALUES ('USER');
INSERT INTO `page` (`name`) VALUES ('PRODUCT');
INSERT INTO `page` (`name`) VALUES ('USER_ROLE');


INSERT INTO `privilege` (`name`) VALUES ('WRITE');
INSERT INTO `privilege` (`name`) VALUES ('UPDATE');
INSERT INTO `privilege` (`name`) VALUES ('READ');
INSERT INTO `privilege` (`name`) VALUES ('DELETE');



INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('1', '1');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('1', '2');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('1', '3');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('1', '4');

INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('2', '1');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('2', '2');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('2', '3');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('2', '4');

INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('3', '1');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('3', '2');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('3', '3');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('3', '4');

INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('4', '1');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('4', '2');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('4', '3');
INSERT INTO `pages_privileges` (`page_id`, `privilege_id`) VALUES ('4', '4');


INSERT INTO `role` (`created_at`, `name`) VALUES ('2022-08-20 17:37:08', 'ADMIN');


INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('1', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('2', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('3', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('4', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('5', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('6', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('7', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('8', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('9', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('10', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('11', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('12', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('13', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('14', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('15', '1');
INSERT INTO `role_pages_privileges` (`pages_privileges_id`, `role_id`) VALUES ('16', '1');




