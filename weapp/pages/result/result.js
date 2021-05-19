// pages/result/result.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    imgUrl:null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    const filepath = options.filepath
    const _this = this
    _this.setData({
      imgUrl:filepath,
      results:[]
    })
    wx.getFileSystemManager().readFile({
      filePath:filepath,
      encoding:'base64',
      success(fileRes){
        const baseUrl = fileRes.data
        wx.showLoading()
        wx.request({
          url: 'http://plantgw.nongbangzhu.cn/plant/recognize', //仅用植物花卉识别接口作为实例
          header: { 
            //购买后可得到AppCode，查看方法是在阿里云市场进入买家中心的管理控制台，
            //在已购买的服务列表内，找到 智能植物识别（含花卉与杂草），下方AppCode一行即是
            //相关截图请查看doc目录下的截图文件 
            'Authorization': 'APPCODE 替换为您购买后得到的AppCode，获取方法请看这行代码上方的注释',
            'content-type': 'application/x-www-form-urlencoded; charset=UTF-8'
           }, 
          data:{
            // BASE64编码不需要带 "data:image/jpeg;base64,"否则会报403
            img_base64:baseUrl
          },
          method:'POST',
          success (res){
            wx.hideLoading()
            _this.setData({
              results:res.data.Result
            })
          },
          fail(error){
            console.error('请求出错：' + err);
          }
        })
      }
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})