package com.tcm.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcm.platform.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // MyBatis-Plus 自动提供 CRUD 方法，无需额外编写
}
