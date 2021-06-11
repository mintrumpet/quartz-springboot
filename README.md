# quartz-springboot

quartz结合springboot，实现对quartz资源的管理，使用mysql作为数据源，做到对任务进行持久化处理，以及动态对任务进行编辑、更改、删除等操作。

解决spring-scheduled所带来的无法持久化的问题。

## 关键类

- QuartzConfiguration：配置quartz相关bean，包括任务工厂、SpringBeanJobFactory等
- TriggerFactory：用于处理trigger和job的实例化
- CustomJob：自定义job，实现逻辑是通过传递的参数，找到对应spring bean，并且执行对应的方法（事务支持）

> 参考：https://tech.meituan.com/2014/08/31/mt-crm-quartz.html