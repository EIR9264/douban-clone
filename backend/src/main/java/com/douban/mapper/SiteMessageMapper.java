package com.douban.mapper;

import com.douban.entity.SiteMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SiteMessageMapper {

    @Insert("INSERT INTO site_messages (receiver_id, sender_id, title, content, status) VALUES (#{receiverId}, #{senderId}, #{title}, #{content}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SiteMessage message);

    @Select("SELECT * FROM site_messages WHERE receiver_id = #{receiverId} ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<SiteMessage> findByReceiver(@Param("receiverId") Long receiverId, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT * FROM site_messages WHERE receiver_id = #{receiverId} AND status = 'UNREAD' ORDER BY created_at DESC")
    List<SiteMessage> findUnread(@Param("receiverId") Long receiverId);

    @Update({
            "<script>",
            "UPDATE site_messages SET status = 'READ' WHERE id IN",
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int markRead(@Param("ids") List<Long> ids);

    @Select("SELECT COUNT(*) FROM site_messages WHERE receiver_id = #{receiverId} AND status = 'UNREAD'")
    int countUnread(@Param("receiverId") Long receiverId);
}
