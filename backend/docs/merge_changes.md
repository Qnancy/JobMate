力求鱼
我把你的实体类和我的认证功能合并了
更改的：
1. Company类缺少@Entity注解，编译会报错，我加了@Entity和@Table(name = "companies")
2. ActivityInfo表名你写的是@Table(name = "job_info")，我改成了@Table(name = "activity_info")
3. User类我把我之前写的完整User实现合并进去了，包括UserDetails接口
包结构变化：
我的代码原来在entity包，现在都移到你的models包了，所有import都更新了
新增的配置：
为了让Spring Security正常工作，加了几个配置类：SecurityConfig、CustomUserDetailsService、几个认证处理器、PasswordConfig。pom.xml里加了spring-boot-starter-security依赖。
现在
后端跑在8080端口能正常启动，前端跑在5173端口界面正常，数据库自动建表包括你设计的companies、job_info、activity_info，用户认证用了Spring Security标准流程密码BCrypt加密。
然后我push到我的婉贞姐分支了
我是婉贞姐我是婉贞姐我是婉贞姐我是婉贞姐我是婉贞姐我是婉贞姐我是婉贞姐我是婉贞姐我是婉贞姐我是婉贞姐我是婉贞姐我是婉贞姐