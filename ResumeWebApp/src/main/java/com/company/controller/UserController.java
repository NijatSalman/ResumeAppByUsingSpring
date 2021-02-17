package com.company.controller;

import com.company.entity.User;
import com.company.form.UserForm;
import com.company.service.DummyService;
import com.company.service.inter.UserServiceInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping
public class UserController {

    @Autowired
    private UserServiceInter userServiceInter;

    //this method is working on Servlet which value taken by request.getParam
    @RequestMapping(method = RequestMethod.GET, value = "/users")
    public String index(HttpServletRequest request){
        String name=request.getParameter("name");
        String surname=request.getParameter("surname");
        String nationId=request.getParameter("nationalityId");

        Integer nationalityIdbyRequest=null;
        if (nationId!=null && !nationId.trim().isEmpty()){
            nationalityIdbyRequest=Integer.parseInt(nationId);
        }
        List<User> list= userServiceInter.getAll(name,surname,nationalityIdbyRequest);
        request.setAttribute("userList",list);

        return "users";
    }

    //this method is working @RequestParam but disadvantage is that value taken by one-by-one.
    // if we have 50 values that we need to call @RequestParam 50times :)
//    @RequestMapping(method = RequestMethod.GET, value="/users")//users?name=Nijat
//    public ModelAndView index(
//            @RequestParam(value="name", required = false) String name,
//            @RequestParam(value="surname", required = false) String surname,
//            @RequestParam(value="nid", required = false) Integer nationalityId){
//        List<User> list = userServiceInter.getAll(name, surname, nationalityId);
//        ModelAndView mv = new ModelAndView("users");
//
//        mv.addObject("users",list);
//        return mv;
//    }

    // this method is working with Spring ModelAttribute and values sending and getting together as object.
        @RequestMapping(method = RequestMethod.GET, value="/usersm")
     public ModelAndView indexM(
             @Valid
             @ModelAttribute("user") UserForm u,
             BindingResult bindingResult){
            ModelAndView mv = new ModelAndView("users");

            if(bindingResult.hasErrors()){
                return mv;
            }

        List<User> list =userServiceInter.getAll(u.getName(), u.getSurname(), u.getNationalityId());
        mv.addObject("users",list);
        return mv;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/userDelete")
    public String indexDelete(
            @RequestParam(name ="id",required = false) Integer id
    ){
        //ModelAndView mv = new ModelAndView("users");
        System.out.println("id is ===="+ id);
        userServiceInter.removeUser(id);
        return "users";
    }

    @RequestMapping(method={RequestMethod.GET}, value="/login")
    public String loginPost(){
            return "login";
    }

    @RequestMapping(method={RequestMethod.GET}, value="/logout")
    public String logoutPost(){
        return "logout";
    }

    @Autowired
    DummyService dummyService;

    @RequestMapping(method={RequestMethod.GET}, value="/foo")// value="/foo" this url can be reached by USER and ADMIN that We defined on WebSecurityConfig.class
    public String foo(){
        System.out.println("foo method in Controller");
        dummyService.foo();
        return "users"; // users.jsp page
    }

//this method helps us for sending objects to form when we run project first time and inside the Spring form ModelAttribute will want object
    @ModelAttribute("user")
    public UserForm getEmptyUserForm(){
            return new UserForm(null,null,null);
    }

}
