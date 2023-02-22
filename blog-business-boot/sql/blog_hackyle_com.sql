DROP DATABASE IF EXISTS blog_hackyle_com_dev;
CREATE DATABASE blog_hackyle_com_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE blog_hackyle_com_dev;

DROP TABLE IF EXISTS tb_administrator;
CREATE TABLE tb_administrator (
    id BIGINT NOT NULL COMMENT 'ID：为了后续数据迁移，不使用自增主键，使用时间戳',

    username VARCHAR(128) NOT NULL COMMENT '登录用户名',
    password VARCHAR(512) NOT NULL COMMENT '登录密码',

    nick_name VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    real_name VARCHAR(32) DEFAULT NULL COMMENT '真实姓名',
    description VARCHAR(2048) DEFAULT NULL COMMENT '描述',

    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(64) DEFAULT NULL COMMENT '电话',
    address varchar(128) DEFAULT NULL COMMENT '地址',
    birthday DATETIME DEFAULT NULL COMMENT '生日',
    gender INT DEFAULT 0 COMMENT '性别：1-男, 0-女',
    avatar VARCHAR(512) DEFAULT NULL COMMENT '头像',
    is_locked BIT DEFAULT 0 COMMENT '是否锁定：0-No 1-Yes，锁定的无法登录',

    create_time DATETIME DEFAULT now(),
    update_time DATETIME DEFAULT now() on UPDATE now(),
    is_deleted BIT DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',

    PRIMARY KEY (id),
    INDEX idx_user_pw (username, password)
) ENGINE=InnoDB COMMENT '用户(管理员)信息';
-- 插入默认数据：密码=kyleshawe
INSERT INTO tb_administrator (id, username, password, nick_name, real_name, description, email, phone, address, birthday, gender, avatar)
VALUES (11111, 'blog_hackyle_admin', 'Mo6f7utOcua1B3Id8EsAabzVfKbvRxGWUIW2tsr+CFU=', 'Hackyle', 'Kyle Shawe', 'I am Kyle Shawe, super admin', '1617358182@qq.com', '15797953262', 'SH CN', '1997-01-06 20:00:00', 1, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif');


# 文章主体
DROP TABLE IF EXISTS tb_article;
CREATE TABLE tb_article (
    id BIGINT NOT NULL COMMENT 'ID：为了后续数据迁移，不使用自增主键，使用时间戳',

    title VARCHAR(256) NOT NULL COMMENT '标题',
    summary VARCHAR(5000) DEFAULT NULL COMMENT '总结概要',
	keywords VARCHAR(1000) DEFAULT NULL COMMENT '文章关键字，直接用于meta标签，SEO',
    uri VARCHAR(256) NOT NULL COMMENT '文章的URI，文章链接最终为：https://domain.com/category-code/URI',
    content TEXT NOT NULL COMMENT '文章内容',

    version INT DEFAULT 1 COMMENT '文章历史版本号',
    face_img_link VARCHAR(512) DEFAULT NULL COMMENT '封面图URL',

    is_released BIT DEFAULT 0 COMMENT '是否发布：0-草稿 1-发布',
    is_commented BIT DEFAULT 0 COMMENT '是否可以评论：0-不可评论 1-可以评论',

    create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
    update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
    is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT '文章主体';

# 文章分类
DROP TABLE IF EXISTS tb_category;
CREATE TABLE tb_category (
    id BIGINT NOT NULL COMMENT 'ID：为了后续数据迁移，不使用自增主键，使用时间戳',

    name  VARCHAR(32) NOT NULL COMMENT '分类名称',
    code  VARCHAR(16) NOT NULL COMMENT '分类编码',
    description VARCHAR(1024) DEFAULT NULL COMMENT '分类描述',
    icon_url    VARCHAR(512) DEFAULT NULL COMMENT '分类的图标URL',
    parent_id   BIGINT DEFAULT NULL COMMENT '上一级分类',

    create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
    update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
    is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT '文章分类';

# 分类-文章关联
DROP TABLE IF EXISTS tr_article_category;
CREATE TABLE tr_article_category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category_id BIGINT NOT NULL COMMENT '分类ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',

    create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
    update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
    is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
    PRIMARY KEY (id),
    INDEX idx_cid (category_id),
    INDEX idx_aid (article_id)
) ENGINE=InnoDB COMMENT '文章-分类关联';

# 文章标签
DROP TABLE IF EXISTS tb_tag;
CREATE TABLE tb_tag (
    id BIGINT NOT NULL COMMENT 'ID：为了后续数据迁移，不使用自增主键，使用时间戳',

    name VARCHAR(64) NOT NULL COMMENT '标签名称',
    code  VARCHAR(64) NOT NULL COMMENT '标签编码',
    color VARCHAR(16) DEFAULT NULL COMMENT '标签颜色',
    description VARCHAR(2048) DEFAULT NULL COMMENT '描述',

    create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
    update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
    is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT '文章标签';


# 文章-标签关联
DROP TABLE IF EXISTS tr_article_tag;
CREATE TABLE tr_article_tag (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tag_id BIGINT NOT NULL COMMENT '分类ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',

    create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
    update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
    is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
    PRIMARY KEY (id),
    INDEX idx_tid (tag_id),
    INDEX idx_aid (article_id)
) ENGINE=InnoDB COMMENT '文章-标签关联';

# 作者信息
DROP TABLE IF EXISTS tb_author;
CREATE TABLE tb_author (
    id BIGINT NOT NULL COMMENT 'ID：为了后续数据迁移，不使用自增主键，使用时间戳',

    nick_name VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    real_name VARCHAR(32) DEFAULT NULL COMMENT '真实姓名',
    description VARCHAR(2048) DEFAULT NULL COMMENT '描述',

    email varchar(128) DEFAULT NULL COMMENT '用户邮箱',
    phone varchar(128) DEFAULT NULL COMMENT '用户手机号',
    address varchar(128) DEFAULT NULL COMMENT '地址',

    birthday DATETIME DEFAULT now() COMMENT '生日',
    gender INT DEFAULT 1 COMMENT '1-男; 0-女',
    avatar varchar(512) DEFAULT NULL COMMENT '用户的头像地址',

    is_locked BIT NOT NULL DEFAULT 0 COMMENT '0-False-未锁定，正常; 1-True-已锁定',

    create_time DATETIME DEFAULT now(),
    update_time DATETIME DEFAULT now() ON UPDATE now(),
    is_deleted BIT DEFAULT 0 COMMENT '0-False-未删除;1-True-删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT '作者信息';

# 标签-文章关联
DROP TABLE IF EXISTS tr_article_author;
CREATE TABLE tr_article_author (
    id BIGINT NOT NULL AUTO_INCREMENT,
    author_id BIGINT NOT NULL COMMENT '作者ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',

    create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
    update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
    is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
    PRIMARY KEY (id),
    INDEX author_id_idx (author_id),
    INDEX article_id_idx (article_id)
) ENGINE=InnoDB COMMENT '文章-作者关联';

# 评论信息
DROP TABLE IF EXISTS tb_comment;
CREATE TABLE tb_comment (
	id BIGINT NOT NULL COMMENT 'ID：为了后续数据迁移，不使用自增主键，使用时间戳',
	`target_id` BIGINT NOT NULL COMMENT '被评论主体(文章、页面、动态)的ID',

	# 评论者
	`name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '评论者名称',
	`email` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '评论者的邮箱',
	`link` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '评论者的拓展链接，如个人网站地址',
	`ip` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '评论者的ip地址',

	# 被评论者
	`reply_who` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '被评论者的名称',

	`content` VARCHAR(2048) NOT NULL DEFAULT '' COMMENT '评论内容',

	`parent_id` BIGINT NOT NULL DEFAULT -1 COMMENT '父评论，如果为-1表示没有父评论',

	`is_released` BIT DEFAULT 0 COMMENT '是否发布评论：0-未审核，1-审核通过',
	create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
	update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
	is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
	PRIMARY KEY (id),
	INDEX idx_tid (target_id),
	INDEX idx_pid (parent_id)
) ENGINE=InnoDB COMMENT '评论信息';

DROP TABLE IF EXISTS tb_friend_link;
CREATE TABLE tb_friend_link (
	id BIGINT NOT NULL COMMENT 'ID：为了后续数据迁移，不使用自增主键，使用时间戳',

	name VARCHAR(128) NOT NULL COMMENT '友链名称',
	link_url VARCHAR(256) NOT NULL COMMENT '链接',
	description VARCHAR(1024) DEFAULT NULL COMMENT '链接描述',
	link_avatar_url VARCHAR(256) DEFAULT NULL COMMENT '友链图片地址',
	rank_weight INT DEFAULT 0 COMMENT '链接排序权重',

	create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
	update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
	is_released BIT DEFAULT 0 COMMENT '是否发布：0-待审查 1-发布',
	is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
	PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT '友情链接';

# 留言和反馈
DROP TABLE IF EXISTS tb_feedback_message;
CREATE TABLE tb_feedback_message (
	id BIGINT NOT NULL COMMENT 'ID：为了后续数据迁移，不使用自增主键，使用时间戳',

	name varchar(64) DEFAULT NULL COMMENT '留言者称呼',
	email varchar(128) DEFAULT NULL COMMENT '留言者邮箱',
	phone varchar(64) DEFAULT NULL COMMENT '留言者电话',
	link varchar(1024) DEFAULT NULL COMMENT '留言者的链接（个人主页、博客页）',
	ip varchar(40) DEFAULT NULL COMMENT '留言者IP（IPv4最多15个字符; IPv6-最多29个字符）',
	content varchar(6000) DEFAULT NULL COMMENT '留言内容',

	is_released BIT DEFAULT 0 COMMENT '是否展示（已审查）：0-不展示 1-展示',
	create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
	update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
	is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
	PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT '留言和反馈';


# 文章访问日志
DROP TABLE IF EXISTS tl_article_access;
CREATE TABLE tl_article_access (
	id BIGINT NOT NULL AUTO_INCREMENT,

	-- 被访问者
	article_uri VARCHAR(256) NOT NULL COMMENT '文章地址，tb_article中的uri字段',

	-- 访问者信息
	ip varchar(40) DEFAULT NULL COMMENT '留言者IP（IPv4最多15个字符; IPv6-最多29个字符）',
	address VARCHAR(64) DEFAULT NULL COMMENT '地址',
	browser VARCHAR(64) DEFAULT NULL COMMENT '浏览器类型',
	time_use INT DEFAULT 0 COMMENT '请求耗时',

	create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
	-- update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
	is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
	PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT '文章访问日志';

# 静态资源（图片、视频、音乐、文档等）管理
DROP TABLE IF EXISTS tb_file_storage;
CREATE TABLE tb_file_storage (
	id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID：为了后续数据迁移，不使用自增主键，使用时间戳',

	article_uri VARCHAR(256) DEFAULT '' COMMENT '文章地址，tb_article中的uri字段',
	file_link VARCHAR(512) NOT NULL COMMENT '文件地址',

	create_time DATETIME DEFAULT now() COMMENT '创建时间: 年-月-日 时:分:秒',
	update_time DATETIME DEFAULT now() ON UPDATE now() COMMENT '更新时间',
	is_deleted BIT DEFAULT 0 COMMENT '是否删除：0-false-未删除;1-true-已删除',
	PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT '静态资源（图片、视频、音乐、文档等）管理';

