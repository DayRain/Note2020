一、简单实例

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <!--1、导包-->
    <script src="js/vue.js"></script>
</head>
<body>
<div id="app">
    <p id="content">{{msg}}</p>
</div>
<script>
    //2、创建一个Vue实例
    var vm = new Vue({
        el: '#app',  //提示，表示我们new的这个Vue实例，要控制页面上的哪个区域
        data:{//data属性中，存放的是 el中要用到的数据
            msg: '欢迎学习Vue' //通过Vue提供的指令，可以很方便的把数据渲染到页面上
        }
    });
</script>
</body>
</html>
```

# 二、基本指令学习

## 1、v-cloak   v-text  v-html     v-bind属性绑定  v-on事件绑定

```
<!DOCTYPE html>
<html lang="en" xmlns:v-bind="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <script src="js/vue.js"></script>
</head>
<style>
    [v-vloak]{
        display: none;
    }
</style>
<body>
<div id="app">
    <!--使用 v-cloak可以解决表达式闪烁的问题-->
    <p v-cloak>{{msg}}</p>
    <!-- v-text是没有闪烁问题的。
         v-text是会替换原来标签内的内容-->
    <p v-text = 'msg'></p>
    <p v-clock>{{msg2}}</p>
    <p v-text = 'msg2'></p>
    <!--上面两个会自动转义，下面的不会-->
    <p v-html = 'msg2'></p>

    <!--     v-bind属性绑定        -->
    <input type="button" value="按钮" v-bind:title="myTitle">
    <!--冒号是v-bind 的缩写-->
    <input type="button" value="按钮" :title="myTitle">

    <!--  用 v-on进行事件绑定   -->
    <input type="button" value="Hello" v-on:click="show">
    <input type="button" value="Hello" @click="show">
</div>
<script>
    var vue = new Vue({
        el: '#app',
        data: {
            msg:'cloak测试',
            msg2:'<h1>msg2</h1>',
            myTitle:'自定义Title'
        },
        methods: {
          show:function () {
              alert("hello")
          }  
        }
    })
</script>
</body>
</html>
```

## 2、滚动条实现

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <script src="js/vue.js"></script>
</head>
<body>
<div id="app">
    <p id="content" v-text="msg"></p>
    <input type="button" value="开始" v-on:click="startMsg"/>
    <input type="button" value="停止" v-on:click="stopMsg"/>
</div>

<script>
    var vue = new Vue({
        el: '#app',
        data: {
            msg: "今天天气真不错！",
            internalId: null
        },
        methods:{
            startMsg() {
                // var _this = this;
                // setInterval(function () {
                // //此时内部的this和外部不对应
                //     var start = _this.msg.substring(0,1);
                //     var end = _this.msg.substring(1);
                //     _this.msg = end+start;
                //     console.log(_this.msg);
                // },400)
                if(this.internalId != null) return;
                this.internalId=setInterval(() => {
                    var start = this.msg.substring(0,1);
                    var end = this.msg.substring(1);
                    this.msg = end+start;
                    console.log(this.msg);
                },400)
            },
            stopMsg(){
                clearInterval(this.internalId);
                this.internalId = null;
            }
        }
    })
</script>
</body>
</html>
```

## 3、事件修饰符

.stop 阻止冒泡

```
    <div class="divS" @click="outClick">
        <input type="button" value="按钮" @click.stop="innerClick"/>
    </div>
```

.prevent 阻止默认事件

```
<a href="http://www.baidu.com" @click.prevent="linkClick">百度</a>
```

.capture 添加事件监听器时，使用事件捕获模式

```
    <div class="divS" @click.capture="outClick">
        <input type="button" value="按钮" @click="innerClick"/>
    </div>
    
    先外部，后内部
```

.self 只当时间在该元素本身（比如不是子元素）触发时触发回调

```
    <div class="divS" @click.self="outClick">
        <input type="button" value="按钮" @click="innerClick"/>
    </div>
```

.once事件只触发一次

```
    <a href="http://www.baidu.com" @click.prevent.once="linkClick">百度</a>
    
    第一次点击，弹框
    第二次点击，跳到百度x
    
```

## 4、表单元素数据的双向绑定

```html
<input type="text" v-model="msg"/>	
```
通过v-model可以实现双向绑定，当输入框内修改时，会同步到msg。但是只能运用在表单元素内



