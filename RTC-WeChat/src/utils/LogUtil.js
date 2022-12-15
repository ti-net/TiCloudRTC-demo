
let weChatlog = wx.getRealtimeLogManager ? wx.getRealtimeLogManager() : null


/**
 * @Description: 日志记录(手机可以上传-编译器不能)
 * 可上传调用方法可以打印日志到后台，在小程序管理平台可以查看打印的日志“开发->运维中心->实时日志”进入日志查询页面
 **/
class Logs {
  constructor() {
    this.instance = null
    /* log 开关, true 为显示 log ,false 为不显示 log*/
    this.isShowLog = true
  }

  static getInstance() {
    if (!this.instance) {
      this.instance = new Logs()
    }
    return this.instance
  }

  getStackTrace() {
    var obj = {}
    Error.captureStackTrace(obj, this.getStackTrace)
    return obj.stack
  }

  /**
   * 普通提示
   */
  info() {
    if (!weChatlog || !this.isShowLog) {
      return
    }
    weChatlog.info.apply(weChatlog, arguments)
    console.info.apply(console, arguments)
  }

  /**
   * 警告提示
   */
  warn() {
    if (!weChatlog || !this.isShowLog) {
      return
    }
    weChatlog.warn.apply(weChatlog, arguments)
    console.warn.apply(console, arguments)
  }

  /**
   * 错误提示
   */
  error() {
    if (!weChatlog || !this.isShowLog) {
      return
    }
    weChatlog.error.apply(weChatlog, arguments)
    console.error.apply(console, arguments)
  }

  /**
   * 组合提示
   * @param msg
   */
  setFilterMsg(msg) {
    if (!weChatlog || !weChatlog.setFilterMsg) {
      return
    }
    if (typeof msg !== "string") {
      return
    }
    weChatlog.setFilterMsg(msg)
  }

  addFilterMsg(msg) {
    if (!weChatlog || !weChatlog.addFilterMsg) {
      return
    }
    if (typeof msg !== "string") {
      return
    }
    weChatlog.addFilterMsg(msg)
  }

}

const log = Logs.getInstance()


export {
  log
}