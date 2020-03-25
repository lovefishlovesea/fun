package com.lsd.fun.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.lsd.fun.modules.app.dto.UserRoleDto;

/**
 * 常量
 */
public class Constant {

    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";
    /**
     * 0
     */
    public static final Integer FALSE = 0;
    /**
     * 1
     */
    public static final Integer TRUE = 1;


    public static final String ORDER_TYPE_ERROR = "出入库单类型错误";

    /**
     * 菜单类型
     */
    @Getter
    @AllArgsConstructor
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;
    }

    /**
     * 定时任务状态
     */
    @Getter
    @AllArgsConstructor
    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 暂停
         */
        PAUSE(1);

        private int value;

    }


    /**
     * 系统角色名称枚举类
     */
    @Getter
    @AllArgsConstructor
    public enum RoleName {
        /** 超级管理员role名称 */
        SUPER_ADMIN_ROLENAME("超级管理员"),
        ;

        private String roleName;
    }


    @Getter
    @AllArgsConstructor
    public enum CostAccountStatus {
        NO_SETTLE(0,"未结"),
        HAS_SETTLE(1,"已结"),
        DEPOSIT(2,"预存"),
        PAY(3,"支付"),
        REDUCE(4,"减免"),
        OUT(5,"退住");

        private int value;
        private String name;

    }

    @Getter
    @AllArgsConstructor
    public enum EmployeeState {
        LEAVE(0,"离职"),
        WORD(1,"在职");

        private int value;
        private String name;

    }

    @Getter
    @AllArgsConstructor
    public enum CostAccountType {
        /** 费用流水的类别名称 */
        BED_FEE("床位费"),
        FOOD_FEE("餐饮费"),
        NURSING_FEE("护理费"),
        CUSTOM_FEE("定制护理费"),
        PAY_FEE("交费"),
        MONTH_FEE("消费_月费"),
        DISCOUNT_FEE("折后费用"),
        REDUCE_FEE("减免费用"),
        ;

        private String name;
    }

    @Getter
    @AllArgsConstructor
    public enum CostAccountInstruction {
        /** 费用流水的类别名称 */
        CHECK_IN_PAY("入住支付"),
        REDUCE_PAY("支付减免"),
        OWE_PAY("欠费支付"),
        OUT_REFUND("退住退款")
        ;

        private String name;
    }

    /**
     * 费用说明：入住交费
     */
    @Getter
    @AllArgsConstructor
    public enum CostType {
        CHECK_IN_COST ("1","入住交费");

        private String code;
        private String value;
        public static String getNameByCode(String code) {
            for (var status : values()) {
                if (status.getCode().equals(code)) {
                    return status.getValue();
                }
            }
            return null;
        }
    }

    /**
     * 出入库工单类型
     */
    @Getter
    @AllArgsConstructor
    public enum OrderType {
        /**
         * 入库
         */
        IN("0","入库"),
        /**
         * 出库
         */
        OUT("1","出库");

        private String code;
        private String value;
        public static String getNameByCode(String code) {
            for (var status : values()) {
                if (status.getCode().equals(code)) {
                    return status.getValue();
                }
            }
            return null;
        }

    }
    /**
     * 调拨审核状态
     */
    @Getter
    @AllArgsConstructor
    public enum ReviewStatus {
        /**
         *
         * 不同意
         */
        DISAGREE("-1","不同意"),
        /**
         * 同意
         */
        AGREE("1","同意"),
        /**
         * 未审核
         */
        UNCHECKED ("0","未审核");

        private String code;
        private String value;
        public static String getNameByCode(String code) {
            for (var status : values()) {
                if (status.getCode().equals(code)) {
                    return status.getValue();
                }
            }
            return null;
        }
    }

    /**
     * 盘点结果
     */
    @Getter
    @AllArgsConstructor
    public enum VerificationStatus {
        /**
         * 盘亏
         */
        LOSE("1","盘亏"),
        /**
         * 盘盈
         */
        MORE("2","盘盈"),
        /**
         * 正常
         */
        NORMAL ("0","正常");

        private String code;
        private String value;
        public static String getNameByCode(String code) {
            for (var status : values()) {
                if (status.getCode().equals(code)) {
                    return status.getValue();
                }
            }
            return null;
        }
    }

    /**
     * 老人入住状态
     */
    @Getter
    @AllArgsConstructor
    public enum AgedStatus {
        CONSULT(318,"咨询"),
        APPOINT(319,"预约"),
        TRYLIVE(320,"试住"),
        CHECKIN(321,"入住"),
        OUTSETTLEMENT(322,"待出院"),
        DISCHARGE(323,"退院");

        private Integer value;
        private String name;
    }

    /**
     * 投诉处理状态
     */

    @Getter
    @AllArgsConstructor
    public enum ComplainantHandleStatus {
        UNTREATE(367,"未处理"),
        HANDLING(368,"在处理"),
        HANDLED(369,"已处理");

        private Integer value;
        private String name;
    }

    /**
     * 预存记录说明
     */
    @Getter
    @AllArgsConstructor
    public enum payRecordInstruction {
        PAY_RECORD(1,"费用预存"),
        CHECK_IN(2,"入住花费"),
        OWE(3,"欠费花费"),
        OUT_SETTLEMENT(4,"退住退款");

        private Integer value;
        private String name;
    }


    /**
     * 探视来访状态
     */
    @Getter
    @AllArgsConstructor
    public enum VisitStatus {
        UNLEAVE("0","未离开"),
        HADLEFT("1","已离开");

        private String value;
        private String name;
    }

    /**
     * 默认系统设置名称
     */
    @Getter
    @AllArgsConstructor
    public enum DefaultConfigName{
        COSExpiredObjectClearTime("过期云对象清理任务的执行时间"),
        MONTHLY_INVENTORY("月度盘点"),
        FIXED_MONTHLY_FEE("固定月收费项目"),
        ;
        private String name;
    }

    /**
     * 护理计划任务类型
     */
    @Getter
    @AllArgsConstructor
    public enum NursingPlanType {
        COMMON_TASK(0,"一般任务"),
        CRITICAL_TASK(1,"关键任务"),
        ;

        private int value;
        private String desc;
    }


    /**
     * 支付状态枚举
     */
    @Getter
    @AllArgsConstructor
    public enum PayStatus {
        WAITING_PAY(0,"待支付"),
        PAID(1,"已支付"),
        REFUND(2,"退款"),
        FINISHED(3,"交易完成"),
        CANCEL(4,"交易作废"),
        ;

        private int value;
        private String desc;
    }

    /**
     * 订单详情类型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum FoodOrderDetailType {
        FOOD(0,"食物"),
        RECIPE(1,"套餐"),
        ;

        private int value;
        private String desc;
    }

    /**
     * 订单详情类型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum SubstituteApplyStatus {
        PENDING(0,"待审"),
        ACCEPT(1,"通过"),
        REJECT(2,"拒绝"),
        ;

        private int value;
        private String desc;
    }

    /**
     * 用户类型枚举
     */
    @AllArgsConstructor
    @Getter
    public enum UserType {
        EMPLOYEE_USER(0, "EMPLOYEE_USER"),
        FAMILY_USER(1, "FAMILY_USER"),
        ;
        private int code;
        private String type;

        public static UserType getUserType(UserRoleDto userRoleDto) {
            Constant.UserType userType = Constant.UserType.EMPLOYEE_USER;
            if (userRoleDto.getType() == Constant.UserType.FAMILY_USER.getCode()) {
                userType = Constant.UserType.FAMILY_USER;
            }
            return userType;
        }
    }

}
