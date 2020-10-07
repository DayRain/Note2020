# 1、Shrio认证以及退出的简单演示

```java
package com.ph;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

/**
 * Shiro身份验证
 */
public class AuthenticationTest {
    SimpleAccountRealm realm = new SimpleAccountRealm();

    @Before
    public void addUser(){
        realm.addAccount("admin","123");
    }

    @Test
    public void testAuthentication(){
        //1、构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        // SecurityManager 注入 Realm（域），使其得到合法的用户名及密码，与下面的进行比较。
        defaultSecurityManager.setRealm(realm);
        //2、主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        //Subject（主体），通过SecurityUtils获取主体
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("admin","123");
        //用主体的令牌登录
        subject.login(token);
        //显示当前用户主体的身份是否合法
        System.out.println(subject.isAuthenticated());

        //退出登录
        subject.logout();
    }
}
```

# 2、Shiro认证的简单演示

![1555590049044](C:\Users\13760\Desktop\笔记\img\1555590049044.png)

```java
package com.ph;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

/**
 * Shiro身份验证
 */
public class AuthenticationTest {

SimpleAccountRealm realm = new SimpleAccountRealm();
    @Before
    public void addUser(){
        realm.addAccount("admin","123","admin");
    }

    @Test
    public void testAuthentication(){
        DefaultSecurityManager defaultSecurityManager=new DefaultSecurityManager();
        defaultSecurityManager.setRealm(realm);
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken token = new UsernamePasswordToken("admin","123");
        subject.login(token);
        /**
         * checkRole是用来检查用户是否拥有角色
         * 当checkRole拥有多个参数时，会检查用户是否同时满足所有角色，如果不满足，会抛异常。
         */
        subject.checkRole("admin");
        subject.checkRoles("admin","user");
    }
}
```

# 3、内置Realm

## 1）、IniRealm

​          1、user.ini配置文件

```ini
[users]
Mark=123,admin
[roles]
admin=user:delete,user:update
```

​           2、测试类

```java
package com.ph;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
public class IniRealmTest {
    @Test
    public void initRealTest(){

        IniRealm iniRealm = new IniRealm("classpath:user.ini");
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(iniRealm);
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        AuthenticationToken token = new UsernamePasswordToken("Mark","123");
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        System.out.println(subject.isAuthenticated());
        subject.checkRole("admin");
//        subject.checkRole("admin1");   报错
        //检查是否拥有删除的权限
        subject.checkPermission("user:delete");
       // subject.checkPermission("user:create");  报错，因为ini文件中没有create权限
        subject.checkPermission("user:update");
    }

}

```



## 2）、jdbcRealm

###    1、引入依赖

```
<dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.38</version>
    </dependency>
    <dependency>
      <groupId>com.alibaba</groupId>
      <artifactId>druid</artifactId>
      <version>1.1.8</version>
    </dependency>
```

### 2、代码

```
package com.ph;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
public class JdbcRealmTest {
    private  DruidDataSource dataSource = new DruidDataSource();
    {
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
    }

    @Test
    public void jdbcRealmTest(){

        JdbcRealm jdbcRealm = new JdbcRealm();
        /**
         * 使用jdbcRealm，如果没有自定义查询语句，它会使用默认的查询语句
         * DEFAULT_AUTHENTICATION_QUERY = "select password from users where username = ?";
         * DEFAULT_SALTED_AUTHENTICATION_QUERY = "select password, password_salt from users where username = ?";
         * DEFAULT_USER_ROLES_QUERY = "select role_name from user_roles where username = ?";
         * DEFAULT_PERMISSIONS_QUERY = "select permission from roles_permissions where role_name = ?";
         */
        //设置数据源
        jdbcRealm.setDataSource(dataSource);
        //默认false，开启后才能查询权限数据
      //  jdbcRealm.setPermissionsLookupEnabled(true);

        //如果使用自定义的表，和自定义的查询语句
        jdbcRealm.setAuthenticationQuery("select password from test_users where username=?");

        DefaultSecurityManager defaultSecurityManager=new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        UsernamePasswordToken token = new UsernamePasswordToken("mark","123");
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        System.out.println(subject.isAuthenticated());
        subject.checkRole("admin");
    }
}
```

# 4、用户自定义Realm

### 1、编写一个自定义类

```java
package com.ph.realm;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class CustomRealm extends AuthorizingRealm {
    Map<String,String>userMap = new HashMap<>();
    {
        userMap.put("admin","123");
        //这个取什么名称都无所谓
        super.setName("realName");
    }

    //做授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获得用户名
        String username = (String) principalCollection.getPrimaryPrincipal();
        //从数据库或者缓存中获取角色信息以及权限
        Set<String> roles = getRolesByUsername(username);
        Set<String>permision = getPermissionsByUsername(username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permision);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    private Set<String> getRolesByUsername(String username) {
        Set<String> sets = new HashSet<>();
        sets.add("admin");
        sets.add("user");
        return sets;
    }

    private Set<String> getPermissionsByUsername(String username) {
        Set<String>sets = new HashSet<>();
        sets.add("user:delete");
        sets.add("user:update");
        return sets;
    }
    //做认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1、第一步，从主体传过来的令牌中获取用户名
        String username = (String) authenticationToken.getPrincipal();
        //2、第二步、通过用户名从数据库获得凭证
        String password = getPasswordByUsername(username);
        if(password == null)
            return null;
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo("admin",password,"realmName");
        return simpleAuthenticationInfo;
    }

    private String getPasswordByUsername(String username) {
        //现实开发中，通过此访问数据库
        return userMap.get(username);
    }
}
```

### 2、测试类

```java
package com.ph;

import com.ph.realm.CustomRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class CustomRealmTest {

    @Test
    public void testCustomRealm(){
        CustomRealm customRealm = new CustomRealm();
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(customRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("admin","123");
        subject.login(token);
        System.out.println(subject.isAuthenticated());

        subject.checkRole("admin");
        subject.checkPermission("user:delete");
    }
}
```

# 5、Shiro加密

## 1）、不加盐，md5加密

```java
public class CustomRealm extends AuthorizingRealm {

    Map<String,String>userMap = new HashMap<>();
    {
        userMap.put("admin","202cb962ac59075b964b07152d234b70");
        //这个取什么名称都无所谓
        super.setName("realName");
    }

    //做授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获得用户名
        String username = (String) principalCollection.getPrimaryPrincipal();
        //从数据库或者缓存中获取角色信息以及权限
        Set<String> roles = getRolesByUsername(username);
        Set<String>permision = getPermissionsByUsername(username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permision);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    private Set<String> getRolesByUsername(String username) {
        Set<String> sets = new HashSet<>();
        sets.add("admin");
        sets.add("user");
        return sets;
    }

    private Set<String> getPermissionsByUsername(String username) {
        Set<String>sets = new HashSet<>();
        sets.add("user:delete");
        sets.add("user:update");
        return sets;
    }

    //做认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1、第一步，从主体传过来的令牌中获取用户名
        String username = (String) authenticationToken.getPrincipal();
        //2、第二步、通过用户名从数据库获得凭证
        String password = getPasswordByUsername(username);
        if(password == null)
            return null;
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo("admin",password,"realmName");
        return simpleAuthenticationInfo;
    }

    private String getPasswordByUsername(String username) {
        //现实开发中，通过此访问数据库
        return userMap.get(username);
    }

}

```

```java
public class CustomRealmTest {

    @Test
    public void testCustomRealm(){
        CustomRealm customRealm = new CustomRealm();
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(customRealm);

        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //设置加密算法的名称
        matcher.setHashAlgorithmName("md5");
        //设置加密次数
        matcher.setHashIterations(1);

        customRealm.setCredentialsMatcher(matcher);


        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("admin","123");
        subject.login(token);
        System.out.println(subject.isAuthenticated());

        subject.checkRole("admin");
        subject.checkPermission("user:delete");
    }
}

```

## 2)、加盐，md5

```
public class CustomRealm extends AuthorizingRealm {

    Map<String,String>userMap = new HashMap<>();
    {
        userMap.put("admin","da12ef61c3ed65e9ca2b8196e589f642");
        //这个取什么名称都无所谓
        super.setName("realName");
    }

    //做授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获得用户名
        String username = (String) principalCollection.getPrimaryPrincipal();
        //从数据库或者缓存中获取角色信息以及权限
        Set<String> roles = getRolesByUsername(username);
        Set<String>permision = getPermissionsByUsername(username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permision);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    private Set<String> getRolesByUsername(String username) {
        Set<String> sets = new HashSet<>();
        sets.add("admin");
        sets.add("user");
        return sets;
    }

    private Set<String> getPermissionsByUsername(String username) {
        Set<String>sets = new HashSet<>();
        sets.add("user:delete");
        sets.add("user:update");
        return sets;
    }

    //做认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1、第一步，从主体传过来的令牌中获取用户名
        String username = (String) authenticationToken.getPrincipal();
        //2、第二步、通过用户名从数据库获得凭证
        String password = getPasswordByUsername(username);
        if(password == null)
            return null;
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo("admin",password,"realmName");
        //返回前，加盐
        simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("MySalt"));


        return simpleAuthenticationInfo;
    }

    private String getPasswordByUsername(String username) {
        //现实开发中，通过此访问数据库
        return userMap.get(username);
    }

    public static void main(String[] args) {
        //盐一般用随机数，这里为了方便写死了
        Md5Hash hash = new Md5Hash("123","MySalt");
        System.out.println(hash);
    }

}

```

```
package com.ph;

import com.ph.realm.CustomRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class CustomRealmTest {

    @Test
    public void testCustomRealm(){
        CustomRealm customRealm = new CustomRealm();
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(customRealm);

        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //设置加密算法的名称
        matcher.setHashAlgorithmName("md5");
        //设置加密次数
        matcher.setHashIterations(1);

        customRealm.setCredentialsMatcher(matcher);


        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("admin","123");
        subject.login(token);
        System.out.println(subject.isAuthenticated());

        subject.checkRole("admin");
        subject.checkPermission("user:delete");
    }
}

```

# 7、Shiro集成Spring

# 8、通过注解的方式授权

1)、添加AOP依赖

```
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.2</version>
        </dependency>
```

2）、添加beans

```
    <bean class="org.apache.shiro.spring.LifecycleBeanPostProcessor"/>

    <bean class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
        <property name="securityManager" ref="defaultWebSecurityManager"/>
    </bean>
```

3）、控制器

```
       // @RequiresPermissions()
    @RequiresRoles("admin")
    @ResponseBody
    @RequestMapping("/testRoles")
    public String testRoles(){
        return "test Successful";
    }
```

9、Shiro内置过滤器

anon   无需认真

authBasic          httpbasic

authc     需要认证之后才可以访问

user      当前存在用户才可以访问

logout    退出



和授权相关的过滤器

perms  后面加一个中括号：需要具备某些权限才可以访问。

roles                                      需要某些角色才可以访问。

ssl              需要安全的协议，https才可以访问。

port     要求端口是中括号后面写的一些端口才可以访问。