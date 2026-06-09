[English](./README.md) | [中文](./README-zh.md) | [日本語](./README-ja.md)

# Moli 管理バックエンド（moli-project-single）

**棠羽管理システム**向けの **Spring Boot** ベース **Java 管理用 API** です。**Maven マルチモジュール**構成で、API は `MoliResult<T>` / `PageRes<T>` で統一し、**RBAC**、**Shiro** セッション認証、**Swagger2** を提供します。

> 本リポジトリは**バックエンドのみ**です。Vue 系の管理画面フロントエンドと組み合わせて利用します。

## 機能概要

| ドメイン | 内容 |
|----------|------|
| **システム** | ユーザー、ロール、メニュー、部門、ポスト、辞書、ログ |
| **運用** | プロジェクト、サーバー、プラットフォーム、コンポーネントデプロイ |
| **AI** | ChatGPT 連携 API |
| **多言語** | メニュー・辞書の **zh-CN / en-US / ja-JP**、ユーザー言語設定 |
| **セキュリティ** | Shiro + Redis セッション、キャプチャ ON/OFF |
| **ストレージ** | MinIO クライアント |

## 技術スタック

- **ランタイム**: Java 8、Spring Boot 2.3.x
- **データ**: MySQL 8.x、MyBatis-Plus、Druid
- **キャッシュ / セッション**: Redis、Jedis、Shiro Redis
- **認証**: Apache Shiro（SHA-256 + ソルト、15 回）
- **ドキュメント**: Swagger2（Springfox）

## モジュール

| モジュール | 役割 |
|------------|------|
| `moli-parent` | 親 POM（依存バージョン統一） |
| `moli-common` | 共通エンティティ、VO、定数 |
| `moli-server` | Controller、Service、Mapper、設定 |

## RBAC 設計

**ユーザー → ロール → メニュー** の古典的 RBAC です。メニューはナビゲーションと権限識別子を兼ね、ログイン時に返却されるメニューツリーでフロントが画面を制御します。

### 主要テーブル

- `sys_user` / `sys_role` / `sys_menu`
- `sys_user_role`（ユーザー↔ロール）
- `sys_role_menu`（ロール↔メニュー）

### メニュー種別（`menu_type`）

| 種別 | コード | 説明 |
|------|--------|------|
| ディレクトリ | `M` | サイドバー分组 |
| メニュー | `C` | ルーティング可能なページ |
| ボタン | `F` | 細粒度操作（例: `system:user:add`） |

### 権限識別子（`perms`）

形式: **`module:resource:action`**（例: `system:user:list`）

### 認証フロー

1. `POST /login` → Shiro 認証 → `token`（Session ID）+ `user` + `menuVoList`
2. Session は **Redis** に保存
3. 一般ユーザーはロールに紐づくメニューのみ取得
4. ユーザー名 `superadmin` は全メニューを取得
5. `/**` は `authc`、`/login` と Swagger は `anon`

## データベース初期化

```bash
mysql -u root -p < sql/schema_moli.sql
mysql -u root -p moli < sql/seed_sys_menu.sql
```

## クイックスタート

```bash
cd moli-parent && mvn -DskipTests install && cd ..
mvn -pl moli-common,moli-server -am -DskipTests package
```

`application-dev.yml` を編集し、`moli-server` で `mvn -Dmaven.test.skip=true spring-boot:run` を実行。

本番設定は `application-pro.yml.example` をコピーし、**シークレットを Git にコミットしない**こと。環境変数 `SPRING_DATASOURCE_PASSWORD` 等を使用。

## ドキュメント

- [AWS デプロイガイド（MySQL + Nginx + Redis）](docs/aws-deployment-guide.en.md)
- [API イテレーションマップ](docs/api-iteration-map.md)
- [プロジェクトイテレーション基線](docs/project-iteration-baseline.md)
- [依存関係セキュリティロードマップ](docs/dependency-security-roadmap.md)

## ライセンス

Copyright (c) 2026 **wujinsen**

本プロジェクトは **[MIT License](LICENSE)** の下で提供されます。ソフトウェアは **現状のまま（AS IS）** 提供され、いかなる保証もありません。全文は [LICENSE](LICENSE) を参照してください。
