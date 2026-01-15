package in.tech_camp.protospace_b.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import in.tech_camp.protospace_b.entity.PrototypeEntity;

@Mapper
public interface PrototypeRepository {
  @Insert("INSERT into prototypes (name, catch_copy, concept, image, user_id) VALUES (#{name}, #{catchCopy}, #{concept}, #{image}, #{user.id})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(PrototypeEntity prototype);

  @Select("SELECT * FROM prototypes WHERE id = #{id}")
  PrototypeEntity findById(Integer id);

  @Update("UPDATE prototypes SET name=#{name}, catch_copy=#{catchCopy}, " +
          "concept=#{concept}, image=#{image} WHERE id=#{id}")
  void update(PrototypeEntity prototype);
}
