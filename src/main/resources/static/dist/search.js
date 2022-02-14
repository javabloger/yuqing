/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 全文搜索js
 */

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 切换方案请求数据
 */
function switchProject(event) {
    let $event = $(event);
    let projectid = $event.attr("data-index");
    let group_id = $event.attr("data-groupid");
    console.log("projectid=====>" + projectid);
    console.log("group_id=====>" + group_id);
}