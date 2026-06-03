# 智医康 (Zencare)

基于最新 Android 框架 + Kotlin 实现的医疗健康综合服务平台 Android 客户端。

## 业务模块

| 模块 | 功能 | 状态 |
|------|------|------|
| **AI 问诊** | AI 智能聊天咨询，支持文字、图片、语音消息，流式响应 | MVP |
| **健康管理** | 血压/血糖/体重等健康指标记录、趋势统计 | MVP |
| **健康商城** | 药品/保健品/医疗器械浏览、购物车、下单 | MVP |

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Kotlin | 2.1 |
| UI | Jetpack Compose + Material3 | BOM 2025.06 |
| 架构 | MVVM + UDF 单向数据流 | — |
| DI | Hilt | 2.54 |
| 网络 | Retrofit + OkHttp + Kotlin Serialization | 2.11 / 4.12 |
| 图片 | Coil 3 (Compose 原生) | 3.1 |
| 数据库 | Room | 2.7 |
| 键值存储 | DataStore Preferences | 1.1 |
| 导航 | Navigation Compose (type-safe routes) | 2.8 |
| 构建 | Gradle 8.11.1 + Version Catalog | — |

## 模块架构

```
:core:model       纯数据模型，零依赖
:core:common      工具类、扩展函数、AppResult
:core:network     Retrofit API 契约、MockInterceptor
:core:data        Room DB、Repository、DataStore
:core:ui          Material3 主题、通用 Composable 组件
:feature:consultation  AI 问诊模块
:feature:health        健康管理模块
:feature:shop          医疗电商模块
:app                   壳模块，DI 装配 + 导航
```

**关键约束：** Feature 模块之间禁止相互依赖，共享代码通过 `:core:*` 层通信。

## 开发指南

### 环境要求

- Android Studio (最新稳定版)
- JDK 17+（AS 自带的 JBR 即可）
- Android SDK（compileSdk 36, minSdk 23）

### 构建

```bash
# Debug 构建
./gradlew assembleDebug

# 单模块编译
./gradlew :feature:consultation:compileDebugKotlin

# 运行测试
./gradlew test
```

### Mock 数据

开发期 API 通过 `MockInterceptor` 返回假数据（`BuildConfig.MOCK_API = true`），后端就绪后关闭即可切换为真实接口。

## 兼容性

- **最低支持：** Android 6.0 (API 23)
- **目标版本：** Android 16 (API 36)
