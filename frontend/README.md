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

   #### 🧾 推荐的 Commit 类型

   | 类型         | 说明                                               | 示例                              |
   | ------------ | -------------------------------------------------- | --------------------------------- |
   | **feat**     | ✨ 新功能（feature）                                | `feat: 新增登录页`                |
   | **fix**      | 🐛 修复 bug                                         | `fix: 修复导航栏样式错位`         |
   | **chore**    | 🧹 项目维护（不影响业务逻辑，如依赖更新、配置修改） | `chore: 升级依赖到 vue 3.5`       |
   | **docs**     | 📖 仅修改文档内容（README、注释等）                 | `docs: 更新安装说明`              |
   | **style**    | 💅 代码格式调整（不影响逻辑，如空格、缩进、分号）   | `style: 调整组件缩进风格`         |
   | **refactor** | 🧠 重构（非新增功能或修复 bug）                     | `refactor: 优化状态管理逻辑`      |
   | **perf**     | ⚡ 性能优化                                         | `perf: 减少列表渲染开销`          |
   | **test**     | ✅ 添加或修改测试代码                               | `test: 增加登录模块单元测试`      |
   | **build**    | 🏗️ 构建系统或依赖相关的改动                         | `build: 调整 Vite 构建配置`       |
   | **ci**       | 🤖 CI/CD 配置改动（GitHub Actions、Jenkins等）      | `ci: 优化自动化部署脚本`          |
   | **revert**   | ⏪ 回滚之前的提交                                   | `revert: 回滚 feat: 新增注册功能` |
   | **merge**    | 🔀 分支合并提交                                     | `merge: 合并 dev/fe 到 main`      |

