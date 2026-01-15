package in.tech_camp.protospace_b.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace_b.entity.PrototypeEntity;

@Mapper
public interface PrototypeRepository {
  
  @Select("SELECT * FROM prototypes WHERE id = #{id}")
  @Results(value={
    @Result(property = "user", column = "user_id",
      one = @One(select = "in.tech_camp.protospace_b.repository.UserRepository.findById"))
  })
   PrototypeEntity findById(Integer id);
   
}
