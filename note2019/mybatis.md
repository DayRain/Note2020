# 一、官网文档链接

http://www.mybatis.org/mybatis-3/zh/index.html

# 二、mybatis简介

## 概念

​      MyBatis 是一款优秀的持久层框架，它支持定制化 SQL、存储过程以及高级映射。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。

​    对于以上定义的理解：

​          持久化（写入到了物理介质当中的数据，持久化的数据，不仅仅是数据库）。

​          定制sql（区别于手写）

​         高级映射（不需要再一味的写get set函数）

​         消除了模块代码

​         使用xml书写sql（风格）

​        ORM（object relational Mapping） 对象关系映射（对象是java中的概念，schema关系是数据库中的概念）

​        mybatis是一个半自动的ORM框架（还有一些需要手工来完成）

​     优点：全程我们说了算，性能好掌握。适用于所需要java访问数据库的项目

​     缺点：累一点

  ## 和hibernate区别

hibernate适合复杂度小，不怎么变更、稳定性强的项目。

mybatis还可以做数据报表，hibernate做这个很吃力。

# 三、入门体验

1、引入依赖

2、编写一个核心的配置文件

3、配置会话工厂

4、通过API操作

​       1、pojo

​       2、Mapper接口

​       3、MapperSQL xml文件

# 四、组件分析

- SqlSessionFactoryBuilder(建造者模式，过河拆桥)
- SqlSessionFactory（工厂，持续生产，不能死，单例模式）
- SqlSession（工厂生产出来的）
- Mapper
- Mapper.xml

# 五、动态sql

## if标签

```
    <select id="queryByCityOrCountry" resultType="com.ph.pojo.Addresses">
        select * from address
        <where>
            <if test="country != null">
                and  country = #{country}
            </if>
            <if test="city != null">
                and city = #{city}
            </if>
        </where>
    </select>
```

## where标签

  注意点：使用where标签时，多余的前置and可以自动去掉，但是少的不能补上。

```
    <select id="queryByCityOrCountry" resultType="com.ph.pojo.Addresses">
        select * from address
        <where>
            <if test="country != null">
                and  country = #{country}
            </if>
            <if test="city != null">
                and city = #{city}
            </if>
        </where>
    </select>
```

## set标签

注意点：使用set标签时，多余的后置逗号可以自动去掉，但是少的不能补上。

```
<update id="updateAddressesSelective">
        update address
        <set>
            <if test="city != null">
                city = #{city},
            </if>
            <if test="state != null">
                state = #{state},
            </if>
            <if test="country != null">
                country = #{country},
            </if>
            <if test="street != null" >
                street = #{street},
            </if>
            <if test="zip != null">
                zip = #{zip}
            </if>
        </set>
        where addr_id = #{addrId};
    </update>
```

## choose，when,otherwise

注意点：补充在if不满足条件时的其他用法

```
    <select id="queryByCondition" resultType="com.ph.pojo.Addresses">
        select *from address
        where
        <choose>
            <when test="country != null">
                 country = #{country};
            </when>
            <otherwise>
                 city = #{city};
            </otherwise>
        </choose>
    </select>
```

## trim标签的使用

```
<insert id="insertTrim">
       insert into t_blog
       <trim prefix="(" suffix=")" prefixOverrides=",">
           <if test="b_id != null">
               ,b_id
           </if>
           <if test="title != null and title != ''">
               ,title
           </if>
           <if test="content != null and content != ''">
               ,content
           </if>
           <if test="comment_num != null">
               ,comment_num
           </if>
       </trim>

        <trim prefix="values(" suffix=")" prefixOverrides=",">
            <if test="b_id != null">
                ,#{b_id}
            </if>
            <if test="title != null and title != ''">
                ,#{title}
            </if>
            <if test="content != null and content != ''">
                ,#{content}
            </if>
            <if test="comment_num != null">
                ,#{comment_num}
            </if>
        </trim>

    </insert>
```

## 模糊查询

```
 <!--模糊查询-->
    <select id="queryTitleLike" resultType="com.ph.pojo.MyBlog">
    select
    <include refid="baseSql"/>
    , content
    from t_blog
    where title like concat('%',#{title},'%')
    </select>
    <!--
       方法三：绑定
        <select id="queryTitleLike"resultType="com.ph.pojo.MyBlog">
        <bind name="bTitle" value="'%'+title+'%'"/>
        select *
        from t_blog
        where title like #{bTitle}
    </select>
    -->
```



## note1

测试过程中出现子项目无法继承父项目jar包问题，解决方案：删除项目目录文件夹下的“.idea”文件夹，然后重启idea，打开项目。

## note2

${}     #{}  区别

${}可用于表名，但是有sql注入风险，一般推荐用 #{}

properties节点：  可添加resource属性引入外部文件，也可以直接在该节点内写。

typeAliases节点：类型别名

封装方法：单个基本数据类型封装，多个基本数据类型封装，javaBean封装，Map封装（通过key值）

```
List<Girl>selectByNameAndFlowerWithMap(Map<String,Object>map);
```

单个javaBean：通过JavaBean里面的属性名去引用，通过getter方法获取值

# 六、error汇总

## 1、返回集问题

```
org.apache.ibatis.exceptions.TooManyResultsException: Expected one result (or null) to be returned by selectOne(), but found: 2
```

出现上述问题时，说明select的返回集不止一个，不能用单个对象来接受，应该定义为多个。

## 2、参数未找到问题

```
 Cause: org.apache.ibatis.binding.BindingException: Parameter 'name' not found. Available parameters are [arg1, arg0, param1, param2]
```

当使用两个及以上基本数据类型参数时，没有添加param标记，会报错。

## 3.创建文件失败后，不能创建同名的文件

```
Unable to parse template "Class"
Error message: Selected class file name 'Blog.java' mapped to not java file type 'Text'
```

解决方案：setting————fileType-——————删掉创建错误的文件

# 七、几个配置文件及工具类

## 1、工具类

```
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MybatisUtil {
    private static SqlSessionFactory sqlSessionFactory=null;
    static{
        InputStream in = null;
        String resource = "mybatis.cfg.xml";
        try{
            in = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static SqlSession getSession(){
        return sqlSessionFactory.openSession();
    }
}
```

## 2、mybatis.cfg.xml（可通过插件生成）

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <properties resource="jdbc.properties"></properties>
    <settings>
        <!-- Globally enables or disables any caches configured in any mapper under this configuration -->
        <setting name="cacheEnabled" value="false"/>
        <!-- Sets the number of seconds the driver will wait for a response from the database -->
        <setting name="defaultStatementTimeout" value="5"/>
        <!-- Enables automatic mapping from classic database column names A_COLUMN to camel case classic Java property names aColumn -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <!-- Allows JDBC support for generated keys. A compatible driver is required.
        This setting forces generated keys to be used if set to true,
         as some drivers deny compatibility but still work -->
        <setting name="useGeneratedKeys" value="true"/>
    </settings>
    <!-- Continue editing here -->
    <environments default="dev">
        <environment id="dev">
            <transactionManager type="JDBC"></transactionManager>
            <dataSource type="UNPOOLED">
                <property name="url" value="${url}"/>
                <property name="driver" value="${driver}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="com.ph.mapper/UserMapper.xml"/>
    </mappers>
</configuration>
```

## 3、mapper头文件

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
```

## 4、如果使用log4j调试，则需要添加 log4j.properties

```
# Global logging configuration
log4j.rootLogger=ERROR, stdout
# MyBatis logging configuration...
log4j.logger.com.ph.mapper=DEBUG
# Console output...
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%5p [%t] - %m%n
```

# 八、自动配置插件使用

1、引入依赖和插件

```
    <dependency>
      <groupId>org.mybatis.generator</groupId>
      <artifactId>mybatis-generator-core</artifactId>
      <version>1.3.2</version>
    </dependency>
```



```
<plugin>
          <groupId>org.mybatis.generator</groupId>
          <artifactId>mybatis-generator-maven-plugin</artifactId>
          <version>1.3.2</version>
          <dependencies>
            <dependency>
              <groupId>org.mybatis.generator</groupId>
              <artifactId>mybatis-generator-core</artifactId>
              <version>1.3.2</version>
            </dependency>
            <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
              <version>5.1.38</version>
            </dependency>
          </dependencies>
          <executions>
            <execution>
              <id>mybatis  generator</id>
              <phase>package</phase>
              <goals>
                <goal>generate</goal>
              </goals>
            </execution>
          </executions>
          <configuration>
            <!--配置文件的位置-->
            <configurationFile>src/main/resources/generatorConfig.xml</configurationFile>
<!--           允许移动生成的文件 -->
            <verbose>true</verbose>
<!--            是否覆盖-->
            <overwrite>true</overwrite>
          </configuration>
        </plugin>
```

2、配置文件 generatorConfig.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
<!--    <classPathEntry location="/Program Files/IBM/SQLLIB/java/db2java.zip" />-->

    <context id="DB2Tables" targetRuntime="MyBatis3">
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://127.0.0.1/miaosha"
                        userId="root"
                        password="123456">
        </jdbcConnection>

        <javaTypeResolver >
            <property name="forceBigDecimals" value="false" />
        </javaTypeResolver>


        <!--生成的DataObject类存放位置（pojo）-->
        <javaModelGenerator targetPackage="com.ph.dataObject" targetProject="src/main/java">
            <property name="enableSubPackages" value="true" />
            <property name="trimStrings" value="true" />
        </javaModelGenerator>

<!--        生成的映射文件存放位置-->
        <sqlMapGenerator targetPackage="mapping"  targetProject="src/main/resources">
            <property name="enableSubPackages" value="true" />
        </sqlMapGenerator>
        
<!--        生成的DAO所在位置-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.ph.dao"  targetProject="src/main/java">
            <property name="enableSubPackages" value="true" />
        </javaClientGenerator>
<!--        生成对应表及表格-->
<!--        <table schema="DB2ADMIN" tableName="ALLTYPES" domainObjectName="Customer" >-->
<!--            <property name="useActualColumnNames" value="true"/>-->
<!--            <generatedKey column="ID" sqlStatement="DB2" identity="true" />-->
<!--            <columnOverride column="DATE_FIELD" property="startDate" />-->
<!--            <ignoreColumn column="FRED" />-->
<!--            <columnOverride column="LONG_VARCHAR_FIELD" jdbcType="VARCHAR" />-->
<!--        </table>-->
        <table tableName="user_info" domainObjectName="UserDO" enableCountByExample="false"
        enableDeleteByExample="false" enableUpdateByExample="false"
        enableSelectByExample="false" selectByExampleQueryId="false"></table>
        <table tableName="user_password" domainObjectName="UserPasswordDO" enableCountByExample="false"
        enableDeleteByExample="false" enableUpdateByExample="false"
        enableSelectByExample="false" selectByExampleQueryId="false"></table>
    </context>
</generatorConfiguration>

```

3、配置参数

![1555736425163](C:\Users\13760\Desktop\笔记\img\1555736425163.png)