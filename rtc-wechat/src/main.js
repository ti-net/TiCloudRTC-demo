import * as Pinia from 'pinia';
import App from './App'
import { createSSRApp } from 'vue'


App.mpType = "app"

// 不能修改导出的 createApp 方法名，不能修改从 Vue 中导入的 createSSRApp。
export function createApp() {
  const app = createSSRApp(App)
  
  app.use(Pinia.createPinia());
  return {
      app,
      Pinia
  }
}
