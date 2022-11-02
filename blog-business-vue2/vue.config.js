// const { defineConfig } = require('@vue/cli-service')

//从配置文件中读取项目使用的端口
let port = process.env.PORT
let backendAPI = process.env.BACKEND_API

module.exports = {
  // transpileDependencies: true,

  lintOnSave: process.env.NODE_ENV === 'development', //如果是开发环境，就是true

  devServer: {
    host: 'localhost', //本机ip
    port: port, //本项目使用的端口
    open: true,
     proxy: {
         '/': {  //代理别名
             target: backendAPI,   //代理目标值
             changeOrigin: true,
             secure: true,
             //地址栏显示：http://localhost:7878/#/login
             //尽管请求地址显示的是：http://localhost:7878/api/login
             //但实际访问的却是：tartget/login
             //也就是说，域名被代理了，但路由地址是不变的
             pathRewrite:{  //替换路径中的/api
                 '^/':'/'
             }
             //pathRequiresRewrite: {
             //  '^/api': ''
             //}
         }
     },
    overlay: {
      warning: false,
      errors: true
    }
  }
}
