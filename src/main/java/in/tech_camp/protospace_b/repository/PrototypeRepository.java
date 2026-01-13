package in.tech_camp.protospace_b.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import in.tech_camp.protospace_b.entity.PrototypeEntity;

@Mapper
public interface PrototypeRepository {
  @Insert("INSERT into prototypes (name, catch_copy, concept, image, user_id) VALUES (#{name}, #{catchCopy}, #{concept}, #{image}, #{userId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(PrototypeEntity prototype);
}
