# 1、安装

## 依赖

```
 <!--Activity-->
        <dependency>
            <groupId>org.activiti</groupId>
            <artifactId>activiti-spring-boot-starter</artifactId>
            <version>7.1.0.M5</version>
        </dependency>
        <dependency>
            <groupId>org.activiti.dependencies</groupId>
            <artifactId>activiti-dependencies</artifactId>
            <version>7.1.0.M5</version>
            <type>pom</type>
        </dependency>
```

## 插件

去idea官网下载

actibpm.jar

## 配置文件

```
spring:
  datasource:
    username: root
    password: Ph0716
    url: jdbc:mysql://175.24.15.179:3306/activiti7?useUnicode=true&characterEncoding=utf-8
    driver-class-name: com.mysql.cj.jdbc.Driver
  activiti:
    history-level: full
    db-history-used: true
    check-process-definitions: false
```

# 2、基础

### 部署

## 涉及数据库表

ACT_RE_DEPLOYMENT

ACT_GE_BYTEARRAY

## 流程部署与查询

```
    @Test
    public void initDeploymentBPMN() {
        String filename = "BPMN/Part1_Deployment.bpmn";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(filename)  //部署bpmn
                .name("流程部署测试").deploy();
        System.out.println(deployment.getName());
    }

    @Test
    public void selectDeploymentBPMN() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : list) {
            System.out.println(deployment.getId());
            System.out.println(deployment.getName());
            System.out.println(deployment.getDeploymentTime());
            System.out.println(deployment.getKey());
            System.out.println("________________________________");
        }
    }
```

## 流程定义Process

Deployment：添加资源文件、获取部署信息、部署时间

v：获取版本号、key、资源名称、部署ID



流程定义涉及表：

ACT_RE_PROCDEF



Deployment和ProcessDefinition通过外键相联，ProcessDefinition是Deployment的一个补充。

获取流程定义

```
    @Test
    public void getDefinitions() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : list) {
            System.out.println(processDefinition.getId());
            System.out.println(processDefinition.getCategory());
            System.out.println(processDefinition.getDeploymentId());
            System.out.println(processDefinition.getDiagramResourceName());
            System.out.println(processDefinition.getVersion());
            System.out.println(processDefinition.getName());
            System.out.println(processDefinition.getKey());
        }
    }

```

删除流程

```
 @Test
    public void delDefinition() {
        //这边的id指的是deploymentId
        String pdID = "fce0b19f-2a56-11eb-9730-a4b1c1134224";
        repositoryService.deleteDeployment(pdID, true); //第二个参数表示是否保留历史

    }
```

## 流程实例化

设计表

ACT_RU_EXECUTION



```
 @SpringBootTest
public class Part3ProcessInstance {
    @Autowired
    RuntimeService runtimeService;

    //初始化流程实例
    @Test
    public void initProcessInstance() {
        //1、获取表单填报内容，请假时间、请假事由，String fromData
        //2、formData写入业务表，返回业务表主键ID==businessKey
        //3、把业务数据与Activit7流程数据相关联（第二个参数）
        //拓展：如果不用这个businesskey，也可以将自己的表与processInstance的主键相关联
        //第一个参数对应的是process的key
        //启动后，execution表会有两条数据，因为一个是start节点，一个是end节点
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess_Part1","bKey001");
        System.out.println("实例id" + processInstance.getProcessDefinitionId());
    }

    //获取流程实例
    @Test
    public void getProcessInstance() {
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().list();
        for (ProcessInstance processInstance : processInstances) {
            System.out.println("id:"+ processInstance.getId());
            System.out.println("所属流程定义的id:"+ processInstance.getProcessDefinitionId());
            System.out.println("是否结束:"+ processInstance.isEnded());
            System.out.println("是否被挂起:"+ processInstance.isSuspended());
        }
    }

    //暂停与激活流程实例
    @Test
    public void activitieProcessInstances() {
        //挂起
        // runtimeService.suspendProcessInstanceById("068eedfb-2a5c-11eb-b891-a4b1c1134224");

        //激活
        runtimeService.activateProcessInstanceById("068eedfb-2a5c-11eb-b891-a4b1c1134224");
    }

    //删除流程实例
    @Test
    public void deleteProcessInstances() {
        runtimeService.deleteProcessInstance("068eedfb-2a5c-11eb-b891-a4b1c1134224", "删除的理由");
    }
}


```

## 任务处理

Assignaee：执行人、代理人

Candidate Users：候选人

Candidate Groups：候选组

Due Date：任务到期时间

```
@SpringBootTest
public class Part4Task {

    @Autowired
    TaskService taskService;

    //ACT_RU_TASK
    //ACT_RU_IDENTITYLINK

    //任务查询
    @Test
    public void  getTasks() {
        List<Task> list = taskService.createTaskQuery().list();
        for (Task task : list) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getAssignee());
        }
    }

    //查询某人的代办任务
    @Test
    public void getTasksByAssignee() {
        //如果一个任务完成后，这边是查不到的。
        //这边查到的只是目前应该完成的任务。
        //执行完就删掉，将任务放到了历史记录中，这是为了效率
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("wukong").list();
        for (Task task : list) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getAssignee());
        }
    }

    //执行任务
    @Test
    public void completeTask() {
        taskService.complete("13d5b94c-2a61-11eb-936c-a4b1c1134224");
        System.out.println("完成任务");
    }

    //拾取任务
    @Test
    public void claimTask() {
        List<Task> list = taskService.createTaskQuery().taskId("").list();
        // 拾取任务，第一个任务id，第二个用户名
        taskService.claim("","bajie");

    }

    //归还任务、交办任务
    @Test
    public void setTaskAssignee() {
        List<Task> list = taskService.createTaskQuery().taskId("").list();
        // 拾取任务，第一个任务id，第二个归还候选任务
        taskService.setAssignee("","null");
        //交办
        taskService.setAssignee("","wukong");

    }


}
```

## 获取历史记录

```
@SpringBootTest
public class Part5HistoryTaskInstance {
    @Autowired
    private HistoryService historyService;

    //根据用户名查找历史记录
    @Test
    public void HistoryTaskInstanceByUser() {
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc()
                .taskAssignee("bajie")
                .list();
    }

    //根据流程实例id来查询历史
    @Test
    public void HistoryTaskInstanceByPID() {
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc()
                .processInstanceId("")
                .list();
    }


}
```



## 运行参数表

涉及数据库：

act_ru_variable  运行时参数表

act_hi_varinst  历史参数表

## UEL表达式

```
@SpringBootTest
public class Part7UEL {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    //运算符表达式的赋值 场合：
    //流程开始的时候、任务的完成环节、代码赋值 。

    @Test
    public void initProcessInstanceWithArgs() {

        //流程变量      ${Zhixingren} 变量是流程定义环节指定的，mbpn
        Map<String, Object> variables = new HashMap<>();
        variables.put("Zhixingren", "大师兄");
        //可以传多个参数
        // variables.put("Zhixingre2", "大师兄");
        // variables.put("Zhixingre3", "大师兄");
        // variables.put("Zhixingre4", "大师兄");


        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("myProcess_Task", "bKey001");
        System.out.println("实例id" + processInstance.getProcessDefinitionId());
    }

    //完成任务带参数

    /**
     *在定义的时候，比如网关判断的时候，可以用表达式 ${pay > 100}
     */
    @Test
    public void completeTaskWithArgs() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("pay", "101");
        taskService.complete("13d5b94c-2a61-11eb-936c-a4b1c1134224", null);
    }

    private class UEL_POJO implements Serializable {
        private String zhixinren;
        private String pay;

        public String getZhixinren() {
            return zhixinren;
        }

        public void setZhixinren(String zhixinren) {
            this.zhixinren = zhixinren;
        }

        public String getPay() {
            return pay;
        }

        public void setPay(String pay) {
            this.pay = pay;
        }
    }

    //使用流程实例带参数，使用实体类
    @Test
    public void completeTaskWithClass() {

    }

    //启动流程实例带参数，指定多个实体类
    @Test
    public void initProcessInstanceWithClassArgs() {
        UEL_POJO uel_pojo = new UEL_POJO();
        uel_pojo.setZhixinren("bajie");

        //流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("uelpojo", uel_pojo);


        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("myProcess_Task", "bKey001");
        System.out.println("实例id" + processInstance.getProcessDefinitionId());
    }

    //启动流程实例参数吗，指定多个候选人
    @Test
    public void initProcessInstanceWithCandiDateArgs() {
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("houxuanren", "wukong.tangsen");
        taskService.complete("", variables);

    }


    //直接指定流程变量
    @Test
    public void otherArgs() {
        runtimeService.setVariable("", "pay", "101");
        // runtimeService.setVariables();
        // taskService.setVariable();
        // taskService.setVariables();
    }

    //局部变量
    @Test
    public void otherLocalArgs() {
        runtimeService.setVariableLocal("", "pay", "101");
        // runtimeService.setVariablesLocal();
        // taskService.setVariableLocal();
        // taskService.setVariablesLocal();
    }



}

```

## Runtime

```
@SpringBootTest
public class Part8ProcessRuntime {

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private SecurityUtil securityUtil;


    //获取流程实例
    @Test
    public void getProcessInstance() {
        securityUtil.logInAs("bajie");
        Page<ProcessInstance> processInstancePage = processRuntime.processInstances(Pageable.of(0, 100));
        System.out.println("流程实例数量：" + processInstancePage.getTotalItems());

        List<ProcessInstance> list = processInstancePage.getContent();
        for (ProcessInstance processInstance : list) {
            System.out.println(processInstance.getId());
            System.out.println(processInstance.getName());
            System.out.println(processInstance.getStartDate());
            System.out.println(processInstance.getStatus());
            System.out.println(processInstance.getProcessDefinitionId());
            System.out.println(processInstance.getProcessDefinitionKey());
        }
    }

    //启动流程实例
    @Test
    public void startProcessInstance() {
        securityUtil.logInAs("bajie");
        processRuntime.start(ProcessPayloadBuilder
        .start()
        .withProcessDefinitionKey("defKey")
        .withName("")
        .withBusinessKey("")
        .build());
    }


    //删除流程实例
    @Test
    public void delProcessInstance() {
        securityUtil.logInAs("bajie");
        processRuntime.delete(ProcessPayloadBuilder
        .delete()
        .withProcessInstanceId("")
        .build());
    }

    //挂起
    @Test
    public void suspendProcessInstance() {
        securityUtil.logInAs("bajie");
        ProcessInstance processInstance = processRuntime.suspend(ProcessPayloadBuilder
                .suspend()
                .withProcessInstanceId("")
                .build());
    }

    //激活
    @Test
    public void resumeProcessInstance() {
        securityUtil.logInAs("bajie");
        ProcessInstance processInstance = processRuntime.resume(ProcessPayloadBuilder
                .resume()
                .withProcessInstanceId("")
                .build());
    }

    //流程参数
    @Test
    public void getVariables() {
        securityUtil.logInAs("bajie");
        List<VariableInstance> variableInstances = processRuntime.variables(ProcessPayloadBuilder
                .variables()
                .withProcessInstanceId("")
                .build());
    }

}

```





```
@SpringBootTest
public class Part9TaskRuntime {

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TaskRuntime taskRuntime;

    //获取当前登录用户任务
    @Test
    public void getTasks() {
        securityUtil.logInAs("bajie");
        //登陆后，task就添加了当前用户作为候选人条件
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100));
        List<Task>list = tasks.getContent();
        for (Task task : list) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getStatus());
            System.out.println(task.getCreatedDate());
            if(task.getAssignee() == null) {
                //候选人为当前用户，null的时候需要前端拾取
                System.out.println("待拾取");
            }
            else {
                System.out.println("Assignee:" + task.getAssignee());
            }
        }
    }

    //完成任务
    @Test
    public void completeTask() {
        securityUtil.logInAs("bajie");
        Task task = taskRuntime.task("");
        if(task.getAssignee() == null) {
            taskRuntime.claim(TaskPayloadBuilder.claim()
            .withTaskId(task.getId())
            .build());
        }

        taskRuntime.complete(TaskPayloadBuilder.complete()
        .withTaskId(task.getId())
        .build());
        System.out.println("任务执行完成");
    }
}
```

# 3、整合SpringBoot

## 1、环境

## 2、注意点

1、删除“部署”，也会同时删除process

2、图形界面定义流程的时候，填写的编号就是实例定义的key

3、发布新的流程后，需要重启服务

```
 ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("WareHousing2");
```

因为如果这样启动实例的话，用的是缓存

```
public ProcessInstance execute(CommandContext commandContext) {
    DeploymentManager deploymentCache = commandContext.getProcessEngineConfiguration().getDeploymentManager();

    // Find the process definition
    ProcessDefinition processDefinition = null;
    if (processDefinitionId != null) {

      processDefinition = deploymentCache.findDeployedProcessDefinitionById(processDefinitionId);
      if (processDefinition == null) {
        throw new ActivitiObjectNotFoundException("No process definition found for id = '" + processDefinitionId + "'", ProcessDefinition.class);
      }

    } else if (processDefinitionKey != null && (tenantId == null || ProcessEngineConfiguration.NO_TENANT_ID.equals(tenantId))) {

      processDefinition = deploymentCache.findDeployedLatestProcessDefinitionByKey(processDefinitionKey);
      if (processDefinition == null) {
        throw new ActivitiObjectNotFoundException("No process definition found for key '" + processDefinitionKey + "'", ProcessDefinition.class);
      }

```



## 3、TaskService、RuntimeService区别

```
public RestResult getCurrentUserTask(){
        System.out.println("__________________________TaskRuntime____________________");
        //默认当前用户的信息
        Page<org.activiti.api.task.model.Task> tasks = taskRuntime.tasks(Pageable.of(0, 100));
        for (org.activiti.api.task.model.Task task : tasks.getContent()) {
            System.out.println(task.getName());
            System.out.println(task.getAssignee());
        }

        System.out.println("----------------------------TaskService----------------------");

        //所有用户的信息
        for (Task task : taskService.createTaskQuery().list()) {
            System.out.println(task.getName());
            System.out.println(task.getAssignee());
            System.out.println(task.getId());
        }
        return RestResult.success();
    }
```

