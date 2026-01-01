# Team Shop配置注入修复说明

## 问题描述

在使用BRAC插件为BedwarsRel游戏注入自定义配置时，发现：
- ✅ BedwarsRel的config和shop配置可以正常注入
- ✅ SBA的config配置可以正常注入
- ❌ **SBA的team_shop配置完全不生效**

## 问题根源

### 原始代码分析

在 `Config.java` 的 `loadConfig()` 方法中（第351-352行）：

```java
file_config = getVerifiedConfig("config.yml");
language_config = getVerifiedConfig("language.yml");
FileConfiguration team_shop_config = getVerifiedConfig("team_shop.yml");  // 局部变量！
```

**关键问题**：
1. `file_config` 和 `language_config` 是**静态字段**，可以通过反射注入
2. `team_shop_config` 只是**局部变量**，无法通过反射访问
3. ConfigAPI只能注入静态字段，所以team_shop配置无法注入

### ConfigAPI的原始实现

在 `ConfigAPI.java` 的 `loadGameConfig()` 方法中（第56-60行）：

```java
// 只注入了file_config和language_config
injectConfig("file_config", customConfig);
if (customLanguage != null) {
    injectConfig("language_config", customLanguage);
}
// 缺少team_shop_config的注入！
```

## 解决方案

### 1. 修改 Config.java

**添加静态字段**（第40行）：
```java
private static FileConfiguration file_config;
private static FileConfiguration language_config;
private static FileConfiguration team_shop_config;  // 新增
```

**修改loadConfig()方法**（第352行）：
```java
file_config = getVerifiedConfig("config.yml");
language_config = getVerifiedConfig("language.yml");
team_shop_config = getVerifiedConfig("team_shop.yml");  // 改为使用静态字段
```

### 2. 修改 ConfigAPI.java

#### 2.1 添加备份字段（第30-33行）
```java
private static FileConfiguration originalTeamShopConfig = null;
private static boolean teamShopFieldExists = true;  // 兼容性标志
```

#### 2.2 修改 backupOriginalConfigs() 方法
```java
// 尝试备份team_shop_config（可能在旧版本中不存在）
try {
    Field teamShopConfigField = Config.class.getDeclaredField("team_shop_config");
    teamShopConfigField.setAccessible(true);
    originalTeamShopConfig = (FileConfiguration) teamShopConfigField.get(null);
    Main.getInstance().getLogger().info("[ConfigAPI] Backed up original configs (including team_shop)");
} catch (NoSuchFieldException e) {
    Main.getInstance().getLogger().info("[ConfigAPI] Backed up original configs (team_shop_config field not found)");
    teamShopFieldExists = false;
}
```

#### 2.3 修改 loadGameConfig() 方法
```java
// 注入team_shop_config
if (customTeamShop != null) {
    try {
        injectConfig("team_shop_config", customTeamShop);
        Main.getInstance().getLogger().info("[ConfigAPI] Injected custom team_shop config for game: " + gameName);
    } catch (NoSuchFieldException e) {
        Main.getInstance().getLogger().warning("[ConfigAPI] team_shop_config field not found, skipping team shop injection");
        teamShopFieldExists = false;
    }
}

// 重新加载Config以更新所有静态字段
if (customTeamShop != null && teamShopFieldExists) {
    Config.loadConfig();
    Main.getInstance().getLogger().info("[ConfigAPI] Reloaded Config to apply team shop settings");
}
```

#### 2.4 修改 restoreDefaultConfig() 方法
```java
// 恢复team_shop_config
if (originalTeamShopConfig != null && teamShopFieldExists) {
    try {
        injectConfig("team_shop_config", originalTeamShopConfig);
        Main.getInstance().getLogger().info("[ConfigAPI] Restored original team_shop config");
    } catch (NoSuchFieldException e) {
        Main.getInstance().getLogger().warning("[ConfigAPI] Failed to restore team_shop_config");
    }
}
```

## 工作原理

### 修复前的流程
1. ConfigAPI注入file_config和language_config ✅
2. ConfigAPI尝试注入team_shop_config ❌（字段不存在）
3. 游戏启动时使用默认的team_shop配置 ❌

### 修复后的流程
1. ConfigAPI注入file_config和language_config ✅
2. ConfigAPI注入team_shop_config到静态字段 ✅
3. 调用Config.loadConfig()重新加载配置 ✅
4. Config.loadConfig()从静态字段team_shop_config读取配置 ✅
5. 所有teamshop相关的静态字段（如teamshop_enabled、teamshop_upgrade_shop_title等）都被正确更新 ✅

## 为什么需要调用Config.loadConfig()？

**关键理解**：
- `file_config` 和 `language_config` 可以直接注入，因为SBA代码直接使用这些字段
- `team_shop_config` 注入后，还需要调用 `Config.loadConfig()` 来更新所有派生的静态字段

**原因**：
```java
// Config.java 第549-683行
teamshop_enabled = team_shop_config.getBoolean("enabled");
teamshop_upgrade_shop_title = ColorUtil.color(team_shop_config.getString("upgrade_shop.title"));
teamshop_upgrade_shop_frame = ColorUtil.colorList(team_shop_config.getStringList("upgrade_shop.frame"));
// ... 还有100多个字段需要从team_shop_config读取
```

如果不调用 `Config.loadConfig()`，这些字段仍然是旧值，team_shop配置不会生效。

## 兼容性考虑

代码添加了兼容性检查：
- 如果Config类中没有team_shop_config字段（旧版本），会优雅地跳过
- 使用 `teamShopFieldExists` 标志避免重复尝试
- 不会影响其他配置的正常注入

## 测试建议

1. **创建测试配置**：
   ```bash
   /brac create teamshop test_teamshop
   ```

2. **为游戏启用配置**：
   ```bash
   /brac enable <游戏名> teamshop test_teamshop
   ```

3. **启动游戏并检查日志**：
   ```
   [ConfigAPI] Injected custom team_shop config for game: <游戏名>
   [ConfigAPI] Reloaded Config to apply team shop settings
   [ConfigAPI] Loaded custom config for game: <游戏名>
   ```

4. **进入游戏验证**：
   - 打开团队商店
   - 检查升级项目的价格、名称等是否使用了自定义配置

5. **游戏结束后检查恢复**：
   ```
   [ConfigAPI] Restored original team_shop config
   [ConfigAPI] Restored default config
   ```

## 构建说明

修改后需要重新构建SBA-Adapt：

```bash
cd BedwarsScoreBoardAddon-Adapt/BedwarsScoreBoardAddon-master
mvn clean package -DskipTests
```

生成的JAR文件：
- `target/BedwarsScoreBoardAddon-2.13.1.jar`

## 相关文件

- `src/main/java/me/ram/bedwarsscoreboardaddon/config/Config.java` - 添加静态字段
- `src/main/java/me/ram/bedwarsscoreboardaddon/api/ConfigAPI.java` - 添加注入逻辑

## 提交记录

- Commit: Fix: 修复SBA team_shop配置注入问题
- 修改文件：2个
- 新增代码：约40行
- 删除代码：约5行

