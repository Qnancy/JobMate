import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import 'vant/lib/index.css';
// 以下几个是「函数式 API」，@vant/auto-import-resolver 不会自动注入它们的样式，
// 必须显式 import，否则 showToast/showDialog/showNotify 会渲染成一个没样式的白色方块。
import 'vant/es/toast/style';
import 'vant/es/dialog/style';
import 'vant/es/notify/style';
import { applyThemeMode, getStoredThemeMode } from './utils/theme';

applyThemeMode(getStoredThemeMode());

createApp(App).use(router).mount('#app')
