## 小王社区

### 资料
[spring 文档](https://spring.io/guides)   
[spring web](https://spring.io/guides/gs/serving-web-content/)  
[Bootstrap](https://v3.bootcss.com/getting-started/)    
[Github deploy key](https://developer.github.com/v3/guides/managing-deploy-keys/#deploy-keys)   
[es中文社区](https://elasticsearch.cn/explore)   
[spring MVC](https://docs.spring.io/spring/docs/5.0.3.RELEASE/spring-framework-reference/web.html#spring-web)
### 工具
[Git](https://git-scm.com/)   
[Visual-paradigm](https://www.visual-paradigm.com/cn/)  
[flyway](https://flywaydb.org/getstarted/firststeps/maven)
### sql脚本
```
create table USER
(
	ID int auto_increment,
	ACCOUNT_ID VARCHAR(100),
	NAME VARCHAR(50),
	TOKEN CHAR(36),
	GMT_CREATE BIGINT,
	GMT_MODIFIED BIGINT,
	constraint USER_PK
		primary key (ID)
);
```
```bash
mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate
```