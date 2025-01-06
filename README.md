# 每日食记 (DailyFoodLog)

每日食记是一款基于 Kotlin Multiplatform Compose 的跨平台应用。它帮助用户通过 AI 技术识别食物照片的营养组成，记录每日饮食，并与健康摄入数据进行对比，提供个性化的膳食建议，助力用户实现科学合理的饮食管理。

---

## 功能特色 ✨

1. **AI 食物识别**  
   使用先进的 AI 模型识别用户上传的食物照片，分析食材和营养成分（如热量、蛋白质、脂肪、碳水化合物等）。

2. **每日饮食记录**  
   自动保存用户的饮食信息，帮助用户查看和管理历史记录。

3. **健康摄入对比**  
   根据用户的身高、体重、年龄和目标（减脂、增肌或维持体重），提供每日推荐摄入量，并实时对比已摄入的营养数据。

4. **膳食提醒**  
   提醒用户合理膳食搭配，避免过量摄入或营养不均。

5. **多平台支持**
   - 使用 **Kotlin Multiplatform Compose** 开发，可在 Android 和 iOS 平台运行。

---

## 技术栈 💻

### 前端
- **Kotlin Multiplatform Compose**
   - 实现跨平台界面开发
   - 提供一致的用户体验

### 后端
- **Spring Boot**
   - 提供强大的 RESTful API 支持
   - 处理用户数据和每日饮食记录

### AI 模型
- **计算机视觉**
   - 使用图像识别技术分析食物照片
   - 提供精准的营养成分数据

---

## 项目架构 🏗️

```
├── frontend/               # Kotlin Multiplatform Compose 前端代码
│   ├── android/           # Android 平台代码
│   ├── ios/               # iOS 平台代码
│   └── common/            # 通用业务逻辑和 UI 代码
```

---

## 贡献指南 🤝

欢迎贡献代码！请按照以下步骤提交您的改动：

1. Fork 仓库
2. 创建功能分支 (`git checkout -b feature/awesome-feature`)
3. 提交更改 (`git commit -m 'Add awesome feature'`)
4. 推送到分支 (`git push origin feature/awesome-feature`)
5. 提交 Pull Request

---

## 开发者 👩‍💻👨‍💻

- **Haodong Ju** - 项目作者

如有任何问题或建议，请通过邮箱联系：juhaodong@gmail.com

---

## 许可证 📝

该项目遵循 [MIT License](LICENSE)。