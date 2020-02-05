# lesson-7 Spring Boot 整合 Spring Security

***Spring Security ???***

***Java 领域常用的安全框架有 Shiro 和 Spring Security。Spring Security 是一个相对重量级的安全框架，功能强，权限细化，同时 Spring Security 是 Spring 技术栈的一个模块，可以和 Spring 应用直接整合使用，当然 Spring Boot 也提供了自动化配置，开箱即用。***

***Spring Security是一个灵活和强大的身份验证和访问控制框架，以确保基于Spring的Java Web应用程序的安全。***

***Spring Security是一个轻量级的安全框架，它确保基于Spring的应用程序提供身份验证和授权支持。它与Spring MVC有很好地集成，并配备了流行的安全算法实现捆绑在一起***

#### 简单项目搭建流程

* 1、创建简单 Maven 工程，pom.xml中添加相关依赖
spring-boot-starter-security 为 Spring Security 依赖。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>springboot7</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.4.RELEASE</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <!-- Spring Boot 整合 Spring Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.10</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```
* 2、创建 Handler
```java
package xyz.fusheng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityHandler {

    @GetMapping("/index")
    public String index(){
        return "index";
    }
}
```
* 3、创建 HTML 前端页面 index.html [PS : 在 Resources/templates 路径下创建]
```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <p>Spring Boot 整合 Spring Security</p>
    <from method="post" action="/logout">
        <input type="submit" value="退出"/>
    </from>
</body>
</html>
```
* 4、创建 application.yml 配置文件
```yaml
spring:
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    servlet:
      content-type: text/html
    encoding: UTF-8
    mode: HTML5
```
* 5、创建启动类 Application 
```java
package xyz.fusheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
```
* 6、启动 Application,打开浏览器访问 http://localhost:8080/index 
![image](4D5F784433B9459AA43F5C3DBC18C1E5)
***PS : 上面就是 Spring Security 给所有请求添加的自动验证规则，必须是登陆用户才可以访问。默认用户名是 user , 密码是 控制台 Using generated security password: 68b13953-39e2-4c2e-a977-7ef259fbd856 后半截数据***
> Using generated security password: 68b13953-39e2-4c2e-a977-7ef259fbd856

***输入之后，即可成功访问***
![image](F296CE53B9A14FD89C187A229EE24A43)

***实际情况下的用户名于密码可自定义，在 application.yml 中添加配置即可***
```yaml
spring:
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    servlet:
      content-type: text/html
    encoding: UTF-8
    mode: HTML5
  security:
    user:
      name: admin
      password: 123456
```

#### 权限管理

***实际开发中，开发人员一般不会直接将权限和用户绑定，在中间有个[角色]的存在，将权限付给角色，在该用户添加角色。***

下面的案例我门定义两个 HTML 资源（index.html admin.html），同时定义两个角色 Admin 和 User，访问权限设置，Admin 拥有访问 index.html 和 admin.html 的权限， User 只能访问 index.html。

7. 创建 SecurityConfig 类，继承 WebSecurityConfigureAdapter。
```java
package xyz.fusheng.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().passwordEncoder(new MyPasswordEncoder())
                // 定义用户名 user 密码 000 赋予角色 User
                .withUser("user").password(new MyPasswordEncoder()
                .encode("000")).roles("User")
                .and()
                // 定义用户名 admin 密码 123456 赋予角色 Admin User
                .withUser("admin").password(new MyPasswordEncoder()
                .encode("123456")).roles("Admin","User");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                // 对访问资源 赋予角色访问权限
                // 访问/admin需要具备Admin角色
                .antMatchers("/admin").hasRole("Admin")
                // 访问/index需要具备Admin或者User角色
                .antMatchers("/index").access("hasRole('Admin') or hasRole('User')")
                // 其他请求都必须经过权限验证才能访问
                .anyRequest().authenticated()
                // 连接方式
                .and()
                // 表示配置信息
                .formLogin()
                // 表示使用自定义登陆页面
                .loginPage("/login")
                // 表示与登陆相关额请求都需要验证通过
                .permitAll()
                .and()
                // 表示继集成退出功能
                .logout()
                .permitAll()
                .and()
                // 关闭 csrf
                .csrf()
                .disable();
    }
}

```
***重写 congigure(AuthenticationManagerBuilder auth)和configure(HttpSecurity http),在congigure(AuthenticationManagerBuilder auth)中添加用户和角色，此处需要定义 MyPasswordEncoder 提供一个 PasswordEncorder 的实例。***
* 8、 MyPasswordEncoder 
```java
package xyz.fusheng.config;

import org.springframework.security.crypto.password.PasswordEncoder;

public class MyPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence){
        return charSequence.toString();
    }
    @Override
    public boolean matches(CharSequence charSequence, String s){
        return s.equals(charSequence.toString());
    }
}
```
* 9、修改 Handler。添加 /admin 和 /login 请求资源映射
```java
package xyz.fusheng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityHandler {

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
```
* 10、启动 Application,访问 http://localhost:8080/index  直接跳转到登陆页面，如果登陆 User 用户就不能在请求 admin 页面，如果登陆 Admin 角色用户 则可以继续请求 admin 页面。









