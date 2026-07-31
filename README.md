# 永恒的像素地牢

这是基于 [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon) 源码制作的个人 Mod 版本，加入了 E.D. 职业、专属转职与「独一无二的王之宝藏」。原项目的 GPL-3.0 许可、版权声明和现有署名均应保留。

## 构建

项目包含 Android、iOS、桌面端和核心代码模块。Android 测试包可以通过 GitHub Actions 构建：

1. 打开仓库的 **Actions** 页面。
2. 选择 **Build Eternal Pixel Dungeon APK**。
3. 点击 **Run workflow**，等待构建完成。
4. 从构建结果的 **Artifacts** 下载 `eternal-pixel-dungeon-apk`。

正式发布前，请确认已使用自己的签名密钥，并检查 `build.gradle` 中的应用名称、包名和版本号。

## 开发文档

- [Android 构建指南](docs/getting-started-android.md)
- [桌面端构建指南](docs/getting-started-desktop.md)
- [iOS 构建指南](docs/getting-started-ios.md)
- [制作自定义版本的建议](docs/recommended-changes.md)

## E.D. 内容

- E.D.：痛苦越高，攻击越强。
- 王之宝藏：击杀敌人时收纳掠夺物，死亡后清空。
- 破碎王冠：围绕低生命值、反伤和护盾构筑。
- 僭主：围绕扩大宝藏、击杀护盾和掠夺增伤构筑。

## 许可与署名

本项目基于 GPL-3.0 授权的 Shattered Pixel Dungeon。发布修改版本时，请公开对应的完整源码，保留 `LICENSE.txt`、原始文件头版权声明和原项目 credits，不要将本 Mod 冒充为官方版本。
