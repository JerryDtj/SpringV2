package com.dtj.springMvc.controller;


import com.dtj.springMvc.service.DemoService;
import com.dtj.spring.annotation.Autowried;
import com.dtj.spring.annotation.Controller;
import com.dtj.spring.annotation.RequestMapping;
import com.dtj.spring.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/demo")
public class DemoAction {

    @Autowried
    private DemoService demoService;

    @RequestMapping("/query.json")
    public void query(HttpServletRequest req,HttpServletResponse resp,
                      @RequestParam("name") String name){
        String result = demoService.get(name);
        System.out.println(result);
//		try {
//			resp.getWriter().write(result);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    }

    @RequestMapping("/edit.json")
    public void edit(HttpServletRequest req,HttpServletResponse resp,Integer id){

    }

}
