# 九、错误处理机制

1）如果是浏览器，会返回一个错误页面

2）如果是客户端，则会响应一个JSON数据

# 十、数据访问之整合jdbc

## 1、依赖引入

如果是创建时勾选 mysql jdbc，则不需要引入。

```
        <dependency>
          <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
```

## 2、配置

```
spring:
  datasource:
    username: root
    password: Ph0716
    url: jdbc:mysql://192.168.70.131:3306/jdbc
    driver-class-name: com.mysql.cj.jdbc.Driver
    initialization-mode: always
```

## 3、运行见表语句的几种方法

### 1）、将sql名改为

schema-all.sql

![1554888739662](C:\Users\13760\Desktop\笔记\img\1554888739662.png)

### 2)、添加配置文件

```
spring:
  datasource:
    username: root
    password: Ph0716
    url: jdbc:mysql://192.168.70.131:3306/jdbc
    driver-class-name: com.mysql.cj.jdbc.Driver
    initialization-mode: always
    hikari:
      schema:
        - classpath:depatment.sql
```

### 3）、实例

```
@Controller
public class HelloController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @RequestMapping("/query")
    @ResponseBody
    public Map<String,Object> selectByJbcd(){
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select *from department");
        return list.get(0);
    }
}
```

返回的是键值对，字段名：字段值

### 4）、使用自定义的数据源，例如druid数据源

####        引入依赖

```
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.8</version>
        </dependency>
```

#### 配置文件

```
spring:
  datasource:
    username: root
    password: Ph0716
    url: jdbc:mysql://192.168.70.131:3306/jdbc
    driver-class-name: com.mysql.cj.jdbc.Driver
    initialization-mode: always
    type: com.alibaba.druid.pool.DruidDataSource
    initialSize: 5
    minIdle: 5
    maxActive: 20
    maxWait: 60000
    timeBetweenEvictionRunsMillis: 60000
    minEvictableIdleTimeMillis: 300000
    validationQuery: SELECT 1 FROM DUAL
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    poolPreparedStatements: true
    #   配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
    filters: stat,wall,log
    maxPoolPreparedStatementPerConnectionSize: 20
    useGlobalDataSourceStat: true
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
#    hikari:
#      schema:
#        - classpath:depatment.sql
#    type: org.springframework.jdbc.datasource.DriverManagerDataSource
```

#### 配置类

```
@Configuration
public class DruidConfig {
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid(){
        return new DruidDataSource();
    }
    //配置Druid的监控
    //1、配置一个管理后台的Servlet
    @Bean
    public ServletRegistrationBean statViewServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
        Map<String,String>initParams = new HashMap<>();
        initParams.put("loginUsername","admin");
        initParams.put("loginPassword","123456");
        initParams.put("allow","");//默认允许所有
        initParams.put("deny","192.168.15.21");
        bean.setInitParameters(initParams);
        return bean;
    }
    //2、注册一个Web监控的filter
    @Bean
    public FilterRegistrationBean webStatusFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();

        bean.setFilter(new WebStatFilter());
        Map<String,String>initParams = new HashMap<>();
        initParams.put("exclusion","*.js,*.css,/druid/*");
        bean.setInitParameters(initParams);
        bean.setUrlPatterns(Arrays.asList("/*"));
        return bean;
    }
}
```

#### 访问页面

```
http://localhost:8080/druid/sql.html
```

2018/4/10

# 十一、数据访问之整合mybatis

## 1、引入starter

创建springBoot的时候，勾选mysql，jdbc，mybatis，会自动引入

## 2、注解版

```
@Mapper
public interface DepartmentMapper {

    @Select("select * from department where id = #{id}")
    Department getDeptById(Integer id);

    @Delete("delete from department where id = #{id}")
    int deleteDeptByid(Integer id);


    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into department(departmentName) values(#{departmentName})")
    int inserDept(Department department);

    @Update("update department set departmentName= #{departmentName} where id = #{id}")
    int uodatDept(Department department);
}
```

开启驼峰命名，以及定制其他功能

需要实现以下类

```
@org.springframework.context.annotation.Configuration
public class MybatisConfig {

    public ConfigurationCustomizer configurationCustomizer(){
        return new ConfigurationCustomizer() {
            @Override
            public void customize(Configuration configuration) {
                //开启驼峰命名
                configuration.setMapUnderscoreToCamelCase(true);
            }
        };
    }
}
```

如果mapper类上不添加mapper注解

也可以在SpringBoot的配置类上添加扫描

```
@MapperScan(value = "com.ph.project6mybatis.mapper")
@SpringBootApplication
public class Project6MybatisApplication {
    public static void main(String[] args) {
        SpringApplication.run(Project6MybatisApplication.class, args);
    }
}
```



## 3、XML版



1）、全局配置文件

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <settings>
        <setting name="mapUnderscoreToCamelCase" value="true"/>
    </settings>
</configuration>
```

2）、mapper配置文件

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ph.project6mybatis.mapper.EmployeeMapper">
    <select id="selectEmp" resultType="com.ph.project6mybatis.bean.Employee">
        select * from employee where id = #{id}
    </select>
    <insert id="insertEmp" useGeneratedKeys="true" keyProperty="id">
        insert into employee(lastName,email,gender,d_id) values(#{lastName},#{email},#{gender},#{d_id});
    </insert>
</mapper>
```

3）、yml配置注册

```
mybatis:
  config-location: classpath:mybatis/mybatis-config.xml
  mapper-locations: classpath:mybatis/mapper/*.xml
```

4）、mapper文件

```
public interface EmployeeMapper {
    //查询
    Employee selectEmp (Integer id);
    //插入
    Integer insertEmp(Employee employee);
}
```

