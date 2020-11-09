# 注解

## @TableId

value属性

```
对应数据库表的字段名
```

type属性

id的配置策略，已废弃的就不看了

```
    #数据库自增
    AUTO(0), 
    
    #mybatis plus 设置主键，雪花算法
    NONE(1), 
    
    #开发者，手动复制
    INPUT(2),
    
    #mybatis分配id，可以使用Long、Integer、String
    ASSIGN_ID(3),
    
    #分配一个uuid，必须是String
    ASSIGN_UUID(4),
```

## @TableField

普通字段的映射

```
    @TableField(value = "name")
```

看下这个注解里面的东西

```
    String value() default "";

    #该字段数据库中没有，不需要映射
    boolean exist() default true;

    String condition() default "";

    String update() default "";

    FieldStrategy insertStrategy() default FieldStrategy.DEFAULT;

    FieldStrategy updateStrategy() default FieldStrategy.DEFAULT;

    FieldStrategy whereStrategy() default FieldStrategy.DEFAULT;

    #自动填充
    FieldFill fill() default FieldFill.DEFAULT;

    #查询的时候，查询结果忽略该字段
    boolean select() default true;

    boolean keepGlobalFormat() default false;

    JdbcType jdbcType() default JdbcType.UNDEFINED;

    Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

    boolean javaType() default false;

    String numericScale() default "";
```

## @Version

乐观锁

数据库表中添加一个version字段，并将默认值设为1

```
    @Version
    private Integer version;
```

添加拦截器插件

```
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor()); // 乐观锁插件
        return interceptor;
    }

}
```

## 枚举

可以直接将java枚举类型映射为数据库字段

1、建需要映射的java枚举类

加注解

```
public enum StatusEnum {
    WORK(0,"上班"),
    REST(1,"休息");
    StatusEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    @EnumValue
    private Integer code;
    private String msg;
}

```

或者实现接口可以

```
public enum  StatusEnum2 implements IEnum<Integer> {
    WORK(0,"上班"),
    REST(1,"休息");
    StatusEnum2(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    private Integer code;
    private String msg;
    @Override
    public Integer getValue() {
        return null;
    }
}
```



2、实体类直接使用

名字也要映射，如果和数据库字段不一致，可以用@TableField指定

```
    private StatusEnum status;
```

3、配置文件

```
mybatis-plus:
  type-enums-package: com.dayrain.nums
```

## 逻辑删除

1、加注解

```    @TableLogic
@TableLogic
private Integer deleted;
```

2、配置文件

logic-not-delete-value表示正常状况的值，

logic-delete-value表示逻辑删除后的值，

这些是可以根据实际情况自行调整

```
mybatis-plus:
  global-config:
    db-config:
      logic-not-delete-value: 1
      logic-delete-value: 0
```

实战：

```
    @Test
    public void delete() {
        userMapper.deleteById(1);
    }
```

控制台输出

```
==>  Preparing: UPDATE user SET deleted=0 WHERE id=? AND deleted=1
==> Parameters: 1(Integer)
<==    Updates: 0
```

可以看出，删除实际执行的update，逻辑上的删除

此时执行查询

```
    @Test
    void testSelect() {
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }
```

控制台输出

```
JDBC Connection [HikariProxyConnection@1188871851 wrapping com.mysql.cj.jdbc.ConnectionImpl@36e43829] will not be managed by Spring
==>  Preparing: SELECT id,name,age,create_time,update_time,version,status,deleted FROM user WHERE deleted=1
==> Parameters: 
<==      Total: 0
```

发现查询语句会自动加上deleted字段

# 添加



# 修改

# 删除