## CQRS+イベントソーシングのサンプルコード

## 注意
CQRS+イベントソーシングのサンプルコードです。
未完成な部分が多いです。

### 題材
題材はCMS(Content Management System)です。
複数のサイトを一つのアカウントで管理できます。

管理している記事はビルドサーバーを通じて、静的なサイトとして書き出して公開します。
記事に対するコメントやお気に入り登録などの動的な部分に関してはWeb APIサービスを使って、閲覧、書き込み、編集ができるようになっています。

書いた記事はマーケットプレイスを通じて、商品として売り出すことができます。
記事を買うためには、ポイントを有料でチャージして、交換する必要があります。

### 技術
 - コメント欄への書き込みは負荷分散とパフォーマンス向上、データ解析のためにCQRS+ES(Akka Persistence, Akka Persistence Query)を使っています。
 - 記事の購入(ポイントとの交換)にはプロセスマネージャー(PersistentFSM)を使っています。

### コンテキスト
各コンテキストの説明はそれぞれのサブプロジェクト内のREADMEを御覧ください。
 - アカウント(nv-account)
 - サイト(nv-site)
 - ディスカッション(nv-discussion)  
 　CQRS+ESで実装
 - マーケット(nv-market)
 - 購入(nv-purchase)  
 　プロセスマネージャーをPersistentFSMで実装
 - 解析(nv-analysis)

