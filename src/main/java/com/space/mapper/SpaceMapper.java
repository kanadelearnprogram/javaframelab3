package com.space.mapper;

import com.space.model.entity.Space;
import com.space.model.entity.User;
import org.apache.ibatis.annotations.Insert;

public interface SpaceMapper {


    @Insert("INSERT INTO `t_space` (`user_id`, `total_size`, `used_size`, `apply_status`, `create_time`, `update_time`)" +
            "VALUES (#{userId},#{totalSize},#{usedSize}, 'Normal', NOW(), NOW())")
    boolean addUserSpace(Space space);

}
