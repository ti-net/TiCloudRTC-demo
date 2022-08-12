import { log } from "./LogUtil.js"

/** 请求 util */
class Request {
    constructor() {
        this.instance = null
    }

    static getInstance() {
        if (!this.instance) {
            this.instance = new Request()
        }
        return this.instance
    }

    execute(config) {
        return new Promise((resolve, reject) => {
            wx.showLoading({
                title: '请求中...'
            })
            log.info("WechatSDK：request start:\n", JSON.stringify({
                "url": config.baseURL + config.urlpath,
                "param": config.param
            }))
            wx.request({
                url: config.baseURL + config.urlpath,
                method: config.method,
                data: config.param,
                header: config.header,
                success: res => {
                    log.info("WechatSDK：request result:\n", JSON.stringify(res.data))
                    resolve(res)
                },
                fail: res => {
                    log.error("WechatSDK：request result error:\n", JSON.stringify(res))
                    reject(res)
                },
                complete: () => {
                    wx.hideLoading()
                }
            })
        })
    }

    executeNoLoading(config) {
        return new Promise((resolve, reject) => {
            log.info("WechatSDK：request start:\n", JSON.stringify({
                "url": config.baseURL + config.urlpath,
                "param": config.param
            }))
            wx.request({
                url: config.baseURL + config.urlpath,
                method: config.method,
                data: config.param,
                header: config.header,

                success: res => {
                    log.info("WechatSDK：request result:\n", JSON.stringify(res.data))
                    resolve(res)
                },
                fail: res => {
                    log.error("WechatSDK：request result error:\n", JSON.stringify(res))
                    reject(res)
                },
                complete: () => {
                    
                }
            })
        })
    }

    uploadFile(config) {
        return new Promise((resolve, reject) => {
            wx.showLoading({
                title: '请求中...'
            })
            log.info("WechatSDK：uploadFile start:\n", JSON.stringify({
                "url": config.baseURL + config.urlpath,
                "param": config.param
            }))
            wx.uploadFile({
                url: config.baseURL + config.urlpath,
                formData: config.param,
                header: config.header,
                filePath: config.filePath,
                name: 'screenFile',
                success: res => {
                    log.info("WechatSDK：uploadFile result:\n", JSON.stringify(res.data))
                    resolve(res)
                },
                fail: res => {
                    log.error("WechatSDK：uploadFile result error:\n", JSON.stringify(res))
                    reject(res)
                },
                complete: () => {
                    wx.hideLoading()
                }
            })
        })
    }
}

const request = Request.getInstance()

export {
    request
}