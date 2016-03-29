import {createStore} from 'redux';
import {ArticlesService} from '../service/articles.service';
import {reducer} from './articles.reducer';
import {Article} from '../model/article';
import {Injectable} from 'angular2/core';
import {loadArticles} from './articles.action';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ArticlesStore {
    store:Redux.Store;

    constructor(private articlesService:ArticlesService) {
        this.store = createStore(reducer, []);
    }

    get articles() {
        return this.store.getState();
    }

    getArticles(offset:number, limit:number):Observable<Array<Article>> {
        let obs = this.articlesService.getArticles(offset, limit);
        obs.subscribe((articles:Array<Article>)=> {
            this.store.dispatch(loadArticles(articles));
        });
        return obs;
    }
}
