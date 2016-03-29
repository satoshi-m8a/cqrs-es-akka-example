import {Article} from '../model/article';
import {IArticleAction} from './articles.action';

export function reducer(state:Array<Article>, action:IArticleAction) {
    switch (action.type) {
        case 'LOAD_ARTICLES':
            return action.articles;
        default:
            return state;
    }
}
