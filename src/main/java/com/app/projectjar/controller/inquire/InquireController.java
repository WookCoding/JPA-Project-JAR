package com.app.projectjar.controller.inquire;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("board/inquire/*")
public class InquireController {

    @GetMapping("detail")
    public void inquireDetail(){}
    @GetMapping("list")
    public void inquireList(){}
    @GetMapping("write")
    public void inquireWrite(){}
//     민구 왔다감ㅋㅋ
}
