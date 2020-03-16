create table notification
(
	id bigint auto_increment,
	notifier bigint not null comment '通知者',
	receiver bigint not null comment '接受者',
	outer_id bigint not null,
	type int not null comment '通知类型',
	gmt_create bigint not null,
	status int not null comment '已读未读状态',
		primary key (id)
)
comment '展示通知';

