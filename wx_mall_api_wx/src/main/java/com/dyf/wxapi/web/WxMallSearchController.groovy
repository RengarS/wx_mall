package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallKeywordDO
import com.dyf.db.domain.WxMallSearchHistoryDO
import com.dyf.db.service.WxMallKeywordService
import com.dyf.db.service.WxMallSearchHistoryService
import com.dyf.wxapi.annotation.LoginUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wx/search")
class WxMallSearchController {
    @Autowired
    private WxMallKeywordService keywordsService
    @Autowired
    private WxMallSearchHistoryService searchHistoryService

    /**
     * 搜索页面信息
     *
     * 如果用户已登录，则给出用户历史搜索记录。
     *
     * @param userId 用户ID
     * @return 搜索页面信息
     *   成功则
     *{
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *{
     *          defaultKeyword: xxx,
     *          historyKeywordList: xxx,
     *          hotKeywordList: xxx
     *}*}*  失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("/index")
    def index(@LoginUser Integer userId) {
        //取出输入框默认的关键词
        def defaultKeyword = keywordsService.selectOne(new EntityWrapper<WxMallKeywordDO>()
                .where("defaulted = true and deleted = false"))
        //取出热闹关键词
        def hotKeywordList = keywordsService.selectList(new EntityWrapper<WxMallKeywordDO>()
                .where("is_hot = true and deleted = false"))

        def historyList = null
        if (userId != null) {
            //取出用户历史关键字
            historyList = searchHistoryService.selectList(new EntityWrapper<WxMallSearchHistoryDO>()
                    .setSqlSelect("keywords")
                    .where("user_id = {0} and deleted = false"))
        }

        def data = [:]
        data.put("defaultKeyword", defaultKeyword)
        data.put("historyKeywordList", historyList)
        data.put("hotKeywordList", hotKeywordList)
        return HttpResponse.success(data)
    }

    /**
     * 关键字提醒
     *
     * 当用户输入关键字一部分时，可以推荐系统中合适的关键字。
     *
     * @param keyword 关键字
     * @return 合适的关键字
     *   成功则
     *{
     *      errno: 0,
     *      errmsg: '成功',
     *      data: xxx
     *}*   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("/helper")
    def helper(String keyword) {
        if (keyword == null) {
            return HttpResponse.badArgument()
        }

        def page = 1
        def size = 10
        def keywordsList = keywordsService.selectPage(new Page<WxMallKeywordDO>(page, size), new EntityWrapper<WxMallKeywordDO>()
                .where("keyword like {0} and deleted = false", "%" + keyword + "%"))
        String[] keys = new String[keywordsList.size]
        int index = 0
        for (WxMallKeywordDO key : keywordsList) {
            keys[index++] = key.keyword
        }
        return HttpResponse.success(keys)
    }

    /**
     * 关键字清理
     *
     * 当用户输入关键字一部分时，可以推荐系统中合适的关键字。
     *
     * @param userId 用户ID
     * @return 清理是否成功
     *   成功则 { errno: 0, errmsg: '成功' }*   失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("/clearhistory")
    def clearhistory(@LoginUser Integer userId) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        searchHistoryService.delete(new EntityWrapper<WxMallSearchHistoryDO>()
                .where("user_id = {0}", userId))
        return HttpResponse.success()
    }
}
