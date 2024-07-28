package com.atguigu.daijia.rules;

import com.alibaba.fastjson.JSON;
import com.atguigu.daijia.model.form.rules.FeeRuleRequest;
import com.atguigu.daijia.model.form.rules.ProfitsharingRuleRequest;
import com.atguigu.daijia.model.form.rules.RewardRuleRequest;
import com.atguigu.daijia.model.vo.rules.FeeRuleResponse;
import com.atguigu.daijia.model.vo.rules.ProfitsharingRuleResponse;
import com.atguigu.daijia.model.vo.rules.RewardRuleResponse;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class ElasticsearchDemoApplicationTests {

    @Autowired
    private KieContainer kieContainer;


    @Test
    public void test01() {
        // 开启会话
        KieSession kieSession = kieContainer.newKieSession();

        // 触发规则
        kieSession.fireAllRules();
        // 中止会话
        kieSession.dispose();
    }

    /*
     *  1 00:00:00-06:59:59  19元(含3公里)   == 超出12公里
     *  2 00:00:00-06:59:59   4元/1公里      == 增加一公里，单价4元
     *  3 等候10分钟后  1元/1分钟             == 超过10分钟，单价1元
     *  4 订单行程超出12公里后每公里1元        == 超出3公里 ，单价 1元
     * 代驾总金额：19 + 12*4 + 10 + 3 = 80
     */
    @Test
    void test1() {
        FeeRuleRequest feeRuleRequest = new FeeRuleRequest();
        feeRuleRequest.setDistance(new BigDecimal(15.0));
        feeRuleRequest.setStartTime("01:59:59");
        feeRuleRequest.setWaitMinute(20);

        // 开启会话
        KieSession kieSession = kieContainer.newKieSession();

        FeeRuleResponse feeRuleResponse = new FeeRuleResponse();
        kieSession.setGlobal("feeRuleResponse", feeRuleResponse);
        // 设置订单对象
        kieSession.insert(feeRuleRequest);
        // 触发规则
        kieSession.fireAllRules();
        // 中止会话
        kieSession.dispose();
        System.out.println("后："+JSON.toJSONString(feeRuleResponse));
    }

//    @Test
//    void test2() {
//        RewardRuleRequest rewardRuleRequest = new RewardRuleRequest();
//        rewardRuleRequest.setStartTime("01:59:59");
//        rewardRuleRequest.setOrderNum(10);
//
//        // 开启会话
//        KieSession kieSession = kieContainer.newKieSession();
//
//        RewardRuleResponse rewardRuleResponse = new RewardRuleResponse();
//        kieSession.setGlobal("rewardRuleResponse", rewardRuleResponse);
//        // 设置订单对象
//        kieSession.insert(rewardRuleRequest);
//        // 触发规则
//        kieSession.fireAllRules();
//        // 中止会话
//        kieSession.dispose();
//        System.out.println("后："+JSON.toJSONString(rewardRuleRequest));
//    }
//
//    @Test
//    void test3() {
//        ProfitsharingRuleRequest profitsharingRuleRequest = new ProfitsharingRuleRequest();
//        profitsharingRuleRequest.setOrderAmount(new BigDecimal(34));
//        profitsharingRuleRequest.setOrderNum(0);
//
//
//        BigDecimal d = profitsharingRuleRequest.getOrderAmount().multiply(new BigDecimal("0.006"));
//        System.out.println(d);
//
////        profitsharingRuleRequest.getOrderAmount().setScale(2, RoundingMode.HALF_UP);
//        // 开启会话
//        KieSession kieSession = kieContainer.newKieSession();
//
//        ProfitsharingRuleResponse profitsharingRuleResponse = new ProfitsharingRuleResponse();
//        kieSession.setGlobal("profitsharingRuleResponse", profitsharingRuleResponse);
//        // 设置订单对象
//        kieSession.insert(profitsharingRuleRequest);
//        // 触发规则
//        kieSession.fireAllRules();
//        // 中止会话
//        kieSession.dispose();
//        System.out.println("后："+JSON.toJSONString(profitsharingRuleResponse));
//    }
}
