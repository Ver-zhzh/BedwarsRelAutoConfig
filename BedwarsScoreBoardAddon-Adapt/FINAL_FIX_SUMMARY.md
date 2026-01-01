# Team Shop配置注入最终修复总结

## 问题历程

### 问题1：team_shop_config是局部变量
**症状**：team_shop配置完全不生效

**原因**：
- SBA的Config类中，`team_shop_config` 只是`loadConfig()`方法中的局部变量
- ConfigAPI无法通过反射注入局部变量

**修复**：
- 将`team_shop_config`改为静态字段
- ConfigAPI添加备份和注入team_shop_config的代码

### 问题2：ConfigAPI不支持configFile为null
**症状**：当只启用teamshop配置时，抛出`IllegalArgumentException: File cannot be null`

**原因**：
- 用户只启用了teamshop配置，没有启用SBAconfig
- ConfigAPI收到null的configFile参数
- `YamlConfiguration.loadConfiguration(null)` 抛出异常

**修复**：
- ConfigAPI允许configFile参数为null
- 只在configFile不为null时加载和注入

### 问题3：Config.loadConfig()覆盖注入的配置
**症状**：无法打开队伍商店

**原因**：
- ConfigAPI注入team_shop_config后，调用`Config.loadConfig()`重新加载静态字段
- `Config.loadConfig()`从磁盘重新加载所有配置，覆盖了注入的配置
- 导致使用默认配置而不是自定义配置

**修复**：
- 修改`Config.loadConfig()`，添加null检查
- 只在配置为null时才从磁盘加载
- 如果配置已被注入，则保留注入的配置

## 最终解决方案

### 1. Config.java修改

#### 添加静态字段（第40行）
```java
private static FileConfiguration file_config;
private static FileConfiguration language_config;
private static FileConfiguration team_shop_config;  // 新增
```

#### 修改loadConfig()方法（第349-359行）
```java
Main.getInstance().getLocaleConfig().loadLocaleConfig();
// Only reload from disk if not already injected by ConfigAPI
if (file_config == null) {
    file_config = getVerifiedConfig("config.yml");
}
if (language_config == null) {
    language_config = getVerifiedConfig("language.yml");
}
if (team_shop_config == null) {
    team_shop_config = getVerifiedConfig("team_shop.yml");
}
```

**关键改进**：
- 添加null检查，只在配置为null时才从磁盘加载
- 保留ConfigAPI注入的配置，不会被覆盖

### 2. ConfigAPI.java修改

#### 支持configFile为null（第52行）
```java
// Load custom configs (configFile can be null if only team_shop is provided)
FileConfiguration customConfig = configFile != null ? YamlConfiguration.loadConfiguration(configFile) : null;
FileConfiguration customLanguage = languageFile != null ? YamlConfiguration.loadConfiguration(languageFile) : null;
FileConfiguration customTeamShop = teamShopFile != null ? YamlConfiguration.loadConfiguration(teamShopFile) : null;
```

#### 添加null检查（第60-74行）
```java
// Inject custom configs into Config class using reflection
if (customConfig != null) {
    injectConfig("file_config", customConfig);
}
if (customLanguage != null) {
    injectConfig("language_config", customLanguage);
}
if (customTeamShop != null) {
    try {
        injectConfig("team_shop_config", customTeamShop);
        Main.getInstance().getLogger().info("[ConfigAPI] Injected custom team_shop config for game: " + gameName);
    } catch (NoSuchFieldException e) {
        Main.getInstance().getLogger().warning("[ConfigAPI] team_shop_config field not found, skipping team shop injection");
        teamShopFieldExists = false;
    }
}
```

#### 调用Config.loadConfig()重新加载静态字段（第81-84行）
```java
// However, for team_shop_config, we need to reload Config to update all static fields
// Config.loadConfig() has been modified to preserve injected configs (not reload from disk if already set)
if (customTeamShop != null && teamShopFieldExists) {
    Config.loadConfig();
    Main.getInstance().getLogger().info("[ConfigAPI] Reloaded Config to apply team shop settings");
}
```

**关键改进**：
- 支持configFile为null的情况
- 调用Config.loadConfig()安全地重新加载静态字段
- Config.loadConfig()会保留注入的配置

### 3. SBAInjector.java修改

#### 移除fallback到legacy模式（第343行）
```java
// Load game config using ConfigAPI with all available configs
// ConfigAPI now supports configFile being null (only team_shop injection)
Method loadGameConfigMethod = configAPIClass.getDeclaredMethod("loadGameConfig",
    String.class, File.class, File.class, File.class);
Boolean success = (Boolean) loadGameConfigMethod.invoke(null, gameName, tempConfigFile, null, tempTeamShopFile);
```

**关键改进**：
- 移除了fallback到legacy模式的逻辑
- 直接使用ConfigAPI处理所有情况
- 简化代码逻辑

## 工作流程

### 完整的配置注入流程

1. **游戏开始事件触发**
   - GameConfigListener监听BedwarsGameStartEvent

2. **缓存配置**
   - 如果有SBAconfig，调用`sbaInjector.injectMainConfig()`缓存
   - 如果有teamshop，调用`sbaInjector.injectTeamShopConfig()`缓存

3. **应用配置**
   - 调用`sbaInjector.applyPendingConfigs()`
   - 创建临时配置文件
   - 调用`ConfigAPI.loadGameConfig()`

4. **ConfigAPI注入配置**
   - 备份原始配置（如果是第一次）
   - 加载自定义配置（支持configFile为null）
   - 注入file_config、language_config、team_shop_config

5. **重新加载静态字段**
   - 如果注入了team_shop_config，调用`Config.loadConfig()`
   - Config.loadConfig()检测到配置已注入，不从磁盘重新加载
   - 从注入的配置中读取所有静态字段

6. **游戏运行**
   - 使用自定义配置运行游戏
   - 队伍商店使用自定义的价格、名称等

7. **游戏结束**
   - 调用`ConfigAPI.restoreDefaultConfig()`
   - 恢复原始配置
   - 重新加载Config

## 支持的配置组合

- ✅ 只有SBAconfig（主配置）
- ✅ 只有teamshop（团队商店配置）
- ✅ 同时有SBAconfig和teamshop
- ✅ 没有任何自定义配置（使用默认配置）

## 测试建议

### 测试1：只启用teamshop配置
```bash
/brac create teamshop test_shop
/brac enable <游戏名> teamshop test_shop
```

**预期日志**：
```
[ConfigAPI] Injected custom team_shop config for game: <游戏名>
[ConfigAPI] Reloaded Config to apply team shop settings
[ConfigAPI] Loaded custom config for game: <游戏名>
```

**验证**：
- 队伍商店可以正常打开
- 使用自定义的价格和名称

### 测试2：同时启用SBAconfig和teamshop
```bash
/brac create SBAconfig test_sba
/brac create teamshop test_shop
/brac enable <游戏名> SBAconfig test_sba
/brac enable <游戏名> teamshop test_shop
```

**预期日志**：
```
[ConfigAPI] Injected custom team_shop config for game: <游戏名>
[ConfigAPI] Reloaded Config to apply team shop settings
[ConfigAPI] Loaded custom config for game: <游戏名>
```

**验证**：
- SBA主配置生效
- 队伍商店配置生效
- 两者互不影响

### 测试3：游戏结束后恢复
**预期日志**：
```
[ConfigAPI] Restored original team_shop config
[ConfigAPI] Restored default config (was: <游戏名>)
```

**验证**：
- 启动另一个游戏，使用默认配置
- 队伍商店使用默认价格

## Git提交记录

```
f776ef9 Fix: 修复Config.loadConfig()覆盖注入配置的问题
d9804eb Fix: 支持只注入team_shop配置的情况
505b216 Docs: 添加team_shop配置注入修复文档和测试指南
394d3b5 Fix: 修复SBA team_shop配置注入问题
4fe2b99 Fix: 修复SBA teamshop和SBAconfig配置注入失效问题
```

## 构建文件

- **SBA-Adapt**: `BedwarsScoreBoardAddon-Adapt/BedwarsScoreBoardAddon-master/target/BedwarsScoreBoardAddon-2.13.1.jar`
- **BRAC**: `target/BedwarsRelAutoConfig-1.0.0.jar`

## 部署说明

1. 停止服务器
2. 替换插件JAR文件：
   - `plugins/BedwarsScoreBoardAddon.jar` → 新的SBA-Adapt JAR
   - `plugins/BedwarsRelAutoConfig.jar` → 新的BRAC JAR
3. 启动服务器
4. 检查日志确认插件正确加载
5. 进行测试

## 已知限制

1. **只支持SBA-Adapt**：原版SBA不支持team_shop配置注入
2. **需要重新加载Config**：注入team_shop后必须调用Config.loadConfig()
3. **临时文件**：ConfigAPI会创建临时配置文件，游戏结束后会清理

## 未来改进

1. **优化性能**：避免每次都调用Config.loadConfig()
2. **更好的错误处理**：提供更详细的错误信息
3. **支持更多配置类型**：如language.yml等

