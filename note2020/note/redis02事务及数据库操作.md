# 一、事务

redis事务：一组命令的集合，这一组命令都会被序列化，在事务执行的过程中，按照顺序执行

- 开始事务（multi）
- 命令入队（输入一系列命令）
- 执行事务（exec）

discard可以取消事务

watch配合事务可以实现乐观锁

# 二、应用

## 1、jedis

api与前面的命令类似

```
public class TestPing {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("175.24.15.179",6379);
        jedis.auth("Ph0716");

        jedis.flushDB();
        jedis.set("name","admin");
        jedis.set("age","18");
        String name = jedis.type("name");
        System.out.println(name);
        jedis.rename("name","username");
        System.out.println(jedis.select(0));

        System.out.println(jedis.keys("*"));
    }
}

```

事务

```
public class TestTX {
    public static void main(String[] args) throws JsonProcessingException {
        Jedis jedis = new Jedis("175.24.15.179",6379);
        jedis.auth("Ph0716");

        jedis.flushDB();
        HashMap<String, Object> map = new HashMap<String, Object> ();
        map.put("name", "hello");
        map.put("pwd", "123");
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(map);

        Transaction multi = jedis.multi();
        try{
            multi.set("user1", s);
            multi.set("user2", s);
            int i = 1/0;
            multi.exec();
        }catch (Exception e){
            multi.discard();//放弃事务
            e.printStackTrace();
        }finally {
            System.out.println(jedis.get("user1"));
            System.out.println(jedis.get("user2"));
        }

        System.out.println(jedis.keys("*"));
    }
}

```

## 2、SpringBoot中使用

springboot2.x后，用lettuce代替了jedis

jedis：采用的直连，多个线程操作的话，是不安全的。

lettuce：采用netty，实例可以由多个线程共享。

### 1、导入依赖

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
```

### 2、注入使用

```
    @Autowired
	private RedisTemplate redisTemplate;

	@Test
	void contextLoads() {
		redisTemplate.opsForValue().set("name", "dayrain");
		Object name = redisTemplate.opsForValue().get("name");
		System.out.println(name);

		//获取redis连接对象
		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
		connection.flushDb();
		connection.flushAll();
	}
```

### 3、序列化问题

如果想要存对象，对象是必须经过序列化的。

```
	@Test
	void test2() throws JsonProcessingException {
		redisTemplate.getConnectionFactory().getConnection().flushDb();
		User user = new User();
		user.setAge(18);
		user.setUsername("dayrain");
		user.setPassword("123");
//		redisTemplate.opsForValue().set("user1", new ObjectMapper().writeValueAsString(user));
		redisTemplate.opsForValue().set("user1", user);
		Object user1 = redisTemplate.opsForValue().get("user1");
		System.out.println(user1.toString());
	}
```

如果上述的user没序列化，会直接报错。

### 4、自定义模板

```
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);

        //序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        //string的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //key采用string的序列化
        template.setKeySerializer(stringRedisSerializer);
        //hash的key也采用string的方式序列化
        template.setHashKeySerializer(stringRedisSerializer);
        //value的方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //hash的value方法用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }
}
```

### 5、工具类

```
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Object>redisTemplate;

    // =============================common============================
    /**
     * 指定缓存失效时间
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }


    // ============================String=============================

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 普通缓存放入并设置时间
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */

    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 递增
     * @param key   键
     * @param delta 要增加几(大于0)
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * 递减
     * @param key   键
     * @param delta 要减少几(小于0)
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }


    // ================================Map=================================

    /**
     * HashGet
     * @param key  键 不能为null
     * @param item 项 不能为null
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * HashSet 并设置时间
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }


    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }


    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }


    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     * @param key 键
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 获取set缓存的长度
     *
     * @param key 键
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */

    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取list缓存的长度
     *
     * @param key 键
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将list放入缓存
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */

    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */

    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

}
```



# 三、配置



快照

```
表示900秒内，如果至少有一个请求，则开始持久化
save 900 1
300，如果至少有一个请求，则开始持久化
save 300 10
表示60秒内，如果至少有一个请求，则开始持久化
save 60 10000
```

持久化出错是否还将继续工作

```
stop-writes-on-bgsave-error yes
```

redb文件是否压缩

```
rdbcompression yes
```

保存rdb文件的时候，是否要进行校验

```
rdbchecksum yes
```

持久化文件的文件名

```
dbfilename dump.rdb
```

rdb文件保存的目录

```
dir ./

```

# 四、持久化

## 1、RDB

主进程不进行任何IO操作，开一个子进程来进行持久化，将数据先保存在临时文件内，待持久化过程快要结束的的时候，再用整个临时文件夹替换上次持久化好的文件。

### 1、触发场景

1、save的规则满足的情况下，会自动触发rdb规则。

2、执行flushall命令，也会出发我们的rdb规则。

3、退出redis，也会产生rdb文件！

备份就会自动生成一个dump.rdb

### 2、如何恢复

1、只需要将rdb文件放到redis启动目录下就可以了，redis启动的时候会自动检查dump.rdb回复其中的数据！

```
将rdb文件放在redis启动目录就可以，redis启动的时候会自动检查dump.rdb恢复其中的数据！
```

查看位置

```
127.0.0.1:6379> config get dir
1) "dir"
2) "/opt/redis-6.0.6/src"
```

## 2、AOF

append only file

把命令记录下来，恢复的时候吧文件全部执行一遍。

###  1、配置

默认不开启，如果想要开启，修改

appendonly=yes

### 2、触发场景

重启，redis立即生效

### 3、修改

如果文件被破坏，需要修复的话

```
redis-check-aof --fix appendonly.aof
```

# 五、订阅发布

## 1、订阅频道

```
127.0.0.1:6379> SUBSCRIBE dayrain
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "dayrain"
3) (integer) 1
```

## 2、发布消息

再开一个窗口来发布消息

```
127.0.0.1:6379> PUBLISH dayrain "hello,world"
(integer) 1
```

## 3、收到消息

向某频道发送消息后，订阅该频道的所有人都会收到

```
1) "message"   #载体
2) "dayrain"   #频道名称
3) "hello,world"  #消息的具体内容
```

## 4、原理

redis-server里维护了一个字典，字典的键就是一个个频道，而字典的值则是一个链表，链表中保存了所有订阅这个频道的客户端，subscribe命令的关键，就是把客户端添加到给定频道的订阅链表中。

# 六、redis集群s

## 1、概念

主从复制，是指将一台Redis服务器的数据，复制到其他的Redis服务器。前者称为主节点（master/leader），后者称为从节点（slave/follower)；数据的复制时单向的，只能从主节点到从节点。Master以写为主，Slave以读为主。

默认情况下，每台Redis服务器都是主节点；且一个主节点可以有多个从节点（或者没有从节点），但一个从节点只能有一个主节点。

## 2、主从复制

主要有以下作用：

1、数据冗余：主从复制实现了数据的热备份，是持久化以外的一种数据冗余方式。

2、故障恢复：当主从节点出现问题时，可以由从节点提供服务，实现快速的故障恢复；实际上是一种服务的冗余。

3、负载均衡：在主从复制的基础上，配合读写分离，可以由主节点提供写服务，由从节点提供读服务（即写Redis数据时应用连接主节点，读Redis数据时应用连接从节点），分担服务器负载；尤其是在写少读多的场景下，通过多个从节点分担读负载，可以大大提高redis服务器的并发量。

4、高可用（集群）基石：除了上述作用以外，主从复制还是哨兵和集群能够实施的基础，因此说主从复制是Redis高可用的基础。

## 3、环境配置

只配置从库，不用配置主库！

查看主从信息

```
127.0.0.1:6379> info replication
# Replication
role:master
connected_slaves:0
master_replid:6c8d7323791558a0b0d085284a115df0efc1103b
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:0
second_repl_offset:-1
repl_backlog_active:0
repl_backlog_size:1048576
repl_backlog_first_byte_offset:0
repl_backlog_histlen:0

```

### 3.1  主机未设密码

如果主机没有设密码，直接在从机客户端内认证即可

```
 SLAVEOF  主机号   端口
```

也可以在配置文件中配置

```
replicaof 127.0.0.1 6379
```

### 3.2 主机设置密码

如果主机设置了密码，则需要在从机配置文件中配置

```
replicaof 127.0.0.1 6379
masterauth Ph0716
```

命令行设置只是暂时的，例如命令行设置一个从机，重启后还是会自动变成主机。如果要想

## 4、复制原理

Slave启动成功连接到master后会发送一个sync命令。

Master接到命令，启动后台的存盘进程，同时收集所有接收到的用于修改数据集命令，在后台进程执行完毕后，master将传送整个数据文件到slave，并完成一次完全同步。

全量复制：slave刚连接或者是重新连接master时，将会进行一次全量复制，后续master写入新的数据后，会将收集到的命令传递给slave，完成增量复制。

## 5、宕机

如果宕机了，可以在子节点通过命令

```
slaveof on one
```

使其变为主节点，再手动配置其他节点。

## 6、哨兵模式

主从切换技术的方法是：当主服务器宕机后，需要手动把一台从服务器切换为主服务器，这就需要人工干预，费时费力，话会造成一段时间内服务的不可用。这不是一种推荐的方式，更多时候，我们优先考虑哨兵模式。Redis从2.8开始正式提供了Sentinel(哨兵)架构来解决这个问题。

哨兵模式是一种特殊的模式，首先Redis提供了哨兵的命令，哨兵是一个独立的进程，作为进程，它会独立运行。其原理是哨兵通过发送命令。等待Redis服务器响应，从而监控多个Redis实例。

单机哨兵：哨兵只有一个

当哨兵有多个时：

假设服务器宕机，哨兵1先检测到这个结果，系统并不会马上进行failover过程，仅仅是哨兵1主观的认为主服务器不可用，这个现象称为  主观下线 ，当后面的哨兵也检测到主服务器不可用，并且达到一定值时，那么哨兵之间会进行一次投票，投票的结果由一个哨兵发起，进行failover【故障转移】操作。切换成功后，就会通过发布订阅模式。让各个哨兵把自己监控的从服务器实现切换主机，这个过程称为客观下线。

### 简单配置

1、哨兵配置文件sentinel.conf

```
sentinel monitor mymaster 127.0.0.1 6379 1
sentinel auth-pass mymaster Ph0716


#后台运行
#daemonize yes
#日志记录
#logfile "/var/log/sentinel_log.log"
```

2、启动哨兵

```
 ./redis-sentinel ../myconf/sentinal.conf 
```

### 优点

1、哨兵集群，基于主从复制模式

2、主从可以切换，故障可以转移。

3、哨兵模式就是主从模式的升级版

### 缺点

1、添加哨兵后，Redis在线扩容相对来说比较麻烦。

2、实现哨兵模式的配置其实是很麻烦的，有较多限制

### 详细配置

```
# 哨兵sentinel实例运行的端口，默认26379  
port 26379
# 哨兵sentinel的工作目录
dir ./

# 哨兵sentinel监控的redis主节点的 
## ip：主机ip地址
## port：哨兵端口号
## master-name：可以自己命名的主节点名字（只能由字母A-z、数字0-9 、这三个字符".-_"组成。）
## quorum：当这些quorum个数sentinel哨兵认为master主节点失联 那么这时 客观上认为主节点失联了  
# sentinel monitor <master-name> <ip> <redis-port> <quorum>  
sentinel monitor mymaster 127.0.0.1 6379 2

# 当在Redis实例中开启了requirepass <foobared>，所有连接Redis实例的客户端都要提供密码。
# sentinel auth-pass <master-name> <password>  
sentinel auth-pass mymaster 123456  

# 指定主节点应答哨兵sentinel的最大时间间隔，超过这个时间，哨兵主观上认为主节点下线，默认30秒  
# sentinel down-after-milliseconds <master-name> <milliseconds>
sentinel down-after-milliseconds mymaster 30000  

# 指定了在发生failover主备切换时，最多可以有多少个slave同时对新的master进行同步。这个数字越小，完成failover所需的时间就越长；反之，但是如果这个数字越大，就意味着越多的slave因为replication而不可用。可以通过将这个值设为1，来保证每次只有一个slave，处于不能处理命令请求的状态。
# sentinel parallel-syncs <master-name> <numslaves>
sentinel parallel-syncs mymaster 1  

# 故障转移的超时时间failover-timeout，默认三分钟，可以用在以下这些方面：
## 1. 同一个sentinel对同一个master两次failover之间的间隔时间。  
## 2. 当一个slave从一个错误的master那里同步数据时开始，直到slave被纠正为从正确的master那里同步数据时结束。  
## 3. 当想要取消一个正在进行的failover时所需要的时间。
## 4.当进行failover时，配置所有slaves指向新的master所需的最大时间。不过，即使过了这个超时，slaves依然会被正确配置为指向master，但是就不按parallel-syncs所配置的规则来同步数据了
# sentinel failover-timeout <master-name> <milliseconds>  
sentinel failover-timeout mymaster 180000

# 当sentinel有任何警告级别的事件发生时（比如说redis实例的主观失效和客观失效等等），将会去调用这个脚本。一个脚本的最大执行时间为60s，如果超过这个时间，脚本将会被一个SIGKILL信号终止，之后重新执行。
# 对于脚本的运行结果有以下规则：  
## 1. 若脚本执行后返回1，那么该脚本稍后将会被再次执行，重复次数目前默认为10。
## 2. 若脚本执行后返回2，或者比2更高的一个返回值，脚本将不会重复执行。  
## 3. 如果脚本在执行过程中由于收到系统中断信号被终止了，则同返回值为1时的行为相同。
# sentinel notification-script <master-name> <script-path>  
sentinel notification-script mymaster /var/redis/notify.sh

# 这个脚本应该是通用的，能被多次调用，不是针对性的。
# sentinel client-reconfig-script <master-name> <script-path>
sentinel client-reconfig-script mymaster /var/redis/reconfig.sh

```

# 七、缓存穿透和雪崩

## 1、缓存穿透

### 概念

当用户发起请求访问redis时，redis没有需要的内容，会直接访问持久层数据库（例如MySQL）。如果这样的请求特别多，会给持久层数据库带来很大的压力。

### 解决方案

- 布隆过滤器

是一种数据结构，对所有可能查询的参数以hash形式存储，在控制层进行校验，不符合则丢弃，从而避免了对底层存储系统的查询压力。

- 缓存空对象

用户查询失败后，会存一个空对象。

带来的问题：1、存储空间的浪费。2、可能会造成某些时间段持久层数据库与缓存不一致的问题。

## 2、缓存击穿

### 概念

指的是某个key值，正在被大规模的访问中，承载着高并发的压力。某个时间点，突然过期了，例如6秒过期，6.1秒重新缓存，那么这0.1秒之内所有的访问将会直接转到持久层数据库，会使持久层数据库的压力瞬间增大。

### 解决方案

- 设置热点数据永不过期
- 枷锁互斥

分布式锁：使用分布式锁，保证对于每个key同时只有线程去查询后端服务，其他线程没有获得分布式锁的权限。这种方式本质上是将压力转移到分布式锁上。

## 3、缓存雪崩

### 概念

某个时间段，缓存集中过期失效。Redis宕机。

### 解决方案

- redis高可用

多设几台redis，异地多活

- 限流降级

缓存失效后，通过加锁或者列队来控制读数据库写缓存的线程数量。比如对某个key只允许一个线程查询数据和写缓存，其他线程等待。

- 数据预热

数据预热的含义就是在正式部署之前，把可能的数据先预先访问一遍，这样大部分可能大量访问的数据就会加载到缓存中。在即将发生大并发访问前手动触发加载缓存不同的key，设置不同的过期时间，让缓存失效的时间点尽量均匀。

