# 一、概述

## 1、参考

笔记基于：

https://www.bilibili.com/video/BV1xE411d7hY?from=search&seid=6779503161160282525

## 2、SOAP

Simple Object Acess Protocal 简单对象访问协议

## 3、WSDL

Web Service Definition Language web服务器描述语言与结构分析

## 4、UDDI

Universal Description Discovery Integration 统一描述、发现和集成

## 5、CXF

## 6、参考图

![image-20200901124134641](https://raw.githubusercontent.com/DayRain/picGo_image/master/img/image-20200901124134641.png)







# 二、测试demo

## 1、服务端代码

### 1.1  依赖

springBoot版本

```
<version>2.3.3.RELEASE</version>
```

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web-services</artifactId>
        </dependency>

        <!-- CXF webservice -->
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
            <version>3.2.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-transports-http</artifactId>
            <version>3.2.1</version>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
```

### 1.2  pojo

```
public class FileDao {

    private String fileId;

    private String fileName;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

```

### 1.3  接口

```
@WebService(name = "FileService", targetNamespace = "http://file.webservice.zkkj.com")
public interface FileService {

    @WebMethod
    List<FileDao> getFileList(@WebParam String userId);
}

```

### 1.4 接口实现

```
@Service
@WebService(name = "FileService",    // 与接口中的name相同

        targetNamespace = "http://file.webservice.zkkj.com",  // 一般为当前包名的倒序

        endpointInterface = "com.zjjk.webservice.service.FileService"  // 为接口类的包名

)
public class FileServiceImpl implements FileService {
    @Override
    public List<FileDao> getFileList(String data) {
        FileDao fileDao = new FileDao();
        fileDao.setFileId("010203040506");
        fileDao.setFileName("档案一");
        return Collections.singletonList(fileDao);
    }
}
```

### 1.5  配置文件

```
cxf.path=/zkkj
```

### 1.6 配置类

```
@Configuration
public class WebServiceConfig {

    @Autowired
    FileService fileService;

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    /**
     * 此方法作用是改变项目中服务名的前缀名，此处127.0.0.1或者localhost不能访问时，请使用ipconfig查看本机ip来访问
     * 此方法被注释后, 即不改变前缀名(默认是services), wsdl访问地址为 http://127.0.0.1:8080/services/ws/api?wsdl
     * 去掉注释后wsdl访问地址为：http://127.0.0.1:8080/soap/ws/api?wsdl
     * http://127.0.0.1:8080/soap/列出服务列表 或 http://127.0.0.1:8080/soap/ws/api?wsdl 查看实际的服务
     * 新建Servlet记得需要在启动类添加注解：@ServletComponentScan
     *
     * 如果启动时出现错误：not loaded because DispatcherServlet Registration found non dispatcher servlet dispatcherServlet
     * 可能是springboot与cfx版本不兼容。
     * 同时在spring boot2.0.6之后的版本与xcf集成，不需要在定义以下方法，直接在application.properties配置文件中添加：
     * cxf.path=/service（默认是services）
     */
    //@Bean
    //public ServletRegistrationBean dispatcherServlet() {
    //    return new ServletRegistrationBean(new CXFServlet(), "/soap/*");
    //}

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), fileService);
        endpoint.publish("/file/api");
        return endpoint;
    }
}
```



## 2、客户端代码

### 2.1 自动生成

需要借助cxf包下的工具  wsdl2java

```
.\wsdl2java -encoding utf8  http://localhost:8080/zkkj/file/api?wsdl
```

### 2.2 FileDao

```
/**
 * &lt;p&gt;fileDao complex type的 Java 类。
 * 
 * &lt;p&gt;以下模式片段指定包含在此类中的预期内容。
 * 
 * &lt;pre&gt;
 * &amp;lt;complexType name="fileDao"&amp;gt;
 *   &amp;lt;complexContent&amp;gt;
 *     &amp;lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&amp;gt;
 *       &amp;lt;sequence&amp;gt;
 *         &amp;lt;element name="fileId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *         &amp;lt;element name="fileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&amp;gt;
 *       &amp;lt;/sequence&amp;gt;
 *     &amp;lt;/restriction&amp;gt;
 *   &amp;lt;/complexContent&amp;gt;
 * &amp;lt;/complexType&amp;gt;
 * &lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileDao", propOrder = {
    "fileId",
    "fileName"
})
public class FileDao {

    protected String fileId;
    protected String fileName;

    /**
     * 获取fileId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * 设置fileId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileId(String value) {
        this.fileId = value;
    }

    /**
     * 获取fileName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置fileName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }


    @Override
    public String toString() {
        return "FileDao{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
```

### 2.3  FileService

```
/**
 * This class was generated by Apache CXF 3.4.0
 * 2020-09-02T09:41:37.493+08:00
 * Generated source version: 3.4.0
 *
 */
@WebService(targetNamespace = "http://file.webservice.zkkj.com", name = "FileService")
@XmlSeeAlso({ObjectFactory.class})
public interface FileService {

    @WebMethod
    @RequestWrapper(localName = "getFileList", targetNamespace = "http://file.webservice.zkkj.com", className = "com.zjjk.client.service.GetFileList")
    @ResponseWrapper(localName = "getFileListResponse", targetNamespace = "http://file.webservice.zkkj.com", className = "com.zjjk.client.service.GetFileListResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<FileDao> getFileList(

        @WebParam(name = "arg0", targetNamespace = "")
        String arg0
    );
}
```

### 2.4 FileServiceImplService

```
/**
 * This class was generated by Apache CXF 3.4.0
 * 2020-09-02T09:41:37.531+08:00
 * Generated source version: 3.4.0
 *
 */
@WebServiceClient(name = "FileServiceImplService",
                  wsdlLocation = "http://localhost:8080/zkkj/file/api?wsdl",
                  targetNamespace = "http://file.webservice.zkkj.com")
public class FileServiceImplService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://file.webservice.zkkj.com", "FileServiceImplService");
    public final static QName FileServicePort = new QName("http://file.webservice.zkkj.com", "FileServicePort");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/zkkj/file/api?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(FileServiceImplService.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "http://localhost:8080/zkkj/file/api?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public FileServiceImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public FileServiceImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public FileServiceImplService() {
        super(WSDL_LOCATION, SERVICE);
    }

    public FileServiceImplService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public FileServiceImplService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public FileServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }


    /**
     *
     * @return
     *     returns FileService
     */
    @WebEndpoint(name = "FileServicePort")
    public FileService getFileServicePort() {
        return super.getPort(FileServicePort, FileService.class);
    }

    /**
     *
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns FileService
     */
    @WebEndpoint(name = "FileServicePort")
    public FileService getFileServicePort(WebServiceFeature... features) {
        return super.getPort(FileServicePort, FileService.class, features);
    }

}
```

### 2.5 测试

```
    @Test
    void contextLoads() {

        List<FileDao> xiaoming1 = new FileServiceImplService().getFileServicePort().getFileList("xiaoming");

        System.out.println(xiaoming1);
    }

```

# 三、wsdl含义

```
<wsdl:definitions xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:tns="http://file.webservice.zkkj.com" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:ns1="http://schemas.xmlsoap.org/soap/http" name="FileServiceImplService" targetNamespace="http://file.webservice.zkkj.com">
<wsdl:types>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tns="http://file.webservice.zkkj.com" elementFormDefault="unqualified" targetNamespace="http://file.webservice.zkkj.com" version="1.0">
<xs:element name="getFileList" type="tns:getFileList"/>
<xs:element name="getFileListResponse" type="tns:getFileListResponse"/>
<xs:complexType name="getFileList">
<xs:sequence>
<xs:element minOccurs="0" name="arg0" type="xs:string"/>
</xs:sequence>
</xs:complexType>
<xs:complexType name="getFileListResponse">
<xs:sequence>
<xs:element maxOccurs="unbounded" minOccurs="0" name="return" type="tns:fileDao"/>
</xs:sequence>
</xs:complexType>
<xs:complexType name="fileDao">
<xs:sequence>
<xs:element minOccurs="0" name="fileId" type="xs:string"/>
<xs:element minOccurs="0" name="fileName" type="xs:string"/>
</xs:sequence>
</xs:complexType>
</xs:schema>
</wsdl:types>
<wsdl:message name="getFileListResponse">
<wsdl:part element="tns:getFileListResponse" name="parameters"> </wsdl:part>
</wsdl:message>
<wsdl:message name="getFileList">
<wsdl:part element="tns:getFileList" name="parameters"> </wsdl:part>
</wsdl:message>
<wsdl:portType name="FileService">
<wsdl:operation name="getFileList">
<wsdl:input message="tns:getFileList" name="getFileList"> </wsdl:input>
<wsdl:output message="tns:getFileListResponse" name="getFileListResponse"> </wsdl:output>
</wsdl:operation>
</wsdl:portType>
<wsdl:binding name="FileServiceImplServiceSoapBinding" type="tns:FileService">
<soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
<wsdl:operation name="getFileList">
<soap:operation soapAction="" style="document"/>
<wsdl:input name="getFileList">
<soap:body use="literal"/>
</wsdl:input>
<wsdl:output name="getFileListResponse">
<soap:body use="literal"/>
</wsdl:output>
</wsdl:operation>
</wsdl:binding>
<wsdl:service name="FileServiceImplService">
<wsdl:port binding="tns:FileServiceImplServiceSoapBinding" name="FileServicePort">
<soap:address location="http://localhost:8080/zkkj/file/api"/>
</wsdl:port>
</wsdl:service>
</wsdl:definitions>
```

## 1、标签描述

### 1.1   wsdl:definitions

命名空间，根元素

### 1.2 wsdl:types

webservice 使用的数据类型

### 1.3 wsdl:message

webservice 使用的消息，每个消息由一个或者多个部件组成。可以把它当作java中的一个函数调用的参数。

### 1.4 wsdl:portType

webservice 执行的操作，类似Java的一个函数库（或一个模块、或一个类）

### 1.5 wsdl:binding

使用的通信协议、为每个端口定义消息割舍和协议细节

### 1.6 wsdl:service

对外暴露

# 四、异常总结

## 1、 未定义的元素

在使用天气预报的webservice出现如下错误

```
WSDLToJava Error: http://ws.webxml.com.cn/WebServices/WeatherWS.asmx?wsdl [15,19]: undefined element declaration 's:schema'
http://ws.webxml.com.cn/WebServices/WeatherWS.asmx?wsdl [61,19]: undefined element declaration 's:schema'
http://ws.webxml.com.cn/WebServices/WeatherWS.asmx?wsdl [101,13]: undefined element declaration 's:schema'

```

原因是C#生成的wsdl和java的略有区别，删去报错的东西即可。

## 2、 Failed to generate types.

报错如下：

```
WSDLToJava Error: Failed to generate types.
```

网上一般出现该错误还有其他原因，但我这个仅仅提示“ Failed to generate types”

因为我在执行wsdl2java的时候，指定了编码

```
 .\wsdl2java -encoding utff8 C:\Users\13760\Desktop\WeatherWS.xml
```

尝试去掉编码后

```
 .\wsdl2java C:\Users\13760\Desktop\WeatherWS.xml
```

