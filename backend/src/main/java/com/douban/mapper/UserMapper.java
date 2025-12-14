package com.douban.mapper;

import com.douban.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    @Insert("INSERT INTO users (username, email, password_hash, avatar, bio) VALUES (#{username}, #{email}, #{passwordHash}, #{avatar}, #{bio})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET username = #{username}, email = #{email}, avatar = #{avatar}, bio = #{bio} WHERE id = #{id}")
    int update(User user);

    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);
}
