# 贴合10项技术要求的个人空间系统目录树（SpringMVC+MyBatis）
以下目录树深度适配指定的10项技术要求，每个核心文件/目录均对应具体技术要求的落地场景，确保所有要求在工程结构中可落地、可追溯：

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── space/          # 项目主包，承载所有业务逻辑与技术要求落地
│   │   │           ├── controller/ # SpringMVC控制器，落地路径映射、路径传参要求
│   │   │           │   ├── UserController.java       # 处理用户注册登录，包含@PathVariable传参、redirect重定向跳转
│   │   │           │   ├── SpaceController.java      # 空间管理逻辑，接收分页参数、处理多参数路径传递
│   │   │           │   ├── FileController.java       # 核心文件CRUD，适配日期类型路径参数的@DateTimeFormat转换、多参数路径传参
│   │   │           │   ├── VisitorController.java    # 游客浏览逻辑，传递模糊查询参数、处理分页查询请求
│   │   │           │   └── AdminController.java      # 管理员审批操作，实现控制类间redirect重定向、路径传参
│   │   │           ├── service/    # 业务逻辑层，落地分页计算、删除日志记录要求
│   │   │           │   ├── impl/
│   │   │           │   │   ├── UserServiceImpl.java
│   │   │           │   │   ├── SpaceServiceImpl.java
│   │   │           │   │   ├── FileServiceImpl.java  # 封装分页总页数计算逻辑、删除文件后插入操作日志
│   │   │           │   │   ├── VisitorServiceImpl.java # 调用Mapper执行模糊查询、查询分页总记录数
│   │   │           │   │   └── AdminServiceImpl.java
│   │   │           │   ├── UserService.java
│   │   │           │   ├── SpaceService.java
│   │   │           │   ├── FileService.java
│   │   │           │   ├── VisitorService.java
│   │   │           │   └── AdminService.java
│   │   │           ├── mapper/     # MyBatis映射接口，落地模糊查询、分页、多表查询、主键回查要求
│   │   │           │   ├── UserMapper.java
│   │   │           │   ├── SpaceMapper.java
│   │   │           │   ├── FileMapper.java           # 定义模糊查询、分页查询、多表联查接口
│   │   │           │   ├── FileCategoryMapper.java   # 提供分类数据查询接口，支撑动态控件生成
│   │   │           │   ├── FollowMapper.java
│   │   │           │   ├── AdminMapper.java
│   │   │           │   └── OperateLogMapper.java     # 定义操作日志的增删改查接口
│   │   │           ├── model/      # 实体类，落地日期处理、图片路径存储、日志实体定义要求
│   │   │           │   ├── User.java                 # 注册日期字段添加@DateTimeFormat注解、定义主键回显属性
│   │   │           │   ├── Space.java
│   │   │           │   ├── File.java                 # 存储图片相对路径属性、日期字段添加@DateTimeFormat注解
│   │   │           │   ├── FileCategory.java         # 分类实体，支撑动态控件数据源
│   │   │           │   ├── Follow.java
│   │   │           │   ├── AuditRecord.java
│   │   │           │   ├── Notification.java
│   │   │           │   └── OperateLog.java           # 定义删除日志实体，包含操作时间、类型、参数、描述字段
│   │   │           ├── interceptor/ # 拦截器，可选统一记录删除操作日志
│   │   │           │   ├── LoginInterceptor.java
│   │   │           │   ├── AdminAuthInterceptor.java
│   │   │           │   └── OperateLogInterceptor.java # 拦截删除请求，统一记录操作日志
│   │   │           ├── util/       # 工具类，封装日期处理、分页计算逻辑
│   │   │           │   ├── FileSizeUtil.java
│   │   │           │   ├── WebSocketUtil.java
│   │   │           │   ├── DateUtil.java             # 适配不同日期格式转换，支撑日期类型数据处理
│   │   │           │   ├── PageUtil.java             # 封装分页总页数计算公式，支撑分页查询
│   │   │           │   └── ResultUtil.java
│   │   │           └── exception/  # 自定义异常
│   │   │               ├── SpaceFullException.java
│   │   │               ├── AuthException.java
│   │   │               └── BusinessException.java
│   │   ├── resources/              # 配置文件目录，落地MyBatis配置、日期类型适配
│   │   │   ├── mybatis/            # MyBatis映射XML，落地模糊查询、分页、主键回查、多表查询
│   │   │   │   ├── UserMapper.xml                    # 通过<selectKey>标签回查自增主键、适配日期字段映射
│   │   │   │   ├── SpaceMapper.xml
│   │   │   │   ├── FileMapper.xml                    # 使用CONCAT('%', #{参数名}, '%')实现模糊查询、通过limit实现分页、编写多表联查SQL
│   │   │   │   ├── FileCategoryMapper.xml           # 查询分类数据，支撑动态控件生成
│   │   │   │   ├── FollowMapper.xml
│   │   │   │   ├── AdminMapper.xml
│   │   │   │   └── OperateLogMapper.xml             # 编写日志插入SQL，支撑删除日志记录
│   │   │   ├── spring/             # Spring配置文件，适配日期转换、视图解析
│   │   │   │   ├── spring-mvc.xml           # 配置日期转换格式化器、视图解析器（支撑路径跳转）
│   │   │   │   ├── spring-mybatis.xml       # 配置数据源、Mapper扫描，整合MyBatis
│   │   │   │   └── spring-service.xml       # 配置事务管理，确保删除文件与插入日志的原子性
│   │   │   ├── jdbc.properties
│   │   │   ├── mybatis-config.xml          # 配置实体别名、日期类型处理器，适配数据库与Java日期类型
│   │   │   └── log4j.properties
│   │   └── webapp/                  # Web应用根目录，落地页面跳转、图片预览、删除/更新确认、动态控件
│   │       ├── WEB-INF/
│   │       │   ├── views/           # 前端页面，落地路径写法、图片预览、confirm确认、分页栏、动态控件
│   │       │   │   ├── user/
│   │       │   │   │   ├── register.jsp       # 提供yyyy-MM-dd格式日期输入框、使用../和/实现路径跳转
│   │       │   │   │   └── login.jsp
│   │       │   │   ├── space/
│   │       │   │   │   ├── index.jsp          # 渲染分页栏（包含首页、上一页、页码、下一页、尾页）、通过<img>标签预览图片
│   │       │   │   │   ├── upload.jsp         # 基于多表查询结果动态生成分类下拉框，禁止写死选项
│   │       │   │   │   ├── file-manage.jsp    # 点击删除/更新按钮触发confirm弹窗、渲染分页栏、通过<img>预览图片
│   │       │   │   │   └── space-manage.jsp   # 使用../写法跳上一层目录页面、/写法跳根目录页面
│   │       │   │   ├── visitor/
│   │       │   │   │   ├── hot-file.jsp       # 提供模糊查询表单、渲染分页栏
│   │       │   │   │   └── file-detail.jsp    # 通过<img>标签预览图片、接收路径传递的参数
│   │       │   │   └── admin/
│   │       │   │       ├── file-audit.jsp     # 基于分类数据动态生成单选框、点击更新触发confirm弹窗
│   │       │   │       └── space-audit.jsp    # 渲染分页栏、使用redirect实现控制类间跳转
│   │       │   └── web.xml
│   │       ├── static/              # 静态资源目录，落地图片存储、JS封装confirm弹窗
│   │       │   ├── js/               # 封装confirm弹窗逻辑、分页栏生成、动态控件渲染
│   │       │   │   ├── common.js                   # 封装confirm删除/更新逻辑，仅确认后提交请求
│   │       │   │   ├── file.js                     # 封装图片预览逻辑、动态渲染分类下拉框/单选框
│   │       │   │   └── page.js                     # 封装分页栏生成逻辑，循环生成页码至总页数
│   │       │   ├── css/
│   │       │   └── images/           # 存储图片文件，支撑图片相对路径预览
│   │       └── index.jsp
│   └── test/
│       └── java/
│           └── com/
│               └── space/
├── pom.xml
└── target/
```

## 技术要求落地说明
日期类型数据处理在实体类（如User.java、File.java）中落地，核心日期字段（如注册日期、文件上传日期）均添加@DateTimeFormat注解指定yyyy-MM-dd/yyyy/MM/dd格式，同时在mybatis-config.xml中配置日期类型处理器，确保数据库DATE/DATETIME类型与Java Date类的自动转换无异常，DateUtil工具类则进一步适配不同场景下的日期格式转换需求。

模糊查询的实现集中在MyBatis映射XML文件（如FileMapper.xml）中，所有模糊查询场景均通过CONCAT('%', #{参数名}, '%')拼接通配符，全程使用#{}预编译占位符接收参数，彻底规避${}字符串拼接带来的SQL注入风险，对应的Mapper接口（如FileMapper.java）则定义清晰的模糊查询方法，由Service层调用。

分页查询的核心逻辑拆分到多个层级：MyBatis映射XML中编写COUNT(*)聚合函数SQL获取总记录数，同时通过limit 起始索引, 每页条数实现分页数据查询，起始索引按(当前页-1)*每页条数计算；PageUtil工具类封装总页数计算公式（总记录数%每页条数==0 ? 总记录数/每页条数 : 总记录数/每页条数+1）；前端页面（如hot-file.jsp、file-manage.jsp）则通过JS循环生成包含首页、上一页、页码、下一页、尾页的完整分页栏，页码数匹配计算出的总页数。

路径映射与跳转在控制器和前端页面双向落地：控制器中插入、更新等操作完成后，均通过redirect:/目标控制类映射路径实现控制类间重定向跳转；前端页面则灵活使用../（跳上一层目录）、/（跳根目录）的路径写法，如space-manage.jsp中通过../user/login.jsp返回用户登录页，确保页面跳转路径规范。

路径传参通过控制器（如FileController.java）中的@PathVariable注解实现，路径中用{参数名}占位，注解与参数名严格绑定，针对日期类型的路径参数，额外配合@DateTimeFormat注解完成格式转换，支持1个、2个及多个参数的路径传递场景，如/file/{userId}/{fileId}可同时接收用户ID和文件ID。

图片预览的核心是数据库与前端的配合：File实体类存储图片相对路径（如/images/xxx.jpg），图片文件实际存放于webapp/static/images目录；前端页面（如index.jsp、file-detail.jsp）通过<img>标签的src属性加载该相对路径，直接访问static目录下的静态资源，实现图片/照片的预览显示。

删除/更新操作确认的逻辑封装在static/js/common.js中，所有页面的删除/更新按钮均绑定onclick事件，触发原生confirm()弹窗，仅当用户点击“确认”时才提交请求，点击“取消”则终止操作，如file-manage.jsp中的删除按钮，彻底避免无确认的直接提交风险。

添加数据回查主键的需求在MyBatis映射XML（如UserMapper.xml）中通过<selectKey>标签实现，结合LAST_INSERT_ID()函数，在插入数据后自动将自增主键回显到实体类对应属性（如User的userId），确保新增数据的主键可即时获取，支撑后续关联操作。

多表查询与动态控件的落地分为两步：首先在FileMapper.xml中编写多表联查SQL，一次查询完成分类数据加载与对应业务数据查询；然后前端页面（如upload.jsp、file-audit.jsp）通过JSTL循环标签，基于查询到的分类数据动态生成下拉框、单选按钮/复选框，完全禁止写死选项，确保控件与数据库数据同步。

删除操作日志记录的实现有两种方式：一是在FileServiceImpl.java中删除文件后，直接调用OperateLogMapper的插入方法，将操作时间、操作类型（删除）、操作参数（如删除ID）、操作描述（如删除文件名称）持久化到日志表；二是通过OperateLogInterceptor拦截器统一拦截删除请求，自动记录日志，两种方式均确保所有删除操作可追溯，spring-service.xml中配置的事务管理则保障删除文件与插入日志的原子性。

# 个人空间系统整体要求总结
## 一、核心业务需求
基于SpringMVC+MyBatis技术栈开发个人空间系统，覆盖**用户、普通用户、游客、管理员**四大角色的核心操作，核心规则如下：

### 1. 用户注册（含空间初始化）
- 提交账号、密码、昵称、多格式日期（yyyy-MM-dd/yyyy/MM/dd）完成注册，账号需唯一；
- 注册成功后自动为用户开辟5MB（5*1024*1024字节）默认个人空间，创建以用户ID命名的物理目录，初始化Space表关联记录，失败则回滚注册操作。

### 2. 普通用户-个人空间操作
- **文件上传**：支持文档/图片/相册/音乐类型，上传前校验剩余空间，不足则禁止上传，文件关联分类且状态为“待审核”；
- **文件管理**：对已审核文件实现增删改查、冻结/解冻，删除为逻辑删除且需记录操作日志，支持文件名模糊查询、分页展示；
- **空间管理**：查看容量使用情况，提交扩容申请（待管理员审批）；
- **置顶操作**：对已审核文件设置/取消置顶，置顶文件列表优先展示；
- **在线预览/播放**：图片支持轮播预览，音乐支持试听，文档支持在线预览，仅展示已审核文件。

### 3. 游客-浏览者操作
- **热点信息浏览**：按下载量排序展示已审核的公开文件，支持模糊查询、分页展示；
- **下载量统计**：文件下载后累计下载量，同一IP短时间重复下载仅统计1次，详情页展示实时下载量；
- **关注功能**：通过WebSocket实现关注创作者，创作者上传新文件（审核通过）时，关注者实时接收通知。

### 4. 管理员操作
- **文件审批**：分页查看待审核文件，支持通过/驳回/冻结操作，驳回需填写原因，操作前需确认；
- **空间扩容审批**：按用户文件总下载量审批扩容申请（下载量越高额度越大），分页查看申请列表，支持通过/驳回操作。

## 二、10项技术要求（独立Util封装+SpringMVC+MyBatis落地）
将10项技术要求按“单一职责”封装为独立工具类（Util），统一置于`com.space.util`包下，适配SpringMVC+MyBatis特性，核心目标与落地形式如下：

| 技术要求                | 核心目标                                                                 | 封装工具类          | 核心落地场景                                                                 |
|-------------------------|--------------------------------------------------------------------------|---------------------|------------------------------------------------------------------------------|
| 日期类型数据处理        | 适配数据库DATE/DATETIME与Java Date转换，支持多格式日期解析/格式化        | DateFormatUtil      | 用户注册日期解析、文件上传时间格式化、日期参数绑定                            |
| 模糊查询实现            | 用CONCAT+#{}实现模糊查询（防SQL注入），支持前后/前缀/后缀匹配            | FuzzyQueryUtil      | 文件名模糊查询、热点文件前缀匹配                                             |
| 分页查询实现            | 封装总页数/起始索引计算、分页栏页码生成、分页结果封装                    | PageUtil            | 文件管理分页、审核列表分页、热点信息分页                                     |
| 路径映射与跳转          | 封装redirect重定向、页面相对路径转换，统一路径规则                      | PathJumpUtil        | 注册/删除后重定向、页面上下级目录跳转                                       |
| 路径传参实现            | 封装多参数路径拼接、日期参数转换，适配@PathVariable注解                  | PathParamUtil       | 文件删除/预览的多参数路径传递、日期类型路径参数解析                          |
| 图片预览实现            | 封装图片相对路径生成、img标签生成、路径有效性校验                        | ImagePreviewUtil    | 文件管理页图片预览、轮播图展示                                               |
| 删除/更新操作确认       | 封装confirm弹窗JS生成、后端防绕过校验                                    | ConfirmOperateUtil  | 文件删除/审核更新的确认交互                                                 |
| 添加数据回查主键        | 封装<selectKey>标签生成、主键回显校验，适配自增主键（LAST_INSERT_ID()）| PrimaryKeyUtil      | 用户注册/文件上传的主键回显                                                 |
| 多表查询与动态控件      | 封装动态下拉框/单选框生成、多表联查参数封装，禁止写死选项                | DynamicControlUtil  | 文件分类下拉框、审核状态单选框、File+FileCategory多表查询                    |
| 删除操作日志记录        | 封装日志实体构建、操作描述生成、通用插入方法，确保删除操作可追溯          | DeleteLogUtil       | 文件删除后的日志记录，包含操作时间/类型/参数/描述                            |

## 三、整体实现约束
1. **Util设计原则**：各Util独立封装、单一职责，跨Util仅复用核心逻辑（如PathParamUtil调用DateFormatUtil），适配全业务场景，支持Controller/Service/Mapper/前端直接调用；
2. **技术栈适配**：所有实现贴合SpringMVC（重定向、@PathVariable、视图解析）和MyBatis（模糊查询、分页、主键回查、多表联查）特性；
3. **业务-技术融合**：10项技术要求深度嵌入业务流程（如注册时用PrimaryKeyUtil回查主键、删除文件时用DeleteLogUtil记录日志），无孤立技术实现；
4. **异常与合规**：关键操作（注册、上传、删除）需事务保障，模糊查询禁止用${}防注入，删除/更新需确认交互，确保系统稳定与合规。