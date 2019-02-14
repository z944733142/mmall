create table mmall_user (
  id int(11) not null  auto_increment COMMENT '用户名id',
  username varchar(50) not null  comment '用户名',
  password varchar(50) not null comment '用户密码, md5加密',
  email varchar(50) not null comment ,
  phone varchar(20) default null ,
  question varchar(100) default null comment '召回密码问题',
  answer varchar(100) default null  comment '召回密码答案',
  role int(4) not null comment '角色0 管理员 角色1 普通用户',
  create_time datatime not null comment '创建时间',
  update _time datatime not null comment '最后一次更新时间',
  primary key (id),
  unique key user_name_unique (username) using BTREE
) engine=InnoDB auto_increment=21 default  charset=utf8