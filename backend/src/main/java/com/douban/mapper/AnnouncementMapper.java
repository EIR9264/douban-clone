package com.douban.mapper;

import com.douban.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    @Select("SELECT * FROM announcements ORDER BY created_at DESC")
    List<Announcement> findAll();

    @Insert("INSERT INTO announcements (title, content, active, created_by) VALUES (#{title}, #{content}, #{active}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);

    @Update("UPDATE announcements SET active = #{active} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("active") boolean active);

    @Delete("DELETE FROM announcements WHERE id = #{id}")
    int delete(@Param("id") Long id);
}
