USE blog_system;

CREATE TABLE user(id INTEGER PRIMARY KEY AUTO_INCREMENT,
username VARCHAR(12) NOT NULL,
password VARCHAR(16) NOT NULL,
email VARCHAR(32) NOT NULL,
avatar VARCHAR(128) NOT NULL,
admin BOOLEAN DEFAULT FALSE) ENGINE = InnoDB
DEFAULT CHARSET = UTF8;

create table article(`id` integer primary key auto_increment,
`is_publish` boolean default false,
`post_time` timestamp default now(),
`last_modified` timestamp default now(),
`word_counter` integer default 0,
`publisher` varchar(32) default 'TINY',
`view_times` integer default 0,
`article_title` varchar(120),
`article_content` TEXT(65535),
`description` varchar(655),
`recommend` integer default 0,
`is_top` boolean default false,
`title_image` varchar(655),
`article_label` varchar(655));

delete from article;