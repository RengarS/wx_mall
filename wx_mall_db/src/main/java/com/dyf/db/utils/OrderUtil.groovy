package com.dyf.db.utils

import com.dyf.db.domain.WxMallOrderDO


/*
 * 订单流程：下单成功－》支付订单－》发货－》收货
 * 订单状态：
 * 101 订单生成，未支付；102，下单未支付用户取消；103，下单未支付超期系统自动取消
 * 201 支付完成，商家未发货；202，订单生产，已付款未发货，却取消
 * 301 商家发货，用户未确认；
 * 401 用户确认收货，订单结束； 402 用户没有确认收货，但是快递反馈已收获后，超过一定时间，系统自动确认收货，订单结束。
 *
 * 当101用户未付款时，此时用户可以进行的操作是取消订单，或者付款操作
 * 当201支付完成而商家未发货时，此时用户可以取消订单并申请退款
 * 当301商家已发货时，此时用户可以有确认收货的操作
 * 当401用户确认收货以后，此时用户可以进行的操作是删除订单，评价商品，或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除订单，评价商品，或者再次购买
 *
 * 目前不支持订单退货
 */

class OrderUtil {

    static final Short STATUS_CREATE = 101
    static final Short STATUS_PAY = 201
    static final Short STATUS_SHIP = 301
    static final Short STATUS_CONFIRM = 401
    static final Short STATUS_CANCEL = 102
    static final Short STATUS_AUTO_CANCEL = 103
    static final Short STATUS_REFUND = 202
    static final Short STATUS_AUTO_CONFIRM = 402


    static String orderStatusText(WxMallOrderDO order) {
        int status = order.getOrderStatus().intValue()

        if (status == 101) {
            return "未付款"
        }

        if (status == 102) {
            return "已取消"
        }

        if (status == 103) {
            return "已取消(系统)"
        }

        if (status == 201) {
            return "已付款"
        }

        if (status == 202) {
            // 进一步跟踪退款状态
            return "已取消，退款中"
        }

        if (status == 301) {
            return "已发货"
        }

        if (status == 401) {
            return "已收货"
        }

        if (status == 402) {
            return "已收货(系统)"
        }

        throw new IllegalStateException("orderStatus不支持")
    }


    static OrderHandleOption build(WxMallOrderDO order) {
        int status = order.getOrderStatus().intValue()
        OrderHandleOption handleOption = new OrderHandleOption()

        if (status == 101) {
            // 如果订单没有被取消，且没有支付，则可支付，可取消
            handleOption.setCancel(true)
            handleOption.setPay(true)
        } else if (status == 102 || status == 103) {
            // 如果订单已经取消或是已完成，则可删除
            handleOption.setDelete(true)
            handleOption.setRebuy(true)
        } else if (status == 201) {
            // 如果订单已付款，没有发货，则可退款操作
            handleOption.setRefund(true)
        } else if (status == 202) {
            // 如果订单已经取消或是已完成，则可删除
            handleOption.setDelete(true)
            handleOption.setRebuy(true)
        } else if (status == 301) {
            // 如果订单已经发货，没有收货，则可收货操作,
            // 此时不能取消订单
            handleOption.setConfirm(true)
        } else if (status == 401 || status == 402) {
            // 如果订单已经支付，且已经收货，则可完成交易、评论和再次购买
            handleOption.setDelete(true)
            handleOption.setComment(true)
            handleOption.setRebuy(true)
        } else {
            throw new IllegalStateException("status不支持")
        }

        return handleOption
    }

    static List<Short> orderStatus(Integer showType) {
        // 全部订单
        if (showType == 0) {
            return null
        }

        List<Short> status = new ArrayList<Short>(2)

        if (showType.equals(1)) {
            // 待付款订单
            status.add((short) 101)
        } else if (showType == 2) {
            // 待发货订单
            status.add((short) 201)
        } else if (showType == 3) {
            // 待收货订单
            status.add((short) 301)
        } else if (showType == 4) {
            // 待评价订单
            status.add((short) 401)
            status.add((short) 401)
        } else {
            return null
        }

        return status
    }


    static boolean isPayStatus(WxMallOrderDO litemallOrder) {
        return STATUS_PAY == litemallOrder.getOrderStatus().shortValue()
    }

    static boolean isShipStatus(WxMallOrderDO litemallOrder) {
        return STATUS_SHIP == litemallOrder.getOrderStatus().shortValue()
    }
}
