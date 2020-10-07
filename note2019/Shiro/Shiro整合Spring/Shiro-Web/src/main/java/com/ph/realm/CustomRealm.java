package com.ph.realm;

import com.ph.dao.UserDao;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomRealm extends AuthorizingRealm {

//    private  Map<String,String>userMap = new HashMap<>();
//    {
//        userMap.put("admin","da12ef61c3ed65e9ca2b8196e589f642");
//        //这个取什么名称都无所谓
//        super.setName("realName");
//    }
    @Autowired
    UserDao userDao;
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

//    private String getPasswordByUsername(String username) {
//        //现实开发中，通过此访问数据库
//        return userDao.get(username);
//    }
    private String getPasswordByUsername(String username){
        String password = userDao.getPasswordByUsername(username);
        System.out.println(password);
        return password;
    }

    public static void main(String[] args) {
        //盐一般用随机数，这里为了方便写死了
        Md5Hash hash = new Md5Hash("123","MySalt");
        System.out.println(hash);
    }

}
