/**
 * Copyright (C), 2020-2020, code_fusheng
 * FileName: SecurityHandler
 * Author:   25610
 * Date:     2020/2/5 15:00
 * Description:
 * History:
 * <author>        <time>      <version>       <desc>
 * 作者姓名       修改时间       版本号         描述
 */
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
