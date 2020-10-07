# 1、ES  VS Mysql

| Mysql    | ES       |
| -------- | -------- |
| database | index    |
| table    | type     |
| row      | document |
| column   | field    |
| schema   | mapping  |

# 2、安装测试

## 2.1 启动

windows直接点击bin下面的elasticsearch.bat

## 2.2 端口

9200

浏览器输入localhost:9200访问

## 2.3 笔记

https://www.yuque.com/gaohanghang/vx5cb2/aa576g#HuZ1N

# 3、POSTMAN交互

## 3.1 查看所有索引

GET:      localhost:9200/_all

```
{
    "test": {
        "aliases": {},
        "mappings": {},
        "settings": {
            "index": {
                "creation_date": "1595935547929",
                "number_of_shards": "1",
                "number_of_replicas": "1",
                "uuid": "RsFhzVEgTSG9LIykCik0Uw",
                "version": {
                    "created": "7050099"
                },
                "provided_name": "test"
            }
        }
    }
}
```

## 3.2 添加一个索引

PUT:     localhost:9200/test

```
{
    "acknowledged": true,
    "shards_acknowledged": true,
    "index": "test"
}
```

## 3.3 删除一个索引

DELETE：     localhost:9200/test2

```
{
    "acknowledged": true
}
```

## 3.4 创建索引 person

```
localhost:9200/person
```

## 3.5 新增数据

```
localhost:9200/person/_doc/1

{
    "first_name": "peng",
    "last_name": "hao",
    "age": 25,
    "about": "i love games",
    "interests" : "play games"
}

localhost:9200/person/_doc/2

{
    "first_name": "li",
    "last_name": "jin",
    "age": 24,
    "about": "i love games",
    "interests" : "play games"
}
```

## 3.6 搜索交互

```
localhost:9200/person/_doc/1

{
    "_index": "person",
    "_type": "_doc",
    "_id": "1",
    "_version": 1,
    "_seq_no": 0,
    "_primary_term": 1,
    "found": true,
    "_source": {
        "first_name": "peng",
        "last_name": "hao",
        "age": 25,
        "about": "i love games",
        "interests": "play games"
    }
}

localhost:9200/person/_doc/_search?q=first_name:peng

{
    "took": 47,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 1,
            "relation": "eq"
        },
        "max_score": 0.6931472,
        "hits": [
            {
                "_index": "person",
                "_type": "_doc",
                "_id": "1",
                "_score": 0.6931472,
                "_source": {
                    "first_name": "peng",
                    "last_name": "hao",
                    "age": 25,
                    "about": "i love games",
                    "interests": "play games"
                }
            }
        ]
    }
}
```

# 4、kibana交互

## 4.1 端口地址

http://localhost:5601

点击左列倒数第三个图标，dev-tools



## 4.2 条件查询

should 类似sql里的 or

```
POST /person/_search
{
  "query":{
    "bool":{
      "should": [
        {"match": {
          "last_name": "jin"
        }},
        {"match": {
          "age": "18"
        }}
      ]
    }
  }
}
```

must类似 and

```

```

