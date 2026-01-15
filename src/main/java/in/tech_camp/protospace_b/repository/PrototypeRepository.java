package in.tech_camp.protospace_b.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace_b.entity.PrototypeEntity;

@Mapper
public interface PrototypeRepository {
  @Select("SELECT * FROM prototypes WHERE user_id = #{userId}")
    List<PrototypeEntity> findByUserId(Integer userId);
}
