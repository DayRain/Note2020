package com.ph.dao.daoImp;
import com.ph.dao.UserDao;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDaoImp implements UserDao {
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Override
    public String getPasswordByUsername(String username) {
        String sql = "select password from users where username = ?";
        List<String>list = jdbcTemplate.query(sql, new String[]{username}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                String password = resultSet.getString("password");
                return password;
            }
        });
        if(CollectionUtils.isEmpty(list)){
            return null;
        }else{
            return list.get(0);
        }
    }
}
