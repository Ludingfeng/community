package com.ldf.community.community.mapper;

import com.ldf.community.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("insert into user(id,account_id,name,token,gmt_create,gmt_modified) values" +
            "(#{id},#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified})")
    void insertUser(User user);

    @Select("select id,account_id,name,token,gmt_create,gmt_modified from user where token=#{token}")
    User findUserByToken(@Param("token") String token);
}