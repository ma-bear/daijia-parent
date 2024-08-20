# 项目总结

## 1 项目功能

* 项目包含：**乘客端、司机端**

* 乘客端：

登录--选择代驾地址--呼叫代驾--等待接单--15分钟没有司机接单自动取消--15内有司机接单，司乘同显--账单支付

* 司机端

登录--认证--开始接单--抢单--开始代驾--生成账单，发送乘客



### **核心业务流程图**

![image-20240820152147687](https://github.com/xqboot/daijia-parent/blob/main/images\image-20240820152147687-1724143174050-2-1724146881535-5.png)



## 2 项目收货

项目特点：**项目技术栈广**，业务贴近实际，采用微信小程序运行，覆盖当前主流后端技术框架：JDK17、SpringBoot、SpringCloud、MyBatisPlus、Redis7、RabbitMQ、MongoDB、腾讯云服务等，契合当前企业的实际需求。



项目收货：学习了新技术 **Redisson**、**Drools、XXL-JOB、CompletableFuture异步编排、MongoDB、MinIO、**腾讯云服务（对象存储COS、人脸 | 文字识别）、腾讯位置服务、微信支付



## 3 技术选型

- **SpringBoot+SpringCloudAlibaba(Nacos + OpenFeign + Gateway）**
- MyBatis-Plus：持久层框架
- Redis：内存做缓存、**GEO**存储和计算位置信息、**分布式锁**
- Redisson：**分布式锁、延迟队列**
- **MongoDB**: 分布式文件存储的数据库
- RabbitMQ：消息中间件；分布式事务最终一致性
- **Seata**：分布式事务
- **Drools**：规则引擎，计算预估费用、平台分账、系统奖励
- ThreadPoolExecutor+**CompletableFuture**：**异步编排**，线程池来实现异步操作，提高效率
- **XXL-JOB**: 分布式任务调度框架
- Knife4J：Api接口文档工具
- **MinIO**（私有化对象存储集群）：分布式文件存储 类似于OSS（公有）
- **Natapp**：内网穿透
- **腾讯云服务**：对象存储COS、身份证认证、文字识别、人脸识别、静态活体检测、腾讯云数据万象、**腾讯位置服务**



## **4 项目演示**

![recording](https://github.com/xqboot/daijia-parent/blob/main/images\recording-1724147059646-115.gif)



## 5 规则引擎 Drools

### Drools基础语法

#### 1、规则文件构成

在使用Drools时非常重要的一个工作就是编写规则文件，通常规则文件的后缀为.drl。

**drl是Drools Rule Language的缩写**。在规则文件中编写具体的规则内容。

一套完整的规则文件内容构成如下：

| 关键字   | 描述                                                         |
| :------- | :----------------------------------------------------------- |
| package  | 包名，只限于逻辑上的管理，同一个包名下的查询或者函数可以直接调用 |
| import   | 用于导入类或者静态方法                                       |
| global   | 全局变量                                                     |
| function | 自定义函数                                                   |
| query    | 查询                                                         |
| rule end | 规则体                                                       |

Drools支持的规则文件，除了drl形式，还有Excel文件类型的。

#### 2、规则体语法结构

规则体是规则文件内容中的重要组成部分，是进行业务规则判断、处理业务结果的部分。

规则体语法结构如下：

```java
rule "ruleName"
    attributes
    when
        LHS 
    then
        RHS
end
```

**rule**：关键字，表示规则开始，参数为规则的唯一名称。

**attributes**：规则属性，是rule与when之间的参数，为可选项。

**when**：关键字，后面跟规则的条件部分。

**LHS**(Left Hand Side)：是规则的条件部分的通用名称。它由零个或多个条件元素组成。**如果LHS为空，则它将被视为始终为true的条件元素**。  （左手边）

**then**：关键字，后面跟规则的结果部分。

**RHS**(Right Hand Side)：是规则的后果或行动部分的通用名称。 （右手边）

**end**：关键字，表示一个规则结束。



#### 3、注释

在drl形式的规则文件中使用注释和Java类中使用注释一致，分为单行注释和多行注释。

单行注释用"//"进行标记，多行注释以"/*"开始，以"*/"结束。如下示例：

```drl
//规则rule1的注释，这是一个单行注释
rule "rule1"
    when
    then
        System.out.println("rule1触发");
end

/*
规则rule2的注释，
这是一个多行注释
*/
rule "rule2"
    when
    then
        System.out.println("rule2触发");
end
```



#### 4、Pattern模式匹配

前面我们已经知道了Drools中的匹配器可以将Rule Base中的所有规则与Working Memory中的Fact对象进行模式匹配，那么我们就需要在规则体的LHS部分定义规则并进行模式匹配。LHS部分由一个或者多个条件组成，条件又称为pattern。

**pattern的语法结构为：绑定变量名:Object(Field约束)**

其中绑定变量名可以省略，通常绑定变量名的命名一般建议以$开始。如果定义了绑定变量名，就可以在规则体的RHS部分使用此绑定变量名来操作相应的Fact对象。Field约束部分是需要返回true或者false的0个或多个表达式。



例如我们的入门案例中：

```java
//规则二：100元 - 500元 加100分
rule "order_rule_2"
    when
        $order:Order(amout >= 100 && amout < 500)
    then
         $order.setScore(100);
         System.out.println("成功匹配到规则二：100元 - 500元 加100分");
end
```

通过上面的例子我们可以知道，匹配的条件为：

1、工作内存中必须存在Order这种类型的Fact对象-----类型约束

2、Fact对象的amout属性值必须大于等于100------属性约束

3、Fact对象的amout属性值必须小于500------属性约束

以上条件必须同时满足当前规则才有可能被激活。



#### 5、比较操作符

Drools提供的比较操作符，如下表：

| 符号         | 说明                                                         |
| :----------- | :----------------------------------------------------------- |
| <            | 小于                                                         |
| >            | 大于                                                         |
| >=           | 大于等于                                                     |
| <=           | 小于等于                                                     |
| ==           | 等于                                                         |
| !=           | 不等于                                                       |
| contains     | 检查一个Fact对象的某个属性值是否包含一个指定的对象值         |
| not contains | 检查一个Fact对象的某个属性值是否不包含一个指定的对象值       |
| memberOf     | 判断一个Fact对象的某个属性是否在一个或多个集合中             |
| not memberOf | 判断一个Fact对象的某个属性是否不在一个或多个集合中           |
| matches      | 判断一个Fact对象的属性是否与提供的标准的Java正则表达式进行匹配 |
| not matches  | 判断一个Fact对象的属性是否不与提供的标准的Java正则表达式进行匹配 |

前6个比较操作符和Java中的完全相同。



#### 6、Drools内置方法

规则文件的`RHS`部分的主要作用是通过**插入，删除或修改工作内存中的Fact数据**，来达到控制规则引擎执行的目的。Drools提供了一些方法可以用来操作工作内存中的数据，**操作完成后规则引擎会重新进行相关规则的匹配，**原来没有匹配成功的规则在我们修改数据完成后有可能就会匹配成功了。

##### 6.1、update方法

**update方法的作用是更新工作内存中的数据，并让相关的规则重新匹配。**   （要避免死循环）

参数：

```java
//Fact对象，事实对象
Order order = new Order();
order.setAmout(30);
```

规则：

```java
//规则一：100元以下 不加分
rule "order_rule_1"
    when
        $order:Order(amout < 100)
    then
        $order.setAmout(150);
	    update($order) //update方法用于更新Fact对象，会导致相关规则重新匹配
        System.out.println("成功匹配到规则一：100元以下 不加分");
end

//规则二：100元 - 500元 加100分
rule "order_rule_2"
    when
        $order:Order(amout >= 100 && amout < 500)
    then
         $order.setScore(100);
         System.out.println("成功匹配到规则二：100元 - 500元 加100分");
end
```

在更新数据时需要注意防止发生死循环。

##### 6.2、insert方法

insert方法的作用是向工作内存中插入数据，并让相关的规则重新匹配。

```java
//规则一：100元以下 不加分
rule "order_rule_1"
    when
        $order:Order(amout < 100)
    then
        Order order = new Order();
        order.setAmout(130);
        insert(order);      //insert方法的作用是向工作内存中插入Fact对象，会导致相关规则重新匹配
        System.out.println("成功匹配到规则一：100元以下 不加分");
end

//规则二：100元 - 500元 加100分
rule "order_rule_2"
    when
        $order:Order(amout >= 100 && amout < 500)
    then
         $order.setScore(100);
         System.out.println("成功匹配到规则二：100元 - 500元 加100分");
end
```

##### 6.3、retract方法

**retract方法的作用是删除工作内存中的数据，并让相关的规则重新匹配。**

```java
//规则一：100元以下 不加分
rule "order_rule_1"
    when
        $order:Order(amout < 100)
    then
        retract($order)      //retract方法的作用是删除工作内存中的Fact对象，会导致相关规则重新匹配
        System.out.println("成功匹配到规则一：100元以下 不加分");
end
```



### 规则属性  attributes

前面我们已经知道了规则体的构成如下：

```java
rule "ruleName"
    attributes
    when
        LHS
    then
        RHS
end
```

本章节就是针对规则体的**attributes**属性部分进行讲解。Drools中提供的属性如下表(部分属性)：

| 属性名           | 说明                                               |
| :--------------- | :------------------------------------------------- |
| salience         | 指定规则执行优先级                                 |
| dialect          | 指定规则使用的语言类型，取值为java和mvel           |
| enabled          | 指定规则是否启用                                   |
| date-effective   | 指定规则生效时间                                   |
| date-expires     | 指定规则失效时间                                   |
| activation-group | 激活分组，具有相同分组名称的规则只能有一个规则触发 |
| agenda-group     | 议程分组，只有获取焦点的组中的规则才有可能触发     |
| timer            | 定时器，指定规则触发的时间                         |
| auto-focus       | 自动获取焦点，一般结合agenda-group一起使用         |
| no-loop          | 防止死循环                                         |

重点说一下我们项目需要使用的属性

#### 1、salience属性

salience属性用于指定规则的执行优先级，**取值类型为Integer**。**数值越大越优先执行**。每个规则都有一个默认的执行顺序，如果不设置salience属性，规则体的执行顺序为由上到下。

可以通过创建规则文件salience.drl来测试salience属性，内容如下：

```java
package com.order

rule "rule_1"
    when
        eval(true)
    then
        System.out.println("规则rule_1触发");
end
    
rule "rule_2"
    when
        eval(true)
    then
        System.out.println("规则rule_2触发");
end

rule "rule_3"
    when
        eval(true)
    then
        System.out.println("规则rule_3触发");
end
```



通过控制台可以看到，由于以上三个规则没有设置salience属性，所以执行的顺序是按照规则文件中规则的顺序由上到下执行的。接下来我们修改一下文件内容：

```java
package com.order

rule "rule_1"
    salience 9
    when
        eval(true)
    then
        System.out.println("规则rule_1触发");
end

rule "rule_2"
    salience 10
    when
        eval(true)
    then
        System.out.println("规则rule_2触发");
end

rule "rule_3"
    salience 8
    when
        eval(true)
    then
        System.out.println("规则rule_3触发");
end
```

通过控制台可以看到，规则文件执行的顺序是按照我们设置的salience值由大到小顺序执行的。

建议在编写规则时使用salience属性明确指定执行优先级。



#### 2、no-loop属性

no-loop属性用于防止死循环，当规则通过update之类的函数修改了Fact对象时，可能使当前规则再次被激活从而导致死循环。取值类型为Boolean，默认值为false，测试步骤如下：

编写规则文件/resources/rules/activationgroup.drl

```java
//订单积分规则
package com.order
import com.atguigu.drools.model.Order

//规则一：100元以下 不加分
rule "order_rule_1"
    no-loop true         //防止陷入死循环
    when
        $order:Order(amout < 100)
    then
        $order.setScore(0);
        update($order)
        System.out.println("成功匹配到规则一：100元以下 不加分");
end
```

通过控制台可以看到，由于我们没有设置no-loop属性的值，所以发生了死循环。接下来设置no-loop的值为true再次测试则不会发生死循环。



### Drools高级语法

前面章节我们已经知道了一套完整的规则文件内容构成如下：

| 关键字   | 描述                                                         |
| :------- | :----------------------------------------------------------- |
| package  | 包名，只限于逻辑上的管理，同一个包名下的查询或者函数可以直接调用 |
| import   | 用于导入类或者静态方法                                       |
| global   | 全局变量                                                     |
| function | 自定义函数                                                   |
| query    | 查询                                                         |
| rule end | 规则体                                                       |

#### global全局变量

global关键字用于在规则文件中**定义全局变量**，它可以让应用程序的对象在规则文件中能够被访问。可以用来为规则文件提供数据或服务。

语法结构为：**global 对象类型 对象名称**

在使用global定义的全局变量时有两点需要注意：

1、如果对象类型为**包装类型**时，在一个规则中改变了global的值，那么**只针对当前规则有效**，对其他规则中的global不会有影响。可以理解为它是当前规则代码中的global副本，规则内部修改不会影响全局的使用。

2、如果对象类型为**集合类型或JavaBean**时，在一个规则中改变了global的值，对java代码和所有规则都有效。

订单Order：

```java
package com.atguigu.drools.model;

public class Order {

    private double amout;

    public double getAmout() {
        return amout;
    }

    public void setAmout(double amout) {
        this.amout = amout;
    }

}
```

积分Integral：

```java
package com.atguigu.drools.model;

public class Integral {

    private double score;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
```

规则文件：

```java
//订单积分规则
package com.order
import com.atguigu.drools.model.Order

global com.atguigu.drools.model.Integral integral;

//规则一：100元以下 不加分
rule "order_rule_1"
    no-loop true         //防止陷入死循环
    when
        $order:Order(amout < 100)
    then
        integral.setScore(10);
        update($order)
        System.out.println("成功匹配到规则一：100元以下 不加分");
end
```

测试：

```java
@Test
public void test1(){
    //从Kie容器对象中获取会话对象
    KieSession session = kieContainer.newKieSession();

    //Fact对象，事实对象
    Order order = new Order();
    order.setAmout(30);

    //全局变量
    Integral integral = new Integral();
    session.setGlobal("integral", integral);

    //将Order对象插入到工作内存中
    session.insert(order);

    //激活规则，由Drools框架自动进行规则匹配，如果规则匹配成功，则执行当前规则
    session.fireAllRules();
    //关闭会话
    session.dispose();

    System.out.println("订单金额：" + order.getAmout());
    System.out.println("添加积分：" + integral.getScore());
}
```



## 6 任务调度

前面乘客端已经下单了，附近的司机我们也能搜索了，接下来我们就要看怎么把这两件事给关联上？

乘客下单，搜索附近的司机，但是可能当时附近有司机，也有可能当时附近没有司机，乘客下单的一个等待时间为15分钟（15分钟后系统自动取消订单），那么下单与搜索司机怎么关联上呢？答案肯定是任务调度。

乘客下单了，然后启动一个任务调度，每隔1分钟执行一次搜索附近司机的任务调度，只要在15分钟内没有司机接单，那么就必须一直查找附近适合的司机，直到15分钟内有司机接单为止。任务调度搜索到满足条件的司机后，会在服务器端给司机建立一个临时队列（1分钟过期），把新订单数据放入队列，司机小程序端开启接单服务后，每隔几秒轮询获取临时队列里面的新订单数据，在小程序前端进行语音播报，司机即可进行抢单操作。

### 1、定时任务调度框架

#### 1.1、单机

- Timer：这是 java 自带的 java.util.Timer 类，这个类允许你调度一个 java.util.TimerTask 任务。使用这种方式可以让你的程序按照某一个频度执行，但不能在指定时间运行。一般用的较少。
- ScheduledExecutorService：也 jdk 自带的一个类；是基于线程池设计的定时任务类，每个调度任务都会分配到线程池中的一个线程去执行，也就是说，任务是并发执行，互不影响。
- Spring Task：Spring3.0 以后自带的 task，配置简单功能较多，如果系统使用单机的话可以优先考虑spring定时器。

#### 1.2、分布式

- Quartz：Java事实上的定时任务标准。但Quartz关注点在于定时任务而非数据，并无一套根据数据处理而定制化的流程。虽然Quartz可以基于数据库实现作业的高可用，但缺少分布式并行调度的功能。
- TBSchedule：阿里早期开源的分布式任务调度系统。代码略陈旧，使用timer而非线程池执行任务调度。众所周知，timer在处理异常状况时是有缺陷的。而且TBSchedule作业类型较为单一，只能是获取/处理数据一种模式。还有就是文档缺失比较严重。
- elastic-job：当当开发的弹性分布式任务调度系统，功能丰富强大，采用zookeeper实现分布式协调，实现任务高可用以及分片，并且可以支持云开发。
- Saturn：是唯品会自主研发的分布式的定时任务的调度平台，基于当当的elastic-job 版本1开发，并且可以很好的部署到docker容器上。
- xxl-job: 是大众点评员工徐雪里于2015年发布的分布式任务调度平台，是一个轻量级分布式任务调度框架，其核心设计目标是开发迅速、学习简单、轻量级、易扩展，其在唯品会内部已经发部署350+个节点，每天任务调度4000多万次。同时，管理和统计也是它的亮点。使用案例 大众点评、易信(IM)、京东(电商系统)、360金融(金融系统)、易企秀、随行付(支付系统)、优信二手车。

我们项目选择：XXL-JOB



### 2、XXL-JOB分布式任务调度平台

官方文档：https://www.xuxueli.com/xxl-job/

#### 2.1、概述

XXL-JOB是一个分布式任务调度平台，其核心设计目标是开发迅速、学习简单、轻量级、易扩展。现已开放源代码并接入多家公司线上产品线，开箱即用。



#### 2.2、特性（仅了解）

- 1、简单：支持通过Web页面对任务进行CRUD操作，操作简单，一分钟上手；
- 2、动态：支持动态修改任务状态、启动/停止任务，以及终止运行中任务，即时生效；
- 3、调度中心HA（中心式）：调度采用中心式设计，“调度中心”自研调度组件并支持集群部署，可保证调度中心HA；
- 4、执行器HA（分布式）：任务分布式执行，任务”执行器”支持集群部署，可保证任务执行HA；
- 5、注册中心: 执行器会周期性自动注册任务, 调度中心将会自动发现注册的任务并触发执行。同时，也支持手动录入执行器地址；
- 6、弹性扩容缩容：一旦有新执行器机器上线或者下线，下次调度时将会重新分配任务；
- 7、触发策略：提供丰富的任务触发策略，包括：Cron触发、固定间隔触发、固定延时触发、API（事件）触发、人工触发、父子任务触发；
- 8、调度过期策略：调度中心错过调度时间的补偿处理策略，包括：忽略、立即补偿触发一次等；
- 9、阻塞处理策略：调度过于密集执行器来不及处理时的处理策略，策略包括：单机串行（默认）、丢弃后续调度、覆盖之前调度；
- 10、任务超时控制：支持自定义任务超时时间，任务运行超时将会主动中断任务；
- 11、任务失败重试：支持自定义任务失败重试次数，当任务失败时将会按照预设的失败重试次数主动进行重试；其中分片任务支持分片粒度的失败重试；
- 12、任务失败告警；默认提供邮件方式失败告警，同时预留扩展接口，可方便的扩展短信、钉钉等告警方式；
- 13、路由策略：执行器集群部署时提供丰富的路由策略，包括：第一个、最后一个、轮询、随机、一致性HASH、最不经常使用、最近最久未使用、故障转移、忙碌转移等；
- 14、分片广播任务：执行器集群部署时，任务路由策略选择”分片广播”情况下，一次任务调度将会广播触发集群中所有执行器执行一次任务，可根据分片参数开发分片任务；
- 15、动态分片：分片广播任务以执行器为维度进行分片，支持动态扩容执行器集群从而动态增加分片数量，协同进行业务处理；在进行大数据量业务操作时可显著提升任务处理能力和速度。
- 16、故障转移：任务路由策略选择”故障转移”情况下，如果执行器集群中某一台机器故障，将会自动Failover切换到一台正常的执行器发送调度请求。
- 17、任务进度监控：支持实时监控任务进度；
- 18、Rolling实时日志：支持在线查看调度结果，并且支持以Rolling方式实时查看执行器输出的完整的执行日志；
- 19、GLUE：提供Web IDE，支持在线开发任务逻辑代码，动态发布，实时编译生效，省略部署上线的过程。支持30个版本的历史版本回溯。
- 20、脚本任务：支持以GLUE模式开发和运行脚本任务，包括Shell、Python、NodeJS、PHP、PowerShell等类型脚本;
- 21、命令行任务：原生提供通用命令行任务Handler（Bean任务，”CommandJobHandler”）；业务方只需要提供命令行即可；
- 22、任务依赖：支持配置子任务依赖，当父任务执行结束且执行成功后将会主动触发一次子任务的执行, 多个子任务用逗号分隔；
- 23、一致性：“调度中心”通过DB锁保证集群分布式调度的一致性, 一次任务调度只会触发一次执行；
- 24、自定义任务参数：支持在线配置调度任务入参，即时生效；
- 25、调度线程池：调度系统多线程触发调度运行，确保调度精确执行，不被堵塞；
- 26、数据加密：调度中心和执行器之间的通讯进行数据加密，提升调度信息安全性；
- 27、邮件报警：任务失败时支持邮件报警，支持配置多邮件地址群发报警邮件；
- 28、推送maven中央仓库: 将会把最新稳定版推送到maven中央仓库, 方便用户接入和使用;
- 29、运行报表：支持实时查看运行数据，如任务数量、调度次数、执行器数量等；以及调度报表，如调度日期分布图，调度成功分布图等；
- 30、全异步：任务调度流程全异步化设计实现，如异步调度、异步运行、异步回调等，有效对密集调度进行流量削峰，理论上支持任意时长任务的运行；
- 31、跨语言：调度中心与执行器提供语言无关的 RESTful API 服务，第三方任意语言可据此对接调度中心或者实现执行器。除此之外，还提供了 “多任务模式”和“httpJobHandler”等其他跨语言方案；
- 32、国际化：调度中心支持国际化设置，提供中文、英文两种可选语言，默认为中文；
- 33、容器化：提供官方docker镜像，并实时更新推送dockerhub，进一步实现产品开箱即用；
- 34、线程池隔离：调度线程池进行隔离拆分，慢任务自动降级进入”Slow”线程池，避免耗尽调度线程，提高系统稳定性；
- 35、用户管理：支持在线管理系统用户，存在管理员、普通用户两种角色；
- 36、权限控制：执行器维度进行权限控制，管理员拥有全量权限，普通用户需要分配执行器权限后才允许相关操作；



#### 2.3、下载

**文档地址**

- [中文文档](https://www.xuxueli.com/xxl-job/)
- [English Documentation](https://www.xuxueli.com/xxl-job/en/)

**源码仓库地址**

| 源码仓库地址                           | Release Download                                          |
| -------------------------------------- | --------------------------------------------------------- |
| <https://github.com/xuxueli/xxl-job>   | [Download](https://github.com/xuxueli/xxl-job/releases)   |
| <http://gitee.com/xuxueli0323/xxl-job> | [Download](http://gitee.com/xuxueli0323/xxl-job/releases) |

**中央仓库地址**

当前项目使用版本：2.4.1-SNAPSHOT

注：为了统一版本，已统一下载，在资料中获取：xxl-job-master.zip

```xml
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>${最新稳定版本}</version>
</dependency>
```

#### 2.4、快速入门

##### 2.4.1、导入项目到idea

解压：xxl-job-master.zip，导入idea，如图：

![69016961678](https://github.com/xqboot/daijia-parent/blob/main/images\1690169616781-1724146881535-6.png)

项目结构说明：

```yaml
xxl-job-master：
    xxl-job-admin：调度中心
    xxl-job-core：公共依赖
    xxl-job-executor-samples：执行器Sample示例（选择合适的版本执行器，可直接使用，也可以参考其并将现有项目改造成执行器）
        xxl-job-executor-sample-springboot：Springboot版本，通过Springboot管理执行器，推荐这种方式；
        xxl-job-executor-sample-frameless：无框架版本；
```

##### 2.4.2、初始化“调度数据库”

获取 “调度数据库初始化SQL脚本” 并执行即可。

调度数据库初始化SQL脚本” 位置为：

```
/xxl-job-master/doc/db/tables_xxl_job.sql
```

##### 2.4.3、部署”调度中心“

```
调度中心项目：xxl-job-admin
作用：统一管理任务调度平台上调度任务，负责触发调度执行，并且提供任务管理平台。     
```

###### 步骤一：修改数据库连接

```properties
### xxl-job, datasource
spring.datasource.url=jdbc:mysql://localhost:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

###### 步骤二：启动项目

调度中心访问地址：<http://localhost:8080/xxl-job-admin> 

默认登录账号 “admin/123456”, 登录后运行界面如下图所示：

![69017820961](https://github.com/xqboot/daijia-parent/blob/main/images\1690178209611-1724146881535-7.png)

###### 步骤三：调度中心集群部署（可选）

调度中心支持集群部署，提升调度系统容灾和可用性。

调度中心集群部署时，几点要求和建议：

- DB配置保持一致；
- 集群机器时钟保持一致（单机集群忽视）；
- 建议：推荐通过nginx为调度中心集群做负载均衡，分配域名。调度中心访问、执行器回调配置、调用API服务等操作均通过该域名进行。

##### 2.4.4、配置部署“执行器项目”

```
“执行器”项目：xxl-job-executor-sample-springboot (提供多种版本执行器供选择，现以 springboot 版本为例，可直接使用，也可以参考其并将现有项目改造成执行器)
作用：负责接收“调度中心”的调度并执行；可直接部署执行器，也可以将执行器集成到现有业务项目中。
```

###### 步骤一：maven依赖

确认pom文件中引入了 “xxl-job-core” 的maven依赖；

```xml
<!-- xxl-job-core -->
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>2.4.1-SNAPSHOT</version>
</dependency>
```

###### 步骤二：执行器配置

执行器配置，配置内容说明：

```properties
### 调度中心部署根地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；
xxl.job.admin.addresses=http://127.0.0.1:8080/xxl-job-admin
### 执行器通讯TOKEN [选填]：非空时启用；
xxl.job.accessToken=
### 执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册
xxl.job.executor.appname=xxl-job-executor-sample
### 执行器注册 [选填]：优先使用该配置作为注册地址，为空时使用内嵌服务 ”IP:PORT“ 作为注册地址。从而更灵活的支持容器类型执行器动态IP和动态映射端口问题。
xxl.job.executor.address=
### 执行器IP [选填]：默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"；
xxl.job.executor.ip=
### 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口；
xxl.job.executor.port=9999
### 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径；
xxl.job.executor.logpath=/data/applogs/xxl-job/jobhandler
### 执行器日志文件保存天数 [选填] ： 过期日志自动清理, 限制值大于等于3时生效; 否则, 如-1, 关闭自动清理功能；
xxl.job.executor.logretentiondays=30
```

###### 步骤三：执行器组件配置

执行器组件，配置内容说明：

```java
package com.xxl.job.executor.core.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
public class XxlJobConfig {
    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${xxl.job.executor.appname}")
    private String appname;

    @Value("${xxl.job.executor.address}")
    private String address;

    @Value("${xxl.job.executor.ip}")
    private String ip;

    @Value("${xxl.job.executor.port}")
    private int port;

    @Value("${xxl.job.executor.logpath}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;


    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appname);
        xxlJobSpringExecutor.setAddress(address);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return xxlJobSpringExecutor;
    }

    /**
     * 针对多网卡、容器内部署等情况，可借助 "spring-cloud-commons" 提供的 "InetUtils" 组件灵活定制注册IP；
     *
     *      1、引入依赖：
     *          <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-commons</artifactId>
     *             <version>${version}</version>
     *         </dependency>
     *
     *      2、配置文件，或者容器启动变量
     *          spring.cloud.inetutils.preferred-networks: 'xxx.xxx.xxx.'
     *
     *      3、获取IP
     *          String ip_ = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
     */


}
```

###### 步骤四：启动执行器项目：

启动：xxl-job-executor-sample-springboot

###### 步骤五：执行器集群（可选）：

执行器支持集群部署，提升调度系统可用性，同时提升任务处理能力。

执行器集群部署时，几点要求和建议：

- 执行器回调地址（xxl.job.admin.addresses）需要保持一致；执行器根据该配置进行执行器自动注册等操作。
- 同一个执行器集群内AppName（xxl.job.executor.appname）需要保持一致；调度中心根据该配置动态发现不同集群的在线执行器列表。

##### 2.4.5、第一个任务调度

###### 步骤一：配置执行器

![69018192871](https://github.com/xqboot/daijia-parent/blob/main/images\1690181928710-1724146881535-9.png)

上面我们启动了xxl-job-executor-sample-springboot 执行器项目，当前已注册上来，我们执行使用改执行器。

执行器属性说明：

```
AppName: 是每个执行器集群的唯一标示AppName, 执行器会周期性以AppName为对象进行自动注册。可通过该配置自动发现注册成功的执行器, 供任务调度时使用;
名称: 执行器的名称, 因为AppName限制字母数字等组成,可读性不强, 名称为了提高执行器的可读性;排序: 执行器的排序, 系统中需要执行器的地方,如任务新增, 将会按照该排序读取可用的执行器列表;
注册方式：调度中心获取执行器地址的方式；    
	自动注册：执行器自动进行执行器注册，调度中心通过底层注册表可以动态发现执行器机器地址；    
	手动录入：人工手动录入执行器的地址信息，多地址逗号分隔，供调度中心使用；
机器地址："注册方式"为"手动录入"时有效，支持人工维护执行器的地址信息；
```

###### 步骤二：新建任务：

登录调度中心：<http://localhost:8080/xxl-job-admin> 

默认登录账号 “admin/123456”

任务管理 ==》 新增

![69018006762](https://github.com/xqboot/daijia-parent/blob/main/images\1690180067623-1724146881535-8.png)

添加成功，如图：

![69018012796](https://github.com/xqboot/daijia-parent/blob/main/images\1690180127960-1724146881535-10.png)

###### 步骤三：执行器项目开发job方法

使用xxl-job-executor-sample-springboot项目job实例，与步骤二的JobHandler配置一致

```java
/**
 * 1、简单任务示例（Bean模式）
 */
@XxlJob("demoJobHandler")
public void demoJobHandler() throws Exception {
    XxlJobHelper.log("XXL-JOB, Hello World.");

    for (int i = 0; i < 5; i++) {
        XxlJobHelper.log("beat at:" + i);
        TimeUnit.SECONDS.sleep(2);
    }
    // default success
}
```

###### 步骤四：启动任务

![69018040084](https://github.com/xqboot/daijia-parent/blob/main/images\1690180400846-1724146881535-11.png)

任务列表状态改变，如图：

![69018046414](https://github.com/xqboot/daijia-parent/blob/main/images\1690180464140-1724146881535-12.png)

设置断点，执行结果：

![69018166550](https://github.com/xqboot/daijia-parent/blob/main/images\1690181665504-1724146881535-13.png)

查看调度日志：

![69018267735](https://github.com/xqboot/daijia-parent/blob/main/images\1690182677356-1724146881535-14.png)



## 7 MongoDB

### 1、MongoDB

#### 1.1、MongoDB 概念

##### 1.1.1、什么是MongoDB

MongoDB 是在2007年由DoubleClick公司的几位核心成员开发出的一款分布式文档数据库，由C++语言编写。

目的是为了解决数据大量增长的时候系统的可扩展性和敏捷性。MongoDB要比传统的关系型数据库简单很多。

在MongoDB中数据主要的组织结构就是`数据库、集合和文档`，文档存储在集合当中，集合存储在数据库中。

MongoDB中每一条数据记录就是一个文档，`数据结构由键值(key=>value)对组成`。

文档类似于 JSON 对象，它的数据结构被叫做`BSON`（Binary JSON）。

![img](https://github.com/xqboot/daijia-parent/blob/main/images\788db5ab-31c3-4fa4-bf9c-881c3d09ec54-1724146881535-15.png)





下表将帮助您更容易理解MongoDB中的一些概念：

| RDBMS  | MongoDB  |
| ------ | -------- |
| 数据库 | 数据库   |
| 表格   | 集合     |
| 行     | 文档     |
| 列     | 字段     |
| 表联合 | 嵌入文档 |
| 主键   | _id      |



##### 1.1.2、MongoDB适用场景

MongoDB不需要去明确指定一张表的具体结构，对字段的管理非常灵活，有很强的可扩展性。

支持高并发、高可用、高可扩展性，自带数据压缩功能，支持海量数据的高效存储和访问。

支持基本的CRUD、数据聚合、文本搜索和地理空间查询功能。



**适用场景：**

- 网站数据：Mongo非常适合实时的插入，更新与查询，并具备网站实时数据存储所需的复制及高度伸缩性。
- 高伸缩性的场景：Mongo非常适合由数十或数百台服务器组成的数据库。
- 大尺寸，低价值的数据：使用传统的关系型数据库存储一些数据时可能会比较昂贵，在此之前，很多时候程序员往往会选择传统的文件进行存储。
- 缓存：由于性能很高，Mongo也适合作为信息基础设施的缓存层。在系统重启之后，由Mongo搭建的持久化缓存层可以避免下层的数据源过载。

**例如：**

弹幕、直播间互动信息、朋友圈信息、物流场景等



**不适用场合：**

- 高度事务性系统：例如银行系统。传统的关系型数据库目前还是更适用于需要大量原子性复杂事务的应用程序。
- 传统的商业智能应用：针对特定问题的BI数据库会对产生高度优化的查询方式。对于此类应用，数据仓库可能是更合适的选择。



#### 1.2、安装和启动（docker方式）

##### 1.2.1、拉取镜像

```shell
docker pull mongo:7.0.0
```

##### 1.2.2、创建和启动容器

需要在宿主机建立文件夹

> rm -rf /opt/mongo
>
> mkdir -p /opt/mongo/data/db

```shell
docker run -d --restart=always -p 27017:27017 --name mongo -v /opt/mongo/data/db:/data/db mongo:7.0.0
```

##### 1.2.3、进入容器

```shell
docker exec -it mongo mongosh
```

##### 1.2.4、基本命令

```shell
show dbs
db.version() #当前db版本
db.getMongo() #查看当前db的链接机器地址
db.help() #帮助
quit() #退出命令行
```



#### 1.3、客户端远程远程连接

**资料：**`资料>mongodb客户端>mongodb-compass-1.39.3-win32-x64.exe`，安装

**客户端连接：**

![69337734195](https://github.com/xqboot/daijia-parent/blob/main/images\1693377341958-1724146881535-16.png)



#### 1.4、数据库操作

##### 1.4.1、创建数据库

如果数据库不存在，则创建数据库，否则切换到指定数据库。

```shell
use tingshu
```

##### 1.4.2、查看当前数据库

```
db.getName()
```

##### 1.4.3、显示当前数据库状态

```
db.stats()
```

##### 1.4.4、删除当前数据库

```
db.dropDatabase()
```



#### 1.5、集合操作

##### 1.5.1、创建集合

```shell
db.createCollection("User")
```

##### 1.5.2、删除集合

```shell
db.User.drop()
```



#### 1.6、文档操作

文档是一组键值(key-value)对。MongoDB 的文档不需要设置相同的字段，并且相同的字段不需要相同的数据类型，这与关系型数据库有很大的区别，也是 MongoDB 非常突出的特点。



**需要注意的是：**

1、MongoDB区分类型和大小写。

2、MongoDB的文档不能有重复的键。



##### 1.6.1、insert

向User集合插入一条记录。可以预先使用createCollection方法创建集合，也可以不创建集合，直接插入数据，那么集合会被自动创建

```shell
db.User.insert({name:'zhangsan',age:21,sex:true})
```

##### 1.6.2、query

查询当前User集合中所有的记录

```shell
db.User.find()
```

查询当前User集合中name是zhangsan的记录

```shell
db.User.find({name:"zhangsan"})
```

##### 1.6.3、update

只更新匹配到的第一条记录

```shell
db.User.update({age:20}, {$set:{name:100}}) 
```

更新匹配到的所有记录

```shell
db.User.update({age:21}, {$set:{age:99}}, {multi: true})
```

##### 1.6.4、remove

移除一个文档

```shell
db.User.remove(id)
```

移除所有文档

```shell
db.User.remove({}) 
```



**更多命令参考：**https://www.runoob.com/mongodb/mongodb-tutorial.html



### 2、SpringBoot集成MongoDB

spring-data-mongodb提供了`MongoTemplate`与`MongoRepository`两种方式访问mongodb，MongoRepository操作简单，MongoTemplate操作灵活，我们在项目中可以灵活使用这两种方式操作mongodb。



     <!--mongodb-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>



1. **MongoRepository** 是 Spring Data MongoDB 提供的基于接口的抽象化操作，通过**定义接口继承** MongoRepository，可以自动实现基本的 CRUD 操作。这种方式更简洁，易于维护，但功能相对有限。

2. **MongoTemplate** 是 Spring Data MongoDB 提供的模板类，通过实例化 MongoTemplate，可以执行更复杂的查询和操作。这种方式功能更强大，但代码量相对较多。（按照规则组装方法）

总结：如果你需要快速实现基本的 CRUD 操作，可以选择 MongoRepository；如果你需要执行更复杂的查询和操作，可以选择 MongoTemplate。



## 8 Minio

司机在代驾的过程中要上传录音文件信息，我们可以保存到Minio里面。毕竟我们是拿Minio充当私有云来使用的，当前我们来封装Minio的上传接口



### 1、Minio介绍

官网：https://www.minio.org.cn/

MinIO是一个开源的**分布式对象存储服务器**，支持S3协议并且可以在多节点上实现数据的高可用和容错。它采用Go语言开发，拥有轻量级、高性能、易部署等特点，并且可以自由选择底层存储介质。



MinIO的主要特点包括：

1、高性能：MinIO基于GO语言编写，具有高速、轻量级、高并发等性能特点，还支持多线程和缓存等机制进行优化，可以快速地处理大规模数据。

2、可扩展性：MinIO采用分布式存储模式，支持水平扩展，通过增加节点数量来扩展存储容量和性能，支持自动数据迁移和负载均衡。

3、安全性：MinIO提供了多种安全策略，如访问控制列表（ACL）、服务端加密（SSE）、传输层安全性（TLS）等，可以保障数据安全和隐私。

4、兼容性：MinIO兼容AWS S3 API，还支持其他云服务提供商的API，比如GCP、Azure等，可以通过简单的配置实现互操作性。

5、简单易用：MinIO的部署和管理非常简单，只需要运行一个二进制包即可启动服务，同时提供了Web界面和命令行工具等方便的管理工具。



**S3协议**是Amazon Web Services (AWS) 提供的对象存储服务（Simple Storage Service）的API协议。它是一种 RESTful风格的Web服务接口，使用HTTP/HTTPS协议进行通信，支持多种编程语言和操作系统，并实现了数据的可靠存储、高扩展性以及良好的可用性。

### 2、Minio安装

官网地址：https://www.minio.org.cn/docs/cn/minio/container/index.html

具体命令：

```java
// 创建数据存储目录
mkdir -p ~/minio/data

// 创建minio
docker run \
   -p 9000:9000 \
   -p 9090:9090 \
   --name minio \
   -v ~/minio/data:/data \
   -e "MINIO_ROOT_USER=admin" \
   -e "MINIO_ROOT_PASSWORD=admin123456" \
   -d \
   quay.io/minio/minio server /data --console-address ":9090"
```

### 3、Minio入门

本章节会给大家介绍一下如何通过Java客户端操作Minio，可以参考官网地址。

官网地址：https://min.io/docs/minio/linux/developers/java/minio-java.html

具体步骤：

1、加入如下依赖

已引入可忽略

```xml
<!-- web-driver模块中加入如下依赖 -->
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.2</version>
</dependency>
```

2、示例代码

```java
public class FileUploadTest {

    public static void main(String[] args) throws Exception {

        // 创建一个Minio的客户端对象
        MinioClient minioClient = MinioClient.builder()
                        .endpoint("http://192.168.136.142:9001")
                        .credentials("admin", "admin123456")
                        .build();

        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("daijia").build());

        // 如果不存在，那么此时就创建一个新的桶
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("daijia").build());
        } else {  // 如果存在打印信息
            System.out.println("Bucket 'daijia' already exists.");
        }

        FileInputStream fis = new FileInputStream("D://images//1.jpg") ;
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket("spzx-bucket")
                .stream(fis, fis.available(), -1)
                .object("1.jpg")
                .build();
        minioClient.putObject(putObjectArgs) ;

        // 构建fileUrl
        String fileUrl = "http://192.168.136.142:9000/spzx-bucket/1.jpg" ;
        System.out.println(fileUrl);

    }

}
```

注意：设置minio的中该桶的访问权限为public，如下所示：

![image-20230515234445425](https://github.com/xqboot/daijia-parent/blob/main/images\image-20230515234445425-1724146881535-17.png)



## 9 CompletableFuture异步编排

### 1、CompletableFuture异步编排

#### 1.1、CompletableFuture介绍

* 问题：司机结束代驾服务页面非常复杂，数据的获取都需要远程调用，必然需要花费更多的时间。

假如司机结束代驾服务的每个查询，需要如下标注的时间才能完成

1. 获取订单信息   1s
2. 计算防止刷单 0.5s
3. 计算订单实际里程 0.5s
4. 计算订单实际代驾费用 1s
5. ......

* 那么，司机需要4s后才能结束代驾服务。很显然是不能接受的。如果有多个线程同时完成这多步操作，也许只需要1.1s即可完成响应。

* 使用`CompletableFuture`可用于线程异步编排，使原本串行执行的代码，变为并行执行，提高代码执行速度。 

#### 1.2、CompletableFuture使用

说明：使用`CompletableFuture`异步编排大多方法都会有一个重载方法，会多出一个executor参数，用来传来自定义的线程池，如果不传就会使用默认的线程池。

##### 1.2.1、创建异步编排对象

```java
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier);
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor);

public static CompletableFuture<Void> runAsync(Runnable runnable);
public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor);
```



##### 1.2.2、线程串行方法

```java
// 使线程串行执行，无入参，无返回值
public CompletableFuture<Void> thenRun(Runnable action);
public CompletableFuture<Void> thenRunAsync(Runnable action);
public CompletableFuture<Void> thenRunAsync(Runnable action, Executor executor);

// 使线程串行执行，有入参，无返回值
public CompletableFuture<Void> thenAccept(Consumer<? super T> action);
public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action);
public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor);

// 使线程串行执行，有入参，有返回值
public <U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn);
public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn);
public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor);
```



##### 1.2.3、多任务组合

```java
public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs);
```



##### 1.2.4、代码示例

```java
package com.atguigu.daijia.driver;

import lombok.SneakyThrows;

import java.util.concurrent.*;

public class CompletableFutureTest5 {

    @SneakyThrows
    public static void main(String[] args) {
        //动态获取服务器核数
        int processors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                processors+1, // 核心线程个数 io:2n ,cpu: n+1  n:内核数据
                processors+1,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> "任务1", executor);
        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> "任务2", executor);
        CompletableFuture<String> future03 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务3";
        }, executor);

        // 串联起若干个线程任务, 没有返回值
        CompletableFuture<Void> all = CompletableFuture.allOf(future01, future02, future03);
        // 等待所有线程执行完成
        // .join()和.get()都会阻塞并获取线程的执行情况
        // .join()会抛出未经检查的异常，不会强制开发者处理异常 .get()会抛出检查异常，需要开发者处理
        all.join();
        all.get();
    }
}   
```



### 2、结束代驾

#### 2.1、ThreadPoolConfig

全局自定义线程池配置

```java
package com.atguigu.daijia.driver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全局自定义线程池配置
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        //动态获取服务器核数
        int processors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                processors+1, // 核心线程个数 io:2n ,cpu: n+1  n:内核数据
                processors+1,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        //  返回线程池对象
        return threadPoolExecutor;
    }
}
```

#### 2.2、OrderServiceImpl

```java
@Autowired
private ThreadPoolExecutor threadPoolExecutor;

@SneakyThrows
@Override
public Boolean endDrive(OrderFeeForm orderFeeForm) {
   //1.获取订单信息
   CompletableFuture<OrderInfo> orderInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
      OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderFeeForm.getOrderId()).getData();
      return orderInfo;
   }, threadPoolExecutor);

   //2.防止刷单，计算司机的经纬度与代驾的终点经纬度是否在2公里范围内
   CompletableFuture<OrderServiceLastLocationVo> orderServiceLastLocationVoCompletableFuture = CompletableFuture.supplyAsync((() -> {
      OrderServiceLastLocationVo orderServiceLastLocationVo = locationFeignClient.getOrderServiceLastLocation(orderFeeForm.getOrderId()).getData();
      return orderServiceLastLocationVo;
   }), threadPoolExecutor);

   //合并
   CompletableFuture.allOf(orderInfoCompletableFuture,
         orderServiceLastLocationVoCompletableFuture
   ).join();

   //获取数据
   OrderInfo orderInfo = orderInfoCompletableFuture.get();
   //2.1.判断刷单
   OrderServiceLastLocationVo orderServiceLastLocationVo = orderServiceLastLocationVoCompletableFuture.get();
   //司机的位置与代驾终点位置的距离
   double distance = LocationUtil.getDistance(orderInfo.getEndPointLatitude().doubleValue(), orderInfo.getEndPointLongitude().doubleValue(), orderServiceLastLocationVo.getLatitude().doubleValue(), orderServiceLastLocationVo.getLongitude().doubleValue());
   if(distance > SystemConstant.DRIVER_START_LOCATION_DISTION) {
      throw new GuiguException(ResultCodeEnum.DRIVER_END_LOCATION_DISTION_ERROR);
   }

   //3.计算订单实际里程
   CompletableFuture<BigDecimal> realDistanceCompletableFuture = CompletableFuture.supplyAsync(() -> {
      BigDecimal realDistance = locationFeignClient.calculateOrderRealDistance(orderFeeForm.getOrderId()).getData();
      log.info("结束代驾，订单实际里程：{}", realDistance);
      return realDistance;
   }, threadPoolExecutor);


   //4.计算代驾实际费用
   CompletableFuture<FeeRuleResponseVo> feeRuleResponseVoCompletableFuture = realDistanceCompletableFuture.thenApplyAsync((realDistance)->{
      FeeRuleRequestForm feeRuleRequestForm = new FeeRuleRequestForm();
      feeRuleRequestForm.setDistance(realDistance);
      feeRuleRequestForm.setStartTime(orderInfo.getStartServiceTime());
      //等候时间
      Integer waitMinute = Math.abs((int) ((orderInfo.getArriveTime().getTime() - orderInfo.getAcceptTime().getTime()) / (1000 * 60)));
      feeRuleRequestForm.setWaitMinute(waitMinute);
      log.info("结束代驾，费用参数：{}", JSON.toJSONString(feeRuleRequestForm));
      FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm).getData();
      log.info("费用明细：{}", JSON.toJSONString(feeRuleResponseVo));
      //订单总金额 需加上 路桥费、停车费、其他费用、乘客好处费
      BigDecimal totalAmount = feeRuleResponseVo.getTotalAmount().add(orderFeeForm.getTollFee()).add(orderFeeForm.getParkingFee()).add(orderFeeForm.getOtherFee()).add(orderInfo.getFavourFee());
      feeRuleResponseVo.setTotalAmount(totalAmount);
      return feeRuleResponseVo;
   });

   //5.计算系统奖励
   //5.1.获取订单数
   CompletableFuture<Long> orderNumCompletableFuture = CompletableFuture.supplyAsync(() -> {
      String startTime = new DateTime(orderInfo.getStartServiceTime()).toString("yyyy-MM-dd") + " 00:00:00";
      String endTime = new DateTime(orderInfo.getStartServiceTime()).toString("yyyy-MM-dd") + " 24:00:00";
      Long orderNum = orderInfoFeignClient.getOrderNumByTime(startTime, endTime).getData();
      return orderNum;
   }, threadPoolExecutor);
   //5.2.封装参数
   CompletableFuture<RewardRuleResponseVo> rewardRuleResponseVoCompletableFuture = orderNumCompletableFuture.thenApplyAsync((orderNum)->{
      RewardRuleRequestForm rewardRuleRequestForm = new RewardRuleRequestForm();
      rewardRuleRequestForm.setStartTime(orderInfo.getStartServiceTime());
      rewardRuleRequestForm.setOrderNum(orderNum);
      //5.3.执行
      RewardRuleResponseVo rewardRuleResponseVo = rewardRuleFeignClient.calculateOrderRewardFee(rewardRuleRequestForm).getData();
      log.info("结束代驾，系统奖励：{}", JSON.toJSONString(rewardRuleResponseVo));
      return rewardRuleResponseVo;
   });

   //6.计算分账信息
   CompletableFuture<ProfitsharingRuleResponseVo> profitsharingRuleResponseVoCompletableFuture = feeRuleResponseVoCompletableFuture.thenCombineAsync(orderNumCompletableFuture, (feeRuleResponseVo, orderNum)->{
      ProfitsharingRuleRequestForm profitsharingRuleRequestForm = new ProfitsharingRuleRequestForm();
      profitsharingRuleRequestForm.setOrderAmount(feeRuleResponseVo.getTotalAmount());
      profitsharingRuleRequestForm.setOrderNum(orderNum);
      ProfitsharingRuleResponseVo profitsharingRuleResponseVo = profitsharingRuleFeignClient.calculateOrderProfitsharingFee(profitsharingRuleRequestForm).getData();
      log.info("结束代驾，分账信息：{}", JSON.toJSONString(profitsharingRuleResponseVo));
      return profitsharingRuleResponseVo;
   });
   CompletableFuture.allOf(orderInfoCompletableFuture,
         realDistanceCompletableFuture,
         feeRuleResponseVoCompletableFuture,
         orderNumCompletableFuture,
         rewardRuleResponseVoCompletableFuture,
         profitsharingRuleResponseVoCompletableFuture
   ).join();

   //获取执行结果
   BigDecimal realDistance = realDistanceCompletableFuture.get();
   FeeRuleResponseVo feeRuleResponseVo = feeRuleResponseVoCompletableFuture.get();
   RewardRuleResponseVo rewardRuleResponseVo = rewardRuleResponseVoCompletableFuture.get();
   ProfitsharingRuleResponseVo profitsharingRuleResponseVo = profitsharingRuleResponseVoCompletableFuture.get();

   //7.封装更新订单账单相关实体对象
   UpdateOrderBillForm updateOrderBillForm = new UpdateOrderBillForm();
   updateOrderBillForm.setOrderId(orderFeeForm.getOrderId());
   updateOrderBillForm.setDriverId(orderFeeForm.getDriverId());
   //路桥费、停车费、其他费用
   updateOrderBillForm.setTollFee(orderFeeForm.getTollFee());
   updateOrderBillForm.setParkingFee(orderFeeForm.getParkingFee());
   updateOrderBillForm.setOtherFee(orderFeeForm.getOtherFee());
   //乘客好处费
   updateOrderBillForm.setFavourFee(orderInfo.getFavourFee());

   //实际里程
   updateOrderBillForm.setRealDistance(realDistance);
   //订单奖励信息
   BeanUtils.copyProperties(rewardRuleResponseVo, updateOrderBillForm);
   //代驾费用信息
   BeanUtils.copyProperties(feeRuleResponseVo, updateOrderBillForm);

   //分账相关信息
   BeanUtils.copyProperties(profitsharingRuleResponseVo, updateOrderBillForm);
   updateOrderBillForm.setProfitsharingRuleId(profitsharingRuleResponseVo.getProfitsharingRuleId());
   log.info("结束代驾，更新账单信息：{}", JSON.toJSONString(updateOrderBillForm));

   //8.结束代驾更新账单
   orderInfoFeignClient.endDrive(updateOrderBillForm);
   return true;
}
```



# 分布式事务

## 一、分布式事务Seata

### 1、事务回顾

#### 1.1、什么是事务

**提供一种“要么什么都不做，要么做全套（All or Nothing）”机制。**

#### 1.2、事务的作用

**保证数据一致性**

#### 1.3、事务ACID四大特性

**A：原子性(Atomicity)**

一个事务(transaction)中的所有操作，要么全部完成，要么全部不完成，不会结束在中间某个环节。事务在执行过程中发生错误，会被回滚（Rollback）到事务开始前的状态，就像这个事务从来没有执行过一样。

**C：一致性(Consistency)**

事务的一致性指的是在一个事务执行之前和执行之后数据库都必须处于一致性状态。

如果事务成功地完成，那么系统中所有变化将正确地应用，系统处于有效状态。

如果在事务中出现错误，那么系统中的所有变化将自动地回滚，系统返回到原始状态。

**I：隔离性(Isolation)**

指的是在并发环境中，当不同的事务同时操纵相同的数据时，每个事务都有各自的完整数据空间。由并发事务所做的修改必须与任何其他并发事务所做的修改隔离。事务查看数据更新时，数据所处的状态要么是另一事务修改它之前的状态，要么是另一事务修改它之后的状态，事务不会查看到中间状态的数据。

**D：持久性(Durability)**

指的是只要事务成功结束，它对数据库所做的更新就必须保存下来。即使发生系统崩溃，重新启动数据库系统后，数据库还能恢复到事务成功结束时的状态。

#### 1.4、事务的并发问题

**脏读****：** ​

事务A读取了事务B更新的数据，事务B未提交并回滚数据，那么A读取到的数据是脏数据

**不可重复读****：** ​

事务 A 多次读取同一数据，事务 B 在事务A多次读取的过程中，对数据作了更新并提交，导致事务A多次读取同一数据时，结果 不一致。

**幻读****：** ​

系统管理员A将数据库中所有学生的成绩从具体分数改为ABCDE等级，但是系统管理员B就在这个时候插入了一条具体分数的记录，当系统管理员A更改结束后发现还有一条记录没有改过来，就好像发生了幻觉一样，这就叫幻读。

　　小结：不可重复读的和幻读很容易混淆，不可重复读侧重于修改，幻读侧重于新增或删除。解决不可重复读的问题只需锁住满足条件的行，解决幻读需要锁表。

#### 1.5、MySQL事务隔离级别

| 事务隔离级别&#xA;                 | 脏读&#xA; | 不可重复读&#xA; | 幻读&#xA; |
| --------------------------------- | --------- | --------------- | --------- |
| 读未提交（read-uncommitted）&#xA; | √&#xA;    | √&#xA;          | √&#xA;    |
| 读已提交（read-committed）&#xA;   | ×&#xA;    | √&#xA;          | √&#xA;    |
| 可重复读（repeatable-read）&#xA;  | ×&#xA;    | ×&#xA;          | √&#xA;    |
| 串行化（serializable）&#xA;       | ×&#xA;    | ×&#xA;          | ×&#xA;    |

mysql默认的事务隔离级别为repeatable-read

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_DBSapxVIGA-1724146881535-18.png)

#### 1.6、事务传播行为（propagation behavior）

指的就是当一个事务方法被另一个事务方法调用时，这个事务方法应该如何进行。 
例如：methodA事务方法调用methodB事务方法时，methodB是继续在调用者methodA的事务中运行呢，还是为自己开启一个新事务运行，这就是由methodB的事务传播行为决定的。

Spring定义了七种传播行为：参考 TransactionDefinition类

| **事务传播行为类型**             | **说明**                                                     |
| -------------------------------- | ------------------------------------------------------------ |
| PROPAGATION\_REQUIRED&#xA;       | 如果当前没有事务，就新建一个事务，如果已经存在一个事务中，加入到这个事务中。默认&#xA; |
| PROPAGATION\_SUPPORTS&#xA;       | 支持当前事务，如果当前没有事务，就以非事务方式执行&#xA;      |
| PROPAGATION\_MANDATORY&#xA;      | 使用当前的事务，如果当前没有事务，就抛出异常。&#xA;          |
| PROPAGATION\_REQUIRES\_NEW&#xA;  | 新建事务，如果当前存在事务，把当前事务挂起。（一个新的事务将启动，而且如果有一个现有的事务在运行的话，则这个方法将在运行期被挂起，直到新的事务提交或者回滚才恢复执行）&#xA; |
| PROPAGATION\_NOT\_SUPPORTED&#xA; | 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。&#xA; |
| PROPAGATION\_NEVER&#xA;          | 以非事务方式执行，如果当前存在事务，则抛出异常。&#xA;        |
| PROPAGATION\_NESTED&#xA;         | 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则执行与PROPAGATION\_REQUIRED类似的操作。（外层事务抛出异常回滚，那么内层事务必须回滚，反之内层事务并不影响外层事务）&#xA; |

#### 1.7、本地事务

本地事务也称为\*数据库事务\*或\*传统事务\*（相对于分布式事务而言）。它的执行模式就是常见的：

| **1. transaction begin****2. insert/delete/update****3. insert/delete/update****4. ...****5. transaction commit/rollback** |
| ------------------------------------------------------------ |

- 本地事务有这么几个特征:
  - 一次事务只连接一个支持事务的数据库（一般来说都是关系型数据库）
  - 事务的执行结果保证ACID
  - 会用到数据库锁
- 起初，事务仅限于对单一数据库资源的访问控制，架构服务化以后，事务的概念延伸到了服务中。倘若将一个单一的服务操作作为一个事务，那么整个服务操作只能涉及一个单一的数据库资源，这类**基于**单个服务单一数据库**资源访问的事务，被称为本地事务(Local Transaction)**。

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_1_2bmRgG83FM-1724146881535-19.png)



### 2、分布式事务

#### 2.1、微服务分布式事务问题

- 首先，传统的**单体应用（Monolithic App）**，通过 3 个 Module，在同一个数据源上更新数据来完成一项业务。很自然的，整个业务过程的**数据一致性**由**本地事务**来保证。

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_2_ZS5EDNceeo-1724146881535-20.png)

- 随着业务需求和架构的变化，**单体应用被拆分为微服务**：原来的 3 个 Module 被拆分为 3 个独立的服务，分别使用独立的数据源。业务过程将由 3 个服务的调用来完成。

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_3_uRp3PBs3Ka-1724146881535-21.png)

- 此时，每一个服务内部的数据一致性仍由本地事务来保证。而整个业务层面的全局数据一致性要如何保障呢？这就是微服务架构下面临的，典型的分布式事务需求：我们需要一个分布式事务的解决方案保障业务全局的数据一致性。

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_4_l0qm0VIoEj-1724146881535-22.png)

#### 2.2、什么是分布式事务

分布式事务指事务的参与者、支持事务的服务器、资源服务器以及事务管理器分别位于不同的分布式系统的不同节点之上。

指一次大的操作由不同的小操作组成的，这些小的操作分布在不同的服务器上，分布式事务需要保证这些小操作要么全部成功，要么全部失败。

本质上来说，分布式事务就是为了保证不同数据库的数据一致性。

#### 2.3、什么是分布式系统

- 部署在不同节点上的系统通过网络交互来完成协同工作的系统。
- 比如：
  - 充值加积分的业务，用户在充值系统向自己的账户充钱，在积分系统中自己积分相应的增加。
  - 充值系统和积分系统是两个不同的系统，一次充值加积分的业务就需要这两个系统协同工作来完成。

#### 2.4、分布式事务应用在哪些场景

- 电商系统中的下单扣库存

电商系统中，**订单系统**和**库存系统**是两个系统，一次下单的操作由两个系统协同完成

- 金融系统中的银行卡充值

在金融系统中通过银行卡向平台充值需要通过**银行系统**和**金融系统**协同完成。

- 教育系统中下单选课业务

在线教育系统中，用户购买课程，下单支付成功后学生选课成功，此事务由**订单系统**和**选课系统**协同完成。

- SNS系统的消息发送

在社交系统中发送站内消息同时发送手机短信，一次消息发送由**站内消息系统**和**手机通信系统**协同完成。

#### 2.5、跨多服务多数据库的分布式事务

一个服务操作调用另一个服务，这时事务需要跨越多个服务。在这种情况下，起始服务的事务在调用另外一个服务的时候，需要以某种机制流转到另外一个服务，从而使被调用的服务访问的资源也自动加入到该事务当中来。这就需要跨服务跨数据库的全局事务进行数据一致性的保证。

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_5_t3E5cx2soD-1724146881536-25.png)

较之基于单一数据库资源访问的本地事务，分布式事务的应用架构更为复杂。在不同的分布式应用架构下，实现一个分布式事务要考虑的问题并不完全一样，比如对多资源的协调、事务的跨服务传播等，实现机制也是复杂多变。



### 3、分布式事务解决方案

1.XA两段提交(低效率)-分布式事务解决方案

2.TCC三段提交(2段,高效率\[不推荐(补偿代码)])

3.本地消息(MQ+Table)

4.Seata(alibaba)

#### 3.1、基于XA协议的两阶段提交(2PC)

X/Open 组织（即现在的 Open Group ）定义了分布式事务处理模型

XA协议：XA是一个分布式事务协议。XA中大致分为两部分：**事务管理器**和**本地资源管理器**。其中本地资源管理器往往由数据库实现，比如Oracle、DB2这些商业数据库都实现了XA接口，而事务管理器作为全局的调度者，负责各个本地资源的提交和回滚。

##### 3.1.1、概念

二阶段提交2PC（Two phase Commit）是指，在分布式系统里，为了保证所有节点在进行事务提交时保持一致性的一种算法。

##### 3.1.2、背景

在分布式系统里，\*\*每个节点都可以知晓自己操作的成功或者失败，却无法知道其他节点操作的成功或失败。\*\*

当一个事务跨多个节点时，为了保持事务的原子性与一致性，需要引入一个\*\*协调者\*\*（Coordinator）来统一掌控所有\*\*参与者\*\*（Participant）的操作结果，并指示它们是否要把操作结果进行真正的提交（commit）或者回滚（rollback）。

##### 3.1.3、思路

2PC顾名思义分为两个阶段，其实施思路可概括为：

（1）投票阶段（voting phase）：参与者将操作结果通知协调者；

（2）提交阶段（commit phase）：收到参与者的通知后，协调者再向参与者发出通知，根据反馈情况决定各参与者是否要提交还是回滚；

##### 3.1.4、缺陷

算法执行过程中，\*\*所有节点都处于阻塞状态，所有节点所持有的资源（例如数据库数据，本地文件等）都处于封锁状态。\*\*

典型场景为：

（1）某一个参与者发出通知之前，所有参与者以及协调者都处于阻塞状态；

（2）在协调者发出通知之前，所有参与者都处于阻塞状态；

另外，如有协调者或者某个参与者出现了崩溃，为了避免整个算法处于一个完全阻塞状态，往往需要借助超时机制来将算法继续向前推进，故此时算法的效率比较低。

总的来说，\*\*2PC是一种比较保守的算法\*\*。

##### 3.1.5、举例

甲乙丙丁四人要组织一个会议，需要确定会议时间，不妨设甲是协调者，乙丙丁是参与者。

**投票阶段：**

（1）甲发邮件给乙丙丁，周二十点开会是否有时间；

（2）甲回复有时间；

（3）乙回复有时间；

（4）丙迟迟不回复，此时对于这个活动，甲乙丙均处于阻塞状态，算法无法继续进行；

（5）丙回复有时间（或者没有时间）；

**提交阶段：**

（1）协调者甲将收集到的结果反馈给乙丙丁（什么时候反馈，以及反馈结果如何，在此例中取决与丙的时间与决定）；

（2）乙收到；

（3）丙收到；

（4）丁收到；

##### 3.1.6、结论

2PC效率很低，分布式事务很难做

##### 3.1.7、实际应用交互流程

###### 2PC两阶段提交的正向流程

**第一阶段：**

2PC中包含着两个角色：\*\*事务协调者\*\*和\*\*事务参与者\*\*。让我们来看一看他们之间的交互流程：

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_6_5fJBL98sLM-1724146881536-23.png)

在分布式事务的第一阶段，作为事务协调者的节点会首先向所有的参与者节点发送Prepare请求。

在接到Prepare请求之后，每一个参与者节点会各自执行与事务有关的数据更新，写入Undo Log和Redo Log。如果参与者执行成功，暂时不提交事务，而是向事务协调节点返回“完成”消息。

当事务协调者接到了所有参与者的返回消息，整个分布式事务将会进入第二阶段。

**第二阶段：**

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_7_31p4P-FIYl-1724146881536-24.png)

在2PC分布式事务的第二阶段，如果事务协调节点在之前所收到都是正向返回，那么它将会向所有事务参与者发出Commit请求。

接到Commit请求之后，事务参与者节点会各自进行本地的事务提交，并释放锁资源。当本地事务完成提交后，将会向事务协调者返回“完成”消息。

当事务协调者接收到所有事务参与者的“完成”反馈，整个分布式事务完成。

###### 失败情况的处理流程

**第一阶段：**

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_8_Ueo-7XP2YE-1724146881536-26.png)

**第二阶段：**

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_9_XSy1jA8dpt-1724146881536-28.png)

在2PC的第一阶段，如果某个事务参与者反馈失败消息，说明该节点的本地事务执行不成功，必须回滚。

于是在第二阶段，事务协调节点向所有的事务参与者发送Abort(中止)请求。接收到Abort请求之后，各个事务参与者节点需要在本地进行事务的回滚操作，回滚操作依照Undo Log来进行。

以上就是2PC两阶段提交协议的详细过程。

###### 2PC两阶段提交究竟有哪些不足呢？

1. **性能问题**

2PC遵循强一致性。在事务执行过程中，各个节点占用着数据库资源，只有当所有节点准备完毕，事务协调者才会通知提交，参与者提交后释放资源。这样的过程有着非常明显的性能问题。

1. **协调者单点故障问题**

2PC模型的核心，一旦事务协调者节点挂掉，参与者收不到提交或是回滚通知，参与者会一直处于中间状态无法完成事务。

1. **丢失消息导致的不一致问题。**

第二个阶段，如果发生局部网络问题，一部分事务参与者收到了提交消息，另一部分事务参与者没收到提交消息，那么就导致了节点之间数据的不一致。



#### 3.2、代码补偿事务(TCC）

TCC的作用主要是解决跨服务调用场景下的分布式事务问题

##### 3.2.1、场景案例

以航班预定的案例，来介绍TCC要解决的事务场景。在这里笔者虚构一个场景，把自己当做航班预定的主人公，来介绍这个案例。从合肥 –> 昆明 –> 大理。

准备从合肥出发，到云南大理去游玩，然后使用美团App(机票代理商)来订机票。发现没有从合肥直达大理的航班，需要到昆明进行中转。如下图：

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_10_veUMpNEaN8-1724146881536-29.png)

从图中我们可以看出来，从合肥到昆明乘坐的是四川航空，从昆明到大理乘坐的是东方航空。

 由于使用的是美团App预定，当我选择了这种航班预定方案后，美团App要去四川航空和东方航空各帮我购买一张票。如下图：

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_11_dorvIpjgPi-1724146881536-27.png)

考虑最简单的情况：美团先去川航帮我买票，如果买不到，那么东航也没必要买了。如果川航购买成功，再去东航购买另一张票。

 现在问题来了：假设美团先从川航成功买到了票，然后去东航买票的时候，因为天气问题，东航航班被取消了。那么此时，美团必须取消川航的票，因为只有一张票是没用的，不取消就是浪费我的钱。那么如果取消会怎样呢？如果读者有取消机票经历的话，非正常退票，肯定要扣手续费的。在这里，川航本来已经购买成功，现在因为东航的原因要退川航的票，川航应该是要扣代理商的钱的。

 那么美团就要保证，如果任一航班购买失败，都不能扣钱，怎么做呢？

 两个航空公司都为美团提供以下3个接口：**机票预留接口、确认接口、取消接口**。美团App分2个阶段进行调用，如下所示：

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_12_69i9kjZ-Mo-1724146881536-30.png)

**在第1阶段：**

美团分别请求两个航空公司预留机票，两个航空公司分别告诉美团预留成功还是失败。航空公司需要保证，机票预留成功的话，之后一定能购买到。

**在第2阶段：**

如果两个航空公司都预留成功，则分别向两个公司发送确认购买请求。

如果两个航空公司任意一个预留失败，则对于预留成功的航空公司也要取消预留。这种情况下，对于之前预留成功机票的航班取消，也不会扣用户的钱，因为购买并没实际发生，之前只是请求预留机票而已。

通过这种方案，可以保证两个航空公司购买机票的一致性，要不都成功，要不都失败，即使失败也不会扣用户的钱。如果在两个航班都已经已经确认购买后，再退票，那肯定还是要扣钱的。

当然，实际情况肯定这里提到的肯定要复杂，通常航空公司在第一阶段，对于预留的机票，会要求在指定的时间必须确认购买(支付成功)，如果没有及时确认购买，会自动取消。假设川航要求10分钟内支付成功，东航要求30分钟内支付成功。以较短的时间算，如果用户在10分钟内支付成功的话，那么美团会向两个航空公司都发送确认购买的请求，如果超过10分钟(以较短的时间为准)，那么就不能进行支付。

这个方案提供给我们一种跨服务保证事务一致性的一种解决思路，可以把这种方案当做TCC的雏形。

具体流程：

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_13_eeaoEFRsEj-1724146881536-31.png)

TCC是Try ( 尝试 ) — Confirm(确认) — Cancel ( 取消 ) 的简称:

| **操作方法** | **含义**                                                     |
| ------------ | ------------------------------------------------------------ |
| Try&#xA;     | 完成所有业务检查（一致性），预留业务资源(准隔离性) 回顾上面航班预定案例的阶段1，机票就是业务资源，所有的资源提供者(航空公司)预留都成功，try阶段才算成功&#xA; |
| Confirm&#xA; | 确认执行业务操作，不做任何业务检查， 只使用Try阶段预留的业务资源。回顾上面航班预定案例的阶段2，美团APP确认两个航空公司机票都预留成功，因此向两个航空公司分别发送确认购买的请求。&#xA; |
| Cancel&#xA;  | 取消Try阶段预留的业务资源。回顾上面航班预定案例的阶段2，如果某个业务方的业务资源没有预留成功，则取消所有业务资源预留请求。&#xA; |

##### 3.2.2、TCC两阶段提交与XA两阶段提交的区别

 XA是资源层面的分布式事务，强一致性，在两阶段提交的整个过程中，一直会持有资源的锁。

TCC是业务层面的分布式事务，最终一致性，不会一直持有资源的锁。

其核心在于将业务分为两个操作步骤完成。不依赖 RM 对分布式事务的支持，而是通过对业务逻辑的分解来实现分布式事务。



#### 3.3、本地消息表（异步确保）- 事务最终一致性

这种实现方式的思路，其实是源于 ebay，后来通过支付宝等公司的布道，在业内广泛使用。\*\*其基本的设计思想是将远程分布式事务拆分成一系列的本地事务\*\*。如果不考虑性能及设计优雅，借助关系型数据库中的表即可实现。

- 订单系统新增一条消息表，将新增订单和新增消息放到一个事务里完成，然后通过轮询的方式去查询消息表，将消息推送到 MQ，库存系统去消费 MQ。

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_14_QGUcikslqc-1724146881536-33.png)

- 执行流程：
  - 订单系统，添加一条订单和一条消息，在一个事务里提交。
  - 订单系统，使用定时任务轮询查询状态为未同步的消息表，发送到 MQ，如果发送失败，就重试发送。
  - 库存系统，接收 MQ 消息，修改库存表，需要保证幂等操作。
  - 如果修改成功，调用 RPC 接口修改订单系统消息表的状态为已完成或者直接删除这条消息。
  - 如果修改失败，可以不做处理，等待重试。
- 订单系统中的消息有可能由于业务问题会一直重复发送，所以为了避免这种情况可以记录一下发送次数，当达到次数限制之后报警，人工接入处理；库存系统需要保证幂等，避免同一条消息被多次消费造成数据一致。
- 本地消息表这种方案实现了最终一致性，需要在业务系统里增加消息表，业务逻辑中多一次插入的 DB 操作，所以性能会有损耗，而且最终一致性的间隔主要由定时任务的间隔时间决定。
- 优点： 一种非常经典的实现，避免了分布式事务，实现了最终一致性。
- 缺点： 消息表会耦合到业务系统中，如果没有封装好的解决方案，会有很多杂活需要处理。



#### 3.4、Seata介绍

[http://seata.io/zh-cn/](http://seata.io/zh-cn/ "http://seata.io/zh-cn/")

**Seata**是阿里开源的一个分布式事务框架，能够让大家在操作分布式事务时，像操作本地事务一样简单。一个注解搞定分布式事务。

**解决分布式事务问题，有两个设计初衷**

- **对业务无侵入**：即减少技术架构上的微服务化所带来的分布式事务问题对业务的侵入
- **高性能**：减少分布式事务解决方案所带来的性能消耗

**Seata中有两种分布式事务实现方案，AT及TCC**

- AT模式主要关注多 DB 访问的数据一致性，当然也包括多服务下的多 DB 数据访问一致性问题 2PC-改进
- TCC 模式主要关注业务拆分，在按照业务横向扩展资源时，解决微服务间调用的一致性问题

**那 Seata 是怎么做到的呢？下面说说它的各个模块之间的关系。**

Seata 的设计思路是将一个分布式事务可以理解成一个全局事务，下面挂了若干个分支事务，而一个分支事务是一个满足 ACID 的本地事务，因此我们可以操作分布式事务像操作本地事务一样。

2019 年 1 月，**阿里**巴巴中间件团队发起了开源项目 [*Fescar*](https://www.oschina.net/p/fescar "Fescar")（Fast & EaSy Commit And Rollback），和社区一起共建开源分布式事务解决方案。Fescar 的愿景是让分布式事务的使用像本地事务的使用一样，简单和高效，并逐步解决开发者们遇到的分布式事务方面的所有难题。

Seata全称：Simple Extensible Autonomous Transaction Architecture,简单可扩展自治事务框架。

##### 3.4.1、AT模式（Automatic (Branch) Transaction Mode）

- **Transaction Coordinator （TC）：** 事务协调器，**维护全局事务的运行状态**，负责协调并决定全局事务的提交或回滚。
- **Transaction Manager（TM）：** 控制全局事务的边界，负责开启一个全局事务，并最终发起**全局提交**或**全局回滚**的决议。
- **Resource Manager （RM）：**资源管理器，负责本地事务的注册，本地事务状态的汇报(投票)，并且**负责本地事务的提交和回滚**。
- **XID：** 一个全局事务的唯一标识

其中，TM是一个分布式事务的发起者和终结者，TC负责维护分布式事务的运行状态，而RM则负责本地事务的运行。

如下图所示：

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_16_yy7mgWiiFD-1724146881536-34.png)

下面是一个分布式事务在Seata中的执行流程：

1. TM 向 TC 申请开启一个全局事务，全局事务创建成功并生成一个全局唯一的 XID
2. XID 在微服务调用链路的上下文中传播。
3. RM 向 TC 注册分支事务，接着执行这个分支事务并提交（重点：RM在第一阶段就已经执行了本地事务的提交/回滚），最后将执行结果汇报给TC
4. TM 根据 TC 中所有的分支事务的执行情况，发起全局提交或回滚决议。
5. TC 调度 XID 下管辖的全部分支事务完成提交或回滚请求。

Seata 中有三大模块，分别是 TM、RM 和 TC。 其中 TM 和 RM 是作为 Seata 的客户端与业务系统集成在一起，TC 作为 Seata 的服务端独立部署。




##### 3.4.2 MT模式（Manual (Branch) Transaction Mode）

Seata还支持MT模式。MT模式本质上是一种TCC方案，业务逻辑需要被拆分为 Prepare/Commit/Rollback 3 部分，形成一个 MT 分支，加入全局事务。如图所示：

![](https://github.com/xqboot/daijia-parent/blob/main/images\tcc-1724146881536-32.png)

MT 模式一方面是 AT 模式的补充。另外，更重要的价值在于，通过 MT 模式可以把众多非事务性资源纳入全局事务的管理中。



### 4、Seata之原理简介

#### 4.1、再看TC/TM/RM三大组件

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_35_UAj1MYO6FQ-1724146881536-36.png)

#### 4.2、分布式事务的执行流程

- TM开启分布式事务(TM向TC注册全局事务记录)
- 换业务场景，编排数据库，服务等事务内资源（RM向TC汇报资源准备状态）
- TM结束分布式事务，事务一阶段结束（TM通知TC提交/回滚分布式事务）
- TC汇总事务信息，决定分布式事务是提交还是回滚
- TC通知所有RM提交/回滚资源，事务二阶段结束。

#### 4.3、AT模式如何做到对业务的无侵入

##### 4.3.1、一阶段加载

- 在一阶段，Seata会拦截"业务SQL"
  - 1.解析SQL语义，找到“业务SQL”要更新的业务数据，在业务数据被更新前，将其保存成“before image”
  - 2.执行"业务SQL"更新业务数据，在业务数据更新之后，
  - 3.其保存成“after image”，最后生成行锁。
- 以上操作全部在一个数据库事务内完成，这样保证了一阶段操作的原子性。

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_38_EB06uJVw2I-1724146881536-35.png)

##### 4.3.2、二阶段提交

- 二阶段如果顺利提交的话，因为“业务SQL”在一阶段已经提交至数据库，所以Seata框架只需将一阶段保存的快照数据和行锁删掉，完成数据清理即可。

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_39_5lNHgn0ybu-1724146881536-37.png)

##### 4.3.3、二阶段回滚

二阶段如果是回滚的话，Seata就需要回滚一阶段执行的“业务SQL”,还原业务数据。

回滚方式便是用“before image”还原业务数据；但在还原前要首先校验脏写，对比“数据库当前业务数据”和“after image”,如果两份数据完全一致就说明没有脏写，可以还原业务数据，如果不一致就说明有脏写，出现脏写就需要转人工处理。

![](https://github.com/xqboot/daijia-parent/blob/main/images\image_40_Y67Xy72vPH-1724146881536-39.png)



## 二、使用Seata添加分布式事务

项目中很多功能都可以添加分布式事务。

比如，支付后处理，首先更新订单状态，之后要获取系统奖励添加到司机账户，这两件事情保证数据一致性，两件事情在不同的模块中，可以使用分布式事务 

再比如，乘客下单要做很多事情，但是我们只需要关注保存订单信息与任务调度开启，这两件事件必须保证强一致性，只要乘客下单了，任务调度就必须开启，要么都成功，要么都回滚。

下面我们就引入Seata来解决问题。

### 1 安装和启动Seata服务

* Seata可以在Linux操作系统中使用docker安装
* **Seata也可以安装在windows系统（测试）**

**到Seata官网下载安装文件**

![image-20240320093120065](https://github.com/xqboot/daijia-parent/blob/main/images\image-20240320093120065-1724146881537-40.png)

![image-20240320093633784](https://github.com/xqboot/daijia-parent/blob/main/images\image-20240320093633784-1724146881536-38.png)

### 2 在使用Seata的相关模块引入依赖

* 结合实际，在service-payment、service-order和service-driver模块引入依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
</dependency>
<dependency>
    <groupId>io.seata</groupId>
    <artifactId>seata-spring-boot-starter</artifactId>
    <version>1.7.1</version>
</dependency>
```



### 3 在使用Seata的相关模块添加配置

* 结合实际，在service-payment、service-order和service-driver模块添加
* 添加到bootstrap.properties里面

```properties
seata.tx-service-group=tingshu-tx-group
seata.service.vgroup-mapping.tingshu-tx-group=default
seata.service.grouplist.default=127.0.0.1:8091
```



### 4 在业务方法添加注解

```java
@GlobalTransactional
```











