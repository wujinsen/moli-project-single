[English](./README.md) | [中文](./README-zh.md) | [日本語](./README-ja.md)

# Moli 管理バックエンド（moli-project-single）

**Spring Boot** ベースの **Java 製管理用バックエンド** です。**Maven マルチモジュール** で構成され、API は `MoliResult<T>` / `PageRes<T>` で統一し、認証に **Shiro**、API ドキュメントに **Swagger2** を利用します。

## 技術スタック

- **ランタイム**: Java 8、Spring Boot 2.3.x  
- **データ**: MySQL、MyBatis-Plus、Druid  
- **キャッシュ / セッション**: Redis、Jedis、Shiro Redis セッション  
- **その他**: Apache Shiro、EasyExcel、MinIO クライアント、Swagger2（Springfox）

## モジュール

| モジュール | 役割 |
|------------|------|
| `moli-parent` | 親 POM（依存バージョンの統一） |
| `moli-common` | 共通エンティティ、VO、定数、ユーティリティ |
| `moli-server` | Web 層、業務サービス、Mapper |

## 前提条件

- JDK 8  
- Maven 3.6+  
- MySQL 8.x（または互換版）  
- Redis  

## クイックスタート

1. **親 POM をローカルにインストール**（本リポジトリの `moli-parent` をローカル Maven リポジトリへ）:

   ```bash
   cd moli-parent && mvn -DskipTests install && cd ..
   ```

2. **ビルド**:

   ```bash
   mvn -pl moli-common,moli-server -am -DskipTests package
   ```

3. **設定**: `moli-server/src/main/resources/application-dev.yml`（DB URL、認証情報、Redis など）。`application.yml` の既定プロファイルは `dev` です。

4. **起動**（`UserServiceTest` がコンパイルできない環境ではテストをスキップ）:

   ```bash
   cd moli-server && mvn -Dmaven.test.skip=true spring-boot:run
   ```

5. **ブラウザ**: HTTP ポート **1125**、[http://localhost:1125/swagger-ui.html](http://localhost:1125/swagger-ui.html)（プロファイルで `swagger.show` が有効な場合）。

## ドキュメント

- [API イテレーションマップ](docs/api-iteration-map.md)  
- [プロジェクトイテレーション基線](docs/project-iteration-baseline.md)  
- [依存関係セキュリティロードマップ](docs/dependency-security-roadmap.md)  
- AI コラボレーション: [AGENTS.md](AGENTS.md) / [AGENTS.en.md](AGENTS.en.md)

## ライセンス

本プロジェクトは **MIT License** の下で提供されます。詳細は [LICENSE](LICENSE) を参照してください。
