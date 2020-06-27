package com.noxus.draven.flow.controller;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.ui.common.security.DefaultPrivileges;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 这里临时解决了
 * 1.简单的授权问题
 * 2.获取不到用户
 */
@RestController
@RequestMapping("/app")
public class DemoController {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;

//    @Autowired
//    private ProessEngine processEngine;

    @Autowired
    private ModelService modelService;


    @RequestMapping(value = "/rest/account", method = RequestMethod.GET, produces = "application/json")
    public UserRepresentation getAccount() {
        // 获取用户信息 - 结合具体业务提供相关信息
        User entity = new UserEntityImpl();
        entity.setId("draven");
        SecurityUtils.assumeUser(entity);
        UserRepresentation userRepresentation = new UserRepresentation();
        // 临时用户
        userRepresentation.setId("admin");
        userRepresentation.setEmail("admin@flowable.org");
        userRepresentation.setFullName("Test Administrator");
        userRepresentation.setLastName("Administrator");
        userRepresentation.setFirstName("Test");
        List<String> privileges = new ArrayList<>();
        privileges.add(DefaultPrivileges.ACCESS_MODELER);
        privileges.add(DefaultPrivileges.ACCESS_IDM);
        privileges.add(DefaultPrivileges.ACCESS_ADMIN);
        privileges.add(DefaultPrivileges.ACCESS_TASK);
        privileges.add(DefaultPrivileges.ACCESS_REST_API);
        userRepresentation.setPrivileges(privileges);
        return userRepresentation;
    }

   /* public static User getCurrentUserObject() {
        if (assumeUser != null) {
            return assumeUser;
        }

        RemoteUser user = new RemoteUser();
        user.setId("admin");
        user.setDisplayName("admin");
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setEmail("admin@admin.com");
        user.setPassword("test");
        List<String> pris = new ArrayList<>();
        pris.add(DefaultPrivileges.ACCESS_MODELER);
        pris.add(DefaultPrivileges.ACCESS_IDM);
        pris.add(DefaultPrivileges.ACCESS_ADMIN);
        pris.add(DefaultPrivileges.ACCESS_TASK);
        pris.add(DefaultPrivileges.ACCESS_REST_API);
        user.setPrivileges(pris);
        return user;
    }*/
    /**
     * 以下准备针对一个完整的流程进行设计
     * 需要将rest-api集成
     * 便于业务使用
     */


    /**
     * 部署流程定义
     *
     * @return www.1b23.com
     */
    @RequestMapping(value = "/deployment")
    //@RequiresPermissions("fhmodel:edit")
    @ResponseBody
    public Object deployment() {
        Map<String, Object> map = new HashMap<String, Object>();
        String result = "success";
        try {
            //部署流程定义
            String s = deploymentProcessDefinitionFromUIModelId("f17c0acd-b77b-11ea-931c-12093a94d86f");
            System.out.println(s);
        } catch (Exception e) {
            result = "error";
        } finally {
            map.put("result", result);
        }
        return map;
    }

    /**
     * @param modelId 模型ID 根据modeid部署
     * @return 部署ID
     */
    protected String deploymentProcessDefinitionFromUIModelId(String modelId) throws Exception {
        Model model = modelService.getModel(modelId);
        BpmnModel bpmnModel = modelService.getBpmnModel(model);
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName())
                .addBpmnModel(model.getKey() + ".bpmn", bpmnModel).deploy();
        //部署ID
        return deployment.getId();
    }



    /**
     * 添加报销
     *
     * @param userId    用户Id
     * @param money     报销金额
     * @param descption 描述
     */
    @RequestMapping(value = "add")
    @ResponseBody
    public String addExpense(String userId, Integer money, String descption) {
        //启动流程
        HashMap<String, Object> map = new HashMap<>();
        map.put("taskUser", userId);
        map.put("money", money);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Expense", map);
        return "提交成功.流程Id为：" + processInstance.getId();
    }

    /**
     * 获取审批管理列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String userId) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            System.out.println(task.toString());
        }
        return tasks.toArray().toString();
    }

    /**
     * 批准
     *
     * @param taskId 任务ID
     */
    @RequestMapping(value = "apply")
    @ResponseBody
    public String apply(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        //通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("outcome", "通过");
        taskService.complete(taskId, map);
        return "processed ok!";
    }

    /**
     * 拒绝
     */
    @ResponseBody
    @RequestMapping(value = "reject")
    public String reject(String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("outcome", "驳回");
        taskService.complete(taskId, map);
        return "reject";
    }

    /**
     * 生成流程图
     *
     * @param processId 任务ID
     */
    /*@RequestMapping(value = "processDiagram")
    public void genProcessDiagram(HttpServletResponse httpServletResponse, String processId) throws Exception {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();

        //流程走完的不显示图
        if (pi == null) {
            return;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        String InstanceId = task.getProcessInstanceId();
        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .processInstanceId(InstanceId)
                .list();

        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0);
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }*/


    /**
     * @author haiyangp
     * date:  2018/4/7
     * desc: flowable配置----为放置生成的流程图中中文乱码
     */
//    @Configuration
//    public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
//
//        @Override
//        public void configure(SpringProcessEngineConfiguration engineConfiguration) {
//            engineConfiguration.setActivityFontName("宋体");
//            engineConfiguration.setLabelFontName("宋体");
//            engineConfiguration.setAnnotationFontName("宋体");
//        }
//    }
    @GetMapping("/getTasks")
    public List<Task> getTasks(String assignee) {
        return taskService.createTaskQuery().taskAssignee(assignee).list();
    }

    @GetMapping("/startProcess")
    public void startProcess(String name) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        System.out.println(taskQuery);
    }

    /**
     * 启用流程
     *
     * @param name1
     * @param name2
     */
    @GetMapping("/runtimeService")
    public void test(String name1, String name2) {
        Map<String, Object> var3 = new HashMap<>();
        ProcessInstance xpenseProcess = runtimeService.startProcessInstanceByKey(name1, name2, var3);
        System.out.println(xpenseProcess);
    }



}
