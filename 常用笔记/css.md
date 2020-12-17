# 外部引入

css外部引入：

```
 <link rel="stylesheet" href = "./css/text.css">
```

# 技巧

## 1、单行文字垂直居中

```
<html>
    <head>

        <style>
            div {
                height: 50px;
                width: 200px;
                background-color: pink;
                line-height: 40px;
            }
        </style>
    </head>

    <body>
        <div> hello </div>
    </body>
</html>
```

## 2、设置大背景图片

```
            body {
                background-image: url('./img/bg.png'); 
                background-position: center top;
                background-repeat: no-repeat;
            }
```

## 3、动态行高

```
<html>
    <head>
        
        <style>
            body {
                font: 14px/1.5 'Microsoft YaHei'
            }

            div{
                font-size: 20px;
            }
        </style>
    </head>

    <body>
        hello
        <div>
            hello
        </div>
    </body>
</html>
```

## 3、padding撑大容器

padding会撑大容器，

如果想它不撑大，就不要加width：100% 或者width：100px之类的

## 4、div居中

```
<html>
    <head>

        <style>
            div {
                width: 300px;
                height: 50px;
                background-color: brown;
                margin: 0 auto;
            }
        </style>
    </head>


    <body>
        <div>
        
        </div>
    </body>
</html>
```

## 5、行内元素居中

```
          text-align: center;
```

## 6、清除内外边距

有的元素之间会自带内外边距，为了风格统一，我们可以指定一下

```
  * {
      margin:0;
      top:0;
  }
```

## 7、绝对定位的盒子居中

```
<html>
    <head>

        <style>
            #body {
                width: 200px;
                height: 50px;
                background-color: blue;
                position: absolute;
                left: 50%;
                margin-left: -100px;
                top: 50%;
                margin-top: -25px;


                /*                 
                margin: auto;
                加上绝对定位后，再用margin:auto就无效了
                */

            }
        </style>
    </head>


    <body>
        <div id="body">

        </div>
    </body>
</html>
```

## 8、readonly和disabled区别

disabled用户不可输入，也不会向后台传值

readonly用户也不可输入吗，但是可以有焦点，并且可以向后台传值。

# 选择器

## 标签选择器
 div
单个标签表示选中所有的同名标签，例如：

将所有的div标签中文字的颜色改成aqua
``` css
        <style>
            div {
                color: aqua;
            }
        </style>
```

## 类选择器

## id选择器

## 通配符选择器

``` css
        <style>
            * {
                color: aqua;
            }
        </style>
```

## 复合选择器

### 后代选择器

后代选择器条件相对比较宽松，div p之间可以隔着很多个，div前面可以有很多个元素，p后面也可以有很多个元素。主要答题满足这个顺序的p就会被选中

div里面的p

```
div p
```

### 子元素选择器

两个选择器必须是紧紧挨在一起的，不可以有间隔

```
            div>p{
                color: aqua;
            }
```

### 并集选择器

任何选择器都可以作为并集选择器，表示两个选择器选定的内容的汇总

例如

```
span, p
div, div>p
span, div p
```



# 文本样式

## text-align
表示位置
text-align: center/right/lef

如下demo实际上是让文字在h1这个块级元素中居中

``` css
<html>
    <head>
        
        <style>
            h1 {
                text-align: center;
            }
        </style>
    </head>

    <body>
        <h1>hello,world</h1>
    </body>
</html>
```

## text-decoration
表示文本的一些装饰

取值如下

none 不需要式样

underline 下划线

overline 上划线

line-through 删除线

```css
text-decoration: underline;
```



## text-indent

首行缩进

```
            p {
                /* text-indent: 10px; */
                text-indent: 2em;
            }
```

## line-height

表示行高

```

            p {
                /* text-indent: 10px; */
                text-indent: 2em;
                line-height: 16px;
            }
```

# 元素显示模式

## 分类

行内元素：span， a

块级元素：div，h1，p，ul，ol，li

行内块元素：img、input、td

## 特点

行内元素：

宽高默认是本身，且不能指定

一行可以放多个

块级元素：

可以指定宽高

一行只能放一个



行内块元素：

一行可以放多个，但是会有空白间隙

有默认宽高，且是本身，但是也可以指定

## 注意点

p、h1等元素的标签里面不能放块元素，比如div

a里面不能再放a，a特殊情况下可以放块级元素，但最好给他转换一下

## 转换

display:block

display:inline



# css三大特性

## 层叠性

后面的样式会覆盖前面的样式

```
div{
color:red
}

div{
color:black
}
```



## 继承性

子标签会继承父标签的大小

## 优先级



# 盒子模型

## 边框

 border-style: solid;没有的话，是不出边框的

```

        <style>
            div {
                width: 100px;
                height: 20px;
                border-width: 2px;
                border-color: black;
                border-style: solid;
            }
        </style>
```

边框合并

靠在一起的边框可以合并

```
boder-collaspe
```

## 注意点

1、边框会影响实际大小，测量的时候不要量边框

2、padding也会影响实际大小，如果已有宽度和高度，padding会撑大盒子

# 塌陷问题

父元素有margin：top

子元素也有margin：top

那么子元素会粘着父元素，不会产生相对距离



## 浮动

添加浮动的块元素，或脱离标准流

### 清除浮动

1、额外标签

```
clear:both
```

2、父元素添加

overflow：hidden/auto/scroll

# 定位

属性：

top、left、bottom、right

定位 = 定位模式 + 边偏移

## 静态定位

不脱标

选择器{postition:static;}



## 相对定位

不脱标

选择器{position: relative;}

相对定位是相对于它自身原来的位置

添加相对定位后，它不会脱离标准流，原来的位置依然占有

## 绝对定位

脱标

绝对定位的参照物是他的祖先元素，如果没有祖先元素，则以浏览器为定位。

如果祖先元素有定位，则以最近的一个祖先元素为参照物。也就是说，父盒子必须有定位，才能约束使用绝对定位的子盒子

决定定位会脱标，不影响原来的元素。

## 固定定位

脱标

浏览器滚动的时候，这个组件不会动

```
选择器{position:fixed;}
```

会脱标

## 子绝父相

子级如果使用绝对定位，父级使用相对定位

## 定位的叠放顺序

z-index：1

数值越大，越靠上

## 定位带来的影响

1、

行内元素添加绝对定位和固定定位也和浮动类似。可以直接设置宽高

块级元素加了绝对定位和固定定位后，如果不指定大小，则默认大小是里面元素的大小。

2、

绝对定位会完全压住盒子（压住标准流的东西）

浮动元素只会压盒子，不会压里面的东西，会把文字这些挤出去