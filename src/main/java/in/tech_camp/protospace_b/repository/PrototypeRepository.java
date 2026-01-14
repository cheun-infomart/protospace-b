package in.tech_camp.protospace_b.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace_b.entity.PrototypeEntity;

@Mapper
public interface PrototypeRepository {
  
  @Select("SELECT p.*, u.name AS user_name FROM prototypes p INNER JOIN users u ON p.user_id = u.id")
  @Results(value = {
    @Result(property="name", column="name"),
    @Result(property="catchCopy", column="catch_copy"),
    @Result(property="concept", column="concept"),
    @Result(property="image", column="image"),
    @Result(property="user.name", column="user_name")
  })
  List<PrototypeEntity> findAll();
}
