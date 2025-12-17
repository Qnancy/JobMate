# JubMate 前端项目

**JubMate 的前端代码仓库，基于 Vite + Vue 3 + TypeScript 开发。**

## 🚀 安装依赖

**推荐使用**[pnpm](https://pnpm.io/) 作为包管理工具，以获得更好的性能与一致性。

```
 pnpm install
```

**如果本地还没有安装pnpm，请先执行：**

```
 npm install -g pnpm
```

## 🧑‍💻 启动开发环境

```
 pnpm run dev
```

**启动后可在浏览器访问**`http://localhost:5173`（或终端显示的端口）。

## 🏗️ 打包构建

```
 pnpm run build
```

**构建结果将输出到**`dist/` 目录中。

## 📁 项目结构说明

`src/pages/` —— 页面组件目录（基于文件的自动路由）** **详情参考：[Vite File-based Routing 指南](https://uvr.esm.is/guide/file-based-routing.html)

## 🌿 Git 开发流程规范

**为避免代码冲突、保持主分支稳定，请遵守以下规范：**

1. **不要直接推送到**`main` 分支。

2. **每次开发新功能或修复问题时，新建一个分支：**

   ```
    git checkout -b dev/fe
   ```

3. **开发完成后推送到远程：**

   ```
    git push origin dev/fe
   ```

4. **确认无误后合并到**`main`。

5. **每次拉取更新后请执行**`pnpm install`，确保依赖一致。

## TODO
- [ ] 收藏逻辑
- [ ] 报名逻辑
- [ ] 管理员的一些系统设置？