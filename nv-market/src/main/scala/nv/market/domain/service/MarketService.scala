package nv.market.domain.service

import nv.account.domain.model.account.AccountId
import nv.site.domain.model.article.ArticleId

/**
  * 売り出しサービス
  * 記事やページを売り出す。
  */
class MarketService {

  /**
    * 記事を出品する
    */
  def sellArticle(seller: AccountId, article: ArticleId, price: Long) = {

  }

}
