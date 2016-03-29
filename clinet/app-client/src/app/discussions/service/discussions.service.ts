import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {Discussion} from '../model/discussion';
import {Response} from 'angular2/http';

@Injectable()
export class DiscussionsService {
    constructor(private http:Http) {
    }

    getDiscussions():Observable<Array<Discussion>> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get('http://jsonplaceholder.typicode.com/posts').map((res:Response)=> {
            console.log(res);
            return [new Discussion('id', 'title')];
        });
    }
}
