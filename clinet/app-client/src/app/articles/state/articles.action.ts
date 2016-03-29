import {Article} from '../model/article';
export interface IArticleAction {
    type:string;
    articles?:Array<Article>;
}

export function loadArticles(articles:Array<Article>):IArticleAction {
    return {
        type: 'LOAD_ARTICLES',
        articles: articles
    };
}
