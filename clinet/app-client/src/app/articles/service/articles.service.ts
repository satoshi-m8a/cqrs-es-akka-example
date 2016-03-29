import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {Response} from 'angular2/http';
import {Article} from '../model/article';

@Injectable()
export class ArticlesService {
    constructor(private http:Http) {
    }

    getArticles(offset:number, limit:number):Observable<Array<Article>> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get('http://jsonplaceholder.typicode.com/posts').map((res:Response)=> {
            let list = res.json().map(()=> {
                return {id: 'i', title: 't'};
            });
            list.meta = {
                total: 100
            };

            return list;
        });
    }
}
