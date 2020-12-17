# 一、语法

## 1、Function

js中的函数比较特殊，与其他语言比较不同。

所有的函数都是Function的实例。

### 1.1函数定义

第一种

```
function cal(num1){
    console.log(num1);
}
```

第二种

```
let cal = function(num1){
    console.log(num1);
}
```

第三种（不推荐）

开销大

```
cal = new Function("num1", "console.log(num1)")
```

### 1.2 函数中没有重载

后面的方法覆盖前面的方法

可以从指针的角度来理解

```
function sum(num1, num2) {
    return num1 + num2;
}

function sum(num1, num2) {
    return num1 + num2 + 10;
}

alert(sum(1,2))
```

但如果是这种写法，则会报错：变量已经存在

```

function sum(num1, num2) {
    return num1 + num2;
}

let sum = function(num1, num2) {
    return num1 + num2 + 10;
}

alert(sum(1,2))
```

## 2、对象

### 2.1 入门

```
let person = {
    name: "admin",
    age: 29,

    sayName: function() {
        alert(this.name)
    }
}

person.sayName();
```

### 2.2 创建对象

1、 工厂模式

```
function person(name, age) {
    let obj = new Object();
    obj.name = name;
    obj.age = age;
    obj.sayName = function() {
        alert(name)
    }
}
```

2、构造函数

```
function Person(name, age) {
    this.name = name;
    this.age = age;
    this.sayName = function(){
        alert(this.name);
    }
}

let per = new Person("admin", 18)
per.sayName();
```

